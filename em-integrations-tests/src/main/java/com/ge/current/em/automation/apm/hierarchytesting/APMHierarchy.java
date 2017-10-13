package com.ge.current.em.automation.apm.hierarchytesting;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.util.APMTestUtil;
import com.ge.current.em.utils.APMParentType;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class APMHierarchy extends APMTestUtil {

	private static final Log logger = LogFactory.getLog(APMHierarchy.class);
	private static final String JSON_FILE_PATH = "src/main/resources/test-suite/data/apmhierarchy/";
	private static final String JSON_FILE_PATH_APM = "src/main/resources/test-suite/data/apm/";

	private Map<String, String> TAG_TYPES = new HashMap<String, String>();

	private String ROOT_ENTERPRISE = "ENTERPRISE_979139b5-3ce7-304c-8304-42e2128e4efe";
	private String Non_ROOT_ENTERPRISE = "";
	private String REGION = "";
	private String SITE = "";
	private String SEGMENT_KEY = "";
	private String GATEWAY_ASSET = "";
	private String SEGMENT_KEYWITHASSET = "";
	public String SEGMENT_ID = "";
	public String ASSET_ID = "";

	/**
	 * Creating Root Enterprise
	 *
	 * @throws Throwable
	 */
	@Test
	public void testEnterpriseCanExistWithoutAParent() throws Throwable {
		/*
		 * RestAssured.basePath= "/enterprises"; String body =
		 * readPayloadFile(JSON_FILE_PATH + "CreateRootEnterprise.json",
		 * getProperties()); Response response =
		 * postRequest(RestAssured.basePath, body); String jsonString =
		 * response.getBody().asString(); logger.info("Response Body:" +
		 * jsonString); logger.info("Response time:" + response.getTime());
		 * List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		 * for (HashMap<String, Object> e : list) { String srcKey =
		 * e.get(APMConstants.SOURCE_KEY).toString(); String classificationCode
		 * = e.get(CLASSIFICATION_CODE).toString(); logger.info("sourceKey - " +
		 * srcKey); ROOT_ENTERPRISE = srcKey;
		 * JSON_PARAM_MAP.put("apm.ROOT_ENTERPRISE", ROOT_ENTERPRISE);
		 * JSON_PARAM_MAP.put("csv.EnterpriseName", e.get(NAME).toString());
		 * assertTrue("Enterprise source key pattern not valid for - " + srcKey,
		 * srcKey.startsWith(ENTERPRISE_PATTERN)); validatePayload(srcKey,
		 * classificationCode, e); }
		 */
		JSON_PARAM_MAP.put("apm.ROOT_ENTERPRISE", ROOT_ENTERPRISE);
		JSON_PARAM_MAP.put("csv.EnterpriseName", "TEST_APM_ENTERPRISE");
	}

	/**
	 * Creating Non Root Enterprise
	 *
	 * @throws Throwable
	 */

	@Test(priority = 1, dependsOnMethods = { "testEnterpriseCanExistWithoutAParent" })
	public void testEnterpriseParentCanBeRootEnterprise() throws Throwable {
		RestAssured.basePath = "/enterprises";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateNonRootEnterprise.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get(APMConstants.SOURCE_KEY).toString();
			String parentsrcKey = e.get(APMConstants.PARENT_SOURCE_KEY).toString();
			String parenttype = e.get(APMConstants.PARENT_CLASSIFICATION_CODE).toString();
			String classificationCode = e.get(APMConstants.CLASSIFICATION_CODE).toString();
			validatePayload(RestAssured.baseURI + "/enterprises/" + srcKey, classificationCode, e);
			testParentTypeForEnterprise(srcKey, parentsrcKey, parenttype);
			Non_ROOT_ENTERPRISE = srcKey;
			JSON_PARAM_MAP.put("apm.NonROOT_ENTERPRISE", Non_ROOT_ENTERPRISE);
			JSON_PARAM_MAP.put("csv.EnterpriseName", e.get(APMConstants.NAME).toString());
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testEnterpriseParentCanBeRootEnterprise" })
	public void testSiteParentCanBeOfEnterprise() throws Throwable {
		RestAssured.basePath = "/sites";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSiteUnderRootEnterprise.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			String parentsrcKey = e.get(APMConstants.PARENT_SOURCE_KEY).toString();
			String parenttype = e.get(APMConstants.PARENT_CLASSIFICATION_CODE).toString();
			String classificationCode = e.get(APMConstants.CLASSIFICATION_CODE).toString();
			validatePayload(RestAssured.baseURI + "/sites/" + siteSourceKey, classificationCode, e);
			testParentTypeForSite(siteSourceKey, parentsrcKey, parenttype);
			SITE = siteSourceKey;
			JSON_PARAM_MAP.put("apm.siteunderRootEnterprise", SITE);
			JSON_PARAM_MAP.put("csv.siteName", e.get(APMConstants.NAME).toString());
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testEnterpriseParentCanBeRootEnterprise" })
	public void testRegionParentCanBeEnterprise() throws Throwable {
		RestAssured.basePath = "/regions";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateRegionUnderRootEnterprise.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String regionSourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			String parentsrcKey = e.get(APMConstants.PARENT_SOURCE_KEY).toString();
			String parenttype = e.get(APMConstants.PARENT_CLASSIFICATION_CODE).toString();
			String classificationCode = e.get(APMConstants.CLASSIFICATION_CODE).toString();
			validatePayload(RestAssured.baseURI + "/regions/" + regionSourceKey, classificationCode, e);
			testParentTypeForRegion(regionSourceKey, parentsrcKey, parenttype);
			REGION = regionSourceKey;
			JSON_PARAM_MAP.put("apm.RegionUnderEnterprise", REGION);
		}

	}

	@Test(priority = 1, dependsOnMethods = { "testRegionParentCanBeEnterprise" })
	public void testRegionParentCanBeRegion() throws Throwable {
		RestAssured.basePath = "/regions";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateRegionUnderRegion.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String regionSourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			String parentsrcKey = e.get(APMConstants.PARENT_SOURCE_KEY).toString();
			String parenttype = e.get(APMConstants.PARENT_CLASSIFICATION_CODE).toString();
			String classificationCode = e.get(APMConstants.CLASSIFICATION_CODE).toString();
			validatePayload(RestAssured.baseURI + "/regions/" + regionSourceKey, classificationCode, e);
			testParentTypeForRegion(regionSourceKey, parentsrcKey, parenttype);
		}

	}

	@Test(priority = 1, dependsOnMethods = { "testRegionParentCanBeEnterprise" })
	public void testSiteParentCanBeRegion() throws Throwable {
		RestAssured.basePath = "/sites";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSiteUnderRegion.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			String parentsrcKey = e.get(APMConstants.PARENT_SOURCE_KEY).toString();
			String parenttype = e.get(APMConstants.PARENT_CLASSIFICATION_CODE).toString();
			SITE = siteSourceKey;
			String classificationCode = e.get(APMConstants.CLASSIFICATION_CODE).toString();
			validatePayload(RestAssured.baseURI + "/sites/" + siteSourceKey, classificationCode, e);
			testParentTypeForSite(SITE, parentsrcKey, parenttype);
			JSON_PARAM_MAP.put("apm.siteunderregion", SITE);
			JSON_PARAM_MAP.put("csv.siteName", e.get(APMConstants.NAME).toString());
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testSiteParentCanBeRegion" })
	public void testSiteParentCanBeSite() throws Throwable {
		RestAssured.basePath = "/sites";
		String body = readPayloadFile(JSON_FILE_PATH + "CreateSiteUnderSite.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String siteSourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			String parentsrcKey = e.get(APMConstants.PARENT_SOURCE_KEY).toString();
			String parenttype = e.get(APMConstants.PARENT_CLASSIFICATION_CODE).toString();
			String classificationCode = e.get(APMConstants.CLASSIFICATION_CODE).toString();
			validatePayload(RestAssured.baseURI + "/sites/" + siteSourceKey, classificationCode, e);
			testParentTypeForSite(siteSourceKey, parentsrcKey, parenttype);
			SITE = siteSourceKey;
			JSON_PARAM_MAP.put("apm.siteundersite", SITE);
			JSON_PARAM_MAP.put("csv.siteName", e.get(APMConstants.NAME).toString());
			JSON_PARAM_MAP.put("csv.siteNameundersite", e.get(APMConstants.NAME).toString());
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testEnterpriseParentCanBeRootEnterprise" })
	public void testCreateTagTypes() throws Exception {
		/*
		 * logger.info("CreateTag2 execute"); Response response =
		 * createType("Tag_Type_AHU.json"); String jsonString =
		 * response.getBody().asString(); logger.info("Response Body:" +
		 * jsonString); logger.info("Response time:" + response.getTime());
		 * List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		 * for (HashMap<String, Object> e : list) { String sourceKey =
		 * e.get(APMConstants.SOURCE_KEY).toString(); String type =
		 * e.get("type").toString(); //
		 * logger.info("sourceKey tagtype --- ---- " + "apm." + //
		 * e.get(NAME).toString() + ".tagtype == " + sourceKey);
		 * assertTrue("tag type source key pattern not valid for - " +
		 * sourceKey, sourceKey.startsWith(type)); TAG_TYPES.put("apm." +
		 * e.get(NAME).toString() + ".tagtype", sourceKey);
		 * validateTypeUnderRoot(sourceKey, type); validatePayload(sourceKey,
		 * type, e); } JSON_PARAM_MAP.putAll(TAG_TYPES);
		 */
		getTagTypes();
		JSON_PARAM_MAP.putAll(TAG_TYPES);
	}

	private void getTagTypes() {
		String uri = RestAssured.baseURI + "/types?typeClass=TAG_TYPE&typeName=dischargeAirTempSensor" + "&size=20";
		Response response = RestAssured.given().contentType("application/json").when()
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE)
				.get(uri).then().extract().response();
		List<HashMap<String, Object>> list = response.jsonPath().getList(APMConstants.CONTENT);
		for (HashMap<String, Object> e : list) {
			String sourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			TAG_TYPES.put("apm." + e.get(APMConstants.NAME).toString() + ".tagtype", sourceKey);
		}
	}

	@Test(dependsOnMethods = { "testEnterpriseCanExistWithoutAParent" })
	public void testCreateSegmentTypeUnderRootEnterprise() throws Exception {
		Response response = createType("Segment_Type.json");
		String jsonStringST = response.getBody().asString();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		List<HashMap<String, Object>> list1 = JsonPath.from(jsonStringST).get();
		for (HashMap<String, Object> e : list1) {
			String sourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			JSON_PARAM_MAP.put("apm.segment.segmentType", sourceKey);
			String classificationCode = e.get("type").toString();
			validateTypeUnderRoot(sourceKey, "SEGMENT_TYPE");
			validatePayload(RestAssured.baseURI + "/types/" + sourceKey, classificationCode, e);
		}

	}

	@Test(priority = 1, dependsOnMethods = { "testSiteParentCanBeSite", "testCreateTagTypes" })
	public void testCreateAssetType() throws Exception {
		/*
		 * RestAssured.basePath = "/types";
		 * JSON_PARAM_MAP.put("apm.asset.siteSourceKey", SITE); String value =
		 * readPayloadFile(JSON_FILE_PATH_APM + "Tags_AHU.json",
		 * getProperties()); JSON_PARAM_MAP.put("apm.tag.value",
		 * JSONObject.quote(value)); String body =
		 * readPayloadFile(JSON_FILE_PATH_APM + "Assets_Type_AHU.json",
		 * getProperties()).replaceAll("\"\"", "\""); Response response =
		 * RestAssured.given().contentType("application/json").body(body)
		 * .header("ROOT_ENTERPRISE_SOURCEKEY",
		 * ROOT_ENTERPRISE).when().header(APMConstants.AUTHORIZATION,
		 * AUTH_TOKEN) .post().then().extract().response(); String jsonString =
		 * response.getBody().asString();
		 * assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
		 * response.getStatusCode() == HttpStatus.CREATED.value());
		 * List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		 * for (HashMap<String, Object> e : list) { String sourceKey =
		 * e.get(APMConstants.SOURCE_KEY).toString(); String type =
		 * e.get("type").toString(); ASSET_TYPE = sourceKey;
		 * validatePayload(sourceKey, type, e); validateTypeUnderRoot(sourceKey,
		 * type); }
		 */
	}

	@Test(priority = 1, dependsOnMethods = { "testCreateAssetType" })
	public void testCreateAssetModelType() throws Exception {
		/*
		 * RestAssured.basePath = "/types";
		 * JSON_PARAM_MAP.put("apm.assetModel.parent", ASSET_TYPE); String body
		 * = readPayloadFile(JSON_FILE_PATH_APM + "Assets_Model_Type_AHU.json",
		 * getProperties()); logger.info(body); Response response =
		 * RestAssured.given().contentType("application/json").body(body)
		 * .header("ROOT_ENTERPRISE_SOURCEKEY",
		 * ROOT_ENTERPRISE).when().header(APMConstants.AUTHORIZATION,
		 * AUTH_TOKEN) .post().then().extract().response(); String jsonString =
		 * response.getBody().asString();
		 * assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
		 * response.getStatusCode() == HttpStatus.CREATED.value());
		 * List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		 * for (HashMap<String, Object> e : list) { String sourceKey =
		 * e.get(APMConstants.SOURCE_KEY).toString(); String type =
		 * e.get("type").toString(); validatePayload(sourceKey, type, e);
		 * validateTypeUnderRoot(sourceKey, type); }
		 */
	}

	@Test(priority = 1, dependsOnMethods = { "testCreatingSiteGateway", "testSiteParentCanBeSite" })
	public void testCreatingAssetUnderSite() throws Throwable {
		RestAssured.basePath = "/assets";
		JSON_PARAM_MAP.put("apm.asset.siteSourceKey", SITE);
		JSON_PARAM_MAP.put("apm.asset.gatewaySourceKey", GATEWAY_ASSET);
		String body = readPayloadFile(JSON_FILE_PATH + "Asset_Under_Site_AHU.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String assetSourceKey = ((HashMap<String, String>) e.get(APMConstants.IDENTIFIER))
					.get(APMConstants.SOURCE_KEY);
			ASSET_ID = assetSourceKey;
			JSON_PARAM_MAP.put("csv.assetName", e.get(APMConstants.NAME).toString());
			validatePayload(RestAssured.baseURI + "/assets/" + assetSourceKey, ASSET_KEY_PATTERN, e);
			checkAssetVisibilitySite(assetSourceKey, SITE);
			checkAssetVisibilityRegion(assetSourceKey, REGION);
			checkAssetVisibilityEnterprise(assetSourceKey, ROOT_ENTERPRISE);
			JSON_PARAM_MAP.put("apm.assetID", assetSourceKey);
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testCreatingAssetUnderSite" })
	public void testCreatingTagsUnderAsset() throws Throwable {
		RestAssured.basePath = "/assets/" + ASSET_ID + "/tags";
		String body = readPayloadFile(JSON_FILE_PATH_APM + "SegmentTag.json", getProperties());
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).expect().statusCode(HttpStatus.CREATED.value()).when()
				.put().then().extract().response();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		List<HashMap<String, Object>> list = response.jsonPath().getList(APMConstants.TAGS);
		for (HashMap<String, Object> e : list) {
			String sourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			e.remove(APMConstants.PROPERTIES);
			validatePayload(RestAssured.baseURI + "/tags/" + sourceKey, "TAG", e);
		}
		logger.info("After removing properties" + list);
		Response getresponse = RestAssured.given().contentType("application/json")
				.headers(APMConstants.AUTHORIZATION, AUTH_TOKEN).when().header(APMConstants.AUTHORIZATION, AUTH_TOKEN)
				.get().then().extract().response();
		List<HashMap<String, Object>> getlist = getresponse.jsonPath().getList(APMConstants.CONTENT);
		assertTrue("Tag not matched for testCreatingTagsUnderAsset Expected :" + list + "Actual:" + getlist,
				getlist.toString().contains(list.toString()));
	}

	@Test(priority = 1, dependsOnMethods = { "testCreateAssetModelType", "testSiteParentCanBeSite" })
	public void testCreatingSiteGateway() throws Throwable {
		RestAssured.basePath = "/sites/" + SITE + "/gateways";
		String body = readPayloadFile(JSON_FILE_PATH_APM + "SiteGateway.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String sourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			GATEWAY_ASSET = sourceKey;
			validatePayload(RestAssured.baseURI + "/assets/" + sourceKey, "Gateway_asset", e);
			validateGatewayUnderSite(sourceKey, SITE);
		}

	}

	/**
	 * @throws Throwable
	 */
	@Test(priority = 1, dependsOnMethods = { "testSiteParentCanBeSite", "testCreateSegmentTypeUnderRootEnterprise" })
	public void testSegmentParentCanbeSite() throws Throwable {
		RestAssured.basePath = "/segments";
		JSON_PARAM_MAP.put("apm.siteundersite", SITE);
		JSON_PARAM_MAP.put("apm.segment.parentType", "SITE");
		String body = readPayloadFile(JSON_FILE_PATH_APM + "Segmentparentcanbesite.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String segmentSourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			testParentTypeForSegment(segmentSourceKey, SITE, "SITE");
			SEGMENT_KEY = segmentSourceKey;
			validatePayload(RestAssured.baseURI + "/segments/" + segmentSourceKey, SEGMENT_KEY_PATTERN, e);
			validateSegmentUnderSite(segmentSourceKey, SITE);
			validateSegmentUnderEnterprise(segmentSourceKey, ROOT_ENTERPRISE);
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testSegmentParentCanbeSite" })
	public void testSegmentParentCanBeSegment() throws Throwable {
		RestAssured.basePath = "/segments";
		JSON_PARAM_MAP.put("apm.segmentundersite", SEGMENT_KEY);
		String body = readPayloadFile(JSON_FILE_PATH_APM + "SegmentparentcanbeSegment.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String segmentSourceKey = e.get(APMConstants.SOURCE_KEY).toString();
			String parentsrcKey = e.get(APMConstants.PARENT_SOURCE_KEY).toString();
			String parenttype = e.get(APMConstants.PARENT_CLASSIFICATION_CODE).toString();
			SEGMENT_KEYWITHASSET = segmentSourceKey;
			SEGMENT_ID = segmentSourceKey;
			JSON_PARAM_MAP.put("apm.asset.segmentKeyWithAsset", SEGMENT_KEYWITHASSET);
			logger.info("sourceKey - " + segmentSourceKey);
			validatePayload(RestAssured.baseURI + "/segments/" + segmentSourceKey, SEGMENT_KEY_PATTERN, e);
			testParentTypeForSegment(segmentSourceKey, parentsrcKey, parenttype);
			validateSegmentUnderSite(segmentSourceKey, SITE);
			validateSegmentUnderEnterprise(segmentSourceKey, ROOT_ENTERPRISE);
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testCreatingSiteGateway", "testSegmentParentCanbeSite" })
	public void testCreatingAssetUnderSegment() throws Throwable {
		// pass parent as segment - take creatingAssetwithvalidinputs as sample
		RestAssured.basePath = "/assets";
		JSON_PARAM_MAP.put("apm.asset.segmentSourceKey", SEGMENT_KEY);
		JSON_PARAM_MAP.put("apm.asset.gatewaySourceKey", GATEWAY_ASSET);
		String body = readPayloadFile(JSON_FILE_PATH_APM + "Asset_AHU.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		for (HashMap<String, Object> e : list) {
			String assetSourceKey = ((HashMap<String, String>) e.get(APMConstants.IDENTIFIER))
					.get(APMConstants.SOURCE_KEY);
			JSON_PARAM_MAP.put("csv.assetName", e.get(APMConstants.NAME).toString());
			validatePayload(RestAssured.baseURI + "/assets/" + assetSourceKey, ASSET_KEY_PATTERN, e);
			checkAssetVisibilitySegment(assetSourceKey, SEGMENT_KEY);
			checkAssetVisibilitySite(assetSourceKey, SITE);
			checkAssetVisibilityEnterprise(assetSourceKey, ROOT_ENTERPRISE);
		}

	}

	@Test(priority = 1, dependsOnMethods = { "testSegmentParentCanbeSite", "testCreatingAssetUnderSegment" })
	public void testPatchSegmentChildren() throws Throwable {
		RestAssured.basePath = "/segments/" + SEGMENT_ID + "/children";
		String body = readPayloadFile(JSON_FILE_PATH_APM + "SegmentChildren.json", getProperties());
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).expect().statusCode(HttpStatus.OK.value()).when()
				.patch().then().extract().response();
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		RestAssured.basePath = "/segments/" + SEGMENT_ID + "/assets";
		Response getresponse = RestAssured.given().contentType("application/json")
				.headers(APMConstants.AUTHORIZATION, AUTH_TOKEN).when().header(APMConstants.AUTHORIZATION, AUTH_TOKEN)
				.get().then().extract().response();
		String jsonGetString = getresponse.getBody().asString();
		List<HashMap<String, Object>> getlist = JsonPath.from(jsonGetString).get(APMConstants.CONTENT);
		for (HashMap<String, Object> element : list) {
			assertTrue(
					"patched asset " + element.get(APMConstants.SOURCE_KEY).toString()
							+ " is not available under segment assets",
					presenceCheck(getlist, element.get(APMConstants.SOURCE_KEY).toString()));
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testSegmentParentCanbeSite", "testCreatingAssetUnderSegment" })
	public void testPutSegmentChildren() throws Throwable {
		// pass parent as segment - take creatingAssetwithvalidinputs as sample
		RestAssured.basePath = "/segments/" + SEGMENT_ID + "/children";
		String body = readPayloadFile(JSON_FILE_PATH_APM + "SegmentChildren.json", getProperties());
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).expect().statusCode(HttpStatus.OK.value()).when().put()
				.then().extract().response();
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get();
		RestAssured.basePath = "/segments/" + SEGMENT_ID + "/assets";
		Response getresponse = RestAssured.given().contentType("application/json")
				.headers(APMConstants.AUTHORIZATION, AUTH_TOKEN).when().header(APMConstants.AUTHORIZATION, AUTH_TOKEN)
				.get().then().extract().response();
		String jsonGetString = getresponse.getBody().asString();
		List<HashMap<String, Object>> getlist = JsonPath.from(jsonGetString).get(APMConstants.CONTENT);
		for (HashMap<String, Object> element : list) {
			assertTrue(
					"put asset " + element.get(APMConstants.SOURCE_KEY).toString()
							+ " is not available under segment assets",
					presenceCheck(getlist, element.get(APMConstants.SOURCE_KEY).toString()));
		}

	}

	@Test(priority = 1, dependsOnMethods = { "testCreateTagTypes", "testSegmentParentCanbeSite" })
	public void testCreatingTagUnderSegment() throws Throwable {
		RestAssured.basePath = "/segments/" + SEGMENT_KEY + "/tags";
		String body = readPayloadFile(JSON_FILE_PATH_APM + "SegmentTag.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get(APMConstants.TAGS);
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get(APMConstants.SOURCE_KEY).toString();
			validatePayload(RestAssured.baseURI + "/tags/" + srcKey, "TAG", e);
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testCreateTagTypes" })
	public void testCreatingTagsUnderSite() throws Throwable {
		RestAssured.basePath = "/sites/" + SITE + "/tags";
		String body = readPayloadFile(JSON_FILE_PATH_APM + "SiteTag.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get(APMConstants.TAGS);
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get(APMConstants.SOURCE_KEY).toString();
			validatePayload(RestAssured.baseURI + "/tags/" + srcKey, "TAG", e);
		}
	}

	@Test(priority = 1, dependsOnMethods = { "testCreateTagTypes" })
	public void testCreateTagUnderEnterprise() throws Throwable {
		// RestAssured.basePath ="/enterprises/" + ROOT_ENTERPRISE + "/tags";
		RestAssured.basePath = "/enterprises/" + Non_ROOT_ENTERPRISE + "/tags";
		String body = readPayloadFile(JSON_FILE_PATH_APM + "EnterpriseTag.json", getProperties());
		Response response = postRequest(RestAssured.basePath, body);
		String jsonString = response.getBody().asString();
		logger.info("Response Body:" + jsonString);
		logger.info("Response time:" + response.getTime());
		List<HashMap<String, Object>> list = JsonPath.from(jsonString).get(APMConstants.TAGS);
		for (HashMap<String, Object> e : list) {
			String srcKey = e.get(APMConstants.SOURCE_KEY).toString();
			validatePayload(RestAssured.baseURI + "/tags/" + srcKey, "TAG", e);
		}
	}

	private void testParentTypeForEnterprise(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for enterprise id : " + sourceKey,
				APMParentType.ENTERPRISE.contains(parentClassification));
		childValidation(sourceKey, parentSourceKey, parentClassification);
	}

	private void testParentTypeForRegion(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for region id : " + sourceKey,
				APMParentType.REGION.contains(parentClassification));
		childValidation(sourceKey, parentSourceKey, parentClassification);
	}

	private void testParentTypeForSite(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for site id : " + sourceKey,
				APMParentType.SITE.contains(parentClassification));
		childValidation(sourceKey, parentSourceKey, parentClassification);
	}

	private void testParentTypeForSegment(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for segment id : " + sourceKey,
				APMParentType.SEGMENT.contains(parentClassification));
		childValidation(sourceKey, parentSourceKey, parentClassification);
	}

	private void childValidation(String sourceKey, String parentSourceKey, String parentClassification) {
		String uri = RestAssured.baseURI;
		if (parentClassification.equals("ENTERPRISE")) {
			uri += "/enterprises/" + parentSourceKey + "/children";
		} else if (parentClassification.equals("SITE")) {
			uri += "/sites/" + parentSourceKey + "/children";
		} else if (parentClassification.equals("REGION")) {
			uri += "/regions/" + parentSourceKey + "/children";
		} else if (parentClassification.equals("SEGMENT")) {
			uri += "/segments/" + parentSourceKey + "/children";
		}
		uri += "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> content = getResponse(uri).jsonPath().getList(APMConstants.CONTENT);
		assertTrue("child is not available in the mentioned parent - parenkey : " + parentSourceKey + " -- child : "
				+ sourceKey, presenceCheck(content, sourceKey));
	}

	private void validatePayload(String uri, String entityType, HashMap<String, Object> input) {
		JsonPath getResponse = getResponse(uri).jsonPath();
		Iterator<String> keyItr = input.keySet().iterator();
		String key = "";
		while (keyItr.hasNext()) {
			key = keyItr.next();
			if (!key.equals(APMConstants.PARENT_SOURCE_KEY) && !key.equals(APMConstants.PARENT_CLASSIFICATION_CODE)
					&& !key.equals("locations") && !key.equals("catalog") && !key.equals("gatewaySourceKey")) {
				if (entityType.equals("Gateway_asset") || entityType.equals("ASSET")) {
					if (key.equals(APMConstants.IDENTIFIER)) {
						HashMap<String, Object> identifier = (HashMap<String, Object>) input.get(key);
						assertEquals(identifier.get(APMConstants.SOURCE_KEY), getResponse.get(APMConstants.SOURCE_KEY));
						if (entityType.equals("Gateway_asset")) {
							break;
						}
						continue;
					}
				}
				if (!entityType.equals("Gateway_asset")) {
					Object actual = getResponse.get(key);
					Object expected = input.get(key);
					assertEquals(actual, expected, "key checked - " + key);
				}
			}
		}
	}

	private void validateTypeUnderRoot(String sourceKey, String typeClass) {
		String uri = RestAssured.baseURI + "/types?typeClass=" + typeClass + "&size=20&sort=entryTimestamp,desc";
		Response response = RestAssured.given().contentType("application/json").when()
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE)
				.get(uri).then().extract().response();
		assertEquals(HttpStatus.OK.value(), response.statusCode());
		List<HashMap<String, Object>> elements = response.jsonPath().getList(APMConstants.CONTENT);
		assertTrue("Type not available under root :" + ROOT_ENTERPRISE + ", sourcekey - " + sourceKey,
				presenceCheck(elements, sourceKey));
	}

	private Response createType(String fileName) throws Exception {
		String uri = RestAssured.baseURI + "/types";
		String body = readPayloadFile(JSON_FILE_PATH_APM + fileName, getProperties());
		Response response = RestAssured.given().contentType("application/json").body(body)
				.header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE).when()
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).post(uri).then().extract().response();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		return response;
	}

	private void validateGatewayUnderSite(String sourceKey, String siteKey) {
		String uri = RestAssured.baseURI + "/sites/" + siteKey + "/gateways";
		List<HashMap<String, Object>> gateways = getResponse(uri).jsonPath().get("");
		assertTrue("gateway:" + sourceKey + " not under site  : " + siteKey, presenceCheck(gateways, sourceKey));
	}

	private void validateSegmentUnderEnterprise(String segmentSourceKey, String enterpriseSourceKey) {
		String uri = RestAssured.baseURI + "/enterprises/" + enterpriseSourceKey + "/segments";
		List<HashMap<String, Object>> segments = getResponse(uri).jsonPath().get("");
		assertTrue("segment:" + segmentSourceKey + " not under enterprise  : " + enterpriseSourceKey,
				presenceCheck(segments, segmentSourceKey));
	}

	private void validateSegmentUnderSite(String segmentSourceKey, String siteSourceKey) {
		String uri = RestAssured.baseURI + "/sites/" + siteSourceKey + "/segments";
		List<HashMap<String, Object>> segments = getResponse(uri).jsonPath().get("");
		assertTrue("segment:" + segmentSourceKey + " not under site  : " + siteSourceKey,
				presenceCheck(segments, segmentSourceKey));
	}

	private void checkAssetVisibilitySite(String assetSourceKey, String siteSourceKey) {
		String uri = RestAssured.baseURI + "/sites/" + siteSourceKey + "/assets";
		List<HashMap<String, Object>> assets = getResponse(uri).jsonPath().get(APMConstants.CONTENT);
		assertTrue("asset :" + assetSourceKey + " not under site  : " + siteSourceKey,
				presenceCheck(assets, assetSourceKey));
	}

	private void checkAssetVisibilitySegment(String assetSourceKey, String segmentKey) {
		String uri = RestAssured.baseURI + "/segments/" + segmentKey + "/assets";
		List<HashMap<String, Object>> assets = getResponse(uri).jsonPath().get(APMConstants.CONTENT);
		assertTrue("asset :" + assetSourceKey + " not under segment  : " + segmentKey,
				presenceCheck(assets, assetSourceKey));
	}

	private void checkAssetVisibilityEnterprise(String assetSourceKey, String enterpriseKey) {
		String uri = RestAssured.baseURI + "/enterprises/" + enterpriseKey + "/assets" + "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> assets = getResponse(uri).jsonPath().get(APMConstants.CONTENT);
		assertTrue("asset :" + assetSourceKey + " not under enterprise  : " + enterpriseKey,
				presenceCheck(assets, assetSourceKey));
	}

	private void checkAssetVisibilityRegion(String assetSourceKey, String regionKey) {
		String uri = RestAssured.baseURI + "/regions/" + regionKey + "/assets" + "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> assets = getResponse(uri).jsonPath().get(APMConstants.CONTENT);
		assertTrue("asset :" + assetSourceKey + " not under enterprise  : " + regionKey,
				presenceCheck(assets, assetSourceKey));
	}

	private boolean presenceCheck(List<HashMap<String, Object>> validationList, String validationKey) {
		for (HashMap<String, Object> element : validationList) {
			if (validationKey.equals(element.get(APMConstants.SOURCE_KEY))) {
				return true;
			}
		}
		return false;
	}
}