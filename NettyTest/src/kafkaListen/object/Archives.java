package kafkaListen.object;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Archives implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8300918062248240220L;
	
	private String terminalAddr;
	List<String> list = new ArrayList<String>();
	
	Map<String, String> map = new HashMap<String,String>();
	
 	
	public List<String> getList() {
		map.put("hello", "world");
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
		
	}

	private Byte optType;

	
	public void setOptType(Byte optType) {
		this.optType = optType;
	}

	public Byte getOptType() {
		return optType;
	}

	public String getTerminalAddr() {
		return terminalAddr;
	}

	public void setTerminalAddr(String terminalAddr) {
		this.terminalAddr = terminalAddr;
	}
	
}
