package org.jftone.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.commons.lang.SerializationException;

public final class SerializeUtil {

    private SerializeUtil() {
        super();
    }

    /**
     * 将一个对象序列化为输出流
     * @param obj
     * @param outputStream
     */
    public static void serialize(Object obj, OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("输出对象为空");
        }
        ObjectOutputStream out = null;
        try {
            // stream closed in the finally
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);
            
        } catch (IOException ex) {
            throw new SerializationException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * 将对象序列化为字节数组
     * @param obj
     * @return
     */
    public static byte[] serialize(Object obj) {
    	if(!(obj instanceof Serializable)){
    		 throw new IllegalArgumentException("对象没有实现序列化接口");
    	}
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }

    /**
     * 将一个输入流对象反序列为对象
     * @param inputStream
     * @return
     */
    public static Object deserialize(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流对象为空");
        }
        ObjectInputStream in = null;
        try {
            // stream closed in the finally
            in = new ObjectInputStream(inputStream);
            return in.readObject();
            
        } catch (ClassNotFoundException ex) {
            throw new SerializationException(ex);
        } catch (IOException ex) {
            throw new SerializationException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * 字节流反序列化为对象
     * @param objectData
     * @return
     */
    public static Object deserialize(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("字节流数组为空");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        return deserialize(bais);
    }
}
