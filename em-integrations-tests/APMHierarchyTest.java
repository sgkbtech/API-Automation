package com.ge.current.em.automation.apm.hierarchytesting;

import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.ge.current.em.APMJsonUtility;
import com.ge.current.em.automation.provider.APMDataProvider;
import com.ge.current.em.automation.util.APMTestUtil;
import com.ge.current.em.utils.APMChildrenType;
import com.ge.current.em.utils.APMParentType;
import com.opencsv.CSVWriter;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class APMHierarchyTest extends APMTestUtil {

	private static final Log logger = LogFactory.getLog(APMHierarchyTest.class);
	private static final String JSON_FILE_PATH = "src/main/resources/test-suite/data/apmhierarchy/";
	public static final String APM_SERVICE_URL = "apm_service_url";
	public static final String ENTERPRISE_PATTERN = "ENTERPRISE";
	public static final String ASSET_KEY_PATTERN = "ASSET";
	public static final String REGION_KEY_PATTERN = "REGION";
	public static final String SEGMENT_KEY_PATTERN = "SEGMENT";
	public static final String SITE_KEY_PATTERN = "SITE";
	private Map<String, String> TAG_TYPES = new HashMap<String, String>();
	
	private String ASSET_MODEL_TYPE = "";
	private String ROOT_ENTERPRISE = "";
	private String Non_ROOT_ENTERPRISE = "";
	private String NR_UNDER_NRENTERPRISE = "";
	private String REGION = "";
	private String SITE = "";
	private String SEGMENT_KEY = "";
	private String ASSET_KEY = "";
	private String SEGMENT_UNDER_SEGMENT = "";
	private String ASSET_TYPE = "";
	private String GATEWAY_ASSET = "";


	/**
	 * Creating Root Enterprise
	 * 
	 * @throws Throwable
	 */
	@Test
	public void enterpriseCanExistWithoutAParent() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises";
		String enterprisedetails = readPayloadFile(JSON_FILE_PATH + "CreateRootEnterprise.json", p);
		Response response = RestAssured.given().contentType("application/json").body(enterprisedetails)
				.header("Authorization", AUTH_TOKEN).expect().statusCode(HttpStatus.CREATED.value()).when().post(uri)
				.then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			writeCreatedElement(srcKey, ENTERPRISE_PATTERN);
			ROOT_ENTERPRISE = srcKey;
			JSON_PARAM_MAP.put("apm.ROOT_ENTERPRISE", ROOT_ENTERPRISE);
			JSON_PARAM_MAP.put("csv.EnterpriseName", e.get("name").toString());
			assertTrue("Asset source key pattern not valid for - " + srcKey, srcKey.startsWith(ENTERPRISE_PATTERN));
		}
	}

	/**
	 * Creating Type
	 * 
	 * @throws Exception
	 */
	@Test(dependsOnMethods = { "enterpriseCanExistWithoutAParent" })
	public void createSegmentTypeUnderRootEnterprise() throws Exception {
		String uri = getProperty(APM_SERVICE_URL) + "/types";
		String body = readPayloadFile(JSON_FILE_PATH + "Segment_Type.json", p);
		logger.info(body);
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE).when().header("Authorization", AUTH_TOKEN)
				.post(uri).then().extract().response();
		String jsonStringST = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		List<HashMap<String, Object>> list1 = JsonPath.from(jsonStringST).get();
		for (HashMap<String, Object> e : list1) {
			String sourceKey = e.get("sourceKey").toString();
			JSON_PARAM_MAP.put("apm.segment.segmentType", sourceKey);
			writeCreatedElement(sourceKey, "SEGMENT_TYPE");
		}

	}

	/**
	 * Creating Non Root Enterprise
	 * 
	 * @throws Throwable
	 */
	@Test(priority = 1, dependsOnMethods = { "enterpriseCanExistWithoutAParent" })
	public void enterpriseParentCanBeRootEnterprise() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises";
		String enterprisedetails = readPayloadFile(JSON_FILE_PATH + "CreateNonRootEnterprise.json", p);
		Response response = RestAssured.given().contentType("application/json").body(enterprisedetails)
				.header("Authorization", AUTH_TOKEN).expect().statusCode(HttpStatus.CREATED.value()).when().post(uri)
				.then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForEnterprise(srcKey, parentsrcKey, parenttype);
			logger.info("sourceKey - " + srcKey);
			writeCreatedElement(srcKey, ENTERPRISE_PATTERN);
			Non_ROOT_ENTERPRISE = srcKey;
			JSON_PARAM_MAP.put("apm.NonROOT_ENTERPRISE", Non_ROOT_ENTERPRISE);
			JSON_PARAM_MAP.put("csv.EnterpriseName", e.get("name").toString());
			assertTrue("Asset source key pattern not valid for - " + srcKey, srcKey.startsWith(ENTERPRISE_PATTERN));
		}
	}

	/**
	 * 
	 * Creating Non Root Enterprise under non-Root Enterprise
	 * 
	 * @throws Throwable
	 */
	@Test(priority = 1, dependsOnMethods = { "enterpriseParentCanBeRootEnterprise" })
	public void enterpriseParentCanBeEnterprise() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises";
		String enterprisedetails = readPayloadFile(JSON_FILE_PATH + "CreateNRUnderNREnterprise.json", p);
		Response response = RestAssured.given().contentType("application/json").body(enterprisedetails)
				.header("Authorization", AUTH_TOKEN).expect().statusCode(HttpStatus.CREATED.value()).when().post(uri)
				.then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForEnterprise(srcKey, parentsrcKey, parenttype);
			logger.info("sourceKey - " + srcKey);
			writeCreatedElement(srcKey, ENTERPRISE_PATTERN);
			NR_UNDER_NRENTERPRISE = srcKey;
			JSON_PARAM_MAP.put("apm.NRUnderNREnterprise", NR_UNDER_NRENTERPRISE);
			JSON_PARAM_MAP.put("csv.EnterpriseName", e.get("name").toString());
			assertTrue("Asset source key pattern not valid for - " + srcKey, srcKey.startsWith(ENTERPRISE_PATTERN));
		}
	}

	/**
	 * Creating Region under Root Enterprise
	 * 
	 * @throws Throwable
	 */
	@Test(priority = 1, dependsOnMethods = { "enterpriseCanExistWithoutAParent" })
	public void regionParentCanBeRootEnterprise() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/regions";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateRegionUnderRootEnterprise.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String regionSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForRegion(regionSourceKey, parentsrcKey, parenttype);
			REGION = regionSourceKey;
			logger.info("sourceKey - " + regionSourceKey);
			writeCreatedElement(regionSourceKey, REGION_KEY_PATTERN);
			JSON_PARAM_MAP.put("apm.RegionUnderEnterprise", REGION);
			assertTrue("Asset source key pattern not valid for - " + regionSourceKey,
					regionSourceKey.startsWith(REGION_KEY_PATTERN));
		}

	}

	/**
	 * Creating Region under Non-Root Enterprise
	 * 
	 * @throws Throwable
	 */
	@Test(priority = 1, dependsOnMethods = { "enterpriseParentCanBeRootEnterprise" })
	public void regionParentCanBeEnterprise() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/regions";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateRegionUnderNREnterprise.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String regionSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForRegion(regionSourceKey, parentsrcKey, parenttype);
			REGION = regionSourceKey;
			logger.info("sourceKey - " + regionSourceKey);
			writeCreatedElement(regionSourceKey, REGION_KEY_PATTERN);
			JSON_PARAM_MAP.put("apm.RegionUnderNREnterprise", REGION);
			assertTrue("Asset source key pattern not valid for - " + regionSourceKey,
					regionSourceKey.startsWith(REGION_KEY_PATTERN));
		}

	}

	/**
	 * Creating Region under Region
	 * 
	 * @throws Throwable
	 */
	@Test(priority = 1, dependsOnMethods = {"regionParentCanBeEnterprise" })
	public void regionParentCanBeRegion() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/regions";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateRegionUnderRegion.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String regionSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForRegion(regionSourceKey, parentsrcKey, parenttype);
			REGION = regionSourceKey;
			logger.info("sourceKey - " + regionSourceKey);
			writeCreatedElement(regionSourceKey, REGION_KEY_PATTERN);
			JSON_PARAM_MAP.put("apm.RegionUnderRegion", REGION);
			assertTrue("Asset source key pattern not valid for - " + regionSourceKey,
					regionSourceKey.startsWith(REGION_KEY_PATTERN));
		}

	}

	/**
	 * Creating Site under Root Enterprise
	 * 
	 * @throws Throwable
	 */
	@Test(priority = 1, dependsOnMethods = { "enterpriseCanExistWithoutAParent" })
	public void siteParentCanBeRootEnterprise() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSiteUnderRootEnterprise.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForSite(siteSourceKey, parentsrcKey, parenttype);
			logger.info("sourceKey - " + siteSourceKey);
			SITE = siteSourceKey;
			writeCreatedElement(siteSourceKey, SITE_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + siteSourceKey,
					siteSourceKey.startsWith(SITE_KEY_PATTERN));
			JSON_PARAM_MAP.put("apm.siteunderRootEnterprise", SITE);
			JSON_PARAM_MAP.put("csv.siteName", e.get("name").toString());

		}
	}

	@Test(priority = 1, dependsOnMethods = { "enterpriseParentCanBeRootEnterprise" })
	public void siteParentCanBeOfEnterprise() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSiteUnderNREnterprise.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForSite(siteSourceKey, parentsrcKey, parenttype);
			logger.info("sourceKey - " + siteSourceKey);
			SITE = siteSourceKey;
			writeCreatedElement(siteSourceKey, SITE_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + siteSourceKey,
					siteSourceKey.startsWith(SITE_KEY_PATTERN));
			JSON_PARAM_MAP.put("apm.siteunderNREnterprise", SITE);
			JSON_PARAM_MAP.put("csv.siteName", e.get("name").toString());

		}
	}

	@Test(priority = 1, dependsOnMethods = { "regionParentCanBeEnterprise" })
	public void siteParentCanBeRegion() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSiteUnderRegion.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + siteSourceKey);
			SITE = siteSourceKey;
			writeCreatedElement(siteSourceKey, SITE_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + siteSourceKey,
					siteSourceKey.startsWith(SITE_KEY_PATTERN));
			JSON_PARAM_MAP.put("apm.siteunderregion", SITE);
			JSON_PARAM_MAP.put("csv.siteNameunderregion", e.get("name").toString());

		}
	}

	@Test(priority = 1, dependsOnMethods = { "siteParentCanBeRegion" })
	public void siteParentCanBeSite() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSiteUnderSite.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForSite(siteSourceKey, parentsrcKey, parenttype);
			logger.info("sourceKey - " + siteSourceKey);
			SITE = siteSourceKey;
			writeCreatedElement(siteSourceKey, SITE_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + siteSourceKey,
					siteSourceKey.startsWith(SITE_KEY_PATTERN));
			JSON_PARAM_MAP.put("apm.siteundersite", SITE);
			JSON_PARAM_MAP.put("csv.siteNameundersite", e.get("name").toString());

		}
	}

	@Test(priority = 1, dependsOnMethods = { "regionParentCanBeRegion" })
	public void siteParentCanBeRegionUnderRegion() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSiteUnderRegionUnderRegion.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForSite(siteSourceKey, parentsrcKey, parenttype);
			logger.info("sourceKey - " + siteSourceKey);
			SITE = siteSourceKey;
			writeCreatedElement(siteSourceKey, SITE_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + siteSourceKey,
					siteSourceKey.startsWith(SITE_KEY_PATTERN));
			JSON_PARAM_MAP.put("apm.siteunderRegionUnderRegion", SITE);
			JSON_PARAM_MAP.put("csv.siteNameunderRegionUnderRegion", e.get("name").toString());

		}
	}

	@Test(priority = 1, dependsOnMethods = { "siteParentCanBeRegionUnderRegion", "createSegmentTypeUnderRootEnterprise" })
	public void segmentParentCanbeSite() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSegmentUnderSite.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String segmentSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForSegment(segmentSourceKey, parentsrcKey, parenttype);
			logger.info("sourceKey - " + segmentSourceKey);
			writeCreatedElement(segmentSourceKey, SEGMENT_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + segmentSourceKey,
					segmentSourceKey.startsWith(SEGMENT_KEY_PATTERN));
			SEGMENT_KEY = segmentSourceKey;
			JSON_PARAM_MAP.put("apm.SegmentunderSite", SEGMENT_KEY);
		}
	}

	@Test(priority = 1, dependsOnMethods = { "segmentParentCanbeSite", "createSegmentTypeUnderRootEnterprise" })
	public void segmentParentCanBeSegment() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSegmentUnderSegment.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String segmentSourceKey = e.get("sourceKey").toString();
			String parentsrcKey = e.get("parentSourceKey").toString();
			String parenttype = e.get("parentClassificationCode").toString();
			testParentTypeForSegment(segmentSourceKey, parentsrcKey, parenttype);
			logger.info("sourceKey - " + segmentSourceKey);
			writeCreatedElement(segmentSourceKey, SEGMENT_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + segmentSourceKey,
					segmentSourceKey.startsWith(SEGMENT_KEY_PATTERN));
			SEGMENT_UNDER_SEGMENT = segmentSourceKey;
			JSON_PARAM_MAP.put("apm.SegmentunderSegment", SEGMENT_UNDER_SEGMENT);
		}
	}

	@Test(priority = 1, dependsOnMethods = { "enterpriseCanExistWithoutAParent", "createSegmentTypeUnderRootEnterprise", "segmentParentCanBeSegment" })
	public void createHirearchyTest() throws Throwable {
		enterpriseParentCanBeRootEnterprise();
		regionParentCanBeEnterprise();
		siteParentCanBeRegion();
		segmentParentCanbeSite();
	}
	
	@Test(priority = 1, dependsOnMethods = {"postEnterprisewithvalidinputs" }, dataProvider = "TagTypeJson", dataProviderClass = APMDataProvider.class)
	public void createTagTypes(String filename) throws Exception {
		Response response = createType(filename);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String sourceKey = e.get("sourceKey").toString();
			String type = e.get("type").toString();
			ASSET_TYPE = sourceKey;
			// logger.info("sourceKey tagtype --- ---- " + "apm." +
			// e.get("name").toString() + ".tagtype == " + sourceKey);
			writeCreatedElement(sourceKey, type);
			assertTrue("tag type source key pattern not valid for - " + sourceKey, sourceKey.startsWith(type));
			TAG_TYPES.put("apm." + e.get("name").toString() + ".tagtype", sourceKey);
		}
		JSON_PARAM_MAP.putAll(TAG_TYPES);
		}
	
	@Test(priority = 1, dependsOnMethods = { "postEnterprisewithvalidinputs", "createTagTypes" })
	public void creatingSiteswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites";
		JSON_PARAM_MAP.put("apm.site.parentSourceKey", ROOT_ENTERPRISE);
		JSON_PARAM_MAP.put("apm.site.parentType", "ENTERPRISE");
		String body = readPayloadFile(JSON_FILE_PATH + "Sites.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + siteSourceKey);
			SITE = siteSourceKey;
			writeCreatedElement(siteSourceKey, SITE_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + siteSourceKey,
					siteSourceKey.startsWith(SITE_KEY_PATTERN));
			JSON_PARAM_MAP.put("apm.enterprise.invalidparent", SITE);
			JSON_PARAM_MAP.put("csv.siteName", e.get("name").toString());
			JSON_PARAM_MAP.put("apm.enterprise.invalidparentType", "SITE");
		}
	}
	
	@Test(priority = 1, dependsOnMethods = { "creatingSiteswithvalidinputs", "createTagTypes" })
	public void createAssetTypeValidInputs() throws Exception {
		String uri = getProperty(APM_SERVICE_URL) + "/types";
		JSON_PARAM_MAP.put("apm.asset.siteSourceKey", SITE);
		String value = readPayloadFile(JSON_FILE_PATH + "Tags_submeter.json", p);
		JSON_PARAM_MAP.put("apm.tag.value", JSONObject.quote(value));
		// logger.info("apm.tag.value ---#
		// "+JSON_PARAM_MAP.get("apm.tag.value"));
		String body = readPayloadFile(JSON_FILE_PATH + "Assets_Type_Submeter.json", p).replaceAll("\"\"", "\"");
		// logger.info("Assets_Type_Submeter ---# "+body);
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE).when().header("Authorization", AUTH_TOKEN)
				.post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String sourceKey = e.get("sourceKey").toString();
			String type = e.get("type").toString();
			ASSET_TYPE = sourceKey;
			logger.info("sourceKey - " + sourceKey);
			writeCreatedElement(sourceKey, type);
			assertTrue("Asset type source key pattern not valid for - " + sourceKey, sourceKey.startsWith(type));
		}
	}
	
	@Test(priority = 1, dependsOnMethods = { "createAssetTypeValidInputs" })
	public void createAssetModelTypeValidInputs() throws Exception {
		String uri = getProperty(APM_SERVICE_URL) + "/types";
		JSON_PARAM_MAP.put("apm.assetModel.parent", ASSET_TYPE);
		String body = readPayloadFile(JSON_FILE_PATH + "Assets_Model_Type_Submeter.json", p);
		logger.info(body);
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE).when().header("Authorization", AUTH_TOKEN)
				.post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String sourceKey = e.get("sourceKey").toString();
			ASSET_MODEL_TYPE = sourceKey;
			String type = e.get("type").toString();
			logger.info("sourceKey - " + sourceKey);
			writeCreatedElement(sourceKey, type);
			assertTrue("Asset model type source key pattern not valid for - " + sourceKey, sourceKey.startsWith(type));
		}
	}
	
	@Test
	public void postEnterprisewithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises";
		String enterprisedetails = readPayloadFile(JSON_FILE_PATH + "Enterprise.json", p);
		Response response = RestAssured.given().contentType("application/json").body(enterprisedetails)
				.header("Authorization", AUTH_TOKEN).post(uri).then().log().ifError().assertThat()
				.statusCode(HttpStatus.CREATED.value()).extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			writeCreatedElement(srcKey, ENTERPRISE_PATTERN);
			ROOT_ENTERPRISE = srcKey;
			JSON_PARAM_MAP.put("apm.ROOT_ENTERPRISE", ROOT_ENTERPRISE);
			JSON_PARAM_MAP.put("csv.EnterpriseName", e.get("name").toString());
			assertTrue("Asset source key pattern not valid for - " + srcKey, srcKey.startsWith(ENTERPRISE_PATTERN));
		}
	}

	@Test(priority = 1, dependsOnMethods = { "createAssetModelTypeValidInputs" })
	public void creatingSiteGatewaywithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites/" + SITE + "/gateways";
		String body = readPayloadFile(JSON_FILE_PATH + "SiteGateway.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String sourceKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + sourceKey);
			GATEWAY_ASSET = sourceKey;
			writeCreatedElement(sourceKey, "Gateway_asset");
			assertTrue("Asset source key pattern not valid for - " + sourceKey,
					sourceKey.startsWith(ASSET_KEY_PATTERN));
		}

	}
	
	@Test(priority = 1, dependsOnMethods = { "creatingSiteswithvalidinputs" })
	public void creatingSegmentwithvalidinputs() throws Throwable {
		Response responseSegType = createType("Segment_Type.json");
		String jsonStringST = responseSegType.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), responseSegType),
				responseSegType.getStatusCode() == HttpStatus.CREATED.value());
		List<HashMap<String, Object>> list1 = JsonPath.from(jsonStringST).get();
		for (HashMap<String, Object> e : list1) {
			String sourceKey = e.get("sourceKey").toString();
			JSON_PARAM_MAP.put("apm.segment.segmentType", sourceKey);
			writeCreatedElement(sourceKey, "SEGMENT_TYPE");
		}
		String uri = getProperty(APM_SERVICE_URL) + "/segments";
		JSON_PARAM_MAP.put("apm.segment.parent", SITE);
		JSON_PARAM_MAP.put("apm.segment.parentType", "SITE");
		String body = readPayloadFile(JSON_FILE_PATH + "Segment.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String segmentSourceKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + segmentSourceKey);
			writeCreatedElement(segmentSourceKey, SEGMENT_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + segmentSourceKey,
					segmentSourceKey.startsWith(SEGMENT_KEY_PATTERN));
			SEGMENT_KEY = segmentSourceKey;
		}
	}
	
	@Test(priority = 1, dependsOnMethods = { "creatingSiteGatewaywithvalidinputs","creatingSegmentwithvalidinputs" })
	 public void creatingAssetUnderSegment() throws Throwable {
	  // pass parent as segment  - take creatingAssetwithvalidinputs as sample
		String uri = getProperty(APM_SERVICE_URL) + "/assets";
		JSON_PARAM_MAP.put("apm.asset.segmentSourceKey", SEGMENT_KEY);
		JSON_PARAM_MAP.put("apm.asset.gatewaySourceKey", GATEWAY_ASSET);
		String body = readPayloadFile(JSON_FILE_PATH + "Asset_Under_Segment.json", p);
		logger.info(SEGMENT_KEY);
		logger.info(body);
		
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
	    assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response Time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
		String assetSourceKey = ((HashMap<String, String>) e.get("identifier")).get("sourceKey");
			logger.info("sourceKey - " + assetSourceKey);
			writeCreatedElement(assetSourceKey, ASSET_KEY_PATTERN);
			assertTrue("Asset source key pattern not valid for - " + assetSourceKey,
					assetSourceKey.startsWith(ASSET_KEY_PATTERN));
			ASSET_KEY = assetSourceKey;
			JSON_PARAM_MAP.put("csv.assetName", e.get("name").toString());
			writeToCSV();
		}
		
	 }
	
	@Test(priority = 1, dependsOnMethods = { "createTagTypes" })
	public void postEnterpriseTagwithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises/" + ROOT_ENTERPRISE + "/tags";
		String enterprisedetails = readPayloadFile(JSON_FILE_PATH + "EnterpriseTag.json", p);
		Response response = RestAssured.given().contentType("application/json").body(enterprisedetails).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get("tags");
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			writeCreatedElement(srcKey, "TAG");
			assertTrue("Tag source key pattern not valid for - " + srcKey, srcKey.startsWith("TAG_"));
		}
	}

	@Test(priority = 1, dependsOnMethods = { "createTagTypes" })
	public void creatingSitetagswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites/" + SITE + "/tags";
		String body = readPayloadFile(JSON_FILE_PATH + "SiteTag.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get("tags");
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			writeCreatedElement(srcKey, "TAG");
			assertTrue("Tag source key pattern not valid for - " + srcKey, srcKey.startsWith("TAG_"));
		}
	}
	
	@Test(priority = 1, dependsOnMethods = { "createTagTypes", "creatingSegmentwithvalidinputs" })
	public void creatingSegmentTagwithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments/" + SEGMENT_KEY + "/tags";
		String body = readPayloadFile(JSON_FILE_PATH + "SegmentTag.json", p);
		Response response = RestAssured.given().contentType("application/json").body(body).when()
				.header("Authorization", AUTH_TOKEN).post(uri).then().extract().response();
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get("tags");
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			writeCreatedElement(srcKey, "TAG");
			assertTrue("Tag source key pattern not valid for - " + srcKey, srcKey.startsWith("TAG_"));
		}
	}

  @AfterTest
	public void checkHierarchy() {
		verifyParent("ENTERPRISE", getChildren(ROOT_ENTERPRISE, "ENTERPRISE"));
	}
	
  private void writeToCSV() throws IOException {
		String[] rowData = new String[15];
		CSVWriter csvWriter = new CSVWriter(new FileWriter("src/main/resources/SiteDefinitionFiles/Assets.csv", true),
				',', CSVWriter.NO_QUOTE_CHARACTER);
		rowData[0] = JSON_PARAM_MAP.get("csv.siteName");
		rowData[1] = JSON_PARAM_MAP.get("csv.assetName");
		rowData[2] = ASSET_KEY;
		rowData[3] = "Submeter";
		rowData[4] = ASSET_TYPE;
		rowData[5] = "Submeter Model";
		rowData[6] = ASSET_MODEL_TYPE;
		rowData[7] = "SubMeter.csv";
		rowData[8] = "PST";
		rowData[9] = "1";
		rowData[10] = ROOT_ENTERPRISE;
		rowData[11] = "1";
		rowData[12] = "FALSE";
		rowData[13] = "America/Los_Angeles";
		rowData[14] = JSON_PARAM_MAP.get("csv.EnterpriseName") + ".csv";
		csvWriter.writeNext(rowData);
		csvWriter.close();

	}
	private Response createType(String fileName) throws Exception {
		String uri = getProperty(APM_SERVICE_URL) + "/types";
		String body = readPayloadFile(JSON_FILE_PATH + fileName, p);
		logger.info(body);
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE).when().header("Authorization", AUTH_TOKEN)
				.post(uri).then().extract().response();
		return response;
	}

	private List<Map<String, Object>> getChildren(String sourcekey, String classification) {
		String uri = getProperty(APM_SERVICE_URL) + "/" + classification.toLowerCase() + "s/" + sourcekey
				+ "/children?fetchProperties=false";
		Response response = RestAssured.given().contentType("application/json").when()
				.header("Authorization", AUTH_TOKEN).get(uri).then().extract().response();
		JsonPath jsonPath = response.jsonPath();
		List<Map<String, Object>> content = jsonPath.getList("content");
		return content;
	}

	private void verifyParent(String parenClassification, List<Map<String, Object>> children) {
		for (Map<String, Object> child : children) {
			String classificationCode = child.get("classificationCode").toString();
			String sourceKey = child.get("sourceKey").toString();
			String errorMessage = parenClassification + " -- " + classificationCode + " == " + sourceKey;
			if (parenClassification.equals("ENTERPRISE")) {
				assertTrue(errorMessage, APMChildrenType.ENTERPRISE.contains(classificationCode));
			} else if (parenClassification.equals("SITE")) {
				assertTrue(errorMessage, APMChildrenType.SITE.contains(classificationCode));
			} else if (parenClassification.equals("SEGMENT")) {
				assertTrue(errorMessage, APMChildrenType.SEGMENT.contains(classificationCode));
			} else if (parenClassification.equals("REGION")) {
				assertTrue(errorMessage, APMChildrenType.REGION.contains(classificationCode));
			}
			verifyParent(classificationCode, getChildren(sourceKey, classificationCode));
		}
	}
	
	//@Test
	public void dynamicHirearchyCreationTest() throws Throwable {
		List<Map<String, String>> listOfElements = readFromHierarchyJSON(JSON_FILE_PATH + "hierarchy.json");
		Map<String, String> sourceKeys = new HashMap<String, String>();
		for (Map<String, String> valueMap : listOfElements) {
			String parentType = valueMap.get("parentType");
			String parentId = valueMap.get("parentId");
			String type = valueMap.get("type");
			String id = valueMap.get("id");
			String sourceKey = "";
			String parentSourceKey = valueMap.get(parentId);
			boolean createCalled = false;
			if (parentType.isEmpty() && type.equals("ROOTENTERPRISE")) {
				JSON_PARAM_MAP.put("apm.newEnterprise", id + "_unixTime");
				enterpriseCanExistWithoutAParent();
				sourceKeys.put(id, ROOT_ENTERPRISE);
				createSegmentTypeUnderRootEnterprise();
				createCalled = true;
			}
			if (parentType.equals("ROOTENTERPRISE")) {
				if (type.equals("ENTERPRISE")) {
					JSON_PARAM_MAP.put("apm.newEnterprise", id + "_unixTime");
					enterpriseParentCanBeRootEnterprise();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.NonROOT_ENTERPRISE"));
					createCalled = true;
				}
				if (type.equals("REGION")) {
					JSON_PARAM_MAP.put("apm.region.name", id + "_unixTime");
					regionParentCanBeRootEnterprise();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.RegionUnderEnterprise"));
					createCalled = true;
				}
				if (type.equals("SITE")) {
					JSON_PARAM_MAP.put("apm.site.name", id + "_unixTime");
					siteParentCanBeRootEnterprise();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.siteunderRootEnterprise"));
					createCalled = true;
				}
			}

			if (parentType.equals("ENTERPRISE")) {
				if (type.equals("ENTERPRISE")) {
					JSON_PARAM_MAP.put("apm.newEnterprise", id + "_unixTime");
					JSON_PARAM_MAP.put(parentSourceKey, sourceKeys.get(parentId));
					enterpriseParentCanBeEnterprise();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.NRUnderNREnterprise"));
					createCalled = true;
				}
				if (type.equals("REGION")) {
					JSON_PARAM_MAP.put("apm.region.name", id + "_unixTime");
					JSON_PARAM_MAP.put(parentSourceKey, sourceKeys.get(parentId));
					regionParentCanBeEnterprise();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.RegionUnderNREnterprise"));
					createCalled = true;
				}
				if (type.equals("SITE")) {
					JSON_PARAM_MAP.put("apm.site.name", id + "_unixTime");
					JSON_PARAM_MAP.put(parentSourceKey, sourceKeys.get(parentId));
					siteParentCanBeOfEnterprise();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.siteunderNREnterprise"));
					createCalled = true;
				}
			}
			if (parentType.equals("REGION")) {
				if (type.equals("REGION")) {
					JSON_PARAM_MAP.put("apm.region.name", id + "_unixTime");
					JSON_PARAM_MAP.put(parentSourceKey, sourceKeys.get(parentId));
					regionParentCanBeRegion();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.siteunderRegionUnderRegion"));
					createCalled = true;
				}
				if (type.equals("SITE")) {
					JSON_PARAM_MAP.put("apm.site.name", id + "_unixTime");
					JSON_PARAM_MAP.put("apm.RegionUnderEnterprise", sourceKeys.get(parentId));
					siteParentCanBeRegion();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.siteunderregion"));
					createCalled = true;
				}
			}
			if (parentType.equals("SITE")) {
				if (type.equals("SITE")) {
					JSON_PARAM_MAP.put("apm.site.name", id + "_unixTime");
					JSON_PARAM_MAP.put(parentSourceKey, sourceKeys.get(parentId));
					siteParentCanBeSite();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.siteundersite"));
					createCalled = true;
				}
				if (type.equals("SEGMENT")) {
					JSON_PARAM_MAP.put("apm.segment.name", id + "_unixTime");
					JSON_PARAM_MAP.put(parentSourceKey, sourceKeys.get(parentId));
					segmentParentCanbeSite();
					sourceKeys.put(id, JSON_PARAM_MAP.get("apm.SegmentunderSite"));
					createCalled = true;
				}
			}
			if (parentType.equals("SEGMENT") && type.equals("SEGMENT")) {
				JSON_PARAM_MAP.put("apm.segment.name", id + "_unixTime");
				JSON_PARAM_MAP.put(parentSourceKey, sourceKeys.get(parentId));
				segmentParentCanBeSegment();
				sourceKeys.put(id, JSON_PARAM_MAP.get("apm.SegmentunderSegment"));
				createCalled = true;
			}
			assertTrue(type + " under  " + parentType + " is not valid scenario", createCalled);
			sourceKeys.put(id, sourceKey);
		}
	}

	private void testParentTypeForEnterprise(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for enterprise id : " + sourceKey,
				APMParentType.ENTERPRISE.contains(parentClassification));
	}

	private void testParentTypeForRegion(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for region id : " + sourceKey,
				APMParentType.REGION.contains(parentClassification));
	}

	private void testParentTypeForSite(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for site id : " + sourceKey,
				APMParentType.SITE.contains(parentClassification));
	}

	private void testParentTypeForSegment(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for segment id : " + sourceKey,
				APMParentType.SEGMENT.contains(parentClassification));
	}

	private List<Map<String, String>> readFromHierarchyJSON(String file) throws IOException, ParseException {
		String fileContent = (new APMJsonUtility()).readFromFile(file);
		JSONObject obj = new JSONObject(fileContent);
		logger.info(obj);
		JSONArray jsArr = new JSONArray();
		jsArr.put(obj);
		List<Map<String, String>> listElements = new ArrayList<Map<String, String>>();
		listElements.addAll(getChildrenHierarchy("","",jsArr));
		return listElements;
	}

	private List<Map<String, String>> getChildrenHierarchy(String parentId, String parentType, JSONArray children) {
		List<Map<String, String>> listElements = new ArrayList<Map<String, String>>();
		for(int i=0;i<children.length();i++){
			JSONObject child  = (JSONObject) children.get(i);
			String id = child.getString("id");
			String type = child.getString("type");
			Map<String,String> ret = new HashMap<String,String>();
			ret.put("parentId", parentId);
			ret.put("parentType", parentType);
			ret.put("id", id);
			ret.put("type", type);
			listElements.add(ret);
			JSONArray grandChildren = new JSONArray();
			if(child.has("children")){
				grandChildren = child.getJSONArray("children");
			}
			logger.info(id);
			listElements.addAll(getChildrenHierarchy(id,type,grandChildren));
		}
		return listElements;
	}

}
