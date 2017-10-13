package com.ge.current.em.automation.apm.dto;

import java.util.ArrayList;
import java.util.List;

import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.ValidatableBeanList;
import com.ge.current.em.persistenceapi.dto.GeoCoordinatesDTO;
import com.ge.current.em.persistenceapi.dto.LocalCoordinatesDTO;
import com.ge.current.em.persistenceapi.dto.LocationDTO;
import com.ge.current.em.persistenceapi.dto.MetaEntityDTO;
import com.ge.current.em.persistenceapi.dto.MetaTypeDTO;
import com.ge.current.em.persistenceapi.dto.PostalAddressDTO;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.SegmentDTO;

public class SegmentHelper {

	private long unixTime = System.currentTimeMillis() % 1000000000L;

	public ValidatableBeanList<SegmentDTO> getSegmentUnderSite(String siteunderRegionUnderRegion, String SEGMENT_TYPE) {
		ValidatableBeanList<SegmentDTO> vbl = new ValidatableBeanList<SegmentDTO>();
		SegmentDTO dto = new SegmentDTO();
		dto.setName("Test_segmentundersite_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SITE);
		dto.setParentSourceKey(siteunderRegionUnderRegion);
		dto.setSegmentTypeSourceKey(SEGMENT_TYPE);
		dto.setDescription("Test_segment");

		MetaTypeDTO typeDto = new MetaTypeDTO();
		typeDto.setType(APMConstants.SEGMENT_TYPE);

		dto.setProperties(getProperties());
		vbl.getList().add(dto);
		return vbl;
	}

	public ValidatableBeanList<SegmentDTO> getSegmentUnderSegment(String SegmentunderSite, String SEGMENT_TYPE) {
		ValidatableBeanList<SegmentDTO> vbl = new ValidatableBeanList<SegmentDTO>();
		SegmentDTO dto = new SegmentDTO();
		dto.setParentClassificationCode(APMConstants.SEGMENT);
		dto.setParentSourceKey(SegmentunderSite);
		dto.setSegmentTypeSourceKey(SEGMENT_TYPE);
		dto.setName("Test_segmentundersegment_" + unixTime);
		dto.setDescription("Test_segment");

		List<PropertiesDTO> propList = new ArrayList<PropertiesDTO>();
		PropertiesDTO properties = new PropertiesDTO();
		Object[] value = { "5" };
		properties.setId("SegmentProp");
		properties.setType("INTEGER");
		properties.setValue(value);

		PropertiesDTO properties1 = new PropertiesDTO();
		Object[] value1 = { "5" };
		properties1.setId("IS_VISUAL");
		properties1.setType("INTEGER");
		properties1.setValue(value1);

		propList.add(properties);
		propList.add(properties1);
		dto.setProperties(propList);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<SegmentDTO> getSegmentWithoutParent( String SEGMENT_TYPE) {
		ValidatableBeanList<SegmentDTO> vbl = new ValidatableBeanList<SegmentDTO>();
		SegmentDTO dto = new SegmentDTO();
		dto.setName("Test_segmentundersite_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SITE);
		dto.setParentSourceKey("");
		dto.setSegmentTypeSourceKey(SEGMENT_TYPE);
		dto.setDescription("Test_segment");

		MetaTypeDTO typeDto = new MetaTypeDTO();
		typeDto.setType(APMConstants.SEGMENT_TYPE);

		dto.setProperties(getProperties());
		vbl.getList().add(dto);
		return vbl;
	}

	public List<PropertiesDTO> getProperties() {
		List<PropertiesDTO> propList = new ArrayList<PropertiesDTO>();

		PropertiesDTO properties = new PropertiesDTO();
		Object[] value = { "5" };
		properties.setId("SegmentProp");
		properties.setType("INTEGER");
		properties.setValue(value);
		propList.add(properties);

		Object[] value1 = { "5" };
		properties.setId("IS_VISUAL");
		properties.setType("INTEGER");
		properties.setValue(value1);
		propList.add(properties);

		return propList;
	}

	public List<LocationDTO> getLocationList() {
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

	public ValidatableBeanList<PropertiesDTO> getNewPropertiesToLoad() {
		ValidatableBeanList<PropertiesDTO> vbl = new ValidatableBeanList<PropertiesDTO>();
		PropertiesDTO name = new PropertiesDTO();
		Object[] value = { "Test" };
		name.setId("firstname");
		name.setType("STRING");
		name.setValue(value);
		vbl.getList().add(name);
		PropertiesDTO age = new PropertiesDTO();
		Object[] ageValue = { "25" };
		age.setId("age");
		age.setType("NUMBER");
		age.setValue(ageValue);
		vbl.getList().add(age);
		return vbl;
	}
	
	public ValidatableBeanList<MetaEntityDTO> getAssetForPatch(String asset_id) {
		ValidatableBeanList<MetaEntityDTO> vbl = new ValidatableBeanList<MetaEntityDTO>();
		MetaEntityDTO dto = new MetaEntityDTO();
		dto.setSourceKey(asset_id);
		dto.setClassificationCode("ASSET");
		vbl.getList().add(dto);
		return vbl;
		}

}
