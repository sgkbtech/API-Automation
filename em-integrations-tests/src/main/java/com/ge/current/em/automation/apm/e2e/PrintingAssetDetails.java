package com.ge.current.em.automation.apm.e2e;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class PrintingAssetDetails {
	String authTokenURL = "https://e6dfde0c-918e-4d5f-9587-f0deb5652d05.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token?grant_type=client_credentials";
	HashMap<String, Object> assetDetails = new HashMap<String, Object>();
	String password = "se3ret";
	String username = "ems-apm-admin2";
	public static String authToken = null;

	@BeforeMethod
	public void getServiceToken() {

		authToken = given().auth().basic(username, password).param("grant_type", "client_credentials").expect()
				.statusCode(200).when().get(authTokenURL).jsonPath().getString("access_token");
	}

	@Test
	public void assetDetails() throws IOException, EncryptedDocumentException, InvalidFormatException {
		RestAssured.baseURI = "https://ie-ems-persistence-ems-stage.run.aws-usw02-pr.ice.predix.io/v1";
		String getEnterpriseUri = RestAssured.baseURI + "/enterprises/root";
		Response response = given().param("fetchProperties", "true").contentType("application/json").when()
				.header("Authorization", "Bearer" + authToken).get(getEnterpriseUri).then().extract().response();
		String enterpriseDetails = response.getBody().asString();
		List<HashMap<String, Object>> rootEnterprises = JsonPath.from(enterpriseDetails).get("content");
int rowNo = 0;
		for (HashMap<String, Object> rootenterprise : rootEnterprises) {
			//assetDetails.clear();
			String rootEnterpriseid = rootenterprise.get("sourceKey").toString();
			String rootEnterprisename = rootenterprise.get("name").toString();
			String getSiteUri = RestAssured.baseURI + "/enterprises/" + rootEnterpriseid + "/sites";
			Response siteResponse = given().param("fetchProperties", "true").contentType("application/json").when()
					.header("Authorization", "Bearer" + authToken).get(getSiteUri).then().extract().response();
			String siteDetails = siteResponse.getBody().asString();
			List<HashMap<String, Object>> sites = JsonPath.from(siteDetails).get("content");
			String siteId, Sitename, assetId, date, dt = null;

			String segmentId;
			for (HashMap<String, Object> site : sites) {
				siteId = site.get("sourceKey").toString();
				Sitename = site.get("name").toString();
				List<HashMap<String, Object>> siteProperties = (List<HashMap<String, Object>>) site.get("properties");
				for (HashMap<String, Object> siteproperty : siteProperties) {
					if (siteproperty.get("id").toString().equals("EMSActivationDate")) {
						date = siteproperty.get("value").toString();
						String[] formatteddate = date.replace("[", "") // remove
																		// the
																		// right
																		// bracket
								.replace("]", "") // remove the left bracket
								.trim().split("/");
						dt = formatteddate[2] + ("00" + formatteddate[0]).substring(formatteddate[0].length())
								+ ("00" + formatteddate[1]).substring(formatteddate[1].length());
					}

				}
				String getSegmentUri = RestAssured.baseURI + "/sites/" + siteId + "/segments";
				Response siteChildren = given().param("fetchProperties", "true").contentType("application/json").when()
						.header("Authorization", "Bearer" + authToken).get(getSegmentUri).then().extract().response();
				String SegmentDetails = siteChildren.getBody().asString();
				List<HashMap<String, Object>> segments = JsonPath.from(SegmentDetails).get();
				for (HashMap<String, Object> segment : segments) {
					if (segment.get("name").toString().equals("COST_METER")) {
						List<HashMap<String, Object>> segmentProperties = (List<HashMap<String, Object>>) segment
								.get("properties");
						for (HashMap<String, Object> property : segmentProperties) {
							if (property.get("id").toString().equals("correlatedEntity")) {
								segmentId = property.get("value").toString();
								for (HashMap<String, Object> eachSegment : segments) {
									if (segmentId.contains(eachSegment.get("sourceKey").toString())) {
										List<HashMap<String, Object>> eachSegmentProperties = (List<HashMap<String, Object>>) eachSegment
												.get("properties");
										for (HashMap<String, Object> eachSegmentProp : eachSegmentProperties) {
											if (eachSegmentProp.get("id").toString().equals("correlatedEntity")) {
												// String[] stringArray =
												// Arrays.copyOf(eachSegmentProp.get("value"),
												// (Object)eachSegmentProp.get("value").length,
												// String[].class);
												assetId = eachSegmentProp.get("value").toString();
												String formattedassetId = assetId.replace(",", "%2") // remove
																										// the
																										// commas
														.replace("[", "") // remove
																			// the
																			// right
																			// bracket
														.replace("]", "") // remove
																			// the
																			// left
																			// bracket
														.trim();
												assetDetails.put("Asset"+rowNo, formattedassetId);
												assetDetails.put("Sitename"+rowNo, Sitename);
												assetDetails.put("SiteId"+rowNo, siteId);
												assetDetails.put("EnterpriseName"+rowNo, rootEnterprisename);
												assetDetails.put("EnterpriseId"+rowNo, rootEnterpriseid);
												assetDetails.put("Insdate"+rowNo, dt);												
												rowNo++;
												// String str =
												// Arrays.toString(arr);
												/*
												 * for(int
												 * i=0;i<assetId.size();i++){
												 * assetId.
												 */
											}
											// assetId=assetId.replaceAll(",",
											// "%2");
										}
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("RowNo : " + rowNo);
		ExcelUtils.writeAssertDetailsIntoExcel("Asset",assetDetails,rowNo);
		
	}
}
