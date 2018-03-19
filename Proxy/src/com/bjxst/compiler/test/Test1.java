package com.bjxst.compiler.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import com.bjxst.proxy.Moveable;
import com.bjxst.proxy.Tank;

public class Test1 {
	public static void main(String args[]) throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		String rtString = "\r\n";
		String src = "package com.bjxst.proxy;"+rtString+rtString+rtString+
		"public class TankTimeProxy implements Moveable {"+rtString+
			
			
			
			"private Moveable t;"+rtString+
			"public TankTimeProxy(Moveable t){"+rtString+
				"super();"+rtString+
				"this.t = t;"+rtString+
			"}"+rtString+
			
			"@Override"+rtString+
			"public void move() {"+rtString+
				// TODO Auto-generated method stub

				"long start = System.currentTimeMillis();"+rtString+
				"System.out.println(\"Tank moving\");"+rtString+
				"t.move();"+rtString+
				
				"long end = System.currentTimeMillis();"+rtString+
				"System.out.println(\"time:\"+(start-end));"+rtString+
			"}"+rtString+

			"@Override"+rtString+
			"public void stop() {"+rtString+
				// TODO Auto-generated method stub
				"long start = System.currentTimeMillis();"+rtString+
				"System.out.println(\"Tank moving\");"+rtString+
				"t.stop();"+rtString+
				
				"long end = System.currentTimeMillis();"+rtString+
				"System.out.println(\"time:\"+(start-end));"+rtString+
			"}"+rtString+

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
		Moveable m = (Moveable)constructor.newInstance(new Tank());
		m.move();
	}
}
