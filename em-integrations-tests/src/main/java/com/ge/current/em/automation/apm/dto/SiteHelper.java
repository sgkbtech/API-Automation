package com.ge.current.em.automation.apm.dto;

import java.util.ArrayList;
import java.util.List;

import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.ValidatableBeanList;
import com.ge.current.em.persistenceapi.dto.GatewayDTO;
import com.ge.current.em.persistenceapi.dto.GeoCoordinatesDTO;
import com.ge.current.em.persistenceapi.dto.LocalCoordinatesDTO;
import com.ge.current.em.persistenceapi.dto.LocationDTO;
import com.ge.current.em.persistenceapi.dto.PostalAddressDTO;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.SiteDTO;

public class SiteHelper {

	private long unixTime = System.currentTimeMillis() % 1000000000L;

	public ValidatableBeanList<SiteDTO> getSiteUnderEnterprise(String enterpriseSourceKey) {
		ValidatableBeanList<SiteDTO> vbl = new ValidatableBeanList<SiteDTO>();
		SiteDTO dto = new SiteDTO();
		unixTime = System.currentTimeMillis() % 1000000000L;
		dto.setName("Test_site_" + unixTime);
		dto.setParentClassificationCode(APMConstants.ENTERPRISE);
		dto.setParentSourceKey(enterpriseSourceKey);
		dto.setLocations(getLocationList());
		vbl.getList().add(dto);
		return vbl;
	}

	public ValidatableBeanList<SiteDTO> getSiteUnderSite(String siteSourceKey) {
		ValidatableBeanList<SiteDTO> vbl = new ValidatableBeanList<SiteDTO>();
		SiteDTO dto = new SiteDTO();
		dto.setName("Test_siteundersite_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SITE);
		dto.setParentSourceKey(siteSourceKey);
		dto.setLocations(getLocationList());
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<SiteDTO> getSiteUnderRegion(String regionSourceKey) {
		ValidatableBeanList<SiteDTO> vbl = new ValidatableBeanList<SiteDTO>();
		SiteDTO dto = new SiteDTO();
		dto.setName("Test_siteunderregion_" + unixTime);
		dto.setParentClassificationCode(APMConstants.REGION);
		dto.setParentSourceKey(regionSourceKey);
		dto.setLocations(getLocationList());
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<SiteDTO> getSiteUnderSegment(String segmentSourceKey) {
		ValidatableBeanList<SiteDTO> vbl = new ValidatableBeanList<SiteDTO>();
		SiteDTO dto = new SiteDTO();
		dto.setName("Test_siteundersegment_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SEGMENT);
		dto.setParentSourceKey(segmentSourceKey);
		dto.setLocations(getLocationList());
		vbl.getList().add(dto);
		return vbl;
	}
	
	
	public ValidatableBeanList<SiteDTO> getSiteWithoutParent() {
		ValidatableBeanList<SiteDTO> vbl = new ValidatableBeanList<SiteDTO>();
		SiteDTO dto = new SiteDTO();
		dto.setName("Test_siteundersegment_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SEGMENT);
		dto.setParentSourceKey("");
		dto.setLocations(getLocationList());
		vbl.getList().add(dto);
		return vbl;
	}


	public ValidatableBeanList<GatewayDTO> getGatewayAsset() {
		ValidatableBeanList<GatewayDTO> vbl = new ValidatableBeanList<GatewayDTO>();
		GatewayDTO gateway = new GatewayDTO();
		gateway.setName("Test Site Gateway"+ unixTime);
		gateway.setExternalRefId("JaceID "+ unixTime);
		gateway.setHostname("3.39.67.70");
		gateway.setPort(4141);
		gateway.setUsername("leslie");
		gateway.setSecret("password");
		gateway.setManufacturer("Trane");
		gateway.setCode("JACE");
		gateway.setSerialNumber("unixTime");
		gateway.setDescription("Jace 001 in the office");
		PropertiesDTO properties = new PropertiesDTO();
		Object[] value = { "aha" };
		properties.setId("TEST_PROP_GATEWAY");
		properties.setType("STRING");
		properties.setValue(value);
		List<PropertiesDTO> propList =  new ArrayList<PropertiesDTO>();
		propList.add(properties);
		gateway.setProperties(propList);
		vbl.getList().add(gateway);
		return vbl;
	}
	
	public List<LocationDTO> getLocationList(){
		List<LocationDTO> locationList = new ArrayList<LocationDTO>();
		LocationDTO locations = new LocationDTO();
		locations.setParentLocationSourceKey(null);
		locations.setAreaInSqft(1);
		locations.setName("ST_PG5");
		locations.setLocationType("PTHER");
		locations.setClassificationCode("OTHER");

		GeoCoordinatesDTO geocoordinates = new GeoCoordinatesDTO();
		geocoordinates.setLatitude(37.7673179);
		geocoordinates.setLongitude(-121.9584364);
		geocoordinates.setElevation((double) 450);
		geocoordinates.setTimezone("Pacific/Midway");
		
		PostalAddressDTO postalAddress = new PostalAddressDTO();
		postalAddress.setStreetLine1("2623 Camino Ramon");
		postalAddress.setStreetLine2("");
		postalAddress.setCountry("USA");
		postalAddress.setRegion("WEST");
		postalAddress.setState("CA");
		postalAddress.setCity("San Ramon");
		postalAddress.setPostalCode("94583");

		LocalCoordinatesDTO localcoordinates = new LocalCoordinatesDTO();
		localcoordinates.setHorizontalRange((double) 20);
		localcoordinates.setVerticalRange((double) 26);
		localcoordinates.setElevation((double) 20);
		localcoordinates.setAngle(83.12);
		
		locations.setGeoCoordinates(geocoordinates);
		locations.setPostalAddress(postalAddress);
		locations.setLocalCoordinates(localcoordinates);
		locationList.add(locations);
		return locationList;
	}

}
