package com.ge.current.em.provider;

/**
 * Created by 502645575 on 11/12/16.
 */
/*
 * Copyright (c) 2016 GE. All Rights Reserved.
 * GE Confidential: Restricted Internal Distribution
 */

import java.util.ArrayList;
import java.util.List;

import com.ge.current.em.TestUtil;
import com.ge.current.em.utils.TimePeriod;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.ge.current.em.utils.Cost;

import static com.ge.current.em.TestUtil.entitygivenkey;


public class APMDataProvider extends DataProviderBase {

    private static final Log LOGGER = LogFactory.getLog(APMDataProvider.class);

    @DataProvider(name = "Enterprise-provider")
    public static Object[][] getEnterprises(ITestContext context) {
        LOGGER.info("someshit->"+"getEnterprises");
        if(TestUtil.environment.equals("Sanity.configuration.properties")||( TestUtil.environment.equals("McDonaldsSanity.configuration.properties")))
        {
            Object[][] data =new Object[1][3];
            data[0][0] =(String) getEnterpriseSourceKey(context);
            data[0][1]=(String) TimePeriod.TODAY.getAbbr();
            data[0][2]= (String) entitygivenkey.get(getEnterpriseSourceKey(context));
            return data;
        }

        List<TimePeriod> timePeriods = TimePeriod.timePeriodsAsList();
        Object[][] data = new Object[timePeriods.size()][3];
        for (int i = 0; i < timePeriods.size(); i++) {
            data[i][0] =(Object) getEnterpriseSourceKey(context);
            data[i][1] = (Object) timePeriods.get(i).getAbbr();
            data[i][2]= (Object) entitygivenkey.get(getEnterpriseSourceKey(context));

        }
        return data;

    }

    @DataProvider(name = "site-provider")
    public static Object[][] getSites(ITestContext context) {

        if(TestUtil.environment.equals("Sanity.configuration.properties")||( TestUtil.environment.equals("McDonaldsSanity.configuration.properties")))
        {
            Object[][] data =new Object[1][3];
            data[0][0] = (Object) getSiteSourceKey(context);
            data[0][1]=(Object) TimePeriod.TODAY.getAbbr();
            data[0][2]= (Object) entitygivenkey.get(getSiteSourceKey(context));
            return data;
        }

        List<TimePeriod> timePeriods = TimePeriod.timePeriodsAsList();
        Object[][] data = new Object[timePeriods.size()][3];
        for (int i = 0; i < timePeriods.size(); i++) {
            data[i][0] = (Object) getSiteSourceKey(context);
            data[i][1] =(Object) timePeriods.get(i).getAbbr();
            data[i][2]= (Object) entitygivenkey.get(getSiteSourceKey(context));

        }
        return data;

    }



    @DataProvider(name = "segmentUI-provider")
    public static Object[][] getSegmentsUI(ITestContext context) {

        if(TestUtil.environment.equals("Sanity.configuration.properties")||( TestUtil.environment.equals("McDonaldsSanity.configuration.properties")))
        {
            Object[][] data =new Object[1][3];
            data[0][0] = (Object) getSegmentSourceKey(context);
            data[0][1]=(Object) TimePeriod.TODAY.getAbbr();
            data[0][2]= (Object) entitygivenkey.get(getSegmentSourceKey(context));
            return data;
        }
        Object[][] sourceKey =getSegments(context);
        Object[][] data =new Object[sourceKey.length*3][3];
        List<TimePeriod> timePeriods = TimePeriod.timePeriodsAsList();
        int z=0;
        for (int i = 0; i < sourceKey.length; i++) {
            for (int j = 0; j < timePeriods.size(); ++j) {
                data[z+j][0] = (Object) sourceKey[i][0];
                data[z+j][1] = (Object) timePeriods.get(j).getAbbr();
                data[z+j][2] = (Object) entitygivenkey.get(sourceKey[i][0]);

            }
            z +=3;
        }
        return data;

    }

    @DataProvider(name = "segment-provider")
    public static Object[][] getSegments(ITestContext context) {

        if(TestUtil.environment.equals("Sanity.configuration.properties")||(TestUtil.environment.equals("Test.configuration.properties")||( TestUtil.environment.equals("McDonaldsSanity.configuration.properties"))))
        {
            Object[][] data=new Object[1][1];
            data [0][0]=(Object) getSegmentSourceKey(context);
            return data;

        }

        ArrayList<String> segmentProp = new ArrayList<String>();

        String uri = getApmUrl(context) + "sites" + "/" + getSiteSourceKey(context) + "/" + "children";
        LOGGER.info("segment uri:->" + uri);
        Response response = RestAssured.given().
                contentType("application/json").
                expect().statusCode(200).
                when().
                get(uri).
                then().
                extract().response();

        String jsonString = response.getBody().asString();

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = (JSONArray) jo.get("content");

        List<String> segmentSourceKey = new ArrayList<>();
        for (int j = 0; j < ja.length(); j++) {
            JSONArray joProp = (JSONArray) ja.getJSONObject(j).get("properties");
            if (joProp.length() != 0) {
                JSONObject joname = joProp.getJSONObject(0);
                if (joname.get("id").equals("CorrelatedSubmeters")) {
                    Object sourceKey = (String) ja.getJSONObject(j).get("sourceKey");
                    segmentSourceKey.add(sourceKey.toString());
                }
            }
        }

        Object[][] data = new Object[segmentSourceKey.size()][1];
        for (int i = 0; i < segmentSourceKey.size(); i++) {
            data[i][0] = segmentSourceKey.get(i);
        }

        return data;
    }



    @DataProvider(name = "Forecast-provider")
    public static Object[][] getForecastProvider(ITestContext context) {

        if(TestUtil.environment.equals("Sanity.configuration.properties")|( TestUtil.environment.equals("McDonaldsSanity.configuration.properties")))
        {
            Object[][] data =new Object[1][3];
            data[0][0] = (Object) getSegmentSourceKey(context);
            data[0][1]=(Object) Cost.HOURLY.getAbbr();
            data[0][2]= (Object) entitygivenkey.get(getSegmentSourceKey(context));
            return data;
        }

        List<Cost> costs = Cost.costsAsList();
        Object[][] data = new Object[costs.size()][3];
        for (int i = 0; i < costs.size(); i++) {
            data[i][0] = (Object) getSiteSourceKey(context);
            data[i][1] = (Object) costs.get(i).toString();
            data[i][2]= (Object) entitygivenkey.get(getSiteSourceKey(context));

        }
        return data;

    }

    @DataProvider(name = "solar-provider")
    public static Object[][] getSolar(ITestContext context) throws NullPointerException {

        String solarSourceKey=null;

        String uri=getApmUrl(context)+"sites"+"/"+getSiteSourceKey(context)+"/"+"children";
        //logger.info("segment uri:->"+uri);
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
        //logger.info("Response Body:"+jsonString);
        JSONObject jo=new JSONObject(jsonString);
        JSONArray ja = (JSONArray) jo.get("content");
        //System.out.println("JA:"+ja.length());
        for(int j=0;j<ja.length();j++)
        {
            JSONObject Segment =ja.getJSONObject(j);
            if(Segment.get("name").equals("SOLAR"))
                solarSourceKey=Segment.get("sourceKey").toString();


        }

        if(TestUtil.environment.equals("Sanity.configuration.properties")||( TestUtil.environment.equals("McDonaldsSanity.configuration.properties")))
        {
            Object[][] data =new Object[1][3];
            data[0][0] = (Object) solarSourceKey;
            data[0][1]=(Object) TimePeriod.TODAY.getAbbr();
            data[0][2]= (Object) entitygivenkey.get(solarSourceKey);
            return data;
        }

        List<TimePeriod> timePeriods = TimePeriod.timePeriodsAsList();

        Object[][] data = new Object[3][3];
        data[0][0]=(Object) solarSourceKey;
        data[0][1]= timePeriods.get(0).getAbbr();
        data[0][2]=(Object) entitygivenkey.get(solarSourceKey);
        data[1][0]=(Object) solarSourceKey;
        data[1][1]=timePeriods.get(1).getAbbr();;
        data[1][2]=(Object) entitygivenkey.get(solarSourceKey);
        data[2][0]=(Object) solarSourceKey;
        data[2][1]=timePeriods.get(2).getAbbr();;
        data[2][2]=(Object) entitygivenkey.get(solarSourceKey);


        return data;
    }


}
