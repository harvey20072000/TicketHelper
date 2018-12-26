package ga.workshop.com.TicketHelper.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
import tw.com.geosat.util.WebUtil;

public class GoogleUtil{

	private final static Pattern REQUIREMENT_FORMAT = Pattern.compile("(?<index>\\d+)(?<operator>>=|<=|<|=|>)(?<value>([\\w\\d-]+))");
	
	private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
	
	public static boolean checkA_(){
		List<Map> list = queryGoogleDrive(A_L, "4="+A_T);
		if(list == null || list.size() == 0)
			return false;
		try {
			if(new Date().getTime() > SDF.parse(list.get(0).get(5).toString()).getTime())
				return false;
		} catch (Exception e) {
			System.err.println("checkA_ error : "+e.toString());
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
//		String workingDir = System.getProperty("user.dir");
//		System.out.println(workingDir);
		System.out.println(checkA_());
	}
	
	public static List<Map> queryGoogleDrive(String url, String question) {
		List result = new LinkedList();
		// Step.1 依URL取得Google drive資料，並parse資料成JSONObject
		JsonParser parser = new JsonParser();
		JsonObject root;
		try {
			root = parser.parse(WebUtil.sendGet(url)).getAsJsonObject();
			JsonObject feed = root.getAsJsonObject("feed");
			JsonArray entries = feed.getAsJsonArray("entry");
			
			int row = feed.getAsJsonObject("gs$rowCount").get("$t").getAsInt();
			int col = feed.getAsJsonObject("gs$colCount").get("$t").getAsInt();
			
			// Step.2 取得資料
			Map<Integer, Map> datas = new LinkedHashMap();
			Map<Integer, String> cols = null;
			JsonObject colData;
			int rowIndex=-1, tmpIndex;
			for (int i=0 ; i<entries.size() ; i++) {
				colData = entries.get(i).getAsJsonObject().getAsJsonObject("gs$cell");
				tmpIndex = colData.get("row").getAsInt();
				if (tmpIndex!=rowIndex) {
					if (cols!=null)
						datas.put(rowIndex, cols);
					cols = new LinkedHashMap();
					rowIndex = tmpIndex;
				}
				cols.put(colData.get("col").getAsInt(), colData.get("$t").getAsString().trim());
				
				// 最末筆
				if (i==entries.size()-1) {
					datas.put(rowIndex, cols);
				}
			}
			
			// Step.3 過濾符合條件資料
			Iterator keys = datas.keySet().iterator();
			Map data;
			while(keys.hasNext()) {	//每row都跑，col則過濾
				data = datas.get(keys.next());
				if (Util.isBlank(question) || dataFilter(data, question)) {
					result.add(data);
				}
			}
		} catch (Exception e) {
			Log.error("query google drive data fail, exception => " + e.toString());
		}
		return result;
	}
	
	private final static String A_T = "O6oc3NZnF-rbaJlSeQe-wUkKJ0GG08";
	
	private final static String A_L = 
			"https://spreadsheets.google.com/feeds/cells/1-rRPgGWWM3zEPNtZJCLJJBN8Zr-fpUlG9GutNQJhSNE/1/public/values?alt=json";
	
	private static String getIndex(JsonObject entry) {
		try {
			JsonObject colData = entry.getAsJsonObject("title");
			return colData.get("$t").getAsString().trim();
		} catch (Exception e) {
			Log.error("get col title fail, exception => " + e.toString());
		}
		return "";
	}
	
	private static String getValue(JsonObject entry) {
		try {
			JsonObject colData = entry.getAsJsonObject("gs$cell");
			return colData.get("$t").getAsString().trim();
		} catch (Exception e) {
			Log.error("get col value fail, exception => " + e.toString());
		}
		return "";
	}

	private static boolean dataFilter(Map data, String querySql) {
		String[] requirements = querySql.split(",");
		int index;
		String srcValue, checkValue, operator;
		Matcher matcher;
		Double srcNValue, checkNValue;
		for (String requirement : requirements) {
			matcher = REQUIREMENT_FORMAT.matcher(requirement);
			if (matcher.find()) {	// 符合條件規則
				index = Integer.parseInt(matcher.group("index"));
				if (data.containsKey(index)) {	// 指定位置是否有值
					try {
						srcValue = data.get(index).toString();
						checkValue = matcher.group("value");
						operator = matcher.group("operator");
						if ("=".equals(operator) &&				// 純文字
								srcValue.equals(checkValue)) {
							continue;
						} else {								// 數字
							srcNValue = Double.parseDouble(srcValue.replace(",", ""));
							checkNValue = Double.parseDouble(checkValue);
							if (("=".equals(operator) &&
									srcNValue == checkNValue) ||
								 (">".equals(operator) &&
										 srcNValue > checkNValue) ||
								 ("<".equals(operator) &&
										 srcNValue < checkNValue) ||
								 (">=".equals(operator) &&
										 srcNValue >= checkNValue) ||
								 ("<=".equals(operator) &&
										 srcNValue <= checkNValue)) {
								continue;
							}
						}
					} catch (Exception e) {
						Log.error("dataFilter fail exception => {}", e.toString());
						System.out.println("dataFilter fail exception => "+ e.toString());
						return false;
					}
				}
				return false;
			}
		}
		return true;
	}
}
