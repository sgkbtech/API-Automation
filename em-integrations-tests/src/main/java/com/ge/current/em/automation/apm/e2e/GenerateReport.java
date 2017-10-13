package com.ge.current.em.automation.apm.e2e;

import static io.restassured.RestAssured.given;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import com.ge.current.em.automation.util.DateAndTimeUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.springframework.http.HttpStatus;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GenerateReport {
	ExcelUtils objExcelUtils = new ExcelUtils();
	Map<String, Object> energyusage = new HashMap<String, Object>();
	Map<String, Object> cost = new HashMap<String, Object>();
	Map<String, Object> lifetimeUsageSavings = new HashMap<String, Object>();
	Map<String, Object> yoyUsageSavings = new HashMap<String, Object>();
	Map<String, Object> lifeTimeCostSavings = new HashMap<String, Object>();
	Map<String, Object> yoyCostSavings = new HashMap<String, Object>();
	
	
	
	

	String testdate, month, authToken;
	String authTokenURL = "https://e6dfde0c-918e-4d5f-9587-f0deb5652d05.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token?grant_type=password";
	String validateLog = "https://validate-services.run.aws-usw02-pr.ice.predix.io/v1/validateFromLog";
	String energyURL = "https://ie-ems-ui-services-ems-stage.run.aws-usw02-pr.ice.predix.io/v1/";
	String password = "se3ret";
	String username = "Walgreens.admin.user";
	String authorization = "ZW1zLXVpLWFwcDE6cGFzc3dvcmQ=";
	String enterpriseSourceKey = "ENTERPRISE_fe8e4739-e77b-30ae-a912-9b5a1284fb40";
	Map<String, String> params=new HashMap<String,String>();
	Map<String, String> params1=new HashMap<String,String>();

	public String getServiceToken() {
		return given().formParam("username", username).formParam("password", password)
				.header("Authorization", "Basic " + authorization).param("grant_type", "password").expect()
				.statusCode(200).when().get(authTokenURL).jsonPath().getString("access_token");
	}

	@Test(dataProvider = "Assetdetails", dataProviderClass = ExcelUtils.class)
	public void validateEnergyusage(String assetkey, String sitekey, String sitename, String instdate,
			String timePeriod, String endDate)
			throws Exception {
		String url = energyURL +"enterprises/"+ enterpriseSourceKey + "/energy";
				
		params.put("timePeriod",timePeriod);
		params.put("endDate",endDate);
		params.put("normalization", "none");
		params.put("isHistoric", "true");
		params.put("refreshLifeTime", "true");

		URI uri = appendURI(url, params);
		//String authtoken=getServiceToken();

		Response response = given().contentType("application/json").when()
				.header("predix-zone-id", "Walgreens1-EM-ADMIN").header("Authorization", "Bearer " + getServiceToken()).get(uri)
				.then().extract().response();
		assertTrue((response.statusCode() == HttpStatus.OK.value()),
				"Assertion Failed:Response Code expected is 200 - Code returned is:" + response.statusCode()
						+ "\n uri : " + uri + "\n");

		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> EnergyUseList = JsonPath.from(jsonString).get("sites");

		String startdate = timePeriod + "000000";
		String enddatetime = endDate + "235959";
		params1.put("start", startdate);
		params1.put("end", enddatetime);
		params1.put("timezone", "America%2FChicago");
		params1.put("assets", assetkey);
		params1.put("siteSourceKey", sitekey);
		params1.put("installationDate", instdate);
		params1.put("usageMeasure","zoneElecMeterEnergySensor");
		URI validateuri = appendURI(validateLog, params1);

		Response responseValidatelog = given().contentType("application/json").when()
				.header("Cache-Control", "no-cache").header("postman-token", "8fbae095-0d6d-a911-57bc-3574966e7c7d")
				.get(validateuri).then().extract().response();
		assertTrue((responseValidatelog.statusCode() == HttpStatus.OK.value()),
				"Assertion Failed:Response Code expected is 200 - Code returned is:" + response.statusCode()
						+ "\n uri : " + validateuri + "\n");
		String jsonValidatelog = responseValidatelog.getBody().asString();
		HashMap<String, Object> validateLogList = JsonPath.from(jsonValidatelog).get();
		for (HashMap<String, Object> e : EnergyUseList) {
			String sourceKey = e.get("siteSourceKey").toString();
			if (sourceKey.equals(sitekey)) {
				calculateEnergyUsage(sitekey, sitename, timePeriod, e, validateLogList);
				calculateCost(sitekey, sitename, timePeriod, e, validateLogList);
				lifeTimeUsageSavings(sitekey, sitename, timePeriod, e, validateLogList);
				yoyUsageSavings(sitekey, sitename, timePeriod, e, validateLogList);
				lifeTimeCostSavings(sitekey, sitename, timePeriod, e, validateLogList);
				yoyCostSavings(sitekey, sitename, timePeriod, e, validateLogList);
			}
		}

	}

	public void calculateEnergyUsage(String sitesourcekey, String sitename, String timePeriod,
			HashMap<String, Object> energyuse, HashMap<String, Object> validatelog)
			throws  IOException {
		String filename = sitesourcekey +"CalculateEnergyUsage"+ timePeriod;

		double siteUIServicesEnergyUse = 0;
		siteUIServicesEnergyUse = Double.valueOf(energyuse.get("energyUse").toString());
		double siteEstimatedEnergyUsage = Double.valueOf(validatelog.get("site estimated usage ").toString());
		double siteEstimatedEnergyUsageAbsolute = Double.valueOf( validatelog.get("site estimated absolute usage ").toString());
		double siteActualEnergyUsageHourlyKPIJob = Double.valueOf(validatelog.get("site actual usage hourly from kpi job").toString());
		double siteActualEnergyUsageDailyKPIJob = Double.valueOf( validatelog.get("site actual usage daily from kpi job").toString());
		double siteActualEnergyUsageHourlyAggregation = Double.valueOf( validatelog
				.get("site actual usage hourly from regular aggregation").toString());
		double siteActualEnergyUsageDailyAggregation =Double.valueOf(validatelog
				.get("site actual usage daily from regular aggregation").toString());
		double perDiffEnergyUse = 0;
		if (siteEstimatedEnergyUsage > 0) {
			perDiffEnergyUse = siteEstimatedEnergyUsage > 0
					? ((siteUIServicesEnergyUse - siteEstimatedEnergyUsage) / siteEstimatedEnergyUsage) * 100 : 0;
			System.out.println(
					"siteUIServicesEnergyUse is $siteUIServicesEnergyUse and Percentage Diff is $perDiffEnergyUse\n");
		}

		double perDiffEnergyUseHourlyAggregatedTable = 0;
		if (siteActualEnergyUsageHourlyAggregation > 0) {
			perDiffEnergyUseHourlyAggregatedTable = siteActualEnergyUsageHourlyAggregation > 0
					? ((siteUIServicesEnergyUse - siteActualEnergyUsageHourlyAggregation) / siteUIServicesEnergyUse)
							* 100
					: null;
		}

		energyusage.put("SiteName", sitename);
		energyusage.put("SiteSourceKey", sitesourcekey);
		energyusage.put("testDate", timePeriod);
		energyusage.put("siteEstimatedEnergyUsage", siteEstimatedEnergyUsage);
		energyusage.put("siteEstimatedEnergyUsageAbsolute", siteEstimatedEnergyUsageAbsolute);
		energyusage.put("siteUIServicesEnergyUse", siteUIServicesEnergyUse);
		energyusage.put("perDiffEnergyUse", perDiffEnergyUse);
		energyusage.put("siteActualEnergyUsageHourlyKPIJob", siteActualEnergyUsageHourlyKPIJob);
		energyusage.put("siteActualEnergyUsageDailyKPIJob", siteActualEnergyUsageDailyKPIJob);
		energyusage.put("siteActualEnergyUsageHourlyAggregation", siteActualEnergyUsageHourlyAggregation);
		energyusage.put("perDiffEnergyUseHourlyAggregatedTable,", perDiffEnergyUseHourlyAggregatedTable);
		energyusage.put("siteActualEnergyUsageDailyAggregation", siteActualEnergyUsageDailyAggregation);

		objExcelUtils.writeIntoExcel(filename, energyusage);

	}

	public void calculateCost(String siteSourceKey, String siteName, String timePeriod,
			HashMap<String, Object> energyuse, HashMap<String, Object> validatelog)
			throws  IOException {
		String filename = siteSourceKey +"Calculatecost"+ timePeriod;
		double siteEstimatedCost = Double.valueOf( validatelog.get("site estimated cost ").toString());
		double siteActualCostHourly = Double.valueOf(validatelog.get("site actual cost hourly  ").toString());
		double siteActualCostDaily = Double.valueOf(validatelog.get("site actual cost daily  ").toString());

		double siteUIServicesenergySpend = Double.valueOf(energyuse.get("energySpend").toString());

		double perDiffenergySpend = 0;
		if (siteEstimatedCost > 0) {
			perDiffenergySpend = siteEstimatedCost > 0
					? ((siteUIServicesenergySpend - siteEstimatedCost) / siteEstimatedCost) * 100 : 0;
			System.out.println("siteUIServicesenergySpend is " + siteUIServicesenergySpend + " and Percentage Diff is"
					+ perDiffenergySpend);
		}
		double siteActualEnergyUsageHourlyAggregation = Double.valueOf(energyusage
				.get("siteActualEnergyUsageHourlyAggregation").toString());
		double siteHourlyTableenergySpend = (siteActualEnergyUsageHourlyAggregation) * 0.08;
		double perDiffenergySpendHourlyTable = siteHourlyTableenergySpend > 0
				? ((siteUIServicesenergySpend - siteHourlyTableenergySpend) / siteHourlyTableenergySpend) * 100 : 0;
		System.out.println("perDiffenergySpendHourlyTable is " + siteHourlyTableenergySpend + " and Percentage Diff is "
				+ perDiffenergySpendHourlyTable);

		cost.put("siteName", siteName);
		cost.put("siteSourceKey", siteSourceKey);
		cost.put("testDate", timePeriod);
		cost.put("siteUIServicesenergySpend", siteUIServicesenergySpend);
		cost.put("perDiffenergySpend", perDiffenergySpend);
		cost.put("siteActualEnergyUsageHourlyAggregation", siteActualEnergyUsageHourlyAggregation);
		cost.put("siteHourlyTableenergySpend", siteHourlyTableenergySpend);
		cost.put("perDiffenergySpendHourlyTable", perDiffenergySpendHourlyTable);
		cost.put("siteActualCostHourly", siteActualCostHourly);
		cost.put("siteActualCostDaily", siteActualCostDaily);
		objExcelUtils.writeIntoExcel(filename, cost);

	}

	public void lifeTimeUsageSavings(String siteSourceKey, String siteName, String timePeriod,
			HashMap<String, Object> energyuse, HashMap<String, Object> validatelog)
			throws  IOException {
		String filename = siteSourceKey + "lifeTimeUsageSavings" + timePeriod;
		double siteEstimatedlifetimeUsageSavings = Double.valueOf(validatelog.get("site expected usage Lifetime savings ").toString());
		double siteEstimatedlifetimeUsageSavingsPercentage = Double.valueOf(validatelog.get("site expected usage Lifetime % ").toString());
		double siteActuallifetimeUsageSavings = Double.valueOf(validatelog.get("site actual usage Lifetime savings ").toString());
		double siteActuallifetimeUsageSavingsPercentage = Double.valueOf(validatelog.get("site actual usage Lifetime % ").toString());

		System.out.println("siteEstimatedlifetimeUsageSavings is " + siteEstimatedlifetimeUsageSavings);
		System.out.println(
				"siteEstimatedlifetimeUsageSavingsPercentage is " + siteEstimatedlifetimeUsageSavingsPercentage);
		System.out.println("siteActuallifetimeUsageSavings is " + siteActuallifetimeUsageSavings);
		System.out.println("siteActuallifetimeUsageSavingsPercentage is" + siteActuallifetimeUsageSavingsPercentage);

		double siteUIServicesenergyUseSavingsLifetime = Double.valueOf(energyuse.get("energyUseSavingsLifetime").toString());

		double perDiffenergyUseSavingsLifetime = 0;
		if (siteEstimatedlifetimeUsageSavings > 0) {
			perDiffenergyUseSavingsLifetime = siteEstimatedlifetimeUsageSavings > 0
					? ((siteUIServicesenergyUseSavingsLifetime - siteEstimatedlifetimeUsageSavings)
							/ siteEstimatedlifetimeUsageSavings) * 100
					: 0;
			System.out.println("siteUIServicesenergySpend is " + siteUIServicesenergyUseSavingsLifetime
					+ " and Percentage Diff is" + perDiffenergyUseSavingsLifetime);

		}
		double siteUIServicesenergyUseBaseline = Double.valueOf(energyuse.get("energyUseBaseline").toString());
		System.out.println("siteUIServicesenergyUseBaseline is" + siteUIServicesenergyUseBaseline);

		lifetimeUsageSavings.put("siteName", siteName);
		lifetimeUsageSavings.put("siteSourceKey", siteSourceKey);
		lifetimeUsageSavings.put("testDate", timePeriod);
		lifetimeUsageSavings.put("siteEstimatedlifetimeUsageSavings", siteEstimatedlifetimeUsageSavings);
		lifetimeUsageSavings.put("siteActuallifetimeUsageSavings", siteActuallifetimeUsageSavings);
		lifetimeUsageSavings.put("siteUIServicesenergyUseSavingsLifetime", siteUIServicesenergyUseSavingsLifetime);
		lifetimeUsageSavings.put("perDiffenergyUseSavingsLifetime", perDiffenergyUseSavingsLifetime);
		lifetimeUsageSavings.put("siteActuallifetimeUsageSavingsPercentage", siteActuallifetimeUsageSavingsPercentage);
		lifetimeUsageSavings.put("siteEstimatedlifetimeUsageSavingsPercentage",
				siteEstimatedlifetimeUsageSavingsPercentage);
		lifetimeUsageSavings.put("siteUIServicesenergyUseBaseline", siteUIServicesenergyUseBaseline);
		objExcelUtils.writeIntoExcel(filename, lifetimeUsageSavings);
	}

	public void yoyUsageSavings(String siteSourceKey,String siteName,String timePeriod,HashMap<String, Object> energyuse,HashMap<String, Object> validatelog) throws   IOException{
		String filename =siteSourceKey+"yoyUsageSavings"+timePeriod; 

		  double siteEstimatedYOYUsageSavings = Double.valueOf(validatelog.get("site expected usage YOY savings ").toString());
		  double siteActualYOYUsageSavings = Double.valueOf(validatelog.get("site actual usage YOY savings ").toString());
		  double siteEstimatedYOYUsageSavingsPercentage =Double.valueOf(validatelog.get("site expected usage YOY % ").toString()); 
		  double siteActualYOYUsageSavingsPercentage = Double.valueOf(validatelog.get("site actual usage YOY % ").toString());
		 System.out.println("siteEstimatedYOYUsageSavings is "+siteEstimatedYOYUsageSavings);
		 System.out.println("siteActualYOYUsageSavings is "+siteActualYOYUsageSavings);
		 System.out.println("siteEstimatedYOYUsageSavingsPercentage is "+siteEstimatedYOYUsageSavingsPercentage);
		 System.out.println("siteActualYOYUsageSavingsPercentage is "+siteActualYOYUsageSavingsPercentage);
		  
		  double siteUIServicesenergyUseSavingsYoY = Double.valueOf(energyuse.get("energyUseSavingsYoY").toString());
		  
		  double perDiffenergyUseSavingsYoY=0;
		  if (siteEstimatedYOYUsageSavings > 0 ) {
		    perDiffenergyUseSavingsYoY = siteEstimatedYOYUsageSavings > 0? ((siteUIServicesenergyUseSavingsYoY - siteEstimatedYOYUsageSavings)/siteEstimatedYOYUsageSavings) * 100 : 0;
		    System.out.println("siteUIServicesenergyUseSavingsYoY is "+siteUIServicesenergyUseSavingsYoY+" and Percentage Diff is "+perDiffenergyUseSavingsYoY);
		    
		  }
		  double siteUIServicesenergyUsePrevYear = Double.valueOf(energyuse.get("energyUsePrevYear").toString());
		  System.out.println("siteUIServicesenergyUsePrevYear is"+siteUIServicesenergyUsePrevYear);  

		  
		  yoyUsageSavings.put("siteName", siteName);
		  yoyUsageSavings.put("siteSourceKey", siteSourceKey);
		  yoyUsageSavings.put("testDate", timePeriod);
		  yoyUsageSavings.put("siteEstimatedYOYUsageSavings", siteEstimatedYOYUsageSavings);
		  yoyUsageSavings.put("siteActualYOYUsageSavings", siteActualYOYUsageSavings);
		  yoyUsageSavings.put("siteUIServicesenergyUseSavingsYoY", siteUIServicesenergyUseSavingsYoY);
		  yoyUsageSavings.put("perDiffenergyUseSavingsYoY", perDiffenergyUseSavingsYoY);
		  yoyUsageSavings.put("siteEstimatedYOYUsageSavingsPercentage", siteEstimatedYOYUsageSavingsPercentage);
		  yoyUsageSavings.put("siteUIServicesenergyUsePrevYear", siteUIServicesenergyUsePrevYear);
		  yoyUsageSavings.put("siteActualYOYUsageSavingsPercentage", siteActualYOYUsageSavingsPercentage);
		  objExcelUtils.writeIntoExcel(filename, yoyUsageSavings);
		  

	}
	
	public void lifeTimeCostSavings(String siteSourceKey, String siteName, String timePeriod,
			HashMap<String, Object> energyuse, HashMap<String, Object> validatelog) throws  IOException{
		String filename =siteSourceKey+"lifeTimeCostSavings"+timePeriod; 
		  double siteEstimatedlifetimeCostSavings = Double.valueOf(validatelog.get("site expected cost Lifetime savings ").toString());
		  double siteActuallifetimeCostSavings = Double.valueOf(validatelog.get("site actual cost Lifetime savings ").toString());
		  double siteEstimatedlifetimeCostSavingsPercentage = Double.valueOf(validatelog.get("site expected cost Lifetime % ").toString());
		  double siteActuallifetimeCostSavingsPercentage =Double.valueOf(validatelog.get("site actual cost Lifetime % ").toString());
		  System.out.println("siteEstimatedlifetimeCostSavings is "+siteEstimatedlifetimeCostSavings);
		  System.out.println("siteActuallifetimeCostSavings is"+siteActuallifetimeCostSavings);
		  System.out.println("siteEstimatedlifetimeCostSavingsPercentage is"+siteEstimatedlifetimeCostSavingsPercentage);
		  System.out.println("siteActuallifetimeCostSavingsPercentage is"+siteActuallifetimeCostSavingsPercentage);
		  double siteUIServicesenergySpendSavingsLifetime = Double.valueOf(energyuse.get("energySpendSavingsLifetime").toString());
		  double perDiffenergySpendSavingsLifetime=0;
		  if (siteEstimatedlifetimeCostSavings > 0 ) {
		    perDiffenergySpendSavingsLifetime = siteEstimatedlifetimeCostSavings >0 ? ((siteUIServicesenergySpendSavingsLifetime - siteEstimatedlifetimeCostSavings)/siteEstimatedlifetimeCostSavings) * 100 : 0;
		    System.out.println("siteUIServicesenergySpendSavingsLifetime is"+siteUIServicesenergySpendSavingsLifetime+" and Percentage Diff is "+perDiffenergySpendSavingsLifetime);
		    }
		  double siteUIServicesenergySpendBaseline = Double.valueOf(energyuse.get("energySpendBaseline").toString());
		  System.out.println("siteUIServicesenergySpendBaseline is"+ siteUIServicesenergySpendBaseline);
		  lifeTimeCostSavings.put("siteName", siteName);
		  lifeTimeCostSavings.put("testDate", timePeriod);
		  lifeTimeCostSavings.put("siteSourceKey", siteSourceKey);
		  lifeTimeCostSavings.put("siteEstimatedlifetimeCostSavings", siteEstimatedlifetimeCostSavings);
		  lifeTimeCostSavings.put("siteActuallifetimeCostSavings", siteActuallifetimeCostSavings);
		  lifeTimeCostSavings.put("siteUIServicesenergySpendSavingsLifetime", siteUIServicesenergySpendSavingsLifetime);
		  lifeTimeCostSavings.put("perDiffenergySpendSavingsLifetime", perDiffenergySpendSavingsLifetime);
		  lifeTimeCostSavings.put("siteEstimatedlifetimeCostSavingsPercentage", siteEstimatedlifetimeCostSavingsPercentage);
		  lifeTimeCostSavings.put("siteUIServicesenergySpendBaseline", siteUIServicesenergySpendBaseline);
		    objExcelUtils.writeIntoExcel(filename, lifeTimeCostSavings);

		  



	}
	public void yoyCostSavings(String siteSourceKey, String siteName, String timePeriod,
			HashMap<String, Object> energyuse, HashMap<String, Object> validatelog) throws  IOException{
		String filename =siteSourceKey+"YoYCostSavings"+timePeriod; 

		  double siteEstimatedYOYCostSavings = Double.valueOf(validatelog.get("site expected cost YOY savings ").toString()); 
		  double siteActualYOYCostSavings = Double.valueOf(validatelog.get("site actual cost YOY savings ").toString());
		  double siteEstimatedYOYCostSavingsPercentage = Double.valueOf( validatelog.get("site expected cost YOY % ").toString());
		  double siteActualYOYCostSavingsPercentage = Double.valueOf( validatelog.get("site actual cost YOY % ").toString());
		  System.out.println("siteEstimatedYOYCostSavings is"+siteEstimatedYOYCostSavings); 
		  System.out.println("siteActualYOYCostSavings is"+siteActualYOYCostSavings); 
		  System.out.println("siteEstimatedYOYCostSavingsPercentage is"+siteEstimatedYOYCostSavingsPercentage); 
		  System.out.println("siteActualYOYCostSavingsPercentage is"+siteActualYOYCostSavingsPercentage); 
		   double siteUIServicesenergySpendSavingsYoY =Double.valueOf(energyuse.get("energySpendSavingsYoY").toString());
		  
		  double perDiffenergySpendSavingsYoY=0;
		  if (siteEstimatedYOYCostSavings > 0){ 	
		 	perDiffenergySpendSavingsYoY = siteEstimatedYOYCostSavings >0?((siteUIServicesenergySpendSavingsYoY - siteEstimatedYOYCostSavings)/siteEstimatedYOYCostSavings) * 100 : 0;
		 	System.out.println("siteUIServicesenergySpendSavingsYoY is"+siteUIServicesenergySpendSavingsYoY+" and Percentage Diff is"+perDiffenergySpendSavingsYoY);
		    }
		  double energySpendPrevYear = Double.valueOf(energyuse.get("energySpendPrevYear").toString());
		  System.out.println("energySpendPrevYear is "+energySpendPrevYear);  
		  yoyCostSavings.put("siteName", siteName);
		  yoyCostSavings.put("testDate", timePeriod);
		  yoyCostSavings.put("siteSourceKey", siteSourceKey);
		  yoyCostSavings.put("siteEstimatedYOYCostSavings", siteEstimatedYOYCostSavings);
		  yoyCostSavings.put("siteActualYOYCostSavings", siteActualYOYCostSavings);
		  yoyCostSavings.put("siteUIServicesenergySpendSavingsYoY", siteUIServicesenergySpendSavingsYoY);
		  yoyCostSavings.put("perDiffenergySpendSavingsYoY", perDiffenergySpendSavingsYoY);
		  yoyCostSavings.put("siteEstimatedYOYCostSavingsPercentage", siteEstimatedYOYCostSavingsPercentage);
		  yoyCostSavings.put("siteActualYOYCostSavingsPercentage", siteActualYOYCostSavingsPercentage);
		  yoyCostSavings.put("energySpendPrevYear", energySpendPrevYear);
		  objExcelUtils.writeIntoExcel(filename, yoyCostSavings);
		  

		  


	}

	public URI appendURI(String url, Map<String, String> params) throws URISyntaxException {
		URI uri = null;

		uri = new URI(url);

		return appendURI(uri, params);
	}

	public URI appendURI(URI uri, Map<String, String> params) {

		Object[] keys = params.entrySet().toArray();
		for (Object appendQuery : keys) {
			String query = uri.getQuery();
			// System.out.println("query : " + query);
			if (query == null) {
				query = appendQuery.toString();
			} else {
				query += "&" + appendQuery;
			}
			try {
				uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		return uri;
	}
	
	 public static String[] separateDateAndTime(Date date) {
	        String[] result = new String[2];

	        SimpleDateFormat formatWithoutTime = new SimpleDateFormat("YYYYMMDD");
	        //SimpleDateFormat formatJustTime = new SimpleDateFormat(DateAndTimeUtil.TIME_FORMAT);

	        result[0] = formatWithoutTime.format(date);
	        

	        return result;
	    }

}
