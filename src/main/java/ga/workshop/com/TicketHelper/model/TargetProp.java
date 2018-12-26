package ga.workshop.com.TicketHelper.model;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import ga.workshop.com.TicketHelper.utils.Const;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetProp {
	// TODO 授權
	
	private Platform platform;
	private Date startTime;
	private String targetDateTime;
	private String targetAreaFloor;
	private String targetAreaCode;
	private List<Integer> targetAreaPrices;
	private Integer targetNums;
	
	public TargetProp(Properties properties) throws Exception{
		super();
		this.platform = new Platform(properties.getProperty("ticket.platform"));
		this.startTime = Const.DATE_FORMAT_FULL.parse(properties.getProperty("active.time.start"));
		this.targetDateTime = properties.getProperty("target.date");
		// this.targetAreaFloor = properties.getProperty("target.area.floor");
		this.targetAreaCode = properties.getProperty("target.area.code");
		this.targetAreaPrices = new LinkedList<>();
		for (String price : properties.getProperty("target.area.price").split("[~-]"))
			if (getTargetAreaPrices().size() < 2 && !getTargetAreaPrices().contains(Integer.parseInt(price)))
				getTargetAreaPrices().add(Integer.parseInt(price));
		Collections.sort(getTargetAreaPrices());
		this.targetNums = Integer.parseInt(properties.getProperty("target.nums"));
	}
	
}
