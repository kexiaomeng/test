package Protocol;

import java.util.concurrent.ConcurrentHashMap;

public class CC {

	private static ConcurrentHashMap<String,Integer> clearCode2LocalMap = new ConcurrentHashMap<String,Integer>();
	
	static{
		clearCode2LocalMap.put("113110", 1); //定义日冻结数据对应的fn号为1
	}
	
	public static Short getFnByCC(String cc){
		Integer val = clearCode2LocalMap.get(cc);
		if(val !=null){
			return val.shortValue();
		}
		return null;
	}
}
