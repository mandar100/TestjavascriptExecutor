package com.verifyBuild;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

public class TestTakeScreenShot {
	public static void takeSnap(WebDriver driver, String filePath) throws Exception{
		TakesScreenshot scrShot =((TakesScreenshot)driver);
		File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File DestFile = new File(filePath);
		FileUtils.copyFile(srcFile, DestFile);
	}

}
