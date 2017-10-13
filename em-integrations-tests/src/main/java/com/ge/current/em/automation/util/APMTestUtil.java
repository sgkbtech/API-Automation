package com.ge.current.em.automation.util;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;

import com.ge.current.em.APMJsonUtility;
import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.provider.APMDataProvider;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class APMTestUtil extends EMTestUtil {

	private static final Log logger = LogFactory.getLog(APMDataProvider.class);
	public static final String APM_SERVICE_URL = "apm_service_url";
	public static final String ASSET_KEY_PATTERN = "ASSET";
	public static final String SEGMENT_KEY_PATTERN = "SEGMENT";
	protected static final String JSON_FILE_PATH_PATCH = "src/main/resources/test-suite/data/apmpatch/";
	
	public static final String REGION_ID = "REGION_75835a52-136c-3ecf-be6c-f5d57b94c49b";
	public static final String SEGMENT_ID = "SEGMENT_a03b2a94-13ac-3eed-a9b4-58b752b3280c";
	public static final String SITE_ID = "SITE_4d4214d3-79d0-3a2b-a37e-64fb12bb4e0e";
	public static final String TAG_ID = "TAG_205df6fc-e29f-3676-8326-fcaf13220f58";
	public static final String ENTERPRISE_ID = "ENTERPRISE_d8fdd4f9-ffc4-308d-83ec-e9e0b114e882";
	public static final String ASSET_ID = "ASSET_dcc5a833-90eb-3387-b02d-002d870b7967";
	public static final String TYPE_ID = "SEGMENT_TYPE_9d5b2363-ca37-3dff-a449-d6c1f18bab86";
	
	
	protected Map<String, String> JSON_PARAM_MAP = new HashMap<String, String>() {
		/**
		 * 
		 */
		protected static final long serialVersionUID = -511859336544093314L;
		{
			put("apm.asset.name", "Test_Asset_unixTime");
			put("apm.segment.name", "Test_Segment_unixTime");
			put("dummy", "dummy");
			put("apm.newEnterprise", "Test_EN_unixTime");
			put("apm.site.name", "Test_Site_unixTime");
			put("apm.segment.name", "Test_Segment_unixTime");
			put("apm.region.name", "Test_Region_unixTime");

		}
	};

	protected String AUTH_TOKEN = "";

	@BeforeTest
	public void addPropertiesToTestContext(ITestContext context) {
		setProperty("apm.Authorization", "Bearer " + super.getAPMServiceToken());
		AUTH_TOKEN = getProperty("apm.Authorization");
		RestAssured.baseURI=getProperty(APM_SERVICE_URL);
		for (Map.Entry<Object, Object> property : getProperties().entrySet()) {
			context.setAttribute((String) property.getKey(), property.getValue());
		}
	}

	protected String readPayloadFile(String fileName, Properties p) throws Exception {
		p.putAll(JSON_PARAM_MAP);
		long unixTime = System.currentTimeMillis() % 1000000000L;
		return (new APMJsonUtility()).readPayloadFile(fileName, getProperties()).replaceAll("unixTime", "" + unixTime)
				.replaceAll("dummy", "" + unixTime);
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

	protected JsonPath responseCheck(String uri, String content, String method, int status, String contentType) {
		Response response = null;
		if (method.equals("patch")) {
			response = RestAssured.given().contentType(contentType).body(content).header(APMConstants.AUTHORIZATION, AUTH_TOKEN)
					.when().patch(uri).then().extract().response();
		} else if (method.equals("put")) {
			response = RestAssured.given().contentType(contentType).body(content).header(APMConstants.AUTHORIZATION, AUTH_TOKEN)
					.when().put(uri).then().extract().response();
		}
		assertTrue(getFailureMessage(status, response), response.getStatusCode() == status);
		if (method.equals("put")) {
			JsonPath input = new JsonPath(content);
			List<PropertiesDTO> actual = response.jsonPath().getList("");
			List<PropertiesDTO> expected = input.getList(APMConstants.LIST);
			assertEquals(actual, expected);
		}
		return response.jsonPath();
	}

	protected void responseValidate(String URI, JsonPath in, String method, int status, String contentType) {
		Response response = RestAssured.given().contentType("application/json").when()
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).get(URI).then().extract().response();
		JsonPath out = response.jsonPath();
		List<PropertiesDTO> actual = out.getList(APMConstants.PROPERTIES);
		List<PropertiesDTO> expected = in.getList("");
		assertEquals(actual, expected);
	}

	protected Response getResponse(String uri) {
		Response response = RestAssured.given().contentType("application/json").when()
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).get(uri).then().extract().response();
		assertEquals(HttpStatus.OK.value(), response.statusCode());
		return response;
	}

	protected Response postRequest(String uri, String content) {
		Response response = RestAssured.given().contentType("application/json").body(content)
				.header(APMConstants.AUTHORIZATION, AUTH_TOKEN).expect().statusCode(HttpStatus.CREATED.value()).when().post()
				.then().extract().response();
		assertTrue(getFailureMessage(HttpStatus.CREATED.value(), response),
				response.getStatusCode() == HttpStatus.CREATED.value());
		return response;
	}

}
