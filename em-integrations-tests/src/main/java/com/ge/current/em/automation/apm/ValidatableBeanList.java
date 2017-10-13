package com.ge.current.em.automation.apm;

import java.util.ArrayList;
import java.util.List;

public class ValidatableBeanList<T> {
	
	private List<T> list = new ArrayList<T>();

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
