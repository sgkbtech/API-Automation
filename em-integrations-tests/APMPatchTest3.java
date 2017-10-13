package com.ge.current.em.automation.ui;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ge.current.em.automation.provider.APMDataProvider;
import com.ge.current.em.automation.util.APMTestUtil;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class APMPatchTest3 extends APMTestUtil {
	// Test environment
	private static final Log logger = LogFactory.getLog(APMPatchTest3.class);
	private static final String JSON_FILE_PATH = "src/main/resources/test-suite/data/apmpatch/";
	private static final String APM_SERVICE_URL = "apm_service_url";
	private static final String ENTERPRISE_ID = "ENTERPRISE_4b7f97fd-318f-32f8-998e-ed2252112793";
	private static final String INVALID_ENTERPRISE_ID = "ENTERPRISE_4b7f97fd-318f-32f8-998e-ed2252112793##";
	private static final String UPDATE_SEGMENT_URI = "/SEGMENT_9729cb65-774c-3519-8d7d-5571d6eb2b5f/properties";
	private static final String SEGMENT_D = "SEGMENT_9729cb65-774c-3519-8d7d-5571d6eb2b5f";
	private static final String SITE_ID = "SITE_6c2cb2e2-1c68-351e-9134-c859ee76430f";
	private static final String TAG_ID = "TAG_d6fd81e0-fe4c-3df0-8d20-cd7c87274f3e";
	private static final String ASSET_TYPE = "ASSET_TYPE_a39dd9fc-08df-3565-b884-a79ac377ab8d";
	private static final String ASSET = "ASSET_eb8e07d9-26b3-3a9d-8eba-73e2e1b56d83";
	private static final String INVALID_ASSET = "ASSET_eb8e07d9-26b3-3a9d-8eba-73e2e1b56d83###";
	private Properties PARAM_MAP = new Properties() {/**
		 * 
		 */
		private static final long serialVersionUID = -8352993415615845796L;

	{
		put("apm.zoneElecMeterEnergySensor.tagtype","TAG_TYPE_be9aa832-4d63-3e6d-8579-88dd84f1baec");
		put("apm.zoneElecMeterPowerSensor.tagtype","TAG_TYPE_5e0cce4d-0c09-3c52-a507-c7de9b8ca0e7");
		put("apm.update.segmentKey","SEGMENT_9729cb65-774c-3519-8d7d-5571d6eb2b5f");
		put("apm.update.assetKey","ASSET_eb8e07d9-26b3-3a9d-8eba-73e2e1b56d83");
		put("apm.update.siteKey","SITE_6c2cb2e2-1c68-351e-9134-c859ee76430f");
	}};

	@Test
	public void patchEnterprisewithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises/" + ENTERPRISE_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "EnterprisePatch.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.OK.value(), "application/json");
	}

	@Parameters({ "Filename" })
	@Test(dataProvider = "EnterprisePropertiesNegativejson", dataProviderClass = APMDataProvider.class)
	public void putEnterprisePropertiesNegativeCase(String filename) throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises/" + ENTERPRISE_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + filename,PARAM_MAP);
		responseCheck(uri, content, "put", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchEnterprisePropertiesinvalidContentType() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises/" + ENTERPRISE_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "EnterprisePatch.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "text/plain");
	}

	@Test
	public void patchEnterprisePropertiesinvalidID() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises/" + INVALID_ENTERPRISE_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "EnterprisePatch.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.NOT_FOUND.value(), "application/json");
	}

	@Test
	public void patchEnterprisePropertieswithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/enterprises/" + ENTERPRISE_ID + "/properties";
		responseCheck(uri, "", "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchSegmentPropertieswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments" + UPDATE_SEGMENT_URI;
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSegmentProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.OK.value(), "application/json");
	}

	@Parameters({ "Filename" })
	@Test(dataProvider = "PatchSegmentPropJson", dataProviderClass = APMDataProvider.class)
	public void patchSegmentPropertiesNegativeCase(String filename) throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments" + UPDATE_SEGMENT_URI;
		String content = readPayloadFile(JSON_FILE_PATH + filename,PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchSegmentPropertiesinvalidContentType() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments" + UPDATE_SEGMENT_URI;
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSegmentProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "text/plain");
	}

	@Test
	public void patchSegmentPropertiesinvalidID() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments" + "/SEGMENT_b9a206d3-465b-374c-9658/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSegmentProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.NOT_FOUND.value(), "application/json");
	}

	@Test
	public void patchSegmentPropertieswithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments" + UPDATE_SEGMENT_URI;
		responseCheck(uri, "", "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchSegmentChildrenwithvalidinputs() throws Throwable {

		String uri = getProperty(APM_SERVICE_URL) + "/segments/" + SEGMENT_D + "/children";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSegmentChildren.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.OK.value(), "application/json");
	}

	@Parameters({ "Filename" })
	@Test(dataProvider = "PatchSegmentChildrenJson", dataProviderClass = APMDataProvider.class)
	public void patchSegmentChildrenNegativeCase(String filename) throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments/" + SEGMENT_D + "/children";
		String content = readPayloadFile(JSON_FILE_PATH + filename,PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchSegmentChildreninvalidContentType() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments/" + SEGMENT_D + "/children";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSegmentChildren.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "text/plain");
	}

	@Test
	public void patchSegmentChildreninvalidID() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments" + "/SEGMENT_b9a206d3-465b-374c-9658/children";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSegmentChildren.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.NOT_FOUND.value(), "application/json");
	}

	@Test
	public void patchSegmentChildrenwithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/segments/" + SEGMENT_D + "/children";
		responseCheck(uri, "", "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchSitepropertieswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites/" + SITE_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSiteProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.OK.value(), "application/json");
	}

	@Parameters({ "Filename" })
	@Test(dataProvider = "PatchSitepropertiesJson", dataProviderClass = APMDataProvider.class)
	public void patchSitepropertiesNegativeCase(String filename) throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites/" + SITE_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + filename,PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchSitepropertiesinvalidContentType() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites/" + SITE_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSiteProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "text/plain");
	}

	@Test
	public void patchSitepropertiesinvalidID() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites" + "/SITE_6789adff-a080-3538-bc12-1a2f793/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchSiteProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.NOT_FOUND.value(), "application/json");
	}

	@Test
	public void patchSitepropertieswithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/sites/" + SITE_ID + "/properties";
		responseCheck(uri, "", "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchTagpropertieswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/tags/" + TAG_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchTagProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.OK.value(), "application/json");

	}

	@Parameters({ "Filename" })
	@Test(dataProvider = "PatchTagpropertiesJson", dataProviderClass = APMDataProvider.class)
	public void patchTagpropertiesNegativeCase(String filename) throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/tags/" + TAG_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + filename,PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchTagpropertiesinvalidContentType() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/tags/" + TAG_ID + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchTagProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "text/plain");
	}

	@Test
	public void patchTagpropertiesinvalidID() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/tags" + "/TAG_8149e9c2-6370-36a3-b73b-a3/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchTagProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.NOT_FOUND.value(), "application/json");
	}

	@Test
	public void patchTagpropertieswithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/tags/" + TAG_ID + "/properties";
		responseCheck(uri, "", "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchTypepropertieswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/types/" + ASSET_TYPE + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchTypeProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.OK.value(), "application/json");
	}

	@Parameters({ "Filename" })
	@Test(dataProvider = "PatchTypepropertiesJson", dataProviderClass = APMDataProvider.class)
	public void patchTypepropertiesNegativeCase(String filename) throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/types/" + ASSET_TYPE + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + filename,PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchTypepropertiesinvalidContentType() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/types/" + ASSET_TYPE + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchTypeProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "text/plain");
	}

	@Test
	public void patchTypepropertiesinvalidID() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/types" + "/ASSET_TYPE_b9a206d3-465b-374c-9658/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchTypeProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.NOT_FOUND.value(), "application/json");
	}

	@Test
	public void patchTypepropertieswithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/types/" + ASSET_TYPE + "/properties";
		responseCheck(uri, "", "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchAssetpropertieswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchAssetProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.OK.value(), "application/json");
	}

	@Parameters({ "Filename" })
	@Test(dataProvider = "PatchAssetpropertiesJson", dataProviderClass = APMDataProvider.class)
	public void patchAssetpropertiesNegativeCase(String filename) throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + filename,PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchAssetpropertiesinvalidContentType() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchAssetProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "text/plain");

	}

	@Test
	public void patchAssetpropertiesinvalidID() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + INVALID_ASSET + "/properties";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchAssetProp.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.NOT_FOUND.value(), "application/json");
	}

	@Test
	public void patchAssetpropertieswithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "/properties";
		responseCheck(uri, "", "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchAssetparentswithvalidinputs() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "/parents";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchAssetParents.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.OK.value(), "application/json");
	}

	@Parameters({ "Filename" })
	@Test(dataProvider = "PatchAssetparentsJson", dataProviderClass = APMDataProvider.class)
	public void patchAssetparentsNegativeCase(String filename) throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "/parents";
		String content = readPayloadFile(JSON_FILE_PATH + filename,PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	@Test
	public void patchAssetparentsinvalidContentType() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "/parents";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchAssetParents.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "text/plain");

	}

	@Test
	public void patchAssetparentsinvalidID() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "---" + "/parents";
		String content = readPayloadFile(JSON_FILE_PATH + "PatchAssetParents.json",PARAM_MAP);
		responseCheck(uri, content, "patch", HttpStatus.NOT_FOUND.value(), "application/json");
	}

	@Test
	public void patchAssetparentswithnullbody() throws Throwable {
		String uri = getProperty(APM_SERVICE_URL) + "/assets/" + ASSET + "/parents";
		responseCheck(uri, "", "patch", HttpStatus.BAD_REQUEST.value(), "application/json");
	}

	private void responseCheck(String uri, String content, String method, int status, String contentType) {
		Response response = null;
		if (method.equals("patch")) {
			response = RestAssured.given().contentType(contentType).body(content).header("Authorization", AUTH_TOKEN)
					.when().patch(uri).then().extract().response();
		} else if (method.equals("put")) {
			response = RestAssured.given().contentType("application/json").body(content)
					.header("Authorization", AUTH_TOKEN).when().put(uri).then().extract().response();
		}
		assertTrue(getFailureMessage(status, response), response.getStatusCode() == status);
		logger.info("Response time:" + response.getTime());
	}

}
