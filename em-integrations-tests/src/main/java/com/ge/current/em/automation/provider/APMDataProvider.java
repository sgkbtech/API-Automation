package com.ge.current.em.automation.provider;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;

public class APMDataProvider {

	private static final Log logger = LogFactory.getLog(APMDataProvider.class);
	private static final String APM_PARAM_PREFIX = "apm.";
	private static final String EMPTY = "";
	private static final String PAGE = "page";
	private static final String SORT = "sort";
	private static final String APM_SERVICE_URL = "apm_service_url";

	private static final Map<String, String> PARAM_MAP = new HashMap<String, String>() {
		{
			put("fetchParents", "false");
			put("fetchProperties", "false");
			put("maxDepth", "1");
			put("typeClass", "ASSET_TYPE");
			put("childrenType", "ASSET");
		}
	};

	@DataProvider(name = "listOfAPMServices")
	public static Object[][] getServicesList(ITestContext context) throws URISyntaxException {
		return urlFilteringWithParam(EMPTY, context);
	}

	@DataProvider(name = "listOfAPMServicesWithPage")
	public static Object[][] getServicesListWithPage(ITestContext context) throws URISyntaxException {
		return urlFilteringWithParam(PAGE, context);
	}

	@DataProvider(name = "listOfAPMServicesWithSort")
	public static Object[][] getServicesListWithSort(ITestContext context) throws URISyntaxException {
		return urlFilteringWithParam(SORT, context);
	}

	@DataProvider(name = "listOfChildrenEnterprise")
	public static Object[][] getListOfChildrenEnterprise(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.ROOT_ENTERPRISE_SOURCEKEY").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/enterprises/{enterpriseSourceKey}/children").replace("{enterpriseSourceKey}", param);
		return getListOfChildren(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "listOfChildrenRegion")
	public static Object[][] getListOfChildrenRegion(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.regionSourceKey").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/regions/{regionSourceKey}/children").replace("{regionSourceKey}", param);
		return getListOfChildren(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "listOfChildrenSite")
	public static Object[][] getListOfChildrenSite(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.siteSourceKey").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/sites/{siteSourceKey}/children").replace("{siteSourceKey}", param);
		return getListOfChildren(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "listOfChildrenSegment")
	public static Object[][] getListOfChildrenSegment(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.segmentSourceKey").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/segments/{segmentSourceKey}/children").replace("{segmentSourceKey}", param);
		return getListOfChildren(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "parentsOfEnterprise")
	public static Object[][] getParentsOfEnterprise(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.enterpriseSourceKey.forParentCheck").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/enterprises/{enterpriseSourceKey}/ancestors").replace("{enterpriseSourceKey}", param);
		return getParentDetails(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "parentsOfRegion")
	public static Object[][] getParentsOfRegion(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.regionSourceKey").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/regions/{regionSourceKey}?fetchParents=true").replace("{regionSourceKey}", param);
		return getParentDetails(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "parentsOfSite")
	public static Object[][] getParentsOfSite(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.siteSourceKey").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/sites/{siteSourceKey}/ancestors").replace("{siteSourceKey}", param);
		return getParentDetails(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "parentsOfSegment")
	public static Object[][] getParentsOfSegment(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.segmentSourceKey").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/segments/{segmentSourceKey}/ancestors").replace("{segmentSourceKey}", param);
		return getParentDetails(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "parentsOfAsset")
	public static Object[][] getParentsOfAsset(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.assetSourceKey").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/assets/{assetSourceKey}/ancestors").replace("{assetSourceKey}", param);
		return getParentDetails(uri, param,context.getAttribute("apm.Authorization").toString());
	}

	@DataProvider(name = "listTypes")
	public static Object[][] getTypes(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.ROOT_ENTERPRISE_SOURCEKEY").toString();
		Header header = new Header("ROOT_ENTERPRISE_SOURCEKEY", param);
		String uri = context.getAttribute(APM_SERVICE_URL).toString() + ("/types");
		Response response = RestAssured.given().contentType("application/json").header(header)
				.header("Authorization", context.getAttribute("apm.Authorization")).when().get(uri).then().extract()
				.response();
		if (response.statusCode() != HttpStatus.OK.value()) {
			throw new SkipException("types list is not available for root enterprise : " + param);
		}
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> content = jsonPath.get("content");
		Object[][] ret = new Object[content.size()][3];
		int index = 0;
		for (HashMap<String, Object> type : content) {
			ret[index][0] = param;
			ret[index][1] = type.get("sourceKey");
			ret[index][2] = type.get("type");
			index++;
		}
		return ret;
	}

	@DataProvider(name = "listAssets")
	public static Object[][] getAssets(ITestContext context) throws URISyntaxException {
		String param = context.getAttribute("apm.ROOT_ENTERPRISE_SOURCEKEY").toString();
		String uri = context.getAttribute(APM_SERVICE_URL).toString()
				+ ("/enterprises/{enterpriseSourceKey}/assets").replace("{enterpriseSourceKey}", param);
		Response response = RestAssured.given().log().all().contentType("application/json")
				.header("Authorization", context.getAttribute("apm.Authorization")).when().get(uri).then().extract()
				.response();
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> content = jsonPath.get("content");
		Object[][] ret = new Object[content.size()][1];
		int index = 0;
		for (HashMap<String, Object> asset : content) {
			ret[index][0] = asset.get("sourceKey");
			index++;
		}
		return ret;
	}

	@DataProvider(name = "listTags")
	public static Object[][] getTags(ITestContext context) throws URISyntaxException {
		Object[][] assets = getAssets(context);
		List<HashMap<String, Object>> content = null;
		String param = null;
		for (Object[] asset : assets) {
			param = asset[0].toString();
			String uri = context.getAttribute(APM_SERVICE_URL).toString()
					+ ("/assets/{assetSourceKey}/tags").replace("{assetSourceKey}", param);
			Response response = RestAssured.given().contentType("application/json")
					.header("Authorization", context.getAttribute("apm.Authorization")).when().get(uri).then().extract()
					.response();
			if (response.statusCode() != HttpStatus.OK.value()) {
				throw new SkipException("types list is not available for root enterprise : " + param);
			}
			JsonPath jsonPath = response.jsonPath();
			if (content == null) {
				content = jsonPath.get("content");
			} else {
				content.addAll(jsonPath.get("content"));
			}
			if (content.size() > 10) {
				break;
			}
		}
		Object[][] ret = new Object[content.size()][3];
		int index = 0;
		for (HashMap<String, Object> type : content) {
			ret[index][0] = param;
			ret[index][1] = type.get("sourceKey");
			ret[index][2] = type.get("dataType");
			index++;
		}
		return ret;
	}

	@DataProvider(name = "TagTypeJson")
	public static Object[][] getTagTypeJson() throws URISyntaxException {
		return getJson("Tag_Type");
	}

	/**
	 * 
	 * @return Enterprise JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "EnterpriseNegativejson")
	public static Object[][] getEnterpriseJson() throws URISyntaxException {
		return getJson("EnterpriseN");
	}

	/**
	 * 
	 * @return Enterprise Tag JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "EnterpriseTagNegativejson")
	public static Object[][] getEnterpriseTagJson() throws URISyntaxException {
		return getJson("EnterpriseTagN");
	}

	/**
	 * 
	 * @return Region JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "RegionNegativejson")
	public static Object[][] RegionJson() throws URISyntaxException {
		return getJson("RegionN");
	}

	/**
	 * 
	 * @return Assets JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "AssetNegativejson")
	public static Object[][] assetJson() throws URISyntaxException {
		return getJson("AssetsN");
	}

	/**
	 * 
	 * @return Location JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "locationNegativejson")
	public static Object[][] locationJson() throws URISyntaxException {
		return getJson("LocationN");
	}

	/**
	 * 
	 * @return Segment JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "SegmentNegativejson")
	public static Object[][] segmentJson() throws URISyntaxException {
		return getJson("SegmentN");
	}

	/**
	 * 
	 * @return Segment Tag JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "SegmentTagNegativejson")
	public static Object[][] segmentTagJson() throws URISyntaxException {
		return getJson("SegmentTagN");
	}

	/**
	 * 
	 * @return Sites JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "SitesNegativejson")
	public static Object[][] sitesJson() throws URISyntaxException {
		return getJson("SitesN");
	}

	/**
	 * 
	 * @return Sites Tag JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "SitesTagNegativejson")
	public static Object[][] sitesTagJson() throws URISyntaxException {
		return getJson("SiteTagN");
	}

	/**
	 * 
	 * @return Sites Gateway JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "SitesgatewayNegativejson")
	public static Object[][] sitesgatewayJson() throws URISyntaxException {
		return getJson("SiteGatewayN");
	}

	/**
	 * 
	 * @return EnterprisePut JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "EnterprisePutNegativejson")
	public static Object[][] getEnterprisePutJson() throws URISyntaxException {
		return getJson("EnterprisePutN");
	}

	/**
	 * 
	 * @return EnterpriseProperties JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "EnterprisePropertiesNegativejson")
	public static Object[][] getEnterprisePropertiesJson() throws URISyntaxException {
		return getJson("EnterprisePropertiesN");
	}

	/**
	 * 
	 * @return EnterpriseLocationNegativejson
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "EnterpriseLocationNegativejson")
	public static Object[][] getEnterpriseLocationJson() throws URISyntaxException {
		return getJson("EnterpriseLocationN");
	}

	/**
	 * 
	 * @return SitePropertiesNegativejson
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "SitePropertiesNegativejson")
	public static Object[][] getSitePropertiesNegativeJson() throws URISyntaxException {
		return getJson("SitePropertiesN");
	}

	/**
	 * 
	 * @return SiteLocationsNegativejson
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "SiteLocationsNegativejson")
	public static Object[][] getSiteLocationsNegativeJson() throws URISyntaxException {
		return getJson("SiteLocationsN");
	}

	/**
	 * 
	 * @return UpdateSite JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "UpdateSitejson")
	public static Object[][] getUpdateSiteJson() throws URISyntaxException {
		return getJson("UpdateSiteN");
	}

	/**
	 * 
	 * @return UpdateSegment JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "UpdateSegmentjson")
	public static Object[][] getSegmentpropertiesJson() throws URISyntaxException {
		return getJson("PutSegmentN");
	}

	/**
	 * 
	 * @return UpdateTypeProp JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "UpdateTypepropjson")
	public static Object[][] getTypepropertiesJson() throws URISyntaxException {
		return getJson("PutTypePropN");
	}

	/**
	 * 
	 * @return UpdateTagProp JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "UpdateTagpropjson")
	public static Object[][] getTagpropertiesJson() throws URISyntaxException {
		return getJson("PutTagPropN");
	}

	/**
	 * 
	 * @return UpdateAssetTag JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "UpdateAssetTagjson")
	public static Object[][] getAssetTagJson() throws URISyntaxException {
		return getJson("PutAssetTagN");
	}

	/**
	 * 
	 * @return UpdateAssetProp JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "UpdateAssetPropjson")
	public static Object[][] getAssetPropJson() throws URISyntaxException {
		return getJson("PutAssetPropN");
	}

	/**
	 * 
	 * @return UpdateAssetparents JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "UpdateAssetparentsjson")
	public static Object[][] getAssetparentsJson() throws URISyntaxException {
		return getJson("PutAssetParentN");
	}

	/**
	 * 
	 * @return Patch EnterprisesProp JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "Patchenterprisepropjson")
	public static Object[][] getPatchEntpropJson() throws URISyntaxException {
		return getJson("EnterprisePropPatchN");
	}

	/**
	 * 
	 * @return Patch SegmentProp JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "PatchSegmentPropJson")
	public static Object[][] getPatchSegmentPropJson() throws URISyntaxException {
		return getJson("PatchSegmentPropN");
	}

	/**
	 * 
	 * @return Patch SegmentChildren JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "PatchSegmentChildrenJson")
	public static Object[][] getPatchSegmentChildrenJson() throws URISyntaxException {
		return getJson("PatchSegmentChildrenN");
	}

	/**
	 * 
	 * @return Patch Siteproperties JSON files
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "PatchSitepropertiesJson")
	public static Object[][] getPatchSitepropertiesJson() throws URISyntaxException {
		return getJson("PatchSitePropN");
	}

	/**
	 * 
	 * @return PatchTagpropertiesJson
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "PatchTagpropertiesJson")
	public static Object[][] getPatchTagpropertiesJson() throws URISyntaxException {
		return getJson("PatchTagPropN");
	}

	/**
	 * 
	 * @return PatchTypepropertiesJson
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "PatchTypepropertiesJson")
	public static Object[][] getPatchTypeopertiesJson() throws URISyntaxException {
		return getJson("PatchTagPropN");
	}

	/**
	 * 
	 * @return PatchAssetpropertiesJson
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "PatchAssetpropertiesJson")
	public static Object[][] getPatchAssetopertiesJson() throws URISyntaxException {
		return getJson("PatchAssetPropN");
	}

	/**
	 * 
	 * @return PatchAssetparentsJson
	 * @throws URISyntaxException
	 */
	@DataProvider(name = "PatchAssetparentsJson")
	public static Object[][] getPatchAssetparentsJson() throws URISyntaxException {
		return getJson("PatchAssetParents");
	}

	private static Object[][] getParentDetails(String url, String param,String token) throws URISyntaxException {
		Response response = RestAssured.given().contentType("application/json").when()
				.header("Authorization", token).get(url).then().extract().response();
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> parents = jsonPath.getList("parents");
		assertFalse("Non root enterprise should have a parent. Enterprise key checked : " + param, parents.isEmpty());
		Object[][] ret = new Object[parents.size()][3];
		int index = 0;
		for (HashMap<String, Object> parent : parents) {
			ret[index][0] = param;
			ret[index][1] = parent.get("sourceKey");
			ret[index][2] = parent.get("classificationCode");
			index++;
		}
		return ret;
	}

	private static Object[][] getListOfChildren(String url, String param,String token) throws URISyntaxException {
		Response response = RestAssured.given().contentType("application/json").when()
				.header("Authorization", token).get(url).then().extract().response();
		JsonPath jsonPath = response.jsonPath();
		List<HashMap<String, Object>> content = jsonPath.getList("content");
		Object[][] ret = new Object[content.size()][3];
		int index = 0;
		for (HashMap<String, Object> child : content) {
			ret[index][0] = param;
			ret[index][1] = child.get("sourceKey");
			ret[index][2] = child.get("classificationCode");
			index++;
		}
		return ret;
	}

	private static Object[][] urlFilteringWithParam(String paramName, ITestContext context) throws URISyntaxException {
		logger.info("listOfAPMServicesWith -- " + paramName + " -- data provider");
		List<URI> uriList = new ArrayList<URI>();
		List<Map<String, String>> headersList = new ArrayList<Map<String, String>>();
		Swagger swagger = new SwaggerParser().read(context.getAttribute("em.api-docs").toString());
		Map<String, Path> paths = swagger.getPaths();
		Set<String> keys = paths.keySet();
		Iterator<String> itr = keys.iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			Path path = paths.get(key);
			Operation oprs = path.getGet();
			if (oprs != null) {
				// System.out.println(key);
				List<Parameter> params = oprs.getParameters();
				if (paramName.equals(EMPTY) || checkParamFor(params, paramName)) {
					uriList.add(getUri(key, params, context));
					headersList.add(getHeaders(params, context));
				}
			}
		}
		Object[][] data = new Object[uriList.size()][2];
		for (int i = 0; i < uriList.size(); i++) {
			data[i][0] = uriList.get(i);
			data[i][1] = headersList.get(i);
		}
		return data;

	}

	private static boolean checkParamFor(List<Parameter> params, String paramName) {
		for (Parameter param : params) {
			if (param.getName().equals(paramName)) {
				return true;
			}
		}
		return false;
	}

	private static Map<String, String> getHeaders(List<Parameter> params, ITestContext context) {
		Map<String, String> headers = new HashMap<String, String>();
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i).getRequired()
					&& params.get(i).getClass().equals(io.swagger.models.parameters.HeaderParameter.class)) {
				String name = params.get(i).getName();
				String value = PARAM_MAP.containsKey(name) ? PARAM_MAP.get(name)
						: context.getAttribute(APM_PARAM_PREFIX + name).toString();
				logger.info("name:value -- " + name + ":" + value);
				headers.put(name, value);
			}
		}
		return headers;
	}

	/**
	 * 
	 * @param filename
	 * @return returns list of json files with filter
	 */
	private static Object[][] getJson(String filename) {
		String[] filepath = { "src/main/resources/test-suite/data/apm", "src/main/resources/test-suite/data/apmpatch" };
		List<String> results = new ArrayList<String>();
		for (String path : filepath) {
			File[] files = new File(path).listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith(filename);
				}
			});
			for (File file : files) {
				if (file.isFile()) {
					String actualfilename = file.getName();
					// if (actualfilename.contains(filename)) {
					results.add(actualfilename);
					// }
				}
			}
		}
		Object[][] ret = new Object[results.size()][1];
		for (int i = 0; i < results.size(); i++) {
			ret[i][0] = results.get(i);
		}
		return ret;
	}

	private static URI getUri(String key, List<Parameter> params, ITestContext context) throws URISyntaxException {
		if (key.contains("{")) {
			String source = key.substring(key.indexOf("{") + 1, key.indexOf("}"));
			// System.out.println(source + ":" + getProperty(source));
			key = key.replace("{" + source + "}", context.getAttribute(APM_PARAM_PREFIX + source).toString());
			// System.out.println(key);
		}
		URI uri = new URI(context.getAttribute(APM_SERVICE_URL).toString() + key);
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i).getRequired()
					&& !params.get(i).getClass().equals(io.swagger.models.parameters.HeaderParameter.class)) {
				String query = uri.getQuery();
				String name = params.get(i).getName();
				Object attr = PARAM_MAP.containsKey(name) ? PARAM_MAP.get(name)
						: context.getAttribute(APM_PARAM_PREFIX + name).toString();
				if (attr == null) {
					logger.info("param null for uri : " + uri + "   : param  : " + params.get(i).getName());
				}
				String appendQuery = name + "=" + attr;
				if (query == null) {
					query = appendQuery;
				} else {
					query += "&" + appendQuery;
				}
				uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
			}
		}
		return uri;
	}

}
