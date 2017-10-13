package com.ge.current.em.automation.ingestion;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ge.current.em.automation.util.EMTestUtil;
import com.opencsv.CSVWriter;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Created by 212582713 on 01/05/2017.
 */


public class AggregationTest extends EMTestUtil{
	
	private static final String  SEARCH_BY = "/filtersearch?query=resrc_uid:";
	private static final Long FQ_TIME_INTERVAL = (long) (15*60*1000);
	private static final Long TS_HOURLY_OFFSET = (long) (2*60*60*1000);
	private static final Long TS_DAILY_OFFSET = (long) (24*60*60*1000);
	private static final Long TS_MONTHLY_OFFSET = TS_DAILY_OFFSET * 30;
	private static final Long TS_MIN_OFFSET = (long) (15 * 60 * 1000);
	private static String ASSET_TYPE = "";
	
//	String submetertags[] = { "zoneElecMeterEnergySensor", "zoneElecMeterPowerSensor" };
//	String ahutags[] = { "zoneAirTempSensor", "dischargeAirTempSensor" };
	private HashMap<String, String[]> submetertags = new HashMap<String, String[]>();
	{
		submetertags.put("kWh", new String[] {});
		submetertags.put("zoneElecMeterEnergySensor", new String[] {});
		submetertags.put("zoneElecMeterPowerSensor", new String[] {});
		
		submetertags.put("zoneElecMeterApparentPfSensor", new String[] {});
		submetertags.put("zoneElecMeterCurrentSensor", new String[] {});
		submetertags.put("zoneElecMeterDisplacementPfSensor", new String[] {});
		//submetertags.put("zoneElecMeterEnergyNegativeSensor", new String[] {});
		submetertags.put("zoneElecMeterEnergyPositiveSensor", new String[] {});
		submetertags.put("zoneElecMeterVoltSensor", new String[] {});
	}

	private HashMap<String, String[]> ahutags = new HashMap<String, String[]>();
	{
		ahutags.put("zoneAirTempSensor", new String[] {});
		ahutags.put("dischargeAirTempSensor", new String[] {});
		ahutags.put("ahuCoolStage1", new String[] { "on", "off" });
		ahutags.put("ahuHeatStage1", new String[] { "on", "off" });

	}

	DateTime dt = new DateTime();
	URI uri;
	String filterQuery, fromTime, toTime,startTimeNormalized,endTimeNormalized; 
	
	private void setUp(String aggregationType,Long offSet,String asset_uid) {
		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema")
				+ aggregationType
				+ SEARCH_BY 
				+ asset_uid);
		Instant timenow = Instant.now();
		fromTime =  timenow.minus(offSet).minus(FQ_TIME_INTERVAL)
				.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));//"2017-05-23T00:12:25Z";
		toTime = timenow.minus(FQ_TIME_INTERVAL).toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));//"2017-05-23T00:27:25Z";
		//fromTime = "2017-06-02T11:40:27Z";
		//toTime = "2017-06-02T11:55:27Z";
		filterQuery = "event_ts:[" + fromTime + "%20TO%20" + toTime + "]";
	}
	
	@Test
	public void testMetadataInMinAggregationForSoftJace() throws URISyntaxException {
		
		Integer hhmmExpected = ((dt.getMinuteOfHour()/15)-1)*15;
		hhmmExpected =(hhmmExpected < 0) ? (60+hhmmExpected):hhmmExpected;
		
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET,getProperty("em.SoftJace.assetSourcekey"));
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
		
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.SoftJace.assetSourcekey"));
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
		
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.SoftJace.assetSourcekey"));
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
		 
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.SoftJace.assetSourcekey"));
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
		
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Asset.assetSourcekey"));
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
		ASSET_TYPE = "SUBMETER";
		validate("MinAggregation",submetertags);
	}

	@Test
	public void testMetadataInMinAggregationForFE2ESubmeterSite() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Submeter.Site.siteSourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("MinAggregation",submetertags);
	}

	@Test
	public void testMetadataInMinAggregationForFE2ESubmeterEnterprise() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET,
				getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("MinAggregation",submetertags);
	}

	@Test
	public void testMetadataInMinAggregationForFE2ESubmeterSegment() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Submeter.Segment.SourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("MinAggregation",submetertags);
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2ESubmeter() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.Submeter.Asset.assetSourcekey"));
		ASSET_TYPE = "SUBMETER";
		validate("HourlyAggregation",submetertags);
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2ESubmeterSite() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.Submeter.Site.siteSourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("HourlyAggregation",submetertags);
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2ESubmeterEnterprise() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET,
				getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("HourlyAggregation",submetertags);
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2ESubmeterSegment() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.Submeter.Segment.SourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("HourlyAggregation",submetertags);
	}
	@Test
	public void testMetadataInDailyAggregationForFE2ESubmeter() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Submeter.Asset.assetSourcekey"));
		ASSET_TYPE = "SUBMETER";
		validate("DailyAggregation",submetertags);
	}

	@Test
	public void testMetadataInDailyAggregationForFE2ESubmeterSite() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Submeter.Site.siteSourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("DailyAggregation",submetertags);
	}

	@Test
	public void testMetadataInDailyAggregationForFE2ESubmeterEnterprise() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET,
				getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("DailyAggregation",submetertags);
	}

	@Test
	public void testMetadataInDailyAggregationForFE2ESubmeterSegment() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Submeter.Segment.SourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("DailyAggregation",submetertags);
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2ESubmeter() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Submeter.Asset.assetSourcekey"));
		ASSET_TYPE = "SUBMETER";
		validate("MonthlyAggregation",submetertags);
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2ESubmeterSite() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Submeter.Site.siteSourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("MonthlyAggregation",submetertags);
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2ESubmeterEnterprise() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET,
				getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("MonthlyAggregation",submetertags);
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2ESubmeterSegment() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Submeter.Segment.SourceKey"));
		ASSET_TYPE = "SUBMETER";
		validate("MonthlyAggregation",submetertags);
	}

	private void validate(String table, HashMap<String, String[]> tagtypes) {
		if (uri.toString().contains("SITE")) {
			filterQuery += "%20AND%20event_bucket:*SITE.SITE_*";
		} else if (uri.toString().contains("SEGMENT")) {
			filterQuery += "%20AND%20event_bucket:*SEGMENT.SEGMENT*";
		}
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(uri);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> expected = jsonPath.get();
		assertFalse(expected.isEmpty(), " expected data cannot be empty " + "\n" + uri + "&fq=" + filterQuery);
		
		if (!table.equals("MonthlyAggregation")) {
			Assert.assertTrue(
					jsonPath.getString("enterprise_uid")
							.contains(getProperty("em.Submeter.Enterprise.enterpriseSourceKey")),
					getProperty("em.Submeter.Enterprise.enterpriseSourceKey") + "Enterprise_uid doesnot match "
							+ jsonPath.getString("enterprise_uid") + "\n" + uri + "&fq=" + filterQuery);
		}
		String tagName = (String) tagtypes.keySet().toArray()[0];
		if (table.equals("MinAggregation")) {

			assertTrue(tagName + " tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_max" + tagName));
			setUp(getProperty("Normalized"), TS_MIN_OFFSET, expected.get(0).get("resrc_uid").toString());
			try{
				String event_ts = expected.get(0).get("event_ts").toString();
				startTimeNormalized = event_ts;
				DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
				DateFormat df1 = new SimpleDateFormat("yyyyMMddkkmm");
				Date parsedDate = df.parse(event_ts);
				String timebucket =  df1.format(parsedDate.getTime());
				filterQuery = "time_bucket:" + timebucket;
				endTimeNormalized = df.format((new Date(parsedDate.getTime() + TS_MIN_OFFSET)));
				validateWithNormalizedData(expected.get(0),tagtypes);
			}catch (ParseException e) {
				assertTrue("time parser exception occured", false);
			}
		} else if (table.equals("HourlyAggregation")) {
			assertTrue(tagName + " tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_max" + tagName));
			setUp(getProperty("MinAggregation"), TS_HOURLY_OFFSET, expected.get(0).get("resrc_uid").toString());
			String hour = expected.get(0).get("hour").toString();
			String yyyymmdd = expected.get(0).get("yyyymmdd").toString();
			hour = hour.length() == 1 ? "0" + hour : hour;
			filterQuery = "yyyymmdd:" + yyyymmdd + "%20AND%20hhmm:" + hour + "*";
			validateWithLevelUpData(expected.get(0),tagtypes);
		} else if (table.equals("DailyAggregation")) {
			String yyyymmExpected = fromTime.substring(0, 7).replace("-", "");
			String yyyymmActual = jsonPath.getString("yyyymm").replace("[", "").replace("]", "");
			Assert.assertTrue(yyyymmActual.contains(yyyymmExpected),
					yyyymmActual + " yearmonth doesnot match expected:" + yyyymmExpected);
			assertTrue(tagName + " tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_max" + tagName));
			setUp(getProperty("HourlyAggregation"), TS_DAILY_OFFSET, expected.get(0).get("resrc_uid").toString());
			String day = expected.get(0).get("day").toString();
			String yyyymm = expected.get(0).get("yyyymm").toString();
			day = day.length() == 1 ? "0" + day : day;
			filterQuery = "yyyymmdd:" + yyyymm + day;
			validateWithLevelUpData(expected.get(0),tagtypes);
		} else if (table.equals("MonthlyAggregation")) {
			assertTrue(tagName + " tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_max" + tagName));
			setUp(getProperty("DailyAggregation"), TS_MONTHLY_OFFSET, expected.get(0).get("resrc_uid").toString());
			String yyyy = expected.get(0).get("yyyy").toString();
			String mm = expected.get(0).get("month").toString();
			mm = mm.length() == 1 ? "0" + mm : mm;
			filterQuery = "yyyymm:" + yyyy + mm;
			validateWithLevelUpData(expected.get(0),tagtypes);
		}
	}

	private void validateWithLevelUpData(HashMap<String, Object> expectedMap, HashMap<String, String[]> tagtypes) {
		if (uri.toString().contains("SITE")) {
			filterQuery += "%20AND%20event_bucket:*SITE.SITE_*";
		} else if (uri.toString().contains("SEGMENT")) {
			filterQuery += "%20AND%20event_bucket:*SEGMENT.SEGMENT*";
		}
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(uri);
		List<HashMap<String, Object>> res = response.jsonPath().get();
		Set<Map.Entry<String, String[]>> tags = tagtypes.entrySet();
		for (Entry<String, String[]> entry : tags) {
			String tag = (String) entry.getKey();
			if (entry.getValue() != null && uri.toString().contains("ASSET")) {
				String[] enumValues = (String[]) entry.getValue();
				for (String enumValue : enumValues) {
					for (int i = 0; i < 2; i++) {
						if (i == 1) {
							enumValue = enumValue + "_runtime";
						}
						String tagName = tag + "_" + enumValue;
						aggregationValidationLevelUp(tagName, res , expectedMap , true);
					}
				}
			} else {
				aggregationValidationLevelUp(tag , res , expectedMap,false);
			}
		}
	}

	private void validateWithNormalizedData(HashMap<String, Object> expectedMap, HashMap<String, String[]> tagtypes) {
		String url = uri.toString();
		if (url.contains("SITE_")) {
			url = url.replace("resrc_uid", "site_uid");
		} else if (url.contains("ENTERPRISE_")) {
			url = url.replace("resrc_uid", "enterprise_uid");
		} else if (url.contains("SEGMENT_")) {
			url = url.replace("resrc_uid", "segment_uid");
		}
		filterQuery += "%20AND%20asset_type:"+ASSET_TYPE;
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(url);
		List<HashMap<String, Object>> res = response.jsonPath().get();
		Collections.sort(res, new MapComparator("event_ts"));
	//	validateTimeZone(url, res);
		Set<Map.Entry<String, String[]>> tags = tagtypes.entrySet();
		for (Entry<String, String[]> entry : tags) {
			String tag = (String) entry.getKey();
			String[] enumValues = (String[]) entry.getValue();
			if (enumValues.length != 0 && uri.toString().contains("ASSET")) {
				for (String enumValue : enumValues) {
					for (int i = 0; i < 2; i++) {
						if (i == 1) {
							enumValue = enumValue + "_runtime";
						}
						String tagName = tag + "_" + enumValue ;
						aggreagationValidation(tagName, res,expectedMap,true);
					}
				}

			} else {
				aggreagationValidation(tag, res,expectedMap,false);
			}
		}
	}
	
	private long convertTimeToSeconds(String time) throws ParseException {
		DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
		Date parsedDate = df.parse(time);
		return parsedDate.getTime()/1000;
	}
	
	private void aggreagationValidation(String tagValue,List<HashMap<String, Object>> res,HashMap<String, Object> expectedMap,boolean aggrOnly){
		String measures = "measures";
		String tags = "tags";
		Double aggr = 0.0;
		Double min = Double.MAX_VALUE;
		Double max = 0.0;
		Double avg = 0.0;
		Double cnt = 0.0;
		boolean isElementFound = false;
		if(tagValue.contains("_runtime")){
			try {
				aggr = calculateRuntime(tagValue , res);
			} catch (ParseException e) {
				assertTrue("time parser exception occured", false);
			}
			if(aggr.intValue() != 0){
				isElementFound = true;
			}
		}else{
			for (HashMap<String, Object> element : res) {
				//tags+tagname_enumvalue
				if(element.containsKey(tags + tagValue.split("_")[0]) && tagValue.contains(element.get(tags + tagValue.split("_")[0]).toString())){
					aggr = aggr + 1;
					isElementFound = true;
					continue;
				}
				if (!element.containsKey(measures + tagValue)) {
					continue;
				} 
				isElementFound = true;
				Double value = Double.valueOf(element.get(measures + tagValue).toString());
				if(!aggrOnly){
					if (min > value) {
						min = value;
					}
					if (max < value) {
						max = value;
					}
					cnt++;
				}
				aggr = aggr + value;
			}
		}
		if (isElementFound) {
			System.out.println("tagValue - "+tagValue);
			String measures_aggr = "measures_aggr";
			String measures_min = "measures_min";
			String measures_max = "measures_max";
			String measures_cnt = "measures_cnt";
			String measures_avg = "measures_avg";
			assertTrue(
					measures_aggr + tagValue
							+ " value is not matching with data \n expected : "
							+ Double.valueOf(
									expectedMap.get(measures_aggr + tagValue).toString())
							+ " \n actual : " + aggr+ "\n filterquery : "+filterQuery,
					(Math.ceil(Double.valueOf(expectedMap.get(measures_aggr + tagValue).toString()))
							 == Math.ceil(aggr)) || Math.floor(Double.valueOf(expectedMap.get(measures_aggr + tagValue).toString()))
					 == Math.floor(aggr) || Math.round(Double.valueOf(expectedMap.get(measures_aggr + tagValue).toString()))
							 == Math.round(aggr));
			if(!aggrOnly){
				avg = aggr / cnt;
				assertTrue(
						measures_min + tagValue
								+ " value is not matching with data \n expected : "
								+ Double.valueOf(
										expectedMap.get(measures_min + tagValue).toString())
								+ " \n actual : " + min+ "\n filterquery : "+filterQuery,
						Double.valueOf(expectedMap.get(measures_min + tagValue).toString())
								.intValue() == min.intValue());
				assertTrue(
						measures_max + tagValue
								+ " value is not matching with data \n expected : "
								+ Double.valueOf(
										expectedMap.get(measures_max + tagValue).toString())
								+ " \n actual : " + max+ "\n filterquery : "+filterQuery,
						Double.valueOf(expectedMap.get(measures_max + tagValue).toString())
								.intValue() == max.intValue());
				assertTrue(
						measures_avg + tagValue
								+ " value is not matching with data \n\n available value : " + avg
								+ "\n expected value:"
								+ Double.valueOf(
										expectedMap.get(measures_avg + tagValue).toString())+ "\n filterquery : "+filterQuery,
						Double.valueOf(expectedMap.get(measures_avg + tagValue).toString())
								.intValue() == avg.intValue());
				assertTrue(
						measures_cnt + tagValue
								+ " value is not matching with data \n expected : "
								+ Double.valueOf(
										expectedMap.get(measures_cnt + tagValue).toString())
								+ " \n actual : " + cnt+ "\n filterquery : "+filterQuery,
						Double.valueOf(expectedMap.get(measures_cnt + tagValue).toString())
								.intValue() == cnt.intValue());
			}
		}

	}
		
	private Double calculateRuntime(String tagValue, List<HashMap<String, Object>> res) throws ParseException {
		String tagV[] = tagValue.split("_");
		String tag = "tags"+tagV[0];
		String enumV = tagV[1];
		String prevTS = "";
		String prevEnum = "";
		Double duration = 0.0;
		int index = 0;
		boolean elementFound = true;
		for (HashMap<String, Object> element : res) {
			if(!element.containsKey(tag)){
				elementFound = false;
				break;
			}
			if(index == 0){
				index++;
				if(element.get(tag).toString().equals(enumV)){
					duration = duration + (convertTimeToSeconds(element.get("event_ts").toString()) - convertTimeToSeconds(startTimeNormalized));
				}
				prevTS = element.get("event_ts").toString();
				prevEnum = element.get(tag).toString();
				continue;
			}else{
				if(prevEnum.equals(enumV))
				{
					duration = duration + (convertTimeToSeconds(element.get("event_ts").toString()) - convertTimeToSeconds(prevTS));
				}
			}
			prevTS = element.get("event_ts").toString();
			prevEnum = element.get(tag).toString();
			index++;
		}
		if(elementFound && prevEnum.equals(enumV))
		{
			duration = duration + (convertTimeToSeconds(endTimeNormalized) - convertTimeToSeconds(prevTS));
		}
		return duration;
	}

	private void aggregationValidationLevelUp(String tag,List<HashMap<String, Object>> res,HashMap<String, Object> expectedMap,boolean aggrOnly){
			String measures_aggr = "measures_aggr";
			String measures_min = "measures_min";
			String measures_max = "measures_max";
			String measures_cnt = "measures_cnt";
			String measures_avg = "measures_avg";
			Double aggr = 0.0;
			Double min = Double.MAX_VALUE;
			Double max = 0.0;
			Double avg = 0.0;
			Double cnt = 0.0;
			Boolean isElementFound = false;
			for (HashMap<String, Object> element : res) {
				if (!element.containsKey(measures_aggr + tag)) {
					continue;
				}
				isElementFound = true;
				aggr = aggr + Double.valueOf(element.get(measures_aggr + tag).toString());
				if(!aggrOnly){
					if (min > Double.valueOf(element.get(measures_min + tag).toString())) {
						min = Double.valueOf(element.get(measures_min + tag).toString());
					}
					if (max < Double.valueOf(element.get(measures_max + tag).toString())) {
						max = Double.valueOf(element.get(measures_max + tag).toString());
					}
					cnt = cnt + Double.valueOf(element.get(measures_cnt + tag).toString());
				}
			}
			if (cnt != 0) {
				avg = aggr / cnt;
			}
			if (isElementFound) {
				//System.out.println("tag - "+tag);
				assertTrue(measures_aggr + tag, expectedMap.get(measures_aggr + tag) != null);
				assertTrue(measures_aggr + tag + " value is not matching with Daily data \n expected : "
						+ Double.valueOf(expectedMap.get(measures_aggr + tag).toString()) + " \n actual : " + aggr,
						Double.valueOf(expectedMap.get(measures_aggr + tag).toString()).intValue() == aggr
								.intValue());
				if(!aggrOnly){
					assertTrue(measures_min + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_min + tag).toString()) + " \n actual : " + min,
							Double.valueOf(expectedMap.get(measures_min + tag).toString()).equals(min));
					assertTrue(measures_max + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_max + tag).toString()) + " \n actual : " + max,
							Double.valueOf(expectedMap.get(measures_max + tag).toString()).equals(max));
					assertTrue(
							measures_avg + tag + " value is not matching with Daily data \n\n available value : " + avg
									+ "\n expected value:"
									+ Double.valueOf(expectedMap.get(measures_avg + tag).toString()),
							Double.valueOf(expectedMap.get(measures_avg + tag).toString()).intValue() == avg
									.intValue());
					assertTrue(measures_cnt + tag + " value is not matching with Daily data \n expected : "
							+ Double.valueOf(expectedMap.get(measures_cnt + tag).toString()) + " \n actual : " + cnt,
							Double.valueOf(expectedMap.get(measures_cnt + tag).toString()).equals(cnt));
				}
			}
		
		}
			
		private void validateTimeZone(String url, List<HashMap<String, Object>> res) {
		String apmUrl = "";
		Response apmResponse;
		String authToken = "Bearer " + getAPMServiceToken();
		String normalizedtimezone = res.get(0).get("event_ts_tz").toString();
		if (url.contains("SITE_")) {
			apmUrl = getProperty("apm_service_url") + "/sites/" + getProperty("em.Submeter.Site.siteSourceKey")
					+ "/locations";
			apmResponse = RestAssured.given().contentType("application/json").when().header("Authorization", authToken)
					.get(apmUrl).then().extract().response();
			assertEquals(HttpStatus.SC_OK, apmResponse.statusCode());
			validateTime(apmResponse, "SITE", normalizedtimezone);
		} else if (url.contains("ENTERPRISE_")) {
			apmUrl = getProperty("apm_service_url") + "/enterprises/"
					+ getProperty("em.Submeter.Enterprise.enterpriseSourceKey") + "/locations";
			apmResponse = RestAssured.given().contentType("application/json").when().header("Authorization", authToken)
					.get(apmUrl).then().extract().response();
			assertEquals(HttpStatus.SC_OK, apmResponse.statusCode());
			validateTime(apmResponse, "ENTERPRISE", normalizedtimezone);
		} else if (url.contains("SEGMENT_")) {
			apmUrl = getProperty("apm_service_url") + "/segments/" + getProperty("em.Submeter.Segment.SourceKey")
					+ "/site";
			apmResponse = RestAssured.given().contentType("application/json").when().header("Authorization", authToken)
					.get(apmUrl).then().extract().response();
			assertEquals(HttpStatus.SC_OK, apmResponse.statusCode());
			validateTime(apmResponse, "SEGMENT", normalizedtimezone);
		} else if (url.contains("ASSET_")) {
			apmUrl = getProperty("apm_service_url") + "/assets/" + getProperty("em.Submeter.Asset.assetSourcekey")
					+ "/site";
			apmResponse = RestAssured.given().contentType("application/json").when().header("Authorization", authToken)
					.get(apmUrl).then().extract().response();
			assertEquals(HttpStatus.SC_OK, apmResponse.statusCode());
			validateTime(apmResponse, "ASSET", normalizedtimezone);
		}

	}

	private void validateTime(Response response, String assetName, String normalizedtimezone) {

		String timeZone = "";
		if (assetName.equals("SITE") || assetName.equals("ENTERPRISE")) {
			List<HashMap<String, Object>> apmResp = response.jsonPath().get();
			HashMap<String, String> geoCoordn = (HashMap<String, String>) apmResp.get(0).get("geoCoordinates");
			timeZone = geoCoordn.get("timezone");
		} else if (assetName.equals("ASSET") || assetName.equals("SEGMENT")) {
			HashMap<String, Object> apmResp = response.jsonPath().get();
			List<HashMap<String, Object>> locations = (List<HashMap<String, Object>>) apmResp.get("locations");
			HashMap<String, String> geoCoordn = (HashMap<String, String>) locations.get(0).get("geoCoordinates");
			timeZone = geoCoordn.get("timezone");
		}
		assertTrue(
				"Normalized data time zone : " + normalizedtimezone + " not matched with APM time zone : " + timeZone,
				timeZone.equals(normalizedtimezone));
	}

	@Test
	public void testMetadataInMinAggregationForFE2EAHU() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Asset.assetSourcekey"));
		ASSET_TYPE = "AHU";
		validate("MinAggregation",ahutags);
	}

	@Test
	public void testMetadataInMinAggregationForFE2EAHUSite() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Site.siteSourceKey"));
		ASSET_TYPE = "AHU";
		validate("MinAggregation",ahutags);
	}

	@Test
	public void testMetadataInMinAggregationForFE2EAHUEnterprise() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET,
				getProperty("em.Enterprise.enterpriseSourceKey"));
		ASSET_TYPE = "AHU";
		validate("MinAggregation",ahutags);
		}

	@Test
	public void testMetadataInMinAggregationForFE2EAHUSegment() throws URISyntaxException {
		setUp(getProperty("MinAggregation"), TS_MIN_OFFSET, getProperty("em.Segment.SegmentSourceKey"));
		ASSET_TYPE = "AHU";
		validate("MinAggregation",ahutags);
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2EAHU() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.Asset.assetSourcekey"));
		ASSET_TYPE = "AHU";
		validate("HourlyAggregation",ahutags);
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2EAHUSite() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.Site.siteSourceKey"));
		ASSET_TYPE = "AHU";
		validate("HourlyAggregation",ahutags);
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2EAHUEnterprise() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET,
				getProperty("em.Enterprise.enterpriseSourceKey"));
		ASSET_TYPE = "AHU";
		validate("HourlyAggregation",ahutags);
	}

	@Test
	public void testMetadataInHourlyAggregationForFE2EAHUSegment() throws URISyntaxException {
		setUp(getProperty("HourlyAggregation"), TS_HOURLY_OFFSET, getProperty("em.Segment.SegmentSourceKey"));
		ASSET_TYPE = "AHU";
		validate("HourlyAggregation",ahutags);
	}

	@Test
	public void testMetadataInDailyAggregationForFE2EAHU() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Asset.assetSourcekey"));
		ASSET_TYPE = "AHU";
		validate("DailyAggregation",ahutags);
	}

	@Test
	public void testMetadataInDailyAggregationForFE2EAHUSite() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Site.siteSourceKey"));
		ASSET_TYPE = "AHU";
		validate("DailyAggregation",ahutags);
	}

	@Test
	public void testMetadataInDailyAggregationForFE2EAHUEnterprise() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET,
				getProperty("em.Enterprise.enterpriseSourceKey"));
		ASSET_TYPE = "AHU";
		validate("DailyAggregation",ahutags);
	}

	@Test
	public void testMetadataInDailyAggregationForFE2EAHUSegment() throws URISyntaxException {
		setUp(getProperty("DailyAggregation"), TS_DAILY_OFFSET, getProperty("em.Segment.SegmentSourceKey"));
		ASSET_TYPE = "AHU";
		validate("DailyAggregation",ahutags);
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2EAHU() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Asset.assetSourcekey"));
		ASSET_TYPE = "AHU";
		validate("MonthlyAggregation",ahutags);
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2EAHUSite() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Site.siteSourceKey"));
		ASSET_TYPE = "AHU";
		validate("MonthlyAggregation",ahutags);
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2EAHUEnterprise() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Enterprise.enterpriseSourceKey"));
		ASSET_TYPE = "AHU";
		validate("MonthlyAggregation",ahutags);
	}

	@Test
	public void testMetadataInMonthlyAggregationForFE2EAHUSegment() throws URISyntaxException {
		setUp(getProperty("MonthlyAggregation"), TS_MONTHLY_OFFSET, getProperty("em.Segment.SegmentSourceKey"));
		ASSET_TYPE = "AHU";
		validate("MonthlyAggregation",ahutags);
	}
	@Test
	public void testSiteHourlyWithMinutes(){
		String siteSourceKey = getProperty("em.aggregation.siteSourceKey");
		String yyyymmddIn = getProperty("em.aggregation.yyyymmdd");
		setUp(getProperty("HourlyAggregation"),TS_HOURLY_OFFSET,siteSourceKey);
		filterQuery = "yyyymmdd:"+yyyymmddIn + "%20AND%20event_bucket:*SITE.SITE_*";;
		ASSET_TYPE = "SUBMETER";
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(uri);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> expected = jsonPath.get();
		//String tagName = (String) submetertags.keySet().toArray()[0];
		for(HashMap<String, Object> hourly : expected){
			//assertTrue(tagName + " tag data not available  \n " + uri + "&fq=" + filterQuery,
				//	hourly.containsKey("measures_max" + tagName));
			setUp(getProperty("MinAggregation"), TS_HOURLY_OFFSET, hourly.get("resrc_uid").toString());
			String hour = hourly.get("hour").toString();
			String yyyymmdd = hourly.get("yyyymmdd").toString();
			hour = hour.length() == 1 ? "0" + hour : hour;
			if(yyyymmdd.equals("20170605") || yyyymmdd.equals("20170616")){
				continue;
			}
			filterQuery = "yyyymmdd:" + yyyymmdd + "%20AND%20hhmm:" + hour + "*";
			validateWithLevelUpData(hourly,submetertags);
			String[] content = {siteSourceKey,yyyymmdd,hour,"hourly with minutes"};
			try {
				writeToCSV(content,yyyymmdd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testSiteMinutesWithNorm(){
		String siteSourceKey = getProperty("em.aggregation.siteSourceKey");
		String yyyymmddIn = getProperty("em.aggregation.yyyymmdd");
		setUp(getProperty("MinAggregation"),TS_MIN_OFFSET,siteSourceKey);
		filterQuery = "yyyymmdd:"+yyyymmddIn + "%20AND%20event_bucket:*SITE.SITE_*";;
		ASSET_TYPE = "SUBMETER";
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(uri);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> expected = jsonPath.get();
		//String tagName = (String) submetertags.keySet().toArray()[0];
		for(HashMap<String, Object> minute : expected){
			//assertTrue(tagName + " tag data not available  \n " + uri + "&fq=" + filterQuery,
					//expected.get(0).containsKey("measures_max" + tagName));
			setUp(getProperty("Normalized"), TS_MIN_OFFSET, minute.get("resrc_uid").toString());
			try{
				String event_ts = minute.get("event_ts").toString();
				DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
				DateFormat df1 = new SimpleDateFormat("yyyyMMddkkmm");
				Date parsedDate = df.parse(event_ts);
				String timebucket =  df1.format(parsedDate.getTime());
				filterQuery = "time_bucket:" + timebucket;
				validateWithNormalizedData(minute,submetertags);
			}catch (ParseException e) {
				assertTrue("time parser exception occured", false);
			}
			String yyyymmdd = minute.get("yyyymmdd").toString();
			String hhmm = minute.get("hhmm").toString();
			String[] content = {siteSourceKey,yyyymmdd,hhmm,"minutes with normalized"};
			try {
				writeToCSV(content,"_norm_"+yyyymmdd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testAssetMinutesWithNorm(){
		for(int i=22;i<=22;i++){
		String siteSourceKey = getProperty("em.aggregation.assetSourceKey");
		String yyyymmddIn = getProperty("em.aggregation.yyyymmdd");
		setUp(getProperty("MinAggregation"),TS_MIN_OFFSET,siteSourceKey);
		if(i<10){
			filterQuery = "yyyymmdd:"+yyyymmddIn +"0"+ i ;
		}else{
			filterQuery = "yyyymmdd:"+yyyymmddIn + i ;
		}
		ASSET_TYPE = "SUBMETER";
		Response response = given().header("Authorization", "Bearer " + getSolrServiceToken()).param("fq", filterQuery)
				.urlEncodingEnabled(false).when().get(uri);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> expected = jsonPath.get();
		String tagName = (String) submetertags.keySet().toArray()[0];
		for(HashMap<String, Object> minute : expected){
			assertTrue(tagName + " tag data not available  \n " + uri + "&fq=" + filterQuery,
					expected.get(0).containsKey("measures_max" + tagName));
			setUp(getProperty("Normalized"), TS_MIN_OFFSET, minute.get("resrc_uid").toString());
			try{
				String event_ts = minute.get("event_ts").toString();
				DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
				DateFormat df1 = new SimpleDateFormat("yyyyMMddkkmm");
				Date parsedDate = df.parse(event_ts);
				String timebucket =  df1.format(parsedDate.getTime());
				//timebucket = minute.get("yyyymmdd").toString() + minute.get("hhmm").toString();
				filterQuery = "time_bucket:" + timebucket;
				validateWithNormalizedData(minute,submetertags);
			}catch (ParseException e) {
				assertTrue("time parser exception occured", false);
			}
			String yyyymmdd = minute.get("yyyymmdd").toString();
			String hhmm = minute.get("hhmm").toString();
			String[] content = {siteSourceKey,yyyymmdd,hhmm,"minutes with normalized"};
			try {
				writeToCSV(content,"_norm_"+yyyymmdd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}
	}
	
	private void writeToCSV(String[] content,String yyyymmdd) throws IOException {
		String[] rowData = content;
		CSVWriter csvWriter = new CSVWriter(new FileWriter("src/main/resources/output_min"+yyyymmdd+".csv", true),
				',', CSVWriter.NO_QUOTE_CHARACTER);
		csvWriter.writeNext(rowData);
		csvWriter.close();

	}

}

class MapComparator implements Comparator<Map<String, Object>>
{
    private final String key;

    public MapComparator(String key)
    {
        this.key = key;
    }

    public int compare(Map<String, Object> first,
                       Map<String, Object> second)
    {
        String firstValue = first.get(key).toString();
        String secondValue = second.get(key).toString();
        return firstValue.compareTo(secondValue);
    }
}