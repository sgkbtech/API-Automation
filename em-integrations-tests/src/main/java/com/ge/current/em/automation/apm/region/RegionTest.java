package com.ge.current.em.automation.apm.region;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.current.em.automation.apm.APMComponentTestUtil;
import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.APMURIConstants;
import com.ge.current.em.automation.apm.dto.PropertiesHelper;
import com.ge.current.em.automation.apm.dto.RegionHelper;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.RegionDTO;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.HttpMethod;

public class RegionTest extends APMComponentTestUtil {

	private ObjectMapper objectMapper = new ObjectMapper();
	private RegionHelper regionHelper = new RegionHelper();
	private PropertiesHelper propertiesHelper = new PropertiesHelper();

	@Test
	public void testRegionProperties() throws Throwable {
		loadRegionProperties();
		updateNewRegionproperties();
		updateExistingRegionproperties();
	}

	private void loadRegionProperties() throws Throwable {
		String uri = baseAPM_URI + APMURIConstants.REGION + APMURIConstants.PATH_SEPARATOR + REGION_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String enterprisedetails = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToLoad());
		JsonPath in = responseCheck(uri, enterprisedetails, HttpMethod.PUT.name(), HttpStatus.OK.value(), "application/json");
		String URI = baseAPM_URI + APMURIConstants.REGION + APMURIConstants.PATH_SEPARATOR + REGION_ID;
		responseValidate(URI, in, "get", HttpStatus.OK.value(), "application/json");
	}

	private void updateNewRegionproperties() throws Throwable {
		String uri = baseAPM_URI + APMURIConstants.REGION + APMURIConstants.PATH_SEPARATOR + REGION_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToAdd());
		JsonPath contentJson = new JsonPath(content);
		String geturi = baseAPM_URI + APMURIConstants.REGION + APMURIConstants.PATH_SEPARATOR + REGION_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		beforePatch.addAll(contentJson.getList(APMConstants.LIST));
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertTrue("beforePatch & afterPatch are not having same elements list", beforePatch.containsAll(afterPatch));

	}

	private void updateExistingRegionproperties() throws Throwable {
		String uri = baseAPM_URI + APMURIConstants.REGION + APMURIConstants.PATH_SEPARATOR + REGION_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getPropertiesToUpdate());
		String geturi = baseAPM_URI + APMURIConstants.REGION + APMURIConstants.PATH_SEPARATOR + REGION_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertEquals(beforePatch.size(), afterPatch.size());
		assertNotEquals(beforePatch, afterPatch);
	}

	@Test
	public void testCreateRegionUnderEnterprise() throws Exception {
		String content = objectMapper.writeValueAsString(regionHelper.getRegionUnderEnterprise(ROOT_ENTERPRISE));
		Response response = postRequest(baseAPM_URI + APMURIConstants.REGION, content);
		TypeReference<List<RegionDTO>> regionMap = new TypeReference<List<RegionDTO>>() {
		};
		List<RegionDTO> regions = objMapper.readValue(response.asString(), regionMap);
		validateRegion(regions);
	}

	@Test
	public void testCreateRegionUnderRegion() throws Exception {
		String content = objectMapper.writeValueAsString(regionHelper.getRegionUnderRegion(REGION_ID));
		Response response = postRequest(baseAPM_URI + APMURIConstants.REGION, content);
		TypeReference<List<RegionDTO>> regionMap = new TypeReference<List<RegionDTO>>() {
		};
		List<RegionDTO> regions = objMapper.readValue(response.asString(), regionMap);
		validateRegion(regions);
	}
	
	@Test
	public void testCreateRegionUnderSite() throws Exception {
		String content = objectMapper.writeValueAsString(regionHelper.getRegionUnderSite(SITE_ID));
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.REGION, content,HttpStatus.BAD_REQUEST.value());
    }

	@Test
	public void testCreateRegionUnderSegment() throws Exception {
		String content = objectMapper.writeValueAsString(regionHelper.getRegionUnderSegment(SEGMENT_ID));
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.REGION, content,HttpStatus.BAD_REQUEST.value());
    }
	
	@Test
	public void testCreateRegionWithoutParent() throws Exception {
		String content = objectMapper.writeValueAsString(regionHelper.getRegionWithoutParent());
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.REGION, content,HttpStatus.BAD_REQUEST.value());
	}

}
