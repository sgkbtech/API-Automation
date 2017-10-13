package com.ge.current.em.automation.ui;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import com.ge.current.em.automation.util.EMTestUtil;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UIServicesTest extends EMTestUtil {
	private static final Log logger = LogFactory.getLog(UIServicesTest.class);
	private static long MONTH = 31L * 24L * 3600000L;
	private static long MINUTES = 900000L;
	//private static long DAYS = 9000000000L;
	private static long DAY = 24*60*60*1000;
	private static final String YYYYMMDD = "yyyyMMdd";
	private static final String YYYYMM = "yyyyMM";
	private static final String TS = "yyyyMMddHHmmss";

	@Test
	public void testEnterpriseAlarmForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "getEnterpriseAlarm";
		Map<String, String> params = new HashMap<String, String>();
		params.put("enterpriseSourceKey", getProperty("em.Enterprise.enterpriseSourceKey"));
		statusCheck(url, params);
	}

	@Test
	public void testEnterpriseEnergyDataForSoftJace() throws IOException {
		String timePeriod = Instant.now().minus(MONTH).toString(DateTimeFormat.forPattern(YYYYMM));
		String endDate = Instant.now().toString(DateTimeFormat.forPattern(YYYYMMDD));
		String url = getProperty("ui_service") + "enterprises/" + getProperty("em.Enterprise.enterpriseSourceKey")
				+ "/energy";
		Map<String, String> params = new HashMap<String, String>();
		params.put("endDate", endDate);
		params.put("timePeriod", timePeriod);
		statusCheck(url, params);
	}

	@Test
	public void testSiteEnergySpendForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/energy-spend";
		statusCheck(url, getSiteParams());
	}

	@Test
	public void testSiteEnergyUsageForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey")
				+ "/energy-usage-breakdown";
		statusCheck(url,getSiteParams());
	}

	@Test
	public void testKPIMeasuresForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "getKPIMeasures";
		statusCheck(url,getKpiParams());
	}

	@Test
	public void testKPIListForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "getKPIs";
		statusCheck(url,getKpiParams());
		
	}

	@Test
	public void testGranularityForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "getGranularity";
		statusCheck(url,getKpiParams());
	}

	@Test
	public void testMVDataForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "getData";
		statusCheck(url,getKpiParams());
	}

	@Test
	public void testSiteEnergySavingsForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/energy-savings";
		Map<String, String> params = getSiteParams();
		params.put("siteInstallationDate", "20170101");
		params.put("refreshLifetimeSavingValue", "true");
		statusCheck(url,params);
	}

	@Test
	public void testSiteEnergyUseForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/energy-use";
		statusCheck(url, getSiteParams());
	}

	@Test
	public void testSitePeakDemandForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/peak-demand";
		statusCheck(url, getSiteParams());
	}

	@Test
	public void testSiteYoyCostSavingsForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "sites/" + getProperty("em.Site.siteSourceKey") + "/yoy-costSavings";
		statusCheck(url, getSiteParams());
	}

	@Test
	public void testSiteMetaDataForSoftJace() throws IOException {
		String url = getProperty("ui_service") + "getSiteMetadataInformation";
		Map<String, String> params = new HashMap<String, String>();
		params.put("enterpriseSourceKey", getProperty("em.Enterprise.enterpriseSourceKey"));
		statusCheck(url, params);
	}

	private void statusCheck(String url, Map<String, String> params) throws IOException {
		URI uri = appendURI(url, params);
		logger.info("uri" + uri);
		Response response = RestAssured.given().contentType("application/json").when()
				.header("predix-zone-id", getProperties().get("em.ui.predix-zone-id"))
				.header("Authorization", "Bearer " + getUIServiceToken()).get(uri).then().extract().response();
		assertTrue((response.statusCode() == HttpStatus.OK.value()), "Assertion Failed:Response Code expected is "
				+ HttpStatus.OK.value() + " - Code returned is:" + response.statusCode() + "\n uri : " + uri + "\n");
	}
	
	private Map<String,String> getSiteParams(){
		String timeFormat = "yyyyMMdd";
		String fromTime = Instant.now().minus(DAY).toString(DateTimeFormat.forPattern(timeFormat));
		String toTime = Instant.now().toString(DateTimeFormat.forPattern(timeFormat));
		Map<String, String> params = new HashMap<String, String>();
		params.put("startDate", fromTime);
		params.put("endDate", toTime);
		params.put("dataFormat", "Hourly");
		params.put("timezone", getProperty("em.timezone"));
		return params;
	}
	
	private Map<String,String> getKpiParams(){
		String fromTime = Instant.now().minus(MINUTES).toString(DateTimeFormat.forPattern(TS));
		String toTime = Instant.now().toString(DateTimeFormat.forPattern(TS));
		Map<String, String> params = new HashMap<String, String>();
		params.put("startTimeStamp", fromTime);
		params.put("endTimeStamp", toTime);
		params.put("granularity", "Hourly");
		params.put("timezone", getProperty("em.timezone"));
		params.put("resourceUid", getProperty("em.Site.siteSourceKey"));
		params.put("kpi", "zoneAirTempSensor");
		params.put("measure", "avg");
		return params;
	}
}
