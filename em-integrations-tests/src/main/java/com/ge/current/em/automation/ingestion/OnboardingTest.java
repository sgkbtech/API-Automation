package com.ge.current.em.automation.ingestion;

import com.ge.current.em.automation.util.EMTestUtil;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertTrue;

public class OnboardingTest extends EMTestUtil {
	
	private static final Log log = LogFactory.getLog(OnboardingTest.class);
	
	private static final String ADMINISTRATOR_BASE_URI = "/administrator/devices/";
	private static final String MAPPER_BASE_URI = "/mapper/devices/";
	private static final int DEVICE_UID = 1234;
	private static final String CREATED_DEVICE_UID = "QNX-1234";
	private static final String WRONG_DEVICE_UID = "XYZ-1234";
	private static final String REPORTING_TOPIC = "reporting.topic.test";
	private static final String ONBOARDING_TOPIC = "onboarding.topic.test";
	private static final String RESPONSE_TOPIC = "response.topic.test";
	private String token;
	private String baseURL;
	
	
	
	@BeforeTest
	public void setUp() {
		try {
			token = "Bearer " + getEdgeAdminToken();
			baseURL = getProperty("edge.admin.url");
			log.info("Edge Admin URL: " +baseURL+ "\n");
			log.info("Token: \n" +token+ "\n");
		} catch (Exception ex) {
			log.error("Exception in getToken() : " +ex.getMessage(), ex);
		}
	}
	
	
	/*
	 * ****************************************************
	 * 				NEGATIVE TEST CASES
	 * 
	 * ****************************************************
	 */
	
	@Test
	public void createEdgeDeviceUnauthorizedTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + DEVICE_UID;

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				when().
				post(uri).
				then().extract().response();
		
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updatePointStatusUnauthorizedTest() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + CREATED_DEVICE_UID + "/points";
		String updatePointJson = createPointJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				body(updatePointJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updatePointStatusWrongDeviceUIDTest() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + WRONG_DEVICE_UID + "/points";
		String updatePointJson = createPointJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(updatePointJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 404),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updatePointStatusWrongInputUIDTest() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + CREATED_DEVICE_UID + "/points";
		String updatePointJson = "";

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(updatePointJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 500),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void getAllEquipmentsForDeviceUnauthorizedTest() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + CREATED_DEVICE_UID + "/equipments";

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void getAllEquipmentsForDeviceWrongDeviceUIDUnauthorizedTest() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + WRONG_DEVICE_UID + "/equipments";

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 404),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void addPointUnauthorizedTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + "points/" + CREATED_DEVICE_UID;
		String additionalPointJson = createAdditionalPointJson();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				body(additionalPointJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void addPointWrongDeviceUIDTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + "points/" + WRONG_DEVICE_UID;
		String additionalPointJson = createAdditionalPointJson();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(additionalPointJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 500),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void addPointWrongInputTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + "points/" + WRONG_DEVICE_UID;
		String additionalPointJson = "";

		URI uri = getURI(url);
		log.info("URI: " +uri);
		
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(additionalPointJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 500),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updateTopicsUnauthorizedTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID + "/topics";
		String topicsJson = createTopicsJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				body(topicsJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updateTopicsWrongDeviceUIDTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + WRONG_DEVICE_UID + "/topics";
		String topicsJson = createTopicsJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(topicsJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 404),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updateTopicsWrongInputTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID + "/topics";
		String topicsJson = "";

		URI uri = getURI(url);
		log.info("URI: " +uri);
		
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(topicsJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 500),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void getEdgeDeviceUnauthorizedTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID;

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void getEdgeDeviceWrongDeviceUIDTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + WRONG_DEVICE_UID;

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 404),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updateEdgeDeviceUnauthorizedTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID;
		String updateEdgeDeviceJson = createEdgeDeviceJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				body(updateEdgeDeviceJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updateEdgeDeviceWrongDeviceUIDTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + WRONG_DEVICE_UID;
		String updateEdgeDeviceJson = createEdgeDeviceJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(updateEdgeDeviceJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 404),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void updateEdgeDeviceWrongInputTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + WRONG_DEVICE_UID;
		String updateEdgeDeviceJson = "";

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(updateEdgeDeviceJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 500),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void createAdditionalAssetKeyUnauthorizedTest() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + CREATED_DEVICE_UID + "/key";
		String pointJson = createPointJsonForUpdate();
		
		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				body(pointJson).
				when().
				post(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void deleteEdgeDeviceUnauthorizedTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID;
		String updateEdgeDeviceJson = createEdgeDeviceJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				body(updateEdgeDeviceJson).
				when().
				delete(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401), "Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void deleteEdgeDeviceWrongDeviceUIDTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + WRONG_DEVICE_UID;
		String updateEdgeDeviceJson = createEdgeDeviceJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(updateEdgeDeviceJson).
				when().
				delete(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 404), "Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test
	public void getAllEdgeDeviceUnauthorizedTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI;

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 401),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	
	/*
	 * ****************************************************
	 * 				POSITIVE TEST CASES
	 * 
	 * ****************************************************
	 */
	
	@Test
	public void createEdgeDeviceTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + DEVICE_UID;

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				when().
				post(uri).
				then().extract().response();
		
		assertTrue((response.statusCode() == 200),"Assertion Failed: Response Code returned is: "+response.statusCode());

		String jsonString = response.getBody().asString();
		log.info("Response Body: " + jsonString);
		ReadContext ctx = JsonPath.parse(jsonString);
		String edgeDeviceId = (ctx.read("$.edgeSiteId").toString());
		assertTrue((edgeDeviceId.equals(CREATED_DEVICE_UID)), "Assertion Failed: edgeDeviceId is not the same as expected: " +edgeDeviceId);
	}
	
	@Test(dependsOnMethods = { "createEdgeDeviceTest" })
	public void updatePointStatusTest() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + CREATED_DEVICE_UID + "/points";
		String updatePointJson = createPointJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(updatePointJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 204),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test(dependsOnMethods = { "updatePointStatusTest" })
	public void getAllEquipmentsForDeviceTest1() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + CREATED_DEVICE_UID + "/equipments";

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 200),"Assertion Failed: Response Code returned is: "+response.statusCode());
	
		String jsonString = response.getBody().asString();
		log.info("Response Body: " + jsonString);
		ReadContext ctx = JsonPath.parse(jsonString);
		String pointStatus = (ctx.read("$[0].points[0].active").toString());
		int pointsListSize = (ctx.read("$[0].points.length()"));
		assertTrue((pointStatus.equals("true")), "Assertion Failed: pointStatus is not the same as expected: " +pointStatus);
		assertTrue((pointsListSize == 1), "Assertion Failed: pointsListSize is not the same as expected. Expected 2 received: " +pointsListSize);
	}
	
	@Test(dependsOnMethods = { "getAllEquipmentsForDeviceTest1" })
	public void addPointTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + "points/" + CREATED_DEVICE_UID;
		String additionalPointJson = createAdditionalPointJson();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(additionalPointJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 204),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test(dependsOnMethods = { "addPointTest" })
	public void updateTopicsTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID + "/topics";
		String topicsJson = createTopicsJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(topicsJson).
				when().
				put(uri).
				then().extract().response();
		String jsonString = response.getBody().asString();
		log.info("Response Body: " + jsonString);
		assertTrue((response.statusCode() == 200),"Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test(dependsOnMethods = { "updateTopicsTest" })
	public void getEdgeDeviceTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID;

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 200),"Assertion Failed: Response Code returned is: "+response.statusCode());
	
		String jsonString = response.getBody().asString();
		log.info("Response Body: " + jsonString);
		ReadContext ctx = JsonPath.parse(jsonString);
		String ipAddress = (ctx.read("$.profile.ipAddress").toString());
		assertTrue((ipAddress.equals("1.2.3.4")), "Assertion Failed: ipAddress is not the same as expected: " +ipAddress);
		
		String onboardingTopic = (ctx.read("$.topics.onboardingTopic").toString());
		assertTrue((onboardingTopic.equals(ONBOARDING_TOPIC)), "Assertion Failed: onboardingTopic is not the same as expected: " +onboardingTopic);
	}
	
	@Test(dependsOnMethods = { "getEdgeDeviceTest" })
	public void updateEdgeDeviceTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID;
		String updateEdgeDeviceJson = createEdgeDeviceJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(updateEdgeDeviceJson).
				when().
				put(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 200),"Assertion Failed: Response Code returned is: "+response.statusCode());
	
		String jsonString = response.getBody().asString();
		log.info("Response Body: " + jsonString);
		ReadContext ctx = JsonPath.parse(jsonString);
		String predixPort = (ctx.read("$.profile.predixPort").toString());
		assertTrue((predixPort.equals("8883")), "Assertion Failed: predixPort is not the same as expected: " +predixPort);
	}
	
	@Test(dependsOnMethods = { "updateEdgeDeviceTest" })
	public void getAllEquipmentsForDeviceTest2() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + CREATED_DEVICE_UID + "/equipments";

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 200),"Assertion Failed: Response Code returned is: "+response.statusCode());
	
		String jsonString = response.getBody().asString();
		log.info("Response Body: " + jsonString);
		ReadContext ctx = JsonPath.parse(jsonString);
		int pointsListSize = (ctx.read("$[0].points.length()"));
		assertTrue((pointsListSize == 2), "Assertion Failed: pointsListSize is not the same as expected. Expected 2 received: " +pointsListSize);
	}
	
	@Test(dependsOnMethods = { "getAllEquipmentsForDeviceTest2" })
	public void createAdditionalAssetKeyTest() throws Exception {
		String url = baseURL + MAPPER_BASE_URI + CREATED_DEVICE_UID + "/key";
		String pointJson = createPointJsonForUpdate();
		
		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(pointJson).
				when().
				post(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 201),"Assertion Failed: Response Code returned is: "+response.statusCode());
	
		String jsonString = response.getBody().asString();
		log.info("Response Body: " + jsonString);
		assertTrue((!jsonString.equals("")), "Assertion Failed: Response should have not be: "+jsonString);
	}
	
	@Test(dependsOnMethods = { "createAdditionalAssetKeyTest" })
	public void deleteEdgeDeviceTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI + CREATED_DEVICE_UID;
		String updateEdgeDeviceJson = createEdgeDeviceJsonForUpdate();

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				body(updateEdgeDeviceJson).
				when().
				delete(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 200), "Assertion Failed: Response Code returned is: "+response.statusCode());
	}
	
	@Test(dependsOnMethods = { "deleteEdgeDeviceTest" })
	public void getAllEdgeDeviceTest() throws Exception {
		String url = baseURL + ADMINISTRATOR_BASE_URI;

		URI uri = getURI(url);
		log.info("URI: " +uri);
		Response response =
				RestAssured.given().
				header("Authorization", token).
				header("Content-Type", "application/json").
				when().
				get(uri).
				then().extract().response();
		assertTrue((response.statusCode() == 200),"Assertion Failed: Response Code returned is: "+response.statusCode());
	
		String jsonString = response.getBody().asString();
		log.info("Response Body: " + jsonString);
		Object dataObject = JsonPath.parse(jsonString).read("$[?(@.edgeSiteId == '" +CREATED_DEVICE_UID+ "')]");
		String dataString = dataObject.toString();
		assertTrue((dataString.equals("[]")), "Assertion Failed: Found deviceUid in system which should have been deleted: "+dataString);
	}
	
	
	/*
	 * ****************************************************
	 * 				PRIVATE METHODS
	 * 
	 * ****************************************************
	 */
	private URI getURI(String url) throws Exception {
		URI uri = null;
		try {
			uri = new URI(url);
		} catch(Exception ex) {
			log.error("Exception in createEdgeDevice : " +ex.getMessage(), ex);
			throw ex;
		}
		return uri;
	}
	
	private String createEdgeDeviceJsonForUpdate() {
		JSONObject jo = new JSONObject();
		jo.put("edgeDeviceType", "JACE");
		jo.put("ipAddress", "0.0.0.0");
		jo.put("moduleVersion", 2.0);
		jo.put("predixHome", "ssl://iot-gateway-dev.ice.predix.io");
		jo.put("predixPort", "8883");
		jo.put("profileRevision", 2);
		jo.put("reportingInterval", 300);
		
		return jo.toString();
	}
	
	private String createPointJsonForUpdate() {
		JSONArray ja = new JSONArray();
		
		JSONObject jo = new JSONObject();
		jo.put("active", true);
		jo.put("derivedEquipName", "AHU");
		jo.put("pointName", "TempSensor");
		
		ja.put(jo);
		return ja.toString();
	}
	
	private String createAdditionalPointJson() {
		
		JSONArray ja = new JSONArray();
		
		JSONObject pointObj1 = new JSONObject();
		pointObj1.put("active", true);
		pointObj1.put("derivedEquipName", "AHU");
		pointObj1.put("pointName", "TempSensor-SMR");
		
		ja.put(pointObj1);
		
		JSONObject pointObj2 = new JSONObject();
		pointObj2.put("active", true);
		pointObj2.put("derivedEquipName", "AHU");
		pointObj2.put("pointName", "TempSensor-AK");
		
		ja.put(pointObj2);
		
		JSONObject jo = new JSONObject();
		jo.put("edgeSiteId", "QNX-1234");
		jo.put("profileRevision", 2);
		jo.put("points", ja);
		
		return jo.toString();
		
	}
	
	private String createTopicsJsonForUpdate() {
		JSONObject jo = new JSONObject();
		
		jo.put("onboarding", "");
		jo.put("assignSchedule", "");
		jo.put("updateSchedule", "");
		jo.put("deleteSchedule", "");
		jo.put("dayOverride", "");
		jo.put("hourOverride", "");
		jo.put("requests", "");
		jo.put("subscribeOther", "");
		jo.put("scheduleEvents", "");
		jo.put("reportingTopic", REPORTING_TOPIC);
		jo.put("onboardingTopic", ONBOARDING_TOPIC);
		jo.put("responseTopic", RESPONSE_TOPIC);
		
		return jo.toString();
	}
	

}
