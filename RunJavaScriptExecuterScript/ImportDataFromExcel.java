package com.verifyBuild;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ImportDataFromExcel {
	static Sheet sensorData;
	static Workbook workbook =null;
	public ImportDataFromExcel(String excelFilePath) throws BiffException, IOException {
		workbook=Workbook.getWorkbook(new File(excelFilePath));
		sensorData=workbook.getSheet("sensor_data");		
		
	}
	public static int getsensorDataRowCount()
	{
		return sensorData.getRows();
	}
	public static String sensorDataReadCell(int column,int row)
    {
        return sensorData.getCell(column,row).getContents();
    }
	public static String sensorDataReadCell(String columnName)
    {
        return sensorData.getCell(columnName).getContents();
    }

}
