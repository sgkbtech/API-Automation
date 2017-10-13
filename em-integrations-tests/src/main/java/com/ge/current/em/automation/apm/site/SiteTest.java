package com.ge.current.em.automation.apm.site;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.current.em.automation.apm.APMComponentTestUtil;
import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.APMURIConstants;
import com.ge.current.em.automation.apm.dto.PropertiesHelper;
import com.ge.current.em.automation.apm.dto.SiteHelper;
import com.ge.current.em.persistenceapi.dto.GatewayDTO;
import com.ge.current.em.persistenceapi.dto.LocationDTO;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.SiteDTO;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.HttpMethod;

public class SiteTest extends APMComponentTestUtil {

	private PropertiesHelper propertiesHelper = new PropertiesHelper();
	private SiteHelper siteHelper = new SiteHelper();
	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testSiteProperties() throws Exception {
		loadSiteProperties();
		updateNewSiteproperties();
		updateExistingSiteproperties();
	}

	private void loadSiteProperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToLoad());
		JsonPath in = responseCheck(uri, content, HttpMethod.PUT.name(), HttpStatus.OK.value(), "application/json");
		String URI = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID;
		responseValidate(URI, in, "get", HttpStatus.OK.value(), "application/json");
	}

	private void updateNewSiteproperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToAdd());
		JsonPath contentJson = new JsonPath(content);
		String geturi = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		beforePatch.addAll(contentJson.getList(APMConstants.LIST));
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertTrue("beforePatch & afterPatch are not having same elements list", beforePatch.containsAll(afterPatch));
	}

	private void updateExistingSiteproperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getPropertiesToUpdate());
		String geturi = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertEquals(beforePatch.size(), afterPatch.size());
		assertNotEquals(beforePatch, afterPatch);
	}

	@Test
	public void testCreateSiteUnderEnterprise() throws Exception {
		String content = objectMapper.writeValueAsString(siteHelper.getSiteUnderEnterprise(ENTERPRISE_ID));
		Response response = postRequest(baseAPM_URI + APMURIConstants.SITE, content);
		TypeReference<List<SiteDTO>> siteMap = new TypeReference<List<SiteDTO>>() {
		};
		List<SiteDTO> list = objMapper.readValue(response.asString(), siteMap);
		validateSite(list);
	}

	@Test
	public void testCreateSiteUnderSite() throws Exception {
		String content = objectMapper.writeValueAsString(siteHelper.getSiteUnderSite(SITE_ID));
		Response response = postRequest(baseAPM_URI + APMURIConstants.SITE, content);
		TypeReference<List<SiteDTO>> siteMap = new TypeReference<List<SiteDTO>>() {
		};
		List<SiteDTO> list = objMapper.readValue(response.asString(), siteMap);
		validateSite(list);
	}

	@Test
	public void testCreateSiteUnderRegion() throws Exception {
		String content = objectMapper.writeValueAsString(siteHelper.getSiteUnderRegion(REGION_ID));
		Response response = postRequest(baseAPM_URI + APMURIConstants.SITE, content);
		TypeReference<List<SiteDTO>> siteMap = new TypeReference<List<SiteDTO>>() {
		};
		List<SiteDTO> list = objMapper.readValue(response.asString(), siteMap);
		validateSite(list);
	}

	@Test
	private void testSiteGatewayUnderSite() throws Exception {
		String content = objectMapper.writeValueAsString(siteHelper.getGatewayAsset());
		TypeReference<List<GatewayDTO>> gateWayTypeRef = new TypeReference<List<GatewayDTO>>() {
		};
		List<GatewayDTO> list = objMapper.readValue(
				postRequest(baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID
						+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.GATEWAY, content).asString(),
				gateWayTypeRef);
		validateGatewayAsset(list);
		validateGatewayUnderSite(list, SITE_ID);
	}

	@Test
	private void testGetSiteLocations() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.LOCATIONS;
		TypeReference<List<LocationDTO>> siteLocMap = new TypeReference<List<LocationDTO>>() {
		};
		List<LocationDTO> list = objMapper
				.readValue(getAPMResponse(uri, new HashMap<String, String>(), apmToken).asString(), siteLocMap);
		validateSiteLocations(list);
	}

	@Test
	public void testGetSiteDescendants() throws Exception {
		Map<String,String> siteAndDecendants = createSiteDescendants();
		Map<String, String> params = new HashMap<String, String>();
		params.put("maxDepth", "1");
		String url = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + siteAndDecendants.get(APMConstants.SITE)
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.DESCENDANTS;
		List<Map<String,Object>> decendants = getAPMResponse(url, params, AUTH_TOKEN).jsonPath().getList("children");
	}
	
	@Test
	public void testCreateSiteUnderSegment() throws Exception {
		String content = objectMapper.writeValueAsString(siteHelper.getSiteUnderSegment(SEGMENT_ID));
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.SITE, content,HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	public void testCreateSiteWithoutParent() throws Exception {
		String content = objectMapper.writeValueAsString(siteHelper.getSiteWithoutParent());
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.SITE, content,HttpStatus.BAD_REQUEST.value());
	}

}
