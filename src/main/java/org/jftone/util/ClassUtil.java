package org.jftone.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jftone.config.Const;
import org.jftone.exception.CommonException;

/**
 * 类相关的工具类
 */
public final class ClassUtil {
	
	/**
	 * 从包package中循环迭代获取所有的class
	 * @param packageName
	 * @return
	 * @throws CommonException 
	 */
	public static List<String> getClasses(String packageName) throws CommonException {
		return getClasses(packageName, true);
	}

	/**
	 * 从包package中获取所有的class
	 * @param packageName
	 * @param recursive		是否循环迭代
	 * @return
	 * @throws CommonException 
	 */
	public static List<String> getClasses(String packageName, boolean recursive) throws CommonException {
		List<String> classes = new ArrayList<String>();
		Enumeration<URL> dirs;			// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		try {
			if (packageName == null || !packageName.matches("[\\w]+(\\.[\\w]+)*")) {
		         throw new IllegalArgumentException("非法的包名");
			}
			String packageDirName = packageName.replace('.', '/');		//获取包的名字 并进行替换
			
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();		//得到协议的名称
				if ("file".equals(protocol)) {		//class文件形式
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), Const.CHARSET_UTF8);
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findClassByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {		//jar文件
					// 如果是jar包文件, 定义一个JarFile
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					findClassByJar(packageName, jar, classes);
				}
			}
		} catch (IOException e) {
			throw new CommonException(e);
		}
		return classes;
	}
	
	/**
	 * 
	 * @param packageName
	 * @param jar
	 * @param classes
	 */
	public static void findClassByJar(String packageName, JarFile jar, List<String> classes) {
		String packageDirName = packageName.replace('.', '/');
		// 从此jar包 得到一个枚举类
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			//如果是以/开头的
			if (name.charAt(0) == '/') {
				name = name.substring(1);		// 获取后面的字符串
			}
			// 如果前半部分和定义的包名相同
			if (name.startsWith(packageDirName)) {
				// 如果是一个.class文件 而且不是目录
				if (name.endsWith(".class") && !entry.isDirectory()) {
					int idx = name.lastIndexOf('/');
					packageName = name.substring(0, idx).replace('/', '.');
					// 去掉后面的".class" 获取真正的类名
					String className = name.substring(idx+1, name.length()-6);
					classes.add(packageName + '.'+ className);
				}
			}
		}
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findClassByFile(String packageName,
			String packagePath, final boolean recursive, List<String> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory())
						|| (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findClassByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length()- 6);
				classes.add(packageName + '.' + className);
			}
		}
	}
}
