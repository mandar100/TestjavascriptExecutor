package frames;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

// This class is used to 
// 1. Read the dimension data from the database
// 2. Format it according to KEyValue pair
// 3. Create/overwrite KeyValue pair by adding the dimension data to template file data
public class ReadDimensionData {
	
	public ReadDimensionData() {
		super();
		
	}

	@Test (description="Read dimension data from database")
	public static void getDimData(){
		// Get the vertical from base_config.prioperties
		Read_properties _base_config_properties = new Read_properties("base_config");
		String	sVertical = _base_config_properties.getPropertyValue("VERTICAL");

		try {
			//----- Read the template data
			String sFileName = System.getProperty("user.dir") + "\\config\\template\\keyvalue_" + sVertical + ".properties";
			System.out.println("File: " + sFileName);
			List<String> lsTemplateData = NomisUtils.GetFileContents(sFileName);
			lsTemplateData.add("# Dimension Data");
			//---------------------------------------------
			
			//----- Read the Dimension values from the database 
			Dataset _ds = new Dataset();
			List<String> lsData = new ArrayList<String>();
			String str = "select dd.id, dd.description, bd.displayname, bd.id from ns_dimension_def dd, ns_base_dimension bd where bd.dimensiondefid_fk in (select id from ns_dimension_def where type=2) and (bd.dimensiondefid_fk = dd.id) order by dd.id";
			lsData = _ds.getData(str);
			//System.out.println(lsData);
			lsData = FormatDimData(lsData);
			//---------------------------------------------
			
			// Append the dimension data to template data and write it into the KevValue file which will be used during runtime.
			lsTemplateData.addAll(lsData);
			lsTemplateData.add("#--------------------------------------------------------------------------------------------------------------------");
			sFileName = System.getProperty("user.dir") + "\\config\\keyvalue_" + sVertical + ".properties";
			NomisUtils.writeInFile(lsTemplateData,sFileName);
			//System.out.println("Query Output" + _ds.ExecQuery("Select * from NS_BASE_DIMENSION"));
			//System.out.println("Query Output" + _ds.ExecQuery("Select * from NS_DOCUMENT"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	//-----------------------------------------------------------------------------------
	// Author: Amol Harnule
	// Description: Formats the database data according to KeyValue pair(s). 
	// Parameter(s) :
	//		1. Database data
	// Return: Formatted KeyValue pair data
	//-----------------------------------------------------------------------------------
	public static List<String> FormatDimData(List<String> lsData){
		Integer i=0;
		Integer j=0;
		String sDimName = "";
		List<String> lsKeyValue = new ArrayList<String>();
		List<String> lsDimValue = new ArrayList<String>();
		
		for (String sData : lsData) {
			String[] lsDim = sData.toString().split(",");

			if (!sDimName.equalsIgnoreCase(lsDim[1])) {
				lsKeyValue.add("#---------------------------------------------");
				i++;
				sDimName = lsDim[1];
				//lsKeyValue.add("Dim" + i +"_ID"+ "=" + lsDim[0]);
				lsKeyValue.add("Dim" + i + "=" + sDimName);
				j=0;
				}
			j++;
		//lsKeyValue.add("Dim" + i + "_Val" + j + "_ID" + "=" + lsDim[3]);
		lsKeyValue.add("Dim" + i + "_Val" + j + "=" + lsDim[2]);
		lsDimValue.add("Dim" + i + "_Val" + j + "," + lsDim[3]);
		lsDimValue.add(lsDim[2] + "," + lsDim[3]);
		//lsDimValue.add(lsDim[0] + "," + lsDim[1] + "," + "Dim" + i + "_Val" + j + "," + lsDim[3]);
		//lsDimValue.add(lsDim[0] + "," + lsDim[1] + "," + lsDim[2] + "," + lsDim[3]);
		//System.out.println("Dim" + i + "_Val" + j + "," + lsDim[3]);
		//System.out.println(lsDim[2] + "," + lsDim[3]);
		}
		
		// ------- Creating Dimension_<Vertical>.csv ----------------------------
		Read_properties _base_config_properties = new Read_properties("base_config");
		String	sVertical = _base_config_properties.getPropertyValue("VERTICAL");
		String sFileName = System.getProperty("user.dir") + "\\config\\Dimension_" + sVertical + ".csv";
		NomisUtils.writeInFile(lsDimValue,sFileName);
		// ----------------------------------------------------------------------
		
		return lsKeyValue;
	}

	
}
