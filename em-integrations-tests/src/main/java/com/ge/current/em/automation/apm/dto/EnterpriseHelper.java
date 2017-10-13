package com.ge.current.em.automation.apm.dto;

import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.ValidatableBeanList;
import com.ge.current.em.persistenceapi.dto.EnterpriseDTO;

public class EnterpriseHelper {

	private long unixTime = System.currentTimeMillis() % 1000000000L;

	public ValidatableBeanList<EnterpriseDTO> getRootEnterprise() {
		ValidatableBeanList<EnterpriseDTO> vbl = new ValidatableBeanList<EnterpriseDTO>();
		EnterpriseDTO dto = new EnterpriseDTO();
		dto.setName("Test_Root_Enterprise_" + unixTime);
		vbl.getList().add(dto);
		return vbl;
	}

	public ValidatableBeanList<EnterpriseDTO> getNonRootEnterprise(String ROOT_ENTERPRISE) {
		EnterpriseDTO dto = new EnterpriseDTO();
		ValidatableBeanList<EnterpriseDTO> vbl = new ValidatableBeanList<EnterpriseDTO>();
		dto.setName("Test_Enterprise_" + unixTime);
		dto.setParentClassificationCode(APMConstants.ENTERPRISE);
		dto.setParentSourceKey(ROOT_ENTERPRISE);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<EnterpriseDTO> getEnterpriseUnderSite(String sitesourcekey) {
		EnterpriseDTO dto = new EnterpriseDTO();
		ValidatableBeanList<EnterpriseDTO> vbl = new ValidatableBeanList<EnterpriseDTO>();
		dto.setName("Test_EnterpriseUnderSite_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SITE);
		dto.setParentSourceKey(sitesourcekey);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<EnterpriseDTO> getEnterpriseUnderSegment(String segmentsourcekey) {
		EnterpriseDTO dto = new EnterpriseDTO();
		ValidatableBeanList<EnterpriseDTO> vbl = new ValidatableBeanList<EnterpriseDTO>();
		dto.setName("Test_EnterpriseUnderSegment_" + unixTime);
		dto.setParentClassificationCode(APMConstants.SEGMENT);
		dto.setParentSourceKey(segmentsourcekey);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<EnterpriseDTO> getEnterpriseUnderRegion(String regionsourcekey) {
		EnterpriseDTO dto = new EnterpriseDTO();
		ValidatableBeanList<EnterpriseDTO> vbl = new ValidatableBeanList<EnterpriseDTO>();
		dto.setName("Test_EnterpriseUnderSegment_" + unixTime);
		dto.setParentClassificationCode(APMConstants.REGION);
		dto.setParentSourceKey(regionsourcekey);
		vbl.getList().add(dto);
		return vbl;
	}

}
