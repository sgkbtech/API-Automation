package com.ge.current.em.automation.apm.dto;

import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.ValidatableBeanList;
import com.ge.current.em.persistenceapi.dto.RegionDTO;

public class RegionHelper {
	
	private long unixTime = System.currentTimeMillis() % 1000000000L;

	
	public ValidatableBeanList<RegionDTO> getRegionUnderRegion(String regionSourceKey) {
		ValidatableBeanList<RegionDTO> vbl = new ValidatableBeanList<RegionDTO>();
		RegionDTO dto = new RegionDTO();
		dto.setName("Test_regionunderregion_" + unixTime);
		dto.setParentClassificationCode(APMConstants.REGION);
		dto.setParentSourceKey(regionSourceKey);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<RegionDTO> getRegionUnderEnterprise(String enterpriseSourcekey) {
		ValidatableBeanList<RegionDTO> vbl = new ValidatableBeanList<RegionDTO>();
		RegionDTO dto = new RegionDTO();
		dto.setName("Test_regionunderregion_" + unixTime);
		dto.setParentClassificationCode(APMConstants.ENTERPRISE);
		dto.setParentSourceKey(enterpriseSourcekey);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<RegionDTO> getRegionUnderSite(String siteSourcekey) {
		ValidatableBeanList<RegionDTO> vbl = new ValidatableBeanList<RegionDTO>();
		RegionDTO dto = new RegionDTO();
		dto.setName("Test_regionundersite_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SITE);
		dto.setParentSourceKey(siteSourcekey);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<RegionDTO> getRegionUnderSegment(String segmentSourcekey) {
		ValidatableBeanList<RegionDTO> vbl = new ValidatableBeanList<RegionDTO>();
		RegionDTO dto = new RegionDTO();
		dto.setName("Test_regionundersite_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SEGMENT);
		dto.setParentSourceKey(segmentSourcekey);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<RegionDTO> getRegionWithoutParent() {
		ValidatableBeanList<RegionDTO> vbl = new ValidatableBeanList<RegionDTO>();
		RegionDTO dto = new RegionDTO();
		dto.setName("Test_regionundersite_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SEGMENT);
		dto.setParentSourceKey("");
		vbl.getList().add(dto);
		return vbl;
	}
	
	
	

}
