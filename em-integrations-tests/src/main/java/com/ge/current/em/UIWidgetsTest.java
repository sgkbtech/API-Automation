

package com.ge.current.em;

import com.ge.current.em.provider.APMDataProvider;
import com.ge.current.em.provider.SolarProvider;
import com.ge.current.em.provider.UIWidgetProvider;
import com.ge.current.em.utils.Constants;
import com.ge.current.em.utils.TimePeriod;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;


import java.util.ArrayList;
import java.util.Calendar;

import static org.testng.Assert.assertTrue;

/**
 * Created by 502645575 on 10/24/16.
 */


public class UIWidgetsTest extends TestUtil {
    private static final Log logger = LogFactory.getLog(UIWidgetsTest.class);



//Consume Cost API - Takes souceKey(ENTERPRISE,SITE,SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and cost and kwh values are >0



    @Test(dataProvider = "TotalUIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void ConsumeCostApiTest(String enterpriseid,String timeperiod,String name,String abc) throws Throwable{
        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.CONSUME_COST);
        logger.info("consume-cost-uri->"+uri);

        Response response =
                RestAssured.given().
                        param("sourceKey", enterpriseid).
                        param("timePeriod",timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();

        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("{}")),"ConsumeCost:"+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        Double cost  = Double.parseDouble(ctx.read("$.cost").toString());
        Double kwh =Double.parseDouble(ctx.read("$.kwh").toString());
        assertTrue((cost > 0) &&(kwh > 0),"ConsumeCost :"+"SourceKey:"+enterpriseid+" "+"name:"+name+" "+"timePeriod:"+timeperiod+" "+"energy:"+kwh +" cost:"+cost);

    }





//Energy Generation API - Takes souceKey(ENTERPRISE,SITE,SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and energy generation  >0



    @Test(dataProvider = "UIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void EnergyGenerationApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.ENERGY_GENERATION);
        logger.info("EnergyGeneration->"+uri);

        Response response =
                RestAssured.given().
                        param("sourceKey", enterpriseid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        // logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("{}")),"EnergyGeneration:"+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        Double energy =Double.parseDouble(ctx.read("$[0].generation").toString());
        assertTrue(energy > Double.parseDouble("0"),"Energy Generation :"+"SourceKey:"+enterpriseid+" "+"name:"+name+" "+"timePeriod:"+timeperiod+" "+"energy:"+energy);


    }


//Consume Mapview - Takes EnterprisesouceKey,timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and KPI values are >0



    @Test(dataProvider = "Enterprise-provider", dataProviderClass = APMDataProvider.class)
    public void MapViewApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        logger.info("mapviewtest:->"+"mapview");

        String uri = getProperty(Constants.AGGREGATION_URL) + getProperty(Constants.MAP_VIEW);
        logger.info("Mapview->" + uri);

        Response response =
                RestAssured.given().
                        param("enterpriseSourceKey", enterpriseid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("{}")),"Mapview:"+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        // logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        Double high_min =Double.parseDouble(ctx.read("$.KPI_Ranges.siteSavings.high.min").toString());
        Double high_max =Double.parseDouble(ctx.read("$.KPI_Ranges.siteSavings.high.max").toString());
        Double low_min =Double.parseDouble(ctx.read("$.KPI_Ranges.siteSavings.low.min").toString());
        Double low_max =Double.parseDouble(ctx.read("$.KPI_Ranges.siteSavings.low.max").toString());
        Double medium_min =Double.parseDouble(ctx.read("$.KPI_Ranges.siteSavings.medium.min").toString());
        Double medium_max =Double.parseDouble(ctx.read("$.KPI_Ranges.siteSavings.medium.max").toString());
        // logger.info("high_min"+high_min);
        //logger.info("high_max"+high_max);
        //logger.info("low_min"+low_min);
        //logger.info("low_max"+low_max);
        //logger.info("medium_min"+medium_min);
        //logger.info("medium_max"+medium_max);
        assertTrue((high_min >0)&&(high_max>0)&&(low_min ==0)&&(low_max>0)&&(medium_min >0)&&(medium_max>0),"Map View :"+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" high-min:"+high_min+" high-max:"+high_max+" low-min:"+low_min+" low-max:"+low_max+" medium-max:"+medium_max+" medium-min:"+medium_min);


    }



//SelfGeneration API- Takes souceKey(ENTERPRISE,SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and on_peak +off_peak =1





    @Test(dataProvider = "UIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void SelfGenerationApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL) +getProperty(Constants.SELF_GENERATION);
        logger.info("SelfGeneration->"+uri);

        Response response =
                RestAssured.given().
                        param("resourceId", enterpriseid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        // logger.info("Response Body:" + jsonString);
        // logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        // logger.info("jsonString"+jsonString);

        assertTrue((!jsonString.equals("{}")),"SelfGeneration:"+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        Double on_peak =Double.parseDouble(ctx.read("$.['On Peak']").toString());
        Double off_peak =Double.parseDouble(ctx.read("$.['Off Peak']").toString());

        logger.info("on_peak"+on_peak);
        logger.info("off_peak"+off_peak);

        assertTrue((on_peak+off_peak)==(double)(1),"SelfGeneration:"+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"On Peak:"+on_peak+" "+"Off Peak:"+off_peak);
    }


//loadByAssetType API - Takes souceKey(ENTERPRISE,SITE,SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 and data is present
//Assertions are not made because couldn't figure out the JSONPath to retrieve the values




    @Test(dataProvider = "TotalUIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void loadByAssetTypeApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.LOAD_BY_ASSET_TYPE);
        logger.info("loadByAssetType->"+uri);

        Response response =
                RestAssured.given().
                        param("resourceId", enterpriseid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("{}")),"loadByAssetType:"+"resourceId:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        // logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());



    }

//Spending API- Takes souceKey(ENTERPRISE,SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and dollar >0

    @Test(dataProvider = "UIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void SpendingApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.ENERGY)+"/"+getProperty(Constants.SPENDING);
        logger.info("Spending->"+uri);

        Response response =
                RestAssured.given().
                        param("sourceKey", enterpriseid).
                        param("timePeriod", timeperiod).
                        param("cost", "true").
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("[]")),"Spending:"+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        // logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        Double dollar_val =Double.parseDouble(ctx.read("$[0].['value']").toString());
        assertTrue((dollar_val > 0),"Spending:"+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"Dollar:"+dollar_val);
    }



//Savings API- Takes souceKey(ENTERPRISE,SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and dollar >0

    @Test(dataProvider = "UIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void SavingsApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.ENERGY)+"/"+getProperty(Constants.SAVINGS);
        logger.info("Savings->"+uri);

        Response response =
                RestAssured.given().
                        param("sourceKey", enterpriseid).
                        param("timePeriod", timeperiod).
                        param("cost", "true").
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("[]")),"Savings:"+"URL:"+uri+":SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        // logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        Double current_val =Double.parseDouble(ctx.read("$[0].['currentValue']").toString());
        assertTrue((current_val > 0),"Savings: "+"URL:"+uri+" :SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"Dollar:"+current_val);

    }



//WatchList API- Takes souceKey(ENTERPRISE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and sum >0

    @Test(dataProvider = "Enterprise-provider", dataProviderClass = APMDataProvider.class)
    public void WatchListApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        logger.info("id:->"+enterpriseid);

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.WATCH_LIST);
        logger.info("WatchList->"+uri);

        Response response =
                RestAssured.given().
                        param("enterpriseId", enterpriseid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("[]")),"WatchList:"+"URL:"+uri+":SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        // logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<Object> usage =ctx.read("$[0].details[*].usage");
        //logger.info("current_val.size"+usage.size());
        double sum =0;
        for(int i=0;i<usage.size();i++)
        {
            sum =sum+Double.parseDouble(usage.get(i).toString());
        }
        assertTrue((sum > 0),"WatchList:"+"URL:"+uri+":SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"Usage:"+sum);


    }


//PeakMetricUsage API- Takes souceKey(ENTERPRISE,SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and usage >0




    @Test(dataProvider = "UIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void PeakMetricUsageApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        logger.info("id"+enterpriseid);

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.PEAK_METRIC_USAGE);
        logger.info("PeakMetricUsage->"+uri);

        Response response =
                RestAssured.given().
                        param("assetUid", enterpriseid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("[]")),"PeakMetricUsage:"+"URL:"+uri+":SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<Object> usage =ctx.read("$[*].['usage']");
        //logger.info("usage"+usage.size());
        double sum =0;
        for(int i=0;i<usage.size();i++)
        {
            sum =sum+Double.parseDouble(usage.get(i).toString());
        }
        assertTrue((sum >(double)(0)),"PeakMetricUsage:"+"URL:"+uri+":SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"Usage:"+sum);


    }



//PeakDollarUsage API- Takes souceKey(ENTERPRISE,SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and usage >0



    @Test(dataProvider = "UIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void PeakDollarUsageApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.PEAK_DOLLAR_USAGE);
        logger.info("PeakDollarUsage->"+uri);

        Response response =
                RestAssured.given().
                        param("assetUid", enterpriseid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"PeakDollarUsage:"+"URL:"+uri+":SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<Object> usage =ctx.read("$[*].['usage']");
        //logger.info("usage"+usage.size());
        double sum =0;
        for(int i=0;i<usage.size();i++)
        {
            sum =sum+Double.parseDouble(usage.get(i).toString());
        }
        assertTrue((sum >(double)(0)),"PeakDollarUsage:"+"URL:"+uri+":SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"Usage:"+sum);



    }



//AlarmsCount API- Takes souceKey(ENTERPRISE,SITE,SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and fault >=0



    @Test(dataProvider = "TotalUIWidget-provider", dataProviderClass = UIWidgetProvider.class)
    public void AlarmsCountApiTest(String enterpriseid,String timeperiod,String name) throws Throwable{

        //logger.info("siteid"+enterpriseid);

        String uri=getProperty(Constants.L0_ALARMS_URL)+getProperty(Constants.ALARMS_COUNT);
        logger.info("AlarmsCount->"+uri);

        Response response =
                RestAssured.given().
                        param("parentId", enterpriseid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("{}")),"AlarmsCount:"+"URL:"+uri+":SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        // logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        int fault = ctx.read("$.fault");
        assertTrue((fault >= (int) 0),"AlarmsCount:"+"URL:"+uri+"SourceKey:"+enterpriseid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"Fault:"+fault);

    }


//CategoryMetricUsage API- Takes souceKey(SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present
//Assertions are not made because couldn't figure out the JSONPath to retrieve the values


    @Test(dataProvider = "site-provider", dataProviderClass = APMDataProvider.class)
    public void categoryMetricUsageApiTest(String siteid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.CATEGORY_METRIC_USAGE);
        logger.info("categoryMetricUsage->"+uri);

        Response response =
                RestAssured.given().
                        param("siteSourceKey", siteid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());

        assertTrue((!jsonString.equals("{}")),"categoryMetricUsage:"+"URL:"+uri+":SourceKey:"+siteid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");

    }


//UsageForecast API- Takes souceKey(SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present
//Assertions are not made because couldn't figure out the JSONPath to retrieve the values and what values need to be checked



    @Test(dataProvider = "Forecast-provider", dataProviderClass = APMDataProvider.class)
    public void UsageForecastApiTest(String siteid,String group,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.USAGE_FORECAST);

        logger.info("UsageForecast->"+uri);

        ArrayList<String> timeStamp =getTimeStamp(group);

        // logger.info("fulluri:->"+super.aggregationurl+super.usageForecast+"?"+"siteSourceKey"+"="+siteid+"&"+"startTimestamp"+"="+timeStamp.get(0)+"&"+"endTimestamp"+"="+timeStamp.get(1)+"&"+"groupBy"+"="+group);

        Response response =
                RestAssured.given().
                        param("siteSourceKey", siteid).
                        param("startTimestamp", timeStamp.get(0)).
                        param("endTimestamp", timeStamp.get(1)).
                        param("groupBy",group).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("[]")),"UsageForecast:"+"URL:"+uri+":SourceKey:"+siteid+" "+"name:"+name+" group:"+group+" "+"startTimestamp:"+timeStamp.get(0)+" "+"endTimestamp:"+timeStamp.get(1)+" HAS NO DATA");
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());



    }

//UsageHistoric API- Takes souceKey(SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present
//Assertions are not made because couldn't figure out the JSONPath to retrieve the values and what values need to be checked



    @Test(dataProvider = "Forecast-provider", dataProviderClass = APMDataProvider.class)
    public void UsageHistoricApiTest(String siteid,String group,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.USAGE_HISTORIC);

        logger.info("UsageHistoric->"+uri);

        ArrayList<String> timeStamp =getTimeStamp(group);

        //logger.info("fulluri:->"+super.aggregationurl+super.usageHistoric+"?"+"siteSourceKey"+"="+siteid+"&"+"startTimestamp"+"="+timeStamp.get(0)+"&"+"endTimestamp"+"="+timeStamp.get(1)+"&"+"groupBy"+"="+group);

        Response response =
                RestAssured.given().
                        param("siteSourceKey", siteid).
                        param("startTimestamp", timeStamp.get(0)).
                        param("endTimestamp", timeStamp.get(1)).
                        param("groupBy",group).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();

        assertTrue((!jsonString.equals("[]")),"UsageHistoric:"+"URL:"+uri+":SourceKey:"+siteid+" "+"name:"+name+" group:"+group+" "+"startTimestamp:"+timeStamp.get(0)+" "+"endTimestamp:"+timeStamp.get(1)+" HAS NO DATA");
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());



    }


//Billing API- Takes souceKey(SITE),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present
//Assertions are not made because couldn't figure out the JSONPath to retrieve the values and what values need to be checked




    @Test
    public void BillingApiTest() throws Throwable{

        String uri=getProperty(Constants.BILLING_URL)+getProperty(Constants.SITE_SOURCE_KEY)+"/"+"card";
        logger.info("Billing->"+uri);
        //logger.info("Billing_fulluri:->"+super.aggregationurl+super.usageHistoric+"?"+"siteSourceKey"+"="+siteid+"&"+"startTimestamp"+"="+timeStamp.get(0)+"&"+"endTimestamp"+"="+timeStamp.get(1)+"&"+"groupBy"+"="+group);

        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"Billing:"+"URL:"+uri+":SourceKey:"+getProperty(Constants.SITE_SOURCE_KEY)+" "+"name:"+getProperty(Constants.SITE_SOURCE_KEY)+" HAS NO DATA");



    }


//getElectricityConsumed API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present,consumed > 0



    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void getElectricityConsumedApiTest(String segmentid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.GET_ELECTRICITY_CONSUMED);
        logger.info("getElectricityConsumed->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());

        assertTrue((!jsonString.equals("{}")),"getElectricityConsumed:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        Double consumed = Double.parseDouble(ctx.read("$.electricityConsumed").toString());
        assertTrue((consumed > (double) 0),"getElectricityConsumed:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"Consumed:"+consumed);

    }


//getElectricityGenerated API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present,generated > 0


    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void getElectricityGeneratedApiTest(String segmentid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.GET_ELECTRICITY_GENERATED);
        logger.info("getElectricityGenerated->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("{}")),"getElectricityGenerated:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        Double generated = Double.parseDouble(ctx.read("$.electricityGenerated").toString());
        assertTrue((generated > (double) 0),"getElectricityGenerated:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"generated:"+generated);

    }


//getProductionMeter API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present,average > 0


    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void getProductionMeter(String segmentid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.GET_PRODUCTION_METER);
        logger.info("getProductionMeter->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"getProductionMeter:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        Double average = Double.parseDouble(ctx.read("$[0].average").toString());
        assertTrue((average > (double) 0),"getProductionMeter:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"average:"+average);

    }


//getCumulativeReading API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present,average > 0


    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void getCumulativeReadingApiTest(String segmentid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.GET_CUMULATIVE_READING);
        logger.info("getCumulativeReading->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"getCumulativeReading:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        Double average = Double.parseDouble(ctx.read("$[0].average").toString());
        assertTrue((average > (double) 0),"getCumulativeReading:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"average:"+average);


    }




//getPerformanceRatio API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present,average > 0


    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void getPerformanceRatioApiTest(String segmentid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.GET_PERFOMANCE_RATIO);
        logger.info("getPerformanceRatio->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"getPerformanceRatio:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        Double average = Double.parseDouble(ctx.read("$[0].average").toString());
        assertTrue((average > (double) 0),"getPerformanceRatio:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"average:"+average);

    }




//getUptime API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present,average > 0



    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void getUptimeApiTest(String segmentid,String timeperiod,String name) throws Throwable{

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.GET_UPTIME);
        logger.info("getUptime->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"getUptime:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        Double average = Double.parseDouble(ctx.read("$[0].average").toString());
        assertTrue((average > (double) 0),"getUptime:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"average:"+average);

    }



//segmentScheduleSavings API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present,average > 0



    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void segmentScheduleSavingsApiTest(String segmentid,String timeperiod,String name) throws Throwable{

        // logger.info("segmentid:->"+segmentid);



        //String segmentName=super.entitygivenkey.get(segmentid);

        //logger.info("segmentName:->"+segmentName);

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.SEGMENT_SCHEDULE_SAVINGS);
        logger.info("segmentScheduleSavings->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        param("cost", "true").
                        param("assetType", name).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        assertTrue((!jsonString.equals("[]")),"segmentScheduleSavings:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<Object> average = ctx.read("$[*].currentValue");
        double sum =0;
        for(int i=0;i<average.size();i++)
        {
            sum =sum+Double.parseDouble(average.get(i).toString());
        }

        //logger.info("sum"+sum);

        assertTrue((sum > (double) 0),"segmentScheduleSavings:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"average:"+sum);


    }



//segmentScheduleSpending API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present,average > 0


    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void segmentScheduleSpendingApiTest(String segmentid,String timeperiod,String name) throws Throwable{

        // logger.info("segmentid:->"+segmentid);

        //String segmentName=getSegmentName(segmentid);

        //logger.info("segmentName:->"+segmentName);

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.SEGMENT_SCHEDULE_SPENDING);
        logger.info("segmentScheduleSpending->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        param("cost", "true").
                        param("assetType", name).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"segmentScheduleSpending:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        //  logger.info("Response Body:" + jsonString);
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<Object> average = ctx.read("$[*].value");
        double sum =0;
        for(int i=0;i<average.size();i++)
        {
            sum =sum+Double.parseDouble(average.get(i).toString());
        }

        //logger.info("sum"+sum);

        assertTrue((sum > (double) 0),"segmentScheduleSpending:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"average:"+sum);



    }

//Complaince API- Takes souceKey(SEGMENT),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present



    @Test(dataProvider = "segmentUI-provider", dataProviderClass = APMDataProvider.class)
    public void ComplainceApiTest(String segmentid,String timeperiod,String name) throws Throwable{
        // logger.info("segmentid:->"+segmentid);
        //String segmentName=getSegmentName(segmentid);
        //logger.info("segmentName:->"+segmentName);

        String uri=getProperty(Constants.COMPLAINCE_URL);
        ArrayList<String> timestamp=getCompliancetimeStamp(timeperiod);
        logger.info("Compliance->"+uri);
        logger.info("starttimestamp"+timestamp.get(0));
        logger.info("endtimestamp"+timestamp.get(1));

        Response response =
                RestAssured.given().
                        param("segmentId", segmentid).
                        param("startTimestamp", timestamp.get(0)).
                        param("endTimestamp", timestamp.get(1)).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"Compliance:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        //  logger.info("Response Body:" + jsonString);

    }




//solarProductionWidget API- Takes souceKey(SEGMENT-SOLAR),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and on_peak and off_peak > 0


    @Test(dataProvider = "solar-provider", dataProviderClass = SolarProvider.class)
    public void solarProductionWidgetApiTest(String segmentid,String timeperiod,String name) throws Throwable{

        // logger.info("segmentid:->"+segmentid);
        // String segmentName=getSegmentName(segmentid);
        //logger.info("segmentName:->"+segmentName);

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.SOLAR_PRODUCTION_WIDGET);
        logger.info("solarProductionWidget->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("[]")),"solarProductionWidget:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<Object> on_peak = ctx.read("$[*].['On Peak']");
        ArrayList<Object> off_peak = ctx.read("$[*].['Off Peak']");
        double sum1 =0;
        for(int i=0;i<on_peak.size();i++)
        {
            sum1 =sum1+Double.parseDouble(on_peak.get(i).toString());
        }
        double sum2 =0;
        for(int i=0;i<off_peak.size();i++)
        {
            sum2 =sum2+Double.parseDouble(on_peak.get(i).toString());
        }

        assertTrue((( sum1 > (double) 0)&&(sum2 > (double) 0)),"solarProductionWidget:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"on_peak:"+sum1+" "+"off_peak:"+sum2);
    }


//solarProductionMetrics API- Takes souceKey(SEGMENT-SOLAR),timeperiod(MTD,YTD,TODAY),entity name as input
//Check whether the api returns 200 ,data is present and energyCharges and demandCharges > 0


    @Test(dataProvider = "solar-provider", dataProviderClass = SolarProvider.class)
    public void solarProductionMetricsApiTest(String segmentid,String timeperiod,String name) throws Throwable{
        // logger.info("segmentid:->"+segmentid);
        // String segmentName=getSegmentName(segmentid);
        //logger.info("segmentName:->"+segmentName);

        String uri=getProperty(Constants.AGGREGATION_URL)+getProperty(Constants.SOLAR_PRODUCTION_METRICS);
        logger.info("solarProductionMetrics->"+uri);

        Response response =
                RestAssured.given().
                        param("segmentSourceKey", segmentid).
                        param("timePeriod", timeperiod).
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("{}")),"solarProductionMetrics:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        Double energyCharges =Double.parseDouble(ctx.read("$.savings.energyCharges").toString());
        Double demandCharges =Double.parseDouble(ctx.read("$.savings.demandCharges").toString());
        assertTrue((( energyCharges > (double) 0)&&(demandCharges > (double) 0)),"solarProductionMetrics:"+"URL:"+uri+":SourceKey:"+segmentid+" "+"name:"+name+" timePeriod:"+timeperiod+" "+" "+"energyCharges:"+energyCharges+" "+"demandCharges:"+demandCharges);
    }



//WeatherService API- Uses souceKey(SITE-CROTONVILLE),Latitude and Longitude as input
//Check whether the api returns 200 ,data is present and energyCharges and demandCharges > 0


    @Test
    public void WeatherServiceApiTest() throws Throwable{

        String uri=getProperty(Constants.WEATHER_URL)+"now"+"/"+getProperty(Constants.SITE_SOURCE_KEY)+"/"+latitude+"/"+longitude;
        logger.info("WeatherService->"+uri);

        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        //  logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        assertTrue((!jsonString.equals("{}")),"WeatherService:"+"URL:"+uri+":SourceKey:"+getProperty(Constants.SITE_SOURCE_KEY)+" "+"name:"+entitygivenkey.get(getProperty(Constants.SITE_SOURCE_KEY))+" "+"HAS NO DATA");
        ReadContext ctx = JsonPath.parse(jsonString);
        Double temp = ctx.read("$.measures.temperature");
        Double humidity = ctx.read("$.measures.humidity");
        assertTrue(((temp != (double) 0)&&(humidity != (double) 0)),"WeatherService:"+"URL:"+uri+":SourceKey:"+getProperty(Constants.SITE_SOURCE_KEY)+" "+"name:"+entitygivenkey.get(getProperty(Constants.SITE_SOURCE_KEY))+" "+"Temperature:"+temp+" "+"Humidity:"+humidity);


    }



//Pass the timeStamp for UsageForecast and Usage Historic in the format yyyymmddhh acording to group
    //Returns a list of strings havings startdatetime and enddatetime


    public ArrayList<String> getTimeStamp(String group)
    {
        String startHour,endHour,startDay,endDay,startMonth,endMonth,startYear,endYear;

        if(group.toUpperCase().equals("HOURLY"))
        {
            if(localTimeZone.equals("America/New_York")) {
                if ((localCalendar.get(Calendar.HOUR_OF_DAY) + 3) < 10)
                    startHour = "0"+String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
                else
                    startHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
            }
            else {
                if ((localCalendar.get(Calendar.HOUR_OF_DAY)) < 10)
                    startHour = "0"+String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY));
                else
                    startHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY));
            }
            endHour=startHour;

            if ((localCalendar.get(Calendar.DATE)) < 10)
                startDay = "0"+String.valueOf(localCalendar.get(Calendar.DATE));
            else
                startDay = String.valueOf(localCalendar.get(Calendar.DATE));

            if ((localCalendar.get(Calendar.DATE)+1) < 10)
                endDay = "0"+String.valueOf(localCalendar.get(Calendar.DATE)+1);
            else
                endDay = String.valueOf(localCalendar.get(Calendar.DATE)+1);

            if ((localCalendar.get(Calendar.MONTH)+1) < 10)
                startMonth = "0"+String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            else
                startMonth = String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            endMonth=startMonth;

            startYear=String.valueOf(localCalendar.get(Calendar.YEAR));
            endYear=startYear;

        }

        else if(group.toUpperCase().equals("DAILY"))
        {
            if(localTimeZone.equals("America/New_York")) {
                if ((localCalendar.get(Calendar.HOUR_OF_DAY) + 3) < 10)
                    startHour = "0"+String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
                else
                    startHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
            }
            else {
                if ((localCalendar.get(Calendar.HOUR_OF_DAY)) < 10)
                    startHour = "0"+String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY));
                else
                    startHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY));
            }
            endHour=startHour;

            if ((localCalendar.get(Calendar.DATE)) < 10)
                startDay = "0"+String.valueOf(localCalendar.get(Calendar.DATE));
            else
                startDay = String.valueOf(localCalendar.get(Calendar.DATE));

            if ((localCalendar.get(Calendar.DATE)+12) < 10)
                endDay = "0"+String.valueOf(localCalendar.get(Calendar.DATE)+12);
            else
                endDay = String.valueOf(localCalendar.get(Calendar.DATE)+12);

            if ((localCalendar.get(Calendar.MONTH)) < 10)
                startMonth = "0"+String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            else
                startMonth = String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            endMonth=startMonth;

            startYear=String.valueOf(localCalendar.get(Calendar.YEAR));
            endYear=startYear;
        }
        else
        {
            if(localTimeZone.equals("America/New_York")) {
                if ((localCalendar.get(Calendar.HOUR_OF_DAY) + 3) < 10)
                    startHour = "0"+String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
                else
                    startHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
            }
            else {
                if ((localCalendar.get(Calendar.HOUR_OF_DAY)) < 10)
                    startHour = "0"+String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY));
                else
                    startHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY));
            }
            endHour=startHour;

            if ((localCalendar.get(Calendar.DATE)) < 10)
                startDay = "0"+String.valueOf(localCalendar.get(Calendar.DATE));
            else
                startDay = String.valueOf(localCalendar.get(Calendar.DATE));
            endDay=startDay;

            if ((localCalendar.get(Calendar.MONTH)+1) < 10)
                startMonth = "0"+String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            else
                startMonth = String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            endMonth=String.valueOf(localCalendar.get(Calendar.MONTH)+2);

            startYear=String.valueOf(localCalendar.get(Calendar.YEAR));
            endYear=String.valueOf(localCalendar.get(Calendar.YEAR)+1);
        }
        //logger.info("startDay+"+startDay);
        // logger.info("endDay+"+endDay);
        String startDate=startYear+startMonth+startDay+startHour;
        String endDate=endYear+endMonth+endDay+endHour;
        ArrayList<String> timeStamp=new ArrayList<String>();
        timeStamp.add(startDate);
        timeStamp.add(endDate);
        return timeStamp;
    }

//Pass the timeStamp for Compliance in the format yyyymmddhh acording to TODAY,MTD,YTD
    //Returns a list of strings havings startdatetime and enddatetime

    public ArrayList<String> getCompliancetimeStamp(String timePeriod)
    {
        String startHour=null,endHour=null,startDay=null,endDay=null,startMonth=null,endMonth=null,startYear=null,endYear=null;
        if(timePeriod.equals("TODAY"))
        {
            startHour="00";
            if(localTimeZone.equals("America/New_York")) {

                if ((localCalendar.get(Calendar.HOUR_OF_DAY) + 3) < 10)
                    endHour = "0" + String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
                else
                    endHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
            }



            if ((localCalendar.get(Calendar.DATE)) < 10)
                startDay = "0"+String.valueOf(localCalendar.get(Calendar.DATE));
            else
                startDay = String.valueOf(localCalendar.get(Calendar.DATE));

            endDay=startDay;

            if ((localCalendar.get(Calendar.MONTH)) < 10)
                startMonth = "0"+String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            else
                startMonth = String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            endMonth=startMonth;

            startYear=String.valueOf(localCalendar.get(Calendar.YEAR));
            endYear=startYear;
        }
        else if(timePeriod.equals("MTD"))
        {
            startHour="00";
            if(localTimeZone.equals("America/New_York")) {

                if ((localCalendar.get(Calendar.HOUR_OF_DAY) + 3) < 10)
                    endHour = "0" + String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
                else
                    endHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
            }
            startDay="01";
            if ((localCalendar.get(Calendar.DATE)) < 10)
                endDay = "0"+String.valueOf(localCalendar.get(Calendar.DATE));
            else
                endDay = String.valueOf(localCalendar.get(Calendar.DATE));
            if ((localCalendar.get(Calendar.MONTH)+1) < 10)
                startMonth = "0"+String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            else
                startMonth = String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            endMonth=startMonth;
            startYear=String.valueOf(localCalendar.get(Calendar.YEAR));
            endYear=startYear;
        }
        else
        {
            startHour="00";
            if(localTimeZone.equals("America/New_York")) {

                if ((localCalendar.get(Calendar.HOUR_OF_DAY) + 3) < 10)
                    endHour = "0" + String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
                else
                    endHour = String.valueOf(localCalendar.get(Calendar.HOUR_OF_DAY) + 3);
            }
            startDay="01";
            if ((localCalendar.get(Calendar.DATE)) < 10)
                endDay = "0"+String.valueOf(localCalendar.get(Calendar.DATE));
            else
                endDay = String.valueOf(localCalendar.get(Calendar.DATE));
            if ((localCalendar.get(Calendar.MONTH)) < 10)
                startMonth = "0"+String.valueOf(localCalendar.get(Calendar.MONTH));
            else
                startMonth = String.valueOf(localCalendar.get(Calendar.MONTH));
            if ((localCalendar.get(Calendar.MONTH)+1) < 10)
                endMonth = "0"+String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            else
                endMonth = String.valueOf(localCalendar.get(Calendar.MONTH)+1);
            startYear=String.valueOf(localCalendar.get(Calendar.YEAR));
            endYear=startYear;
        }
        String startDate=startYear+startMonth+startDay+startHour;
        String endDate=endYear+endMonth+endDay+endHour;
        ArrayList<String> timeStamp=new ArrayList<String>();
        timeStamp.add(startDate);
        timeStamp.add(endDate);
        return timeStamp;
    }



}
