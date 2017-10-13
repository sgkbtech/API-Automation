package com.ge.current.em.automation.apm.segment;

import static org.testng.Assert.assertNotEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ge.current.em.automation.apm.APMComponentTestUtil;
import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.APMURIConstants;
import com.ge.current.em.automation.apm.dto.AssetHelper;
import com.ge.current.em.automation.apm.dto.PropertiesHelper;
import com.ge.current.em.automation.apm.dto.SegmentHelper;
import com.ge.current.em.persistenceapi.dto.AssetDTO;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.SegmentDTO;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.HttpMethod;

public class SegmentTest extends APMComponentTestUtil {
	private PropertiesHelper propertiesHelper = new PropertiesHelper();
	private SegmentHelper segmentHelper = new SegmentHelper();
	private AssetHelper assetHelper = new AssetHelper();

	@Test
	public void testSegmentProperties() throws Exception {
		loadSegmentProperties();
		updateNewSegmentproperties();
		updateExistingSegmentproperties();
	}

	private void loadSegmentProperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR + SEGMENT_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToLoad());
		JsonPath in = responseCheck(uri, content, HttpMethod.PUT.name(), HttpStatus.OK.value(), "application/json");
		String URI = baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR + SEGMENT_ID;
		responseValidate(URI, in, "get", HttpStatus.OK.value(), "application/json");
	}

	private void updateNewSegmentproperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR + SEGMENT_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToAdd());
		JsonPath contentJson = new JsonPath(content);
		String geturi = baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR + SEGMENT_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		beforePatch.addAll(contentJson.getList(APMConstants.LIST));
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertTrue("beforePatch & afterPatch are not having same elements list", beforePatch.containsAll(afterPatch));
	}

	private void updateExistingSegmentproperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR + SEGMENT_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getPropertiesToUpdate());
		String geturi = baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR + SEGMENT_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		AssertJUnit.assertEquals(beforePatch.size(), afterPatch.size());
		assertNotEquals(beforePatch, afterPatch);
	}

	@Test
	public void testCreateSegmentUnderSite() throws Exception {
		String content = objectMapper.writeValueAsString(segmentHelper.getSegmentUnderSite(SITE_ID, SEGMENT_TYPE));
		Response response = postRequest(baseAPM_URI + APMURIConstants.SEGMENT, content);
		TypeReference<List<SegmentDTO>> siteMap = new TypeReference<List<SegmentDTO>>() {
		};
		List<SegmentDTO> list = objMapper.readValue(response.asString(), siteMap);
		validateSegment(list);
	}

	@Test
	public void testCreateSegmentUnderSegment() throws Exception {
		String content = objectMapper
				.writeValueAsString(segmentHelper.getSegmentUnderSegment(SEGMENT_ID, SEGMENT_TYPE));
		Response response = postRequest(baseAPM_URI + APMURIConstants.SEGMENT, content);
		TypeReference<List<SegmentDTO>> siteMap = new TypeReference<List<SegmentDTO>>() {
		};
		List<SegmentDTO> list = objMapper.readValue(response.asString(), siteMap);
		validateSegment(list);
	}

	@Test
	public void testPatchSegmentChildren() throws Exception {
		String content = objectMapper.writeValueAsString(assetHelper.getAssetUnderSite(SITE_ID, GATEWAY_ASSET));
		Response response = postRequest(baseAPM_URI + APMURIConstants.ASSET, content);
		TypeReference<List<AssetDTO>> assetMap = new TypeReference<List<AssetDTO>>() {
		};
		List<AssetDTO> list = objMapper.readValue(response.asString(), assetMap);
		String patchContent = objectMapper
				.writeValueAsString(assetHelper.getAssetForPatch(list.get(0).getIdentifier().getSourceKey()));
		patchRequest(baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR
				+ SEGMENT_ID + APMURIConstants.PATH_SEPARATOR + APMURIConstants.CHILDREN, patchContent);
		checkAssetVisibilitySegment(list.get(0).getIdentifier().getSourceKey(), SEGMENT_ID);
	}

	@Test
	public void testPutSegmentChildren() throws Exception {
		String content = objectMapper.writeValueAsString(assetHelper.getAssetUnderSite(SITE_ID, GATEWAY_ASSET));
		Response response = postRequest(baseAPM_URI + APMURIConstants.ASSET, content);
		TypeReference<List<AssetDTO>> assetMap = new TypeReference<List<AssetDTO>>() {
		};
		List<AssetDTO> list = objMapper.readValue(response.asString(), assetMap);
		String patchContent = objectMapper
				.writeValueAsString(assetHelper.getAssetForPatch(list.get(0).getIdentifier().getSourceKey()));
		putRequest(baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR
				+ SEGMENT_ID + APMURIConstants.PATH_SEPARATOR + APMURIConstants.CHILDREN, patchContent);
		checkAssetVisibilitySegment(list.get(0).getIdentifier().getSourceKey(), SEGMENT_ID);
	}
	
	@Test
	public void testCreateSegmentWithoutParent() throws Exception {
		String content = objectMapper.writeValueAsString(segmentHelper.getSegmentWithoutParent(SEGMENT_TYPE));
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.SEGMENT, content,HttpStatus.BAD_REQUEST.value());
	}

}
