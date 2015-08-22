package com.wxisme.idb1;

import java.util.ArrayList;

public class DataBase {
	public String dataBaseName;
	public ArrayList<Table> table;
	
	
	public DataBase(String dataBaseName, ArrayList<Table> table) {
		super();
		this.dataBaseName = dataBaseName;
		this.table = table;
	}

    public DataBase(String name) {
    	this.dataBaseName = name;
    	this.table = new ArrayList<Table>();
    }
	
	public DataBase() {
		this.table = new ArrayList<Table>();
	}

	//获取某个数据表
	public Table getTable(String name) {
		Table ans = null;
		for(int i=0; i<this.table.size(); i++) {
			if(table.get(i).tableName.equals(name)) {
				ans = table.get(i);
				break;
			}
		}
		return ans;
	}
	
	public String getDataBaseName() {
		return dataBaseName;
	}


	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}


	public ArrayList<Table> getTable() {
		return table;
	}


	public void setTable(ArrayList<Table> table) {
		this.table = table;
	}
}
