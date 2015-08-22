package com.wxisme.idb1;

import java.util.ArrayList;

public class PrintView {
	
	public static void printCmd() {
		System.out.println("help-");
		System.out.println("     -help user");
		System.out.println("     -help table");
		System.out.println("     -help database");
		System.out.println();
		System.out.println("show-");
		System.out.println("     -show cmd");
		System.out.println("new-");
		System.out.println("     -new username password");
		System.out.println();
		System.out.println("change-");
		System.out.println("     -change user");
		System.out.println();
		System.out.println("use-");
		System.out.println("     -use database databasename;");
		System.out.println("delete-");
		System.out.println("     -delete");
		System.out.println("      from tablename");
		System.out.println("      where col1=v1 and col2=v2 or ...;");
		System.out.println();
		System.out.println("update-");
		System.out.println("     -update tableName");
		System.out.println("      set col1=value1,col2=value2...;");
		System.out.println("     -update tableName");
		System.out.println("      set col1=value1,col2=value2...;");
		System.out.println("      where col1=value1 and col2=value2...;");
		System.out.println();
		System.out.println("select-");
		System.out.println("     -select *");
		System.out.println("      from tablename");
		System.out.println("      where col1=v1 and col2=v2 or...;");
		System.out.println("     -select col1,col3");
		System.out.println("      from tablename");
		System.out.println("      where col1=v1 and col2=v2 or...;");
		System.out.println();
		System.out.println("create-");
		System.out.println("     -create database databasename;");
		System.out.println("     -create table tableName (");
		System.out.println("      col1 type1,");
		System.out.println("      col2 type2,");
		System.out.println("      ...");
		System.out.println("      );");
		System.out.println();
		System.out.println("insert-");
		System.out.println("     -insert");
		System.out.println("      into tablename");
		System.out.println("      values(v1,v2,v3,...);");
		System.out.println();
		System.out.println("exit-");
		System.out.println("      -exit");
		
		 

	}
	
	public static void printTableHead(String[] head) {
		int colsSum = head.length/2;
		String[] headStr = new String[colsSum];
		int tableWidth = 0;
		for(int i=0,j=0; i<head.length; i+=2,j++) {
			headStr[j] = head[i];
		}
		tableWidth = colsSum*11 + 1;
		int colCnt = 0;
		//每一列占10个字符的位置（可以改成自适应大小不过太麻烦）
		for(int j=0; j<3; j++) {
			for(int i=0; i<tableWidth; i++) {
				if(j==0 || j==2) {
					if(i%11 == 0)
						System.out.print("+");
					else
						System.out.print("-");
				}
				if(j==1) {
					if(i==0) {
						System.out.print("|");
					}
					else if(i==1 || i==colCnt*12){
						System.out.printf("%-10s",headStr[colCnt++]);
						System.out.print("|");
					}
				}
				
			}
			if(j!=2)
				System.out.println();
		}
	}
	
	public static void printSubTableHead(String line) {
		String[] headStr = line.split(",");
		int colsSum = headStr.length;
		int tableWidth = colsSum*11 + 1;
		int colCnt = 0;
		//每一列占10个字符的位置（可以改成自适应大小不过太麻烦）
		for(int j=0; j<3; j++) {
			for(int i=0; i<tableWidth; i++) {
				if(j==0 || j==2) {
					if(i%11 == 0)
						System.out.print("+");
					else
						System.out.print("-");
				}
				if(j==1) {
					if(i==0) {
						System.out.print("|");
					}
					else if(i==1 || i==colCnt*12){
						System.out.printf("%-10s",headStr[colCnt++]);
						System.out.print("|");
					}
				}
				
			}
			if(j!=2)
				System.out.println();
		}
		
		
	}
	
	public static void printTableBody(String line) {
		String[] colsStr = line.split(" ");
		int colsSum = colsStr.length;
		int tableWidth = colsSum*11 + 1;
		int colCnt = 0;
		//每一列占10个字符的位置（可以改成自适应大小不过太麻烦）
		for(int j=0; j<3; j++) {
			for(int i=0; i<tableWidth; i++) {
				if(j==2) {
					if(i%11 == 0)
						System.out.print("+");
					else
						System.out.print("-");
				}
				if(j==1) {
					if(i==0) {
						System.out.print("|");
					}
					else if(i==1 || i==colCnt*12){
						System.out.printf("%-10s",colsStr[colCnt++]);
						System.out.print("|");
					}
				}
				
			}
			if(j!=2)
				System.out.println();
		}
		
	}
	
	public static void printSubTableBody(String line, ArrayList<Integer> colIndex) {
		String[] allcolsStr = line.split(" ");
		String[] colsStr = new String[colIndex.size()];
		for(int i=0; i<colsStr.length; i++) {
			colsStr[i] = allcolsStr[colIndex.get(i)];
		}
		int colsSum = colsStr.length;
		int tableWidth = colsSum*11 + 1;
		int colCnt = 0;
		//每一列占10个字符的位置（可以改成自适应大小不过太麻烦）
		for(int j=0; j<3; j++) {
			for(int i=0; i<tableWidth; i++) {
				if(j==2) {
					if(i%11 == 0)
						System.out.print("+");
					else
						System.out.print("-");
				}
				if(j==1) {
					if(i==0) {
						System.out.print("|");
					}
					else if(i==1 || i==colCnt*12){
						System.out.printf("%-10s",colsStr[colCnt++]);
						System.out.print("|");
					}
				}
				
			}
			if(j!=2)
				System.out.println();
		}
		
	}
	
	
	

}
