package com.ge.current.em;

import com.ge.current.em.utils.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.DataProvider;

import java.io.*;
import java.util.*;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.CoreMatchers.hasItem;
import com.jayway.jsonpath.*;

public class TestUtil  {
    //Initialize variables from Application.properties
    private static final Log logger = LogFactory.getLog(TestUtil.class);

    public static String environment;
    public static Properties p = null;
    public static Calendar localCalendar;
    public static Double latitude=null;
    public static Double longitude =null;
    public static String localTimeZone=null;
    public static HashMap<String,String> entitygivenkey = new HashMap<String, String>();

    @BeforeSuite
    @Parameters( {"environment"} )
    public void initFramework(@Optional("vpc_dev.configuration.properties") String configfile, 
        ITestContext context) throws Exception {

        environment=configfile;
        String s="in SuiteTestBase";
        logger.info("In SuiteTestBase.java"+s);

        init(configfile);
        configureProxySettings();
        addPropertiesToTestContext(context);

        if(!(environment.equals("vpc_dev.configuration.properties"))) {
            fillentitygivenkey();
            setGeoCoordinates();
        }
    }

    private void addPropertiesToTestContext(ITestContext context) {
        for (Map.Entry<Object, Object> property : p.entrySet()) {
            context.setAttribute((String) property.getKey(), property.getValue());
        }
    }

    protected String getProperty(String name) {
        return getProperty(name, null);
    }

    protected String getProperty(String name, String defaultValue) {
        return p.getProperty(name, defaultValue);
    }

    protected String getSiteSourceKey() {
        return getProperty(Constants.SITE_SOURCE_KEY);
    }

    //Map sourcekey to the entity name
    private void fillentitygivenkey() {

        if(getProperty(Constants.ENTERPRISE_SOURCE_KEY)!=null)
        {
            String enterpriseName=getName(getProperty(Constants.ENTERPRISE_SOURCE_KEY));
            entitygivenkey.put(getProperty(Constants.ENTERPRISE_SOURCE_KEY),enterpriseName);
        }

        if(getProperty(Constants.SITE_SOURCE_KEY)!=null)
        {
            String siteName=getName(getProperty(Constants.SITE_SOURCE_KEY));
            entitygivenkey.put(getProperty(Constants.SITE_SOURCE_KEY),siteName);
            ArrayList<String> segmentsInSite =getSegmentsforSite(getProperty(Constants.SITE_SOURCE_KEY));

            for(Object s:segmentsInSite) {

                String segmentName=getName(s.toString());
                entitygivenkey.put((String) s.toString(),segmentName);
            }
        } else {
            ArrayList<String> site_sourceKey=getSiteinEnterprise(getProperty(Constants.ENTERPRISE_SOURCE_KEY));
            for(String s:site_sourceKey) {
                String siteName=getName(s);
                entitygivenkey.put(s,siteName);
            }
        }
        /*
        for(String s : entitygivenkey.keySet())
        {
            logger.info("Key->"+s);
            logger.info("Value->"+entitygivenkey.get(s));
        }
        */
        //logger.info("entitygivenkey.size+"+entitygivenkey.size());
    }

    //Use EnterpriseSourceKey to get all Sites in it
    private ArrayList<String> getSiteinEnterprise(String enterpriseSourceKey) {

        String uri=getProperty(Constants.APM_URL)+"enterprises"+"/"+enterpriseSourceKey+"/"+"sites";
        logger.info("uri"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().
                        //log().body().
                        extract().response();
        String jsonString = response.getBody().asString();
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<String> sites =ctx.read("$.content[*].sourceKey");
        return  sites;
    }

    //Use SiteSourceKey to get all SegmentSourceKeys
    private ArrayList<String> getSegmentsforSite(String siteSourceKey) {

        String uri=getProperty(Constants.APM_URL)+"sites"+"/"+siteSourceKey+"/"+"children";
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().
                        //log().body().
                        extract().response();
        String jsonString = response.getBody().asString();
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<String> segments =ctx.read("$.content[*].sourceKey");
        return segments;
    }

    //Utility function to get given SourceKey fetch the name in APM
    public  String getName(String sourceKey) {
    
        String uri;
        if(sourceKey.contains("ENTERPRISE"))
        {
            uri=getProperty(Constants.APM_URL)+"enterprises"+"/"+sourceKey;
        }
        else if(sourceKey.contains("SITE"))
            uri=getProperty(Constants.APM_URL)+"sites"+"/"+sourceKey;
        else
            uri=getProperty(Constants.APM_URL)+"segments"+"/"+sourceKey;

        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().
                        //log().body().
                                extract().response();
        //logger.info("response:"+response.getBody().toString());
        String jsonString = response.getBody().asString();
        ReadContext ctx = JsonPath.parse(jsonString);
        String name = ctx.read("$.name");
        return  name;
    }

    private void setGeoCoordinates() {

        String uri=getProperty(Constants.APM_URL)+"sites"+"/"+getProperty(Constants.SITE_SOURCE_KEY)+"/"+"locations";
        logger.info("uri"+uri);

        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().
                        //log().body().
                        extract().response();
        String jsonString = response.getBody().asString();
        ReadContext ctx = JsonPath.parse(jsonString);
        JSONArray ja =new JSONArray(jsonString);
        JSONObject jo=ja.getJSONObject(0);
        if(( environment.equals("McDonaldsSanity.configuration.properties"))) {
            JSONObject parentLocation = jo.getJSONObject("parentLocation");
            JSONObject geoCoordinates =parentLocation.getJSONObject("geoCoordinates");
            logger.info("geoCoord:" + geoCoordinates.toString());
            latitude = geoCoordinates.getDouble("latitude");
            longitude = geoCoordinates.getDouble("longitude");
            localTimeZone = geoCoordinates.getString("timezone");
        }

        if((environment.equals("Sanity.configuration.properties")))
        {
            JSONObject geoCoordinates =jo.getJSONObject("geoCoordinates");
            logger.info("geoCoord:" + geoCoordinates.toString());
            latitude = geoCoordinates.getDouble("latitude");
            longitude = geoCoordinates.getDouble("longitude");
            localTimeZone = geoCoordinates.getString("timezone");
        }
    }

    //Set the proxy for the environment from properties file
    public void configureProxySettings() throws Exception{
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("http.proxyHost", p.getProperty("em.proxy.host"));
        System.getProperties().put("http.proxyPort", p.getProperty("em.proxy.port"));
        System.getProperties().put("https.proxyHost", p.getProperty("em.proxy.host"));
        System.getProperties().put("https.proxyPort", p.getProperty("em.proxy.port"));
    }

    //Load the properties file
    public void init(String configFile) {
        try {
            FileInputStream fis = new FileInputStream(configFile);
            p = new Properties();
            p.load(fis);
        } catch (Exception e) {
            logger.info("Exception "+e.getMessage());
        }
    }
}
