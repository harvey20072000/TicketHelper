package ga.workshop.com.TicketHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

public class TestSelenium {
	public static void main(String[] args) throws Throwable{
		WebDriver driver = null;
		
		// TODO 每個段落都設重試機制
		// TODO 給用戶在外部文件設定條件
		// TODO 內建授權
		// TODO 強制關閉程式機制
		
		// D:/Program File/x32/chromedriver_win32/chromedriver.exe
//		System.setProperty("webdriver.chrome.driver", String.format("%s\\%s", System.getProperty("user.dir"),"chromedriver.exe"));
		System.setProperty("webdriver.chrome.driver", "D:/Program File/x32/chromedriver_win32/chromedriver.exe");
		driver = new ChromeDriver(DesiredCapabilities.chrome());
		
		// TODO 選擇訂票網站
		driver.get("https://tixcraft.com/activity/detail/19_JimG");
		System.out.println(driver.getTitle());
		
		// TODO 等時間到就執行(點到有tag a出現才點) 50ms點一次
		List<WebElement> elements = driver.findElements(By.className("list-inline"));
		elements.get(elements.size()-1).findElements(By.tagName("a")).get(0).click();
		
		TimeUnit.MILLISECONDS.sleep(50);
		List<WebElement> trs = driver.findElement(By.id("gameList")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
		for(WebElement tr : trs) {
			// TODO 可設定想要的場次時間(限一場)
			if(tr.findElements(By.tagName("td")).get(0).getText().contains("03/31")) {
				tr.findElement(By.xpath("//input[@type='button']")).click();
				break;
			}
		}
		
		TimeUnit.MILLISECONDS.sleep(50);
		List<WebElement> areas = driver.findElements(By.xpath("//ul[@class='area-list']"));
		for(WebElement area : areas) {
			// TODO 可設定想要的區域(幾樓、票價)
			if(area.getText().contains("1800")) {
				area.findElement(By.tagName("a")).click();
				break;
			}
		}
		
		// TODO 設定張數
		TimeUnit.MILLISECONDS.sleep(50);
		Select dropdown = new Select(driver.findElement(By.id("ticketPriceList")).findElement(By.tagName("select")));
		dropdown.selectByVisibleText("3");
		driver.findElement(By.id("TicketForm_agree")).click();
		driver.findElement(By.id("TicketForm_verifyCode")).sendKeys("");
	}
}
