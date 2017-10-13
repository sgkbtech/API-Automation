package com.ge.current.em;

import com.ge.current.em.utils.Constants;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertTrue;

import org.json.simple.parser.ParseException;
import org.testng.annotations.*;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class APMTest extends TestUtil {


    private static final Log logger = LogFactory.getLog(APMTest.class);

    APMJsonUtility jsonUtility = new APMJsonUtility();
    ArrayList<String> siteRepo = new ArrayList<String>();
    ArrayList<String> siteNameRepo = new ArrayList<String>();
    String enterpriseid;
    String enterprisename;
    String siteid;
    String sitename;
    Properties siteProperties=new Properties();
    HashMap<String,String> tagTypeMap = new HashMap<String,String>();
    Properties tagTypeProperties=new Properties();
    Properties assetTypeProperties=new Properties();
    HashMap<String,String> assetTypeMap = new HashMap<String,String>();
    HashMap<String,String> assetModelTypeMap = new HashMap<String,String>();
    HashMap<String,String> assetMap = new HashMap<String,String>();
    Properties assetModelTypeProperties = new Properties();
    Properties assetPostProperties = new Properties();
    Properties assetProperties = new Properties();

    //Test to check whether enterprise is created in APM


    @Test
    public void PostEnterprise() throws Throwable {
        String uri = getAPMURL() + "enterprises";

        String enterprisedetails = readEnterprise("src/main/resources/test-suite/data/payload/enterprise.json");
        //String enterprisedetails =null;
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(enterprisedetails).
                        expect().statusCode(201).
                        when().
                        post(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
        enterpriseid = ctx.read("$[0].sourceKey");
        enterprisename = ctx.read("$[0].name");


    }


    //Runs after PostEnterprise method .Check whether the Enterprise Name set is retrieved

    @Test(priority = 1,dependsOnMethods = "PostEnterprise")
    public void validateEnterprise() throws Throwable {
        String uri = getAPMURL() + "enterprises" + "/" + enterpriseid;
        //System.out.println(uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).body("name", equalTo(enterprisename)).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
    }
//Un implemented Test cases for reference

    @Test(priority = 2,dependsOnMethods = "validateEnterprise")
    public void PostSite() throws Throwable {
        String siteDet=readEnterprise("src/main/resources/test-suite/data/payload/site.json");
        logger.info("siteDet"+siteDet);
        Properties parentEnterpriseProperties = new Properties();
        parentEnterpriseProperties.setProperty("parentSourceKey", enterpriseid);
        String parentRequestBody = jsonUtility.readPayloadFile("src/main/resources/test-suite/data/payload/site.json", parentEnterpriseProperties);
        String uri = getAPMURL() + "sites";
        logger.info("parentRequestBody"+parentRequestBody);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(parentRequestBody).
                        expect().statusCode(201).
                        when().
                        post(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
        ReadContext ctx = JsonPath.parse(jsonString);
         siteid = ctx.read("$[0].sourceKey");
        sitename = ctx.read("$[0].name");
        logger.info("siteid:"+siteid);
        logger.info("sitename:"+sitename);
        siteProperties.setProperty("siteid",siteid);

    }

    @Test(dependsOnMethods = "PostSite")
    public void validateSite() throws Throwable {
        String uri = getAPMURL()+ "sites" + "/" + siteid;
        //System.out.println(uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).body("name", equalTo(sitename)).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString = response.getBody().asString();
        logger.info("Response Body:" + jsonString);
        logger.info("Response time:" + response.getTime());
    }



    //@Test(priority = 3,dependsOnMethods = "validateSite")
    @Test(dependsOnMethods ="validateSite" )
    public void PostTagTypeCall() throws Throwable {
       for(int i=1;i<=2;i++)
       {
           assertTrue(PostTagType(i),"TAG TYPE not created");
       }

       for(String key : tagTypeMap.keySet())
       {
           logger.info("Tag TYPE :"+key+" "+":Value:"+ tagTypeMap.get(key));
       }
        setTagTypeProperties();

    }

    @Test(dependsOnMethods = "PostTagTypeCall")
    public void PostAssetTypeCall() throws Throwable {
        for(int i=1;i<=2;i++)
        {
            assertTrue(PostAssetTypeTag(i),"ASSET TYPE not Created");
        }

        for(String key : assetTypeMap.keySet())
        {
            logger.info("ASSET TYPE Key:"+key+" "+":Value:"+assetTypeMap.get(key));
        }

        setAssetTypeProperties();

    }

    @Test(dependsOnMethods = "PostAssetTypeCall")
    public void PostAssetModelTypeCall() throws Throwable {
        for(int i=1;i<=2;i++)
        {
            assertTrue(PostAssetModelTypeTag(i),"ASSET MODEL TYPE not created");
        }

        for(String key : assetModelTypeMap.keySet())
        {
            logger.info("ASSET MODEL TYPE Key:"+key+" "+":Value:"+assetModelTypeMap.get(key));
        }

        setAssetModelTypeProperties();

    }

    @Test(dependsOnMethods = "PostAssetModelTypeCall")
    public void PostAssetCall() throws Throwable {
        setPropertiesforPostAsset();
        logger.info("assetPostProperties:->size:->"+assetPostProperties.size());
        assetPostProperties.list(System.out);
        for(int i=1;i<=2;i++)
        {
            assertTrue(PostAsset(i),"ASSET not Created");
        }

        for(String key : assetMap.keySet())
        {
            logger.info("ASSET Key:"+key+" "+":Value:"+assetMap.get(key));
        }

        setAssetProperties();

    }

    @AfterTest
    public void AfterAlltests() throws Throwable
    {
        deleteEnterprise(enterpriseid);
        for(String key : tagTypeMap.keySet()) {
            deleteType(tagTypeMap.get(key));
        }
            for(String key : assetModelTypeMap.keySet()) {
                deleteType(assetModelTypeMap.get(key));
            }
                for(String key : assetTypeMap.keySet()) {
                    deleteType(assetTypeMap.get(key));

        }
    }

    private void deleteType(String typeId) {
        String uri = getAPMURL() + "types"+"/"+typeId;
        logger.info("uri:->"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        when().
                        delete(uri).
                        then().extract().response();
        logger.info("Respponse Code TYPE:"+response.statusCode());
        assertTrue((response.statusCode()==200),"Type not deleted");
    }

    private void deleteEnterprise(String passedEnterpriseid) {

        String uri = getAPMURL() + "enterprises"+"/"+passedEnterpriseid;
        logger.info("uri:->"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        when().
                        delete(uri).
                        then().extract().response();
        assertTrue((response.statusCode()==200),"Enterprise not deleted");


    }

    public boolean PostTagType(int i) throws Throwable {
        List<String> tagid = new ArrayList<String>();
        List<String> tagname = new ArrayList<String>();
        String path ="src/main/resources/test-suite/data/payload/tagType_"+i+".json";
        String tagDet=readEnterprise(path);
        logger.info("tagDet"+tagDet);
        String uri = getAPMURL() + "types";
        logger.info("parentRequestBody:Tag->"+tagDet);
        logger.info("uri:->"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(tagDet).
                        when().
                        post(uri).
                        then().extract().response();
        if(response.statusCode()==201) {
            String jsonString = response.getBody().asString();
            logger.info("Response Body:" + jsonString);
            logger.info("Response time:" + response.getTime());
            ReadContext ctx = JsonPath.parse(jsonString);
            tagid = ctx.read("$[*].sourceKey");
            tagname = ctx.read("$[*].name");
            for (int j = 0; j < tagid.size(); j++) {
                tagTypeMap.put(tagname.get(j), tagid.get(j));
            }
            return true;
        }
        else
            return false;



    }

    public boolean PostAssetTypeTag(int i) throws Throwable {
        List<String> assettypeid = new ArrayList<String>();
        List<String> assettypename = new ArrayList<String>();
        String path ="src/main/resources/test-suite/data/payload/assetType_"+i+".json";
        String assetTypeDet = jsonUtility.readPayloadFile(path, tagTypeProperties);
        logger.info("assetTypeDet"+assetTypeDet);
        String uri = getAPMURL() + "types";
        logger.info("parentRequestBody:->AssetType->"+assetTypeDet);
        logger.info("uri:->"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(assetTypeDet).
                        when().
                        post(uri).
                        then().extract().response();
        if(response.statusCode()==201) {
            String jsonString = response.getBody().asString();
            logger.info("Response Body:" + jsonString);
            logger.info("Response time:" + response.getTime());
            ReadContext ctx = JsonPath.parse(jsonString);
            assettypeid = ctx.read("$[*].sourceKey");
            assettypename = ctx.read("$[*].name");
            for (int j = 0; j < assettypeid.size(); j++) {
                assetTypeMap.put(assettypename.get(j), assettypeid.get(j));
            }
            return true;
        }
        else
            return false;



    }

    public boolean PostAssetModelTypeTag(int i) throws Throwable {
        List<String> assetModeltypeid = new ArrayList<String>();
        List<String> assetModeltypename = new ArrayList<String>();
        String path ="src/main/resources/test-suite/data/payload/assetModelType_"+i+".json";
        String assetModelTypeDet = jsonUtility.readPayloadFile(path, assetTypeProperties);
        logger.info("assetTypeDet"+assetModelTypeDet);
        String uri = getAPMURL() + "types";
        logger.info("parentRequestBody:->AssetModelType->"+assetModelTypeDet);
        logger.info("uri:->"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(assetModelTypeDet).
                        when().
                        post(uri).
                        then().extract().response();
        if(response.statusCode()==201) {
            String jsonString = response.getBody().asString();
            logger.info("Response Body:" + jsonString);
            logger.info("Response time:" + response.getTime());
            ReadContext ctx = JsonPath.parse(jsonString);
            assetModeltypeid = ctx.read("$[*].sourceKey");
            assetModeltypename = ctx.read("$[*].name");
            for (int j = 0; j < assetModeltypeid.size(); j++) {
                assetModelTypeMap.put(assetModeltypename.get(j), assetModeltypeid.get(j));
            }
            return true;
        }
        else
            return false;



    }
    public boolean PostAsset(int i) throws Throwable {
        String assetid =null ;
        String assetname =null ;
        String path ="src/main/resources/test-suite/data/payload/asset_"+i+".json";
        String assetDet = jsonUtility.readPayloadFile(path, assetPostProperties);
        logger.info("assetDet"+assetDet);
        String uri = getAPMURL()+ "assets";
        logger.info("parentRequestBody:Tag->"+assetDet);
        logger.info("uri:->"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(assetDet).
                        when().
                        post(uri).
                        then().extract().response();
        if(response.statusCode()==201) {
            String jsonString = response.getBody().asString();
            logger.info("Response Body:" + jsonString);
            logger.info("Response time:" + response.getTime());
            ReadContext ctx = JsonPath.parse(jsonString);
            assetid = ctx.read("$[0].identifier.sourceKey");
            if(postTagforAsset(assetid,i)==false)
                return false;
            else
                return true;

        }
        else
            return false;



    }

    private Boolean postTagforAsset(String assetid,int i) throws Throwable
    {
        List<String> assetTagid = new ArrayList<String>();
        List<String> assetTagName = new ArrayList<String>();
        String path ="src/main/resources/test-suite/data/payload/assetTag_"+i+".json";
        String assetTagDet = jsonUtility.readPayloadFile(path, tagTypeProperties);
        logger.info("assetTagDet"+assetTagDet);
        String uri = getAPMURL() + "assets"+"/"+assetid+"/"+"tags";
        logger.info("parentRequestBody:Tag->"+assetTagDet);
        logger.info("uri:->"+uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        body(assetTagDet).
                        when().
                        put(uri).
                        then().extract().response();
        if(response.statusCode()==201) {
            String jsonString = response.getBody().asString();
            logger.info("Response Body:" + jsonString);
            logger.info("Response time:" + response.getTime());
            return true;
        }
        else
            return false;
    }

    private void setTagTypeProperties() throws Exception {
        for(String key : tagTypeMap.keySet()) {
            tagTypeProperties.setProperty(key+"id", tagTypeMap.get(key));

        }
    }

    private void setAssetTypeProperties() {
        for(String key :assetTypeMap.keySet()) {
            assetTypeProperties.setProperty(key+"id", assetTypeMap.get(key));

        }
    }

    private void setAssetModelTypeProperties() {

        for(String key :assetModelTypeMap.keySet()) {
            assetModelTypeProperties.setProperty(key+"id", assetModelTypeMap.get(key));

        }
    }

    private void setPropertiesforPostAsset() throws Exception {
        assetPostProperties.putAll(assetModelTypeProperties);
        assetPostProperties.putAll(siteProperties);

    }

    private void setAssetProperties() throws Exception {
        for(String key :assetMap.keySet()) {
            assetProperties.setProperty(key+"id", assetMap.get(key));

        }
    }


/*
	@Test
	 public void getEnterprise() throws Throwable{
	 	String uri=super.apmurl+"enterprises";
		//System.out.println(uri);
		Response response =
				RestAssured.given().
						contentType("application/json").
						expect().statusCode(200).
						when().
						get(uri).
						then().extract().response();
		String jsonString =response.getBody().asString();
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<String> enterprises  =ctx.read("$.content[*].sourceKey");
        ArrayList<String> enterprisesNames  =ctx.read("$.content[*].name");
        PrintWriter pw = new PrintWriter(new File("/Users/502645575/Desktop/Enterprises.csv"));

        StringBuilder sb = new StringBuilder();
        for(int i=0;i<enterprises.size();i++)
        {
            logger.info("Enterprise id:"+enterprises.get(i));
            logger.info("Enterprise name:"+enterprisesNames.get(i));
            sb.append(enterprises.get(i));
            sb.append(',');
            sb.append(enterprisesNames.get(i));
            sb.append(',');
            sb.append('\n');
            //getSiteGivenEnterprise(enterprises.get(i),enterprisesNames.get(i));

        }
        getSiteGivenEnterprise("ENTERPRISE_c23f46d8-5ee8-303e-9ef0-f194a681fe6d","PerfEnt100");
        pw.write(sb.toString());
        pw.close();
        //logger.info("Enterprises.size():"+enterprises.size());
		logger.info("Response time:"+response.getTime());

	 }



	@Test
	public void getSiteGivenEnterprise(String enterpriseid,String enterprisename) throws Throwable{
		ArrayList<String> siteid =new ArrayList<String>();
		ArrayList<String> sitename =new ArrayList<String>();
		String uri=super.apmurl+"enterprises"+"/"+enterpriseid+"/"+"sites";
		//System.out.println(uri);
		Response response =
				RestAssured.given().
						contentType("application/json").
                        param("size",5000).
						expect().statusCode(200).
						when().
						get(uri).
						then().extract().response();
		String jsonString =response.getBody().asString();
		ReadContext ctx = JsonPath.parse(jsonString);
		siteid = ctx.read("$.content[*].sourceKey");
		siteRepo.addAll(siteid);
		sitename = ctx.read("$.content[*].name");
		siteNameRepo.addAll(sitename);
        PrintWriter pw = new PrintWriter(new File("/Users/502645575/Desktop/"+enterprisename+":Site.csv"));
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<siteid.size();i++)
        {
            logger.info("Site id:"+siteid.get(i));
            logger.info("sitename:"+sitename.get(i));
            sb.append(siteid.get(i));
            sb.append(',');
            sb.append(sitename.get(i));
            sb.append(',');
            sb.append('\n');
            getAssetGivenSite(siteid.get(i),sitename.get(i));

        }
        pw.write(sb.toString());
        pw.close();
		//logger.info("Response Body:"+jsonString);
		logger.info("Response time:"+response.getTime());

	}

    @Test
    public void getAssetGivenSite(String siteid,String sitename) throws Throwable{

        String uri=super.apmurl+"sites"+"/"+siteid+"/"+"assets";
        System.out.println(uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString =response.getBody().asString();
        ReadContext ctx = JsonPath.parse(jsonString);
        ArrayList<String> assetid = ctx.read("$.content[*].sourceKey");
        PrintWriter pw = new PrintWriter(new File("/Users/502645575/Desktop/"+sitename+":Asset.csv"));
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<assetid.size();i++)
        {
            sb.append(siteid);
            sb.append(',');
            sb.append(assetid.get(i));
            sb.append(',');
            sb.append('\n');

        }
        pw.write(sb.toString());
        pw.close();
      //  logger.info("Response Body:"+jsonString);
        logger.info("Response time:"+response.getTime());
    }
/*
	@Test(dependsOnMethods = { "getSiteGivenEnterprise" } ,dataProvider = "site-details", dataProviderClass = APMTest.class)
	public void validateSiteDetails(String siteid,String sitename) throws Throwable{

		String uri=super.apmurl+"sites"+"/"+siteid;
		System.out.println(uri);
		Response response =
				RestAssured.given().
						contentType("application/json").
							expect().statusCode(200).body("name", equalTo(sitename)).
						when().
						get(uri).
						then().extract().response();
		String jsonString =response.getBody().asString();
		logger.info("Response Body:"+jsonString);
		logger.info("Response time:"+response.getTime());
	}


    @Test(dependsOnMethods = { "validateSiteDetails" } ,dataProvider = "site-details", dataProviderClass = APMTest.class)
    public void getSegmentGivenSite(String siteid,String sitename) throws Throwable{

        String uri=super.apmurl+"sites"+"/"+siteid+"/"+"children";
        System.out.println(uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString =response.getBody().asString();
        logger.info("Response Body:"+jsonString);
        logger.info("Response time:"+response.getTime());
    }



    @Test(dependsOnMethods = { "validateSiteDetails" } ,dataProvider = "site-details", dataProviderClass = APMTest.class)
    public void getAssetGivenSite(String siteid,String sitename) throws Throwable{

        String uri=super.apmurl+"sites"+"/"+siteid+"/"+"assets";
        System.out.println(uri);
        Response response =
                RestAssured.given().
                        contentType("application/json").
                        expect().statusCode(200).
                        when().
                        get(uri).
                        then().extract().response();
        String jsonString =response.getBody().asString();
        logger.info("Response Body:"+jsonString);
        logger.info("Response time:"+response.getTime());
    }

    */

/*

    @DataProvider(name = "site-details")
	public Object[][] getAllSiteDetails()
	{
		Object[][] data = new Object[siteRepo.size()][2];
		for (int i=0;i<siteRepo.size();i++)
		{
			data[i][0]=(Object)siteRepo.get(i);
			data[i][1]=(Object)siteNameRepo.get(i);
		}
		return data;
	}
	*/

    public String readEnterprise(String path) throws IOException, ParseException {

        String enterprisejsonFile = path;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(new File(enterprisejsonFile)));
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);



        }
        return sb.toString();

    }

    public String getAPMURL() {
        return getProperty(Constants.APM_URL);
    }
}


