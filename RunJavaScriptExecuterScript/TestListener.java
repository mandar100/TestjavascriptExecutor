package frames;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestListener extends TestListenerAdapter{
	@Override
	public void onTestFailure(ITestResult tr){
		Config _config = new Config();
		_config.captureScreenShot(tr.getName());
	}

}
