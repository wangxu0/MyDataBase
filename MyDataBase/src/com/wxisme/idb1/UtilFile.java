package com.wxisme.idb1;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * 工具文件类，用来结构化存储数据的文件夹以及文件。
 * @author wxisme
 *
 */

public class UtilFile {
	private UserInfo userInfo;
	private String userPath;
	
	public UtilFile() {}
	
	public UtilFile(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.userPath = "src" + File.separator + "userfile" + File.separator
				+ userInfo.getUserName() + userInfo.getPassword();
	}
	//判断用户名和密码是否正确
	public boolean judgeUser() {
		//if()
		File userFile = new File(this.userPath);
		boolean flag = false;
		if(userFile.exists()) {
			PrintInfo.printContinue();
			flag = true;
		}
		else {
			PrintInfo.printUserWrong();
			flag = false;
//			System.out.println(this.userPath);
		}
		return flag;
	}
	
	public static void closeAll(Closeable ... io) {
		for(Closeable temp : io) {
			if(temp != null) {
				try {
					temp.close();
				} catch (IOException e) {
					System.out.println("文件关闭失败");
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void copy(File file1, File file2) {
		Reader r = null;
		try {
			r = new FileReader(file2);
		} catch (FileNotFoundException e) {
			System.out.println("创建字符输入流失败");
			e.printStackTrace();
		}
		Writer w = null;
		try {
			w = new FileWriter(file1);
		} catch (IOException e) {
			System.out.println("创建字符输出流失败");
			e.printStackTrace();
		}
		char[] cbuf = new char[10];
		int len = 0;
		try {
			while((len = r.read(cbuf)) != -1) {
				w.write(cbuf, 0, len);
			}
			w.flush();//强制刷出
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

	}
	
	
	
	
	
	
	

	public String getUserPath() {
		return userPath;
	}

	public void setUserPath(String userPath) {
		this.userPath = userPath;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	
	
	
	
}
