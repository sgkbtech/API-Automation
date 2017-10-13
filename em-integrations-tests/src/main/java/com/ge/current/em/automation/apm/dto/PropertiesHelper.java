package com.ge.current.em.automation.apm.dto;

import com.ge.current.em.automation.apm.ValidatableBeanList;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;

public class PropertiesHelper {

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

	public ValidatableBeanList<PropertiesDTO> getNewPropertiesToAdd() {
		ValidatableBeanList<PropertiesDTO> vbl = new ValidatableBeanList<PropertiesDTO>();
		PropertiesDTO name = new PropertiesDTO();
		Object[] value = { "add lastname" };
		name.setId("lastname");
		name.setType("STRING");
		name.setValue(value);
		vbl.getList().add(name);
		return vbl;
	}

	public ValidatableBeanList<PropertiesDTO> getPropertiesToUpdate() {
		ValidatableBeanList<PropertiesDTO> vbl = new ValidatableBeanList<PropertiesDTO>();
		PropertiesDTO name = new PropertiesDTO();
		Object[] value = { "update lastname" };
		name.setId("lastname");
		name.setType("STRING");
		name.setValue(value);
		vbl.getList().add(name);
		return vbl;
	}

}
