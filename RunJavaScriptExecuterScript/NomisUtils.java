package frames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import objectrepository.NomisBasePage;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.Reporter;

public class NomisUtils {


	private static final String CSV_SEPERATOR = ",";
	private static final String SKIP = "SKIP";
	private static final String BLANK = "BLANK";
	private static final double EPSILON_VALUE=0.001;
	public static int THREE = 3;
	public static int FIVE = 5;
	public static int TWENTY = 20;
	public static int THIRTY = 30;
	public static int FIFTY = 50;
	public static int EIGHTY = 80;

	// Author : Anand Somani
	// Date	: 15-Jan-2013
	// Purpose : Used to switch to new pop-up window depending upon Title
	// Input	: Driver, Title of pop-up window
	// Output 	: N/A
	public static void switchToWindowUsingTitle(WebDriver _driver, String title) { 
		String currentWindow = _driver.getWindowHandle(); 
		Set<String> availableWindows = _driver.getWindowHandles(); 
		if (!availableWindows.isEmpty()) { 
			for (String windowId : availableWindows) { 
				if (_driver.switchTo().window(windowId).getTitle().equals(title)) { 
					//return true; 
				} else { 
					_driver.switchTo().window(currentWindow); 
				} 
			} 
		} 
	} 

	// Author : Anand Somani
	// Date	: 18-Jan-2013
	// Purpose : Used to create a data map from xls sheet, which will get used in testcase to pass testdata
	// Input	: XLS file name
	// Output 	: Map
	public static DataHashMap<String, List<String>> createTestDataMap(String xlsFileName ){
		System.out.println("Creating Testdatamap from " + xlsFileName);
		DataHashMap<String, List<String>> map = new DataHashMap<String, List<String>>();

		try {
			FileInputStream file = new FileInputStream(xlsFileName);

			//Get the workbook instance for XLS file 
			HSSFWorkbook workbook = new HSSFWorkbook(file);

			//Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			//Read header record
			Row row = rowIterator.next();
			int columnCount = row.getPhysicalNumberOfCells();

			while(rowIterator.hasNext()) {
				row = rowIterator.next();
				List<String> values = new ArrayList<String>();

				//For each row, iterate through each columns
				//Iterator<Cell> cellIterator = row.cellIterator();

				String testCaseId = null;
				int cellIndex = 0;
				String cellValue = null;

				for(cellIndex=0;cellIndex<columnCount;cellIndex++)
				{
					Cell cell = row.getCell(cellIndex);

					if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
						values.add("");

					} else {
						if (cellIndex == 0) {
							testCaseId = cell.getStringCellValue();
						} else {
							cellValue = cell.getStringCellValue();
							/*if (cellValue.contains("#")){
								cellValue= Config.getData(cellValue);
							}*/
							if (cellValue.contains("#")){
								String[] cellVals = cellValue.split(",");
								cellValue="";
								for (int i = 0; i < cellVals.length; i++) {
									if (i>0)
										cellValue += ",";
									cellValue += Config.getData(cellVals[i]);
								}
							}

							values.add(cellValue);	
							//System.out.println("CellValue: " + cellValue);
						}

					}

				}
				map.put(testCaseId, values);
				//System.out.println(map.get(testCaseId));
			}
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Map: " + map);
		return map;
	}

	public static void selectValue(WebElement element, String search) throws NoSuchElementException{
		if ((search.length()<1) | (SKIP.equalsIgnoreCase(search))){
			return;
		}

		//System.out.println("Element: " + element.getTagName());
		List<WebElement> options = element.findElements(By.tagName("option"));

		boolean bSearch = false;
		String sGetValue = "";
		for (WebElement option : options) {
			sGetValue = option.getText();
			//System.out.println("List Option: " + sGetValue);
			//if(sGetValue.contains(search))
			if(sGetValue.contentEquals(search)){
				System.out.println("Selecting Option : " + sGetValue);
				option.click();
				bSearch = true;
				break;
			}else if(sGetValue.contains(search)){
				System.out.println("Selected Option (contains): " + sGetValue);
				option.click();
				bSearch = true;
				break;
			}
		}
		if(!bSearch){
			NomisUtils.logMessage(search + " : Application object not found", false);
			throw new NoSuchElementException(search + " : Application object not found");
		}
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It verifies if a value exists or not in the dropdown options
	// Parameter(s) : 
	//			element: the dropdown element
	//			search: Value to be searched
	// Return: Boolean
	//-----------------------------------------------------------------------------------
	public static Boolean verifyDropDownValue(WebElement element, String search) {
		if (search.length()<1){
			return true;
		}

		List<WebElement> options = element.findElements(By.tagName("option"));

		boolean bSearch = false;
		String sGetValue = "";
		for (WebElement option : options) {
			sGetValue = option.getText();
			if(sGetValue.equalsIgnoreCase(search)){
				System.out.println("Option '" + search + "' is available");
				bSearch = true;
				break;
			}else if(sGetValue.contains(search)){
				System.out.println("Option '" + search + "' is available");
				bSearch = true;
				break;
			}
		}
		if (!bSearch)
			logMessage("Option '" + search + "' is not available", true);

		return bSearch;
	}


	// Author 	: Anand Somani
	// Date		: 18-Jan-2013
	// Purpose : Used to capture row and column data from table
	// Input	: Table xpath
	// Output	: Row and column list
	public static List<String> captureTableData(WebElement tableElement){
		String elementTag = "Element: " + tableElement;
		if (elementTag.toLowerCase().contains("worksheetgrid")){
			try {
				if (Config._driver.findElement(By.id("pivotWorksheetGrid"))
						.isDisplayed()) {
					//WebElement element = Config._driver.findElement(By.xpath("//table[@id='gdRightTable']"));
					return capturePivotTableData(Config._driver.findElement(By
							.xpath("//div[@class='gdRightTableContainer']")));
				}
			} catch (Exception e) {
				// This verification is added for Pivot grid. So for other table data it generates exception.
			}
		}
		String sRowData = "";
		int trCount=0,tdCount=0;
		List<WebElement> trList = tableElement.findElements(By.tagName("tr"));
		List<String> lsFullData = new ArrayList<String>();
		System.out.println("Capturing the data from UI...");
		for(trCount=0;trCount<trList.size();trCount++){
			StringBuilder lsData = new StringBuilder();
			List<WebElement> tdList = trList.get(trCount).findElements(By.tagName("td"));
			int tdSize = tdList.size();
			for(tdCount=0;tdCount<tdSize;tdCount++){
				sRowData = tdList.get(tdCount).getText();
				sRowData = sRowData.replace("%", "");
				sRowData = sRowData.replace("$", "");
				sRowData = sRowData.replace(CSV_SEPERATOR, "");
				if(sRowData.length() > 0){
					lsData.append(sRowData);}
				if(tdCount != (tdSize-1)){
					lsData.append(CSV_SEPERATOR);
				}				
			}
			if (lsData.toString().length()>0) 
				lsFullData.add(lsData.toString());
			tdCount=0;
		}
		if(lsFullData.isEmpty())
			logMessage("No data is captured", false);
		return lsFullData;
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It captures Pivot Table data
	// Parameter(s) : Tabel Element
	// Return: Table data
	//-----------------------------------------------------------------------------------
	private static List<String> capturePivotTableData(WebElement tableElement){
		String sRowData = "";
		int trCount=0,tdCount=0;
		List<WebElement> trList = tableElement.findElements(By.tagName("tr"));
		List<String> lsFullData = new ArrayList<String>();
		System.out.println("Capturing the data from Pivot table...");
		for(trCount=0;trCount<trList.size();trCount++){
			StringBuilder lsData = new StringBuilder();
			List<WebElement> tdList = trList.get(trCount).findElements(By.tagName("td"));
			int tdSize = tdList.size();
			for(tdCount=0;tdCount<tdSize;tdCount++){
				sRowData = tdList.get(tdCount).getText();
				sRowData = sRowData.replace("%", "");
				sRowData = sRowData.replace("$", "");
				sRowData = sRowData.replace(CSV_SEPERATOR, "");
				lsData.append(sRowData);
				if(tdCount != (tdSize-1)){
					lsData.append(CSV_SEPERATOR);
				}				
			}
			lsFullData.add(lsData.toString());
			tdCount=0;
		}
		if(lsFullData.isEmpty()){Assert.fail("No data is captured");}
		return lsFullData;
	}


	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It waits for grid render and then captures grid data and stores in a file
	// Parameter(s) : 
	//		element: Grid element
	//		sFileName: Filename to store the data
	// Return: Table data
	//-----------------------------------------------------------------------------------
	public static void captureGridData(WebElement element, String sFileName){
		NomisBasePage.waitForGridRender();
		List<String> lsGridData = captureTableData(element);
		sFileName = getResultFileName(sFileName);
		writeInFile(lsGridData, sFileName);
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It captures table data and stores in a file
	// Parameter(s) : 
	//		element: Table element
	//		sFileName: Filename to store the data
	// Return: Table data
	//-----------------------------------------------------------------------------------
	public static void saveTableData(WebElement element, String sFileName){
		List<String> lsGridData = captureTableData(element);
		sFileName = getResultFileName(sFileName);
		writeInFile(lsGridData, sFileName);
	}

	// Author 	: Anand Somani
	// Date		: 18-Jan-2013
	// Purpose : Used to capture row and column data from table
	// Input	: Row and column list, Expected CSV file name to store data 
	// Output	: N/A
	public static void writeInFile(List<String> lsFullData, String fileName){
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(fileName);
			OutputStreamWriter out = new OutputStreamWriter(fos);
			for(int i=0;i<lsFullData.size();i++){
				out.append(lsFullData.get(i).toString()+"\n");
				//System.out.println(lsFullData.get(i));
			}
			out.close();
			fos.close();
			System.out.println("Data is captured in the file: " + fileName);
		} catch (FileNotFoundException e) {
			Assert.fail(fileName + " File not found");
			e.printStackTrace();
		} catch (IOException e) {
			Assert.fail(fileName + " unable to read/write in file");
			e.printStackTrace();
		}
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It returns the absolute path of the filename depending upon CAPTURE mode
	// Parameter(s) : 
	//		sFileName: Filename to store the data
	// Return: Absolute filename path 
	//-----------------------------------------------------------------------------------
	public static String getResultFileName(String sFileName){
		// Check if the filename already contains absolute path
		if (sFileName.contains("\\results\\") || sFileName.contains(":")){
			return sFileName;
		}

		// Generate the filename depending upon the mode Capture or Verify
		if (Config.bCapture) {
			sFileName = Config.getPropertiesData("RESULT_LOCATION")+ "\\Expected\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName + ".csv";
		}else{
			sFileName = Config.getPropertiesData("RESULT_LOCATION")+ "\\Actual\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName + ".csv";
		}
		return sFileName;
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It sleeps for 10 seconds
	// Parameter(s) : NA
	// Return: NA
	//-----------------------------------------------------------------------------------
	public static void sleepTime(){
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It sleeps for user defined time in seconds
	// Parameter(s) : seconds to pause the execution
	// Return: NA
	//-----------------------------------------------------------------------------------
	public static void sleepTime(long sec){
		try {
			System.out.println("Waiting for " + sec + " secs...");
			sec *= 1000;
			Thread.sleep(sec);
			System.out.println("Wait over..");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It compares the CSVs.Generally the grid values are captured in CSVs, so it compares the values with Epsilon 0.001
	// Parameter(s) : Filename containing the data
	// Return: Boolean
	//-----------------------------------------------------------------------------------
	public static boolean compareCSV(String sFileName){
		boolean bMatch = true;
		sFileName+=".csv";
		if (compareFullCSVs(sFileName))
			return bMatch;
		
		String expectedFileName = Config.getPropertiesData("RESULT_LOCATION")+ "\\Expected\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName;// + ".csv";
		String actualFileName = Config.getPropertiesData("RESULT_LOCATION")+ "\\Actual\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName;// + ".csv";
		

		try {
			File expectedFile = new File(expectedFileName);
			File actualFile = new File(actualFileName);

			if (!expectedFile.exists()){
				logMessage("File not found: " + expectedFileName, false);
				return false;
			}
			if (!actualFile.exists()){
				logMessage("File not found: " + actualFileName, false);
				return false;
			}
			List<String> expectedData = FileUtils.readLines(expectedFile);
			List<String> actualData = FileUtils.readLines(actualFile);

			for (Integer i=0;i<expectedData.size();i++) {
				String expData = expectedData.get(i);
				String actData = actualData.get(i);
				String[] lsExp = expData.split(CSV_SEPERATOR);
				String[] lsAct = actData.split(CSV_SEPERATOR);

				for (Integer j=0;j<lsExp.length;j++){
					if (!lsExp[j].contentEquals(lsAct[j])){
						try{
							double dExp = Double.parseDouble(lsExp[j]);
							double dAct = Double.parseDouble(lsAct[j]);


							if (dExp!=dAct){
								if (Math.abs((dExp-dAct)/dExp) > EPSILON_VALUE){
									logMessage("Expected : " + dExp + ",  Actual: " + dAct, false);
									bMatch = false;
								}
							}
						}catch(NumberFormatException e){
							logMessage("Expected : " + lsExp[j] + ",  Actual: " + lsAct[j], false);
							bMatch = false;
						}
					}
				}
			}

		}catch (IOException e) {
			e.printStackTrace();
			logMessage(e.getMessage(), false);
			bMatch = false;
		}
		if (bMatch) {
			logMessage("*** PASS: Expexcted and Actual data matched",true);
		}else{
			logMessage("*** FAIL: Expexcted and Actual data NOT matched", false);
		}
		return bMatch;
	}

	// Author 	: Anand Somani
	// Date		: 23-Jan-2013
	// Purpose : Used to compare 2 CVS file
	// Input	: Filename
	// Output	: Comparison result.
	/*public static boolean compareFullCSVs(String sFileName){
		String expectedFileName = System.getProperty("user.dir") + "\\Results\\Expected\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName;
		String actualFileName = System.getProperty("user.dir") + "\\Results\\Actual\\" + Config.getPropertiesData("VERTICAL")+ "\\"+sFileName;
		boolean bMatch = true;

		try {
			File expectedFile = new File(expectedFileName);
			File actualFile = new File(actualFileName);
			if (!expectedFile.exists()){
				System.err.print("File not found: " + expectedFileName);
				return false;
			}
			if (!actualFile.exists()){
				System.err.print("File not found: " + actualFileName);
				return false;
			}
			List<String> expectedData = FileUtils.readLines(expectedFile);
			List<String> actualData = FileUtils.readLines(actualFile);

			expectedData.removeAll(actualData);

			if(expectedData.size()>0){
				//System.out.println("Additional Expected Data: " + expectedData);
				return false;
			}

			expectedData = FileUtils.readLines(expectedFile);
			actualData.removeAll(expectedData); 
			if(actualData.size()>0){
				//System.out.println("Additional Actual Data: " + actualData);
				return false;
			}

		}catch (IOException e) {
			e.printStackTrace();
			bMatch = false;
		}
		if (bMatch)
			logMessage("*** PASS: Expexcted and Actual data matched",bMatch);
		return bMatch;
	}
	*/
	public static boolean compareFullCSVs(String sFileName){
		String expectedFileName = System.getProperty("user.dir") + "\\Results\\Expected\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName;
		String actualFileName = System.getProperty("user.dir") + "\\Results\\Actual\\" + Config.getPropertiesData("VERTICAL")+ "\\"+sFileName;
		boolean bMatch = true;
		String sExp = "",sAct="";
		try {
			File expectedFile = new File(expectedFileName);
			File actualFile = new File(actualFileName);
			if (!expectedFile.exists()){
				System.err.print("File not found: " + expectedFileName);
				return false;
			}
			if (!actualFile.exists()){
				System.err.print("File not found: " + actualFileName);
				return false;
			}
			List<String> expectedData = FileUtils.readLines(expectedFile);
			List<String> actualData = FileUtils.readLines(actualFile);

			expectedData.removeAll(actualData);

			if(expectedData.size()>0){
				//System.out.println("Additional Expected Data: " + expectedData);
				if (expectedData.size()==1){
					if (expectedData.get(0).contains("Baseline:"))
						sExp=expectedData.get(0);
				}else
					return false;
			}

			expectedData = FileUtils.readLines(expectedFile);
			actualData.removeAll(expectedData); 
			if(actualData.size()>0){
				//System.out.println("Additional Actual Data: " + actualData);
				if (actualData.size()<3){
					if (actualData.get(0).contains("Baseline:"))
						sAct=actualData.get(0);
				}else
					return false;
			}
			String[]expData = sExp.split(":");
			String[]actData = sAct.split(":");
			for (int i = 0; i < (expData.length-1); i++) {
				if (!expData[i].equalsIgnoreCase(actData[i])){
					System.out.println("Exp: " + expData[i] + ", Act: " + actData[i]);
					bMatch=false;

				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			bMatch = false;
		}
		if (bMatch)
			logMessage("*** PASS: Expexcted and Actual data matched",bMatch);
		return bMatch;
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It compares the data from two CSV expected VS Actual
	// Parameter(s) : sFileName: name of the file
	// Return: Boolean
	//-----------------------------------------------------------------------------------
	public static boolean compareData(String sFileName){
		String expectedFileName = Config.getPropertiesData("RESULT_LOCATION")+ "\\Expected\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName + ".csv";
		String actualFileName = Config.getPropertiesData("RESULT_LOCATION")+ "\\Actual\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName + ".csv";
		boolean bMatch = true;


		File expectedFile = new File(expectedFileName);
		File actualFile = new File(actualFileName);

		if (!expectedFile.exists()){
			logMessage("File not found: " + expectedFileName, false);
			return false;
		}
		if (!actualFile.exists()){
			logMessage("File not found: " + actualFileName, false); 
			return false;
		}
		List<String> expectedData,actualData;
		try {
			expectedData = FileUtils.readLines(expectedFile);
			actualData = FileUtils.readLines(actualFile);
		} catch (IOException e) {
			e.printStackTrace();
			Reporter.log(e.getMessage());
			return false;

		}

		for (int i = 0; i < expectedData.size(); i++) {
			String[] expData = expectedData.get(i).split(CSV_SEPERATOR);
			String[] actData = actualData.get(i).split(CSV_SEPERATOR);
			String expectedValues="";
			String actualValues="";

			for (int j = 0; j < expData.length; j++) {
				if (expData[j].equalsIgnoreCase(actData[j])==false){
					expectedValues += expData[j] + CSV_SEPERATOR;
					actualValues += actData[j] + CSV_SEPERATOR;
					bMatch = false;
				}

			}
			if (expectedValues.length() > 0) {
				logMessage("Expected Values (Row " + i + "): " + expectedValues, true);
				logMessage("Actual Values (Row " + i + "): "  + actualValues, true);
			}
		}
		if (bMatch) {
			System.out.println("*** PASS: Expexcted and Actual data matched");
		}else{
			logMessage("*** FAIL: Expexcted and Actual data NOT matched", false);
		}
		return bMatch;
	}



	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: captures the grid data and compare it against expected
	// Parameter(s) :
	//		1. element: This is the Scenario Grid object
	//		2. sFileName: This is the file name to be used to store/lookup Expected as well as Actual data.
	// Return: True/False
	//-----------------------------------------------------------------------------------
	public static boolean verifyGridData(WebElement element, String sFileName){

		System.out.println("Verify Grid Data for " + sFileName);
		String sExpected = Config.getPropertiesData("RESULT_LOCATION")+ "\\Expected\\" + Config.getPropertiesData("VERTICAL") +"\\"+ sFileName + ".csv";
		File fExpected = new File(sExpected);

		// Get the file name depending upon mode: Capture/Verify
		String sActual = NomisUtils.getResultFileName(sFileName);

		// Get the element declaration and verify if it contains worksheetgrid.
		// for worksheetgrid call different utility else call another one.
		String elementTag = "Element: " + element;
		if (elementTag.toLowerCase().contains("worksheetgrid"))
			NomisUtils.captureGridData(element, sActual);
		else
			NomisUtils.saveTableData(element, sActual);

		// For Capture mode, the data is already captured so return back. 
		if (Config.bCapture) {
			return true;
		}else{
			// In verify mode, check if expected result file is available or not.
			// If not, then capture the expected result and return false.
			if (!fExpected.exists()){
				logMessage("File not found: " + sExpected, false);
				if (!CopyFile(sActual,sExpected)){
					NomisUtils.captureGridData(element, sExpected);
				}
				return false;
			}

		}
		// Return the comparison result
		return compareCSV(sFileName);	
	}

	public void EditGridData (WebElement element,String sDim, String sVal){
		String[] lsDim = sDim.toString().split(CSV_SEPERATOR);
		for (int iCnt=0; iCnt < lsDim.length;iCnt++){
			//System.out.println(lsDim[iCnt]);
		}
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It clicks on the lement and then selects the value from the dropdown.
	//		Ex. Global assumption selection from a category
	// Parameter(s) : 
	//		clickElement: Element to be clicked
	//		selectElement: Element to be selected from the dropdown
	// Return: NA
	//-----------------------------------------------------------------------------------
	public static void ClickAndSelect(WebElement clickElement, WebElement selectElement){
		Actions action = new Actions(Config._driver);
		action.moveToElement(clickElement).click(clickElement).click(selectElement).build().perform();
		NomisUtils.sleepTime();
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It clicks on the element and then selects the value from the dropdown.
	//		Ex. Global assumption selection from a category
	// Parameter(s) : 
	//		clickElement: Element to be clicked
	//		selectElement: Element to be selected from the dropdown
	// Return: NA
	//-----------------------------------------------------------------------------------
	public static void ClickAndSelectNoWait(WebElement clickElement, WebElement selectElement){
		Actions action = new Actions(Config._driver);
		action.moveToElement(clickElement).click(clickElement).click(selectElement).build().perform();
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It is used to copy the file
	// Parameter(s) :
	//		1. source: File to copy
	//		2. destination: The new file name
	// Return: True/False
	//-----------------------------------------------------------------------------------
	public static Boolean CopyFile(String source, String destination){
		Runtime rt=Runtime.getRuntime(); 
		Process p=null;
		try {
			p=rt.exec("cmd /c copy " + source + " " + destination);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} 
		try {
			p.waitFor();
		} catch (InterruptedException e1) {

			e1.printStackTrace();
		}
		System.out.println("Successfully copied the file: " + destination);
		return true;
	}


	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It is used to get the contents of a file (mostly text like .properties)
	// Parameter(s) :
	//		1. sFileName: File to read
	// Return: The data of the file
	//-----------------------------------------------------------------------------------
	public static List<String> GetFileContents(String sFileName){
		System.out.println("Reading file: " + sFileName);
		List<String> fileData = new ArrayList<String>();
		try {
			File readFile = new File(sFileName);

			if (!readFile.exists()){
				System.err.println("File not found: " + sFileName);
				Assert.fail();
			}

			fileData = FileUtils.readLines(readFile);
		}catch (IOException e) {
			e.printStackTrace();
			Assert.fail();

		}
		return fileData;
	}


	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It is used to enter the data after clearing the contents of text field.
	// Parameter(s) : Element and the data to be entered
	// Return: VOID
	//-----------------------------------------------------------------------------------
	public static void ClearAndEnterText(WebElement wElement, String sData){
		if (SKIP.equalsIgnoreCase(sData))
			return;
		wElement.clear();
		if (BLANK.equalsIgnoreCase(sData))
			return;
		wElement.sendKeys(sData);
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It first checks if the new value is blank or not.  
	// Parameter(s) : Element and the data to be entered
	// Return: VOID
	//-----------------------------------------------------------------------------------
	public static void EnterData(WebElement wElement, String sData){
		sData = sData.trim();
		if ((sData.length()<1) | (SKIP.equalsIgnoreCase(sData)))
			return;
		wElement.clear();
		if (BLANK.equalsIgnoreCase(sData))
			return;
		wElement.sendKeys(sData);
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: prints as well as log the message in result file.  
	// Parameter(s) : Message
	// Return: VOID
	//-----------------------------------------------------------------------------------
	public static void logMessage(String sMessage, Boolean bSuccess){
		if (bSuccess)
			System.out.println(sMessage);
		else
			System.err.println(sMessage);
		Reporter.log(sMessage);
	}
	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: Used to click on expand icon (Ex. Biz rule scope).  
	// Parameter(s) : 
	//			parentElement: The element containing all the expand icons
	//			iMageTag: Identifier for the expand image as it is different for different pages
	// Return: VOID
	//-----------------------------------------------------------------------------------
	public static void expandAllItems(WebElement parentElement, String imageTag){
		List<WebElement> lsImages = parentElement.findElements(By.xpath(imageTag));
		if (lsImages.size() > 0){
			for (WebElement element : lsImages) {
				element.click();
			}
			NomisUtils.sleepTime(NomisUtils.THREE);
		}
	}


	public static void checkFolders(){
		String resultLoc= Config.getPropertiesData("RESULT_LOCATION");
		String vertical = Config.getPropertiesData("VERTICAL");
		File file = new File(resultLoc + "\\Expected\\" + vertical);

		if (!file.exists())
			file.mkdir();
		file = new File(resultLoc + "\\Actual\\" + vertical);
		if (!file.exists())
			file.mkdir();
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: It is used to get the text length.
	// Parameter(s) : Text element
	// Return: Integer
	//-----------------------------------------------------------------------------------
	public static Integer getTextLength(WebElement element){
		return element.getAttribute("value").length();
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: Used to refresh the page
	// Parameter(s) : NA
	// Return: VOID
	//-----------------------------------------------------------------------------------
	public static void refreshPage() {
		Config._driver.navigate().refresh();
		sleepTime();
	}
}

