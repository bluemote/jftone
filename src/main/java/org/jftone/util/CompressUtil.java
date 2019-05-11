package org.jftone.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class CompressUtil {
	
	/**
	 * 关闭流对象
	 * @param oStream
	 */
    private static void closeStream(Closeable oStream){
        if(null != oStream){
            try {
                oStream.close();
            } catch (IOException e) {
                oStream=null;//赋值为null,等待垃圾回收
            }
        }
    }

    /**
     * 将字节流数组压缩
     * @param b
     * @return
     */
    public static byte[] compress(byte[] b){
        //将byte数据读入文件流
        ByteArrayOutputStream bos=null;
        GZIPOutputStream gzipos=null;
        try {
            bos = new ByteArrayOutputStream(512);
            gzipos = new GZIPOutputStream(bos);
            gzipos.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(gzipos);
            closeStream(bos);
        }
        return bos.toByteArray();
    }

    /**
     * 解压缩字节流数组
     * @param inByte
     * @return
     */
    public static byte[] decompress(byte[] inByte){
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        GZIPInputStream gzipos = null;
        try {
        	bais = new ByteArrayInputStream(inByte);
        	gzipos = new GZIPInputStream(bais);
            baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int temp = -1;
            while((temp = gzipos.read(b))>0){
            	baos.write(b, 0, temp);
            }
        } catch (Exception e) {
            return null;
        } finally {
            closeStream(baos);
            closeStream(gzipos);
            closeStream(bais);
        }
        return baos.toByteArray();
    }
}
