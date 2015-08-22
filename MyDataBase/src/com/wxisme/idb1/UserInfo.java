package com.wxisme.idb1;

import java.util.ArrayList;

/**
 * 用户相关的信息包括用户名、密码、用户拥有的数据库、数据库中的表。
 * @author wxisme
 *
 */
public class UserInfo {
	    //用户的信息
		private String userName;
		private String password;
		private ArrayList<DataBase> dataBase;
		private int dataBaseCount;
		public static String user;
		
		public UserInfo() {}
		
		

		public UserInfo(String userName, String password) {
			super();
			this.userName = userName;
			this.password = password;
			this.dataBase = DataLoader.dataBaseLoader(userName+password);
			user = userName+password;
			this.dataBaseCount = 0;
		}



		public UserInfo(String userName, String password, ArrayList<DataBase> dataBase) {
			super();
			this.userName = userName;
			this.password = password;
			this.dataBase = dataBase;
		}
		
		


		//获取某个数据库
		public DataBase getDataBase(String name) {
			DataBase ans = null;
			for(int i=0; i<this.dataBase.size(); i++) {
				if(dataBase.get(i).dataBaseName.equals(name)) {
					ans = dataBase.get(i);
					break;
				}
			}
			return ans;
		}
		
		
		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public ArrayList<DataBase> getDataBase() {
			return dataBase;
		}

		public void setDataBase(ArrayList<DataBase> dataBase) {
			this.dataBase = dataBase;
		}



		public int getDataBaseCount() {
			return dataBaseCount;
		}



		public void setDataBaseCount(int dataBaseCount) {
			this.dataBaseCount = dataBaseCount;
		}

		
		
		
}
