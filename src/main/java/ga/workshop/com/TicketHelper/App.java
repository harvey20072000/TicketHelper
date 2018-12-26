package ga.workshop.com.TicketHelper;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import ga.workshop.com.TicketHelper.logic.TixCraftHacker;
import ga.workshop.com.TicketHelper.model.TargetProp;
import ga.workshop.com.TicketHelper.utils.Const;
import ga.workshop.com.TicketHelper.utils.Log;

public class App {
	public static void main(String[] args) {
		try {
			new App().run();
			int result = JOptionPane.showOptionDialog(null, "若是已完成操作，請按『完成』以正常關閉此系統", "購票小幫手", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {"完成"}, "完成");
			System.exit(0);
		} catch (Exception e) {
			Log.error("system err => {}", e);
			System.exit(9);
		}
	}
	
	private void run() {
		String rootPath = System.getProperty("user.dir");
		TargetProp targetProp = initProps(rootPath);
		WebDriver driver = null;
		
		// TODO 內建授權
		// TODO 強制關閉程式機制
		
		// D:/Program File/x32/chromedriver_win32/chromedriver.exe
		System.setProperty("webdriver.chrome.driver", String.format("%s\\%s", rootPath,"chromedriver.exe"));
		driver = new ChromeDriver(new ChromeOptions());
		
		switch (targetProp.getPlatform().getId()) {
		case "tixcraft":
			new TixCraftHacker().hack(driver, targetProp);
			break;
		default:
			break;
		}
	}
	
	private TargetProp initProps(String path){
		FileInputStream fis = null;
		TargetProp targetProp = null;
		try {
			Properties properties = new Properties();
			fis = new FileInputStream(String.format("%s\\%s",path, "target.properties"));
			InputStreamReader isr = new InputStreamReader(fis, Const.DEFAULT_CHARSET);
			properties.load(isr);
			targetProp = new TargetProp(properties);
			return targetProp;
		} catch (Exception e) {
			Log.error("initProps fail, exception => {}",e);
			System.out.println("initProps fail, exception => "+e);
		}finally {
			try {
				if(fis != null)
					fis.close();
			} catch (Exception e2) {}
		}
		return targetProp;
	}
}


