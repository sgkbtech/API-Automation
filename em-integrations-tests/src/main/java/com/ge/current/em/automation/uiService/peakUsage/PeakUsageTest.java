package com.ge.current.em.automation.uiService.peakUsage;

import com.ge.current.em.automation.util.EMTestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ge.current.em.automation.uiService.UIServiceConstants.*;
import static org.testng.Assert.assertEquals;

public class PeakUsageTest extends EMTestUtil {

	private static final Log logger = LogFactory.getLog(PeakUsageTest.class);
	
	@Test
	@Parameters({"timePeriod","endDate", "enterpriseSourceKey"})
	public void test_PeakUsageForEnterprise(@Optional("ENTERPRISE_da0e2eef-46ea-300d-9438-909717d26ae4") String enterpriseSourceKey, 
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
			siteToEnergyMapSolr.put((String)e.get(resrc_uid),(Float)e.get(measures_maxzoneElecMeterPowerSensor));
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
			assertEquals(siteToEnergyMapSolr.get(e.get("siteSourceKey")),(Float)e.get(peakDemand), "Peak Demand for site sourceKey : ".concat((String)e.get("siteSourceKey")));
		});
	}
	
	@Test
	@Parameters({"timePeriod","endDate", "enterpriseSourceKey", "normalization"})
	public void test_NormalizedPeakUsageForEnterprise(@Optional("ENTERPRISE_da0e2eef-46ea-300d-9438-909717d26ae4") String enterpriseSourceKey, 
			@Optional("20170330") String timePeriod, 
			@Optional("20170330") String endDate,
			@Optional("sqft") String normalization) throws IOException {

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
			siteToEnergyMapSolr.put((String)e.get(resrc_uid),(Float)e.get(measures_maxzoneElecMeterPowerSensor));
		});
		/** End - Get response from Solr **/
		
		/** Start - Get response from UI Services **/
		Response actualResult = getResponse(getProperty("ui_service") + "enterprises/" +  enterpriseSourceKey + "/energy", params, getUIServiceToken());
		HashMap<String, Object> sites = JsonPath.from(actualResult.getBody().asString()).get();
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> siteList = (List<HashMap<String, Object>>) sites.get("sites");
		/** End - Get response from UI Services **/
		
		Map<String,Float> apmSiteMap = new HashMap<>();
		Response apmResponse = getResponse(getProperty("apm_service_url") + "/enterprises/" +  enterpriseSourceKey + "/sites?fetchProperties=true", params, getAPMServiceToken());
		List<HashMap<String, Object>> apmSites = JsonPath.from(apmResponse.getBody().asString()).get("content");
		apmSites.stream().forEach(e -> {
			@SuppressWarnings("unchecked")
			List<HashMap<String, Object>> properties = (List<HashMap<String, Object>>) e.get("properties");
			System.out.println(properties);
			@SuppressWarnings("unchecked")
			List<String> sqft = (List<String>) properties.stream().filter( g-> g.get("id").equals("SqFt")  ).findFirst().get().get("value");
			apmSiteMap.put((String)e.get("sourceKey"), Float.valueOf(sqft.get(0)));
		});
		
		/** Assert **/
		siteList.stream().forEach( e-> {
			assertEquals(siteToEnergyMapSolr.get(e.get("siteSourceKey"))/apmSiteMap.get(e.get("siteSourceKey")),(Float)e.get(peakDemand), 0.01);
		});
	}
}
