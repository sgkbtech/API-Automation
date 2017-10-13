package com.ge.current.em.automation.apm.asset;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertNotEquals;

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
import com.ge.current.em.persistenceapi.dto.AssetDTO;
import com.ge.current.em.persistenceapi.dto.MetaEntityDTO;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.HttpMethod;

public class AssetTest extends APMComponentTestUtil {

	private PropertiesHelper propertiesDto = new PropertiesHelper();
	private AssetHelper assetHelper= new AssetHelper();

	@Test
	public void testAssetProperties() throws Exception {
		loadAssetProperties();
		updateNewAssetproperties();
		updateExistingAssetproperties();
	}

	private void loadAssetProperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + ASSET_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesDto.getNewPropertiesToLoad());
		JsonPath in = responseCheck(uri, content, HttpMethod.PUT.name(), HttpStatus.OK.value(), "application/json");
		String URI = baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + ASSET_ID;
		responseValidate(URI, in, "get", HttpStatus.OK.value(), "application/json");
	}

	private void updateNewAssetproperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + ASSET_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesDto.getNewPropertiesToAdd());
		JsonPath contentJson = new JsonPath(content);
		String geturi = baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + ASSET_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		beforePatch.addAll(contentJson.getList(APMConstants.LIST));
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertTrue("beforePatch & afterPatch are not having same elements list", beforePatch.containsAll(afterPatch));
	}

	private void updateExistingAssetproperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + ASSET_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesDto.getPropertiesToUpdate());
		String geturi = baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + ASSET_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		AssertJUnit.assertEquals(beforePatch.size(), afterPatch.size());
		assertNotEquals(beforePatch, afterPatch);
	}

	@Test
	public void testCreateAssetUnderSite() throws Exception {
		String content = objectMapper.writeValueAsString(assetHelper.getAssetUnderSite(SITE_ID, GATEWAY_ASSET));
		Response response = postRequest(baseAPM_URI + APMURIConstants.ASSET, content);
		TypeReference<List<AssetDTO>> assetMap = new TypeReference<List<AssetDTO>>() {
		};
		List<AssetDTO> list = objMapper.readValue(response.asString(), assetMap);
		validateAsset(list,APMConstants.SITE,SITE_ID);
	}

	@Test
	public void testCreateAssetUnderSegment() throws Exception {
		String content = objectMapper.writeValueAsString(assetHelper.getAssetUnderSegment(SEGMENT_ID, GATEWAY_ASSET));
		Response response = postRequest(baseAPM_URI + APMURIConstants.ASSET, content);
		TypeReference<List<AssetDTO>> assetMap = new TypeReference<List<AssetDTO>>() {
		};
		List<AssetDTO> list = objMapper.readValue(response.asString(), assetMap);
		validateAsset(list,APMConstants.SEGMENT,SEGMENT_ID);
	}

	@Test
	public void testGetAssetParents() throws Exception {
		Response response = getRequest(baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + ASSET_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PARENTS);
		TypeReference<List<MetaEntityDTO>> assetMap = new TypeReference<List<MetaEntityDTO>>() {
		};
		List<MetaEntityDTO> list = objMapper.readValue(response.asString(), assetMap);
		validateParentDetailForAsset(ASSET_ID, list.get(0).getSourceKey(), list.get(0).getClassificationCode());

	}
	
	@Test
	public void testCreateAssetWithoutGatewayInfo() throws Exception {
		String content = objectMapper.writeValueAsString(assetHelper.getAssetWithoutGatewayInfo(SITE_ID));
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.ASSET, content,HttpStatus.BAD_REQUEST.value());
		
	}

}
