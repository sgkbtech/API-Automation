package com.ge.current.em.automation.apm.dto;

import com.ge.current.em.automation.apm.ValidatableBeanList;
import com.ge.current.em.persistenceapi.dto.TagDTO;

public class TagHelper {

	private long unixTime = System.currentTimeMillis() % 1000000000L;

	public ValidatableBeanList<TagDTO> getTag(String typeSourceKey) {
		ValidatableBeanList<TagDTO> vbl = new ValidatableBeanList<TagDTO>();
		TagDTO tag = new TagDTO();
		tag.setName("test_tag_enterprise_" + unixTime);
		tag.setTagTypeSourceKey(typeSourceKey);
		tag.setDescription("C/F");
		tag.setUom("C/F");
		tag.setIsWritable(true);
		tag.setIsRequired(false);
		tag.setDataType("NUMBER");
		tag.setExternalRefId("Ext Ref Id for tag");
		tag.setResourceUri("www.uri.com");
		vbl.getList().add(tag);
		return vbl;
	}

}
