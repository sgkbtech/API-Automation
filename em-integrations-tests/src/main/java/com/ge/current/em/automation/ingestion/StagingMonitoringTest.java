package com.ge.current.em.automation.ingestion;

import com.ge.current.em.automation.util.EMTestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Created by 212582713 on 04/04/2017.
 */

public class StagingMonitoringTest extends EMTestUtil {

	private static final String SEARCH_BY = "/filtersearch?query=resrc_uid:";
	private static final Long TS_HOURLY_OFFSET = (long) (60*60*1000);
	private static final Long TS_DAILY_OFFSET = (long) (24*60*60*1000);
	private static final Long TS_MIN_OFFSET = (long) (15*60*1000);
	private String solrServiceToken;

	URI uri;
	String filterQuery, fromTime, toTime;
	String filterQueryHour, fromTimeHour;
	boolean isHour = false;

	@BeforeTest
	public void setUpToken() {
		solrServiceToken = getSolrServiceToken();
	}

	private void setUp(Long minInterval,Long offSet) {
		fromTime = Instant
				.now()
				.minus(minInterval * 60 * 1000L).minus(offSet)
				.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		toTime = Instant.now().minus(offSet).toString(
				DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		filterQuery = "event_ts:[" + fromTime + "%20TO%20" + toTime + "]";
	}

	@Test(dataProvider = "listAssetsForSite")
	public void testIfAssetsAreGettingNormalized(String sourceKey)
			throws URISyntaxException {
		setUp(5L, 0L);
		//System.out.println(sourceKey);
		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema") + getProperty("Normalized")
				+ SEARCH_BY);
		Response response = given()
				.header("Authorization", "Bearer " + solrServiceToken)
				.param("fq", filterQuery).urlEncodingEnabled(false).when()
				.get(uri + sourceKey);

		JsonPath jsonPath = response.jsonPath();

		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");

		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
		Assert.assertTrue(
				resultListEnterprise.size() >= 1,
				"Data points expected within 5 min interval in event_normlog >= 1 for "
						+ sourceKey + " but received: "
						+ resultListEnterprise.size());
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");

	}

	@Test(dataProvider = "listAssetsForSite")
	public void testIfDataReachingEventLog(String sourceKey)
			throws URISyntaxException {
		setUp(5L,0L);
		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema") + getProperty("Log") + SEARCH_BY);
		Response response = given()
				.header("Authorization", "Bearer " + solrServiceToken)
				.param("fq", filterQuery).urlEncodingEnabled(false).when()
				.get(uri + sourceKey);

		JsonPath jsonPath = response.jsonPath();

		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");

		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
		Assert.assertTrue(
				resultListEnterprise.size() >= 1,
				"Data points expected within 5 min interval in event_normlog >= 1 for "
						+ sourceKey + " but received: "
						+ resultListEnterprise.size());
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");

	}

	@Test(dataProvider = "listAssetsForSite")
	public void testIfAssets15MinAggregation(String sourceKey)
			throws URISyntaxException {
		setUp(15L,TS_MIN_OFFSET);
		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema") + getProperty("MinAggregation")
				+ SEARCH_BY);
		Response response = given()
				.header("Authorization", "Bearer " + solrServiceToken)
				.param("fq", filterQuery).urlEncodingEnabled(false).when()
				.get(uri + sourceKey);

		JsonPath jsonPath = response.jsonPath();

		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");

		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
		Assert.assertTrue(
				resultListEnterprise.size() >= 1,
				"Data points expected within 15 min interval in event_normlog >= 1 for "
						+ sourceKey + " but received: "
						+ resultListEnterprise.size());
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
	}

	@Test(dataProvider = "listAssetsForSite")
	public void testIfAssetsHourlyAggregation(String sourceKey)
			throws URISyntaxException {
		setUp(60L,TS_HOURLY_OFFSET);
		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema")
				+ getProperty("HourlyAggregation") + SEARCH_BY);
		Response response = given()
				.header("Authorization", "Bearer " + solrServiceToken)
				.param("fq", filterQuery).urlEncodingEnabled(false).when()
				.get(uri + sourceKey);

		JsonPath jsonPath = response.jsonPath();

		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");

		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
		Assert.assertTrue(
				resultListEnterprise.size() >= 1,
				"Data points expected within 60 min interval in event_normlog >= 1 for "
						+ sourceKey + " but received: "
						+ resultListEnterprise.size());
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
	}

	@Test(dataProvider = "listAssetsForSite")
	public void testIfAssetsDailyAggregation(String sourceKey)
			throws URISyntaxException {
		DateTime now = Instant.now().minus(TS_DAILY_OFFSET).toDateTime();
		DateTime dayAgo = now.minusDays(1);

		fromTime = dayAgo.toString(DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		toTime = now.toString(
				DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		filterQuery = "event_ts:[" + fromTime + "%20TO%20" + toTime + "]";

		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema") + getProperty("DailyAggregation")
				+ SEARCH_BY);
		Response response = given()
				.header("Authorization", "Bearer " + solrServiceToken)
				.param("fq", filterQuery).urlEncodingEnabled(false).when()
				.get(uri + sourceKey);

		JsonPath jsonPath = response.jsonPath();

		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");

		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
		Assert.assertTrue(
				resultListEnterprise.size() >= 1,
				"Data points expected within 1month interval in event_normlog >= 1 for "
						+ sourceKey + " but received: "
						+ resultListEnterprise.size());
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
	}

	@Test(dataProvider = "listAssetsForSite")
	public void testIfAssetsMonthlyAggregation(String sourceKey)
			throws URISyntaxException {
		DateTime now = Instant.now().toDateTime().minusMonths(1);
		DateTime monthAgo = now.minusMonths(1);

		fromTime = monthAgo.toString(DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		toTime = now.toString(
				DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		filterQuery = "event_ts:[" + fromTime + "%20TO%20" + toTime + "]";

		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema")
				+ getProperty("MonthlyAggregation") + SEARCH_BY);
		Response response = given().log().all()
				.header("Authorization", "Bearer " + solrServiceToken)
				.param("fq", filterQuery).urlEncodingEnabled(false).when()
				.get(uri + sourceKey);

		JsonPath jsonPath = response.jsonPath();

		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");

		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
		Assert.assertTrue(
				resultListEnterprise.size() >= 1,
				"Data points expected within 1month interval in event_normlog >= 1 for "
						+ sourceKey + " but received: "
						+ resultListEnterprise.size());
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
				"status code was not Httpstatus.SC_OK");
	}

	/**
	 * Get all assets for the site that are NOT gateway and utility meters
	 * 
	 * @return
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "listAssetsForSite")
	public Object[][] getAssetsForSite() throws URISyntaxException {
		String jaceAssetScrKey = null;
		Response response = given()
				.contentType("application/json")
				.header("Authorization", "Bearer " + getAPMServiceToken())
				.param("size", 100)
				.when()
				.get(getProperty("apm_service_url") + "/sites/"
						+ getProperty("em.Site.siteSourceKey") + "/assets");
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> content = jsonPath.get("content");

		for (HashMap<String, Object> asset : content) {
			if (getProperty("em.select.asetTypes").equals(asset.get("code"))) {
				jaceAssetScrKey = asset.get("sourceKey").toString();
			}
		}

		// System.out.println(" jaceAssetScrKey " + jaceAssetScrKey);
		response = given()
				.contentType("application/json")
				.header("Authorization", "Bearer " + getAPMServiceToken())
				.when()
				.get(getProperty("apm_service_url") + "/assets/"
						+ jaceAssetScrKey + "/children");
		jsonPath = response.jsonPath();
		content = jsonPath.get("content");
		List<String> allAssets = new ArrayList<String>(content.size());
        int index = 0;
		
		for (HashMap<String, Object> asset : content) {
            if(!getProperty("em.blacklist.assetTypes").contains(asset.get("code").toString())){
                allAssets.add(asset.get("sourceKey").toString());
                index++;
            }   
            //System.out.println(asset.get("code").toString()+ " " + index) ;
        }
		allAssets.removeAll(Collections.singleton(null));
        Object[][] listOfAssets = new Object[allAssets.size()][1];
       
        for(index=0;index< allAssets.size();index++){
            listOfAssets[index] = new Object[1];
            listOfAssets[index][0] = allAssets.get(index);
        }
        return listOfAssets;
	}

}
