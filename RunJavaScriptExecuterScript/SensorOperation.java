package com.verifyBuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import jxl.read.biff.BiffException;

import org.openqa.selenium.*;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

import com.google.common.base.Predicate;

public class SensorOperation {
	WebDriver sensorDriver;
	String methodName;
	public Properties hachSensorProperties;
	List<String> sensorIdList  = new ArrayList<String>();
 
  @Test(priority = 1)
  public void login() throws Exception {
	  methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
	 
	 /* Set<Object> sensorPropString=hachSensorProperties.keySet();
	 // Set<String> sensorPropString=hachSensorProperties.stringPropertyNames();
	  for (Object sensorProp : sensorPropString) {
		System.out.println((String)sensorProp);
	}*/
	  String baseUrl = hachSensorProperties.getProperty("baseUrl");
	  sensorDriver.get(baseUrl);
	  String hsaUserName = hachSensorProperties.getProperty("userName");
	  String hsaPassword = hachSensorProperties.getProperty("password");
	 /* WebDriverWait sensorWebDriverWait=new WebDriverWait(sensorDriver, 80);
	  sensorWebDriverWait.*/
	  FluentWait<WebDriver> wait = new FluentWait<WebDriver>(sensorDriver)
		       .withTimeout(80, TimeUnit.SECONDS)
		       .pollingEvery(5000, TimeUnit.MILLISECONDS);
			
	  // start waiting for given element
	  wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.btn.btn-default")));
	  sensorDriver.findElement(By.id("Email")).sendKeys(hsaUserName);
	  sensorDriver.findElement(By.id("Password")).sendKeys(hsaPassword);
	  sensorDriver.findElement(By.cssSelector("input.btn.btn-default")).click();
	 // wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.fusion-logout-button")));
	  wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#dvTitleHome")));
	  if(sensorDriver.getTitle().equalsIgnoreCase("Provisioning - Fusion"))
	  {
		  Reporter.log("Successfully Logged to Provisioning page.");
	  }
	  else
	  {
		  Reporter.log("Problem With Login to HACH application.");
		  TestTakeScreenShot.takeSnap(sensorDriver,hachSensorProperties.getProperty("screenshotLocation")+methodName+".jpg");
	  }
	 
  }
  @Test(dependsOnMethods="login", enabled=true, priority = 2)
  public void goToTenantProvisioningPage() throws InterruptedException
  {
	   WebElement TenantIdentifier = sensorDriver.findElement(By.id("TenantId"));
	  String tenantVisibleText=hachSensorProperties.getProperty("TenantVisibleText");
	  String tenantVisibleID=hachSensorProperties.getProperty("TenantId");
	  Select select = new Select(TenantIdentifier);
	  select.selectByVisibleText(tenantVisibleText);
	  sensorDriver.findElement(By.cssSelector("button.btn-lg")).click();
	  FluentWait<WebDriver> wait = new FluentWait<WebDriver>(sensorDriver)
		       .withTimeout(80, TimeUnit.SECONDS)
		       .pollingEvery(500, TimeUnit.MILLISECONDS);
	 // Reporter.log(By.cssSelector("div.col-xs-12>h2"));
	  wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div.col-xs-12>h2"), "Provision Tenant "+tenantVisibleID));
	  Assert.assertEquals(sensorDriver.getTitle(), "Provision Tenant - Fusion", "Provisioning page is not displayed.");
	  /*goToProvisioningParameter("Provision Sensors", "a.fusion-provisioning-button");
	  wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div.col-xs-12>h2"), "Provision Sensors for Tenant XUUMJJ"));*/
	  //Assert.assertEquals(sensorDriver.getTitle(), "Provision Tenant - Fusion", "Provisioning page is not displayed.");
	  //deleteSensorData("Provision DR3900",);
	  goToProvisioningParameter("Provision DR3900", "a.fusion-provisioning-button");
	  wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div.col-xs-12>h2"), "Provision DR3900 for Tenant "+tenantVisibleID));
	 // wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("span.glyphicon-trash")));
	  deleteDRData();
	  deleteSensorData();
	  
  }
  @SuppressWarnings("static-access")
@Test(dependsOnMethods="goToTenantProvisioningPage",enabled=true, priority = 3)
  public void addSensorData()
  {
	  FluentWait<WebDriver> wait = new FluentWait<WebDriver>(sensorDriver)
		       .withTimeout(60, TimeUnit.SECONDS)
		       .pollingEvery(3500, TimeUnit.MILLISECONDS)
		       .ignoring(NoSuchElementException.class);
	  try {
		  String sensorDataFilePath=hachSensorProperties.getProperty("sensorDataFileName");
		ImportDataFromExcel hachSensorData=new ImportDataFromExcel(sensorDataFilePath);
		
		for (int i = 1; i < hachSensorData.getsensorDataRowCount(); i++) {
			String sensorType, sensorId, gatewayId;
			sensorType=hachSensorData.sensorDataReadCell(0,i );
			gatewayId=hachSensorData.sensorDataReadCell(3,i );
			sensorId=hachSensorData.sensorDataReadCell(2,i );
			sensorIdList.add(sensorId);
			WebElement TenantIdentifier = sensorDriver.findElement(By.id("family"));
			Select select = new Select(TenantIdentifier);
			 select.selectByVisibleText(sensorType);
			 sensorDriver.findElement(By.cssSelector("#SerialNumber")).sendKeys(hachSensorData.sensorDataReadCell(1,i ));
			 WebElement gatewayIdentifier = sensorDriver.findElement(By.id("gatewayId"));
			 Select selectGateway = new Select(gatewayIdentifier);
			 selectGateway.selectByVisibleText(gatewayId);
			 sensorDriver.findElement(By.cssSelector("button.btn-default")).click();
			 
			 // Reporter.log(By.cssSelector("div.col-xs-12>h2"));
			  wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table-condensed"))));
			  performHandshakeGetAllSettings(sensorId,gatewayId,sensorType);
			  
		}
		  
	} catch (BiffException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	sensorDriver.findElement(By.cssSelector("a.fusion-logout-button")).click();  
	wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.btn-default")));
	  
  }
  @Test(dependsOnMethods="addSensorData",enabled=true, priority = 4)
  public void checkSensorCreation()
  {
	  FluentWait<WebDriver> wait = new FluentWait<WebDriver>(sensorDriver)
		       .withTimeout(50, TimeUnit.SECONDS)
		       .pollingEvery(3500, TimeUnit.MILLISECONDS);
	  String tenantUserName=hachSensorProperties.getProperty("TenantUserName");
	  String tenantPassword=hachSensorProperties.getProperty("TenantPassword");
	  sensorDriver.findElement(By.id("Email")).sendKeys(tenantUserName);
	  sensorDriver.findElement(By.id("Password")).sendKeys(tenantPassword);
	  sensorDriver.findElement(By.cssSelector("input.btn.btn-default")).click();
	  wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.fusion-logout-button")));
	  for (String sensorId : sensorIdList) 
	  {
		  try {
			  sensorDriver.findElement(By.id("issues"+sensorId));
			  Reporter.log("Sensor With sensorID "+sensorId+" is present on Device Listing page");
			
		} catch (NoSuchElementException e) {
			// TODO: handle exception
			Reporter.log("Sensor With sensorID "+sensorId+" is absent on Device Listing page");
		}
		
	  }
  }
  private void performHandshakeGetAllSettings(String sensorId, String selectGateway, String sensorType) {
	// TODO Auto-generated method stub
	  Runtime rt= Runtime.getRuntime();
	  String getAllSettingsForSensor=null;
	  String initialHandshakeSensorCommand="FusionDeviceEmulator.exe -m o -s "+sensorId+" --gw  "+selectGateway+" -o 1 --v 2.0";
	   
		
	  switch (sensorType) {
	case "AN-ISE sc":
		getAllSettingsForSensor="FusionDeviceEmulator.exe -m s -s "+sensorId+" --stt sequence_id=get_all_settings temperature_adjustment=1.2 automatic_potassium_compensation=true automatic_chloride_compensation=false chloride_compensation_value=1 response_time=3 parameters=1";
		break;
	case "A-ISE sc":
		getAllSettingsForSensor="FusionDeviceEmulator.exe -m s -s "+sensorId+" --stt sequence_id=get_all_settings temperature_adjustment=1.2 automatic_potassium_compensation=true automatic_chloride_compensation=false chloride_compensation_value=1 response_time=3 parameters=0";	
			break;
	case "N-ISE sc":
		getAllSettingsForSensor="FusionDeviceEmulator.exe -m s -s "+sensorId+" --stt sequence_id=get_all_settings temperature_adjustment=1.2 automatic_potassium_compensation=true automatic_chloride_compensation=false chloride_compensation_value=1 response_time=3 parameters=0";
		break;
	case "SOLITAX sc":
		getAllSettingsForSensor="FusionDeviceEmulator.exe -m s -s "+sensorId+" --stt sequence_id=get_all_settings success=true parameter=tss response_time=12";
		System.out.println("For solitax: "+getAllSettingsForSensor);
		break;
	case "LDO sc":
		getAllSettingsForSensor="FusionDeviceEmulator.exe -m s -s "+sensorId+" --stt sequence_id=get_all_settings success=true salinity_correction=20 air_correction_units=ft air_correction=500 response_time=15";	
			break;
	default:
		System.out.println("Wrong Sensor ID");
		break;
	}
	  String command = "\" D: && cd D:\\Team2_Sensor\\Tools\\FusionDeviceEmulator\\bin\\Debug && "+initialHandshakeSensorCommand+" && "+getAllSettingsForSensor;
	  try {
			
		    Process process = rt.exec("cmd /c"+command);
		    //process=rt.exec(command);
		 
		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream()));
		    String line;
		    while ((line = reader.readLine()) != null) {
		        System.out.println(line);
		    }
		 
		    reader.close();
		 
		} catch (IOException e) {
		    e.printStackTrace();
		}
	
}
private void deleteSensorData() {
	// TODO Auto-generated method stub
	  	goToProvisioningParameter("Provision Sensors", "a.fusion-provisioning-button");
	  	FluentWait<WebDriver> wait = new FluentWait<WebDriver>(sensorDriver)
			       .withTimeout(50, TimeUnit.SECONDS)
			       .pollingEvery(3500, TimeUnit.MILLISECONDS);	  
		List<WebElement> tableRowSensorData=sensorDriver.findElements(By.cssSelector("div.delete-sensor-button"));
		if (tableRowSensorData.size()==0) {
			Reporter.log("\n No Sensor Data is Found for Tenant");
			try {
				TestTakeScreenShot.takeSnap(sensorDriver, hachSensorProperties.getProperty("screenshotLocation")+"deleteSensorScreenSucess.jpg");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			/*for (WebElement rowDeleteSensorButton : tableRowSensorData) {
				
				rowDeleteSensorButton.click();				
				 // Reporter.log(By.cssSelector("div.col-xs-12>h2"));
				  wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table-condensed"))));
				  sensorDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				  tableRowSensorData=sensorDriver.findElements(By.cssSelector("div.delete-sensor-button"));
			}*/
			int sizeOfSensorList=tableRowSensorData.size();
			System.out.println("size of list is :"+sizeOfSensorList);
			while (true) {
				tableRowSensorData=sensorDriver.findElements(By.cssSelector("div.delete-sensor-button"));
				if(tableRowSensorData.size()==0)
				{
					Reporter.log("\nAll the sensors are deleted");
					break;
				}
				sensorDriver.findElement(By.cssSelector("table.table-striped>tbody>tr:nth-child(1)>td:nth-child(5)>div.delete-sensor-button")).click();
				//WebElement deleteSensor=deleteSensor.click();
				//wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.cssSelector("table.table-striped>tbody>tr:nth-child(1)>td:nth-child(5)>div.delete-sensor-button"))));
				wait.until(ExpectedConditions.refreshed(ExpectedConditions.stalenessOf(sensorDriver.findElement(By.cssSelector("table.table-striped>tbody>tr:nth-child(1)>td:nth-child(5)>div.delete-sensor-button")))));
				//visibilityOfElementLocated(By.cssSelector("table.table-striped"))));
				
				
					
			}
			
			/*for (int i = sizeOfSensorList; ;) {
				
				//System.out.println("size of list is :"+sizeOfSensorList);
				//sensorDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				WebElement deleteSensor=sensorDriver.findElement(By.cssSelector("table.table-striped>tbody>tr:nth-child(1)>td:nth-child(5)>div.delete-sensor-button>span"));
				deleteSensor.click();
				wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.cssSelector("table.table-striped>tbody>tr:nth-child(1)>td:nth-child(5)>div.delete-sensor-button"))));
				//wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.delete-sensor-button")));
				
				
			}*/
		}
	
}
private void deleteDRData() {
	// TODO Auto-generated method stub
	List<WebElement> tableRowDRData=sensorDriver.findElements(By.cssSelector("div.delete-dr-button"));
	if (tableRowDRData.size()==0) {
		Reporter.log("No DR Data is Found for Tenant");
		
	} else 
	{
		for (WebElement rowDRButton : tableRowDRData) {
			
			rowDRButton.click();
			FluentWait<WebDriver> wait = new FluentWait<WebDriver>(sensorDriver)
				       .withTimeout(60, TimeUnit.SECONDS)
				       .pollingEvery(3500, TimeUnit.MILLISECONDS);
			 // Reporter.log(By.cssSelector("div.col-xs-12>h2"));
			  wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table-condensed"))));
		}

	}
	sensorDriver.findElement(By.linkText("Provision Tenant page")).click();
}
private void goToProvisioningParameter(String parameterString, String locatorString) 
  {
	// TODO Auto-generated method stub
	List<WebElement> parameterList=sensorDriver.findElements(By.cssSelector(locatorString));
	for (WebElement parameter : parameterList) {
		if (parameter.getText().equalsIgnoreCase(parameterString)) {
			
			parameter.click();
			return;
		}
	}
}

 
  @BeforeSuite
  public void beforeSuite() {
	  FileInputStream hachInputStream;
	  
		try {
			hachInputStream = new FileInputStream("E:/workSpace_06022015/CheckHachBuildVerification/Properties/hachLangeMSM.properties");
			 hachSensorProperties=new Properties();
			  hachSensorProperties.load(hachInputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	  
	  if (hachSensorProperties.getProperty("browser").equalsIgnoreCase("IE")) {
		  File file = new File(hachSensorProperties.getProperty("ieDriverPath"));
			System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
			sensorDriver = new InternetExplorerDriver();
			
		
	} else {
		if (hachSensorProperties.getProperty("browser").equalsIgnoreCase("chrome")) {
			  File file = new File(hachSensorProperties.getProperty("chromeDriverPath"));
				System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
				sensorDriver = new ChromeDriver();
			
		} 
		else{
			 FirefoxProfile profile = new FirefoxProfile();
			  Proxy proxy = new Proxy();
			  proxy.setProxyType(ProxyType.AUTODETECT);
			  proxy.setAutodetect(true);
			  DesiredCapabilities dc = DesiredCapabilities.firefox();
			  dc.setCapability(CapabilityType.PROXY, proxy);
			  sensorDriver=new FirefoxDriver(dc);
		 //FirefoxProfile profile = new FirefoxProfile();
		  //Proxy proxy = new Proxy();
		  //proxy.setProxyType(ProxyType.AUTODETECT);
		//  proxy.setAutodetect(true);
		 // proxy.setHttpProxy("ptproxy.persistent.co.in:8080")
		  //		.setFtpProxy("ptproxy.persistent.co.in:8080")
		  //		.setSslProxy("ptproxy.persistent.co.in:8080");
		//  DesiredCapabilities dc = DesiredCapabilities.firefox();
		//  dc.setCapability(CapabilityType.PROXY, proxy);
			}	

	}
	 
	  sensorDriver.manage().window().maximize();
	  
	 
  }

  @AfterSuite
  public void afterSuite() {
	  
	  sensorDriver.quit();
  }

}
