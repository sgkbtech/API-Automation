package com.ge.current.em.automation.uiService.energyUsage;

import static com.ge.current.em.automation.uiService.UIServiceConstants.ENTERPRISE_SOURCE_KEY;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static com.ge.current.em.automation.uiService.UIServiceConstants.*;
import com.ge.current.em.automation.util.EMTestUtil;

public class EnergyUsageTest extends EMTestUtil {

	private static final Log logger = LogFactory.getLog(EnergyUsageTest.class);

	@Test
	@Parameters({"timePeriod","endDate", "enterpriseSourceKey"})
	public void test_EnergyUsageForEnterprise(@Optional("ENTERPRISE_da0e2eef-46ea-300d-9438-909717d26ae4") String enterpriseSourceKey, 
			@Optional("20170330") String timePeriod, 
			@Optional("20170330") String endDate) throws IOException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("timePeriod", timePeriod);
		params.put("endDate", endDate);

		String yyyy = timePeriod.substring(0, 4);
		String month = timePeriod.substring(4, 6);
		int startDay = Integer.parseInt(timePeriod.substring(6));
		int endDay = Integer.parseInt(endDate.substring(6));

		if(enterpriseSourceKey == null) { enterpriseSourceKey = getProperty(ENTERPRISE_SOURCE_KEY); }

		String query = enterprise_uid + ":"+ enterpriseSourceKey 
				+ " AND "+ resrc_type + ":SITE"
				+ " AND " + yyyymm + ":" + yyyy + month + ""
				+ " AND " + day + ": [ " + startDay + " TO " + endDay + " ]"
				+ " AND " + eventBucket + ":" + eventBucketSearchKpi;

		/** Start - Get response from Solr **/
		Response expectedResult = solrResponse(dailyAggregation, query);
		List<HashMap<String, Object>> list = JsonPath.from(expectedResult.getBody().asString()).get();

		Map<String,Float> siteToEnergyMapSolr = new HashMap<>();
		list.stream().forEach( e-> {
			siteToEnergyMapSolr.put((String)e.get(resrc_uid),(Float)e.get(measures_aggrkWh));
		});
		/** End - Get response from Solr **/

		/** Start - Get response from UI Services **/
		Response actualResult = getResponse(getProperty("ui_service") + "enterprises/" +  enterpriseSourceKey + "/energy", params, getUIServiceToken());
		HashMap<String, Object> sites = JsonPath.from(actualResult.getBody().asString()).get();
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> siteList = (List<HashMap<String, Object>>) sites.get("sites");
		/** End - Get response from UI Services **/

		/** Assert **/
		siteList.stream().forEach( e-> {
			assertEquals((Float)e.get(energyUse), siteToEnergyMapSolr.get(e.get("siteSourceKey")), "Energy Usage for site sourceKey : ".concat((String)e.get("siteSourceKey")));
		});
	}

	@Test
	@Parameters({"timePeriod","endDate", "enterpriseSourceKey", "normalization"})
	public void test_NormalizedEnergyUsageForEnterprise(@Optional("ENTERPRISE_da0e2eef-46ea-300d-9438-909717d26ae4") String enterpriseSourceKey, 
			@Optional("20170330") String timePeriod, 
			@Optional("20170330") String endDate,
			@Optional("sqft") String normalization) throws IOException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("timePeriod", timePeriod);
		params.put("endDate", endDate);
		params.put("normalization", normalization);

		String yyyy = timePeriod.substring(0, 4);
		String month = timePeriod.substring(4, 6);
		int startDay = Integer.parseInt(timePeriod.substring(6));
		int endDay = Integer.parseInt(endDate.substring(6));

		if(enterpriseSourceKey == null) { enterpriseSourceKey = getProperty(ENTERPRISE_SOURCE_KEY); }

		String query = enterprise_uid + ":"+ enterpriseSourceKey 
				+ " AND "+ resrc_type + ":SITE"
				+ " AND " + yyyymm + ":" + yyyy + month + ""
				+ " AND " + day + ": [ " + startDay + " TO " + endDay + " ]"
				+ " AND " + eventBucket + ":" + eventBucketSearchKpi;

		/** Start - Get response from Solr **/
		Response expectedResult = solrResponse(dailyAggregation, query);
		List<HashMap<String, Object>> list = JsonPath.from(expectedResult.getBody().asString()).get();

		Map<String,Float> siteToEnergyMapSolr = new HashMap<>();


		list.stream().forEach( e-> {
			siteToEnergyMapSolr.put((String)e.get(resrc_uid),(Float)e.get(measures_aggrkWh));
		});
		/** End - Get response from Solr **/

		/** Start - Get response from UI Services **/
		Response actualResult = getResponse(getProperty("ui_service") + "enterprises/" +  enterpriseSourceKey + "/energy", params, getUIServiceToken());

		Response apmResponse = getResponse(getProperty("apm_service_url") + "/enterprises/" +  enterpriseSourceKey + "/sites?fetchProperties=true", params, getAPMServiceToken());
		Map<String,Float> apmSiteMap = new HashMap<>();
		List<HashMap<String, Object>> apmSites = JsonPath.from(apmResponse.getBody().asString()).get("content");
		apmSites.stream().forEach(e -> {
			@SuppressWarnings("unchecked")
			List<HashMap<String, Object>> properties = (List<HashMap<String, Object>>) e.get("properties");
			System.out.println(properties);
			@SuppressWarnings("unchecked")
			List<String> sqft = (List<String>) properties.stream().filter( g-> g.get("id").equals("SqFt")  ).findFirst().get().get("value");
			apmSiteMap.put((String)e.get("sourceKey"), Float.valueOf(sqft.get(0)));
		});

		System.out.println(apmSiteMap);

		HashMap<String, Object> sites = JsonPath.from(actualResult.getBody().asString()).get();
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> siteList = (List<HashMap<String, Object>>) sites.get("sites");
		/** End - Get response from UI Services **/

		/** Assert **/
		//"Energy Usage for site sourceKey : ".concat((String)e.get("siteSourceKey"))
		siteList.stream().forEach( e-> {
			assertEquals(siteToEnergyMapSolr.get(
					e.get("siteSourceKey"))/apmSiteMap.get(e.get("siteSourceKey")),(Float)e.get(energyUse), 0.001);
		});
	}

	@Test
	@Parameters({"timePeriod","endDate", "enterpriseSourceKey", "siteSourceKey"})
	public void test_EnergyUsageForSiteForLoadType(
			@Optional("ENTERPRISE_da0e2eef-46ea-300d-9438-909717d26ae4") String enterpriseSourceKey,
			@Optional("SITE_5521b291-1e8e-37d4-9dd9-87d6311305c2") String siteSourceKey, 
			@Optional("20170330") String timePeriod, 
			@Optional("20170330") String endDate) throws IOException {

		String yyyy = timePeriod.substring(0, 4);
		String month = timePeriod.substring(4, 6);
		int startDay = Integer.parseInt(timePeriod.substring(6));
		int endDay = Integer.parseInt(endDate.substring(6));

		if(siteSourceKey == null) { siteSourceKey = getProperty(SITE_SOURCE_KEY); }

		String query = enterprise_uid + ":"+ enterpriseSourceKey 
				+ " AND "+ resrc_type + ":SEGMENT"
				+ " AND " + yyyymm + ":" + yyyy + month + ""
				+ " AND " + day + ": [ " + startDay + " TO " + endDay + " ]"
				+ " AND " + eventBucket + ":" + String.format(eventBucketLoadTypeSearchKpi, siteSourceKey);

		/** Start - Get response from Solr **/
		Response expectedResult = solrResponse(dailyAggregation, query);
		List<HashMap<String, Object>> list = JsonPath.from(expectedResult.getBody().asString()).get();

		Map<String,Float> siteToEnergyMapSolr = new HashMap<>();
		list.stream().forEach( e-> {
			siteToEnergyMapSolr.put((String)e.get(ext_propsName),(Float)e.get(measures_aggrkWh));
		});
		/** End - Get response from Solr **/

		/** Start - Get response from UI Services **/
		Map<String, String> params = new HashMap<String, String>();
		params.put("startDate", timePeriod);
		params.put("endDate", endDate);
		params.put("dataFormat", "daily");
		params.put("timezone", "America/Chicago");

		Response actualResult = getResponse(getProperty("ui_service") + "sites/" +  siteSourceKey + "/energy-use", params, getUIServiceToken());
		List<HashMap<String, Object>> loadTypes = actualResult.body().jsonPath().get("energyUse[0].kwh.loadTypes");

		/** Assert **/
		loadTypes.forEach(e -> {
			assertEquals(siteToEnergyMapSolr.get(e.get("name")), Float.valueOf((String)e.get("value")), 0.01);
		});
		/** End - Get response from UI Services **/

	}

	@Test
	@Parameters({"timePeriod","endDate", "enterpriseSourceKey", "siteSourceKey"})
	public void test_TotalEnergyUsageForSite(
			@Optional("ENTERPRISE_da0e2eef-46ea-300d-9438-909717d26ae4") String enterpriseSourceKey,
			@Optional("SITE_5521b291-1e8e-37d4-9dd9-87d6311305c2") String siteSourceKey, 
			@Optional("20170330") String timePeriod, 
			@Optional("20170330") String endDate) throws IOException {

		String yyyy = timePeriod.substring(0, 4);
		String month = timePeriod.substring(4, 6);
		int startDay = Integer.parseInt(timePeriod.substring(6));
		int endDay = Integer.parseInt(endDate.substring(6));

		if(siteSourceKey == null) { siteSourceKey = getProperty(SITE_SOURCE_KEY); }

		String query = enterprise_uid + ":"+ enterpriseSourceKey 
				+ " AND "+ resrc_type + ":SITE"
				+ " AND " + yyyymm + ":" + yyyy + month + ""
				+ " AND " + day + ": [ " + startDay + " TO " + endDay + " ]"
				+ " AND " + eventBucket + ":" + eventBucketSearchKpi;

		/** Start - Get response from Solr **/
		Response expectedResult = solrResponse(dailyAggregation, query);
		List<HashMap<String, Object>> list = JsonPath.from(expectedResult.getBody().asString()).get();

		Map<String,Float> siteToEnergyMapSolr = new HashMap<>();
		list.stream().forEach( e-> {
			siteToEnergyMapSolr.put((String)e.get("resrc_uid"),(Float)e.get(measures_aggrkWh));
		});
		/** End - Get response from Solr **/

		/** Start - Get response from UI Services **/
		Map<String, String> params = new HashMap<String, String>();
		params.put("startDate", timePeriod);
		params.put("endDate", endDate);
		params.put("dataFormat", "daily");
		params.put("timezone", "America/Chicago");

		Response actualResult = getResponse(getProperty("ui_service") + "sites/" +  siteSourceKey + "/energy-use", params, getUIServiceToken());
		String value = actualResult.body().jsonPath().get("energyUse[0].kwh.total");

		/** Assert **/
		assertEquals(siteToEnergyMapSolr.get(siteSourceKey), Float.valueOf(value), 0.01);
		/** End - Get response from UI Services **/

	}
}
