package com.wxisme.idb1;

import java.util.List;

/**
 * 打印给用户的提示信息，方便用户使用。
 * @author wxisme
 *
 */

public class PrintInfo {
	
	public static void printOne() {
		System.out.println("Please input username and password:");
	}
	
	public static void printUserWrong() {
		System.out.println("Username is not exist or password is wrong!");
	}
	
	public static void printSyntaxError() {
		System.out.println("You have an error in your SQL syntax.");
	}
	
	public static void printContinue() {
		System.out.println("Please continue...");
	}
	
	public static void printNoPath() {
		System.out.println("No DataBase was appointed.");
	}
	
	public static void printFileNotFind(String fileName) {
		System.out.println("The " + fileName + " is not exist!");
	}
	
	public static void printResult() {
		System.out.println("Queried result as follows:");
		
	}
	
	public static void printChanged(String operate, int cnt) {
		System.out.println(operate + " " + cnt + " " + "records.");
	}
	
	public static void printUnUnique(List<String> s) {
		StringBuilder str = new StringBuilder();
		str.append(s.get(0) + "(" + s.get(1) + ")");
		for(int i=2; i<s.size(); ) {
			str.append("," + s.get(i) + "(" + s.get(++i) + ")");
			i ++;
		}
		System.out.println("The input values " + str.toString() + " violated the constraints.");
	}
	
	
	
	
	
	
	
}