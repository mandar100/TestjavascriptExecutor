package frames;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import objectrepository.LoginPage;
import objectrepository.NomisBasePage;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.thoughtworks.selenium.Selenium;


public class Config implements IConfig{

	public static WebDriver _driver = null;
	public Selenium selenium =null;
	public WebElement element;
	public StringBuffer verificationErrors = new StringBuffer();

	public static Read_properties _base_config_properties = null;
	public static Read_properties _keyValueProperties = null;

	public String appURL = null;
	public static String testDataLocation = null;
	public String adminUser=null;
	public String adminPwd=null;
	public String browser=null;
	public String resultLocation=null;
	public int page_timeout=0;
	public  String sVertical=null;
	public static int object_wait_timeout=0;
	public static Boolean bCapture = false;
	
	
	public Logger log = Logger.getLogger(Config.class.getName());;

	public LoginPage _loginPage = null;
	public NomisBasePage _navigatePage = null;

	public static DataHashMap<String, List<String>> _baselineXLSMap = null;
	public static DataHashMap<String, List<String>> _scenarioXLSMap = null;
	public static DataHashMap<String, List<String>> _importFileXLSMap = null;
	public static DataHashMap<String, List<String>> _scenarioGridXLSMap = null;
	public static DataHashMap<String, List<String>> _assumptionsXLSMap = null;
	public static DataHashMap<String, List<String>> _assumptionsEditXLSMap = null;
	public static DataHashMap<String, List<String>> _editGridXLSMap = null;
	public static DataHashMap<String, List<String>> _priceListXLSMap = null;
	public static DataHashMap<String, List<String>> _efXLSMap = null;
	public static DataHashMap<String, List<String>> _tlpXLSMap = null;
	public static DataHashMap<String, List<String>> _dimesionXLSMap = null;
	public static DataHashMap<String, List<String>> _userXLSMap = null;
	public static DataHashMap<String, List<String>> _boundsXLSMap = null;
	public static DataHashMap<String, List<String>> _perfXLSMap = null;
	public static DataHashMap<String, List<String>> _lrrXLSMap = null;
	public static DataHashMap<String, List<String>> _foldersXLSMap = null;
	public static DataHashMap<String, List<String>> _globalAssumptionsXLSMap = null;
	public static DataHashMap<String, List<String>> _messageXLSMap = null;
	public static DataHashMap<String, List<String>> _pricelockXLSMap = null;
	public static DataHashMap<String, List<String>> _shareXLSMap = null;
	public static DataHashMap<String, List<String>> _globalRuleXLSMap = null;

	//===============================================================================
	// Author 	: Anand Somani
	// Date		: 05-Jan-2013
	// Purpose : Reading properties files, Launching Driver
	// Input	: N/A
	// Output	: driver instance
	//===============================================================================
	@BeforeSuite(alwaysRun = true) 
	public WebDriver SetUp() {
		try {
			PropertyConfigurator.configure("config/log4j.properties");
			System.out.println("In @BeforeSuite");
			readPropertiesData();
			NomisUtils.checkFolders();
			if (_base_config_properties.getPropertyValue("CAPTURE").equalsIgnoreCase("FALSE")){
				bCapture = false;
			}
			if(browser.equalsIgnoreCase("IE")){
				File file = new File(System.getProperty("user.dir")+"\\IEDriverServer.exe");
				System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
				_driver = new InternetExplorerDriver();
			}else{
				FirefoxProfile nomisProfle= new FirefoxProfile();
				nomisProfle.setPreference("network.proxy.type", 4);
				nomisProfle.setPreference("browser.download.panel.shown", true);
				nomisProfle.setPreference("browser.download.useDownloadDir", true);
				String sExportPath=_base_config_properties.getPropertyValue("RESULT_LOCATION") + "\\Export";
				System.out.println("Export path: " + sExportPath );
				nomisProfle.setPreference("browser.download.dir", sExportPath);
				nomisProfle.setPreference("browser.download.manager.showWhenStarting", false);
				nomisProfle.setPreference("browser.download.folderList", 2);
				//nomisProfle.setPreference("network.proxy.type", 4);
				_driver = new FirefoxDriver(nomisProfle);
				selenium = new WebDriverBackedSelenium(_driver, appURL);
				
			}
			System.out.println("Setup finished");
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Setup Failed ",e);
			Reporter.log(e.getMessage());
			Assert.fail("Setup Failed ", e);
			
		}
		return _driver;
	}

	//===============================================================================
	// Author 	: Anand Somani
	// Date		: 05-Jan-2013
	// Purpose : Opening application URL
	// Input	: N/A
	// Output	: N/A
	//===============================================================================
	@BeforeTest (alwaysRun = true) 
	public void beforeTest(){
		readPropertiesData();
		_messageXLSMap = NomisUtils.createTestDataMap(testDataLocation + "\\StatusMessage.xls");
		_driver.get(appURL);
		_driver.manage().window().maximize();
		_driver.manage().timeouts().implicitlyWait(object_wait_timeout, TimeUnit.SECONDS);	
		_driver.manage().timeouts().pageLoadTimeout(page_timeout, TimeUnit.SECONDS);
		_driver.manage().timeouts().setScriptTimeout(300, TimeUnit.SECONDS);
		_loginPage = PageFactory.initElements(_driver, LoginPage.class);		
	}

	//===============================================================================
	// Author 	: Anand Somani
	// Date		: 05-Jan-2013
	// Purpose : Close browser
	// Input	: N/A
	// Output	: N/A	
	//===============================================================================
	@AfterTest(alwaysRun = true) 
	public void afterTest(){
		//_driver.quit();
	}

	//===============================================================================
	// Author 	: Anand Somani
	// Date		: 05-Jan-2013
	// Purpose : Quit the driver
	// Input	: N/A
	// Output	: N/A		
	//===============================================================================

	@AfterSuite(alwaysRun = true) 
	public void TearDown() { 
		System.out.println("Closing the browser");
		// Close the browser
		/*_driver.close(); 
		_driver.quit();*/
		_driver.quit();

		appURL = null;	
		_driver = null;
		log.info("Execution successful : Driver Quit");
		System.out.println("Automation suite execution is completed.");
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			Assert.fail(verificationErrorString);
		}
	}

	//===============================================================================
	public static String getData(String sVal){
		if (sVal.contains("#"))
		{
			String sKey = sVal.substring(1);
			sVal = _keyValueProperties.getPropertyValue(sKey);
			//System.out.println("Key: " + sKey + "\t Value: " + sVal);
		}
		return sVal;
	}

	//===============================================================================
	// Author 	: Anand Somani
	// Purpose : Reading application related data
	//===============================================================================
	public void readPropertiesData(){
		System.out.println("In getPropertiesData");
		_base_config_properties = new Read_properties("base_config");
		appURL = _base_config_properties.getPropertyValue("BASEURL");
		adminUser = _base_config_properties.getPropertyValue("ADMIN_USER");
		adminPwd = _base_config_properties.getPropertyValue("ADMIN_PWD");
		browser = _base_config_properties.getPropertyValue("BROWSER");
		page_timeout = Integer.parseInt(_base_config_properties.getPropertyValue("PAGE_WAIT_TIMEOUT"));
		object_wait_timeout = Integer.parseInt(_base_config_properties.getPropertyValue("OBJECT_WAIT_TIMEOUT"));
		//testDataLocation = System.getProperty("user.dir")+ "\\" + _base_config_properties.getPropertyValue("TEST_DATA_LOCATION");
		testDataLocation = _base_config_properties.getPropertyValue("TEST_DATA_LOCATION");
		sVertical = _base_config_properties.getPropertyValue("VERTICAL");
		resultLocation = _base_config_properties.getPropertyValue("RESULT_LOCATION");
		if ("true".equalsIgnoreCase(_base_config_properties.getPropertyValue("CAPTURE"))){
			bCapture=true;
		}
		log.info("Vertical: " + sVertical);

		_keyValueProperties = new Read_properties("keyvalue_" + sVertical);
		_dimesionXLSMap = NomisUtils.createTestDataMap(System.getProperty("user.dir")+"\\Config\\Dimension_" + sVertical + ".xls");

	}

	public static String getPropertiesData(String sPropertyName){
		return _base_config_properties.getPropertyValue(sPropertyName);
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It captures the screen shot.
	// Parameter(s) : NA 
	// Return: void
	//-----------------------------------------------------------------------------------
	public void captureScreenShot(String TestName){
		String sPath=getPropertiesData("RESULT_LOCATION")+ "\\Actual\\Snapshots\\" + TestName + ".png";
		File scrFile = ((TakesScreenshot)_driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(sPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		NomisUtils.logMessage("Captured Screen: " + sPath, true);
	}

}		


