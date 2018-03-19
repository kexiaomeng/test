package com.bjxst.proxy;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

public class Proxy {
	public static Object newProxyInstance(Class inface,InvocationHandler h) throws Exception{
		
		
		String rtString = "\r\n";
		String methodString ="";
		
		Method[] methods = inface.getMethods();
		/*
		for(Method method:methods){
			methodString += "@Override"+rtString+"public void  "+method.getName()+"(){"+rtString+
			"long start = System.currentTimeMillis();"+rtString+
			"System.out.println(\"Tank moving\");"+rtString+
			"t."+method.getName()+"();"+rtString+
			
			"long end = System.currentTimeMillis();"+rtString+
			"System.out.println(\"time:\"+(start-end));"+rtString+
			"}";
		}*/
		
		for(Method method:methods){
			methodString += "@Override"+rtString+"public void  "+method.getName()+"(){"+rtString+
			"h.invoke(this,"+method+")"+";"+rtString+
			"}";
		}
		
		
		String src = "package com.bjxst.proxy;"+rtString+rtString+rtString+
		"public class TankTimeProxy implements Moveable {"+rtString+
			
			
			
			"private Moveable t;"+rtString+
			"public TankTimeProxy(Moveable t){"+rtString+
				"super();"+rtString+
				"this.t = t;"+rtString+
			"}"+rtString+
			
			methodString+

		"}";
		
		String filenameString = System.getProperty("user.dir")
		+"/src/com/bjxst/proxy/TankTimeProxy.java";
		
		File file = new File(filenameString);
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(src);
		fileWriter.flush();
		fileWriter.close();
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable units = fileManager.getJavaFileObjects(filenameString);
		CompilationTask task = compiler.getTask(null, fileManager, null, null, null, units);
		task.call();
		fileManager.close();
		
		URL[] urls = new URL[]{new URL("file:/"+System.getProperty("user.dir")+"/src")};
		URLClassLoader ul = new URLClassLoader(urls);
		Class class1 = ul.loadClass("com.bjxst.proxy.TankTimeProxy");
		System.out.println(class1);
		Constructor constructor = class1.getConstructor(Moveable.class);
		Object m = (Object)constructor.newInstance(new Tank());
		//m.move();
		//return null;
		return m;
	}
}
