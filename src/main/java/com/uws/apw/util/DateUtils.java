package com.uws.apw.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;

@SuppressWarnings("all")
public class DateUtils extends com.uws.core.util.DateUtil{

	public static java.util.Date toTime(String value)
	{
		 GregorianCalendar calendar = new GregorianCalendar();
	     if ((value != null) && (value.length() > 10)) {
	       String date = value.substring(0, 10);
	       String[] d = DataUtil.split(date, "-");
	       int year = DataUtil.strToInt(d[0]);
	       int month = DataUtil.strToInt(d[1]) - 1;
	       int day = DataUtil.strToInt(d[2]);
	       
	       String time = value.substring(date.length());
	       String [] timeArray = DataUtil.split(time.trim(), ":");
	       if(timeArray.length == 3){
	    	   
	    	   int hour = DataUtil.strToInt(DataUtil.isNotNull(timeArray[0])?timeArray[0]:"0");
	    	   int minute = DataUtil.strToInt(DataUtil.isNotNull(timeArray[1])?timeArray[1]:"0");
	    	   int second = DataUtil.strToInt(DataUtil.isNotNull(timeArray[2])?timeArray[2]:"0");
	    	   calendar = new GregorianCalendar(year, month, day,hour,minute,second);
	       }else{
	    	   
	    	   calendar = new GregorianCalendar(year, month, day);
	       }
	 
	       return new java.util.Date(calendar.getTimeInMillis());
	  }else if((value != null) && (value.length() == 10)){
		  	String date = value.substring(0, 10);
		    String[] d = DataUtil.split(date, "-");
		    int year = DataUtil.strToInt(d[0]);
		    int month = DataUtil.strToInt(d[1]) - 1;
		    int day = DataUtil.strToInt(d[2]);
		  
		   calendar = new GregorianCalendar(year, month, day);
		   return new java.util.Date(calendar.getTimeInMillis());
	  }
	     return null;
	}

	/**
	 * 获取当前日期所在的月初日期
	 * @param date
	 * @return
	 */
	public static Date getMonthStart(Date date) { 
        Calendar calendar = Calendar.getInstance(); 
        calendar.setTime(date); 
        int index = calendar.get(Calendar.DAY_OF_MONTH); 
        calendar.add(Calendar.DATE, (1 - index)); 
        return calendar.getTime(); 
    } 
	
	/**
	 * 获取当前日期的上个月的月初日期
	 * @param date
	 * @return
	 */
	public static String getLastMonthStart(Date date) { 
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date); 
		int curYear = calendar.get(Calendar.YEAR); 
		int lastMonth = calendar.get(Calendar.MONTH); 
		String month = (lastMonth<10)?"0"+lastMonth:lastMonth+"";
		
		return curYear+"-"+month+"-01"; 
	} 

	/**
	 * 获取当前日期所在的月末日期
	 * @param date
	 * @return
	 */
	public static Date getMonthEnd(Date date) { 
        Calendar calendar = Calendar.getInstance(); 
        calendar.setTime(date); 
        calendar.add(Calendar.MONTH, 1); 
        int index = calendar.get(Calendar.DAY_OF_MONTH); 
        calendar.add(Calendar.DATE, (-index)); 
        return calendar.getTime(); 
    } 

    /**
     * 获取当前日期的后一天
     * @param date
     * @return
     */
	public static Date getNext(Date date) { 
        Calendar calendar = Calendar.getInstance(); 
        calendar.setTime(date); 
        calendar.add(Calendar.DATE, 1); 
        return calendar.getTime(); 
    }
    
    /**
     * 获取当前日期的前一天
     * @param date
     * @return
     */
	public static Date getLast(Date date) { 
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.setTime(date); 
    	calendar.add(Calendar.DATE, -1); 
    	return calendar.getTime(); 
    }
    
	   /**
	    * 比较两个String类型的日期是否相等
	    * @param str1
	    * @param str2
	    * @return
	    */
	   public static boolean compareDate(String str1, String str2) {
		   String s1=DateUtil.formatDate(str1.trim());
		   String s2=DateUtil.formatDate(str2.trim());
		   return java.sql.Date.valueOf(s1).equals(java.sql.Date.valueOf(s2));
	   }
	   
		/**
		 * 获取日期范围内的所有日期
		 * @param curDate
		 * @param endDate
		 * @param rangeDateList
		 */
		public static void fomateRangeDateList(Date curDate, Date endDate,List<String> rangeDateList) {
			rangeDateList.add(getCustomDateString(curDate, "yyyy-MM-dd"));
			if(hasNext(curDate,endDate)){
				fomateRangeDateList(getNext(curDate),endDate,rangeDateList);
			}else{
				return;
			}
		}
	
		private static boolean hasNext(Date curDate, Date endDate) {
			boolean returnValue = false;
			Date nextDate = getNext(curDate);
			if(nextDate.before(endDate)){
				returnValue = true;
			}
			return returnValue;
		}
		
		/**
		 * 获取日期范围内的天数
		 * @param date1
		 * @param date2
		 * @return
		 */
		public static long getDiffDays(String startDate,String endDate){
			Date d1 =  java.sql.Date.valueOf(startDate);
			Date d2 =  java.sql.Date.valueOf(endDate);
			long diffs = d2.getTime()-d1.getTime();
			return diffs/1000/60/60/24;
		}

		public static void main(String[] args) {
			List<String> dateList = new ArrayList<String>();
			fomateRangeDateList(toTime("2015-05-01 00:00:00"),toTime("2015-05-13 19:41:00"),dateList);
/*			for(String date:dateList){
				System.out.println(date);
			}*/
			
			System.out.println(getLastMonthStart(toTime("2015-05-01")));
		}
		
		//常用属性  
		private static void param(){  
		      Date date = new Date();  
		      Calendar c = Calendar.getInstance();  
		      c.setTime(date);  
		     //Calendar.YEAR:日期中的年  
		      int year = c.get(Calendar.YEAR);  
		     //Calendar.MONTH:日期中的月，需要加1  
		     int mounth = c.get(Calendar.MONTH);  
		     //Calendar.DATE:日期中的日  
		     int day = c.get(Calendar.DATE);  
		     //Calendar.HOUR:日期中的小时(12小时制)  
		     int hour = c.get(Calendar.HOUR);  
		    //Calendar.HOUR_OF_DAY：24小时制  
		    int HOUR_OF_DAY = c.get(Calendar.HOUR_OF_DAY);  
		   //Calendar.MINUTE:日期中的分钟  
	        int minute = c.get(Calendar.MINUTE);  
		   //Calendar.SECOND:日期中的秒  
		    int second = c.get(Calendar.SECOND);  
		    System.err.println(year + "-" + mounth + "-" + day + " " + hour + ":" + minute + ":" + second);  
		    //Calendar.WEEK_OF_YEAR:当前年中星期数  
		    int WEEK_OF_YEAR = c.get(Calendar.WEEK_OF_YEAR);  
		   //Calendar.WEEK_OF_MONTH:当前月中星期数  
		    int WEEK_OF_MONTH = c.get(Calendar.WEEK_OF_MONTH);  
		   //Calendar.DAY_OF_YEAR:当前年中的第几天  
		    int DAY_OF_YEAR = c.get(Calendar.DAY_OF_YEAR);  
		   //Calendar.DAY_OF_MONTH:当前月中的第几天  
		    int DAY_OF_MONTH = c.get(Calendar.DAY_OF_MONTH);  
		    //Calendar.DAY_OF_WEEK:当前星期的第几天(星期天表示第一天，星期六表示第七天)  
		    int  DAY_OF_WEEK = c.get(Calendar.DAY_OF_WEEK);  
	       //Calendar.DAY_OF_WEEK_IN_MONTH:当前月中的第几个星期  
		    int DAY_OF_WEEK_IN_MONTH = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);  
		    try{  
				       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				       Date ampm = format.parse("2012-12-15 21:59:59");  
				       Calendar cc = Calendar.getInstance();  
				       cc.setTime(ampm);  
				       //AM_PM:HOUR 是在中午之前还是在中午之后,在中午12点之前返回0，在中午12点(包括12点)之后返回1  
				       int AM_PM = cc.get(Calendar.AM_PM);  
		     }catch(Exception e){}  
		}  


}
