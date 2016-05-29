package frames;

import org.openqa.selenium.WebDriver;

public interface IConfig {
	public WebDriver SetUp();
	public void TearDown();
}
