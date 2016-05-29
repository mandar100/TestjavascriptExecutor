package frames;

import org.openqa.selenium.NoSuchElementException;

public interface BaseElement {
	
	 abstract public boolean onPage() throws NoSuchElementException;

}