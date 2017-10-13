package com.ge.current.em.automation.util;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.ge.current.em.automation.dto.AlertLogSolrIndex;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBody;
import javafx.collections.ObservableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.client.RestTemplate;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by 212554696 on 4/20/17.
 */
public class SolrServicesTestUtil {

    private static final Log LOGGER = LogFactory.getLog(SolrServicesTestUtil.class);

    public static ArrayNode search(String searchQuery, String schemaName, String tableName) throws ParseException,
            IOException {
        String token = EMTestUtil.getTokenWithGrantTypeClientCredentials(EMTestUtil.getProperties().getProperty("uaaUrl"),
                EMTestUtil.getProperties().getProperty("uaa.username"),
                EMTestUtil.getProperties().getProperty("uaa.password"));

        String uri = EMTestUtil.getProperties().getProperty("solr_service_url") + schemaName + "." + tableName + "/search?query=" + searchQuery;
        LOGGER.info("GET: " + uri);
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .param("grant_type", "client_credentials").contentType(ContentType.JSON)
                .when()
                .get(uri)
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonNode responseBodyInJson =  new ObjectMapper().readValue(response.prettyPrint(), JsonNode.class);
        ArrayNode responseInArrayJson = (ArrayNode) responseBodyInJson;
//        LOGGER.info("Response from search: " + responseInArrayJson.toString());
        return responseInArrayJson;
    }

    public static <T> List<T> convertJsonArray(Class<T> tClass, ArrayNode arrayNode) throws IOException {
        List<T> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < arrayNode.size(); i++) {
            T row = mapper.readValue(arrayNode.get(i).toString(), tClass);
            list.add(row);
        }
        return list;
    }
}
