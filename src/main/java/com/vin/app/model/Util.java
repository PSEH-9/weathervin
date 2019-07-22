package com.vin.app.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
	
	public static String getDate(String input)throws Exception
	{
		Date date=new SimpleDateFormat("yyyy-MM-dd").parse(input);		
		Calendar cal = Calendar.getInstance();		
		cal.setTime(date);		
		int year = cal.get(Calendar.YEAR);		
		int month = cal.get(Calendar.MONTH)+1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dateStr1= day+""+month+""+year;
		return dateStr1;
	}
	
	public static Double avg(Double input1, Double input2)
	{
		double result = (input1.doubleValue()+input2.doubleValue())/2;		
		return result;
	}
	
}
