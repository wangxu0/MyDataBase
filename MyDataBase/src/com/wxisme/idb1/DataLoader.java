package com.wxisme.idb1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataLoader {
	
	public static ArrayList<DataBase> dataBaseLoader(String userpath) {
		ArrayList<DataBase> dataBaseList = new ArrayList<>();
		File users = new File("src" + File.separator + "userfile" + File.separator + userpath);
		String[] dbFiles = users.list();
		if(dbFiles == null) {
			//System.out.println("Username is not exist or password is wrong!");
		}
		else {
			for(int i=0; i<dbFiles.length; i++) {
				dataBaseList.add(new DataBase(dbFiles[i]));
			}
		}
		return dataBaseList;
	}
	
	public static ArrayList<Table> tableLoader(String dbpath) {
		ArrayList<Table> tableList = new ArrayList<>();
		File usersTable = new File("src" + File.separator + "userfile" + File.separator + 
				UserInfo.user + File.separator + dbpath);
		String[] dbFiles = usersTable.list();
		for(int i=0; i<dbFiles.length; i++) {
			tableList.add(new Table(dbFiles[i].substring(0, dbFiles[i].length()-4)));
			//System.out.println(dbFiles[i]);
			//System.out.println(dbFiles[i].substring(0, dbFiles[i].length()-4));
		}
//		System.out.println(tableList.get(0).tableName);
		return tableList;
	}

	
	public static String tableHeadLine(String path) {
		String ans = null;
		BufferedReader br = null;
		try {
		    br = new BufferedReader(new FileReader(new File(path)));
			ans = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ans;
	}
}









