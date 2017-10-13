package com.ge.current.em.automation.apm.enterprise;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.current.em.automation.apm.APMComponentTestUtil;
import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.APMURIConstants;
import com.ge.current.em.automation.apm.dto.EnterpriseHelper;
import com.ge.current.em.automation.apm.dto.PropertiesHelper;
import com.ge.current.em.automation.apm.dto.TagHelper;
import com.ge.current.em.automation.apm.dto.TypeHelper;
import com.ge.current.em.persistenceapi.dto.EnterpriseDTO;
import com.ge.current.em.persistenceapi.dto.MetaTypeDTO;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.TagAssociationDTO;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.HttpMethod;

public class EnterpriseTest extends APMComponentTestUtil {

	private ObjectMapper objectMapper = new ObjectMapper();
	private EnterpriseHelper enterpriseHelper = new EnterpriseHelper();
	private PropertiesHelper propertiesHelper = new PropertiesHelper();
	private TypeHelper typeHelper = new TypeHelper();
	private TagHelper tagDto = new TagHelper();

	// @Test
	public void testCreateRootEnterprise() throws Exception {
		String content = objectMapper.writeValueAsString(enterpriseHelper.getRootEnterprise());
		Response response = postRequest(baseAPM_URI + APMURIConstants.ENTERPRISE, content);
		TypeReference<List<EnterpriseDTO>> entMap = new TypeReference<List<EnterpriseDTO>>() {
		};
		List<EnterpriseDTO> list = objMapper.readValue(response.asString(), entMap);
		validateEnterprise(list);
	}

	@Test
	public void testEnterpriseUnderRootEnterprise() throws Exception {
		String content = objectMapper.writeValueAsString(enterpriseHelper.getNonRootEnterprise(ROOT_ENTERPRISE));
		Response response = postRequest(baseAPM_URI + APMURIConstants.ENTERPRISE, content);
		TypeReference<List<EnterpriseDTO>> entMap = new TypeReference<List<EnterpriseDTO>>() {
		};
		List<EnterpriseDTO> list = objMapper.readValue(response.asString(), entMap);
		validateEnterprise(list);
	}

	@Test
	public void testEnterpriseProperties() throws Exception {
		loadPropertiesForEnterprise();
		addNewPropertiesForEnterprise();
		updateExistingPropertiesForEnterprise();
	}

	@Test
	public void testCreateTagType() throws Exception {
		String content = objectMapper.writeValueAsString(typeHelper.getTagType());
		Response response = createType(content);
		TypeReference<List<MetaTypeDTO>> tagMap = new TypeReference<List<MetaTypeDTO>>() {
		};
		List<MetaTypeDTO> list = objMapper.readValue(response.asString(), tagMap);
		validateType(list);
	}

	@Test
	public void testCreateAssetType() throws Exception {
		String content = objectMapper.writeValueAsString(typeHelper.getAssetType());
		Response response = createType(content);
		TypeReference<List<MetaTypeDTO>> typeMap = new TypeReference<List<MetaTypeDTO>>() {
		};
		List<MetaTypeDTO> list = objMapper.readValue(response.asString(), typeMap);
		validateType(list);
		createAssetModelType(list.stream().findFirst().get().getSourceKey());
	}

	@Test
	public void testCreateSegmentType() throws Exception {
		String content = objectMapper.writeValueAsString(typeHelper.getSegmentType());
		Response response = createType(content);
		TypeReference<List<MetaTypeDTO>> typeMap = new TypeReference<List<MetaTypeDTO>>() {
		};
		List<MetaTypeDTO> list = objMapper.readValue(response.asString(), typeMap);
		validateType(list);
	}

	private void loadPropertiesForEnterprise() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + ENTERPRISE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToLoad());
		JsonPath in = responseCheck(uri, content, HttpMethod.PUT.name(), HttpStatus.OK.value(), "application/json");
		String URI = baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + ENTERPRISE_ID;
		responseValidate(URI, in, "get", HttpStatus.OK.value(), "application/json");
	}

	private void addNewPropertiesForEnterprise() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + ENTERPRISE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getNewPropertiesToAdd());
		JsonPath contentJson = new JsonPath(content);
		String geturi = baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + ENTERPRISE_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		beforePatch.addAll(contentJson.getList(APMConstants.LIST));
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertTrue("beforePatch & afterPatch are not having same elements list", beforePatch.containsAll(afterPatch));
	}

	private void updateExistingPropertiesForEnterprise() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + ENTERPRISE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.PROPERTIES;
		String content = objectMapper.writeValueAsString(propertiesHelper.getPropertiesToUpdate());
		String geturi = baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + ENTERPRISE_ID;
		Response responseBeforePatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> beforePatch = responseBeforePatch.jsonPath().getList(APMConstants.PROPERTIES);
		responseCheck(uri, content, HttpMethod.PATCH.name(), HttpStatus.OK.value(), "application/json");
		Response responseAfterPatch = getAPMResponse(geturi, new HashMap<String, String>(), apmToken);
		List<PropertiesDTO> afterPatch = responseAfterPatch.jsonPath().getList(APMConstants.PROPERTIES);
		assertEquals(beforePatch.size(), afterPatch.size());
		assertNotEquals(beforePatch, afterPatch);
	}

	private void createAssetModelType(String parentSourceKey) throws Exception {
		String content = objectMapper.writeValueAsString(typeHelper.getAssetModelType(parentSourceKey));
		Response response = createType(content);
		TypeReference<List<MetaTypeDTO>> typeMap = new TypeReference<List<MetaTypeDTO>>() {
		};
		List<MetaTypeDTO> list = objMapper.readValue(response.asString(), typeMap);
		validateType(list);
	}

	@Test
	public void testCreateTagUnderEnterprise() throws Exception {
		String uri = baseAPM_URI + APMURIConstants.ENTERPRISE + APMURIConstants.PATH_SEPARATOR + ENTERPRISE_ID
				+ APMURIConstants.PATH_SEPARATOR + APMURIConstants.TAGS;
		String content = objectMapper.writeValueAsString(tagDto.getTag(getTagType("dischargeAirTempSensor")));
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
	
	@Test
	public void testCreateEnterpriseUnderSite() throws Exception {
		String content = objectMapper.writeValueAsString(enterpriseHelper.getEnterpriseUnderSite(SITE_ID));
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.ENTERPRISE, content,HttpStatus.BAD_REQUEST.value());
    }
	
	@Test
	public void testCreateEnterpriseUnderSegment() throws Exception {
		String content = objectMapper.writeValueAsString(enterpriseHelper.getEnterpriseUnderSegment(SEGMENT_ID));
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.ENTERPRISE, content,HttpStatus.BAD_REQUEST.value());
    }
	
	@Test
	public void testCreateEnterpriseUnderRegion() throws Exception {
		String content = objectMapper.writeValueAsString(enterpriseHelper.getEnterpriseUnderRegion(REGION_ID));
		postRequestForNegativeCases(baseAPM_URI + APMURIConstants.ENTERPRISE, content,HttpStatus.BAD_REQUEST.value());
    }
	
	@Test
	public void testCreateTagTypeWithoutDataType() throws Exception {
		String content = objectMapper.writeValueAsString(typeHelper.getTagTypeWithoutDataType());
		createTypeWithoutUOM(content,HttpStatus.BAD_REQUEST.value());
	}

}
