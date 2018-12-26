package ga.workshop.com.TicketHelper.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ga.workshop.com.TicketHelper.utils.GoogleUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Platform {
	
	private String id;
	private String name;
	private String url;
	
	public Platform(String input) throws Exception{
		super();
		if(!GoogleUtil.checkA_())
			throw new Exception("授權錯誤，有疑問請寄信至 harvey20072000@gmail.com");
		if(input == null || "".equals(input))
			throw new Exception("platform input should not be empty");
		if(input.startsWith("http")) {
			setUrl(input);
			for(Entry<String, String> entry : PLATFORM_ID_TO_URL_MAP.entrySet())
				if(entry.getValue().startsWith(input.substring(0, input.substring(10).indexOf("/")+10))) {
					setId(entry.getKey());
					setName(getId());
				}
		}else {
			setName(input);
			setId(PLATFORM_NAMES_TO_ID.get(preHandleInput(input)));
			setUrl(PLATFORM_ID_TO_URL_MAP.get(getId()));
		}
		if(getId() == null || getName() == null || getUrl() == null)
			throw new Exception("target platform is not supported yet");
		
	}
	
	private String preHandleInput(String input) {
		input = input.replace("系統", "");
		return input;
	}
	
	private static Map<String,String> PLATFORM_NAMES_TO_ID;
	private static Map<String,String> PLATFORM_ID_TO_URL_MAP;
	
	static {
		PLATFORM_NAMES_TO_ID = new HashMap<>();
		PLATFORM_NAMES_TO_ID.put("拓元", "tixcraft");
		PLATFORM_NAMES_TO_ID.put("tixcraft", "tixcraft");
		
		PLATFORM_ID_TO_URL_MAP = new HashMap<>();
		PLATFORM_ID_TO_URL_MAP.put("tixcraft", "https://tixcraft.com");
	}
	
}
