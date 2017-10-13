package com.ge.current.em.automation.functionalE2E;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ge.current.em.automation.ingestion.IngestionTest;
import com.ge.current.em.automation.util.EMTestUtil;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

/**
 * Created by 212582713 on 01/05/2017.
 */


public class EndToEndAlarmTest extends IngestionTest{
	
	private static final String  SEARCH_BY = "/filtersearch?query=resrc_uid:";
	private static final Long FQ_TIME_INTERVAL = (long) (15*60*1000);
	private static final Long TS_HOURLY_OFFSET = (long) (60*60*1000);
	private static final Long TS_DAILY_OFFSET = (long) (24*60*60*1000);
	private static final Long TS_MONTHLY_OFFSET = (long) (30*24*60*60*1000);
	private static final Long TS_MIN_OFFSET = (long) (15*60*1000);
	
	DateTime dt = new DateTime();
	URI uri;
	String filterQuery, fromTime, toTime; 
	
	
	private void setUp(String aggregationType,Long offSet,String asset_uid) {
		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema")
				+ aggregationType
				+ SEARCH_BY 
				+ asset_uid);
		fromTime = Instant.now().minus(FQ_TIME_INTERVAL).minus(offSet).toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		toTime = Instant.now().minus(offSet).toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		filterQuery = "event_ts:["+fromTime+"%20TO%20"+toTime+"]";
	}
	
	@Test
	public void testDataInNormalization() throws URISyntaxException {
		
		Integer hhmmExpected = ((dt.getMinuteOfHour()/15)-1)*15;
		hhmmExpected =(hhmmExpected < 0) ? (60+hhmmExpected):hhmmExpected;
		
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.SoftJace.assetSourcekey"));
		Response  response =	given().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
		JsonPath jsonPath = response.jsonPath();
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(jsonPath.getString("enterprise_uid").contains("ENTERPRISE_4951b25b-3616-30e8-a85d-9239593b35fd"),"Enterprise_uid doesnot match");
		Assert.assertTrue(jsonPath.getString("hhmm").replace("[", "").replace("]", "").equals(String.format("%02d", hhmmExpected)), "hhmm doesnot match");
		//Site name will not be stored as part of tuple.
		//Assert.assertEquals(jsonPath.getString("site_name"),"SunSetSite","Site name SunSetSite doesnot match");
		
	}
	
	@Test
	public void testMaxMinAvgCountInMinAggregationForSoftJace() throws URISyntaxException {
		
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET,getProperty("em.SoftJace.assetSourcekey"));
		Response  response =	given().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								urlEncodingEnabled(false).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
	/*	Assert.assertTrue(Double.parseDouble(jsonPath.getString("measures_maxzoneElecMeterEnergySensor").replace("[", "").replace("]", "")) > 0,"measureskWh < 0 ");
		Assert.assertEquals(Double.parseDouble(jsonPath.getString("measures_cntkWh").replace("[", "").replace("]", "")), 3.0, "measureszoneElecMeterEnergySensor =3");
		*/
		Assert.assertEquals(Double.parseDouble(jsonPath.getString("measures_cntzoneElecMeterEnergySensor").replace("[", "").replace("]", "")),3.0,"measures_cntzoneElecMeterEnergySensor =3");
		Assert.assertEquals(Double.parseDouble(jsonPath.getString("measures_cntzoneElecMeterPowerSensor").replace("[", "").replace("]", "")), 3.0,"measures_cntzoneElecMeterPowerSensor =3");
	}
	
	@Test
	public void testMetadataInHourlyAggregationForSoftJace() throws URISyntaxException {
		
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET,getProperty("em.SoftJace.assetSourcekey"));
		Response  response =	given().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								urlEncodingEnabled(false).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(jsonPath.getString("enterprise_uid").contains("ENTERPRISE_4951b25b-3616-30e8-a85d-9239593b35fd"),"Enterprise_uid doesnot match");
		Assert.assertEquals(jsonPath.getString("hour").replace("[", "").replace("]", ""),Integer.toString(dt.getHourOfDay()-1), "hour doesnot match");
		Assert.assertEquals(jsonPath.getString("resrc_type").replace("[", "").replace("]", ""),"ASSET","Rescr_type doesnot match");
		
	}
	
	@Test
	public void testMaxMinAvgCountInHourlyAggregationForSoftJace() throws URISyntaxException {
		 
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET,getProperty("em.SoftJace.assetSourcekey"));
		Response  response =	given().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								urlEncodingEnabled(false).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertEquals(Double.parseDouble(jsonPath.getString("measures_cntkWh").replace("[", "").replace("]", "")),12.0,"measures_cntkWh is not 12");
		Assert.assertEquals(Double.parseDouble(jsonPath.getString("measures_cntzoneElecMeterEnergySensor").replace("[", "").replace("]", "")),12.0, "measures_cntzoneElecMeterEnergySensor is not 12");
		Assert.assertEquals(Double.parseDouble(jsonPath.getString("measures_cntzoneElecMeterPowerSensor").replace("[", "").replace("]", "")),12.0, "measures_cntzoneElecMeterPowerSensor is not 12");
		}
	
	@Test
	public void testMetadataInMinAggregationForFE2E() throws URISyntaxException {
		
		Integer mmExpected = ((dt.getMinuteOfHour()/15)-1)*15;
		mmExpected =(mmExpected < 0) ? (60+mmExpected):mmExpected;
		Integer hhExpected = dt.getHourOfDay();
		String hhmmExpected = hhExpected.toString() + String.format("%02d", mmExpected);
		
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET,getProperty("em.Asset.assetSourcekey"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								urlEncodingEnabled(false).
								when().
								get(uri);
		JsonPath jsonPath = response.jsonPath();
		String hhmmActual = jsonPath.getString("hhmm").replace("[", "").replace("]", "");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(jsonPath.getString("enterprise_uid").contains("ENTERPRISE_df1cdfe9-10dc-363c-a442-4b0cab4e8781"),"Enterprise_uid doesnot match");
		Assert.assertTrue(hhmmActual.equals(hhmmExpected), hhmmActual+" hhmm doesnot match expected:" +mmExpected);
		//Site name will not be stored as part of tuple.
		//Assert.assertEquals(jsonPath.getString("site_name"),"SunSetSite","Site name SunSetSite doesnot match");
		
	}
	
	@Test
	public void testMetadataInMinAggregationForFE2ESubmeter() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Submeter.Asset.assetSourcekey"));
		validateSubmeter("MinAggregation");
	}

	@Test
	public void testMetadataInMinAggregationForFE2ESubmeterSite() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Submeter.Site.siteSourceKey"));
		validateSubmeter("MinAggregation");
	}

	@Test
	public void testMetadataInMinAggregationForFE2ESubmeterEnterprise() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET,
				getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		validateSubmeter("MinAggregation");
	}

	@Test
	public void testMetadataInMinAggregationForFE2ESubmeterSegment() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Submeter.Segment.SourceKey"));
		validateSubmeter("MinAggregation");
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2ESubmeter() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_MIN_OFFSET, getProperty("em.Submeter.Asset.assetSourcekey"));
		validateSubmeter("HourlyAggregation");
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2ESubmeterSite() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.Submeter.Site.siteSourceKey"));
		filterQuery = filterQuery + "%20AND%20event_bucket:*SITE.SITE_*";
		validateSubmeter("HourlyAggregation");
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2ESubmeterEnterprise() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET,
				getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		validateSubmeter("HourlyAggregation");
	}
	
	@Test
	public void testMetadataInHourlyAggregationForFE2ESubmeterSegment() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.Submeter.Segment.SourceKey"));
		validateSubmeter("HourlyAggregation");
	}
	@Test
	public void testMetadataInDailyAggregationForFE2ESubmeter() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Submeter.Asset.assetSourcekey"));
		validateSubmeter("DailyAggregation");
	}

	@Test
	public void testMetadataInDailyAggregationForFE2ESubmeterSite() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Submeter.Site.siteSourceKey"));
		filterQuery = filterQuery + "%20AND%20event_bucket:*SITE.SITE_*";
		validateSubmeter("DailyAggregation");
	}

	@Test
	public void testMetadataInDailyAggregationForFE2ESubmeterEnterprise() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET,
				getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		validateSubmeter("DailyAggregation");
	}

	@Test
	public void testMetadataInDailyAggregationForFE2ESubmeterSegment() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Submeter.Segment.SourceKey"));
		validateSubmeter("DailyAggregation");
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2ESubmeter() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Submeter.Asset.assetSourcekey"));
		validateSubmeter("MonthlyAggregation");
	}
	
	@Test
	public void testMetadataInMonthlyAggregationForFE2ESubmeterSite() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Submeter.Site.siteSourceKey"));
		filterQuery = filterQuery + "%20AND%20event_bucket:*SITE.SITE_*";
		validateSubmeter("MonthlyAggregation");
	}
	
	@Test
	public void testMetadataInMonthlyAggregationForFE2ESubmeterEnterprise() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET,
				getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		validateSubmeter("MonthlyAggregation");
	}
	
	@Test
	public void testMetadataInMonthlyAggregationForFE2ESubmeterSegment() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Submeter.Segment.SourceKey"));
		validateSubmeter("MonthlyAggregation");
	}
	
	private void validateSubmeter(String table) {
		Integer mmExpected = (((dt.getMinuteOfHour()) / 15) - 1) * 15;
		mmExpected = (mmExpected < 0) ? (60 + mmExpected) : mmExpected;
		Integer hhExpected = dt.getHourOfDay();
		String hhmmExpected = hhExpected.toString() + String.format("%02d", mmExpected);
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(uri);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		JsonPath jsonPath = response.jsonPath();
		if (!table.equals("MonthlyAggregation")) {
			Assert.assertTrue(
					jsonPath.getString("enterprise_uid")
							.contains(getProperty("em.Submeter.Enterprise.enterpriseSourceKey")),
					getProperty("em.Submeter.Enterprise.enterpriseSourceKey") + "Enterprise_uid doesnot match "
							+ jsonPath.getString("enterprise_uid") + "\n" + uri + "&fq=" + filterQuery);
		}
		if (table.equals("MinAggregation")) {
			String hhmmActual = jsonPath.getString("hhmm").replace("[", "").replace("]", "");
			Assert.assertTrue(hhmmActual.equals(hhmmExpected),
					hhmmActual + " hhmm doesnot match expected:" + hhmmExpected);
			List<HashMap<String, Object>> expected = jsonPath.get();
			assertFalse(expected.isEmpty(), " expected data cannot be empty ");
			assertTrue("zoneElecMeterEnergySensor tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_aggrzoneElecMeterEnergySensor"));
			setUp(getProperty("Normalized"), TS_MIN_OFFSET, expected.get(0).get("resrc_uid").toString());
			try{
				String event_ts = expected.get(0).get("event_ts").toString();
				DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
				DateFormat df1 = new SimpleDateFormat("yyyyMMddkkmm");
				Date parsedDate = df.parse(event_ts);
				String timebucket =  df1.format(parsedDate.getTime());
				filterQuery = "time_bucket:" + timebucket;
				validateWithNormalizedData(expected.get(0));
			}catch (ParseException e) {
				assertTrue("time parser exception occured", false);
			}
		} else if (table.equals("HourlyAggregation")) {
			String hhActual = jsonPath.getString("hour").replace("[", "").replace("]", "");
			Assert.assertTrue(hhActual.equals(hhmmExpected), hhActual + " hour doesnot match expected:" + hhExpected);
			List<HashMap<String,Object>> expected = jsonPath.get();
			assertFalse(expected.isEmpty() , " expected data cannot be empty ");
			assertTrue("zoneElecMeterEnergySensor tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_aggrzoneElecMeterEnergySensor"));
			setUp(getProperty("MinAggregation"), TS_HOURLY_OFFSET, expected.get(0).get("resrc_uid").toString());
			String hour = expected.get(0).get("hour").toString();
			String yyyymmdd = expected.get(0).get("yyyymmdd").toString();
			hour = hour.length() == 1 ? "0" + hour : hour;
			filterQuery ="yyyymmdd:"+yyyymmdd+"%20AND%20hhmm:"+hour+"*";
			validateWithLevelUpData(expected.get(0));
		}else if (table.equals("DailyAggregation")){
			String yyyymmExpected = fromTime.substring(0, 7).replace("-", "");
			String yyyymmActual = jsonPath.getString("yyyymm").replace("[", "").replace("]", "");
			Assert.assertTrue(yyyymmActual.contains(yyyymmExpected),
					yyyymmActual + " yearmonth doesnot match expected:" + yyyymmExpected);
			List<HashMap<String, Object>> expected = jsonPath.get();
			assertFalse(expected.isEmpty(), " expected data cannot be empty ");
			assertTrue("zoneElecMeterEnergySensor tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_aggrzoneElecMeterEnergySensor"));
			setUp(getProperty("HourlyAggregation"), TS_DAILY_OFFSET, expected.get(0).get("resrc_uid").toString());
			String day = expected.get(0).get("day").toString();
			String yyyymm = expected.get(0).get("yyyymm").toString();
			day = day.length() == 1 ? "0" + day : day;
			filterQuery ="yyyymmdd:"+yyyymm+day;
			//filterQuery="yyyymmdd:20170309";
			validateWithLevelUpData(expected.get(0));
		}else if (table.equals("MonthlyAggregation")){
			List<HashMap<String,Object>> expected = jsonPath.get();
			assertFalse(expected.isEmpty() , " expected data cannot be empty ");
			assertTrue("zoneElecMeterEnergySensor tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_aggrzoneElecMeterEnergySensor"));
			setUp(getProperty("DailyAggregation"), TS_MONTHLY_OFFSET, expected.get(0).get("resrc_uid").toString());
			String yyyy = expected.get(0).get("yyyy").toString();
			String mm = expected.get(0).get("month").toString();
			mm = mm.length()==1?"0"+mm:mm;
			filterQuery ="yyyymm:"+yyyy+mm;
			validateWithLevelUpData(expected.get(0));
		}
	}

	private void validateWithLevelUpData(HashMap<String, Object> expectedMap) {
		if (uri.toString().contains("SITE")) {
			filterQuery += "%20AND%20event_bucket:*SITE.SITE_*";
		} else if (uri.toString().contains("SEGMENT")) {
			filterQuery += "%20AND%20event_bucket:*SEGMENT.SEGMENT*";
		}
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(uri);
		List<HashMap<String,Object>> res = response.jsonPath().get();
		String measures_aggr = "measures_aggr";
		String measures_min = "measures_min";
		String measures_max = "measures_max";
		String measures_cnt = "measures_cnt";
		String measures_avg = "measures_avg";
		String tags[] = {"zoneElecMeterEnergySensor","zoneElecMeterPowerSensor","zoneElecMeterCurrentSensor"};
		for(String tag:tags){
			Double aggr = 0.0;
			Double min = Double.MAX_VALUE;
			Double max = 0.0;
			Double avg = 0.0;
			Double cnt = 0.0;
			for (HashMap<String, Object> element : res) {
				if (!element.containsKey(measures_aggr + tag)) {
					continue;
				}
				aggr = aggr + Double.valueOf(element.get(measures_aggr + tag).toString());
				if (min > Double.valueOf(element.get(measures_min + tag).toString())) {
					min = Double.valueOf(element.get(measures_min + tag).toString());
				}
				if (max < Double.valueOf(element.get(measures_max + tag).toString())) {
					max = Double.valueOf(element.get(measures_max + tag).toString());
				}
				cnt = cnt + Double.valueOf(element.get(measures_cnt + tag).toString());
			}
			if (cnt != 0) {
				avg = aggr / cnt;
			}
			assertTrue(
					measures_aggr + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_aggr + tag).toString()) + " \n actual : " + aggr,
					Double.valueOf(expectedMap.get(measures_aggr + tag).toString()).intValue() == aggr.intValue());
			assertTrue(
					measures_min + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_min + tag).toString()) + " \n actual : " + min,
					Double.valueOf(expectedMap.get(measures_min + tag).toString()).equals(min));
			assertTrue(
					measures_max + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_max + tag).toString()) + " \n actual : " + max,
					Double.valueOf(expectedMap.get(measures_max + tag).toString()).equals(max));
			assertTrue(
					measures_avg + tag + " value is not matching with Daily data \n\n available value : " + avg
							+ "\n expected value:" + Double.valueOf(expectedMap.get(measures_avg + tag).toString()),
					Double.valueOf(expectedMap.get(measures_avg + tag).toString()).intValue() == avg.intValue());
			assertTrue(
					measures_cnt + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_cnt + tag).toString()) + " \n actual : " + cnt,
					Double.valueOf(expectedMap.get(measures_cnt + tag).toString()).equals(cnt));
		}
	}

	private void validateWithNormalizedData(HashMap<String, Object> expectedMap) {
		String url = uri.toString();
		if (url.contains("SITE_")) {
			url = url.replace("resrc_uid", "site_uid");
		} else if (url.contains("ENTERPRISE_")) {
			url = url.replace("resrc_uid", "enterprise_uid");
		} else if (url.contains("SEGMENT_")) {
			url = url.replace("resrc_uid", "segment_uid");
		}
		filterQuery += "%20AND%20asset_type:SUBMETER";
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(url);
		List<HashMap<String, Object>> res = response.jsonPath().get();
		String measures = "measures";
		String tags[] = { "zoneElecMeterEnergySensor", "zoneElecMeterPowerSensor" };
		for (String tag : tags) {
			Double aggr = 0.0;
			Double min = Double.MAX_VALUE;
			Double max = 0.0;
			Double avg = 0.0;
			Double cnt = 0.0;
			for (HashMap<String, Object> element : res) {
				if (!element.containsKey(measures + tag)) {
					continue;
				}
				Double value = Double.valueOf(element.get(measures + tag).toString());
				if (min > value) {
					min = value;
				}
				if (max < value) {
					max = value;
				}
				cnt++;
				aggr = aggr + value;
			}
			avg = aggr / cnt;
			String measures_aggr = "measures_aggr";
			String measures_min = "measures_min";
			String measures_max = "measures_max";
			String measures_cnt = "measures_cnt";
			String measures_avg = "measures_avg";
			assertTrue(
					measures_aggr + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_aggr + tag).toString()) + " \n actual : " + aggr,
					Double.valueOf(expectedMap.get(measures_aggr + tag).toString()).intValue() == aggr.intValue());
			assertTrue(
					measures_min + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_min + tag).toString()) + " \n actual : " + min,
					Double.valueOf(expectedMap.get(measures_min + tag).toString()).equals(min));
			assertTrue(
					measures_max + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_max + tag).toString()) + " \n actual : " + max,
					Double.valueOf(expectedMap.get(measures_max + tag).toString()).equals(max));
			assertTrue(
					measures_avg + tag + " value is not matching with Daily data \n\n available value : " + avg
							+ "\n expected value:" + Double.valueOf(expectedMap.get(measures_avg + tag).toString()),
					Double.valueOf(expectedMap.get(measures_avg + tag).toString()).intValue() == avg.intValue());
			assertTrue(
					measures_cnt + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_cnt + tag).toString()) + " \n actual : " + cnt,
					Double.valueOf(expectedMap.get(measures_cnt + tag).toString()).equals(cnt));
		}
	}
}
