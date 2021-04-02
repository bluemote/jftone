package org.jftone.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

public final class KryoSerializer {

	public static final int MAX_CACHED_BUF_SIZE = 64 * 1024;
	public static final int DEFAULT_BUF_SIZE = 512;
	
	private static Set<Class<?>> javaSerializerTypes = new HashSet<>();
	static {
		javaSerializerTypes.add(Throwable.class);
		javaSerializerTypes.add(DataMap.class);
		javaSerializerTypes.add(ArrayList.class);
	}
	
	private KryoSerializer() {
        super();
    }

	private static ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			for (Class<?> type : javaSerializerTypes) {
				kryo.addDefaultSerializer(type, JavaSerializer.class);
			}
			kryo.setRegistrationRequired(false);
			kryo.setReferences(false);
			return kryo;
		};
	};

	private static ThreadLocal<Output> outputLocal = new ThreadLocal<Output>() {
		protected Output initialValue() {
			return new Output(DEFAULT_BUF_SIZE, -1);
		};
	};

	/**
	 * 设置序列化转换对象
	 * @param type
	 */
	public static void setJavaSerializer(Class<?> type) {
		javaSerializerTypes.add(type);
	}

	/**
	 *  将对象序列化为字节数组
	 * @param obj
	 * @return
	 */
	public static <T> byte[] writeObject(T obj) {
//		if (!(obj instanceof Serializable)) {
//			throw new IllegalArgumentException("对象没有实现序列化接口");
//		}
		Kryo kryo = kryoLocal.get();
		Output output = outputLocal.get();
		try {
			kryo.writeObject(output, obj);
			return output.toBytes();
		} catch (Exception ex) {
			throw new SerializationException(ex);
		} finally {
			output.clear();
			if (output.getBuffer().length > MAX_CACHED_BUF_SIZE) {
				output.setBuffer(new byte[DEFAULT_BUF_SIZE], -1);
			}
		}
	}

	/**
	 * 序列化对象
	 * @param inputStream
	 * @param clazz				指定返回对象类型
	 * @return
	 */
	public static <T> T readObject(InputStream inputStream, Class<T> clazz) {
		if (inputStream == null) {
			throw new IllegalArgumentException("输入流对象为空");
		}
		Input input = null;
		try {
			Kryo kryo = kryoLocal.get();
			input = new Input(inputStream);
			return kryo.readObject(input, clazz);
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	/**
	 * 反序列化对象
	 * @param objectBytes   反序列化字节数组
	 * @param clazz			指定返回对象类型
	 * @return
	 */
	public static <T> T readObject(byte[] objectBytes, Class<T> clazz) {
		if (objectBytes == null) {
			throw new IllegalArgumentException("字节流数组为空");
		}
		return readObject(objectBytes, 0, objectBytes.length, clazz);
	}

	/**
	 * 反序列化对象
	 * @param objectBytes	反序列化字节数组
	 * @param offset		起始偏移位
	 * @param length		长度
	 * @param clazz			指定返回对象类型
	 * @return
	 */
	public static <T> T readObject(byte[] objectBytes, int offset, int length, Class<T> clazz) {
		if (objectBytes == null) {
			throw new IllegalArgumentException("字节流数组为空");
		}
		Input input = new Input(objectBytes, offset, length);
		try {
			Kryo kryo = kryoLocal.get();
			return kryo.readObject(input, clazz);
		} finally {
			if (input != null) {
				input.close();
				input = null;
			}
		}
	}
}
