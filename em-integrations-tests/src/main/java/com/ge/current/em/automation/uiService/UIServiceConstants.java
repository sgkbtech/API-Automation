package com.ge.current.em.automation.uiService;

public interface UIServiceConstants {

	String DAILY = "Daily";
	String HOURLY = "Hourly";
	String ENTERPRISE_SOURCE_KEY = "em.Enterprise.enterpriseSourceKey";
	String SITE_SOURCE_KEY = "em.Enterprise.siteSourceKey";
	String TIME_ZONE = "em.Submeter.timezone";
	
	String eventBucketSearchKpi = "kpi*";
	String eventBucketLoadTypeSearchKpi = "kpi.%s.LT*";
	String costBucketLoadTypeSearchKpi = "cost.%s.loadtype*";
	String eventBucketSearchCost = "cost*";
	String eventBucket = "event_bucket";
	String dailyAggregation = "DailyAggregation";
	
	String measures_aggrkWh = "measures_aggrkWh";
	String energyUse = "energyUse";
	String measures_aggrCOST = "measures_aggrCOST";
	String energySpend = "energySpend";
	String measures_aggrYOY = "measures_aggrYOY";
	String energySpendSavingsYoY = "energySpendSavingsYoY";
	String measures_aggrLIFE = "measures_aggrLIFE";
	String energySpendSavingsLifetime = "energySpendSavingsLifetime";
	String measures_maxzoneElecMeterPowerSensor = "measures_maxzoneElecMeterPowerSensor";
	String peakDemand = "peakDemand";
	
	String measures_aggrYOYkWhSavings = "measures_aggrYOYkWhSavings";
	String measures_aggrLifeTimekWhSavings = "measures_aggrLifeTimekWhSavings";
	
	String ext_propsSqFt = "ext_propsSqFt";
	String ext_propsName = "ext_propsName";
	String resrc_uid = "resrc_uid";
	String resrc_type = "resrc_type";
	String event_ts = "event_ts";
	String yyyymmdd = "yyyymmdd";
	String hour = "hour";
	String yyyymm = "yyyymm";
	String day = "day";
	String sqft = "sqft";
	String enterprise_uid = "enterprise_uid";
	String measures_aggrOffPeakkWh = "measures_aggrOffPeakkWh";
	String measures_aggrMidPeakkWh = "measures_aggrMidPeakkWh";
	String measures_aggrPeakkWh = "measures_aggrPeakkWh";
	String measures_aggrOccupiedkWh = "measures_aggrOccupiedkWh";
	String measures_aggrUnOccupiedkWh = "measures_aggrUnOccupiedkWh";
	String severity = "severity";
	String category = "category";
	String duration = "duration";
	String site_name = "site_name";
	String ext_propssite_name = "ext_propssite_name";
	String ext_propsprofileName = "ext_propsprofileName";
	String Smart = "Smart";
	String P1 = "P1";
	String P2 = "P2";
	String P3 = "P3";
	String P4 = "P4";
}
