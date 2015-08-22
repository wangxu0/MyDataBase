package com.wxisme.idb1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析SQL语句
 * @author 王旭
 * @time 2015.7.22 00:20
 *
 */
public class AnalysisSQL {
	public UtilFile utilFile;
	public String SQLstr;
	public String path;
	public String[] sql;
	
	public AnalysisSQL() {}
	
	public AnalysisSQL(UtilFile utilFile) {
		this.utilFile = utilFile;
		this.path = "noPath";
	}
	
	public AnalysisSQL(UtilFile utilFile, String SQLstr) {
		this.utilFile = utilFile;
		this.SQLstr = SQLstr;
		this.sql = this.SQLstr.split(" ");
	}
	
	public void executeCreate() {
		if(this.sql.length < 3)
			PrintInfo.printSyntaxError();
		//create database databasename;
		else if(this.sql.length == 3) {
			if(this.sql[1].equalsIgnoreCase("database")) {
				String databaseName = this.sql[2].substring(0, this.sql[2].length()-1);
				File databaseFile = new File(this.utilFile.getUserPath()
						+ File.separator + databaseName);
				boolean flag = false;
				flag = databaseFile.mkdir();
				if(flag) {
					this.utilFile.getUserInfo().getDataBase().add(new DataBase(databaseName));
					System.out.println("Please continue...");
				}
				else {
					System.out.println("The database " + databaseName + " is already exsit.");
				}
			}
			else {
				PrintInfo.printSyntaxError();
			}
			
		}
		/**
		 create table tableName (
		 col1 type1,
		 col2 type2,
		 col3 type3
		 );
		 *  
		 */
		
		
		/*
		 * 要完善的地方：要建立数据字典、把元数据和数据分开存储、
		 */
		else {
			if(this.path.equals("noPath")) {
				PrintInfo.printNoPath();
			}
			else if(this.sql[1].equalsIgnoreCase("table")) {
				//唯一性约束检查及其语法检查
				/*
				create table tableName (
				col1 type1 primary key,
	      		col2 type2 not null,
				col3 type3 unique
			    );
			    create table tableName (
				col1 type1
	      		col2 type2
				col3 type3
			    );
				*/
				List<String> primaryList = new ArrayList<>();
				List<String> notNullList = new ArrayList<>();
				List<String> uniqueList = new ArrayList<>();
				
				StringBuilder constraintToNo = new StringBuilder();
				for(int i=0; i<4; i++) {
					constraintToNo.append(this.sql[i]);
					constraintToNo.append(" ");
				}
				for(int i=4; i<this.sql.length; ) {
					if(this.sql[i].equalsIgnoreCase("primary")) {
						primaryList.add(this.sql[i-2]);
						i += 2;
					}
					else if(this.sql[i].equalsIgnoreCase("not")) {
						notNullList.add(this.sql[i-2]);
						i += 2;
					}
					else if(this.sql[i].equalsIgnoreCase("unique")) {
						uniqueList.add(this.sql[i-2]);
						i ++;
					}
					else {
						constraintToNo.append(this.sql[i]);
						constraintToNo.append(" ");
						i ++;
					}
				}
				String[] tSql = constraintToNo.toString().split(" ");
				for(int i=5; i<tSql.length-3; i+=2) {
					if(!tSql[i].endsWith(",")) {
						tSql[i] = tSql[i] + ",";
					}
				}
				this.sql = tSql;
				
				
				
				
				boolean flag = false, syFlag = true;
				String tableName = sql[2];
				if(!sql[sql.length-1].equals(");") || !sql[3].equals("(") || sql[sql.length-2].endsWith(",")) {
					syFlag = false;
				}
				String[] tPath = this.path.split("\\\\");
				
				
				for(int i=4; i<this.sql.length-1; i+=2) {
					if(i < this.sql.length-3) {
						if(!sql[i+1].endsWith(",")) {
							syFlag = false;
							break;
						}
						else {
							//System.out.println(tPath[tPath.length-1]);
							this.utilFile.getUserInfo().getDataBase(tPath[tPath.length-1]).getTable().add(
									new Table(tableName));
							//还要把用户文件下的数据库文件在开启数据库的时候加载到database的List里不然会出现null point。
							this.utilFile.getUserInfo().getDataBase(tPath[tPath.length-1]).getTable(
									tableName).tableMap.put(sql[i], sql[i+1]);
							
						}
					}
					else {
						this.utilFile.getUserInfo().getDataBase(tPath[tPath.length-1]).getTable(
								tableName).tableMap.put(sql[this.sql.length-3], sql[this.sql.length-2]);
					}
				}
				
				if(syFlag) {
					
					//将约束条件写入文件 tablenamecheck.txt
					
					File checkFile = new File(this.path + File.separator + tableName+"check.txt");
					PrintWriter checkPw = null;
					try {
						//主键约束为第一行
						checkPw = new PrintWriter(checkFile);
						if(primaryList.size() == 0) {
							checkPw.println();
						}
						else {
							for(int i=0; i<primaryList.size(); i++) {
								checkPw.print(primaryList.get(i) + " ");
							}
							checkPw.println();
						}
						//非空约束为第二行
						if(notNullList.size() == 0) {
							checkPw.println();
						}
						else {
							for(int i=0; i<notNullList.size(); i++) {
								checkPw.print(notNullList.get(i) + " ");
							}
							checkPw.println();
						}
						//唯一约束为第三行
						if(uniqueList.size() == 0) {
							checkPw.println();
						}
						else {
							for(int i=0; i<uniqueList.size(); i++) {
								checkPw.print(uniqueList.get(i) + " ");
							}
							checkPw.println();
						}
						checkPw.flush();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} finally {
						checkPw.close();
					}
					
					
					
					
					File tableFile = new File(this.path + File.separator + tableName + ".txt");
					File dataDicFile = new File(this.path + File.separator + "dataDic.txt");
					
					try {
						flag = tableFile.createNewFile();
					} catch (IOException e) {
						System.out.println("Failed");
						e.printStackTrace();
					}
					if(!flag) {
						System.out.println("The table " + tableName + " is already exsit.");
					}
					else {
						//收集Table的信息做为headInfoLine并打印到Table的第一行。
						PrintWriter pw = null;
						PrintWriter dataPw = null;
						try {
							pw = new PrintWriter(new FileWriter(tableFile, true));
							dataPw = new PrintWriter(new FileWriter(dataDicFile, true));
							
							
						
						StringBuilder headLine = new StringBuilder();
						for(int i=4; i<this.sql.length-3; i+=2) {
							headLine.append(sql[i]);
							headLine.append(" ");
							headLine.append(sql[i+1]);
							headLine.append(" ");
						}
						
						headLine.append(this.sql[sql.length-3] + " " + this.sql[sql.length-2]);
						pw.println(headLine);
						
						//格式化元数据信息
						String[] mateData = headLine.toString().split(", ");
						
						
						for(int i=0; i<mateData.length; i++) {
							//System.out.println(mateData[i] + " *");
							
							//判断是否有主键约束
							for(int j=0; j<primaryList.size(); j++) {
								String[] ts = mateData[i].split(" ");
								//System.out.println(ts[0]);
								if(ts[0].equals(primaryList.get(j))) {
									//System.out.println("mate:" + mateData[i] + "   pri" + primaryList.get(j));
									mateData[i] = mateData[i] + " primary key";
									break;
								}
							}
							
							for(int j=0; j<notNullList.size(); j++) {
								String[] ts = mateData[i].split(" ");
								if(ts[0].equals(notNullList.get(j))) {
									mateData[i] = mateData[i] + " not null";
									break;
								}
							}
							
							for(int j=0; j<uniqueList.size(); j++) {
								String[] ts = mateData[i].split(" ");
								if(ts[0].equals(uniqueList.get(j))) {
									mateData[i] = mateData[i] + " unique";
									break;
								}
							}
						}
						
						//把元数据信息写入数据字典
						dataPw.println(tableName+": ");
						for(int i=0; i<mateData.length; i++) {
							dataPw.println(mateData[i]);
						}
						
						
						
					} catch (IOException e) {
						System.out.println("Failed");
						e.printStackTrace();
					} finally {
						pw.close();
						dataPw.close();
					}
						
						
						
						PrintInfo.printContinue();
					}
				}
				else {
					PrintInfo.printSyntaxError();
				}
				
				
			}
		}
		
		
		
	}
	
	//use database databasename;
	public void executeUse() {
		if(this.sql[1].equalsIgnoreCase("database") && 
				this.sql[2].endsWith(";") && this.sql.length==3) {
			String dataBaseName = this.sql[2].substring(0, this.sql[2].length()-1);
			this.setPath(this.utilFile.getUserPath() + File.separator + dataBaseName);
			this.utilFile.getUserInfo().getDataBase(dataBaseName).setTable(DataLoader.tableLoader(dataBaseName));
			PrintInfo.printContinue();
		}
		else {
			PrintInfo.printSyntaxError();
		}
	}
	
	/*
	 * insert
	 * into tablename
	 * values(v1,v2,v3,v4);
	 */
	
	//修改：现在需要插入的时候检查完整性  主键和非空约束都不能为空   唯一约束
	public void executeInsert() {
		boolean flag = false, syFlag = true;
		String tempPath = null;
		if(this.sql.length==4 && this.sql[1].equalsIgnoreCase("into")) {
			String tableName = this.sql[2];
			String[] tPath = this.path.split("\\\\");
			
			
			
			//进行唯一性检查
			//读取写有约束条件的文件
			File checkFile = new File(this.path + File.separator + tableName + "check.txt");
			BufferedReader checkReader = null;
			//读取原文件检查是否满足约束条件
			BufferedReader dataReader = null;
			//原有的唯一约束的字段数据   字段名-数据集合
			List<Field> fieldLists = new ArrayList<Field>();
			
			
			try {
				checkReader = new BufferedReader(new FileReader(checkFile));
				String primaryLine = checkReader.readLine();
				String notNullLine = checkReader.readLine();
				String uniqueLine = checkReader.readLine();
				
				String[] primary = primaryLine.split(" ");
				String[] unique = uniqueLine.split(" ");
				for(int i=0; i<primary.length; i++) {
					fieldLists.add(new Field(primary[i]));
				}
				for(int i=0; i<unique.length; i++) {
					fieldLists.add(new Field(unique[i]));
				}
				String dataPath = this.path + File.separator + tableName + ".txt";
				dataReader = new BufferedReader(new FileReader(new File(dataPath)));
				String[] dataHeadLine = dataReader.readLine().split(" ");
				Map<String, Integer> fieldMap = new HashMap<>();
				int fieldNum = 0;
				for(int i=0; i<dataHeadLine.length-1; i+=2) {
					fieldMap.put(dataHeadLine[i], fieldNum ++);
				}
				for(int i=0; i<fieldLists.size(); i++) {
					fieldLists.get(i).setNum(fieldMap.get(fieldLists.get(i).getName()));
				}
				
				//读取非空约束的字段对应的数据
				while(true) {
					String line = dataReader.readLine();
					if(line == null) {
						break;
					}
					String[] dataLine = line.split(" ");
					for(int i=0; i<fieldLists.size(); i++) {
						fieldLists.get(i).getFieldList().add(dataLine[fieldLists.get(i).getNum()]);
					}
				}
				
				
				
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					checkReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					dataReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			tempPath = this.path + File.separator + tableName + ".txt";
			File tableFile = new File(tempPath);
		if(tableFile.exists()) { 
			int colsCnt = this.utilFile.getUserInfo().getDataBase(tPath[tPath.length-1]).getTable(
					tableName).getColCount(tempPath);//计算列数   还要在开启数据库的时候读取！！
			
			String tValues = this.sql[3].substring(7, this.sql[3].length()-2);
			String[] values = tValues.split(",");
			
			
			//检查要插入的数据是否满足非空约束
			boolean isConstraint = true;
			List<String> ss = new ArrayList<String>();
			for(int i=0; i<fieldLists.size(); i++) {
	
				for(int j=0; j<fieldLists.get(i).getFieldList().size(); j++) {
					if(fieldLists.get(i).getFieldList().get(j).equals(values[fieldLists.get(i).getNum()])) {
						//不唯一
						isConstraint = false;
						ss.add(fieldLists.get(i).getName());
						ss.add(values[fieldLists.get(i).getNum()]);
						break;
					}
				}
			}
			
			//不唯一要做处理  提示错误信息
			if(!isConstraint) {
				PrintInfo.printUnUnique(ss);
			}
			
			else {
				if(this.sql[3].charAt(6)=='(' && this.sql[3].endsWith(");")
						&& values.length==colsCnt) {
					//往文件里写数据
					PrintWriter pw = null;
					try {
						pw = new PrintWriter(new FileWriter(tableFile, true));
						pw.print(values[0]);
						for(int i=1; i<values.length; i++) {
							pw.print(" " + values[i]);
						}
						pw.println();
					} catch (IOException e) {
						System.out.println("Failed");
						e.printStackTrace();
					}finally {
						pw.flush();
						pw.close();
					}
					//PrintInfo.printContinue();
					PrintInfo.printChanged("Inserted", 1);
					
					
				}
				else {
					PrintInfo.printSyntaxError();
				}
			}
			
			
			
		}
		else {
			PrintInfo.printFileNotFind(tableName);
		}
		}
		else {
			PrintInfo.printSyntaxError();
		}
		
	}
	
	/*
	 * select *
	 * from tablename
	 * where col1=v1 and col2=v2;
	 * 1.判断语法是否正确
	 * 2.把对应表中的全部数据取出来放到相应的容器中
	 * 3.根据where条件获取相应的数据并print到客户端
	 */
	public void executeSelect() {
		if(this.sql[1].equals("*") && this.sql[2].equalsIgnoreCase("from")
				&& this.sql[4].equalsIgnoreCase("where")) {
			String tableName = this.sql[3];
			String tempPath = this.path + File.separator + tableName + ".txt";
			File tableFile = new File(tempPath);
			//解析SQL语句中的where子句的条件并存放在Map中
			Map<String, String> sqlMap = new HashMap<>();
			int andNum = 0;
			for(int i=6; i<this.sql.length-1; i+=2) {
				if(this.sql[i].equalsIgnoreCase("and")) {
					andNum ++;
				}
			}
			//针对每一个条件做出处理
			for(int i=5; i<this.sql.length; i+=2) {
				String[] col_value = sql[i].split("=");
				if(i==this.sql.length-1) {
					sqlMap.put(col_value[0], col_value[1].substring(0,col_value[1].length()-1));
				}
				else {
					sqlMap.put(col_value[0], col_value[1]);
				}
			}
			
			
			
			Map<String, Integer> colsMap = new HashMap<>();
			
			if(tableFile.exists()) {
				ArrayList<String> lineList = new ArrayList<>();
				try {
					BufferedReader br = new BufferedReader(new FileReader(tableFile));
					//分析表的头信息
					String[] headInfo = br.readLine().split(" ");
					int colNum = 0;
					for(int i=0; i<headInfo.length; i+=2) {
						colsMap.put(headInfo[i], colNum++);
					}
					PrintInfo.printResult();
					PrintView.printTableHead(headInfo);
					while(true) {
						String line = br.readLine();
						if(line==null)
							break;
						lineList.add(line);
						//处理获取到的一个元组
						String[] row = line.split(" ");
						int correspond = 0;
			            for(String colName : sqlMap.keySet()) {
			            	int colsNumber = colsMap.get(colName);
			            	if(row[colsNumber].equals(sqlMap.get(colName))) {
			            		correspond ++;
			            	}
			            }
			            if(correspond >= andNum+1) {
			            	//打印表的视图
			            	//System.out.println(line);
			            	PrintView.printTableBody(line);
			            }
					}
					
					
					
				} catch (FileNotFoundException e) {
					System.out.println("File Not Found");
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println();
			}
			else {
				PrintInfo.printFileNotFind(tableName);
			}
			
		}
		
		/*
		 * select col1,col3
		 * from tablename
		 * where col1=v1 and col2=v2;
		 * 1.判断语法是否正确
		 * 2.把对应表中的全部数据取出来放到相应的容器中
		 * 3.根据where条件获取相应的数据并print到客户端
		 * 分析不到位容错度低
		 */
		else if(!this.sql[1].equals("*") && this.sql[2].equalsIgnoreCase("from")
				&& this.sql[4].equalsIgnoreCase("where")) {
			//SQL解析
			int fromIndex = 0;
			for(int i=0; i<this.sql.length; i++) {
				if(this.sql[i].equalsIgnoreCase("from")) {
					fromIndex = i;
				}
			}
			if(fromIndex == 0) {
				PrintInfo.printSyntaxError();
				return;
			}
			if(!this.sql[fromIndex+2].equalsIgnoreCase("where")) {
				PrintInfo.printSyntaxError();
				return;
			}
			
			
			String tableName = this.sql[fromIndex+1];
			String tempPath = this.path + File.separator + tableName + ".txt";
			File tableFile = new File(tempPath);
			//解析SQL语句中的where子句的条件并存放在Map中
			Map<String, String> sqlMap = new HashMap<>();
			int andNum = 0;
			for(int i=fromIndex+4; i<this.sql.length-1; i+=2) {
				if(this.sql[i].equalsIgnoreCase("and")) {
					andNum ++;
				}
			}
			//针对每一个条件做出处理
			for(int i=fromIndex+3; i<this.sql.length; i+=2) {
				String[] col_value = sql[i].split("=");
				if(i==this.sql.length-1) {
					sqlMap.put(col_value[0], col_value[1].substring(0,col_value[1].length()-1));
				}
				else {
					sqlMap.put(col_value[0], col_value[1]);
				}
			}
			
			
			
			Map<String, Integer> colsMap = new HashMap<>();
			BufferedReader br = null;
			if(tableFile.exists()) {
				ArrayList<String> lineList = new ArrayList<>();
				try {
					br = new BufferedReader(new FileReader(tableFile));
					//分析表的头信息
					String[] headInfo = br.readLine().split(" ");
					int colNum = 0;
					for(int i=0; i<headInfo.length; i+=2) {
						colsMap.put(headInfo[i], colNum++);
					}
					PrintInfo.printResult();
					//只打印所需要的表头---------------------------------------------------
					PrintView.printSubTableHead(this.sql[1]);
					while(true) {
						String line = br.readLine();
						if(line==null)
							break;
						lineList.add(line);
						//处理获取到的一个元组
						String[] row = line.split(" ");
						int correspond = 0;
			            for(String colName : sqlMap.keySet()) {
			            	int colsNumber = colsMap.get(colName);
			            	if(row[colsNumber].equals(sqlMap.get(colName))) {
			            		correspond ++;
			            	}
			            }
			            
			            //找出需要打印的列标
			            ArrayList<Integer> colIndex = new ArrayList<>();
			            String[] coName = this.sql[1].split(",");
			            for(int i=0; i<coName.length; i++) {
			            	colIndex.add(colsMap.get(coName[i]));
			            }
			            
			            if(correspond >= andNum+1) {
			            	//打印表的视图
			            	//只打印所需要的属性值----------------------------------------------
			            	PrintView.printSubTableBody(line,colIndex);
			            }
					}
					
					
					
				} catch (FileNotFoundException e) {
					System.out.println("File Not Found");
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					UtilFile.closeAll(br);
				}
				System.out.println();
			}
			else {
				PrintInfo.printFileNotFind(tableName);
			}
		}
		else {
			PrintInfo.printSyntaxError();
		}
	}
	
	/*
	 update tableName
	 set col1=value1,col2=value2
	 where col1=value1 and col2=value2;
	 1.判断是否有语法错误
	 2.提取set子句中的值
	 3.判断where条件
	 4.对满足要求的元组做出更新
	 */
	public void executeUpdate() {
		if(this.sql.length>4 && this.sql[2].equalsIgnoreCase("set") && this.sql[4].equalsIgnoreCase("where")) {
			String tableName = this.sql[1];
			String tempPath = this.path + File.separator + tableName + ".txt";
			File tableFile = new File(tempPath);
			if(!tableFile.exists()) {
				PrintInfo.printFileNotFind(tableName);
				return;
			}
			//分析set子句
			String[] set = this.sql[3].split(",");
			Map<String, String> setMap = new HashMap<>();
			for(int i=0; i<set.length; i++) {
				String[] k_v = set[i].split("=");
				setMap.put(k_v[0], k_v[1]);
			}
			//分析where子句
			
			//解析SQL语句中的where子句的条件并存放在Map中
			Map<String, String> sqlMap = new HashMap<>();
			int andNum = 0;
			for(int i=6; i<this.sql.length-1; i+=2) {
				if(this.sql[i].equalsIgnoreCase("and")) {
					andNum ++;
				}
			}
			//针对每一个条件做出处理
			for(int i=5; i<this.sql.length; i+=2) {
				String[] col_value = sql[i].split("=");
				if(i==this.sql.length-1) {
					sqlMap.put(col_value[0], col_value[1].substring(0,col_value[1].length()-1));
				}
				else {
					sqlMap.put(col_value[0], col_value[1]);
				}
			}
			BufferedReader br = null;
			PrintWriter temp_pw = null;
			File tempFile = null;
			try {
				tempFile = File.createTempFile("temp", ".txt", new File(this.path));//为何临时文件删除不了坑啊
				//File tempFile = new File(this.path + File.separator + "temp.txt");
				//对原表的读写
				br = new BufferedReader(new FileReader(tableFile));
				//PrintWriter pw = new PrintWriter(tableFile);
				//对临时文件的读写
				//BufferedReader temp_br = new BufferedReader(new FileReader(tempFile));
				temp_pw  = new PrintWriter(tempFile);
				//读取表头信息
				Map<String, Integer> colsMap = new HashMap<>();
				String headLine = br.readLine();
				//将头信息写入临时文件
				temp_pw.println(headLine);
				String[] headInfo = headLine.split(" ");
				int colNum = 0;
				for(int i=0; i<headInfo.length; i+=2) {
					colsMap.put(headInfo[i], colNum++);
				}
				//获取原表的每一个记录，如果不满足修改条件则直接写进临时文件中，否则修改后写进临时文件
				int changedCnt = 0;
				while(true) {
					String line = br.readLine();
					if(line == null) {
						break;
					}
					String[] row = line.split(" ");
					//判断当前元组是否满足where条件
					int correspond = 0;
		            for(String colName : sqlMap.keySet()) {
		            	int colsNumber = colsMap.get(colName);
		            	if(row[colsNumber].equals(sqlMap.get(colName))) {
		            		correspond ++;
		            	}
		            }
		            //满足条件修改后写入临时文件
		            if(correspond >= andNum+1) {
		            	changedCnt ++;
		            	for(String colName : setMap.keySet()) {
		            		int colsNumber = colsMap.get(colName);
		            		row[colsNumber] = setMap.get(colName);
		            	}
		            	temp_pw.print(row[0]);
		            	for(int i=1; i<row.length; i++) {
		            		temp_pw.print(" " + row[i]);
		            	}
		            	temp_pw.println();
		            }
		            //不满足条件直接写入临时文件
		            else {
		            	temp_pw.println(line);
		            }
				}
				temp_pw.flush();
				
				//处理完后，临时文件变成更新之后的结果表需要把临时文件中的内容copy到原表中去。
				tableFile.delete();
				tableFile.createNewFile();
				UtilFile.copy(tableFile, tempFile);
				//程序退出时把临时文件删除
				
				//PrintInfo.printContinue();
				PrintInfo.printChanged("Uptated", changedCnt);
				
 			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				UtilFile.closeAll(br,temp_pw);
				tempFile.delete();
			}
			
			
			
		}
		/*
		   update tableName
	       set col1=value1,col2=value2;
		 */
		else if(this.sql[2].equalsIgnoreCase("set") && this.sql.length==4) {
			String tableName = this.sql[1];
			String tempPath = this.path + File.separator + tableName + ".txt";
			File tableFile = new File(tempPath);
			if(!tableFile.exists()) {
				PrintInfo.printFileNotFind(tableName);
				return;
			}
			//分析set子句
			String[] set = this.sql[3].split(",");
			Map<String, String> setMap = new HashMap<>();
			for(int i=0; i<set.length; i++) {
				String[] k_v = set[i].split("=");
				if(i == set.length-1) {
					k_v[1] = k_v[1].substring(0, k_v[1].length()-1);
				}
				setMap.put(k_v[0], k_v[1]);
			}
			
			
			BufferedReader br = null;
			PrintWriter temp_pw = null;
			File tempFile = null;
			try {
				tempFile = File.createTempFile("temp", ".txt", new File(this.path));//为何临时文件删除不了坑啊
				//File tempFile = new File(this.path + File.separator + "temp.txt");
				//对原表的读写
				br = new BufferedReader(new FileReader(tableFile));
				//PrintWriter pw = new PrintWriter(tableFile);
				//对临时文件的读写
				//BufferedReader temp_br = new BufferedReader(new FileReader(tempFile));
				temp_pw  = new PrintWriter(tempFile);
				//读取表头信息
				Map<String, Integer> colsMap = new HashMap<>();
				String headLine = br.readLine();
				//将头信息写入临时文件
				temp_pw.println(headLine);
				String[] headInfo = headLine.split(" ");
				int colNum = 0;
				for(int i=0; i<headInfo.length; i+=2) {
					colsMap.put(headInfo[i], colNum++);
				}
				//获取原表的每一个记录，修改后写进临时文件
				int changedCnt = 0;
				while(true) {
					changedCnt ++;
					String line = br.readLine();
					if(line == null) {
						break;
					}
					String[] row = line.split(" ");
					
					for(String colName : setMap.keySet()) {
	            		int colsNumber = colsMap.get(colName);
	            		row[colsNumber] = setMap.get(colName);
	            	}
	            	temp_pw.print(row[0]);
	            	for(int i=1; i<row.length; i++) {
	            		temp_pw.print(" " + row[i]);
	            	}
	            	temp_pw.println();
		            
				}
				temp_pw.flush();
				
				//处理完后，临时文件变成更新之后的结果表需要把临时文件中的内容copy到原表中去。
				tableFile.delete();
				tableFile.createNewFile();
				UtilFile.copy(tableFile, tempFile);
				//程序退出时把临时文件删除
				
				//PrintInfo.printContinue();
				PrintInfo.printChanged("Uptated", changedCnt);
				
 			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				UtilFile.closeAll(br,temp_pw);
				tempFile.delete();
			}
		}
		
		
		else {
			PrintInfo.printSyntaxError();
		}
	}

	/*
	 delete
	 from tablename
	 where col1=v1 and col2=v2;
	*/
	public void executeDelete() {
		if(this.sql[1].equalsIgnoreCase("from") && this.sql[3].equalsIgnoreCase("where")) {
			String tableName = this.sql[2];
			String tempPath = this.path + File.separator + tableName + ".txt";
			File tableFile = new File(tempPath);
			if(!tableFile.exists()) {
				PrintInfo.printFileNotFind(tableName);
				return;
			}
			//分析where子句
			//解析SQL语句中的where子句的条件并存放在Map中
			Map<String, String> sqlMap = new HashMap<>();
			int andNum = 0;
			for(int i=5; i<this.sql.length-1; i+=2) {
				if(this.sql[i].equalsIgnoreCase("and")) {
					andNum ++;
				}
			}
			//针对每一个条件做出处理
			for(int i=4; i<this.sql.length; i+=2) {
				String[] col_value = sql[i].split("=");
				if(i==this.sql.length-1) {
					sqlMap.put(col_value[0], col_value[1].substring(0,col_value[1].length()-1));
				}
				else {
					sqlMap.put(col_value[0], col_value[1]);
				}
			}
			BufferedReader br = null;
			PrintWriter temp_pw = null;
			File tempFile = null;
			try {
				tempFile = File.createTempFile("temp", ".txt", new File(this.path));//为何临时文件删除不了坑啊
				//File tempFile = new File(this.path + File.separator + "temp.txt");
				//对原表的读写
				br = new BufferedReader(new FileReader(tableFile));
				//PrintWriter pw = new PrintWriter(tableFile);
				//对临时文件的读写
				//BufferedReader temp_br = new BufferedReader(new FileReader(tempFile));
				temp_pw  = new PrintWriter(tempFile);
				//读取表头信息
				Map<String, Integer> colsMap = new HashMap<>();
				String headLine = br.readLine();
				//将头信息写入临时文件
				temp_pw.println(headLine);
				String[] headInfo = headLine.split(" ");
				int colNum = 0;
				for(int i=0; i<headInfo.length; i+=2) {
					colsMap.put(headInfo[i], colNum++);
				}
				//获取原表的每一个记录，如果不满足修改条件则直接写进临时文件中，否则修改后写进临时文件
				int changedCnt = 0;
				while(true) {
					String line = br.readLine();
					if(line == null) {
						break;
					}
					String[] row = line.split(" ");
					//判断当前元组是否满足where条件
					int correspond = 0;
		            for(String colName : sqlMap.keySet()) {
		            	int colsNumber = colsMap.get(colName);
		            	if(row[colsNumber].equals(sqlMap.get(colName))) {
		            		correspond ++;
		            	}
		            }
		            //满足条件不写入临时文件
		            if(correspond >= andNum+1) {
		            	changedCnt ++;
		            }
		            //不满足条件直接写入临时文件
		            else {
		            	temp_pw.println(line);
		            }
				}
				temp_pw.flush();
				
				//处理完后，临时文件变成更新之后的结果表需要把临时文件中的内容copy到原表中去。
				tableFile.delete();
				tableFile.createNewFile();
				UtilFile.copy(tableFile, tempFile);
				//程序退出时把临时文件删除
				
				//PrintInfo.printContinue();
				PrintInfo.printChanged("Deleted", changedCnt);
				
 			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				UtilFile.closeAll(br,temp_pw);
				tempFile.delete();
			}
		}
	}
	
	public void executeHelp() {
		if(this.sql[1].equalsIgnoreCase("user")) {
			ArrayList<DataBase> db = utilFile.getUserInfo().getDataBase();
			System.out.println("The user's " + db.size() + " databases are as follows:");
			for(int i=0; i<db.size(); i++) {
				System.out.println(db.get(i).getDataBaseName());
			}
		}
		else if(this.sql[1].equalsIgnoreCase("database")) {
			String databaseName = this.sql[2];
			DataBase db = utilFile.getUserInfo().getDataBase(databaseName);
			if(db == null) {
				PrintInfo.printFileNotFind(databaseName);
				return;
			}
			ArrayList<Table> tb = db.getTable();
			List<String> sl = new ArrayList<>();
			
			for(int i=0; i<tb.size(); i++) {
				if(!tb.get(i).getTableName().endsWith("check")) {
					sl.add(tb.get(i).getTableName());
				}
			}
			System.out.println("The user's " + sl.size() + " table are as follows:");
			for(int i=0; i<sl.size(); i++) {
				System.out.println(sl.get(i));
			}
		}
		else if(this.sql[1].equalsIgnoreCase("table")) {
			//System.out.println("JJJ");
			String tableName = this.sql[2];
			String[] s = this.path.split("\\\\");
			String databaseName = s[s.length-1];
			DataBase db = utilFile.getUserInfo().getDataBase(databaseName);
			Table tb = db.getTable(tableName);
			if(db == null || tb==null) {
				PrintInfo.printFileNotFind(databaseName);
				return;
			}
			String head = tb.getHeadInfoLine();
			tb.getColCount(this.path+File.separator+tableName+".txt");
			
			
			
			File filee = new File(this.path+File.separator+"dataDic.txt");
			//System.out.println(filee.getPath());
			StringBuilder headInfo = new StringBuilder();
			BufferedReader bbr = null;
			String mate = null;
			//获取元数据信息
			try {
				bbr = new BufferedReader(new FileReader(filee));
				while(true) {
					mate = bbr.readLine();
					if(mate == null) {
						break;
					}
					if(mate.equals(tableName + ": ")) {
						headInfo.append(mate + "\n");
						//System.out.println(mate);
						while(true) {
							mate = bbr.readLine();
							if(mate == null) {
								break;
							}
							if(mate.endsWith(": ")) {
								break;
							}
							headInfo.append(mate + "\n");
						}
						break;
					}
				}
				
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					bbr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(headInfo.length() == 0) {
				System.out.println("The table " + tableName + " is null.");
				return;
			}
			
			System.out.println(headInfo);
			
			
			PrintInfo.printContinue();
		}
		else {
			PrintInfo.printSyntaxError();
		}
	}
	
	public void executeSQL() {
		if(this.sql[0].equalsIgnoreCase("create")) {
			this.executeCreate();
		}
		else if(this.sql[0].equalsIgnoreCase("use")) {
			this.executeUse();
		}
		else if(this.sql[0].equalsIgnoreCase("insert")) {
			this.executeInsert();
		}
		else if(this.sql[0].equalsIgnoreCase("select")) {
			this.executeSelect();
		}
		else if(this.sql[0].equalsIgnoreCase("update")) {
			this.executeUpdate();
		}
		else if(this.sql[0].equalsIgnoreCase("delete")) {
			this.executeDelete();
		}
		else if(this.sql[0].equalsIgnoreCase("help")) {
			this.executeHelp();
		}
		else if(this.sql[0].equalsIgnoreCase("show")) {
			if(this.sql[1].equalsIgnoreCase("cmd")) {
				PrintView.printCmd();
			}
			else {
				PrintInfo.printSyntaxError();
			}
		}
		else {
			PrintInfo.printSyntaxError();
		}
	}
	
	
	
	
	
	

	public UtilFile getUtilFile() {
		return utilFile;
	}

	public void setUtilFile(UtilFile utilFile) {
		this.utilFile = utilFile;
	}

	public String getSQLstr() {
		return SQLstr;
	}

	public void setSQLstr(String SQLstr) {
		this.SQLstr = SQLstr;
		this.sql = this.SQLstr.split(" ");
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
