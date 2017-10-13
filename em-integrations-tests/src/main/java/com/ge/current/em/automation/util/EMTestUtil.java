/*
 * Copyright (c) 2016 GE. All Rights Reserved. GE Confidential: Restricted
 * Internal Distribution
 */

package com.ge.current.em.automation.util;


import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.current.em.APMJsonUtility;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

/**
 * Created by 212582713 on 12/16/16.
 */

public class EMTestUtil {

	private static final Log logger = LogFactory.getLog(EMTestUtil.class);
	private static String proxyHost;
	private static String proxyPort;
	public static Properties globalProperties = null;
	public ObjectMapper objectMapper = new ObjectMapper();
	private int duration;

	public static String solrServiceToken;
	public static String sparkProxyToken;
	public static String bluToken;
	public static String apmToken;
	public static String uiserviceToken;
	
	private static final String SPARK_CONFIG_PATH = "src/main/resources/test-suite/data/spark/";

	@Parameters({ "envType","enableProxy" })
	@BeforeSuite
	public static void initFramework(
            //@Optional("vpc_stage") String configfile,
            @Optional("vpc_dev") String configfile,
            @Optional("true") Boolean enableProxy) throws Exception {
		configfile = configfile + ".configuration.properties";
		logger.info("envType " + configfile);
		init(configfile);
		if(enableProxy) {
		//	configureProxySettings(configfile);
		}
	}

	@Parameters({ "ingestionDuration" })
	@BeforeSuite
	public void setDuration(@Optional("15") String duration) {
		this.duration = Integer.parseInt(duration);
	}

	private static void configureProxySettings(String configfile) {
		System.out.println("here");
		System.getProperties().put("proxySet", "true");
		System.getProperties().put("https.proxyHost", globalProperties.getProperty("em.proxy.host"));
		System.getProperties().put("https.proxyPort", globalProperties.getProperty("em.proxy.port"));
	}

	public static void init(String configFile) {
		try {
			FileInputStream fis = new FileInputStream(configFile);
			globalProperties = new Properties();
			globalProperties.load(fis);
		} catch (Exception e) {
			logger.info("Exception " + e.getMessage());
		}
	}

	public static RequestSpecification restAssured() {
		if (StringUtils.isEmpty(proxyHost) || StringUtils.isEmpty(proxyPort)) {
			proxyHost = System.getProperty("httpProxy");
			proxyPort = System.getProperty("portNumber");
		}

		return given().proxy(proxyHost, Integer.parseInt(proxyPort));
	}

	public String getSolrServiceToken() {
	    solrServiceToken = given().header("Authorization", "Basic " + globalProperties.getProperty("authorization"))
                .param("grant_type", "client_credentials").expect().statusCode(200).when()
                .get(globalProperties.getProperty("uaaUrl")).jsonPath().getString("access_token");
        return solrServiceToken;
	}

	public static String getBLUToken() {
		bluToken =given().log().all().auth()
				.basic(globalProperties.getProperty("blu.user"), globalProperties.getProperty("blu.password"))
				.param("grant_type", "client_credentials").expect().statusCode(200).when()
				.get(globalProperties.getProperty("apm.uaaUrl")).jsonPath().getString("access_token");
		return bluToken;
	}

	public String getBRToken() {
		String token = given().log().all().auth()
				.basic(getProperty("br.uaa.clientId"), getProperty("br.uaa.clientSecret"))
				.param("grant_type", "client_credentials").expect().statusCode(200).when()
				.get(getProperty("br.uaa.url")).jsonPath().getString("access_token");

		return token;
	}

	public static String getSparkProxyToken() {
		sparkProxyToken = given().log().all().auth()
				.basic(globalProperties.getProperty("sparkproxy.user"), globalProperties.getProperty("sparkproxy.password"))
				.param("grant_type", "client_credentials").expect().statusCode(200)
				.when().get(globalProperties.getProperty("apm.uaaUrl")).jsonPath().getString("access_token");
		return sparkProxyToken;
	}

	public static String getAPMServiceToken() {
		apmToken = given().auth()
				.basic(globalProperties.getProperty("apm.userName"), globalProperties.getProperty("apm.password"))
				.param("grant_type", "client_credentials").expect().statusCode(200).when()
				.get(globalProperties.getProperty("apm.uaaUrl")).jsonPath().getString("access_token");
		return apmToken;
	}

	public static String getUIServiceToken() throws IOException {
		uiserviceToken = given().
					   //request().log().all().
					   formParam("username", globalProperties.getProperty("em.ui.userName")).
					   formParam("password", globalProperties.getProperty("em.ui.password")).
					   header("Authorization", "Basic " + globalProperties.getProperty("em.ui.authorization")).
					   param("grant_type", "password").expect().
					   //response().log().all().
					   statusCode(200).
					   when().
					   get(globalProperties.getProperty("em.ui.uaaUrl")).
					   jsonPath().
					   getString("access_token");
		return uiserviceToken;
	}

	public static String getServiceConnectorToken() throws IOException {

		String token = given().log().all().auth()
				.basic(globalProperties.getProperty("ie.connectors.uaa.client.id"), globalProperties.getProperty("ie.connectors.uaa.client.secret"))
				.param("grant_type", "client_credentials").expect().statusCode(200)
				.when().get(globalProperties.getProperty("ie.connectors.uaaUrl")).jsonPath().get("access_token");
		return token;
	}

	public static String getTokenWithGrantTypeClientCredentials(String uaaUrl, String username, String password) {
		return given().log().all().auth()
				.basic(username, password).param("grant_type", "client_credentials")
				.expect().statusCode(200)
				.when().get(uaaUrl).jsonPath().get("access_token");
	}

	public String getBillingServiceToken() throws IOException {
		String token = given().log().all().auth()
				.basic(globalProperties.getProperty("br.uaa.clientId"), globalProperties.getProperty("br.uaa.clientSecret"))
				.param("grant_type", "client_credentials").expect().statusCode(200).when()
				.get(globalProperties.getProperty("br.uaa.url")).jsonPath().getString("access_token");

		return token;
	}
	
	 public void insertStatementsToCassandra(String cassandraConnectorWebUrl,
             String insertStatementsFileName) throws FileNotFoundException, IOException {
      String cassandraConnectorToken = getcassandraConnectorToken();
      String dbRunStatementUrl = cassandraConnectorWebUrl + "/db/runStatement";
      int count = 1;
      try (BufferedReader br = new BufferedReader(new FileReader(insertStatementsFileName))) {
             String insertStatement;
             while ((insertStatement = br.readLine()) != null) {
                 ResponseBody response = RestAssured.given().
                         accept(ContentType.JSON).
                         contentType(ContentType.JSON).
                         header("Authorization", "Bearer" + cassandraConnectorToken).
                         body(insertStatement).
                         when().
                         post(dbRunStatementUrl).
                         thenReturn().
                         body();
                 System.out.println(count + ":inserted : " + response.asString());
                 count++;
             }
         }
     }
	 
	 public String getcassandraConnectorToken() {
	        String token = given().log().all().auth()
	                .basic(globalProperties.getProperty("ie.cassandra.connector.userName"), globalProperties.getProperty("ie.cassandra.connector.password"))
	                .param("grant_type", "client_credentials").expect().statusCode(200).when()
	                .get(globalProperties.getProperty("ie.cassandra.connector.uaaUrl")).jsonPath().getString("access_token");
	        return token;
	    }

	/**
	 * @param batchProperties to be used as parameters in spark and cassandra details
	 * @param sparkConfigFileName configuration filename; file should contain configurations required to run the spark job.
	 *                            The file should be inside {@value #SPARK_CONFIG_PATH}
	 * @return
	 * @throws Exception
	 */
	public Response getCassandraDataAfterSparkRun(Properties batchProperties, String sparkConfigFileName) throws Exception{
		String  submissionId = submitSparkJob(batchProperties, sparkConfigFileName);
		if(submissionId == null){
			return null;
		}
		if( !isDriverStateSucess(submissionId)){
			return null;
		}
		//to do implementation  generic for data to be cleaned up later
		String uri = getProperty("solr_service_url")
				+ getProperty("DBSchema")
				+ getProperty(batchProperties.getProperty("aggregationType"))
				+ "/filtersearch?query=" +batchProperties.getProperty("query");
		Response  response	= given().log().all().
				header("Authorization","Bearer "+ getSolrServiceToken()).
				param("fq", batchProperties.getProperty("filterQuery")).
				when().
				get(uri);

		response.prettyPrint();
		return response;
	}

	/**
	 *
	 * @param sparkBatchProperties to be used as parameters in spark
	 * @param sparkConfigFileName configuration filename; file should contain configurations required to run the spark job.
	 *                            The file should be inside {@value #SPARK_CONFIG_PATH}
	 *
	 * @return submissionId if submission is successful
	 * @throws Exception
	 */
	public String submitSparkJob(Properties sparkBatchProperties, String sparkConfigFileName)
			throws Exception {
		APMJsonUtility jsonUtility = new APMJsonUtility();
		String submissionId = "";
		if(!sparkConfigFileName.matches(".+\\.json")) {
			throw new RuntimeException("spark configuration must be in .json file format.");
		}
		String path = SPARK_CONFIG_PATH + sparkConfigFileName;
		String token = getTokenWithGrantTypeClientCredentials(globalProperties.getProperty("platform.uaaUrl"),
				globalProperties.getProperty("sparkproxy.user"),
				globalProperties.getProperty("sparkproxy.password"));
		String payload = jsonUtility.readPayloadFile(path, sparkBatchProperties);
		logger.info("Sending payload to spark-proxy service: " + payload);
		Response response = given().log().all()
				.header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON)
				.body(payload)
				.expect().statusCode(200)
				.when()
				.post(globalProperties.getProperty("sparkproxy.url")+"submit");


		JsonNode responseInJson =  new ObjectMapper().readValue(response.print(), JsonNode.class);
		if(response == null) {
			logger.error("Response from spark-proxy service is null.");
			fail();
		} else {
			logger.info("Response from spark-proxy service: " + responseInJson.toString());
			submissionId = responseInJson.get("submissionId").asText().replace("\"", "");

		}
		return submissionId;
	}

	
	/**
	 * @param submissionId is the submitted SparkJob serviceID
	 * @return status(Success/Failure) of the job after polling for completion
	 * @throws InterruptedException
	 */
	public boolean isDriverStateSucess(String submissionId) throws InterruptedException {
		String driverState = "RUNNING";
		JsonPath jsonPath = null;

		String token = getTokenWithGrantTypeClientCredentials(globalProperties.getProperty("platform.uaaUrl"),
				globalProperties.getProperty("sparkproxy.user"),
				globalProperties.getProperty("sparkproxy.password"));
		String getSparkJobStatusURL = globalProperties.getProperty("sparkproxy.url")+ "status/" + submissionId;
		
		while(driverState.equals("RUNNING") || driverState.equals("SUBMITTED")){
			Response  response	=	given()
			.header("Authorization", "Bearer " + token)
			.when()
			.get(getSparkJobStatusURL);

			jsonPath = response.jsonPath();
			driverState = jsonPath.getString("driverState");
			logger.info("GETTING SPARK JOB STATUS: " + driverState);
			Thread.sleep(1000*5);

		}

		if(!driverState.equalsIgnoreCase("FINISHED") && !driverState.equalsIgnoreCase("RUNNING")){
			return false;
		}
		else if(driverState.equalsIgnoreCase("FINISHED")) {
			return true;
		}
		return false;
		
	}

	/**
	 *
	 * @param submissionId id of the spark job to be checked.
	 * @return jsonNode type that contains the status field (e.g. RUNNING, FINISHED, etc.)
	 */
	public JsonNode getSparkJobStatus(String submissionId) throws Exception {
		JsonPath jsonPath = null;
		String token = getTokenWithGrantTypeClientCredentials(globalProperties.getProperty("platform.uaaUrl"),
				globalProperties.getProperty("sparkproxy.user"),
				globalProperties.getProperty("sparkproxy.password"));
		String sparkUrlGetStatus = globalProperties.getProperty("sparkproxy.url")+"status/" + submissionId;
		logger.info("SPARK JOB STATUS URL: " + sparkUrlGetStatus);
		Response response = given()
				.header("Authorization ", "Bearer " + token)
				.when()
				.get(sparkUrlGetStatus);
		jsonPath = response.jsonPath();
		String driverState = jsonPath.getString("driverState");
		logger.info("RAW RESPONSE: " + response.prettyPrint());
		JsonNode responseInJson =  new ObjectMapper().readValue(response.print(), JsonNode.class);
		logger.info("Response from get status spark job: " + responseInJson.toString());
		logger.info("Driver state: " + driverState);
		return responseInJson;
	}

	// we should remove these methods since globalProperties is public, OR we make globalProperties as private and have proper getters and setters
	protected String getProperty(String name) {
		return getProperty(name, null);
	}

	protected String getProperty(String name, String defaultValue) {
		return globalProperties.getProperty(name, defaultValue);
	}

	protected void setProperty(String key, String value) {
		globalProperties.put(key, value);
	}

	public int getDuration() {
		System.out.print("\n  get ingestion--->>>" + duration);
		return duration;
	}

	public URI appendURI(String url, Map<String, String> params) {
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e1) {
			logger.error(e1.getMessage());
		}

		return appendURI(uri, params);
	}

	public URI appendURI(URI uri, Map<String, String> params) {

		Object[] keys = params.entrySet().toArray();
		for (Object appendQuery : keys) {
			String query = uri.getQuery();
			//System.out.println("query : " + query);
			if (query == null) {
				query = appendQuery.toString();
			} else {
				query += "&" + appendQuery;
			}
			try {
				uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		return uri;
	}

	public String getEdgeAdminToken() {
		String token = null;
		try {
			String username = globalProperties.getProperty("edge.admin.username");
			String password = globalProperties.getProperty("edge.admin.password");
			String uaaUrl = globalProperties.getProperty("edge.admin.uaa.url");
			
			token = given().log().all().auth()
					.basic(username, password)
					.param("grant_type", "client_credentials").expect().statusCode(200).when()
					.get(uaaUrl).jsonPath().getString("access_token");
		} catch(Exception ex) {
			System.out.println("Exception in getEdgeAdminToken() -----------> " +ex);
		}
		return token;
	}

	public Response getResponse(String url, Map<String, String> params, String token) throws IOException {
		URI uri = appendURI(url, params);
		logger.info("uri :: " + uri);
		Response response =
				given().
							contentType("application/json").
							when().
							header("predix-zone-id", globalProperties.get("em.ui.predix-zone-id")).
							header("Authorization", "Bearer " + token).
							get(uri).
							then().
							extract().
							response();
		assertTrue((response.statusCode() == HttpStatus.OK.value()),
				"Assertion Failed:Response Code expected is 200 - Code returned is:"
						+ response.statusCode()+ "\n uri : " + uri + "\n");
		return response;
	}

	public Response solrResponse(String tableName, String query) {

		String url = getProperty("solr_service_url") + getProperty("DBSchema") + getProperty(tableName) + "/filtersearch";

		Response response =
				given().
				contentType("application/json").
				when().
				header("Authorization", "Bearer " + solrServiceToken).
				get(url).
				then().
				extract().
				response();

		logger.info("url :: "+ url);
		logger.info("query :: "+ query);
		assertTrue((response.statusCode() == HttpStatus.OK.value()),"Assertion Failed:Response Code expected is 200 - Code returned is:" + response.statusCode() + "\n uri : " + url + "\n");
		return response;
	}

	public static Properties getProperties() {
		return globalProperties;
	}

	protected <T> T getObjectFromJsonFile(String fileName, Class<T> cls) {
		try {
			return objectMapper.readValue(new FileReader("src/main/resources/" + fileName), cls);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return null;
	}

	protected <T> T getObjectFromByteArr(byte[] byteArr, Class<T> cls) {
		try {
			return objectMapper.readValue(byteArr, cls);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return null;
	}

	protected String convertToJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
