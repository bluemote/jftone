package org.jftone.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jftone.config.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IPv4Util {
	private static Logger log = LoggerFactory.getLogger(IPv4Util.class);

	/**
	 * 获取IP地址
	 * 
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		ipAddress = request.getHeader("x-forwarded-for");
		if (StringUtil.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtil.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtil.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1")) {
				// 根据网卡取本机配置的IP
				try {
					ipAddress = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
					return ipAddress;
				}
			}
		}
		/**
		 * 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割 "255.255.255.255".length() = 15
		 */
		if (ipAddress != null && ipAddress.length() > 15 && ipAddress.indexOf(Const.SPLIT_COMMA) > -1) {
			ipAddress = ipAddress.substring(0, ipAddress.indexOf(Const.SPLIT_COMMA)).trim();
		}
		return ipAddress;
	}

	/**
	 * ip转long
	 * 
	 * @param ipAddr
	 * @return long
	 */
	public static long ip2long(String ipAddr) {
		Long[] ip = new Long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = ipAddr.indexOf(".");
		int position2 = ipAddr.indexOf(".", position1 + 1);
		int position3 = ipAddr.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		try {
			ip[0] = Long.parseLong(ipAddr.substring(0, position1));
			ip[1] = Long.parseLong(ipAddr.substring(position1 + 1, position2));
			ip[2] = Long.parseLong(ipAddr.substring(position2 + 1, position3));
			ip[3] = Long.parseLong(ipAddr.substring(position3 + 1));
			return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
		} catch (Exception exception) {
			return 0000l;
		}
	}

	/**
	 * long转ip
	 * 
	 * @param ip
	 * @return
	 */
	public static String long2ip(long ip) {
		final StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(ip >>> 24)).append(".");
		sb.append(String.valueOf((ip & 0xFFFFFF) >>> 16)).append(".");
		sb.append(String.valueOf((ip & 0xFFFF) >>> 8)).append(".");
		sb.append(String.valueOf(ip & 0xFF));
		return sb.toString();
	}

	public static String getLocalAddress() {
		String localIP = "unknown";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			if (null != addr && addr instanceof Inet4Address) {
				localIP = addr.getHostAddress();
			}
		} catch (UnknownHostException e) {
			log.error("获取本机IP地址错误", e);
		}
		return localIP;
	}

	public static List<String> findLocalAddress() {
		List<String> ipList = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface;
			Enumeration<InetAddress> inetAddresses;
			InetAddress inetAddress;
			while (networkInterfaces.hasMoreElements()) {
				networkInterface = networkInterfaces.nextElement();
				inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					inetAddress = inetAddresses.nextElement();
					if (inetAddress != null && !inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address ) {
						ipList.add(inetAddress.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			log.error("获取本机所有IP错误", e);
		}
		return ipList;
	}

}
