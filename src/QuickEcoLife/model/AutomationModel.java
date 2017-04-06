package QuickEcoLife.model;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import QuickEcoLife.util.ImageCombo;
import QuickEcoLife.util.MetadataExtractor;

public class AutomationModel {
	private String dir_working = null;
	private WebDriver driver = null;
	
	// Initialize at selectRandomPoint()
	List<WebElement> square_icons = null;
	
	/**
	 * Initialize WebDriver with specified parameters.
	 */
	public AutomationModel() throws NullPointerException, WebDriverException {	
		// To get the working directory of the running jar.
		dir_working
			= ClassLoader.getSystemClassLoader().getResource(".").getPath();
		try {
			dir_working = URLDecoder.decode(dir_working, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// File name for Unix like OS.
		String chromedriver_name = "chromedriver";
		
		// If the program runs on the Windows system, 
		// the filename extension ".exe" needs to be appended.
		if (System.getProperty("os.name").startsWith("Windows")) {
			chromedriver_name += ".exe";
		}
		
		// There will be a directory called "res" in the jar's working directory
		// and the chromedriver will be put inside the "res".
		String chromedriver_path
			= dir_working + "res" + File.separator + chromedriver_name;
		System.setProperty("webdriver.chrome.driver", chromedriver_path);
		
		// To accept SSL certificate
		DesiredCapabilities capability = DesiredCapabilities.chrome();
		capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		
		// Initialize chromedriver with capability
		driver = new ChromeDriver(capability);
	}
	
	public boolean loginSuccessfully(String user, String passwd) {
		// Initialize a flag.
		boolean success = true;
		
		// Go to login page.
		driver.get("https://ecolife.epa.gov.tw/default.aspx");
		
		// Fill up user & passwd and login.
		String id_user = "cphMain_yAxle_y_login_txtID";
		driver.findElement(By.id(id_user)).sendKeys(user);
		
		String id_passwd = "cphMain_yAxle_y_login_txtPWD";
		driver.findElement(By.id(id_passwd)).sendKeys(passwd);
		
		String id_submit = "cphMain_yAxle_y_login_btnLogin";
		driver.findElement(By.id(id_submit)).click();
		
		// Check if login successfully by the popup alert windonw message.
		//String parentWindowHandler = driver.getWindowHandle();
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5, 100);
	        wait.until(ExpectedConditions.alertIsPresent());
	        Alert alert = driver.switchTo().alert();
	        alert.accept();
	        success = false;
	        	driver.quit();
		} catch (Exception e) {
			if (success) {
				System.out.println(user + " login!");
        			//driver.switchTo().window(parentWindowHandler);
        		
        			// Login successfully, click backend admin button
        			String id_backend_admin = "cphMain_yAxle_y_login_btnAdmin";
        			driver.findElement(By.id(id_backend_admin)).click();	
			}
		}
		
		return success;
	}
	
	public void automateJournalPost(List<ImageCombo> imageCombos, String journal_type) {	 
		// For each imgCombo, select the date according to the image's exif
		int num_uploaded = 0;
		System.out.println("ImageCombos size: " + imageCombos.size());
		for (ImageCombo imgCombo : imageCombos) {
			goToJournalPage(journal_type);
			
			String[] dateInfo_dirty = MetadataExtractor.extractImgExif(imgCombo.getPath_imgDirty());
			String[] dateInfo_clean = MetadataExtractor.extractImgExif(imgCombo.getPath_imgClean());
		
			// 0: year, 1: month, 2: day
			selectDate(dateInfo_dirty, dateInfo_clean, journal_type);			
			
			// Select route
			selectRoute();
			
			// Click journal creation button
			clickJournalCreationButton();
			
			// Select trash icon
			selectTrashIcon();
					
			// Select area
			selectArea();
			
			// Randomly select a point on the map
			selectRandomPoint();
			
			// Upload image with trash
			uploadImg(imgCombo.getPath_imgDirty(), "dirty");
			
			// Upload image without trash
			uploadImg(imgCombo.getPath_imgClean(), "clean");
			
			// Save journal
			clickSaveJournalButton();
			
			// Post journal
			clickPostJournalButton();
			
			if (postSuccessfully(journal_type)) {
				num_uploaded++;
				System.out.println(num_uploaded + " imageCombo uploaded.");
			}
		}
		if (num_uploaded == imageCombos.size()) {
			System.out.println("Process completed"); 
		}
	}
	
	public void quitChrome() {
		driver.quit();
	}
	
	private void goToJournalPage(String journal_type) {
		String url = "https://ecolifepanel.epa.gov.tw/journal/";
		url += (journal_type.equals("inspect")) ? "inspect.aspx" : "clear.aspx";
		
		// Go to site for publishing patrol diary
		driver.get(url);
		checkAlert();
	}
	
	private void selectDate(String[] dateInfo_dirty, String[] dateInfo_clean, String journal_type) { 	
		String[] selector_ids = {"cphMain_ucDateTime_cboYear", "cphMain_ucDateTime_cboMonth", "cphMain_ucDateTime_cboDay",
					"cphMain_ucDateTime_cboHour", "cphMain_ucDateTime_cboMinute",
						"cphMain_ucDateTime_cboHourEnd", "cphMain_ucDateTime_cboMinuteEnd"};
	
		String[] suitable_timeRange = MetadataExtractor.getSuitableTimeRange(dateInfo_dirty, dateInfo_clean);
		//String[] selector_values = {year, month, day, "8", "0", "17", "30"};
		
		
		for (int i = 0; i < selector_ids.length; i++) {
			Select selector = new Select(driver.findElement(By.id(selector_ids[i])));
			selector.selectByVisibleText(suitable_timeRange[i]);
		}
		if (journal_type.equals("clear")) {
			Select selector = new Select(driver.findElement(By.id("cphMain_ucDateTime_cboDayEnd")));
			selector.selectByVisibleText(suitable_timeRange[2]);
		}
	}
	
	private void selectRoute() {
		// Handle the route selector
		String id_route = "cphMain_cboClear";
		Select select_route = new Select(driver.findElement(By.id(id_route)));
		select_route.selectByIndex(1);
	}
	
	private void clickJournalCreationButton() {
		String id_journalCreation = "cphMain_btnOk";
		WebDriverWait wait = new WebDriverWait(driver, 5, 100);
		WebElement clickable_journalCreationBut
			= wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id(id_journalCreation))));
		clickable_journalCreationBut.click();
		
		// Handle the Alert popups.
		checkAlert();
		
		// If the date is a week before the current date
		// there will be another popup alert.
		checkAlert();
	}
	
	private void selectTrashIcon() {
		String id_categorySelector = "cphMain_btnIcon";
		WebDriverWait wait = new WebDriverWait(driver, 5, 100);
		WebElement clickable_selectTrashBut
			= wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id(id_categorySelector))));
		clickable_selectTrashBut.click();
		
//		String id_categorySelector = "cphMain_btnIcon";
//		driver.findElement(By.id(id_categorySelector)).click();
		String xpath_trashIcon = "//*[@id='cphMain_ucIcon_divEcolife']/ul[2]/li[10]/a/img";
		driver.findElement(By.xpath(xpath_trashIcon)).click();
	}
	
	private void selectArea() {
		// Select the area
		String id_areaSelector = "cphMain_cboLocality";
		Select select_areaSelector = new Select(driver.findElement(By.id(id_areaSelector)));
				
		// Index 8 means the "road"
		select_areaSelector.selectByIndex(8);
	}
	
	private void selectRandomPoint() {
		// Square icons filter
		List<WebElement> icons = driver.findElements(By.className("leaflet-marker-icon"));
		square_icons = new ArrayList<WebElement>();
		for (WebElement icon : icons) {
			String src = ((JavascriptExecutor)driver).executeScript("return arguments[0].attributes['src'].value;", icon).toString();
			
			// suqare.png means blank square icons; square means all square icons.
			String icons_onThePath = "square";
			if (src.contains(icons_onThePath)) {
				square_icons.add(icon);
			}
		}
		
		// Randomly select an icon by a random int with range of [0, square_icons.size()-1]
		int randomNum = ThreadLocalRandom.current().nextInt(0, square_icons.size());
		System.out.println("random point: " + randomNum);
		
		// Try to fix the unclickable problem
		// reference: http://stackoverflow.com/questions/35942196/how-to-mouse-hover-and-click-on-element-in-webdriver
		// Wait until the square appears.
		WebDriverWait wait = new WebDriverWait(driver, 5, 100);
		WebElement clickable_square
			= wait.until(ExpectedConditions.elementToBeClickable(square_icons.get(randomNum)));
		
		// ---- Try another way to do so ----
		Actions actions = new Actions(driver);
		actions.moveToElement(clickable_square);
		actions.click().perform();
		
		// Delete the selected square icon so it won't be selected again.
		if (square_icons.size() > 1) {
			square_icons.remove(randomNum);
		}
	}
	
	private void uploadImg(String img_path, String status) {
		String id_uploadImg
			= (Objects.equals(status, "dirty")) ? "cphMain_ucImageUpload_1_fup" : "cphMain_ucImageUpload_3_fup";
			
		driver.findElement(By.id(id_uploadImg)).sendKeys(img_path);
	}
	
	private void clickSaveJournalButton() {
		String id_saveJournalButton = "cphMain_btnOk";
		driver.findElement(By.id(id_saveJournalButton)).click();
	}
	
	private void clickPostJournalButton() {
		String id_postJournalButton = "cphMain_btnPost";
		driver.findElement(By.id(id_postJournalButton)).click();
		checkAlert();
		checkAlert();
	}
	
	private boolean postSuccessfully(String journal_type) {
		String partOf_title = (journal_type.equals("inspect")) ? "查詢巡檢日誌" : "查詢清理日誌";
		
		return driver.getTitle().contains(partOf_title);
	}
	
	private void checkAlert() {
	    try {
	    		WebDriverWait wait = new WebDriverWait(driver, 5, 100);
	        wait.until(ExpectedConditions.alertIsPresent());
	        Alert alert = driver.switchTo().alert();
	        alert.accept();
	    } catch (Exception e) {
	        //exception handling
	    }
	}
}
