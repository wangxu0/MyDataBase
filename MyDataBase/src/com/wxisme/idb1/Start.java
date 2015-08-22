package com.wxisme.idb1;

public class Start {

	public static void main(String[] args) {
		while(true) {
			UtilFile uf;
			while(true) {
				PrintInfo.printOne();
				uf = new UtilFile(GetInput.getUser());
				if(uf.judgeUser())
					break;
			}
			
			AnalysisSQL asql = new AnalysisSQL(uf);
			while(true) {
				asql.setSQLstr(GetInput.getInput());
				if(asql.getSQLstr().equalsIgnoreCase("change user")) {
					break;
				}
				else if(asql.getSQLstr().equalsIgnoreCase("err")) {
					continue;
				}
				else {
					asql.executeSQL();
				}
			}
		}
		
	}

}
