package com.spiceJet;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

public class ActionDemoSpiceJet {
	WebDriver spiceJetDriver;
  @Test
  public void checkActionDemo() {
	  spiceJetDriver.navigate().to("http://www.spicejet.com/");
	  WebDriverWait spiceJetWait=new WebDriverWait(spiceJetDriver, 90);
	  spiceJetWait.until(ExpectedConditions.elementToBeClickable(spiceJetDriver.findElement(By.cssSelector("a[title='Contact Us']"))));
	  Actions spiceJetMoveToContact=new Actions(spiceJetDriver);
	  spiceJetMoveToContact.moveToElement(spiceJetDriver.findElement(By.cssSelector("a[title='Contact Us']"))).click().perform();
	  //spiceJetDriver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
	  spiceJetWait.until(ExpectedConditions.visibilityOf(spiceJetDriver.findElement(By.cssSelector("a[href='/Airports.aspx']"))));
	  spiceJetMoveToContact.moveToElement(spiceJetDriver.findElement(By.cssSelector("a[href='/Airports.aspx']"))).click().perform();
	  spiceJetDriver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
  }
  @BeforeClass
  public void beforeClass() {
	  /*Actions moveMouseToProvisionButton=new Actions(sensorDriver);
	  moveMouseToProvisionButton.moveToElement(sensorDriver.findElement(By.cssSelector("button.btn-primary"))).perform();
	  sensorDriver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);*/
  }

  @AfterClass
  public void afterClass() {
  }

  @BeforeTest
  public void beforeTest() {
  }

  @AfterTest
  public void afterTest() {
  }

  @BeforeSuite
  public void beforeSuite() {
	  /*hachSensorProperties = new Properties();
	  try {
		FileInputStream fis=new FileInputStream("Properties\\hachLangeMSM.properties");
		hachSensorProperties.load(fis);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  if (hachSensorProperties.getProperty("browser").equalsIgnoreCase("IE")) {
		  File file = new File("Drivers\\IEDriverServer.exe");
			System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
			sensorDriver = new InternetExplorerDriver();
		
	} else {
		if (hachSensorProperties.getProperty("browser").equalsIgnoreCase("chrome")) {
			  File file = new File("D:\\WATIR_Tool_Docs\\Install\\Install\\Drivers\\chromedriver.exe");
				System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
				sensorDriver = new ChromeDriver();
			
		} 
		else{
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

	}*/
	  spiceJetDriver=new FirefoxDriver();
	  spiceJetDriver.manage().window().maximize();
  }

  @AfterSuite
  public void afterSuite() {
	  spiceJetDriver.quit();
  }

}
