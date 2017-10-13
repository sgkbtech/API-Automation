package com.ge.current.em.automation.provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.ge.current.em.automation.ui.UIFunctionalTest;
import com.ge.current.em.automation.ui.UIServicesTest;

public class UIDataProvider {
	private static final Log logger = LogFactory.getLog(UIServicesTest.class);
	private static long DAY = 24 * 60 * 60 * 1000;
	private static final String YYYYMMDD = "yyyyMMdd";

	@DataProvider(name = "peakDemand")
	public static Object[][] getPeakDemand(ITestContext context) throws IOException {
		return getEnterpriseEnergyData("peakDemand", context);
	}

	@DataProvider(name = "energyUse")
	public static Object[][] getEnergyUse(ITestContext context) throws IOException {
		return getEnterpriseEnergyData("energyUse", context);
	}
	
	@DataProvider(name = "energySpend")
	public static Object[][] getEnergySpend(ITestContext context) throws IOException {
		return getEnterpriseEnergyData("energySpend", context);
	}

	@DataProvider(name = "energyUseSavingsLifetime")
	public static Object[][] getEnergyUseSavingsLifetime(ITestContext context) throws IOException {
		return getEnterpriseEnergyData("energyUseSavingsLifetime", context);
	}
	
	@DataProvider(name = "energyUseSavingsYoY")
	public static Object[][] getEnergyUseSavingsYoY(ITestContext context) throws IOException {
		return getEnterpriseEnergyData("energyUseSavingsYoY", context);
	}
	
	
	private static Object[][] getEnterpriseEnergyData(String prop, ITestContext context) throws IOException {
		String timePeriod = Instant.now().minus(DAY).toString(DateTimeFormat.forPattern(YYYYMMDD));
		String url = context.getAttribute("ui_service") + "enterprises/"
				+ context.getAttribute("em.SoftJace.enterpriseSourceKey") + "/energy";
		UIFunctionalTest test = new UIFunctionalTest();
		Map<String, String> params = new HashMap<String, String>();
		//timePeriod = "20170309";
		params.put("timePeriod", timePeriod);
		logger.info(test.getResponse(url, params).jsonPath());
		List<Map<String, Object>> sites = test.getResponse(url, params).jsonPath().get("sites");
		Object[][] arr = new Object[sites.size()][3];
		int index = 0;
		for (Map<String, Object> site : sites) {
			arr[index][0] = site.get("siteSourceKey").toString();
			arr[index][1] = Float.valueOf(site.get(prop).toString());
			arr[index][2] = timePeriod;
			index++;
		}
		return arr;
	}

}