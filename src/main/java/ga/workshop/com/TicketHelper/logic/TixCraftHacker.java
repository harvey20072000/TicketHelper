package ga.workshop.com.TicketHelper.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import ga.workshop.com.TicketHelper.model.TargetProp;
import ga.workshop.com.TicketHelper.utils.Log;

public class TixCraftHacker {
	public void hack(WebDriver driver,TargetProp targetProp) {
		// 選擇訂票網站
		// test => https://tixcraft.com/activity/detail/19_JimG
		driver.get(targetProp.getPlatform().getUrl());
		System.out.println(driver.getTitle());

		while (System.currentTimeMillis() < (targetProp.getStartTime().getTime()-20))
			try {
				TimeUnit.MILLISECONDS.sleep(20);
			} catch (Exception e) {}
		long logTime = System.currentTimeMillis();
		
		String currentUrl = driver.getCurrentUrl(),showId = currentUrl.substring(currentUrl.lastIndexOf("/")+1);
		List<WebElement> tempElements = null;
		
		// 等時間到就執行(點到有tag a出現才點) 10ms點一次
		List<WebElement> listInlineElements = null;
		for (int i = 0; i < 1000; i++)
			try {
				try {
					TimeUnit.MILLISECONDS.sleep(25);
				} catch (Exception e2) {}
				listInlineElements = driver.findElements(By.className("list-inline"));
				listInlineElements.get(listInlineElements.size() - 1).findElements(By.tagName("a")).get(0).click();
				break;
			} catch (Exception e) {}
		System.out.println("點擊立即購票完成");
		
		// 設定想要的場次時間(限一場)
		boolean hasDesiredProp = false;
		List<WebElement> trs = null;
		for (int i = 0; i < 1000; i++)
			try {
				try {
					TimeUnit.MILLISECONDS.sleep(25);
				} catch (Exception e2) {}
				trs = driver.findElement(By.id("gameList")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
				if(trs.size() > 0 && trs.get(0).findElements(By.tagName("td")).size() == 4) {
					break;
				}
			} catch (Exception e) {}
		System.out.println("選場次-tbody完成");
		
		List<WebElement> tempTrs = new LinkedList<>();
		for (int i = 0; i < 1000; i++)
			try {
				try {
					TimeUnit.MILLISECONDS.sleep(25);
				} catch (Exception e2) {}
				for (WebElement tr : trs) {
					if((tempElements = tr.findElements(By.tagName("td"))).get(3).findElements(By.tagName("input")).size() > 0) {
						tempTrs.add(tr);
						if (matchTargetDate(tempElements.get(0).getText(),targetProp.getTargetDateTime())) {
							tempElements.get(3).findElements(By.tagName("input")).get(0).click();
							hasDesiredProp = true;
							break;
						}
					}
				}
				if(hasDesiredProp)
					break;
			} catch (Exception e) {}
		if (!hasDesiredProp) {
			System.out.println("tempTrs size:"+tempTrs.size());
			if(!tempTrs.isEmpty())
				tempTrs.get(new Random().nextInt(tempTrs.size())).findElements(By.tagName("td")).get(3).findElements(By.tagName("input")).get(0).click();
			else {
				Log.info("Sorry, there's no show can be reserved.");
				return;
			}
		}
		System.out.println("選場次完成");
		
		if((currentUrl = driver.getCurrentUrl()).substring(currentUrl.indexOf(showId)).split("/").length == 2) {
			// 設定想要的區域(票價)
			hasDesiredProp = false;
			List<WebElement> areas = null;
			for (int i = 0; i < 1000; i++)
				try {
					try {
						TimeUnit.MILLISECONDS.sleep(25);
					} catch (Exception e2) {}
					areas = driver.findElements(By.className("zone")).get(0).findElements(By.tagName("ul"));
					if(areas.size() > 0) {
						break;
					}
				} catch (Exception e) {}
			/*
			for (WebElement area : areas) {
				if (matchTargetArea(area.getText(),targetProp) && !area.findElements(By.tagName("a")).isEmpty()) {
					area.findElement(By.tagName("a")).click();
					hasDesiredProp = true;
					break;
				}
			}
			*/
			WebElement desiredArea = getTargetAreaElement(areas, targetProp);
			if (desiredArea == null) {
				List<WebElement> tempAreas = new LinkedList<>();
				for(WebElement element : areas)
					if(element.findElement(By.tagName("a")) != null)
						tempAreas.add(element);
				if(!tempAreas.isEmpty())
					tempAreas.get(new Random().nextInt(tempAreas.size())).findElement(By.tagName("a")).click();
				else {
					Log.info("Sorry, there's no seat can be reserved.");
					return;
				}
			}else
				desiredArea.findElement(By.tagName("a")).click();
			System.out.println("選區域完成");
		}
		
		if((currentUrl = driver.getCurrentUrl()).substring(currentUrl.indexOf(showId)).split("/").length == 4) {
			// 設定張數
			for (int i = 0; i < 1000; i++)
				try {
					try {
						TimeUnit.MILLISECONDS.sleep(25);
					} catch (Exception e2) {}
					Select dropdown = new Select(driver.findElement(By.id("ticketPriceList")).findElement(By.tagName("select")));
					dropdown.selectByVisibleText(String.valueOf(targetProp.getTargetNums()));
					driver.findElement(By.id("TicketForm_agree")).click();
					driver.findElement(By.id("TicketForm_verifyCode")).sendKeys("");
					break;
				} catch (Exception e) {}
		}
		System.out.printf("hack succeed in %s ms%n",System.currentTimeMillis()-logTime);
	}
	
	private boolean matchTargetDate(String src,String target) {
		// TODO 判定方法可能要改
		if(src == null || "".equals(src) || target == null || "".equals(target))
			return false;
		return src.contains(target);
	}
	
	private boolean matchTargetArea(String src,TargetProp targetProp) {
		if(src == null || "".equals(src))
			return false;
		if(targetProp.getTargetAreaFloor() != null && !"".equals(targetProp.getTargetAreaFloor()) &&
				!src.contains(targetProp.getTargetAreaFloor()))
			return false;
		if(targetProp.getTargetAreaPrices().size() == 1) {
			if(!src.contains(String.valueOf(targetProp.getTargetAreaPrices().get(0))))
				return false;
		}else if(targetProp.getTargetAreaPrices().size() == 2) {
			Pattern pattern = Pattern.compile("\\d{3,7}");
			Matcher matcher = pattern.matcher(src);
			Integer tmp = null;
			if(!(matcher.find() && 
					(tmp = Integer.parseInt(matcher.group())) >= targetProp.getTargetAreaPrices().get(0) &&
					tmp <= targetProp.getTargetAreaPrices().get(1)))
				return false;
		}
		return true;
	}
	
	private WebElement getTargetAreaElement(List<WebElement> list,TargetProp targetProp) {
		String condition = null;
		List<WebElement> tempList = new LinkedList<>();
		if((condition = targetProp.getTargetAreaCode()) != null && !condition.isEmpty())
			for(WebElement element:list)
				if(element.getText().contains(condition))
					tempList.add(element);
		if(tempList.isEmpty())
			tempList.addAll(list);
		list = new LinkedList<>(tempList);
		tempList.clear();
		System.out.println(condition+" done("+System.currentTimeMillis());
		
		if((condition = targetProp.getTargetAreaFloor()) != null && !condition.isEmpty())
			for(WebElement element:list)
				if(element.getText().contains(condition))
					tempList.add(element);
		if(tempList.isEmpty())
			tempList.addAll(list);
		list = new LinkedList<>(tempList);
		tempList.clear();
		System.out.println(condition+" done("+System.currentTimeMillis());

		condition = targetProp.getTargetAreaPrices().toString();
		if(targetProp.getTargetAreaPrices().size() == 1) {
			for(WebElement element:list)
				if(element.getText().contains(condition))
					tempList.add(element);
		}else if(targetProp.getTargetAreaPrices().size() == 2) {
			for(WebElement element:list) {
				Pattern pattern = Pattern.compile("\\d{3,7}");
				Matcher matcher = pattern.matcher(element.getText());
				Integer tmp = null;
				if(matcher.find() && 
						(tmp = Integer.parseInt(matcher.group())) >= targetProp.getTargetAreaPrices().get(0) &&
						tmp <= targetProp.getTargetAreaPrices().get(1))
					tempList.add(element);
			}
		}
		if(tempList.isEmpty())
			tempList.addAll(list);
		list = new LinkedList<>(tempList);
		tempList.clear();
		System.out.println(condition+" done("+System.currentTimeMillis());
		
		if(list.isEmpty())
			return null;
		else if(list.size() == 1)
			return list.get(0);
		else
			return list.get(new Random().nextInt(list.size()));
	}
}
