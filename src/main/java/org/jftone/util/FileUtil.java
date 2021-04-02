package org.jftone.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jftone.config.Const;

public final class FileUtil {
	private static Logger log = LoggerFactory.getLogger(FileUtil.class);
    /**
     * 读取文本文件信息
     * @param file 文件名
     * @param encoding 编码，默认为UTF-8
     * @return 文件内容
     * @throws IOException
     */
    public static String readFile(String file, String encoding) throws IOException {
        StringBuffer str = new StringBuffer("");
        String st = "";
        try {
            FileInputStream fs = new FileInputStream(file);
            InputStreamReader isr;
            if (encoding == null || encoding.equals("")) {
                isr = new InputStreamReader(fs, Const.CHARSET_UTF8);
            } else {
                isr = new InputStreamReader(fs, encoding);
            }
            BufferedReader br = new BufferedReader(isr);
            try {
                String tmp = "";
                while ((tmp = br.readLine()) != null) {
                    if (tmp.trim().length() > 0) {
                        str.append(tmp + "\r\n");
                    }
                }
            } catch (Exception e) {
                str.append(e.toString());
            }
            st = str.toString();
            br.close();
            isr.close();
            fs.close();
        } catch (IOException es) {
            st = "";
        }
        return st;
    }
    
    public static String readFile(String file) throws IOException {
        return readFile(file, null);
    }
    
    /**
     * 写入文件
     * @param file		文件路径及名称
     * @param content	写入内容
     * @param addFlag	是否追加         默认为追加
     * @param encoding	编码，默认为UTF-8
     * @return
     * @throws IOException
     */
    public static boolean writeFile(String file, String content, boolean addFlag, String encoding) throws IOException {
        boolean result = false;
    	try {
    		File f = new File(file);
    		if(!f.exists()){
    			f.createNewFile();
    		}
        	FileOutputStream fs = new FileOutputStream(file, addFlag); 
        	OutputStreamWriter oswr = null;
        	if(encoding == null || encoding.equals("")){
        		oswr = new OutputStreamWriter(fs, Const.CHARSET_UTF8);
        	}else{
        		oswr = new OutputStreamWriter(fs,encoding);
        	}
        	BufferedWriter writer = new BufferedWriter(oswr);
        	
        	StringReader sr = new StringReader(content);
        	BufferedReader br = new BufferedReader(sr);
        	String tmp = "";
        	while((tmp = br.readLine())!=null){
        		writer.write(tmp);
        		writer.newLine();
        	}
        	writer.flush();
        	writer.close(); 
        	br.close();
        	oswr.close();   
        	fs.close(); 
        	result = true;
            
        } catch (IOException es) {
        	es.printStackTrace();
        }
        return result;
    }
    public static boolean writeFile(String file, String content, boolean addFlag) throws IOException {
        return writeFile(file, content, addFlag, null);
    }
    public static boolean writeFile(String file, String content) throws IOException {
        return writeFile(file, content, true, null);
    }
    
    /**
     * 删除文件
     * @param file
     * @return
     */
    public static boolean delFile(String file){
    	File fileObj = new File(file);
    	if (fileObj.exists()) {
    		fileObj.delete();
    		return true;
    	}
    	return false;
    }

    /**
     * 查找源代码根目录下文件
     * @param configFile
     * @return
     * @throws FileNotFoundException 
     */
    public static File loadClasspathFile(String configFile) throws FileNotFoundException {
		if(null == configFile || "".equals(configFile)){
			return null;
		}
		String filePath = Thread.currentThread().getContextClassLoader().getResource(configFile).getPath();
		File file = new File(filePath);
		if(!file.exists()){
			//如果文件不存在，则在jar对应所在目录查找
			String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			if(!path.endsWith(File.separator)){
				path = path.substring(0, path.lastIndexOf(File.separator)+1);
			}
			file = new File(path + configFile);
			if(!file.exists()){
				log.error("文件["+file.getPath()+"]不存在");
				throw new FileNotFoundException("文件["+file.getPath()+"]不存在");
			}
		}
		return file;
	}
    
    /**
     * 加载配置Properties文件
     * @param configFile
     * @return 返回一个Properties对象
     * @throws IOException
     */
    public static Properties loadClasspathProperties(String configFile) throws IOException {
    	File file = loadClasspathFile(configFile);
    	if(null == file) {
    		return null;
    	}
    	InputStream inStream = new FileInputStream(file);
    	Properties properties = new Properties();
    	properties.load(inStream);
    	if(null != inStream) {
    		inStream.close();
    		inStream = null;
    		file = null;
    	}
    	return properties;
    }
    
    /**
     * 加载配置文件
     * @param configFile
     * @return 返回一个DataMap的对象
     * @throws IOException
     */
    public static IData<String, Object> loadClasspathPropsData(String configFile) throws IOException {
    	IData<String, Object> retData = new DataMap<>();
    	Properties props = loadClasspathProperties(configFile);
    	if(null != props && !props.isEmpty()) {
    		for(Map.Entry<Object, Object> entry : props.entrySet()) {
    			retData.put((String) entry.getKey(), entry.getValue());
    		}
    	}
    	return retData;
    }
    
    /**
	 * 加载并解析XML数据
	 * <jftone>
	 *	    <poolSize>5</poolSize>
	 *	    <timeout>1</timeout>
	 *		<hosts>
	 *			<host>
	 *	  			<ip>127.0.0.1</ip>
	 *	  			<port>11211</port>
	 *	  			<weight>1</weight>
	 *	  		</host>
	 *			<host>
	 *	  			<ip>127.0.0.2</ip>
	 *	  			<port>11211</port>
	 *	  			<weight>1</weight>
	 *	  		</host>
	 *	  	</hosts>
	 *	</jftone>
	 * @param xmlConfFile
	 * @return
     * @throws FileNotFoundException 
	 */
    @SuppressWarnings("unchecked")
	public static IData<String, Object> loadClasspathXMLData(String xmlConfFile) throws FileNotFoundException {
		File file = loadClasspathFile(xmlConfFile);
    	if(null == file) {
    		return null;
    	}
    	IData<String, Object> data = new DataMap<String, Object>();
    	try {
    		SAXReader reader = new SAXReader();
    		Element rootEl = reader.read(file).getRootElement();
    		List<Element> props = rootEl.elements();
    		if (null == props || props.isEmpty()) {
    			return null;
    		}
    		for(Element prop : props) {
    			if(prop.isTextOnly()) {
    				data.put(prop.getName(), prop.getTextTrim());
    			}else {
    				data.put(prop.getName(), getXmlElementList(prop));
    			}
    		}
    	}catch(DocumentException e) {
    		
    	}
    	return data;
	}
    
    /**
     * 解析XML节点下List节点数据
	 *		<hosts>
	 *			<host>
	 *	  			<ip>127.0.0.1</ip>
	 *	  			<port>11211</port>
	 *	  			<weight>1</weight>
	 *	  		</host>
	 *			<host>
	 *	  			<ip>127.0.0.2</ip>
	 *	  			<port>11211</port>
	 *	  			<weight>1</weight>
	 *	  		</host>
	 *	  	</hosts>
     * @param element
     * @return
     */
    @SuppressWarnings("unchecked")
	private static List<IData<String, Object>> getXmlElementList(Element listElement) {
    	List<Element> eles = listElement.elements();
		List<IData<String, Object>> retList = new ArrayList<>(eles.size());
		for(Element ele : eles) {
			if(null == ele || ele.isTextOnly()) {
	    		continue;
	    	}
	    	List<Element> props = ele.elements();
			if (null == props || props.isEmpty()) {
				continue;
			}
			IData<String, Object> data = new DataMap<String, Object>();
			for(Element prop : props) {
				if(prop.isTextOnly()) {
					data.put(prop.getName(), prop.getTextTrim());
				}
			}
			if(null != data && !data.isEmpty()) {
				retList.add(data);
			}
		}
		return retList;
	}
}
