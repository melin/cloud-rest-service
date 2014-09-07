/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.iflytek.edu.cloud.frame.support.jdbc.CustomSQLUtil;

public class HttlTest {
	public static void main(String[] args) throws ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phyNeName", "sdfsad");
		 
		String sql = CustomSQLUtil.get("loadAlarBreData", parameters);
		System.out.println(sql);
	}
}
