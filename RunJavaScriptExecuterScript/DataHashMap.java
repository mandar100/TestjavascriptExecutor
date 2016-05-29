package frames;

import java.util.HashMap;
import java.util.List;

import org.testng.Assert;

public class DataHashMap<K,V> extends HashMap{
	
	@Override
	public List<String> get(Object key){
		List<String> ls = (List<String>)(super.get(key));
		if(ls.isEmpty())
			Assert.fail("Value for " + key + "is not found, Please check xls sheet");
		System.out.println("TestData: " + ls);
		return ls;
	}

}

