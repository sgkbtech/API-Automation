package com.ge.current.em;


import com.ge.current.em.provider.APMDataProvider;
import com.ge.current.em.utils.Constants;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.*;

import java.util.Calendar;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.testng.Assert.assertTrue;

/**
 * Created by 502645575 on 10/18/16.
 */


public class AggregationTest extends TestUtil {

    private static final Log logger = LogFactory.getLog(AggregationTest.class);

/*
Takes SegmentSourceKey as input - calculation done to check for the hour and min bucket in Cassandra
Query Cassandra and check whether the latest data is present
 */
    @Test(dataProvider = "segment-provider", dataProviderClass = APMDataProvider.class)
    public void CheckAggregation15min(String assetSourceKey) throws Throwable{

        //logger.info("Segment Key 15 min"+assetSourceKey);

        String uri=getDataServiceUrl();

        String Date;
        String Month;
        String Year;



        Calendar aggrCalendar =Calendar.getInstance();
        long currentmillsec=aggrCalendar.getTimeInMillis();
        aggrCalendar.setTimeInMillis(currentmillsec - 2100000 );

        logger.info("localTimeZone:->"+localTimeZone);

        if(localTimeZone.equals(aggrCalendar.getTimeZone().getID()))
        aggrCalendar.setTimeInMillis(currentmillsec-2100000);
        if(localTimeZone.equals("America/New_York"))
            aggrCalendar.setTimeInMillis(currentmillsec-2100000+10800000);
        if(localTimeZone.equals("America/Chicago"))
            aggrCalendar.setTimeInMillis(currentmillsec-2100000+7200000);
        //logger.info("Local Calendar Minute:->"+super.localCalendar.get(Calendar.MINUTE));
        //logger.info("Local Calendar Hour:->"+super.localCalendar.get(Calendar.HOUR_OF_DAY));
        //logger.info("Aggregation Minute:->"+aggrCalendar.get(Calendar.MINUTE));
        //logger.info("Aggregation Hour:->"+aggrCalendar.get(Calendar.HOUR_OF_DAY));
        if(aggrCalendar.get(Calendar.DATE)<10)
          Date="0"+String.valueOf(aggrCalendar.get(Calendar.DATE));
        else
            Date= String.valueOf(aggrCalendar.get(Calendar.DATE));
        if((aggrCalendar.get(Calendar.MONTH)+1)<10)
            Month="0"+String.valueOf(aggrCalendar.get(Calendar.MONTH)+1);
        else
            Month= String.valueOf(aggrCalendar.get(Calendar.MONTH)+1);
        Year=String.valueOf(aggrCalendar.get(Calendar.YEAR));

        int hh =aggrCalendar.get(Calendar.HOUR_OF_DAY);
        int min=aggrCalendar.get(Calendar.MINUTE);



        if((min>=0)&&(min<15))
            min=0;
        else if((min>=15)&&(min<30))
            min=15;
        else if((min>=30)&&(min<45))
            min=30;
        else
        min=45;
        String totalDate=Year+Month+Date;
        String dbname = getCassandraDbName();
        String tablename=getTableName("EVENTS_BY_FIFTEEN_MINUTES_TABLE");
        String postbody="select * from "+dbname+"."+tablename+" where event_bucket='asset."+assetSourceKey+".reading'"+" and yyyymmdd='"+totalDate+"'"+" and hour="+hh+" and ending_min=" +min+" limit 2";
        logger.info("postbody:->"+postbody);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(postbody).
                        expect().statusCode(200)
                        .when().
                        post(uri).
                        then().extract().response();
        String jsonString =response.getBody().asString();
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        int rowCount =Integer.parseInt(ctx.read("$.rowCount").toString());
        assertTrue((rowCount>=1),"Aggregation_15min has no data :Executed QUery:->"+postbody);

    }

/*

    private String generateDate(String event_detail) {
        String Date,Month,Year,totalDate;
        if(event_detail.equals("eventsdaily"))
        if((super.localCalendar.get(Calendar.DATE)<10)&&(event_detail.equals("eventsdaily")))
            Date="0"+String.valueOf(super.localCalendar.get(Calendar.DATE)-1);
        else
            Date="0"+String.valueOf(super.localCalendar.get(Calendar.DATE));
        else
            Date= String.valueOf(super.localCalendar.get(Calendar.DATE));
        if((super.localCalendar.get(Calendar.MONTH)+1)<10)
            Month="0"+String.valueOf(super.localCalendar.get(Calendar.MONTH)+1);
        else
            Month= String.valueOf(super.localCalendar.get(Calendar.MONTH)+1);
        Year=String.valueOf(super.localCalendar.get(Calendar.YEAR));
        return null;
    }
*/


/*
Takes SegmentSourceKey as input - calculation done to check for the hour  bucket in Cassandra
Query Cassandra and check whether the latest data is present
 */

    @Test(dataProvider = "segment-provider", dataProviderClass = APMDataProvider.class)
    public void CheckAggregation1hr(String assetSourceKey) throws Throwable{

        String uri=getDataServiceUrl();
        String Date;
        String Month;
        String Year;
        int hh=0;
        Calendar aggrCalendar =Calendar.getInstance();
        long currentmillsec=aggrCalendar.getTimeInMillis();
        aggrCalendar.setTimeInMillis(currentmillsec - 2100000 );
        if(localTimeZone.equals(aggrCalendar.getTimeZone().getID()))
            aggrCalendar.setTimeInMillis(currentmillsec-7200000);
        if(localTimeZone.equals("America/New_York"))
            aggrCalendar.setTimeInMillis(currentmillsec-7200000+10800000);
        if(localTimeZone.equals("America/Chicago"))
            aggrCalendar.setTimeInMillis(currentmillsec);

        if(aggrCalendar.get(Calendar.DATE)<10)
            Date="0"+String.valueOf(aggrCalendar.get(Calendar.DATE));
        else
            Date= String.valueOf(aggrCalendar.get(Calendar.DATE));
        if((aggrCalendar.get(Calendar.MONTH)+1)<10)
            Month="0"+String.valueOf(aggrCalendar.get(Calendar.MONTH)+1);
        else
            Month= String.valueOf(aggrCalendar.get(Calendar.MONTH)+1);
        Year=String.valueOf(aggrCalendar.get(Calendar.YEAR));

        hh =aggrCalendar.get(Calendar.HOUR_OF_DAY);
        String totalDate=Year+Month+Date;
       // logger.info("uri:->"+uri);
      //  logger.info("dataservicesurl:->"+super.dataservicesurl);
        String dbname = getCassandraDbName();
        String tablename=getTableName("EVENTS_BY_ONE_HOUR_TABLE");
        String postbody="select * from "+dbname+"."+tablename+" where event_bucket='asset."+ assetSourceKey+".reading'"
        +" and yyyymmdd='"+totalDate+"'"+" and hour="+hh;
        logger.info("postbody:->"+postbody);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(postbody).
                        expect().statusCode(200).
                        when().
                        post(uri).
                        then().extract().response();
        String jsonString =response.getBody().asString();
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        int rowCount =Integer.parseInt(ctx.read("$.rowCount").toString());
        assertTrue((rowCount>=1),"Aggregation_1hr has no data :Executed QUery:->"+postbody);
    }


/*
Takes SegmentSourceKey as input - calculation done to check for the day bucket in Cassandra
Query Cassandra and check whether the latest data is present
 */

    @Test(dataProvider = "segment-provider", dataProviderClass = APMDataProvider.class)
    public void CheckAggregationDaily(String assetSourceKey) throws Throwable{
        String Date;
        String Month;
        String Year;

        Calendar aggrCalendar =Calendar.getInstance();
        long currentmillsec=aggrCalendar.getTimeInMillis();
        aggrCalendar.setTimeInMillis(currentmillsec - 2100000 );
        if(localTimeZone.equals(aggrCalendar.getTimeZone().getID()))
            aggrCalendar.setTimeInMillis(currentmillsec-(2*86400000));
        if(localTimeZone.equals("America/New_York"))
            aggrCalendar.setTimeInMillis(currentmillsec-(86400000)+10800000);
        if(localTimeZone.equals("America/Chicago"))
            aggrCalendar.setTimeInMillis(currentmillsec-(86400000)+7200000);

        if(aggrCalendar.get(Calendar.DATE)<10)
            Date="0"+String.valueOf(aggrCalendar.get(Calendar.DATE));
        else
            Date= String.valueOf(aggrCalendar.get(Calendar.DATE));
        if((aggrCalendar.get(Calendar.MONTH)+1)<10)
            Month="0"+String.valueOf(aggrCalendar.get(Calendar.MONTH)+1);
        else
            Month= String.valueOf(aggrCalendar.get(Calendar.MONTH)+1);
        Year=String.valueOf(aggrCalendar.get(Calendar.YEAR));
        logger.info("millisec"+currentmillsec);
        logger.info("aggrCalendar.millisec"+aggrCalendar.getTimeInMillis());
        logger.info("Date:->"+aggrCalendar.get(Calendar.DATE));
        String totalDate=Year+Month+Date;


        String uri=getDataServiceUrl();
       // logger.info("uri:->"+uri);
       // logger.info("dataservicesurl:->"+super.dataservicesurl);
        //logger.info("totalDate:->"+totalDate);
        String dbname = getCassandraDbName();
        String tablename=getTableName("EVENTS_BY_DAILY_TABLE");
        String postbody="select * from "+dbname+"."+tablename+" where event_bucket='asset."+ assetSourceKey+".reading'"
                +" and yyyymmdd='"+totalDate+"'"+" limit 1";
        logger.info("postbody:->"+postbody);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(postbody).
                        expect().statusCode(200).
                        body("rows.event_bucket", CoreMatchers.notNullValue()).
                        when().
                        post(uri).
                        then().extract().response();
        String jsonString =response.getBody().asString();
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        int rowCount =Integer.parseInt(ctx.read("$.rowCount").toString());
        assertTrue((rowCount>=1),"Aggregation_1hr has no data :Executed QUery:->"+postbody);

    }

    //Unused Code will be removed when necessary-Alternate implementations

    /*

    private int maxDay(int i) {
        if((i==1)||(i==3)||(i==5)||(i==7)||(i==8)||(i==10)||(i==12))
            return 31;
        else if(i==2)
            return 29;
        else
            return 30;
    }

    */

    //Un implemented Code -Using the Calendar year,month,date,hour and minute to get the bucket - But not so efficient

    /*

        logger.info("calendar.month:->"+super.localCalendar.get(Calendar.MONTH));
        if((super.localCalendar.get(Calendar.DATE)-1)==0) {

            if ((super.localCalendar.get(Calendar.MONTH)) < 10)
                Month = "0" + String.valueOf(super.localCalendar.get(Calendar.MONTH));
            else
                Month = String.valueOf(super.localCalendar.get(Calendar.MONTH));
            int day=maxDay(super.localCalendar.get(Calendar.MONTH));
           // logger.info("day:->"+day);
            Date=String.valueOf(day);

        }

        else if((super.localCalendar.get(Calendar.DATE)-1)<10) {

            Date = "0" + String.valueOf(super.localCalendar.get(Calendar.DATE) - 1);
            if((super.localCalendar.get(Calendar.MONTH)+1)<10)
                Month="0"+String.valueOf(super.localCalendar.get(Calendar.MONTH)+1);
            else
                Month= String.valueOf(super.localCalendar.get(Calendar.MONTH)+1);
        }
        else {
            Date = String.valueOf(super.localCalendar.get(Calendar.DATE) - 1);
            if ((super.localCalendar.get(Calendar.MONTH) + 1) < 10)
                Month = "0" + String.valueOf(super.localCalendar.get(Calendar.MONTH) + 1);
            else
                Month = String.valueOf(super.localCalendar.get(Calendar.MONTH) + 1);
        }
        Year=String.valueOf(super.localCalendar.get(Calendar.YEAR));
        String totalDate=Year+Month+Date;
        */

    public String getCassandraDbName() {
        return getProperty(Constants.CASS_DB_NAME);
    }

    public String getTableName(String event)
    {
        if(event.equals("EVENTS_BY_FIFTEEN_MINUTES_TABLE"))
            return getProperty(Constants.EVENTS_BY_FIFTEEN_MINUTES_TABLE);
        else if(event.equals("EVENTS_BY_ONE_HOUR_TABLE"))
            return getProperty(Constants.EVENTS_BY_ONE_HOUR_TABLE);
        else
            return getProperty(Constants.EVENTS_BY_DAILY_TABLE);
    }

    protected String getDataServiceUrl() {
        return getProperty(Constants.DATA_SERVICES_URL);
    }


}
