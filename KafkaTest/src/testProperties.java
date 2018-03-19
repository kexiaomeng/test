import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class testProperties {
	public static void main(String[] args) throws Exception {
		Map<String, String> proMap = null;
		Properties properties = new Properties();
		Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(System.getProperty("user.dir")+File.separator + "src\\kafkaProducer.properties"))));
		properties.load(reader);
		
		proMap = getPropertyMap(properties);
		
		System.out.println("start iterator");
		int i =0;
		for(Map.Entry<String, String> entry:proMap.entrySet()){
			System.out.println(i++);
			System.out.println("key:  "+entry.getKey()+"  value:"+entry.getValue());
		}
	}
	public static Map<String, String> getPropertyMap(Properties properties){
		Map<String,String> propertiesMap = new HashMap<String, String>();
		String valString = null;
		for(Object key : properties.keySet()){
			valString = properties.getProperty((String) key);
			if(valString != null){
				propertiesMap.put((String) key, valString);
				System.out.println(key+"------"+valString);
			}
		}
		return propertiesMap;
	}
}
