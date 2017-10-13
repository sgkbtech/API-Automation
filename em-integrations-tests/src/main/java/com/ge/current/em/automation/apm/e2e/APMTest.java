package com.ge.current.em.automation.apm.e2e;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ge.current.em.automation.provider.APMDataProvider;
import com.ge.current.em.automation.util.APMTestUtil;
import com.ge.current.em.utils.APMChildrenType;
import com.ge.current.em.utils.APMParentType;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class APMTest extends APMTestUtil {

	private static final Log logger = LogFactory.getLog(APMTest.class);
	private static final String[] types = { "ASSET_TYPE", "ASSET_MODEL_TYPE", "TAG_TYPE", "SEGMENT_TYPE" };
	private static final String[] tagDataTypes = { "NUMBER", "DOUBLE", "STRING", "BOOLEAN", "ENUM" };
	private static final Set<String> ACCEPTABLE_TYPES = new HashSet<String>(Arrays.asList(types));
	private static final Set<String> TAG_DATA_TYPES = new HashSet<String>(Arrays.asList(tagDataTypes));
	private static final String ASSET_KEY_PATTERN = "ASSET_";

	@Parameters({ "URL", "Headers" })
	@Test(dataProvider = "listOfAPMServices", dataProviderClass = APMDataProvider.class)
	public void testAvailabilityAPMServices(URI uri, Map<String, String> headers) throws URISyntaxException {
		String assertionErrorMessage = "";
		Response response = RestAssured.given().contentType("application/json").headers(headers).when()
				.header("Authorization", AUTH_TOKEN).get(uri).then().extract().response();
		assertionErrorMessage = assertionErrorMessage + " \n " + uri
				+ " - expected responsecode : HttpStatus.OK.value() , received response code : " + response.statusCode();
		logger.info(uri + " - response code : " + response.statusCode());
		assertTrue(assertionErrorMessage, response.statusCode() == HttpStatus.OK.value());
	}

	@Parameters({ "URL", "Headers" })
	@Test(dataProvider = "listOfAPMServicesWithPage", dataProviderClass = APMDataProvider.class)
	public void testPageParamAPMServices(URI uri, Map<String, String> headers) throws URISyntaxException {
		Response response = RestAssured.given().contentType("application/json").headers(headers).when()
				.header("Authorization", AUTH_TOKEN).get(uri).then().extract().response();
		JsonPath jsonPath = response.jsonPath();
		int totalElements = Integer.valueOf(jsonPath.get("totalElements").toString());
		int size = Integer.valueOf(jsonPath.get("size").toString());
		int totalPages = Integer.valueOf(jsonPath.get("totalPages").toString());
		if (totalElements > size) {
			assertTrue("Pagination not proper", totalPages > 1);
		} else if (totalElements == 0) {
			assertTrue("Pagination not proper", totalPages == 0 || totalPages == 1);
		} else {
			assertTrue("Pagination not proper", totalPages == 1);
		}
	}

	@Parameters({ "URL", "Headers" })
	@Test(dataProvider = "listOfAPMServicesWithSort", dataProviderClass = APMDataProvider.class)
	public void testSortParamAPMServices(URI uri, Map<String, String> headers) throws URISyntaxException {
		String ascSortUri = uri.toString() + (uri.toString().contains("?") ? "&sort=name" : "?sort=name");
		String descSortUri = uri.toString() + (uri.toString().contains("?") ? "&sort=name,desc" : "?sort=name,desc");
		Response ascResponse = RestAssured.given().contentType("application/json").headers(headers).when()
				.header("Authorization", AUTH_TOKEN).get(ascSortUri).then().extract().response();
		Response descResponse = RestAssured.given().contentType("application/json").headers(headers).when()
				.header("Authorization", AUTH_TOKEN).get(descSortUri).then().extract().response();
		assertTrue("URI asc : " + ascSortUri + "\n URI desc : " + descSortUri,
				ascResponse.statusCode() == HttpStatus.OK.value() && descResponse.statusCode() == HttpStatus.OK.value());
		JsonPath ascJsonPath = ascResponse.jsonPath();
		JsonPath descJsonPath = descResponse.jsonPath();
		List<HashMap<String, Object>> ascContent = ascJsonPath.get("content");
		List<HashMap<String, Object>> descContent = descJsonPath.get("content");
		if (ascContent.isEmpty()) {
			throw new SkipException("Content is empty , sort testing is Skipped \n" + ascSortUri);
		}
		assertTrue("Content size comparison failed for asc and desc ", ascContent.size() == descContent.size());
		if (!ascContent.isEmpty()) {
			int totalElements = Integer.valueOf(ascJsonPath.get("totalElements").toString());
			int totalPages = Integer.valueOf(ascJsonPath.get("totalPages").toString());
			if (totalPages == 0 || totalPages == 1) {
				assertTrue("content comparison failed for sorting",
						ascContent.get(0).get("name").equals(descContent.get(totalElements - 1).get("name")));
			} else {
				assertTrue("content comparison failed for sorting",
						!ascContent.get(0).get("name").equals(descContent.get(descContent.size() - 1).get("name")));
				assertTrue("content comparison failed for sorting",
						!ascContent.get(0).get("name").equals(descContent.get(0).get("name")));
			}
		}
	}

	@Test
	public void testNoParentForRootEnterprise() {
		String uri = getProperty(APM_SERVICE_URL) + ("/enterprises/{enterpriseSourceKey}/ancestors")
				.replace("{enterpriseSourceKey}", getProperty("apm.ROOT_ENTERPRISE_SOURCEKEY"));
		Response response = RestAssured.given().contentType("application/json").when()
				.header("Authorization", AUTH_TOKEN).get(uri).then().extract().response();
		JsonPath jsonPath = response.jsonPath();
		List<Object> parents = jsonPath.getList("parents");
		assertTrue(
				"NoParentForRootEnterprise failed for enterprise id : " + getProperty("apm.ROOT_ENTERPRISE_SOURCEKEY"),
				parents.isEmpty());
	}

	@Parameters({ "parent", "childSourceKey", "childClassification" })
	@Test(dataProvider = "listOfChildrenEnterprise", dataProviderClass = APMDataProvider.class)
	public void testChildrenTypeForEnterprise(String parent, String childSourceKey, String childClassification) {
		assertTrue("Classification mismatch for childSourceKey ",
				APMChildrenType.ENTERPRISE.contains(childClassification));
	}

	@Parameters({ "parent", "childSourceKey", "childClassification" })
	@Test(dataProvider = "listOfChildrenRegion", dataProviderClass = APMDataProvider.class)
	public void testChildrenTypeForRegion(String parent, String childSourceKey, String childClassification) {
		assertTrue("Classification mismatch for childSourceKey ", APMChildrenType.REGION.contains(childClassification));
	}

	@Parameters({ "parent", "childSourceKey", "childClassification" })
	@Test(dataProvider = "listOfChildrenSite", dataProviderClass = APMDataProvider.class)
	public void testChildrenTypeForSite(String parent, String childSourceKey, String childClassification) {
		assertTrue("Classification mismatch for childSourceKey ", APMChildrenType.SITE.contains(childClassification));
	}

	@Parameters({ "parent", "childSourceKey", "childClassification" })
	@Test(dataProvider = "listOfChildrenSegment", dataProviderClass = APMDataProvider.class)
	public void testChildrenTypeForSegment(String parent, String childSourceKey, String childClassification) {
		assertTrue("Classification mismatch for childSourceKey ",
				APMChildrenType.SEGMENT.contains(childClassification));
	}

	@Parameters({ "sourceKey", "parentSourceKey", "parentClassification" })
	@Test(dataProvider = "parentsOfEnterprise", dataProviderClass = APMDataProvider.class)
	public void testParentTypeForEnterprise(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for enterprise id : " + sourceKey,
				APMParentType.ENTERPRISE.contains(parentClassification));
	}

	@Parameters({ "sourceKey", "parentSourceKey", "parentClassification" })
	@Test(dataProvider = "parentsOfRegion", dataProviderClass = APMDataProvider.class)
	public void testParentTypeForRegion(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for region id : " + sourceKey,
				APMParentType.REGION.contains(parentClassification));
	}

	@Parameters({ "sourceKey", "parentSourceKey", "parentClassification" })
	@Test(dataProvider = "parentsOfSite", dataProviderClass = APMDataProvider.class)
	public void testParentTypeForSite(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for site id : " + sourceKey,
				APMParentType.SITE.contains(parentClassification));
	}

	@Parameters({ "sourceKey", "parentSourceKey", "parentClassification" })
	@Test(dataProvider = "parentsOfSegment", dataProviderClass = APMDataProvider.class)
	public void testParentTypeForSegment(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for segment id : " + sourceKey,
				APMParentType.SEGMENT.contains(parentClassification));
	}

	@Parameters({ "sourceKey", "parentSourceKey", "parentClassification" })
	@Test(dataProvider = "parentsOfAsset", dataProviderClass = APMDataProvider.class)
	public void testParentTypeForAsset(String sourceKey, String parentSourceKey, String parentClassification) {
		assertTrue("Parent classificationCode check failed for Asset id : " + sourceKey,
				APMParentType.ASSET.contains(parentClassification));
	}

	@Parameters({ "enterpriseKey", "sourceKey", "type" })
	@Test(dataProvider = "listTypes", dataProviderClass = APMDataProvider.class)
	public void testTypeCategoriesForType(String enterpriseKey, String sourceKey, String type) {
		assertTrue(type + " -- Type is not valid for -- " + sourceKey, ACCEPTABLE_TYPES.contains(type));
	}

	@Parameters({ "sourceKey", "type" })
	@Test(dataProvider = "listTags", dataProviderClass = APMDataProvider.class)
	public void testDataTypeForTag(String assestKey, String sourceKey, String type) {
		assertTrue(type + " -- Data Type is not valid for -- " + sourceKey, TAG_DATA_TYPES.contains(type));
	}

	@Parameters({ "assetSourceKey" })
	@Test(dataProvider = "listAssets", dataProviderClass = APMDataProvider.class)
	public void testNamingPatternForAsset(String assetSourceKey) {
		assertTrue("Asset source key pattern not valid for - " + assetSourceKey,
				assetSourceKey.startsWith(ASSET_KEY_PATTERN));
	}

}
