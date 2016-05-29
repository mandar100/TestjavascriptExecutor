package frames;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Read_properties {

	public final static String PROPERTY_FILENAME = "config/base_config.properties";
	private Properties config_properties = new Properties();
  
	
	/**
     * Loads the properties file
	 * @param string 
     */
    public Read_properties(String sFileName) {
        try {
        	config_properties.load(new FileInputStream("config/"+sFileName+".properties"));
        } catch (IOException e) {
        	System.out.println("Unable to load " + sFileName+ ".properties");
		throw new RuntimeException(e);
        }

        assert !config_properties.isEmpty();
    }

    
    
    /**
     * returns the value of the property denoted by key
     * 
     * @param key
     * @return
     */
    public String getPropertyValue(final String key) {
        String property = config_properties.getProperty(key);
        if (property == null){
        	System.err.println("No value found for key: " + key);
        }
        	
        return property != null ? property.trim() : property;
    }


	public String getPropertyValue(Properties _rProperties,final String key) {
        String property = _rProperties.getProperty(key);
        return property != null ? property.trim() : property;
    }
    

}
