package com.ge.current.em.automation.ingestion;

import com.ge.current.em.APMJsonUtility;
import com.ge.current.em.automation.util.EMTestUtil;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import com.jcraft.jsch.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.restassured.RestAssured.given;

/**
 * Created by 212582713 on 12/16/16.
 */

enum Trends {
	LinearNoVariance, LinearVarianceWithRange
}

public class IngestionTest extends EMTestUtil {

	CSVReader csvReader, csvAssetReader;
	CSVWriter csvWriter;
	APMJsonUtility jsonUtility = new APMJsonUtility();
	private int SERIES_COUNT;
	private static final int MAX_ASSET_TAGS_COUNT = 70;
	private static final String ASSET_FILE = "IngestionAssetFiles.txt";
	private static final String ASSET_INGESTION_FILEPATH = "src/main/resources/AssetIngestionFiles/";
	private static final String SITE_DEFINITION_FILEPATH ="src/main/resources/SiteDefinitionFiles/";
	Properties assetMapProperties = new Properties();
	public static Properties context = null;
	private static final int SFTPPORT = 22;
	private static final String ENERGY_READING_IN_MINUTES = "energyReadingInMinutes";
	private static final String ENERGY_READING_IN_SECONDS = "energyReadingInSeconds";

	// SFTP details - public IP
	/*
	 * private static final String SFTPUSER = "ge"; private static final String
	 * SFTPHOST = "14.140.127.132"; private static final String SFTPPASSWORD =
	 * "info@999"; private static final String SFTPDIR
	 * ="/var/ftp/ge/em-history-data-loader/";
	 */
	// SFTP details - public IP - WinVM
	private static final String SFTPUSER = "loaner";
	private static final String SFTPHOST = "10.187.252.166";//"3.39.192.195";
	private static final String SFTPPASSWORD = "GE10Loaner";//"G2o!7$3p0*j(4c56";
	private static final String SFTPDIR = "/Users/Loaner/sftp/em-history-data-loader/";
	private static final String SFTP_SITES_FOLDER = "sites";
	private static final String SFTP_ASSETS_FOLDER = "assets";
	private String INGESTION_INTERVAL = "1";
	private static final String THROTTLING = "false";

	@BeforeTest
	public void setup() {
		SERIES_COUNT = this.getDuration();
	}

	/*
	 * This test checks if the time-series data file for the given Asset_type
	 * with required tags was created and uploaded to sftp successfully.
	 */
	@Parameters({ "dataPatternFile"}) 
	@Test
	public void testCreateIngestAHUNormalizedOnlyRequired(@org.testng.annotations.Optional("AHU.csv")String dataPatternFile)
			throws URISyntaxException, IOException {
		String outputFileName = getProperty("em.Site.siteName") + "_"
				+ getProperty("em.Asset.assetName")
				+ System.currentTimeMillis() + ".csv";

		context = new Properties();
		context.setProperty("assetName", getProperty("em.Asset.assetName"));
		context.setProperty("assetSourcekey",getProperty("em.Asset.assetSourcekey"));
		context.setProperty("assetTypeSourcekey", getProperty("em.Asset.assetTypeSourcekey"));
		context.setProperty("assetModelTypeSourceKey", getProperty("em.Asset.assetModelTypeSourceKey"));
		context.setProperty("siteName", getProperty("em.Site.siteName"));
		context.setProperty("timeZone", getProperty("em.timezone"));
		context.setProperty("siteFile","FTEnterprise_Sites.csv");
		
		String assetCsvFileName = createUploadCSVFileStoreName(dataPatternFile,outputFileName, context);
		Assert.assertNotNull(assetCsvFileName,
				"AssetCSV was not created or uploaded to sftp");
		context.setProperty("enterpriseSourceKey", getProperty("em.Enterprise.enterpriseSourceKey"));
		context.setProperty("asset_type","AHU");
		context.setProperty("asset_model_type","AHU_Model_1");
		testTimeStampAssetIngestion(context);
	}

    @Parameters({ "dataPatternFile"})
    @Test
    public void testCreateIngestCoolingTowerNormalizedOnlyRequired(@org.testng.annotations.Optional("CoolingTower.csv")String dataPatternFile)
            throws URISyntaxException, IOException {
		if(SERIES_COUNT == 0) {
			SERIES_COUNT = 11;
		}
        String outputFileName = getProperty("em.CoolingTower.Site.siteName") + "_"
                + getProperty("em.CoolingTower.Asset.assetName")
                + System.currentTimeMillis() + ".csv";

        context = new Properties();
        context.setProperty("assetName", getProperty("em.CoolingTower.Asset.assetName"));
        context.setProperty("assetSourcekey",getProperty("em.CoolingTower.Asset.assetSourcekey"));
        context.setProperty("assetTypeSourcekey",getProperty("em.CoolingTower.Asset.assetTypeSourcekey"));
        context.setProperty("assetModelTypeSourceKey",getProperty("em.CoolingTower.Asset.assetModelTypeSourceKey"));
        context.setProperty("siteName",getProperty("em.CoolingTower.Site.siteName"));
        context.setProperty("timeZone",getProperty("em.CoolingTower.timezone"));
		context.setProperty("siteFile","E2E-Sample_Enterprise_Sites.csv");

        String assetCsvFileName = createUploadCSVFileStoreName(dataPatternFile,outputFileName, context);
        Assert.assertNotNull(assetCsvFileName,
                "AssetCSV was not created or uploaded to sftp");
        context.setProperty("enterpriseSourceKey", getProperty("em.CoolingTower.Enterprise.enterpriseSourceKey"));
        context.setProperty("asset_type","Cooling Tower");
        context.setProperty("asset_model_type","Cooling Tower Model");
		INGESTION_INTERVAL = "10";
        testTimeStampAssetIngestion(context);
    }


	@Test
	public void testCreateIngestLightingNormalizedOnlyRequired(String dataPatternFile)
			throws URISyntaxException, IOException {
		createIngestLightingNormalizedRequiredOnlyForRuleFile(dataPatternFile);
	}
	
	private void createIngestLightingNormalizedRequiredOnlyForRuleFile(String dataDefinitionFile) throws URISyntaxException, IOException{
		String outputFileName = getProperty("em.Lighting.Site.siteName") + "_"
				+ getProperty("em.Lighting.Asset.assetName")
				+ System.currentTimeMillis() + ".csv";

		context = new Properties();
		context.setProperty("assetName", getProperty("em.Lighting.Asset.assetName"));
		context.setProperty("assetSourcekey",getProperty("em.Lighting.Asset.assetSourcekey"));
		context.setProperty("assetTypeSourcekey", getProperty("em.Lighting.Asset.assetTypeSourcekey"));
		context.setProperty("assetModelTypeSourceKey", getProperty("em.Lighting.Asset.assetModelTypeSourceKey"));
		context.setProperty("siteName", getProperty("em.Lighting.Site.siteName"));
		context.setProperty("timeZone", getProperty("em.Lighting.timezone"));
		context.setProperty("enterpriseSourceKey", getProperty("em.Lighting.Enterprise.enterpriseSourceKey"));
		context.setProperty("siteFile","FTEnterprise_Sites.csv");
		context.setProperty("asset_type","Light");
		context.setProperty("asset_model_type","Light_Model_1");
		
		String assetCsvFileName = createUploadCSVFileStoreName(dataDefinitionFile,outputFileName, context);
		Assert.assertNotNull(assetCsvFileName,
				"AssetCSV was not created or uploaded to sftp");
		testTimeStampAssetIngestion(context);
	}
	
	@Parameters({ "dataPatternFile"})
	@Test
	public void testCreateIngestSubmeterNormalizedOnlyRequired(@org.testng.annotations.Optional("SubMeter.csv") String dataPatternFile)
			throws URISyntaxException, IOException {
		
		String outputFileName = getProperties().getProperty("em.Submeter.Site.siteName") + "_"
				+ getProperties().getProperty("em.Submeter.Asset.assetName")
				+ System.currentTimeMillis() + ".csv";

		context = new Properties();
		context.setProperty("assetName", getProperty("em.Submeter.Asset.assetName"));
		context.setProperty("assetSourcekey", getProperty("em.Submeter.Asset.assetSourcekey"));
		context.setProperty("assetTypeSourcekey", getProperty("em.Submeter.Asset.assetTypeSourcekey"));
		context.setProperty("assetModelTypeSourceKey", getProperty("em.Submeter.Asset.assetModelTypeSourceKey"));
		context.setProperty("siteName", getProperty("em.Submeter.Site.siteName"));
		context.setProperty("timeZone", getProperty("em.Submeter.timezone"));
		context.setProperty("enterpriseSourceKey", getProperty("em.Submeter.Enterprise.enterpriseSourceKey"));
		context.setProperty("siteFile","FTEnterprise_Sites.csv");
		context.setProperty("asset_type","SUBMETER");
		context.setProperty("asset_model_type","Submeter Model");
		String assetCsvFileName = createUploadCSVFileStoreName(dataPatternFile,outputFileName, context);
		Assert.assertNotNull(assetCsvFileName,
				"AssetCSV was not created or uploaded to sftp");
		testTimeStampAssetIngestion(context);
		
	}
	/**
	 * This test is used to ingest the created CSV file to the rabbitMQ via the
	 * bulkLoad APIs
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testTimeStampAssetIngestionForAHU() throws URISyntaxException, IOException{
		context = new Properties();
		context.setProperty("siteFile","FTEnterprise_Sites.csv");
		context.setProperty("enterpriseSourceKey", getProperties().getProperty("em.Enterprise.enterpriseSourceKey"));
		context.setProperty("asset_type","AHU");
		context.setProperty("asset_model_type","AHU_Model_1");
		testTimeStampAssetIngestion(context);
	}
	
	private void testTimeStampAssetIngestion(Properties context) throws URISyntaxException,
			IOException {
		File assetFile = new File(ASSET_FILE);
		try {
			String assetCsvFileName;

			BufferedReader in = new BufferedReader(new FileReader(
					assetFile.getCanonicalFile()));

			while ((assetCsvFileName = in.readLine()) != null) {

				String assetCsvSourcekey = postAssetMapForAssetCSV(
						assetCsvFileName, null,context);
				System.out
						.println("assetCsvSourcekey --->" + assetCsvSourcekey);

				if (!postAssetSiteMapForSiteCSV(assetCsvFileName,
						context.getProperty("siteFile"),
						context.getProperty("enterpriseSourceKey"))) {
					System.out
							.println("\n<<<---Asset /Site mapping failed --->>>>");
				}
				loadSiteCSV(assetCsvSourcekey,
						context.getProperty("enterpriseSourceKey"));
				loadAssetCSV(assetCsvSourcekey);
				ingestData(assetCsvSourcekey, INGESTION_INTERVAL, THROTTLING);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (assetFile.delete()) {
				System.out.println(assetFile.getCanonicalPath()
						+ " was deleted");
			} else {
				System.out.println(assetFile.getCanonicalPath()
						+ " could not be deleted");
			}
		}
	}

	@Test
	public void testTimeStampAssetIngestionAll() throws URISyntaxException,
			IOException {
		File assetFile = new File(ASSET_FILE);
		try {
			String assetCsvFileName;
			String[] rowData;

			csvAssetReader = new CSVReader(new FileReader(
					SITE_DEFINITION_FILEPATH +"Assets.csv"));
			BufferedReader in = new BufferedReader(new FileReader(
					assetFile.getCanonicalFile()));

			while ((assetCsvFileName = in.readLine()) != null
					&& (rowData = csvAssetReader.readNext()) != null) {

				String assetCsvSourcekey = postAssetMapForAssetCSV(
						assetCsvFileName, rowData,null);
				System.out
						.println("assetCsvSourcekey --->" + assetCsvSourcekey);

				if (!postAssetSiteMapForSiteCSV(assetCsvFileName, rowData[14],
						rowData[10])) {
					System.out
							.println("<<<---Asset /Site mapping failed --->>>>");
				}
				loadSiteCSV(assetCsvSourcekey, rowData[10]);
				loadAssetCSV(assetCsvSourcekey);
				ingestData(assetCsvSourcekey, rowData[11], rowData[12]);
			}
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (assetFile.delete()) {
				System.out.println(assetFile.getCanonicalPath()
						+ " was deleted");
			} else {
				System.out.println(assetFile.getCanonicalPath()
						+ " could not be deleted");
			}
		}
	}

	/**
	 * This test is used to ingest the created CSV file to the rabbitMQ via the
	 * bulkLoad APIs for all assets/sites within an enterprise
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testCreateAllAssetTimestampIngestionFiles()
			throws URISyntaxException, IOException {
		csvAssetReader = new CSVReader(new FileReader(
				SITE_DEFINITION_FILEPATH +"Assets.csv"));
		String[] rowData;

		while ((rowData = csvAssetReader.readNext()) != null) {
			SERIES_COUNT = Integer.parseInt(rowData[9]);
			createUploadCSVFileStoreName(rowData,
					Trends.LinearVarianceWithRange);

		}
		csvAssetReader.close();
	}

	private void ingestData(String assetCsvSourcekey, String interval,
			String throttling) {
		given().log()
				.all().header("Authorization","Bearer "+getBLUToken())
				.urlEncodingEnabled(false)
				.when()
				.put(getProperty("bulk_load_tool_url") + "/ingest/"
						+ assetCsvSourcekey + "/" + interval + "/" + throttling)
				.then().statusCode(HttpStatus.SC_OK);

	}

	private void loadSiteCSV(String assetCsvSourcekey, String enterpriseSK) {
		given().log()
				.all().header("Authorization","Bearer "+getBLUToken())
				.urlEncodingEnabled(false)
				.when()
				.post(getProperty("bulk_load_tool_url") + "/site/load/"
						+ enterpriseSK + "/" + assetCsvSourcekey);

	}

	private void loadAssetCSV(String assetCsvSourcekey) {
		given().log()
				.all().header("Authorization","Bearer "+getBLUToken())
				.urlEncodingEnabled(false)
				.when()
				.post(getProperty("bulk_load_tool_url") + "/asset/load/"
						+ assetCsvSourcekey).then()
				.statusCode(HttpStatus.SC_OK);

	}

	private String postAssetMapForAssetCSV(String assetFileName,
			String[] rowData, Properties context) {
		String assetCsvSourcekey = null;
		String path = "src/main/resources/test-suite/data/payload/postAssetMap.json";

		if (rowData == null) {
			assetMapProperties
					.setProperty("ASSET_MODEL_TYPE", context.getProperty("asset_model_type"));
			assetMapProperties.setProperty("ASSET_TYPE", context.getProperty("asset_type"));
			assetMapProperties.setProperty("ENTERPRISE_SK",
					context.getProperty("enterpriseSourceKey"));
		} else {
			assetMapProperties.setProperty("ASSET_MODEL_TYPE", rowData[5]);
			assetMapProperties.setProperty("ASSET_TYPE", rowData[3]);
			assetMapProperties.setProperty("ENTERPRISE_SK", rowData[10]);
		}
		assetMapProperties.setProperty("ASSET_MODEL_TYPE_SK", "");
		// getTypeSourceKey("ASSET_MODEL_TYPE",ASSET_MODEL_TYPE));
		assetMapProperties.setProperty("ASSET_TYPE_SK", "");
		// getTypeSourceKey("ASSET_TYPE",ASSET_TYPE));
		assetMapProperties.setProperty("CSV_Name", assetFileName);

		try {
			Response response = given()
					.log()
					.all().header("Authorization","Bearer "+getBLUToken())
					.contentType(ContentType.JSON)
					.body(jsonUtility.readPayloadFile(path, assetMapProperties))
					.when()
					.put(getProperty("bulk_load_tool_url") + "/asset/map");
			System.out.println("response --->" + response.asString());

			JsonPath jsonPath = response.jsonPath();
                        assetCsvSourcekey = jsonPath.getString("sourceKey")
                            .replace("[", "").replace("]", "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return assetCsvSourcekey;
	}

	private boolean postAssetSiteMapForSiteCSV(String assetCsvFile,
			String siteCsvFile, String enterpriseId) {
		boolean siteMappingStatus = true;
		
		Response response = given()
				.log().all().header("Authorization","Bearer "+getBLUToken())
				.contentType(ContentType.JSON)
				.body("{\n" + "\"assetCsvName\":\"" + assetCsvFile + "\",\n"
						+ "\"siteCsvName\":\"" + siteCsvFile + "\"\n" + "}")
				.when()
				.put(getProperty("bulk_load_tool_url") + "/asset/site/map");
		
		System.out.println("postAssetSiteMapForSiteCSV response --->"
				+ response.asString());
		JsonPath jsonPath = response.jsonPath();
		if (!jsonPath.getString("enterpriseSourceKey").replace("[", "")
				.replace("]", "").equals(enterpriseId)) {
			siteMappingStatus = false;
		}
		return siteMappingStatus;
	}

	@SuppressWarnings("unused")
	private String getTypeSourceKey(String typeClass, String typeName) {
		String typeSourceKey = null;
		Response responseBody = given()
				.log()
				.all()
				.header("ROOT_ENTERPRISE_SOURCEKEY",
						getProperty("em.Enterprise.enterpriseSourceKey"))
				.param("typeClass", typeClass).param("typeName", typeName)
				.when().get(getProperty("apm_service_url") + "/types");
		
		System.out.println("response --->" + responseBody.asString());
		
		JsonPath jsonPath = responseBody.jsonPath();
		typeSourceKey = jsonPath.getString("content.sourceKey")
				.replace("[", "").replace("]", "");

		return typeSourceKey;
	}

	 @Test
	 public void testCreateAHURaw() throws URISyntaxException
	 {
		 try {
	 		String outputFileName = getProperty("em.Site.siteName")+ "_" +
					getProperty("em.Asset.assetName") + System.currentTimeMillis() +".csv";

			 String igFile = makeFileForIgestionWithRawPoints("AHU.csv", true, Trends.LinearVarianceWithRange,outputFileName);

			 if(postFileToSFTPBulkLoadServer(igFile, SFTP_ASSETS_FOLDER)) {
				 System.out.println("\nfile uploaded is: " + igFile);
				 Assert.assertTrue(true);
			 }else{
				 Assert.assertTrue(false);
			 }
	 } catch (IOException e)
		 {
			 e.printStackTrace();
		 }
	 }


	 /* @Test public void testCreateAHUNormalizedAllTags() throws
	 * URISyntaxException { try { //String outputFileName = SITE_NAME+ "_" +
	 * ASSET_NAME + System.currentTimeMillis(); //String igFile =
	 * makeFileForIgestionWithHaystackTags("AHU", false,
	 * Trends.LinearNoVariance,outputFileName); //igFile =
	 * makeFileForIgestionWithHaystackTags("AHU", false,
	 * Trends.LinearVarianceWithRange);
	 * 
	 * //assertTrue(igFile.contains("AHU")); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 * 
	 * @Test public void testCreateVAVNormalizedOnlyRequired() throws
	 * URISyntaxException { try { String igFile =
	 * makeFileForIgestionWithHaystackTags("VAV", true,
	 * Trends.LinearNoVariance); igFile =
	 * makeFileForIgestionWithHaystackTags("VAV", true,
	 * Trends.LinearVarianceWithRange); assertTrue(igFile.contains("VAV")); }
	 * catch (IOException e) { e.printStackTrace(); } }
	 * 
	 * @Test public void testCreateVAVNormalizedAllTags() throws
	 * URISyntaxException { try { String igFile =
	 * makeFileForIgestionWithHaystackTags("VAV", false,
	 * Trends.LinearNoVariance); igFile =
	 * makeFileForIgestionWithHaystackTags("VAV", false,
	 * Trends.LinearVarianceWithRange); assertTrue(igFile.contains("VAV")); }
	 * catch (IOException e) { e.printStackTrace(); } }
	 * 
	 * @Test public void testCreateVAVRaw() throws URISyntaxException { try {
	 * String igFile = makeFileForIgestionWithRawPoints("VAV", true,
	 * Trends.LinearNoVariance); igFile =
	 * makeFileForIgestionWithRawPoints("VAV", true,
	 * Trends.LinearVarianceWithRange); assertTrue(igFile.contains("VAV")); }
	 * catch (IOException e) { e.printStackTrace(); } }
	 */
/**
	private boolean uploadSiteCSVtoSftp(String filePath) {
//		String filePath = "src/main/resources/SiteDefinitionFiles/FTEnterprise_Sites.csv";
		postFileToSFTPBulkLoadServer(filePath, SFTP_SITES_FOLDER);
		return true;
	}
*/

	/**
	 * Create time-series CSV for the given enterprise and Asset( stored in colo
	 * properties) Post to SFTP - BulkLoadAPI needs these files(Site and asset)
	 * Store the AssetCSV name in a text file for ingestion to follow next.
	 * 
	 * @param
	 * @return
	 */
	private String createUploadCSVFileStoreName(String[] assetRule,
			Trends variance) {
		String outputFileName = assetRule[0] + System.currentTimeMillis() + "_"
				+ assetRule[7];
		String fileName = null;
		
		FileWriter fw;
		try {
			fileName = makeFileForIgestionWithHaystackTags(assetRule[7], true,
					variance, outputFileName, assetRule);
			
			postFileToSFTPBulkLoadServer(fileName, SFTP_ASSETS_FOLDER);
			postFileToSFTPBulkLoadServer(SITE_DEFINITION_FILEPATH + "/"+assetRule[14],SFTP_SITES_FOLDER);

			fw = new FileWriter(ASSET_FILE, true);
			fw.write(outputFileName + "\n");
			fw.close();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return outputFileName;
	}

	private String createUploadCSVFileStoreName(String assetTypeFile,String outputFileName, Properties context) {

		String fileName = null;
		FileWriter fw;
		try {
			fileName = makeFileForIgestionWithHaystackTags(assetTypeFile, true,
					Trends.LinearVarianceWithRange, outputFileName, context);

			postFileToSFTPBulkLoadServer(SITE_DEFINITION_FILEPATH +context.getProperty("siteFile"), SFTP_SITES_FOLDER);
			postFileToSFTPBulkLoadServer(fileName, SFTP_ASSETS_FOLDER);

			fw = new FileWriter(ASSET_FILE, true);
			fw.write(outputFileName + "\n");
			fw.close();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		// p.setProperty("em.Igestion.assetFileName", outputFileName);
		return outputFileName;
	}

	private String makeFileForIgestionWithRawPoints(String asset_type_csv,
			boolean onlyRequiredTags, Trends variance, String outputFileName)
			throws IOException {
		String folder = "DataPatternFiles";
		String type = "Raw";
		String filePath = "src/main/resources/" + folder;
		
		context = new Properties();
		context.setProperty("assetName", getProperty("em.Asset.assetName"));
		context.setProperty("assetSourcekey",getProperty("em.Asset.assetSourcekey"));
		context.setProperty("assetTypeSourcekey", getProperty("em.Asset.assetTypeSourcekey"));
		context.setProperty("assetModelTypeSourceKey", getProperty("em.Asset.assetModelTypeSourceKey"));
		context.setProperty("siteName", getProperty("em.Site.siteName"));
		context.setProperty("timeZone", getProperty("em.timezone"));
		
		return makeIngestionFileFromCSV(filePath.toString(), asset_type_csv,
				type, onlyRequiredTags, variance, outputFileName, null, context);

	}

	/***
	 * Create a timeseries data given a Asset_type definition file, variance
	 * function for the data series, and tags required
	 * 
	 * @param
	 * @param onlyRequiredTags
	 * @param variance
	 * @param outputFileName
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private String makeFileForIgestionWithHaystackTags(String asset_type_csv,
			boolean onlyRequiredTags, Trends variance, String outputFileName, Properties context)
			throws IOException, URISyntaxException {
		String folder = "DataPatternFiles";
		String type = "Normalized";
		String filePath = "src/main/resources/" + folder;
		
		return makeIngestionFileFromCSV(filePath.toString(), asset_type_csv,
				type, onlyRequiredTags, variance, outputFileName, null,context);

	}

	private String makeFileForIgestionWithHaystackTags(String asset_type_csv,
			boolean onlyRequiredTags, Trends variance, String outputFileName,
			String[] assetRule) throws IOException, URISyntaxException {
		String folder = "DataPatternFiles";
		String type = "Normalized";
		String filePath = "src/main/resources/" + folder;
		
		return makeIngestionFileFromCSV(filePath.toString(), asset_type_csv,
				type, onlyRequiredTags, variance, outputFileName, assetRule,null);

	}

	private void fillData(Trends function, String[] headerType,
			String[] supportedValue, String[] igestionNumeralRange,
			CSVWriter csvWriter, String[] assetRule, Properties context) throws ParseException {
		String[] rowData = new String[headerType.length];
		long unixTime = System.currentTimeMillis() / 1000L;
		String timeZone = "PST", timeZoneString;
		if (assetRule == null) {
			rowData[0] = context.getProperty("siteName");
			rowData[1] = context.getProperty("assetName");
			rowData[4] = context.getProperty("assetSourcekey");
			rowData[5] = context.getProperty("assetTypeSourcekey");
			rowData[6] = context.getProperty("assetModelTypeSourceKey");
			timeZoneString = context.getProperty("timeZone");

		} else {
			rowData[0] = assetRule[0];
			rowData[1] = assetRule[1];
			rowData[4] = assetRule[2];
			rowData[5] = assetRule[4];
			rowData[6] = assetRule[6];
			timeZone = assetRule[8];
			timeZoneString = assetRule[13];
		}

		rowData[2] = Long.toString(unixTime);
		Timestamp timestamp;
		Date dateTime;
		SimpleDateFormat dFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		dFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

		switch (function) {
		case LinearNoVariance: {
			for (int rowIndex = 0; rowIndex < SERIES_COUNT; rowIndex++) {
				for (int index = 7; index < headerType.length; index++) {
					if (headerType[index] != null) {
						// System.out.print("\n" + headerType[index]);
						if (!headerType[index].equals("On or Off")
								&& !headerType[index].equals("enum")
								&& !headerType[index].equals("boolean")) {
							rowData[index] = "1";
						} else if (!headerType[index].equals("enum")
								&& !headerType[index].equals("boolean")) {
							rowData[index] = "On";
						} else if (!headerType[index].equals("boolean")) {
							String[] enumValue = supportedValue[index]
									.split(",");
							rowData[index] = enumValue[0];
						} else {
							rowData[index] = "true";
						}
					}
				}
				timestamp = new Timestamp(Long.parseLong(rowData[2]) * 1000L);
				dateTime = new Date(timestamp.getTime());
				rowData[3] = dFormat.format(dateTime).toString() + " "
						+ timeZoneString;
				// +" " + TIME_ZONE;

				csvWriter.writeNext(rowData);
				// add 60 second for every row
				rowData[2] = Long.toString(Long.parseLong(rowData[2]) + 60);
			}
		}
			break;
		case LinearVarianceWithRange: {
			// ingests data at 1 min interval.
			int timeInterval = 60;
			List<Integer[]> fillData = new ArrayList<Integer[]>(SERIES_COUNT);
			List<String[]> fillStringData = new ArrayList<String[]>(SERIES_COUNT);
			List<Long[]> fillEpochTime = new ArrayList<Long[]>(SERIES_COUNT);

			for (int i = 0; i < headerType.length; i++) {
				fillData.add(i, null);
				fillStringData.add(i, null);
				fillEpochTime.add(i, null);
			}
			// preprocess the numeral column to come back with resolved value
			// before insertions.
			// insert values as desired
			for (int rowIndex = 0; rowIndex < SERIES_COUNT; rowIndex++) {
				for (int index = 7; index < headerType.length; index++) {
					if (headerType[index] != null) {
						// System.out.print("\n" + headerType[index]);
						if (headerType[index].equals("boolean")) {
							rowData[index] = "true";

						} else if (headerType[index].equals("On or Off")) {
							rowData[index] = "On";
						} else if (headerType[index].equals("enum")) {
							String[] enumValue = supportedValue[index]
									.split(",");

							String[] rangeData = fillEnumStringData(enumValue, SERIES_COUNT);
							fillStringData.set(index, rangeData);

							rowData[index] = fillStringData.get(index)[rowIndex];

						} else {
							if (rowIndex == 0
									&& !igestionNumeralRange[index].isEmpty()) {

								if(igestionNumeralRange[index].equalsIgnoreCase(ENERGY_READING_IN_MINUTES)){
									Long[] epochTimeData = fillEpochTimeData(SERIES_COUNT);
									fillEpochTime.set(index, epochTimeData);
									rowData[index] = Long.toString(fillEpochTime.get(index)[rowIndex]);
								}
								else{
									String[] range = igestionNumeralRange[index].split(",");
									Integer[] rangeData = null;
									if(range.length >=2 && range[1] != null) {
										rangeData = fillRangeData(Integer.parseInt(range[0]), Integer.parseInt(range[1]),
												SERIES_COUNT);

									}else{
										rangeData = fillRangeData(Integer.parseInt(range[0]), Integer.parseInt(range[0]),
												SERIES_COUNT);
									}
									fillData.set(index, rangeData);
								}
							}
							if(null!= fillData && null!= fillData.get(index)) {
								rowData[index] = Integer.toString(fillData.get(index)[rowIndex]);
							}
						}
					}
				}
				timestamp = new Timestamp(Long.parseLong(rowData[2]) * 1000L);
				dateTime = new Date(timestamp.getTime());
				rowData[3] = dFormat.format(dateTime).toString() + " "
						+ timeZoneString;

				csvWriter.writeNext(rowData);
				// add 60 second for every row
				rowData[2] = Integer.toString(Integer.parseInt(rowData[2])
						+ timeInterval);
			}
		}
		}
		return;
	}

	private Long[] fillEpochTimeData(int size) {
		Long[] rangeArray = new Long[size];
			for (int index = 0; index < size; index++) {
				long seconds = System.currentTimeMillis() / 1000;
				long minutes = seconds / 60;
				rangeArray[index] = minutes;
			}
		return rangeArray;
	}

	/**
	 * Read asset_type_def files for asset_tag names, required fields,raw
	 * points,data range, enum values Initiate a csv formatted file with column
	 * headers derieved from tag_types.
	 * */
	private String makeIngestionFileFromCSV(String csvFilePath,
			String csvFileName, String type, boolean onlyRequiredTags,
			Trends variance, String outputFileName, String[] assetRule,Properties context)
			throws IOException {
		int data_column = 0;

		csvReader = new CSVReader(new FileReader(csvFilePath + "/"
				+ csvFileName));
		if (type.equals("Normalized"))
			data_column = 1;

		String[] igestionFileHeaderstemp = new String[MAX_ASSET_TAGS_COUNT];
		String[] igestionHeaderTypestemp = new String[MAX_ASSET_TAGS_COUNT];
		String[] igestionRequired = new String[MAX_ASSET_TAGS_COUNT];
		String[] igestionEnumValues = new String[MAX_ASSET_TAGS_COUNT];
		String[] igestionNumeralRange = new String[MAX_ASSET_TAGS_COUNT];
		String[] rowData;
		int lineNumber = 0;
		int colNumber = 7;
		igestionFileHeaderstemp[0] = "siteRef";
		igestionFileHeaderstemp[1] = "name";
		igestionFileHeaderstemp[2] = "Min15SinceEpoch";
		igestionFileHeaderstemp[3] = "ts";
		igestionFileHeaderstemp[4] = "sourceKey";
		igestionFileHeaderstemp[5] = "typeSourceKey";
		igestionFileHeaderstemp[6] = "modelTypeSourceKey";

		while ((rowData = csvReader.readNext()) != null) {
			if (lineNumber > 0) {
				if (rowData[4].equals("N") && onlyRequiredTags)
					continue;
				else {
					igestionFileHeaderstemp[colNumber] = rowData[data_column];
					igestionHeaderTypestemp[colNumber] = rowData[3];
					igestionRequired[colNumber] = rowData[4];
					igestionEnumValues[colNumber] = rowData[5];
					igestionNumeralRange[colNumber] = rowData[6];
					colNumber++;
				}
			}
			lineNumber++;
		}
		csvReader.close();
		String[] igestionFileHeaders = new String[colNumber];
		igestionFileHeaders = Arrays.copyOfRange(igestionFileHeaderstemp, 0,
				colNumber);

		String[] igestionHeaderType = new String[colNumber];
		igestionHeaderType = Arrays.copyOfRange(igestionHeaderTypestemp, 0,
				colNumber);

		// Create igestionFile with headers from DataPatternFiles/raw points
		String igestionFilePath = ASSET_INGESTION_FILEPATH;
		String igestionFileName;

		if (onlyRequiredTags)
			igestionFileName = outputFileName + "_" + "Required";
		igestionFileName = outputFileName;

		csvWriter = new CSVWriter(new FileWriter(igestionFilePath + "/"
				+ igestionFileName, true), ',', CSVWriter.NO_QUOTE_CHARACTER);
		csvWriter.writeNext(igestionFileHeaders);

		// create data rows for the pattern needed
		try {
			fillData(variance, igestionHeaderType, igestionEnumValues,
					igestionNumeralRange, csvWriter, assetRule, context);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		csvWriter.close();

		// return the newly created full file with headers and data
		String igestionFileFullPath = new File(igestionFilePath + "/"
				+ igestionFileName).getPath().toString();
		return igestionFileFullPath;
	}

	private Integer[] fillRangeData(int lowerBound, int upperBound, int size) {
		Integer[] rangeArray = new Integer[size];
		rangeArray[0] = lowerBound;
		if (upperBound == lowerBound) {
			for (int index = 1; index < size; index++) {
				rangeArray[index] = lowerBound;
			}
		} else {
			for (int index = 1; index < size; index++) {
				rangeArray[index] = (rangeArray[index - 1] + 1)
						% (upperBound + 1);
				rangeArray[index] = (rangeArray[index] < lowerBound) ? lowerBound
						: rangeArray[index];
			}
		}
		return rangeArray;
	}

	private String[] fillEnumStringData(String[] enumValue, int size) {
		String[] rangeArray = new String[size];
		if (enumValue.length == 1) {
			for (int index = 0; index < size; index++) {
				rangeArray[index] = enumValue[0];
			}
		} else {
			for (int index = 0; index < size; index++) {
				int pickUpVal = index % enumValue.length;
				rangeArray[index] = enumValue[pickUpVal];
			}
		}
		return rangeArray;
	}

	private boolean postFileToSFTPBulkLoadServer(String fileName,
			String folderName) {

		String SFTPWORKINGDIR = SFTPDIR + folderName;
		ChannelSftp sftpChannel = null;
		Session sftpSession = null;

		JSch jsch = new JSch();
		try {
			sftpSession = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			sftpSession.setPassword(SFTPPASSWORD);
			sftpSession.setConfig("StrictHostKeyChecking", "no");
			System.out.println("Establishing Connection...");
			sftpSession.connect();
			System.out.println("Connection established.");
			System.out.println("Creating SFTP Channel....");
			sftpChannel = (ChannelSftp) sftpSession.openChannel("sftp");
			sftpChannel.connect();
			sftpChannel.cd(SFTPWORKINGDIR);
			System.out.println("SFTP Channel created.");

			// Add file to sftp server
			File f = new File(fileName);
			sftpChannel.put(new FileInputStream(f), f.getName());

		} catch (JSchException | SftpException | FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(null!=sftpChannel && null!= sftpSession) {
				sftpChannel.exit();
				System.out.println("sftp Channel exited.");
				sftpChannel.disconnect();
				System.out.println("Channel disconnected.");
				sftpSession.disconnect();
				System.out.println("Host Session disconnected.");
			}
		}
		return true;
	}
}
