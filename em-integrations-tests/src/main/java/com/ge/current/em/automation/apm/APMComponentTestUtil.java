package com.ge.current.em.automation.apm;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.current.em.automation.apm.dto.AssetHelper;
import com.ge.current.em.automation.apm.dto.SegmentHelper;
import com.ge.current.em.automation.apm.dto.SiteHelper;
import com.ge.current.em.automation.util.EMTestUtil;
import com.ge.current.em.persistenceapi.dto.AssetDTO;
import com.ge.current.em.persistenceapi.dto.EnterpriseDTO;
import com.ge.current.em.persistenceapi.dto.GatewayDTO;
import com.ge.current.em.persistenceapi.dto.LocationDTO;
import com.ge.current.em.persistenceapi.dto.MetaEntityDTO;
import com.ge.current.em.persistenceapi.dto.MetaTypeDTO;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.RegionDTO;
import com.ge.current.em.persistenceapi.dto.SegmentDTO;
import com.ge.current.em.persistenceapi.dto.SiteDTO;
import com.ge.current.em.persistenceapi.dto.TagDTO;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.HttpMethod;

public class APMComponentTestUtil extends EMTestUtil {

	protected String apmToken = null;
	protected String baseAPM_URI = null;
	protected ObjectMapper objMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);;
	protected String ENTERPRISE_ID = "";
	protected String SITE_ID = "";
	protected String ROOT_ENTERPRISE = "";
	protected String REGION_ID = "";
	protected String SEGMENT_TYPE = "";
	protected String SEGMENT_ID = "";
	protected String ASSET_ID = "";
	protected String TAG_ID = "";
	protected String TYPE_ID = "";
	protected String GATEWAY_ASSET = "";

	private SiteHelper sitedto = new SiteHelper();
	private SegmentHelper segmentdto = new SegmentHelper();
	private AssetHelper assetdto = new AssetHelper();

	@BeforeClass(alwaysRun = true)
	public void setUp() throws IOException {
		apmToken = "bearer " + getAPMServiceToken();
		baseAPM_URI = getProperty(APMConstants.APM_SERVICE_URL) + APMURIConstants.PATH_SEPARATOR;
		ENTERPRISE_ID = getProperty("apm_component.ENTERPRISE");
		ROOT_ENTERPRISE = getProperty("apm_component.ROOT_ENTERPRISE");
		SITE_ID = getProperty("apm_component.SITE");
		REGION_ID = getProperty("apm_component.REGION");
		TAG_ID = getProperty("apm_component.TAG");
		TYPE_ID = getProperty("apm_component.TYPE");
		SEGMENT_TYPE = getProperty("apm_component.SEGMENT_TYPE");
		SEGMENT_ID = getProperty("apm_component.SEGMENT");
		ASSET_ID = getProperty("apm_component.ASSET");
		GATEWAY_ASSET = getProperty("apm_component.GATEWAY_ASSET");

	}

	protected String AUTH_TOKEN = "";

	@BeforeTest
	public void addPropertiesToTestContext(ITestContext context) {
		setProperty("apm.Authorization", "Bearer " + super.getAPMServiceToken());
		AUTH_TOKEN = getProperty("apm.Authorization");
		RestAssured.baseURI = getProperty(APMConstants.APM_SERVICE_URL);
		for (Map.Entry<Object, Object> property : getProperties().entrySet()) {
			context.setAttribute((String) property.getKey(), property.getValue());
		}
	}

	protected void validateEnterprise(List<EnterpriseDTO> enterpriseDTOs) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("fetchParents", "true");
		for (EnterpriseDTO enterpriseDTOExpected : enterpriseDTOs) {
			TypeReference<EnterpriseDTO> entMap = new TypeReference<EnterpriseDTO>() {
			};
			EnterpriseDTO enterpriseDTOActual = objMapper
					.readValue(getAPMResponse(baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR
							+ enterpriseDTOExpected.getSourceKey(), param, apmToken).asString(), entMap);
			assertEquals(enterpriseDTOActual.getSourceKey(), enterpriseDTOExpected.getSourceKey());
			assertEquals(enterpriseDTOActual.getName(), enterpriseDTOExpected.getName());
			assertEquals(enterpriseDTOActual.getClassificationCode(), enterpriseDTOExpected.getClassificationCode());
			testParentDetailForEnterprise(enterpriseDTOExpected.getSourceKey(),
					enterpriseDTOExpected.getParentSourceKey(), enterpriseDTOExpected.getParentClassificationCode());
		}
	}

	protected void validateSite(List<SiteDTO> siteDTOS) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("fetchParents", "true");
		for (SiteDTO siteDTOExpected : siteDTOS) {
			TypeReference<SiteDTO> siteMap = new TypeReference<SiteDTO>() {
			};
			SiteDTO siteDTOActual = objMapper.readValue(getAPMResponse(baseAPM_URI + APMURIConstants.SITE
					+ APMURIConstants.PATH_SEPARATOR + siteDTOExpected.getSourceKey(), param, apmToken).asString(),
					siteMap);
			assertEquals(siteDTOActual.getSourceKey(), siteDTOExpected.getSourceKey());
			assertEquals(siteDTOActual.getName(), siteDTOExpected.getName());
			assertEquals(siteDTOActual.getClassificationCode(), siteDTOExpected.getClassificationCode());
			testParentDetailForSite(siteDTOExpected.getSourceKey(), siteDTOExpected.getParentSourceKey(),
					siteDTOExpected.getParentClassificationCode());
		}
	}

	protected void validateSegment(List<SegmentDTO> segmentDTOS) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("fetchParents", "true");
		for (SegmentDTO segmentDTOExpected : segmentDTOS) {
			TypeReference<SegmentDTO> segmentMap = new TypeReference<SegmentDTO>() {
			};
			SegmentDTO segmentDTOActual = objMapper.readValue(getAPMResponse(baseAPM_URI + APMURIConstants.SEGMENT
					+ APMURIConstants.PATH_SEPARATOR + segmentDTOExpected.getSourceKey(), param, apmToken).asString(),
					segmentMap);
			assertEquals(segmentDTOActual.getSourceKey(), segmentDTOExpected.getSourceKey());
			assertEquals(segmentDTOActual.getName(), segmentDTOExpected.getName());
			testParentDetailForSegment(segmentDTOExpected.getSourceKey(), segmentDTOExpected.getParentSourceKey(),
					segmentDTOExpected.getParentClassificationCode());
			validateSegmentUnderSite(segmentDTOExpected.getSourceKey(), SITE_ID);
			validateSegmentUnderEnterprise(segmentDTOExpected.getSourceKey(), ROOT_ENTERPRISE);
		}
	}

	protected void validateAsset(List<AssetDTO> list, String parentClassification, String parentSourceKey)
			throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("fetchParents", "true");
		for (AssetDTO assetDTOExpected : list) {
			String assetKey = assetDTOExpected.getIdentifier().getSourceKey();
			TypeReference<MetaEntityDTO> assetMap = new TypeReference<MetaEntityDTO>() {
			};
			MetaEntityDTO assetDTOActual = objMapper.readValue(
					getAPMResponse(baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + assetKey,
							param, apmToken).asString(),
					assetMap);
			assertEquals(assetDTOActual.getSourceKey(), assetKey);
			assertEquals(assetDTOActual.getName(), assetDTOExpected.getName());
			checkAssetVisibilitySite(assetKey, SITE_ID);
			checkAssetVisibilityRegion(assetKey, REGION_ID);
			checkAssetVisibilityEnterprise(assetKey, ROOT_ENTERPRISE);
			if (parentClassification.equals(APMConstants.SEGMENT)) {
				checkAssetVisibilitySegment(assetKey, SEGMENT_ID);
			}
		}
	}

	protected void validateRegion(List<RegionDTO> regionDTOS) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("fetchParents", "true");
		for (RegionDTO regionDTOExpected : regionDTOS) {
			TypeReference<RegionDTO> regionMap = new TypeReference<RegionDTO>() {
			};
			RegionDTO regionDTOActual = objMapper.readValue(getAPMResponse(baseAPM_URI + APMURIConstants.REGION
					+ APMURIConstants.PATH_SEPARATOR + regionDTOExpected.getSourceKey(), param, apmToken).asString(),
					regionMap);
			assertEquals(regionDTOActual.getSourceKey(), regionDTOExpected.getSourceKey());
			assertEquals(regionDTOActual.getName(), regionDTOExpected.getName());
			assertEquals(regionDTOActual.getClassificationCode(), regionDTOExpected.getClassificationCode());
			testParentDetailForRegion(regionDTOExpected.getSourceKey(), regionDTOExpected.getParentSourceKey(),
					regionDTOExpected.getParentClassificationCode());
		}
	}

	protected void validateGatewayAsset(List<GatewayDTO> gateways) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		for (GatewayDTO gatewayExpected : gateways) {
			TypeReference<AssetDTO> assetMap = new TypeReference<AssetDTO>() {
			};
			AssetDTO assetActual = objMapper.readValue(getAPMResponse(baseAPM_URI + APMURIConstants.ASSET
					+ APMURIConstants.PATH_SEPARATOR + gatewayExpected.getSourceKey(), param, apmToken).asString(),
					assetMap);
			assertEquals(assetActual.getName(), gatewayExpected.getName());
		}
	}

	protected void validateGatewayUnderSite(List<GatewayDTO> gateways, String siteSourceKey) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		for (GatewayDTO gatewayExpected : gateways) {
			List<HashMap<String, Object>> content = getAPMResponse(
					baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + siteSourceKey
							+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.GATEWAY,
					param, apmToken).jsonPath().getList("");
			presenceCheck(content, gatewayExpected.getSourceKey());

		}
	}

	protected void validateType(List<MetaTypeDTO> metaTypeDTOs) throws IOException {
		for (MetaTypeDTO e : metaTypeDTOs) {
			String response = getAPMResponse(
					baseAPM_URI + APMURIConstants.TYPE + APMURIConstants.PATH_SEPARATOR + e.getSourceKey(),
					new HashMap<String, String>(), apmToken).asString();
			TypeReference<MetaTypeDTO> typeMap = new TypeReference<MetaTypeDTO>() {
			};
			MetaTypeDTO metaTypeDTO = objMapper.readValue(response, typeMap);
			assertEquals(metaTypeDTO.getSourceKey(), e.getSourceKey());
			assertEquals(metaTypeDTO.getType(), e.getType());
			assertEquals(metaTypeDTO.getName(), e.getName());
			validateTypeExistUnderRoot(metaTypeDTO.getSourceKey(), metaTypeDTO.getType(), metaTypeDTO.getName());
		}
	}

	protected void validateTag(List<TagDTO> tags) throws IOException {
		for (TagDTO tag : tags) {
			String response = getAPMResponse(
					baseAPM_URI + APMURIConstants.TAGS + APMURIConstants.PATH_SEPARATOR + tag.getSourceKey(),
					new HashMap<String, String>(), apmToken).asString();
			TypeReference<TagDTO> tagMap = new TypeReference<TagDTO>() {
			};
			TagDTO tagResponse = objMapper.readValue(response, tagMap);
			assertEquals(tagResponse, tag);
		}
	}

	protected void validateSiteLocations(List<LocationDTO> list) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("fetchLocation", "true");
		TypeReference<MetaEntityDTO> siteMap = new TypeReference<MetaEntityDTO>() {
		};
		MetaEntityDTO siteDTOActual = objMapper
				.readValue(getAPMResponse(baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID,
						param, apmToken).asString(), siteMap);
		assertEquals(list, siteDTOActual.getLocations());
	}

	protected void testParentDetailForEnterprise(String sourceKey, String parentSourceKey, String parentClassification)
			throws IOException {
		List<HashMap<String, Object>> content = getAPMResponse(
				baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + parentSourceKey
						+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.CHILDREN + "?sort=entryTimestamp,desc",
				new HashMap<String, String>(), apmToken).jsonPath().getList(APMConstants.CONTENT);
		assertTrue("child is not available in the mentioned parent - parenkey : " + parentSourceKey + " -- child : "
				+ sourceKey, presenceCheck(content, sourceKey));
	}

	private void testParentDetailForRegion(String sourceKey, String parentSourceKey, String parentClassification)
			throws IOException {
		String uri = baseAPM_URI;
		if (parentClassification.equals(APMConstants.ENTERPRISE)) {
			uri = uri + APMURIConstants.ENTERPRISE;
		} else if (parentClassification.equals(APMConstants.REGION)) {
			uri = uri + APMURIConstants.REGION;
		}
		uri = uri + APMURIConstants.PATH_SEPARATOR + parentSourceKey + APMURIConstants.PATH_SEPARATOR
				+ APMURIConstants.CHILDREN + "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> content = getAPMResponse(uri, new HashMap<String, String>(), apmToken).jsonPath()
				.getList(APMConstants.CONTENT);
		assertTrue("child is not available in the mentioned parent - parenkey : " + parentSourceKey + " -- child : "
				+ sourceKey, presenceCheck(content, sourceKey));
	}

	private void testParentDetailForSite(String sourceKey, String parentSourceKey, String parentClassification)
			throws IOException {
		String uri = baseAPM_URI;
		if (parentClassification.equals(APMConstants.ENTERPRISE)) {
			uri = uri + APMURIConstants.ENTERPRISE;
		} else if (parentClassification.equals(APMConstants.REGION)) {
			uri = uri + APMURIConstants.REGION;
		} else if (parentClassification.equals(APMConstants.SITE)) {
			uri = uri + APMURIConstants.SITE;
		}
		uri = uri + APMURIConstants.PATH_SEPARATOR + parentSourceKey + APMURIConstants.PATH_SEPARATOR
				+ APMURIConstants.CHILDREN + "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> content = getAPMResponse(uri, new HashMap<String, String>(), apmToken).jsonPath()
				.getList(APMConstants.CONTENT);
		assertTrue("child is not available in the mentioned parent - parenkey : " + parentSourceKey + " -- child : "
				+ sourceKey, presenceCheck(content, sourceKey));
	}

	private void testParentDetailForSegment(String sourceKey, String parentSourceKey, String parentClassification)
			throws IOException {
		String uri = baseAPM_URI;
		if (parentClassification.equals(APMConstants.SITE)) {
			uri = uri + APMURIConstants.SITE;
		} else if (parentClassification.equals(APMConstants.SEGMENT)) {
			uri = uri + APMURIConstants.SEGMENT;
		}
		uri = uri + APMURIConstants.PATH_SEPARATOR + parentSourceKey + APMURIConstants.PATH_SEPARATOR
				+ APMURIConstants.CHILDREN + "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> content = getAPMResponse(uri, new HashMap<String, String>(), apmToken).jsonPath()
				.getList(APMConstants.CONTENT);
		assertTrue("child is not available in the mentioned parent - parenkey : " + parentSourceKey + " -- child : "
				+ sourceKey, presenceCheck(content, sourceKey));
	}

	protected void validateParentDetailForAsset(String sourceKey, String string, String parentClassification)
			throws IOException {
		String uri = baseAPM_URI;
		if (parentClassification.equals(APMConstants.SITE)) {
			uri = uri + APMURIConstants.SITE;
		} else if (parentClassification.equals(APMConstants.SEGMENT)) {
			uri = uri + APMURIConstants.SEGMENT;
		}
		uri = uri + APMURIConstants.PATH_SEPARATOR + string + APMURIConstants.PATH_SEPARATOR + APMURIConstants.ASSET
				+ "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> content = getAPMResponse(uri, new HashMap<String, String>(), apmToken).jsonPath()
				.getList(APMConstants.CONTENT);
		assertTrue("child is not available in the mentioned parent - parenkey : " + string + " -- child : " + sourceKey,
				presenceCheck(content, sourceKey));
	}

	private boolean presenceCheck(List<HashMap<String, Object>> validationList, String validationKey) {
		for (HashMap<String, Object> element : validationList) {
			if (validationKey.equals(element.get(APMConstants.SOURCE_KEY))) {
				return true;
			}
		}
		return false;
	}

	protected Response postRequest(String uri, String content) {
		Response response = RestAssured.given().contentType("application/json").body(content)
				.header(APMConstants.AUTHORIZATION, apmToken).expect().statusCode(HttpStatus.CREATED.value()).when()
				.post(uri).then().extract().response();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		return response;
	}

	protected Response getRequest(String uri) {
		Response response = RestAssured.given().contentType("application/json")
				.header(APMConstants.AUTHORIZATION, apmToken).expect().statusCode(HttpStatus.OK.value()).when().get(uri)
				.then().extract().response();
		assertTrue(getFailureMessage(HttpStatus.OK.value(), response),
				response.getStatusCode() == HttpStatus.OK.value());
		return response;
	}

	protected JsonPath responseCheck(String uri, String content, String method, int status, String contentType) {
		Response response = null;
		if (method.equals(HttpMethod.PATCH.name())) {
			response = RestAssured.given().contentType(contentType).body(content)
					.header(APMConstants.AUTHORIZATION, apmToken).when().patch(uri).then().extract().response();
		} else if (method.equals(HttpMethod.PUT.name())) {
			response = RestAssured.given().contentType(contentType).body(content)
					.header(APMConstants.AUTHORIZATION, apmToken).when().put(uri).then().extract().response();
		}
		assertTrue(getFailureMessage(status, response), response.getStatusCode() == status);
		if (method.equals(HttpMethod.PUT.name())) {
			JsonPath input = new JsonPath(content);
			List<PropertiesDTO> actual = response.jsonPath().getList("");
			List<PropertiesDTO> expected = input.getList(APMConstants.LIST);
			assertEquals(actual, expected);
		}
		return response.jsonPath();
	}

	protected void responseValidate(String URI, JsonPath in, String method, int status, String contentType) {
		Response response = RestAssured.given().contentType("application/json").when()
				.header(APMConstants.AUTHORIZATION, apmToken).get(URI).then().extract().response();
		JsonPath out = response.jsonPath();
		List<PropertiesDTO> actual = out.getList(APMConstants.PROPERTIES);
		List<PropertiesDTO> expected = in.getList("");
		assertEquals(actual, expected);
	}

	protected String getFailureMessage(int expectedResponse, Response response) {
		String failureMessage = "Expected status code is " + expectedResponse + " but received "
				+ response.getStatusCode() + "\n Failed due to ";
		if (response.getStatusCode() != expectedResponse) {
			failureMessage = failureMessage + "\n" + response.getBody().jsonPath().getString("error");
			if (response.getBody().jsonPath().getString(APMConstants.MESSAGE) != null) {
				failureMessage = failureMessage + "\n" + response.getBody().jsonPath().getString("message");
			}
		}
		return failureMessage;
	}

	protected Response createType(String content) throws Exception {
		String uri = baseAPM_URI + APMURIConstants.TYPE;
		Response response = RestAssured.given().contentType("application/json").body(content)
				.header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE).when()
				.header(APMConstants.AUTHORIZATION, apmToken).post(uri).then().extract().response();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		return response;
	}

	protected Map<String,String> createSiteDescendants() throws Exception {
		Map<String,String> siteAndDecendants = new HashMap<String,String>();
		String sitecontent = objectMapper.writeValueAsString(sitedto.getSiteUnderEnterprise(ENTERPRISE_ID));
		TypeReference<List<SiteDTO>> siteMap = new TypeReference<List<SiteDTO>>() {
		};
		List<SiteDTO> sitelist = objMapper.readValue(postRequest(baseAPM_URI + APMURIConstants.SITE, sitecontent).asString(), siteMap);
		String siteId = sitelist.get(0).getSourceKey();
		siteAndDecendants.put(APMConstants.SITE, siteId);
		String gatewaycontent = objectMapper.writeValueAsString(sitedto.getGatewayAsset());
		Response gatewayresponse = postRequest(baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR
				+ siteId + APMURIConstants.PATH_SEPARATOR + APMURIConstants.GATEWAY,
				gatewaycontent);
		TypeReference<List<GatewayDTO>> gateWayTypeRef = new TypeReference<List<GatewayDTO>>() {
		};
		List<GatewayDTO> gatewaylist = objMapper.readValue(gatewayresponse.asString(), gateWayTypeRef);
		String segmentcontent = objectMapper
				.writeValueAsString(segmentdto.getSegmentUnderSite(siteId, SEGMENT_TYPE));
		Response segmentresponse = postRequest(baseAPM_URI + APMURIConstants.SEGMENT, segmentcontent);
		TypeReference<List<SegmentDTO>> segmentMap = new TypeReference<List<SegmentDTO>>() {
		};
		List<SegmentDTO> segmentlist = objMapper.readValue(segmentresponse.asString(), segmentMap);
		siteAndDecendants.put(APMConstants.SEGMENT, segmentlist.get(0).getSourceKey());
		String assetcontent = objectMapper.writeValueAsString(
				assetdto.getAssetUnderSite(siteId, gatewaylist.get(0).getSourceKey()));
		Response assetresponse = postRequest(baseAPM_URI + APMURIConstants.ASSET, assetcontent);
		TypeReference<List<AssetDTO>> assetMap = new TypeReference<List<AssetDTO>>() {
		};
		List<AssetDTO> assetlist = objMapper.readValue(assetresponse.asString(), assetMap);
		siteAndDecendants.put(APMConstants.ASSET, assetlist.get(0).getIdentifier().getSourceKey());
		return siteAndDecendants;
	}

	protected void validateTypeExistUnderRoot(String sourceKey, String typeClass, String typeName) {
		String uri = baseAPM_URI + APMURIConstants.TYPE + "?typeClass=" + typeClass + "&typeName=" + typeName;
		Response response = RestAssured.given().contentType("application/json").when()
				.header(APMConstants.AUTHORIZATION, apmToken).header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE)
				.get(uri).then().extract().response();
		assertEquals(HttpStatus.OK.value(), response.statusCode());
		List<HashMap<String, Object>> elements = response.jsonPath().getList(APMConstants.CONTENT);
		assertTrue("Type not available under root :" + ROOT_ENTERPRISE + ", sourcekey - " + sourceKey,
				presenceCheck(elements, sourceKey));
	}

	protected Response getAPMResponse(String url, Map<String, String> params, String token) {
		URI uri = appendURI(url, params);
		Response response = given().contentType("application/json").when().header("Authorization", token).get(uri)
				.then().extract().response();
		assertTrue((response.statusCode() == HttpStatus.OK.value()),
				"Assertion Failed:Response Code expected is 200 - Code returned is:" + response.statusCode()
						+ "\n uri : " + uri + "\n");
		return response;
	}

	private void checkAssetVisibilitySite(String assetSourceKey, String siteSourceKey) {
		String uri = RestAssured.baseURI + "/sites/" + siteSourceKey + "/assets" + "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> assets = getResponse(uri).jsonPath().get(APMConstants.CONTENT);
		assertTrue("asset :" + assetSourceKey + " not under site  : " + siteSourceKey,
				presenceCheck(assets, assetSourceKey));
	}

	protected void checkAssetVisibilitySegment(String assetSourceKey, String segmentKey) {
		String uri = RestAssured.baseURI + "/segments/" + segmentKey + "/assets" + "?sort=entryTimestamp,desc";
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

	protected void checkAssetVisibilityRegion(String assetSourceKey, String regionKey) {
		String uri = RestAssured.baseURI + "/regions/" + regionKey + "/assets" + "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> assets = getResponse(uri).jsonPath().get(APMConstants.CONTENT);
		assertTrue("asset :" + assetSourceKey + " not under enterprise  : " + regionKey,
				presenceCheck(assets, assetSourceKey));
	}

	protected Response getResponse(String uri) {
		Response response = RestAssured.given().contentType("application/json").when()
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).get(uri).then().extract().response();
		assertEquals(HttpStatus.OK.value(), response.statusCode());
		return response;
	}

	private void validateSegmentUnderEnterprise(String segmentSourceKey, String enterpriseSourceKey) {
		String uri = RestAssured.baseURI + "/enterprises/" + enterpriseSourceKey + "/segments"
				+ "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> segments = getResponse(uri).jsonPath().get("");
		assertTrue("segment:" + segmentSourceKey + " not under enterprise  : " + enterpriseSourceKey,
				presenceCheck(segments, segmentSourceKey));
	}

	private void validateSegmentUnderSite(String segmentSourceKey, String siteSourceKey) {
		String uri = RestAssured.baseURI + "/sites/" + siteSourceKey + "/segments" + "?sort=entryTimestamp,desc";
		List<HashMap<String, Object>> segments = getResponse(uri).jsonPath().get("");
		assertTrue("segment:" + segmentSourceKey + " not under site  : " + siteSourceKey,
				presenceCheck(segments, segmentSourceKey));
	}

	protected Response patchRequest(String uri, String content) {
		Response response = RestAssured.given().contentType("application/json").body(content)
				.header(APMConstants.AUTHORIZATION, apmToken).expect().statusCode(HttpStatus.OK.value()).when()
				.patch(uri).then().extract().response();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.OK.value());
		return response;
	}

	protected Response putRequest(String uri, String content) {
		Response response = RestAssured.given().contentType("application/json").body(content)
				.header(APMConstants.AUTHORIZATION, apmToken).expect().statusCode(HttpStatus.OK.value()).when().put(uri)
				.then().extract().response();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.OK.value());
		return response;
	}

	protected void childValidation(String sourceKey, String parentSourceKey, String parentClassification) {
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

	protected void createSiteUnderEnterprise() throws Exception {
		String sitecontent = objectMapper.writeValueAsString(sitedto.getSiteUnderEnterprise(ENTERPRISE_ID));
		Response response = postRequest(baseAPM_URI + APMURIConstants.SITE, sitecontent);
		TypeReference<List<SiteDTO>> siteMap = new TypeReference<List<SiteDTO>>() {
		};
		List<SiteDTO> sitelist = objMapper.readValue(response.asString(), siteMap);
		validateSite(sitelist);
	}
	
	protected void postRequestForNegativeCases(String uri, String content,int status) {
		Response response = RestAssured.given().contentType("application/json").body(content)
				.header(APMConstants.AUTHORIZATION, apmToken).when()
				.post(uri).then().extract().response();
		assertTrue(getFailureMessage(status, response), response.getStatusCode() == status);
		
	}
	
	protected void createTypeWithoutUOM(String content,int status) throws Exception {
		String uri = baseAPM_URI + APMURIConstants.TYPE;
		Response response = RestAssured.given().contentType("application/json").body(content)
				.header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE).when()
				.header(APMConstants.AUTHORIZATION, apmToken).post(uri).then().extract().response();
		assertTrue(getFailureMessage(status, response), response.getStatusCode() == status);

	}

}
