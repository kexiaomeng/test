
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

 

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jdom.Attribute;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
/**
 * 用于南网费控
 * 解析和生成xml字符串
 *
 */
public class Test1 {
	
	private static final String ROOT = "SOAP-ENV:Envelope";  
	private static final String BODY = "body";
	private static final String NAMESPACE = "http://gd.soa.csg.cn";
	private static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String SOAP_BODY = "SOAP-ENV:Body";
//private static Logger logger = LoggerFactory.getLogger(Test1.class);

    public Test1() {
        // TODO Auto-generated constructor stub
    }
    /**
     * 将发给webservice的数据组装为soap格式数据
     * @param methodName
     * @param collectionString
     * @param elementMap
     * @param flag
     * @return
     */
    public String createXML(String methodName,String collectionString,Map<String, Object> elementMap){
        String strXML = null;
        Document document = DocumentHelper.createDocument();
        
        /**
         * 不添加报文头的部分
         */
 //*****************************************不加报文头部分      
        Element root = document.addElement(methodName+"Request",NAMESPACE);  //根元素
       // root.addNamespace("", NAMESPACE);  //这种方式会出现子节点自带xlmns=" " 的情况，应避免

        Element collection = root.addElement(collectionString);
        
        for(Map.Entry<String,Object> element : elementMap.entrySet()){
        	String elementName = element.getKey();
        	Element keyElement = collection.addElement(elementName);
        	keyElement.addText(element.getValue().toString());
        }
        
        
//****************************************************************************
        
        /**
         * 添加报文头的部分
         */
 /************************************************************8
        String methodNameString = methodName+"Request";
        Element root = document.addElement(ROOT,SOAP_NAMESPACE);  //根元素
       // root.addNamespace("SOAP-ENV", SOAP_NAMESPACE);
        
        Element soapBodyElement = root.addElement(SOAP_BODY);
        Element methodNameRoot = soapBodyElement.addElement(methodNameString,NAMESPACE);//方法名
        //methodNameRoot.addNamespace("", NAMESPACE);

        Element collection = methodNameRoot.addElement(collectionString); 
       
        for(Map.Entry<String,Object> element : elementMap.entrySet()){ //参数
        	String elementName = element.getKey();
        	Element keyElement = collection.addElement(elementName);
        	keyElement.addText(element.getValue().toString());
        }
/********************************************************/

        //--------
        StringWriter strWtr = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        //format.setEncoding("UTF-8");
        XMLWriter xmlWriter =new XMLWriter(strWtr, format);
        try {
            xmlWriter.write(document);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        strXML = strWtr.toString();
        
        
        File file = new File("SFRZ.xml");  
        if (file.exists()) {  
            file.delete();  
        }
       // logger.debug("创建xml文件");
        try {
            file.createNewFile();
            XMLWriter out = new XMLWriter(new FileWriter(file));  
            out.write(document);  
            out.flush();  
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //--------------

        return strXML;
    }


    
    //把xml文件转换为map形式，其中key为有值的节点名称，并以其所有的祖先节点为前缀，用
	// * "."相连接。如：SubscribeServiceReq.Send_Address.Address_Info.DeviceType
    /**
	 * 
	 * 将xml转换为map形式
	 * @param xmlStr
	 *            xml内容
	 * @return Map 转换为map返回
	 */
	public  TreeMap<String, Object> xml2Map(String xmlStr) throws JDOMException, IOException {
		TreeMap<String, Object> rtnMap = new TreeMap<String, Object>();
		SAXBuilder builder = new SAXBuilder();
		org.jdom.Document doc = (org.jdom.Document) builder.build(new StringReader(xmlStr));
		// 得到根节点
		org.jdom.Element root = doc.getRootElement();
		String rootName = root.getName();
		rtnMap.put("root.name", rootName);
		// 调用递归函数，得到所有最底层元素的名称和值，加入map中
		convert(root, rtnMap, rootName);
		return rtnMap;
	}

	/**
	 * 递归函数，找出最下层的节点并加入到map中，由xml2Map方法调用。
	 * 
	 * @param e
	 *            xml节点，包括根节点
	 * @param map
	 *            目标map
	 * @param lastname
	 *            从根节点到上一级节点名称连接的字串
	 */
	public static void convert(org.jdom.Element e, Map<String, Object> map, String lastname) {
		if (e.getAttributes().size() > 0) {
			Iterator it_attr = e.getAttributes().iterator();
			while (it_attr.hasNext()) {
				Attribute attribute = (Attribute) it_attr.next();
				String attrname = attribute.getName();
				System.out.println(attrname);
				System.out.println("test");
				String attrvalue = e.getAttributeValue(attrname);
				// map.put( attrname, attrvalue);
				map.put(lastname + "." + attrname, attrvalue); // key 根据根节点 进行生成
			}
		}
		List children = e.getChildren();
		Iterator it = children.iterator();
		int i = 0;
		while (it.hasNext()) {
			org.jdom.Element child = (org.jdom.Element) it.next();
			/* String name = lastname + "." + child.getName(); */
			String name = child.getName();
			System.out.println(name);
			// 如果有子节点，则递归调用
			if (child.getChildren().size() > 0) {
				map.put(name, name);
				convert(child, map, lastname + "." + child.getName());
			} else {
				// 如果没有子节点，则把值加入map
				map.put(name, child.getText());
				// 如果该节点有属性，则把所有的属性值也加入map
				if (child.getAttributes().size() > 0) {
					Iterator attr = child.getAttributes().iterator();
					while (attr.hasNext()) {
						Attribute attribute = (Attribute) attr.next();
						String attrname = attribute.getName();
						String attrvalue = child.getAttributeValue(attrname);
						map.put(lastname + "." + child.getName() + "." + attrname, attrvalue);
					}
				}
			}
		}
		
	}

    /**
     * @param args
     * @throws IOException 
     * @throws JDOMException 
     */
    public static void main(String[] args) throws JDOMException, IOException {
        // TODO Auto-generated method stub
        Test1 handler = new Test1();
        Map<String, Object> keyMap = new HashMap<String,Object>();
        
        
        keyMap.put("CXFS", "1");
        keyMap.put("CXZ","0306000002900331");
        
        String strXML=handler.createXML("I_ZZFWZD_KHXXCX","YDKH_IN",keyMap);
        System.out.println(strXML);
        
       keyMap =  handler.xml2Map(strXML);
       for(Map.Entry<String,Object> map : keyMap.entrySet()){
    	   System.out.println("shuchu   "+map.getKey()+"  "+map.getValue());
       }
        System.out.println("-----------");
    }

}