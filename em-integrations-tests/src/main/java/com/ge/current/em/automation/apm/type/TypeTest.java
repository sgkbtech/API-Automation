package com.ge.current.em.automation.apm.type;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.ge.current.em.automation.apm.APMComponentTestUtil;
import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.APMURIConstants;
import com.ge.current.em.automation.apm.dto.PropertiesHelper;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.HttpMethod;

public class TypeTest extends APMComponentTestUtil {
	
	private PropertiesHelper propertiesHelper = new PropertiesHelper();

	@Test
	public void testTypeProperties() throws Exception {
		loadTypeProperties();
		updateNewTyperProperties();
		updateExistingTypeProperties();
	}

	private void loadTypeProperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.TYPE + APMURIConstants.PATH_SEPARATOR + TYPE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToLoad());
		JsonPath in = responseCheck(uri, content, HttpMethod.PUT.name(), HttpStatus.OK.value(), "application/json");
		String URI = baseAPM_URI + APMURIConstants.TYPE + APMURIConstants.PATH_SEPARATOR + TYPE_ID;
		responseValidate(URI, in, "get", HttpStatus.OK.value(), "application/json");
	}

	private void updateNewTyperProperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.TYPE + APMURIConstants.PATH_SEPARATOR + TYPE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToAdd());
		JsonPath contentJson = new JsonPath(content);
		String geturi = baseAPM_URI + APMURIConstants.TYPE + APMURIConstants.PATH_SEPARATOR + TYPE_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		beforePatch.addAll(contentJson.getList(APMConstants.LIST));
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertTrue("beforePatch & afterPatch are not having same elements list", beforePatch.containsAll(afterPatch));
	}

	private void updateExistingTypeProperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.TYPE + APMURIConstants.PATH_SEPARATOR + TYPE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getPropertiesToUpdate());
		String geturi = baseAPM_URI + APMURIConstants.TYPE + APMURIConstants.PATH_SEPARATOR + TYPE_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		AssertJUnit.assertEquals(beforePatch.size(), afterPatch.size());
		assertNotEquals(beforePatch, afterPatch);
	}

}
