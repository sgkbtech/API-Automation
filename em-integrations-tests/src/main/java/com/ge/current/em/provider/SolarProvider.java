package com.ge.current.em.provider;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import com.ge.current.em.utils.TimePeriod;
import org.testng.annotations.DataProvider;

import static com.ge.current.em.TestUtil.entitygivenkey;

import java.util.List;

/**
 * Created by 502645575 on 11/12/16.
 */
public class SolarProvider extends DataProviderBase {


    @DataProvider(name = "solar-provider")
    public static Object[][] getSolar(ITestContext context) throws NullPointerException {

        String segmentSourceKey=null;

        String uri=getApmUrl(context) + "sites" + "/" + getSiteSourceKey(context) + "/" + "children";
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
                segmentSourceKey=Segment.get("sourceKey").toString();


        }
        List<TimePeriod> timePeriods = TimePeriod.timePeriodsAsList();

        Object[][] data = new Object[3][3];
        for(int i=0;i<3;i++) {
            data[i][0] = (Object) segmentSourceKey;
            data[i][1] = timePeriods.get(i).getAbbr();
            data[i][2] = (Object) entitygivenkey.get(segmentSourceKey);

        }


        return data;
    }
}
