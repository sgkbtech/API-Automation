package com.ge.current.em.automation.ingestion;

import com.ge.current.em.automation.util.EMTestUtil;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.opencsv.CSVReader;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by 212431401 on 3/27/17.
 */
public class NormalizationComponentTest extends EMTestUtil {
    private static final String SEARCH_BY = "/filtersearch?query=resrc_uid:";
    private static final Map<String, String> haystackMappings = new HashMap<String, String>();
    private static final Map<String, String> jsonKeys = new HashMap<String, String>();
    private String assetSourceKey = null;
    private String assetSourceKey2 = null;
    private String enterpriseSourceKeyProp;
    private String siteSourceKeyProp;
    private String segmentSourceKeyProp;
    private String assetType;
    private String timeZone;
    private String siteNameProp;
    private String eventType = "reading";
    private String fromTime = null;
    private String toTime = null;
    private String _11minDataIngestionInterval = null;
    private String filePath = null;
    //this current tag value is used in test #8, testNormalization_EventNormLogTable_validateTagField_tagIsNonNumericalAndIsLatest:
    private String curTagValue = "true";
    private String event_ts_timestamp1 = "Apr 18, 2017 4:10:21 PM";
    private String event_ts_timestamp2 = "Apr 18, 2017 4:20:21 PM";
    private String event_ts_timestamp3 = "Apr 18, 2017 4:30:21 PM";

    URI normLogUri, eventLogUri, multipleNormLogUri;


    @BeforeClass
    public void setUp() throws Exception {
        IngestionTest ingestionTest = new IngestionTest();
        ingestionTest.testCreateIngestCoolingTowerNormalizedOnlyRequired("CoolingTower.csv");

        enterpriseSourceKeyProp = getProperty("em.CoolingTower.Enterprise.enterpriseSourceKey");
        siteSourceKeyProp = getProperty("em.CoolingTower.Site.siteSourceKey");
        siteNameProp = getProperty("em.CoolingTower.Site.siteName");
        segmentSourceKeyProp = getProperty("em.CoolingTower.Segment.segmentSourceKey");
        assetSourceKey = getProperty("em.CoolingTower.Asset.assetSourcekey");
        assetSourceKey2 = getProperty("em.CoolingTower.Asset.assetSourcekey2");
        assetType = getProperty("em.CoolingTower.Asset.assetType");
        timeZone = getProperty("em.CoolingTower.timezone");

//      fromTime = p.getProperty("em.CoolingTower.queryfromTime");
//      toTime = p.getProperty("em.CoolingTower.querytoTime");
        fromTime = Instant.now().minus(60000L).toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        toTime = Instant.now().plus(600000L).toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        _11minDataIngestionInterval = "event_ts:["+ fromTime+"%20TO%20"+ toTime+"]";
        filePath = getProperty("em.CoolingTower.filePath");

        normLogUri = URI.create(getProperty("solr_service_url")
                + getProperty("DBSchema")
                + getProperty("Normalized")
                + SEARCH_BY
                + assetSourceKey);

        multipleNormLogUri = URI.create(getProperty("solr_service_url")
                + getProperty("DBSchema")
                + getProperty("Normalized")
                + SEARCH_BY
                + assetSourceKey
                + "+resrc_uid:"
                + assetSourceKey2);

        eventLogUri = URI.create(getProperty("solr_service_url")
                + getProperty("DBSchema")
                + getProperty("EventLog")
                + SEARCH_BY
                + assetSourceKey);

        initializeHaystackMappingValuesForAsset001();
    }



    @AfterMethod
    public void tearDown() throws Exception {
//        softAssert.assertAll();
    }

    private void initializeHaystackMappingValuesForAsset001() {

        try {
            CSVReader csvReader = new CSVReader(new FileReader(filePath));

            String[] rowData;
            int lineNumber = 0;

            while ((rowData = csvReader.readNext()) != null) {
                if (lineNumber > 0) {
                    if (rowData[4].equals("N"))
                        continue;
                    else {
                        haystackMappings.put(rowData[0], rowData[1]);
                        //check if measures or tags
                        if (rowData[3] != null && !rowData[3].isEmpty()) {
                            if (rowData[3].equalsIgnoreCase("boolean") || rowData[3].equalsIgnoreCase("enum")) {
                                jsonKeys.put("tags" + rowData[0], "tags" + rowData[1]);
                            } else {
                                jsonKeys.put("measures" + rowData[0], "measures" + rowData[1]);
                            }
                        }
                    }
                }
                lineNumber++;
            }

            csvReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convert12HourTo24Hour(String event_ts) {
        DateFormat readFormat = new SimpleDateFormat( "MMM dd, yyyy hh:mm:ss aa");
        DateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = readFormat.parse(event_ts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            String formattedDate = writeFormat.format(date);
            return formattedDate;
        }
        return null;
    }

    private boolean isEventtsIsInCorrectBucket(String event_ts) {
        SimpleDateFormat sdf[] = new SimpleDateFormat[] {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")};

        Date dateStart = parse(fromTime, sdf);
        Date dateEnd = parse(toTime, sdf);
        Date dateEventTs = parse(event_ts, sdf);

        return (dateStart.before(dateEventTs) || dateStart.equals(dateEventTs)) && (dateEnd.after(dateEventTs)|| dateStart.equals(dateEventTs));
    }

    public static Date parse(String value, DateFormat... formatters) {
        Date date = null;
        for (DateFormat formatter : formatters) {
            try {
                date = formatter.parse(value);
                break;
            } catch (ParseException e) {
            }
        }
        return date;
    }

    private boolean isTimeBucketFor15Min(String time_bucket) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        formatter.setLenient(false);
        Date parsedDate = null;
        try {
            parsedDate = formatter.parse(time_bucket);
            System.out.println("Parsed date = "+ parsedDate + ", for time bucket = "+time_bucket);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Response getNormLogResponse() {
        Response  response =    given().log().all().
                header("Authorization","Bearer "+ getSolrServiceToken()).
                param("fq", _11minDataIngestionInterval).
                urlEncodingEnabled(false).
                when().
                get(normLogUri);

        return response;
    }

    private Response getMultipleNormLogResponse() {
        Response  response =    given().log().all().
                header("Authorization","Bearer "+ getSolrServiceToken()).
                param("fq", _11minDataIngestionInterval).
                urlEncodingEnabled(false).
                when().
                get(multipleNormLogUri);

        return response;
    }

    private Response getEventLogResponse() {
        Response  response =    given().log().all().
                header("Authorization","Bearer "+ getSolrServiceToken()).
                param("fq", _11minDataIngestionInterval).
                urlEncodingEnabled(false).
                when().
                get(eventLogUri);

        return response;
    }

    //test 1
    @Test
    public void testNormalization_EventLogTable_validateMeasuresField_measuresExist() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getEventLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if(completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                jsonKeys.forEach((k, v) -> {
                    if(k.startsWith("measures")) {
                        System.out.println(" :k= " + k + " : v= " + v);
                        softAssert.assertTrue(message.keySet().contains(k),
                                "Expected measure- " + k + " is not found in the event_log table entry with log_uuid:  " + message.get("log_uuid"));
                    }
                });

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 2
    @Test
    public void testNormalization_EventNormLogTable_validateMeasuresField_measuresExist() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if(completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                jsonKeys.forEach((k, v) -> {
                    if(k.startsWith("measures")) {
                        System.out.println(" :k= " + k + " : v= " + v);
                        softAssert.assertTrue(message.keySet().contains(v),
                                "Expected measure- " + v + " is not found in the event_norm_log table entry with log_uuid:  " + message.get("log_uuid"));
                    }
                });

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 3
    @Test
    public void testNormalization_EventLogTable_validateTagsField_tagsExist() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getEventLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if(completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {
                System.out.println("message.keySet() = ");
                System.out.println(message.keySet());

                jsonKeys.forEach((k, v) -> {
                    if(k.startsWith("tags")) {
                        System.out.println(" :k= " + k + " : v= " + v);
                        softAssert.assertTrue(message.keySet().contains(k),
                                "Expected tag- " + k + " is not found in the event_log table entry with log_uuid:  " + message.get("log_uuid"));
                    }
                });

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 4
    @Test
    public void testNormalization_EventNormLogTable_validateTagsField_tagsExist() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if(completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                jsonKeys.forEach((k, v) -> {
                    if(k.startsWith("tags")) {
                        System.out.println(" :k= " + k + " : v= " + v);
                        softAssert.assertTrue(message.keySet().contains(v),
                                "Expected tag- " + v + " is not found in the event_norm_log table entry with log_uuid:  " + message.get("log_uuid"));
                    }
                });

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }


    //test 7
    @Test
    public void testNormalization_EventNormLogTable_validateRuntimeField_tagsRuntimeExist() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                jsonKeys.forEach((k, v) -> {
                    if (k.startsWith("tags")) {
                        String tagName = null;
                        System.out.println("tag = " + v+ "_" + message.get(v)+"_runtime");
                        if(event_ts_timestamp2.equalsIgnoreCase(message.get("event_ts"))) {
                            tagName = v + "_false_runtime";
                        }else if(event_ts_timestamp1.equalsIgnoreCase(message.get("event_ts"))) {
                            tagName = v + "_true_runtime";
                        }
                        softAssert.assertTrue(message.keySet().contains(tagName), "Expected tag- " + tagName + " is not found in the event_norm_log table entry with log_uuid: " + message
                                .get("log_uuid"));
                    }
                });
            }
        } else {
            softAssert.assertTrue(false, "Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 8
    @Test
    public void testNormalization_EventNormLogTable_validateTagField_tagIsNonNumericalAndIsLatest() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                jsonKeys.forEach((k, v) -> {
                    if (k.startsWith("tags")) {
                        System.out.println("tag = " + v + ", value = " + message.get(v));
                        softAssert.assertTrue(message.get(v) instanceof String,
                                "Expected value is non-numerical for tag " + v + " in the event_norm_log table entry with log_uuid:  " + message.get("log_uuid"));

                    }
                });

                softAssert.assertTrue(curTagValue.equalsIgnoreCase(message.get("tagsfanCmd")),
                            "Expected value for tag- tagsfanCmd is true but instead it is" + message.get("tagsfanCmd")
                                    + ". The entry is in the event_norm_log table with log_uuid:  " + message.get("log_uuid"));

                softAssert.assertTrue(curTagValue.equalsIgnoreCase(message.get("tagsfanSensor")),
                            "Expected value for tag- tagsfanSensor is true but instead it is" + message.get("tagsfanSensor")
                                    + ". The entry is in the event_norm_log table with log_uuid:  " + message.get("log_uuid"));

                softAssert.assertTrue(curTagValue.equalsIgnoreCase(message.get("tagsfaultStatus")),
                            "Expected value for tag- tagsfaultStatus is true but instead it is" + message.get("tagsfaultStatus")
                                    + ". The entry is in the event_norm_log table with log_uuid:  " + message.get("log_uuid"));
            }
        } else {
            softAssert.assertTrue(false, "Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 9
    @Test
    public void testNormalization_EventNormLogTable_validateResrcUidField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                softAssert.assertTrue(assetSourceKey.equals(message.get("resrc_uid")),
                        "resrc_uid tag not matching the assetSourceKey that was requested for. Expected value: "+ assetSourceKey + ", but received value: " + message.get("resrc_uid")+ " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }


    //test 10
    @Test
    public void testNormalization_EventNormLogTable_validateMultipleResrcUidField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getMultipleNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                if(event_ts_timestamp1.equalsIgnoreCase(message.get("event_ts")) || event_ts_timestamp2.equalsIgnoreCase(message.get("event_ts"))) {
                    softAssert.assertTrue(assetSourceKey.equals(message.get("resrc_uid")),
                            "resrc_uid tag not matching the assetSourceKey that was requested for. Expected value: " + assetSourceKey + ", but received value: " + message.get("resrc_uid") + "in the event_log table entry with log_uuid:  " + message.get("log_uuid"));
                }
                else if(event_ts_timestamp3.equalsIgnoreCase(message.get("event_ts"))) {
                    softAssert.assertTrue(assetSourceKey2.equals(message.get("resrc_uid")),
                            "resrc_uid tag not matching the assetSourceKey that was requested for. Expected value: " + assetSourceKey2 + ", but received value: " + message.get("resrc_uid") + "in the event_log table entry with log_uuid:  " + message.get("log_uuid"));
                }
                else{
                    softAssert.assertTrue(false, "no matching resrc_uid found");
                }
            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 11
    @Test
    public void testNormalization_EventNormLogTable_validateTimeBucketField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                softAssert.assertTrue(isTimeBucketFor15Min(message.get("time_bucket")), "The time_bucket field is not in expected format. It should be in yyyymmddhhmm format." + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));
            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 12
    @Test
    public void testNormalization_EventNormLogTable_validateEnterpriseUidField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                softAssert.assertTrue(enterpriseSourceKeyProp.equals(message.get("enterprise_uid")),
                        "enterprise_uid tag not matching the enterpriseSourceKey that was requested for. Expected value: "+ enterpriseSourceKeyProp + ", but received value: " + message.get("enterprise_uid")
                                + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }


    //test 13
    @Test
    public void testNormalization_EventNormLogTable_validateSiteUidField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                softAssert.assertTrue(siteSourceKeyProp.equals(message.get("site_uid")),
                        "site_uid tag not matching the siteSourceKey that was requested for. Expected value: "+ siteSourceKeyProp + ", but received value: " + message.get("site_uid")
                                + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 14
    @Test
    public void testNormalization_EventNormLogTable_validateSegmentUidField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                String[] segmentArray = message.get("segment_uid").split(",");
                for (int i=0; i<segmentArray.length; i++){
                    segmentArray[i].trim();
                    softAssert.assertTrue(segmentSourceKeyProp.contains(segmentArray[i]),
                            "segment_uid tag not matching the segmentSourceKey that was requested for. Expected value: "+ segmentSourceKeyProp + ", but received value: " + message.get("segment_uid")
                                    + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));
                }
            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }


    //test 15
    @Test
    public void testNormalization_EventNormLogTable_validateMeasuresKWHField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {

                if(event_ts_timestamp1.equalsIgnoreCase(message.get("event_ts"))) {
                    softAssert.assertTrue("1.0".equals(String.valueOf(message.get("measureszoneElecMeterEnergySensor"))),
                            "Expected value for measureszoneElecMeterEnergySensor field is 1.0 but received value: " + String.valueOf(message.get("measureszoneElecMeterEnergySensor"))
                                    + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));
                }
                else if(event_ts_timestamp2.equalsIgnoreCase(message.get("event_ts"))) {
                    softAssert.assertTrue("2.0".equals(String.valueOf(message.get("measureszoneElecMeterEnergySensor"))),
                            "Expected value for measureszoneElecMeterEnergySensor field is 2.0 but received value: " + String.valueOf(message.get("measureszoneElecMeterEnergySensor"))
                                    + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));
                }
                else if(event_ts_timestamp3.equalsIgnoreCase(message.get("event_ts"))) {
                    softAssert.assertTrue("3.0".equals(String.valueOf(message.get("measureszoneElecMeterEnergySensor"))),
                            "Expected value for measureszoneElecMeterEnergySensor field is 3.0 but received value: " + String.valueOf(message.get("measureszoneElecMeterEnergySensor"))
                                    + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));
                }
            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }


    //test 16
    @Test
    public void testNormalization_EventNormLogTable_validateAssetTypeField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {
                softAssert.assertTrue(assetType.equals(message.get("asset_type")),
                        "asset_type tag not matching the assetType that was requested for. Expected value: "+ assetType + ", but received value: " + message.get("asset_type")
                                + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 17
    @Test
    public void testNormalization_EventNormLogTable_validateEvent_ts_tzField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {
                softAssert.assertTrue(timeZone.equals(message.get("event_ts_tz")),
                        "event_ts_tz tag not matching the timeZone that was requested for. Expected value: "+ timeZone + ", but received value: " + message.get("event_ts_tz")
                                + " In the event_log table entry with log_uuid:  " + message.get("log_uuid"));

            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }

    //test 18
    @Test
    public void testNormalization_EventNormLogTable_validateEvent_tsField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {
                String date = convert12HourTo24Hour(message.get("event_ts"));
                softAssert.assertTrue(isEventtsIsInCorrectBucket(date), "event_ts does not match the timestamp requested for. Entry in the event_norm_log table having log_uuid:  "+ message.get("log_uuid"));
            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }


    //test 19
    @Test
    public void testNormalization_EventNormLogTable_validateEventTypeField() throws URISyntaxException {
        SoftAssert softAssert = new SoftAssert();
        Response response = getNormLogResponse();
        JsonPath jsonPath = response.jsonPath();

        List<HashMap<String, String>> completeResultList = jsonPath.get();

        if (completeResultList != null && !completeResultList.isEmpty()) {
            for (HashMap<String, String> message : completeResultList) {
                softAssert.assertTrue(eventType.equalsIgnoreCase(message.get("event_type")), "event_type does not match the expected value " + eventType + ". Entry in the event_norm_log table having log_uuid:  "+ message.get("log_uuid"));
            }
        }
        else{
            softAssert.assertTrue(false,"Result list returned is empty");
        }
        softAssert.assertAll();
    }


}
