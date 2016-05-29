package xls;

import java.util.List;
import java.util.Map;



import frames.NomisUtils;

public class XlsReading {
	static Map<String, List<String>> _baselineXLSMap = null;
	static Map<String, List<String>> _scenarioXLSMap = null;
	
	enum baseline {  
		NAME(1),  
		DESCRIPTION(2);  
	    private int value;  
	    private baseline(int value) {  
	        this.value = value;  
	    }  
	    int value() {  
	        return value;  
	    } 
	}
	
	
	public static void main(String[] args){  
		_baselineXLSMap = NomisUtils.createTestDataMap("\\\\ps7342\\Automation\\NPO\\TestData\\Baseline.xls");
		//_scenarioXLSMap = createTestDataMap("\\\\ps7342\\Automation\\NPO\\TestData\\Scenario.xls");
		testcase("Baseline_003");
    }

private static void testcase(String testDataID){
	List<String> testData = _baselineXLSMap.get(testDataID);
	System.out.println("Test Result : "+ testData.get(baseline.NAME.value));

}
}

 
