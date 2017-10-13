package com.ge.current.em.automation.apm.e2e;

import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import com.ge.current.em.APMJsonUtility;
import com.ge.current.em.automation.provider.APMDataProvider;
import com.ge.current.em.automation.util.APMTestUtil;
import com.opencsv.CSVWriter;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class APMPostTest extends APMTestUtil {

	private static final Log logger = LogFactory.getLog(APMPostTest.class);
	private static final String JSON_FILE_PATH = "src/main/resources/test-suite/data/apm/";
	private static final int ASSET_COUNT = 2;
	private static final int SITE_COUNT = 1;

	private Map<String, String> TAG_TYPES = new HashMap<String, String>();
	private String ROOT_ENTERPRISE = "";
	private String SITE = "";
	private String ASSET_TYPE = "";
	private String GATEWAY_ASSET = "";
	private String ASSET_KEY = "";
	private String SEGMENT_KEY = "";
	private String ASSET_MODEL_TYPE = "";

	@Test
	public void testPostEnterprisewithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises";
		String content = readPayloadFile(JSON_FILE_PATH + "Enterprise.json", getProperties());
		Response response = postRequest(uri, content);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			ROOT_ENTERPRISE = srcKey;
			JSON_PARAM_MAP.put("apm.ROOT_ENTERPRISE", ROOT_ENTERPRISE);
			JSON_PARAM_MAP.put("csv.EnterpriseName", e.get("name").toString());
		}
	}

	@Test(priority = 1, dependsOnMethods = { "createTagTypes" })
	public void testPostEnterpriseTagwithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises/" + ROOT_ENTERPRISE + "/tags";
		String content = readPayloadFile(JSON_FILE_PATH + "EnterpriseTag.json", getProperties());
		Response response = postRequest(uri, content);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get("tags");
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			assertTrue("Tag source key pattern not valid for - " + srcKey, srcKey.startsWith("TAG_"));
		}
	}

	@Test(priority = 1, dependsOnMethods = {
			"postEnterprisewithvalidinputs" }, dataProvider = "TagTypeJson", dataProviderClass = APMDataProvider.class)
	public void testCreateTagTypes(String filename) throws Exception {
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
			assertTrue("tag type source key pattern not valid for - " + sourceKey, sourceKey.startsWith(type));
			TAG_TYPES.put("apm." + e.get("name").toString() + ".tagtype", sourceKey);
		}
		JSON_PARAM_MAP.putAll(TAG_TYPES);
	}

	@Test(priority = 1, dependsOnMethods = { "creatingSiteswithvalidinputs", "createTagTypes" })
	public void testCreateAssetTypeValidInputs() throws Exception {
		String uri = getProperty(APM_SERVICE_URL) + "/types";
		JSON_PARAM_MAP.put("apm.asset.siteSourceKey", SITE);
		String value = readPayloadFile(JSON_FILE_PATH + "Tags_submeter.json", getProperties());
		JSON_PARAM_MAP.put("apm.tag.value", JSONObject.quote(value));
		// logger.info("apm.tag.value ---#
		// "+JSON_PARAM_MAP.get("apm.tag.value"));
		String body = readPayloadFile(JSON_FILE_PATH + "Assets_Type_Submeter.json", getProperties()).replaceAll("\"\"",
				"\"");
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
			assertTrue("Asset type source key pattern not valid for - " + sourceKey, sourceKey.startsWith(type));
		}
	}

	@Test(priority = 1, dependsOnMethods = { "createAssetTypeValidInputs" })
	public void testCreateAssetModelTypeValidInputs() throws Exception {
		String uri = getProperty(APM_SERVICE_URL) + "/types";
		JSON_PARAM_MAP.put("apm.assetModel.parent", ASSET_TYPE);
		String body = readPayloadFile(JSON_FILE_PATH + "Assets_Model_Type_Submeter.json", getProperties());
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
			assertTrue("Asset model type source key pattern not valid for - " + sourceKey, sourceKey.startsWith(type));
		}
	}

	@Test(priority = 1, dependsOnMethods = { "createAssetModelTypeValidInputs" })
	public void testCreatingGatewayAsset() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets";
		JSON_PARAM_MAP.put("apm.asset.siteSourceKey", SITE);
		String body = readPayloadFile(JSON_FILE_PATH + "Assets_Gateway.json", getProperties());
		logger.info(body);
		Response response = postRequest(uri, body);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String assetSourceKey = ((HashMap<String, String>) e.get("identifier")).get("sourceKey");
			GATEWAY_ASSET = assetSourceKey;
			logger.info("sourceKey - " + assetSourceKey);
			assertTrue("Asset source key pattern not valid for - " + assetSourceKey,
					assetSourceKey.startsWith(ASSET_KEY_PATTERN));
		}
	}

	@Test(priority = 1, dependsOnMethods = { "creatingSiteGatewaywithvalidinputs" })
	public void testCreatingAssetwithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets";
		JSON_PARAM_MAP.put("apm.asset.siteSourceKey", SITE);
		JSON_PARAM_MAP.put("apm.asset.gatewaySourceKey", GATEWAY_ASSET);
		String body = readPayloadFile(JSON_FILE_PATH + "Assets.json", getProperties());
		logger.info(body);
		Response response =  postRequest(uri, body);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String assetSourceKey = ((HashMap<String, String>) e.get("identifier")).get("sourceKey");
			logger.info("sourceKey - " + assetSourceKey);
			assertTrue("Asset source key pattern not valid for - " + assetSourceKey,
					assetSourceKey.startsWith(ASSET_KEY_PATTERN));
			ASSET_KEY = assetSourceKey;
			JSON_PARAM_MAP.put("csv.assetName", e.get("name").toString());
			writeToCSV();
		}
	}

	@Test(priority = 1, dependsOnMethods = { "postEnterprisewithvalidinputs" })
	public void testCreatingRegionwithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/regions";
		JSON_PARAM_MAP.put("apm.region.parentSourcekey", ROOT_ENTERPRISE);
		String body = readPayloadFile(JSON_FILE_PATH + "Region.json", getProperties());
		Response response =  postRequest(uri, body);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String regionSourceKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + regionSourceKey);
		}

	}

	@Test(priority = 1, dependsOnMethods = { "creatingAssetwithvalidinputs" })
	public void testCreatingLocationwithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET_KEY + "/location";
		String body = (new APMJsonUtility()).readFromFile(JSON_FILE_PATH + "Location.json");
		Response response =  postRequest(uri, body);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
	}

	@Test(priority = 1, dependsOnMethods = { "creatingSiteswithvalidinputs" })
	public void testCreatingSegmentwithvalidinputs() throws Throwable {
		Response responseSegType = createType("Segment_Type.json");
		String jsonStringST = responseSegType.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), responseSegType),
				responseSegType.getStatusCode() == HttpStatus.CREATED.value());
		List<HashMap<String, Object>> list1 = JsonPath.from(jsonStringST).get();
		for (HashMap<String, Object> e : list1) {
			String sourceKey = e.get("sourceKey").toString();
			JSON_PARAM_MAP.put("apm.segment.segmentType", sourceKey);
		}
		String uri = getProperty(APM_SERVICE_URL) + "/segments";
		JSON_PARAM_MAP.put("apm.segment.parent", SITE);
		JSON_PARAM_MAP.put("apm.segment.parentType", "SITE");
		String body = readPayloadFile(JSON_FILE_PATH + "Segment.json", getProperties());
		Response response =  postRequest(uri, body);;
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String segmentSourceKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + segmentSourceKey);
			assertTrue("Asset source key pattern not valid for - " + segmentSourceKey,
					segmentSourceKey.startsWith(SEGMENT_KEY_PATTERN));
			SEGMENT_KEY = segmentSourceKey;
		}
	}

	@Test(priority = 1, dependsOnMethods = { "createTagTypes", "creatingSegmentwithvalidinputs" })
	public void testCreatingSegmentTagwithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments/" + SEGMENT_KEY + "/tags";
		String body = readPayloadFile(JSON_FILE_PATH + "SegmentTag.json", getProperties());
		Response response =  postRequest(uri, body);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get("tags");
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			assertTrue("Tag source key pattern not valid for - " + srcKey, srcKey.startsWith("TAG_"));
		}
	}

	@Test(priority = 1, dependsOnMethods = { "postEnterprisewithvalidinputs", "createTagTypes" })
	public void testCreatingSiteswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites";
		JSON_PARAM_MAP.put("apm.site.parentSourceKey", ROOT_ENTERPRISE);
		JSON_PARAM_MAP.put("apm.site.parentType", "ENTERPRISE");
		String body = readPayloadFile(JSON_FILE_PATH + "Sites.json", getProperties());
		Response response =  postRequest(uri, body);
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
			JSON_PARAM_MAP.put("apm.enterprise.invalidparent", SITE);
			JSON_PARAM_MAP.put("csv.siteName", e.get("name").toString());
			JSON_PARAM_MAP.put("apm.enterprise.invalidparentType", "SITE");
		}
	}

	@Test
	public void testPostSiteswithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/siites";
		Response response = postRequest(uri, "");
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.NOT_FOUND.value(), response),
				response.getStatusCode() == HttpStatus.NOT_FOUND.value());
		logger.info("Response Body:" + jsonString);
	}

	@Test(priority = 1, dependsOnMethods = { "createTagTypes" })
	public void creatingSitetagswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites/" + SITE + "/tags";
		String body = readPayloadFile(JSON_FILE_PATH + "SiteTag.json", getProperties());
		Response response =  postRequest(uri, body);
		String jsonString = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get("tags");
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get("sourceKey").toString();
			logger.info("sourceKey - " + srcKey);
			assertTrue("Tag source key pattern not valid for - " + srcKey, srcKey.startsWith("TAG_"));
		}
	}

	@Test(priority = 1, dependsOnMethods = { "createAssetModelTypeValidInputs" })
	public void testCreatingSiteGatewaywithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites/" + SITE + "/gateways";
		String body = readPayloadFile(JSON_FILE_PATH + "SiteGateway.json", getProperties());
		Response response =  postRequest(uri, body);
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
			assertTrue("Asset source key pattern not valid for - " + sourceKey,
					sourceKey.startsWith(ASSET_KEY_PATTERN));
		}

	}

	private Response createType(String fileName) throws Exception {
		String uri = getProperty(APM_SERVICE_URL) + "/types";
		String body = readPayloadFile(JSON_FILE_PATH + fileName, getProperties());
		logger.info(body);
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE).when().header("Authorization", AUTH_TOKEN)
				.post(uri).then().extract().response();
		return response;
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

	@Test(priority = 10, dependsOnMethods = { "postEnterprisewithvalidinputs", "createTagTypes",
			"creatingAssetwithvalidinputs" })
	public void bulkDataCreation() throws Throwable {
		for (int i = 0; i < SITE_COUNT; i++) {
			testCreatingSiteswithvalidinputs();
			testCreatingSiteGatewaywithvalidinputs();
			for (int j = 0; j < ASSET_COUNT; j++) {
				testCreatingAssetwithvalidinputs();
			}
		}
	}

	@Test(priority = 1, dependsOnMethods = { "creatingSiteGatewaywithvalidinputs", "creatingSegmentwithvalidinputs" })
	public void creatingAssetUnderSegment() throws Throwable {
		// pass parent as segment - take creatingAssetwithvalidinputs as sample
		String uri = getProperty(APM_SERVICE_URL) + "/assets";
		JSON_PARAM_MAP.put("apm.asset.segmentSourceKey", SEGMENT_KEY);
		JSON_PARAM_MAP.put("apm.asset.gatewaySourceKey", GATEWAY_ASSET);
		String body = readPayloadFile(JSON_FILE_PATH + "Asset_Under_Segment.json", getProperties());
		logger.info(SEGMENT_KEY);
		logger.info(body);
		Response response = postRequest(uri, body);
		String jsonString = response.getBody().asString();		
		logger.info("Response Body:" + jsonString);
		logger.info("Response Time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String assetSourceKey = ((HashMap<String, String>) e.get("identifier")).get("sourceKey");
			logger.info("sourceKey - " + assetSourceKey);
			assertTrue("Asset source key pattern not valid for - " + assetSourceKey,
					assetSourceKey.startsWith(ASSET_KEY_PATTERN));
			ASSET_KEY = assetSourceKey;
			JSON_PARAM_MAP.put("csv.assetName", e.get("name").toString());
			writeToCSV();
		}

	}

}
