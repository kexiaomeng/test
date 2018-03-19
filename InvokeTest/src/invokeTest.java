import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class invokeTest {

		
		
		private String name;
		public String sex = "nan";
		
		volatile private static invokeTest in = null;
		public  static invokeTest getInstance(){
			if(in != null ){
				
			}else{
					synchronized (invokeTest.class) {
						if(in == null){
							invokeTest tmp = in;
							tmp = new invokeTest();
							in = tmp;
							
						}
					}
				}
			return in;
				
		
		}
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		

		
		public  invokeTest(){
			
		}
		public invokeTest(String name,String sex){
			this.name = name;
			this.sex  = sex;
		}
		
		
		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}
	
	public static void main(String[] args) throws Exception, NoSuchFieldException {
		Boolean flag = true;
		Class<?> classTypeClass = null;
		try {
			classTypeClass = Class.forName("invokeTest");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Field []name = classTypeClass.getDeclaredFields();
		Method [] methods = classTypeClass.getMethods();
		for(Field name1 : name){
			System.out.println(name1);
		}
		for(Method method1 : methods ){
			System.out.println("参数名称：     "+method1.getName());
			Class<?> returnTypeClass = method1.getReturnType();
			Class<?> []parament        = method1.getParameterTypes();
			int xx = method1.getModifiers();
			System.out.println(Modifier.toString(xx));
			System.out.println(returnTypeClass.getName());
			for(Class<?> p : parament){
				System.out.println(" 参数列表: "+p.getName());
			}
			
		}
		Method methods2 = classTypeClass.getMethod("setSex", String.class);
		System.out.println(methods2);
		System.out.println("构造方法");
		Constructor<?>[] cons = classTypeClass.getDeclaredConstructors();
		for(Constructor<?> con : cons){
			System.out.println(con);
		}
		Constructor<?> testConstructor = classTypeClass.getConstructor(String.class,String.class);
		
		
 		System.out.println("方法");
		Object ooo = testConstructor.newInstance("sm","man");
		Method meth = classTypeClass.getMethod("setSex", String.class);
		System.out.println("测试调用方法");
		meth.invoke(classTypeClass.newInstance(),"man" );
		Method meth2 = classTypeClass.getMethod("getSex");
		System.out.println(meth2.invoke(classTypeClass.newInstance()));
		//System.out.println(((Member) ooo).getName());
		
		invokeTest inv = new invokeTest();
		System.out.println("类加载器 :"+inv.getClass().getClassLoader().getClass().getName() );
		
		
	}
}
