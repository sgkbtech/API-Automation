package com.ge.current.em.automation.apm.dto;

import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.ValidatableBeanList;
import com.ge.current.em.persistenceapi.dto.MetaTypeDTO;

public class TypeHelper {
	private long unixTime = System.currentTimeMillis() % 1000000000L;

	public ValidatableBeanList<MetaTypeDTO> getTagType() {
		MetaTypeDTO typeDto = new MetaTypeDTO();
		typeDto.setName("Test_tag_type_" + unixTime);
		typeDto.setType(APMConstants.TAG_TYPE);
		typeDto.setUom("Units");
		typeDto.setDataType("NUMBER");
		ValidatableBeanList<MetaTypeDTO> vbl = new ValidatableBeanList<MetaTypeDTO>();
		vbl.getList().add(typeDto);
		return vbl;
	}
	
	public ValidatableBeanList<MetaTypeDTO> getAssetType(){
		MetaTypeDTO typeDto = new MetaTypeDTO();
		typeDto.setName("Test_asset_type_" + unixTime);
		typeDto.setType(APMConstants.ASSET_TYPE);
		ValidatableBeanList<MetaTypeDTO> vbl = new ValidatableBeanList<MetaTypeDTO>();
		vbl.getList().add(typeDto);
		return vbl;
	}
	
	public ValidatableBeanList<MetaTypeDTO> getSegmentType(){
		MetaTypeDTO typeDto = new MetaTypeDTO();
		typeDto.setName("Test_segment_type_" + unixTime);
		typeDto.setType(APMConstants.SEGMENT_TYPE);
		ValidatableBeanList<MetaTypeDTO> vbl = new ValidatableBeanList<MetaTypeDTO>();
		vbl.getList().add(typeDto);
		return vbl;
	}
	
	public ValidatableBeanList<MetaTypeDTO> getAssetModelType(String parentSourceKey){
		MetaTypeDTO typeDto = new MetaTypeDTO();
		typeDto.setName("Test_asset_model_type_" + unixTime);
		typeDto.setType(APMConstants.ASSET_MODEL_TYPE);
		typeDto.setParentSourceKey(parentSourceKey);
		ValidatableBeanList<MetaTypeDTO> vbl = new ValidatableBeanList<MetaTypeDTO>();
		vbl.getList().add(typeDto);
		return vbl;
	}
	
	public ValidatableBeanList<MetaTypeDTO> getTagTypeWithoutDataType() {
		MetaTypeDTO typeDto = new MetaTypeDTO();
		typeDto.setName("Test_tag_type_neg_" + unixTime);
		typeDto.setType(APMConstants.TAG_TYPE);
		typeDto.setUom("Units");
		ValidatableBeanList<MetaTypeDTO> vbl = new ValidatableBeanList<MetaTypeDTO>();
		vbl.getList().add(typeDto);
		return vbl;
	}
}
