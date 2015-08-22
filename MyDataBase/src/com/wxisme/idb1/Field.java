package com.wxisme.idb1;

import java.util.ArrayList;
import java.util.List;

/**
 * 字段类 用来结构化存储字段名及其值
 * @author 王旭   
 * @time 2015.7.21 23:29
 */
public class Field {
	private String name;
	private List<String> fieldList;
	private int num;
	
	public Field(String name) {
		this.name = name;
		num = -1;
		fieldList = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<String> fieldList) {
		this.fieldList = fieldList;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	

}
