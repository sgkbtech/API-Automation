package com.ge.current.em.automation.ingestion;

import com.ge.current.em.automation.util.EMTestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Created by 212582713 on 01/05/2017.
 */


public class NormalizationTest extends EMTestUtil{
	
	private static final String  SEARCH_BY = "/filtersearch?query=resrc_uid:";
	
	URI uri;
	String filterQuery, fromTime, toTime; 
	String filterQueryHour,fromTimeHour ;
	boolean isHour = false;
	
	private void setUp(String assetId) {
		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema")
				+ getProperty("Normalized")
				+ SEARCH_BY 
				+ assetId);
		
		fromTime = Instant.now().minus(900000L).toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		toTime = Instant.now().toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		filterQuery = "event_ts:["+fromTime+"%20TO%20"+toTime+"]";
		fromTimeHour = Instant.now().minus(60*60*1000L).toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		filterQueryHour = "event_ts:["+fromTimeHour+"%20TO%20"+toTime+"]";
	}

	@Test
	public void testNormalizationFromSoftJaceIsPresent() throws URISyntaxException {
		setUp(getProperty("em.SoftJace.assetSourcekey"));
		Response  response =	given().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals("ENTERPRISE_4951b25b-3616-30e8-a85d-9239593b35fd")," enterprise Id did not match");	
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}

	@Test
	public void testNormalizationFE2EAssetIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekey"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								urlEncodingEnabled(false).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		System.out.print("<======Number of rows========>"+resultListEnterprise.size());
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=14, "Data points expected within 15 min interval in evnet_normlog : 14 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).toString().contains(getProperty("em.Enterprise.enterpriseSourceKey")),resultListEnterprise.get(0).toString()+" enterprise Id did not match");
	}
	
	@Test
	public void testNormalizationFE2ESubmeterAssetIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Asset.assetSourcekey"));
		validateSubmeter();
	}

	@Test
	public void testNormalizationFE2ESubmeterSiteIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Site.siteSourceKey"));
		uri = new URI(uri.toString().replace("resrc_uid","site_uid"));
		validateSubmeter();
	}
	
	@Test
	public void testNormalizationFE2ESubmeterEnterpriseIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		uri = new URI(uri.toString().replace("resrc_uid","enterprise_uid"));
		validateSubmeter();
	}
	
	@Test
	public void testNormalizationFE2ESubmeterAssetTypeIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Site.siteSourceKey"));
		uri = new URI(uri.toString().replace("resrc_uid","site_uid"));
		filterQuery = filterQuery + " AND asset_type:SUBMETER";
		validateSubmeter();
	}
	
	@Test
	public void testNormalizationFE2ESubmeterSegmentIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Segment.SourceKey"));
		uri = new URI(uri.toString().replace("resrc_uid","segment_uid"));
		validateSubmeter();
	}
	
	@Test
	public void testNormalizationFE2ESubmeterAssetIsPresentHour() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Asset.assetSourcekey"));
		isHour = true;
		validateSubmeter();
	}

	@Test
	public void testNormalizationFE2ESubmeterSiteIsPresentHour() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Site.siteSourceKey"));
		uri = new URI(uri.toString().replace("resrc_uid","site_uid"));
		isHour = true;
		validateSubmeter();
	}
	
	@Test
	public void testNormalizationFE2ESubmeterEnterpriseIsPresentHour() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		uri = new URI(uri.toString().replace("resrc_uid","enterprise_uid"));
		isHour = true;
		validateSubmeter();
	}
	
	@Test
	public void testNormalizationFE2ESubmeterAssetTypeIsPresentHour() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Site.siteSourceKey"));
		uri = new URI(uri.toString().replace("resrc_uid","site_uid"));
		filterQuery = filterQuery + " AND asset_type:SUBMETER";
		isHour = true;
		validateSubmeter();
	}
	
	@Test
	public void testNormalizationFE2ESubmeterSegmentIsPresentHour() throws URISyntaxException {
		setUp(getProperty("em.Submeter.Segment.SourceKey"));
		isHour = true;
		uri = new URI(uri.toString().replace("resrc_uid","segment_uid"));
		validateSubmeter();
	}

	private void validateSubmeter() {
		if(isHour){
			filterQuery = filterQueryHour;
		}
		Response response = given().log().all().header("Authorization", "Bearer " + getSolrServiceToken())
				.param("fq", filterQuery).when().get(uri);
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size() >= 14,
				"Data points expected within 15 min interval in evnet_normlog : 14 but recieved : "
						+ resultListEnterprise.size());
		Assert.assertTrue(
				resultListEnterprise.get(0).toString()
						.contains(getProperty("em.Submeter.Enterprise.enterpriseSourceKey")),
				resultListEnterprise.get(0).toString() + " enterprise Id did not match"+"\n"+uri+"&"+filterQuery);

	}


	@Test
	public void testNormalizationFE2ETagsahuModeSpCheck() throws URISyntaxException {
		List<String> expectedListTagsahuModeSp = new ArrayList<String>();
		expectedListTagsahuModeSp.add("Off");
		expectedListTagsahuModeSp.add("Cooling");
		expectedListTagsahuModeSp.add("Heating");
		int expectedTagListSize = expectedListTagsahuModeSp.size();

		setUp("ASSET_f87729a0-841f-3a09-84f0-0fd5e531d9f4");
		String query = "event_ts:[2017-03-12T15:32:54Z%20TO%202017-03-15T18:02:54Z]";

		Response response = getResponse_SortByEventts();
		JsonPath jsonPath = response.jsonPath();

		List<String> resultListTagsahuModeSp = jsonPath.getList("tagsahuModeSp");

		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");

		if(null!= resultListTagsahuModeSp) {
			Assert.assertTrue(resultListTagsahuModeSp.size()>=14, "Data points expected within 15 min interval in evnet_normlog : 14 but recieved : "+resultListTagsahuModeSp.size());
			for (int i = 0; i < resultListTagsahuModeSp.size(); i++) {
				Assert.assertTrue(expectedListTagsahuModeSp.get(i % expectedTagListSize).equalsIgnoreCase(resultListTagsahuModeSp.get(i)));
			}
		}

	}

	@Test
	public void testNormalizationFE2EAlarmPointCheck() throws URISyntaxException {
		List<String> expectedListAlarmPointTags = new ArrayList<String>();
		expectedListAlarmPointTags.add("Normal");
		expectedListAlarmPointTags.add("Alarm");
		int expectedTagListSize = expectedListAlarmPointTags.size();

		Response response = getResponse_SortByEventts();
		JsonPath jsonPath = response.jsonPath();

		List<String> resultListTagsAlarmPoint = jsonPath.getList("tagsalarmPoint");

		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");

		if(null!= resultListTagsAlarmPoint) {
			Assert.assertTrue(resultListTagsAlarmPoint.size()>=14, "Data points expected within 15 min interval in evnet_normlog : 14 but recieved : "+resultListTagsAlarmPoint.size());
			for (int i = 0; i < resultListTagsAlarmPoint.size(); i++) {
				Assert.assertTrue(expectedListAlarmPointTags.get(i % expectedTagListSize).equalsIgnoreCase(resultListTagsAlarmPoint.get(i)));
			}
		}
	}

	private Response getResponse_SortByEventts() {
		setUp("ASSET_f87729a0-841f-3a09-84f0-0fd5e531d9f4");
		String query = "event_ts:[2017-03-12T15:32:54Z%20TO%202017-03-15T18:02:54Z]";
		Response  response =    given().log().all().
				header("Authorization","Bearer "+ getSolrServiceToken()).
				param("fq", query).
				param("sort", "event_ts").
				param("sortClause", "asc").
				urlEncodingEnabled(false).
				when().
				get(uri);

		return response;
	}


}
