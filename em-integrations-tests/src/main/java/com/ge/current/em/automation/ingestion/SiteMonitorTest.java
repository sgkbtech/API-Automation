package com.ge.current.em.automation.ingestion;

import com.ge.current.em.automation.util.EMTestUtil;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Created by 212582713 on 01/05/2017.
 */


public class SiteMonitorTest extends EMTestUtil{
	
	private static final String  SEARCH_BY = "/filtersearch?query=resrc_uid:";
	
	URI uri;
	String filterQuery, fromTime, toTime; 
	
	private void setUp(String assetId) {
		uri = URI.create(getProperty("solr_service_url")
				+ getProperty("DBSchema")
				+ getProperty("Normalized")
				+ SEARCH_BY 
				+ assetId);
		
		fromTime = Instant.now().minus(900000L).toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		toTime = Instant.now().toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		filterQuery = "event_ts:["+fromTime+"%20TO%20"+toTime+"]";
	}
	
	@Test
	public void testNormalizationForLightingCircuit7IsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekeyLightingCircuit7"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals(getProperty("em.Enterprise.enterpriseSourceKey"))," enterprise Id did not match");
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}
	
	@Test
	public void testNormalizationForLightingCircuit4_10_16IsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekeyLightingCircuit4_10_16"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals(getProperty("em.Enterprise.enterpriseSourceKey"))," enterprise Id did not match");
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}
	
	@Test
	public void testNormalizationForLightingCircuit5IsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekeyLightingCircuit5"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals(getProperty("em.Enterprise.enterpriseSourceKey"))," enterprise Id did not match");
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}
	
	@Test
	public void testNormalizationForLigtingCircuit8IsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekeyLigtingCircuit8"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals(getProperty("em.Enterprise.enterpriseSourceKey"))," enterprise Id did not match");
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}
	
	@Test
	public void testNormalizationForLightingCircuit6IsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekeyLightingCircuit6"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals(getProperty("em.Enterprise.enterpriseSourceKey"))," enterprise Id did not match");
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}
	
	@Test
	public void testNormalizationForEastWallHeaterIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekeyEastWallHeater"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals(getProperty("em.Enterprise.enterpriseSourceKey"))," enterprise Id did not match");
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}
	
	@Test
	public void testNormalizationForNorthWallHeaterIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekeyNorthWallHeater"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals(getProperty("em.Enterprise.enterpriseSourceKey"))," enterprise Id did not match");
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}
	
	@Test
	public void testNormalizationForMainPowerMeterIsPresent() throws URISyntaxException {
		setUp(getProperty("em.Asset.assetSourcekeyMainPowerMeter"));
		Response  response =	given().log().all().
								header("Authorization","Bearer "+ getSolrServiceToken()).
								param("fq", filterQuery).
								when().
								get(uri);
							
		JsonPath jsonPath = response.jsonPath();
		
		List<Object> resultListEnterprise = jsonPath.getList("enterprise_uid");
		List<Object> resultListMmeasureszoneElecMeterPowerSensor = jsonPath.getList("measureszoneElecMeterPowerSensor");
		List<Object> resultListMzoneElecMeterEnergySensor = jsonPath.getList("measureszoneElecMeterEnergySensor");
		
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(resultListEnterprise.size()>=2, "Data points expected within 15 min interval in event_normlog >= 2 but recieved : "+resultListEnterprise.size());
		Assert.assertTrue(resultListEnterprise.get(0).equals(getProperty("em.Enterprise.enterpriseSourceKey"))," enterprise Id did not match");
	
		Assert.assertEquals(response.getStatusCode(),HttpStatus.SC_OK, "status code was not Httpstatus.SC_OK");
		Assert.assertTrue(Double.parseDouble(resultListMmeasureszoneElecMeterPowerSensor.get(0).toString()) > 0,"measureskWh > 0 ");
		Assert.assertTrue(Double.parseDouble(resultListMzoneElecMeterEnergySensor.get(0).toString())> 0, "measureszoneElecMeterEnergySensor > 0");
	}
	
}
