package com.ge.current.em;

/**
 * Created by 502645575 on 12/15/16.
 */

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


@Configuration
@PropertySource("classpath:${envType}-test.properties")
public class APMJsonUtility extends AbstractTestNGSpringContextTests {




    @Autowired
    private Environment env;



    protected String replaceProperties(String src) {
        return replaceProperties(src, null);
    }

    protected String replaceProperties(String src, Properties overrides) {
        return new PropertyPlaceholderHelper("${", "}").replacePlaceholders(src, new Resolver(env, overrides));
    }

    public String readPayloadFile(String fileName, Properties properties) throws Exception {
        logger.info("fileName"+fileName);

        String fileContents = readFromFile(fileName);
        return replaceProperties(fileContents, properties);
    }

    public String readFromFile(String path) throws IOException, ParseException {

        String jsonFile = path;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(new File(jsonFile)));
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);

        }
        return sb.toString();

    }

    private static class Resolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private Environment env;
        // Overrides take precedence over env, always.
        private Properties overrides;

        public Resolver(Environment env, Properties overrides) {
            this.env = env;
            if (overrides == null) {
                this.overrides = new Properties();
            } else {
                this.overrides = overrides;
            }
        }

        @Override
        public String resolvePlaceholder(String s) {
            if (overrides.containsKey(s)) {
                return overrides.getProperty(s);
            }
            return env.getProperty(s);
        }
    };

}
