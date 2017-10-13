package com.ge.current.em;

import com.ge.current.em.provider.AssetDataProvider;
import com.ge.current.em.utils.Constants;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by 502645575 on 12/15/16.
 */


public class DataFlowTest extends TestUtil {

    private static final Log logger = LogFactory.getLog(DataFlowTest.class);


    @Test(priority = 1)
    public void RabbitMQTest()
    {
        String uri=getRabbitMQUrl()+"exchanges"+"/"+getHost()+"/"+getExchange();
        logger.info("uri"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString =response.getBody().asString();
        assertTrue((response.statusCode()==200),"Assertion Failed:Response Code returned is:"+response.statusCode());
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        int publish_in =ctx.read("$.message_stats.publish_in");
        int publish_out =ctx.read("$.message_stats.publish_out");
        assertTrue(!(publish_in==0),"Assertion Failed:Publish In is 0:");
        assertTrue((publish_in==publish_out),"Assertion Failed:Publish In is:"+publish_in+":Publish Out is:"+publish_out);
    }

    @Test(dataProvider = "ASSETProvider",dataProviderClass = AssetDataProvider.class,priority = 2)
    public void eventLogTest(String assetid)
    {
        logger.info("assetid"+assetid);
        String uri=getDataServiceUrl();
        String dbname=getCassandraDbName();
        String tablename=getEventLog();
        logger.info("uri"+uri);
        String postbody="select * from "+dbname+"."+tablename+" where time_bucket= '2016' and asset_uid ='"+ assetid +"'"+" limit 1";
        logger.info("postbody:->"+postbody);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(postbody).
                        when().
                        post(uri).
                        then().extract().response();
        assertTrue((response.statusCode()==200),"Event LOG verficiation Failed");
        String jsonString =response.getBody().asString();
         logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        String asset_uid =ctx.read("$.rows[0].asset_uid");
        logger.info("asset_uid"+asset_uid);
    }
    @Test(dataProvider = "ASSETProvider",dataProviderClass = AssetDataProvider.class,priority = 3)
    public void NormalizedDataTest(String assetid)
    {
        String uri=getDataServiceUrl();
        String dbname=getCassandraDbName();
        String tablename=getNormalizedTable();
        logger.info("uri"+uri);
        String postbody="select * from "+dbname+"."+tablename+" where time_bucket= '2016' and asset_uid ='"+ assetid+"'"+" and event_type='reading'"+" limit 1";
        logger.info("postbody:->"+postbody);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(postbody).
                        when().
                        post(uri).
                        then().extract().response();
        assertTrue((response.statusCode()==200),"Normalization verficiation Failed");
        String jsonString =response.getBody().asString();
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        String asset_uid =ctx.read("$.rows[0].asset_uid");
        logger.info("asset_uid"+asset_uid);
    }

    public String getCassandraDbName() {
        return getProperty(Constants.CASS_DB_NAME);
    }

    public String getNormalizedTable() {
        return getProperty(Constants.NORMALIZED_5_MINS);
    }

    public String getEventLog() {
        return getProperty(Constants.EVENT_LOG);
    }

    protected String getDataServiceUrl() {
        return getProperty(Constants.DATA_SERVICES_URL);
    }

    protected String getRabbitMQUrl() {
        return getProperty(Constants.RABBIT_MQ_URL);
    }

    protected String getHost() {
        return getProperty(Constants.VHOST);
    }

    protected String getExchange() {
        return getProperty(Constants.EXCHANGE);
    }
}
