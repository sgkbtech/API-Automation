package com.ge.current.em.automation.apm.tag;

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
import com.ge.current.em.automation.apm.dto.PropertiesHelper;
import com.ge.current.em.automation.apm.dto.TagHelper;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.TagAssociationDTO;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.HttpMethod;

public class TagTest extends APMComponentTestUtil {

	private PropertiesHelper propertiesHelper = new PropertiesHelper();
	private TagHelper tagHelper = new TagHelper();

	@Test
	public void testTagProperties() throws Exception {
		loadTagProperties();
		updateNewTagproperties();
		updateExistingTagproperties();
	}

	private void loadTagProperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.TAGS + APMURIConstants.PATH_SEPARATOR + TAG_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToLoad());
		JsonPath in = responseCheck(uri, content, HttpMethod.PUT.name(), HttpStatus.OK.value(), "application/json");
		String URI = baseAPM_URI + APMURIConstants.TAGS + APMURIConstants.PATH_SEPARATOR + TAG_ID;
		responseValidate(URI, in, "get", HttpStatus.OK.value(), "application/json");
	}

	private void updateNewTagproperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.TAGS + APMURIConstants.PATH_SEPARATOR + TAG_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToAdd());
		JsonPath contentJson = new JsonPath(content);
		String geturi = baseAPM_URI + APMURIConstants.TAGS + APMURIConstants.PATH_SEPARATOR + TAG_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		beforePatch.addAll(contentJson.getList(APMConstants.LIST));
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertTrue("beforePatch & afterPatch are not having same elements list", beforePatch.containsAll(afterPatch));
	}

	private void updateExistingTagproperties() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.TAGS + APMURIConstants.PATH_SEPARATOR + TAG_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getPropertiesToUpdate());
		String geturi = baseAPM_URI + APMURIConstants.TAGS + APMURIConstants.PATH_SEPARATOR + TAG_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		AssertJUnit.assertEquals(beforePatch.size(), afterPatch.size());
		assertNotEquals(beforePatch, afterPatch);
	}

	@Test
	public void testCreateTagUnderEnterprise() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + ENTERPRISE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.TAGS;
		String content = objectMapper.writeValueAsString(tagHelper.getTag(getTagType("dischargeAirTempSensor")));
		Response response = postRequest(uri, content);
		TypeReference<TagAssociationDTO> tagMap = new TypeReference<TagAssociationDTO>() {
		};
		TagAssociationDTO tagAssociate = objMapper.readValue(response.asString(), tagMap);
		validateTag(tagAssociate.getTags());
	}

	@Test
	public void testCreateTagUnderSite() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SITE + APMURIConstants.PATH_SEPARATOR + SITE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.TAGS;
		String content = objectMapper.writeValueAsString(tagHelper.getTag(getTagType("dischargeAirTempSensor")));
		Response response = postRequest(uri, content);
		TypeReference<TagAssociationDTO> tagMap = new TypeReference<TagAssociationDTO>() {
		};
		TagAssociationDTO tagAssociate = objMapper.readValue(response.asString(), tagMap);
		validateTag(tagAssociate.getTags());
	}

	@Test
	public void testCreateTagUnderSegment() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.SEGMENT + APMURIConstants.PATH_SEPARATOR + SEGMENT_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.TAGS;
		String content = objectMapper.writeValueAsString(tagHelper.getTag(getTagType("dischargeAirTempSensor")));
		Response response = postRequest(uri, content);
		TypeReference<TagAssociationDTO> tagMap = new TypeReference<TagAssociationDTO>() {
		};
		TagAssociationDTO tagAssociate = objMapper.readValue(response.asString(), tagMap);
		validateTag(tagAssociate.getTags());
	}

	@Test
	public void testCreateTagUnderAsset() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ASSET + APMURIConstants.PATH_SEPARATOR + ASSET_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.TAGS;
		String content = objectMapper.writeValueAsString(tagHelper.getTag(getTagType("dischargeAirTempSensor")));
		Response response = postRequest(uri, content);
		TypeReference<TagAssociationDTO> tagMap = new TypeReference<TagAssociationDTO>() {
		};
		TagAssociationDTO tagAssociate = objMapper.readValue(response.asString(), tagMap);
		validateTag(tagAssociate.getTags());
	}

	private String getTagType(String tagTypeName) {
		String uri = baseAPM_URI + APMURIConstants.TYPE + "?typeClass=TAG_TYPE&typeName=" + tagTypeName + "&size=20";
		Response response = RestAssured.given().contentType("application/json").when()
				.header(APMConstants.AUTHORIZATION, apmToken).header("ROOT_ENTERPRISE_SOURCEKEY", ROOT_ENTERPRISE)
				.get(uri).then().extract().response();
		List<HashMap<String, Object>> list = response.jsonPath().getList(APMConstants.CONTENT);
		for (HashMap<String, Object> e : list) {
			return e.get(APMConstants.SOURCE_KEY).toString();
		}
		return null;
	}

}
