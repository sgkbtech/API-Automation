package com.ge.current.em.automation.ui;

import com.ge.current.em.automation.provider.UIDataProvider;
import com.ge.current.em.automation.util.EMTestUtil;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public class UIFunctionalTest extends EMTestUtil {
	private static final Log logger = LogFactory.getLog(UIServicesTest.class);
	private static final String DAILY = "Daily";
	private static final String HOURLY = "Hourly";
	private static long DAY = 24 * 60 * 60 * 1000;
	private static final String YYYYMMDD = "yyyyMMdd";

	@BeforeTest
	public void addPropertiesToTestContext(ITestContext context) {
		for (Map.Entry<Object, Object> property : getProperties().entrySet()) {
			context.setAttribute((String) property.getKey(), property.getValue());
		}
	}

	@Test
	public void testSubmeterDataSiteLevelHourly() throws IOException {
		String timePeriod = Instant.now().toString(DateTimeFormat.forPattern(YYYYMMDD));
		Response response = getResponseSite(
				getProperty("ui_service") + "sites/" + getProperty("em.Submeter.Site.siteSourceKey") + "/peak-demand",
				timePeriod, HOURLY, getProperty("em.Submeter.timezone"));
		assertTrue(!response.getBody().asString().equals("[]"),
				"Submeter Data for the SiteLevel-Hourly is not available in UI services");
	}

	@Test
	public void testSubmeterDataSiteLevelDaily() throws IOException {
		String timePeriod = Instant.now().minus(DAY).toString(DateTimeFormat.forPattern(YYYYMMDD));
		Response response = getResponseSite(
				getProperty("ui_service") + "sites/" + getProperty("em.Submeter.Site.siteSourceKey") + "/peak-demand",
				timePeriod, DAILY, getProperty("em.Submeter.timezone"));
		logger.info("Response ---- " + response.getBody().asString());
		assertTrue(!response.getBody().asString().equals("[]"),
				"Submeter Data for the SiteLevel-Daily is not available in UI services");
	}

	@Test(dataProvider = "peakDemand", dataProviderClass = UIDataProvider.class)
	public void testPeakDemand(String siteSourceKey, float expectedPeakDemand, String timePeriod) throws IOException {
		float actualPeakDemand = getResponseSite(getProperty("ui_service") + "sites/" + siteSourceKey + "/peak-demand",
				timePeriod, DAILY, getProperty("em.timezone")).jsonPath().get("[0].kW");
		assertTrue(expectedPeakDemand == actualPeakDemand,
				" expectedPeakDemand : " + expectedPeakDemand + " -- actualPeakDemand: " + actualPeakDemand);
	}

	@Test(dataProvider = "energyUse", dataProviderClass = UIDataProvider.class)
	public void testEnegryUse(String siteSourceKey, float expectedEnergyUse, String timePeriod)
			throws NumberFormatException, IOException {
		float actualEnergyUse = Float
				.valueOf(getResponseSite(getProperty("ui_service") + "sites/" + siteSourceKey + "/energy-use",
						timePeriod, DAILY, getProperty("em.timezone")).jsonPath().getString("energyUse[0].kwh.total"));
		assertTrue(expectedEnergyUse == actualEnergyUse,
				" expectedEnergyUse : " + expectedEnergyUse + " -- actualEnergyUse: " + actualEnergyUse);
	}

	@Test(dataProvider = "energySpend", dataProviderClass = UIDataProvider.class)
	public void testEnegrySpend(String siteSourceKey, float expectedEnergySpend, String timePeriod)
			throws NumberFormatException, IOException {
		float actualEnergySpend = Float.valueOf(
				getResponseSite(getProperty("ui_service") + "sites/" + siteSourceKey + "/energy-spend", timePeriod,
						DAILY, getProperty("em.timezone")).jsonPath().getString("energySpend[0].cost.total"));
		assertTrue(expectedEnergySpend == actualEnergySpend,
				" expectedEnergySpend : " + expectedEnergySpend + " -- actualEnergySpend: " + actualEnergySpend);
	}

	@Test(dataProvider = "peakDemand", dataProviderClass = UIDataProvider.class)
	public void testPeakDemandAggregation(String siteSourceKey, float expectedPeakDemand, String timePeriod)
			throws IOException {
		float actualPeakDemand = aggregationReponse(siteSourceKey, timePeriod, "kpi.", "DailyAggregation").jsonPath()
				.get("[0].measures_maxzoneElecMeterPowerSensor");
		assertTrue(expectedPeakDemand == actualPeakDemand,
				" expectedPeakDemand : " + expectedPeakDemand + " -- actualPeakDemand: " + actualPeakDemand);
	}

	@Test(dataProvider = "energyUse", dataProviderClass = UIDataProvider.class)
	public void testEnegryUseAggregation(String siteSourceKey, float expectedEnergyUse, String timePeriod)
			throws NumberFormatException, IOException {
		float actualEnergyUse = aggregationReponse(siteSourceKey, timePeriod, "kpi.", "DailyAggregation").jsonPath()
				.get("[0].measures_aggrkWh");
		assertTrue(expectedEnergyUse == actualEnergyUse,
				" expectedEnergyUse : " + expectedEnergyUse + " -- actualEnergyUse: " + actualEnergyUse);
	}

	@Test(dataProvider = "energySpend", dataProviderClass = UIDataProvider.class)
	public void testEnegrySpendAggregation(String siteSourceKey, float expectedEnergySpend, String timePeriod)
			throws NumberFormatException, IOException {
		float actualEnergySpend = aggregationReponse(siteSourceKey, timePeriod, "cost.", "DailyAggregation").jsonPath()
				.get("[0].measures_aggrCOST");
		assertTrue(expectedEnergySpend == actualEnergySpend,
				" expectedEnergySpend : " + expectedEnergySpend + " -- actualEnergySpend: " + actualEnergySpend);
	}

	@Test(dataProvider = "energyUseSavingsLifetime", dataProviderClass = UIDataProvider.class)
	public void testEnergyUseSavingsLifetimeAggregation(String siteSourceKey, float expectedEnergyUseSavingsLifetime,
			String timePeriod) throws NumberFormatException, IOException {
		float actualEnergyUseSavingsLifetime = aggregationReponse(siteSourceKey, timePeriod, "cost.",
				"DailyAggregation").jsonPath().get("[0].measures_aggrLIFE");
		assertTrue(expectedEnergyUseSavingsLifetime == actualEnergyUseSavingsLifetime,
				" expectedEnergyUseSavingsLifetime : " + expectedEnergyUseSavingsLifetime + " -- actualEnergySpend: "
						+ actualEnergyUseSavingsLifetime);
	}

	@Test(dataProvider = "energyUseSavingsYoY", dataProviderClass = UIDataProvider.class)
	public void testEnergyUseSavingsYoYAggregation(String siteSourceKey, float expectedEnergyUseSavingsYoY,
			String timePeriod) throws NumberFormatException, IOException {
		float actualEnergyUseSavingsYoY = aggregationReponse(siteSourceKey, timePeriod, "cost.", "DailyAggregation")
				.jsonPath().get("[0].measures_aggrYOY");
		assertTrue(expectedEnergyUseSavingsYoY == actualEnergyUseSavingsYoY, " expectedEnergyUseSavingsYoY : "
				+ expectedEnergyUseSavingsYoY + " -- actualEnergyUseSavingsYoY: " + actualEnergyUseSavingsYoY);
	}

	@Test
	public void testEnergySavingsSiteLevelHourly() throws IOException {
		String timePeriod = Instant.now().toString(DateTimeFormat.forPattern(YYYYMMDD));
		Response response = getResponseSite(
				getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/energy-savings",
				timePeriod, HOURLY, getProperty("em.timezone"));
		assertTrue(!response.getBody().asString().equals("[]"), "SiteLevel-Hourly is not available in UI services");
		Map<String, Object> firstHourData = response.jsonPath().get("[0]");
		String expectedHour = firstHourData.get("hour").toString();
		float expectedLifeTimeSavings = (float) firstHourData.get("measures_aggrLifeTimekWhSavings");
		float expectedYOYSavings = (float) firstHourData.get("measures_aggrYOYkWhSavings");
		logger.info("expectedHour : - " + expectedHour + ",expectedLifeTimeSavings: " + expectedLifeTimeSavings
				+ ",expectedYOYSavings :" + expectedYOYSavings);
		Response solarResponse = aggregationReponse("em.Site.siteSourceKey", timePeriod, "kpi.", "HourlyAggregation");
		String jsonString = solarResponse.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String actualHour = e.get("hour").toString();
			float actualLifeTimeSavings = Float.valueOf(e.get("measures_aggrLifeTimekWhSavings").toString());
			float actualYOYSavings = Float.valueOf(e.get("measures_aggrYOYkWhSavings").toString());
			if (expectedHour.equals(actualHour)) {
				assertTrue(expectedLifeTimeSavings == actualLifeTimeSavings, " expectedEnergyUseLifeTimeSavings : "
						+ expectedYOYSavings + " -- actualEnergyUseLifrTimeSavings: " + actualYOYSavings);
				assertTrue(expectedYOYSavings == actualYOYSavings, " expectedEnergyUseSavingsYoY : "
						+ expectedYOYSavings + " -- actualEnergyUseSavingsYoY: " + actualYOYSavings);
				break;
			}

		}
	}

	@Test
	public void testEnergySpendSiteLevelHourly() throws IOException {
		String timePeriod = Instant.now().toString(DateTimeFormat.forPattern(YYYYMMDD));
		Response response = getResponseSite(
				getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/energy-spend",
				timePeriod, HOURLY, getProperty("em.timezone"));
		assertTrue(!response.getBody().asString().equals("[]"), " SiteLevel-Hourly is not available in UI services");
		Map<String, Object> firstHourData = response.jsonPath().get("[0]");
		String expectedHour = firstHourData.get("hour").toString();
		float expectedSpend = (float) firstHourData.get("measures_aggrCOST");
		logger.info("expectedHour : - " + expectedHour + ",ExpectedSpend: " + expectedSpend);
		Response solarResponse = aggregationReponse("em.Site.siteSourceKey", timePeriod, "cost.", "HourlyAggregation");
		String jsonString = solarResponse.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String actualHour = e.get("hour").toString();
			float actualEnergyspend = Float.valueOf(e.get("measures_aggrCOST").toString());
			if (expectedHour.equals(actualHour)) {
				assertTrue(expectedSpend == actualEnergyspend,
						" expectedSpend : " + expectedSpend + " -- actualEnergyspend: " + actualEnergyspend);
				break;
			}
		}
	}

	@Test
	public void testEnergyUsageBreakdownHourly() throws IOException {
		String timePeriod = Instant.now().toString(DateTimeFormat.forPattern(YYYYMMDD));
		Response response = getResponseSite(
				getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/energy-spend",
				timePeriod, HOURLY, getProperty("em.timezone"));
		assertTrue(!response.getBody().asString().equals("[]"), " SiteLevel-Hourly is not available in UI services");
		Map<String, Object> firstHourData = response.jsonPath().get("[0]");
		String expectedHour = firstHourData.get("hour").toString();
		float expectedEnergyBreakdown = (float) firstHourData.get("measures_aggrMidPeakkWh");
		logger.info("expectedHour : - " + expectedHour + ",expectedEnergyBreakdown: " + expectedEnergyBreakdown);
		Response solarResponse = aggregationReponse("em.Site.siteSourceKey", timePeriod, "cost.", "HourlyAggregation");
		String jsonString = solarResponse.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String actualHour = e.get("hour").toString();
			float actualEnergyBreakdown = Float.valueOf(e.get("measures_aggrMidPeakkWh").toString());
			if (expectedHour.equals(actualHour)) {
				assertTrue(expectedEnergyBreakdown == actualEnergyBreakdown, " expectedEnergyBreakdown : "
						+ expectedEnergyBreakdown + " -- actualEnergyBreakdown: " + actualEnergyBreakdown);
				break;
			}

		}
	}

	@Test
	public void testEnergyUseHourly() throws IOException {
		String timePeriod = Instant.now().toString(DateTimeFormat.forPattern(YYYYMMDD));
		Response response = getResponseSite(
				getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/energy-use",
				timePeriod, HOURLY, getProperty("em.timezone"));
		assertTrue(!response.getBody().asString().equals("[]"), " SiteLevel-Hourly is not available in UI services");
		Map<String, Object> firstHourData = response.jsonPath().get("[0]");
		String expectedHour = firstHourData.get("hour").toString();
		float expectedEnergyuse = (float) firstHourData.get("measures_aggrkWh");
		logger.info("expectedHour : - " + expectedHour + ",expectedEnergyBreakdown: " + expectedEnergyuse);
		Response solarResponse = aggregationReponse("em.Site.siteSourceKey", timePeriod, "kpi.", "HourlyAggregation");
		String jsonString = solarResponse.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String actualHour = e.get("hour").toString();
			float actualEnergyuse = Float.valueOf(e.get("measures_aggrkWh").toString());
			if (expectedHour.equals(actualHour)) {
				assertTrue(expectedEnergyuse == actualEnergyuse,
						" expectedEnergyuse : " + expectedEnergyuse + " -- actualEnergyuse: " + actualEnergyuse);
				break;
			}

		}
	}

	@Test
	public void testpeakdemandHourly() throws IOException {
		String timePeriod = Instant.now().toString(DateTimeFormat.forPattern(YYYYMMDD));
		Response response = getResponseSite(
				getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/peak-demand",
				timePeriod, HOURLY, getProperty("em.timezone"));
		assertTrue(!response.getBody().asString().equals("[]"), " SiteLevel-Hourly is not available in UI services");
		Map<String, Object> firstHourData = response.jsonPath().get("[0]");
		String expectedHour = firstHourData.get("hour").toString();
		float expectedpeakdemand = (float) firstHourData.get("measures_maxzoneElecMeterPowerSensor");
		logger.info("expectedHour : - " + expectedHour + ",expectedEnergyBreakdown: " + expectedpeakdemand);
		Response solarResponse = aggregationReponse("em.Site.siteSourceKey", timePeriod, "kpi.", "HourlyAggregation");
		String jsonString = solarResponse.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String actualHour = e.get("hour").toString();
			float actualpeakdemand = Float.valueOf(e.get("measures_maxzoneElecMeterPowerSensor").toString());
			if (expectedHour.equals(actualHour)) {
				assertTrue(expectedpeakdemand == actualpeakdemand,
						" expectedEnergyuse : " + expectedpeakdemand + " -- actualpeakdemand: " + actualpeakdemand);
				break;
			}

		}
	}

	private Response getResponseSite(String url, String timePeriod, String dataFormat, String timezone)
			throws IOException {
		Map<String, String> siteparams = new HashMap<String, String>();
		siteparams.put("startDate", timePeriod);
		siteparams.put("endDate", timePeriod);
		siteparams.put("dataFormat", dataFormat);
		siteparams.put("timezone", timezone);
		return getResponse(url, siteparams);
	}

	public Response getResponse(String url, Map<String, String> params) throws IOException {
		URI uri = appendURI(url, params);
		logger.info("uri" + uri);
		Response response = RestAssured.given().contentType("application/json").when()
				.header("predix-zone-id", getProperties().get("em.ui.predix-zone-id"))
				.header("Authorization", "Bearer " + getUIServiceToken()).get(uri).then().extract().response();
		assertTrue((response.statusCode() == HttpStatus.OK.value()),
				"Assertion Failed:Response Code expected is 200 - Code returned is:" + response.statusCode()
						+ "\n uri : " + uri + "\n");
		// logger.info(response.jsonPath());
		return response;
	}

	public Response aggregationReponse(String siteSourceKey, String timePeriod, String eventBucket,
			String aggregationType) {
		String url = getProperty("solr_service_url") + getProperty("DBSchema") + getProperty(aggregationType)
				+ "/filtersearch";
		Map<String, String> params = new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -3648221696828123535L;

			{
				put("query", "resrc_uid:" + siteSourceKey);
				if (aggregationType.equals("HourlyAggregation")) {
					put("fq",
							"event_bucket:" + eventBucket + siteSourceKey + ".reading" + " AND yyyymmdd:" + timePeriod);
				} else {
					put("fq",
							"day:[" + timePeriod.substring(6) + " TO " + timePeriod.substring(6) + "] AND "
									+ "event_bucket:" + eventBucket + siteSourceKey + ".reading" + " AND yyyymm:"
									+ timePeriod.substring(0, 6));
				}
			}
		};
		URI uri = appendURI(url, params);
		logger.info("uri" + uri);
		Response response = RestAssured.given().contentType("application/json").when()
				.header("Authorization", "Bearer " + getSolrServiceToken()).get(uri).then().extract().response();
		assertTrue((response.statusCode() == HttpStatus.OK.value()),
				"Assertion Failed:Response Code expected is 200 - Code returned is:" + response.statusCode()
						+ "\n uri : " + uri + "\n");
		return response;
	}
}
