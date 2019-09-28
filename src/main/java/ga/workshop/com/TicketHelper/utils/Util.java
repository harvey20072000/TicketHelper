package ga.workshop.com.TicketHelper.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

public class Util {
	
	/**
	 * 檢核字串是否為空白
	 * @param checkValue
	 * @return boolean
	 */
	public static boolean isBlank(String checkValue) {
		if (checkValue == null || checkValue.isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 檔案是否存在檢核
	 * @param folderPath
	 * @param fileName
	 * @return boolean
	 */
	public static boolean checkFileExist(String folderPath, String fileName) {
		if (!Util.isBlank(folderPath)) {
			folderPath += File.separatorChar;
		}
		/*
		if (fileName.indexOf("|") > 0) {
			String[] fs = fileName.split("\\|");
			for (String name : fs) {
				if (!checkFileExist(folderPath, name)) {
					return false;
				}
			}
			return true;
		} else
		*/
		if ("*".equals(fileName)) {
			return new File(folderPath).exists();
		} else {
			return new File(
					Util.appendStr(folderPath, fileName)).exists();
		}
	}
	
	/**
	 * 取得日期
	 * @return String
	 */
	public static String getDate() {
		return getDate(Const.DATE_FORMAT_FULL.toPattern());
	}

	/**
	 * 取得日期
	 * @param format
	 * @return String
	 */
	public static String getDate(String format) {
		Date date = new Date();
		return formatDate(date, format);
	}
	
	/**
	 * 格式化日期資料
	 * @param date
	 * @param format
	 * @return String
	 */
	public static String formatDate(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return (df.format(date)).toString();
	}
	
	/**
	 * 日期資料轉換
	 * @param parseValue
	 * @param format
	 * @return Date
	 */
	public static Date parseDate(String parseValue, String format) {
		DateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(parseValue);
		} catch (Exception e) {
			Log.error("parseDate error, parseValue : [{}]", parseValue, e);
		}
		return null;
	}
	
	/**
	 * 檢查是否為日期
	 * @param cooperateFromDate
	 * @param format
	 * @return boolean
	 */
	public static boolean checkDate(String cooperateFromDate, String format) {
		SimpleDateFormat dFormat = new SimpleDateFormat(format);
		dFormat.setLenient(false);
		try {
			dFormat.parse(cooperateFromDate);
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	/**
	 * 取得之後間隔時間
	 * @param endTime
	 * @param format
	 * @return long
	 */
	public static long getAfterSeconds(String endTime, String format) {
		return getBetweenSeconds(getDate(format), endTime, format);
	}
	
	/**
	 * 取得之前間隔時間
	 * @param startTime
	 * @param format
	 * @return long
	 */
	public static long getBeforeSeconds(String startTime, String format) {
		return getBetweenSeconds(startTime, getDate(format), format);
	}
	
	/**
	 * 取得間隔時間
	 * @param startTime
	 * @param endTime
	 * @param format
	 * @return long
	 */
	public static long getBetweenSeconds(String startTime, String endTime, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date sdate = sdf.parse(startTime);
			Date edate = sdf.parse(endTime);
			return ((edate.getTime() - sdate.getTime())/1000);
		} catch (ParseException e) {
			Log.error(e.toString(), e);
		}
		return 0;
	}
	
	/**
	 * 字串分割
	 * @param value
	 * @param splitFlag
	 * @return String[]
	 */
	public static String[] split(String value, String splitFlag) {
		if (isBlank(value)) {
			return new String[]{};
		}
		return value.split(splitFlag);
	}
	
	/**
	 * 右補空白
	 * @param value
	 * @param n
	 * @return String
	 */
	public static String padSpaceStr(String value, int n) {
		if (isBlank(value)) {
			value = "";
		}
		
		StringBuilder result = new StringBuilder(value);
		
		for (int i=result.length() ; i<n ; i++) {
			result.append(" ");
		}
		return result.toString();
	}
	
	/**
	 * 取得IP
	 * @param address
	 * @return String
	 */
	public static String getIp(SocketAddress address) {
		String temp = address.toString();
		return temp.substring(0, temp.indexOf(":")).replace("/", "");
	}
	
	/**
	 * 取得Port
	 * @param address
	 * @return String
	 */
	public static String getPort(SocketAddress address) {
		String temp = address.toString();
		return temp.substring(temp.indexOf(":") + 1).replace("/", "");
	}
	
	/**
	 * 顯示資料
	 * @param obj
	 */
	public static void showData(Object obj) {
		showData(true, obj);
	}
	
	/**
	 * 顯示資料
	 * @param debugLevel
	 * @param obj
	 */
	public static void showData(boolean debugLevel, Object obj) {
		Class c = obj.getClass();
		String className = c.getSimpleName();
		Field[] fs = c.getFields();
		Object result = null;
		
		for (Field f : fs) {
			try {
				result = f.get(obj);
				show(debugLevel, className, f, result);
			} catch (Exception e) {
			}
		}
		
		fs = c.getDeclaredFields();
		String fidleName;
		for (Field f : fs) {
			try {
				fidleName = f.getName();
				fidleName = appendStr(
						fidleName.substring(0, 1).toUpperCase(), fidleName.substring(1));
				Method m = c.getMethod(appendStr("get", fidleName));
				if (m!=null) {
					result = m.invoke(obj);
					show(debugLevel, className, f, result);
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 顯示資料
	 * @param debugLevel
	 * @param className
	 * @param f
	 * @param result
	 */
	private static void show(boolean debugLevel, 
			String className, Field f, Object result) {
		if (result==null || result instanceof String || 
				result instanceof Long || result instanceof Integer || 
				result instanceof Date || result instanceof Boolean) {
			if (debugLevel) {
				Log.debug("{}.{}:{}", className, f.getName(), result);
			} else {
				Log.info("{}.{}:{}", className, f.getName(), result);
			}
		} else if (result instanceof String[] ||
				result instanceof Integer[]) {
			StringBuilder sb = new StringBuilder();
			for (Object o : (Object[])result) {
				sb.append(appendStr(o.toString(), ","));
			}
			if (debugLevel) {
				Log.debug("{}.{}:{}", className, f.getName(), 
						(sb.length()>0?sb.substring(0, sb.length()-1):""));
			} else {
				Log.info("{}.{}:{}", className, f.getName(), 
						(sb.length()>0?sb.substring(0, sb.length()-1):""));
			}
		} else {
			showData(debugLevel, result);
		}
	}
	
	/**
	 * 截字串
	 * @param value
	 * @param byteLength
	 * @return String
	 */
	public static String subString(String value, int byteLength) {
		int length = 0;
		
		if (value!=null) {
			while(value.getBytes().length>byteLength) {
				if (value.length()>byteLength) {
					value = value.substring(0, byteLength);
				} else {
					length = value.length();
					value = value.substring(0, length-1);
				}
			}
		}
		
		return value;
	}
	
	/**
	 * ip格式檢核
	 * @param ip
	 * @return boolean
	 */
	public static boolean ipValid(String ip) {
		if (isBlank(ip)) {
			return false;
		}
		
		String[] temp = ip.split("\\.");
		if (temp.length != 4) {
			return false;
		}
		
		int i;
		for (String t : temp) {
			try {
				i = Integer.parseInt(t);
				if (i<0 || i>254) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
	
	public synchronized static void setThreadName(String threadName) {
		Thread.currentThread().setName(threadName);
	}
	
	/**
	 * 合併字串
	 * @param strings
	 * @return
	 */
	public static String appendStr(Object... strings) {
		StringBuilder sb = new StringBuilder();
		for (Object s : strings) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static boolean isMatch(String value, String matchValue) {
        if (value!=null && 
                value.indexOf(matchValue)>=0) {
            return true;
        }
        return false;
    }
	
	private final static SimpleDateFormat in1 = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat in2 = new SimpleDateFormat("yyyyMMddHH");
	private final static SimpleDateFormat in3 = new SimpleDateFormat("yyyyMMddHHmm");
	private final static SimpleDateFormat in4 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static Date parseString2Date(String dateString) {
		if (dateString!=null) {
			try {
				switch (dateString.length()) {
					case 8:
						return in1.parse(dateString);
					case 10:
						return in2.parse(dateString);
					case 12:
						return in3.parse(dateString);
					case 14:
						return in4.parse(dateString);
				}
			} catch (Exception e) {
				Log.error("parse date[{}] fail, exception => {}", 
						dateString, e.toString());
			}
		}
		return null;
	}
	
	public static String safeGetString(Object input){
		if(input != null)
			return input.toString();
		return "";
	}
	
	/**
	 * 使執行續暫停
	 * @param milliSecond
	 */
	public static void currentThreadSleep(long milliSecond) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliSecond);
		} catch (Exception e) {}
	}
}
