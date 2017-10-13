package com.ge.current.em.automation.uiService.energySavings;

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

public class EnergySpendSavingsTest extends EMTestUtil {

	private static final Log logger = LogFactory.getLog(EnergySpendSavingsTest.class);

	@Test
	@Parameters({"timePeriod", "endDate", "enterpriseSourceKey"})
	public void test_EnergySpendSavingsYOYForEnterprise(@Optional("ENTERPRISE_da0e2eef-46ea-300d-9438-909717d26ae4") String enterpriseSourceKey, 
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
				+ " AND " + eventBucket + ":" + eventBucketSearchCost;

		/** Start - Get response from Solr **/
		Response expectedResult = solrResponse(dailyAggregation, query);
		List<HashMap<String, Object>> list = JsonPath.from(expectedResult.getBody().asString()).get();

		Map<String,Float> siteToEnergyMapSolr = new HashMap<>();
		list.stream().forEach( e-> {
			siteToEnergyMapSolr.put((String)e.get(resrc_uid),(Float)e.get(measures_aggrYOY));
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
			assertEquals((Float)e.get(energySpendSavingsYoY), siteToEnergyMapSolr.get(e.get("siteSourceKey")), "Energy Spend savings - YOY for site sourceKey : ".concat((String)e.get("siteSourceKey")));
		});
	}

	@Test
	@Parameters({"timePeriod","endDate", "enterpriseSourceKey"})
	public void test_EnergySpendSavingsLifeTimeForEnterprise(@Optional("ENTERPRISE_da0e2eef-46ea-300d-9438-909717d26ae4") String enterpriseSourceKey, 
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
				+ " AND " + eventBucket + ":" + eventBucketSearchCost;

		/** Start - Get response from Solr **/
		Response expectedResult = solrResponse(dailyAggregation, query);
		List<HashMap<String, Object>> list = JsonPath.from(expectedResult.getBody().asString()).get();

		Map<String,Float> siteToEnergyMapSolr = new HashMap<>();
		list.stream().forEach( e-> {
			siteToEnergyMapSolr.put((String)e.get(resrc_uid),(Float)e.get(measures_aggrLIFE));
		});
		System.out.println("Map >>>"+siteToEnergyMapSolr);
		/** End - Get response from Solr **/

		/** Start - Get response from UI Services **/
		Response actualResult = getResponse(getProperty("ui_service") + "enterprises/" +  enterpriseSourceKey + "/energy", params, getUIServiceToken());
		HashMap<String, Object> sites = JsonPath.from(actualResult.getBody().asString()).get();
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> siteList = (List<HashMap<String, Object>>) sites.get("sites");
		
		/** End - Get response from UI Services **/

		/** Assert **/
		siteList.stream().forEach( e-> {
			assertEquals((Float)e.get(energySpendSavingsLifetime), siteToEnergyMapSolr.get(e.get("siteSourceKey")), "Energy Spend savings - Lifetime for site sourceKey : ".concat((String)e.get("siteSourceKey")));
		});
	}
}
