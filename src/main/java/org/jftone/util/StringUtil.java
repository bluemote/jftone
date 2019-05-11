package org.jftone.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class StringUtil {
	public static final String SPACE = " ";
	public static final String EMPTY = "";
    private static final int PAD_LIMIT = 8192;
    private static final String CHARACTER="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHAR_NUM="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public static final int NUMBER_MODE=1;
    public static final int CHAR_MODE=2;
    public static final int CHARNUM_MODE=3;
    
    public static Map<String, String> AREA_MAP = new HashMap<String, String>();
    static{
    	AREA_MAP.put("11", "北京"); AREA_MAP.put("12", "天津"); AREA_MAP.put("13", "河北"); AREA_MAP.put("14", "山西"); AREA_MAP.put("15", "内蒙古"); 
    	AREA_MAP.put("21", "辽宁"); AREA_MAP.put("22", "吉林"); AREA_MAP.put("23", "黑龙江");
    	AREA_MAP.put("31", "上海"); AREA_MAP.put("32", "江苏"); AREA_MAP.put("33", "浙江"); AREA_MAP.put("34", "安徽"); AREA_MAP.put("35", "福建"); AREA_MAP.put("36", "江西"); AREA_MAP.put("37", "山东");
    	AREA_MAP.put("41", "河南"); AREA_MAP.put("42", "湖北"); AREA_MAP.put("43", "湖南"); AREA_MAP.put("44", "广东"); AREA_MAP.put("45", "广西"); AREA_MAP.put("46", "海南");
    	AREA_MAP.put("50", "重庆"); AREA_MAP.put("51", "四川"); AREA_MAP.put("52", "贵州"); AREA_MAP.put("53", "云南"); AREA_MAP.put("54", "西藏");
    	AREA_MAP.put("61", "陕西"); AREA_MAP.put("62", "甘肃"); AREA_MAP.put("63", "青海"); AREA_MAP.put("64", "宁夏"); AREA_MAP.put("65", "新疆");
    	AREA_MAP.put("71", "台湾"); AREA_MAP.put("81", "香港"); AREA_MAP.put("82", "澳门"); AREA_MAP.put("91", "国外");
    }
    
    private StringUtil(){
    	super();
    }

	/**
	 * 判断字符是否为空，"" or null为空， 空格字符返回false
	 * <pre>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     * </pre>
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || str.equals("null");
    }
	
	/**
	 * 判断字符是否为空，" " or null为空， 空格字符返回true
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
	
	/**
	 * 判断生成随机验证码
	 * @param len  验证码长度
	 * @param mode  生成验证方式，1位纯数字，2位字母，3数字字母组合
	 * @return
	 */
    public static String getRandomCode(int len, int mode) {
		StringBuilder sb = new StringBuilder(len);
		Random random = new Random();
		if(mode==1){
			for (int i=0; i<len; i++) {
				sb.append(random.nextInt(10));
			}
		}else{
			String str= mode==2? CHARACTER : CHAR_NUM;
			int max = str.length();
			for (int i=0; i<len; i++) {
				sb.append(str.charAt(random.nextInt(max)));
			}
		}
		return sb.toString();
	}
    public static String getRandomCode(int len) {
    	return getRandomCode(len, NUMBER_MODE);
    }
	
	/**
	 * 判断是否为数字字符串
	 * @param str
	 * @return
	 */
    public static boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return isMatches(str, "^\\-?\\d+$");
    }
	
	/**
	 * 判断是否为数字或小数字符串
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
    	if (str == null || str.length() == 0 ) {
            return false;
        }
        return isMatches(str, "^\\-?\\d+(\\.\\d+)?$");
    }
	
	/**
	 * 判断字符串是否为电子邮件
	 * @param str
	 * @return
	 */
    public static boolean isEmail(String str) {
    	if (str == null || str.length() == 0 ) {
        	return false;
    	}
		return isMatches(str, "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
	}
    /**
     * 校验身份证
     * @param str
     * @return
     */
    public static boolean isPspt(String str) {
    	if (str == null || str.length() != 18) {
        	return false;
    	}
    	boolean flag = isMatches(str, "^\\d{6}(19|20)?\\d{2}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|(?i)X)$");
		if(!flag) return false;
		//校验地址格式
    	if(!AREA_MAP.containsKey(str.substring(0,2))){
    		 return false;
    	}
    	int[] code = new int[17];
		for(int i=0; i<17; i++){
			code[i] = Integer.parseInt(str.charAt(i)+"");
		}
		//∑(ai×Wi)/(mod 11)
		//加权因子
		int[] factor = new int[]{7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
		//校验位
		char[] parity = new char[]{'1','0','X','9','8','7','6','5','4','3','2'};  //[0,1,2,3,4,5,6,7,8,9,10]
		int sum = 0;
		for (int i = 0; i<17; i++){
			sum += code[i] * factor[i];
		}
		if(!str.substring(17,18).equals(String.valueOf(parity[sum % 11]))){
			return false;
		}
		return true;
	}

    /**
     * 判断字符串是否为手机号码
     * @return
     */
    public static boolean isMobile(String str) {
    	if (str == null || str.length() == 0 ) {
        	return false;
    	}
    	return isMatches(str, "^1[345789][0-9]{9}$");
	}
	
    /**
     * 判断字符串是否为手机号码
     * @param str
     * @return
     */
    public static boolean isTelePhone(String str) {
    	if (str == null || str.length() == 0 ) {
        	return false;
    	}
    	return isMatches(str, "^0[1-9]\\d{1,2}\\-\\d{7,8}$");
	}	
	
    /**
     * 判断字符串数字组合
     * @param str
     * @return
     */
    public static boolean isMixChar(String str) {
    	if (str == null || str.length() == 0 ) {
        	return false;
    	}
		return isMatches(str, "^[a-zA-Z]+[\\_\\-]?\\d+\\w*$");
	}		
	
    /**
     * 判断是否人民币格式
     * @param str
     * @return
     */
    public static boolean isRMB(String str) {
    	if (str == null || str.length() == 0 ) {
        	return false;
    	}
		return isMatches(str, "^[0-9]+(\\.\\d{1,2})?$");
	}	
	
    /**
     * 判断字母字符
     * @param str
     * @return
     */
    public static boolean isLetterChar(String str) {
    	if (str == null || str.length() == 0 ) {
        	return false;
    	}
		return isMatches(str, "^[a-zA-Z]+$");
	}	
	
    /**
     * 判断是否中文
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
    	if (str == null || str.length() == 0 ) {
        	return false;
    	}
		return isMatches(str, "^[\\x{4e00}-\\x{9fa5}]+$");
	}
	
	/**
	 * 返回对象连接字符串
	 * @param elements
	 * @return
	 */
	public static String join(final Object[] array) {
        return join(array, null);
    }
	
	/**
	 * 返回以separator分隔符连接的字符串
	 * @param array
	 * @param separator
	 * @return
	 */
	public static String join(final Object[] array, final String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }
	
	/**
	 *  返回以separator分隔符连接的字符串
	 * @param array
	 * @param separator
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static String join(final Object[] array, final String separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
	
	/**
	 * 移除开头字符remove
	 * @param str
	 * @param remove
	 * @return
	 */
	public static String removeStart(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)){
            return str.substring(remove.length());
        }
        return str;
    }
	
	/**
	 * 移除结尾字符remove
	 * @param str
	 * @param remove
	 * @return
	 */
	public static String removeEnd(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }
	
    /**
     * 重复n次字符
     * StringUtil.repeat(null, 2) = null
     * StringUtil.repeat("", 0)   = ""
     * StringUtil.repeat("", 2)   = ""
     * StringUtil.repeat("a", 3)  = "aaa"
     * StringUtil.repeat("ab", 2) = "abab"
     * StringUtil.repeat("a", -2) = ""
     */
    public static String repeat(final String str, final int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        final int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return repeat(str.charAt(0), repeat);
        }

        final int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1 :
                return repeat(str.charAt(0), repeat);
            case 2 :
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default :
                final StringBuilder buf = new StringBuilder(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    /**
     * 重复n次字符,并以separator分隔符连接
     * StringUtil.repeat(null, null, 2) = null
     * StringUtil.repeat(null, "x", 2)  = null
     * StringUtil.repeat("", null, 0)   = ""
     * StringUtil.repeat("", "", 2)     = ""
     * StringUtil.repeat("", "x", 3)    = "xxx"
     * StringUtil.repeat("?", ", ", 3)  = "?, ?, ?"
     */
    public static String repeat(final String str, final String separator, final int repeat) {
        if(str == null || separator == null) {
            return repeat(str, repeat);
        }
        final String result = repeat(str + separator, repeat);
        return removeEnd(result, separator);
    }

    /**
     * 
     * StringUtil.repeat('e', 0)  = ""
     * StringUtil.repeat('e', 3)  = "eee"
     * StringUtil.repeat('e', -2) = ""
     */
    public static String repeat(final char ch, final int repeat) {
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }
    
    public static String rightPad(final String str, final int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * StringUtil.rightPad(null, *, *)     = null
     * StringUtil.rightPad("", 3, 'z')     = "zzz"
     * StringUtil.rightPad("bat", 3, 'z')  = "bat"
     * StringUtil.rightPad("bat", 5, 'z')  = "batzz"
     * StringUtil.rightPad("bat", 1, 'z')  = "bat"
     * StringUtil.rightPad("bat", -1, 'z') = "bat"
     */
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    /**
     * StringUtil.rightPad(null, *, *)      = null
     * StringUtil.rightPad("", 3, "z")      = "zzz"
     * StringUtil.rightPad("bat", 3, "yz")  = "bat"
     * StringUtil.rightPad("bat", 5, "yz")  = "batyz"
     * StringUtil.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringUtil.rightPad("bat", 1, "yz")  = "bat"
     * StringUtil.rightPad("bat", -1, "yz") = "bat"
     * StringUtil.rightPad("bat", 5, null)  = "bat  "
     * StringUtil.rightPad("bat", 5, "")    = "bat  "
     */
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }
	
    /**
     * StringUtil.leftPad(null, *)   = null
     * StringUtil.leftPad("", 3)     = "   "
     * StringUtil.leftPad("bat", 3)  = "bat"
     * StringUtil.leftPad("bat", 5)  = "  bat"
     * StringUtil.leftPad("bat", 1)  = "bat"
     * StringUtil.leftPad("bat", -1) = "bat"
     */
    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * StringUtil.leftPad(null, *, *)     = null
     * StringUtil.leftPad("", 3, 'z')     = "zzz"
     * StringUtil.leftPad("bat", 3, 'z')  = "bat"
     * StringUtil.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtil.leftPad("bat", 1, 'z')  = "bat"
     * StringUtil.leftPad("bat", -1, 'z') = "bat"
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * StringUtil.leftPad(null, *, *)      = null
     * StringUtil.leftPad("", 3, "z")      = "zzz"
     * StringUtil.leftPad("bat", 3, "yz")  = "bat"
     * StringUtil.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtil.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtil.leftPad("bat", 1, "yz")  = "bat"
     * StringUtil.leftPad("bat", -1, "yz") = "bat"
     * StringUtil.leftPad("bat", 5, null)  = "  bat"
     * StringUtil.leftPad("bat", 5, "")    = "  bat"
     */
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }
    
    /**
	 * 返回星期
	 * @param weekday
	 * @return
	 */
	public static String getWeek(final int weekday) {
		String weekStr = "";
		String[] weeks = new String[]{"日", "一", "二", "三", "四", "五", "六"};
		if(weekday>=1 && weekday<=7){
			weekStr = weeks[weekday-1];
		}
        return weekStr;
    }

	/**
	 * 判断待匹配字符串是否完全符合匹配模式
	 * @param matcherStr 待匹配字符串
	 * @param patternStr 匹配模式
	 * @return true|false
	 */
	public static boolean isMatches(String matcherStr, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(matcherStr);
		return matcher.matches();
	}
	/**
	 * 判断待匹配字符串是否部分符合匹配模式
	 * @param matcherStr 待匹配字符串
	 * @param patternStr 匹配模式
	 * @return true|false
	 */
	public static boolean find(String matcherStr, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(matcherStr);
		return matcher.find();
	}
	
	/**
	 * 返回整个匹配模式的ArrayList数组
	 * 
	 * @param matcherStr 匹配字符串
	 * @param patternStr 匹配模式
	 * @return ArrayList
	 */
	public static List<String> getMathchList(String matcherStr, String patternStr) {
		List<String> list = new ArrayList<String>();
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(matcherStr);
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;

	}

	/**
	 * 返回匹配模式的各个子序列
	 * 只查找第一个匹配串
	 * 
	 * @param matcherStr   匹配字符串
	 * @param patternStr   匹配模式
	 * @return String[]
	 */
	public static String[] getMathchGroup(String matcherStr, String patternStr) {
		String[] str = null;
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(matcherStr);
		if (matcher.find()) {
			str = new String[matcher.groupCount()];
			for (int i = 0; i < matcher.groupCount(); i++) {
				str[i] = matcher.group(i + 1);
			}
		}
		return str;
	}

	/**
	 * 替换字符串函数 String strSource - 源字符串 String strFrom - 要替换的子串 String strTo -
	 * 替换为的字符串
	 */
	public static String replace(String strSource, String strFrom, String strTo) {
		/**
		 * 如果要替换的子串为空，则直接返回源串
		 */
		if (strFrom == null || strFrom.equals(""))
			return strSource;
		String strDest = "";
		int intFromLen = strFrom.length();	// 要替换的子串长度
		int intPos;		// 循环替换字符串
		while ((intPos = strSource.indexOf(strFrom)) != -1) {
			strDest = strDest + strSource.substring(0, intPos);		//获取匹配字符串的左边子串
			strDest = strDest + strTo;		// 加上替换后的子串
			strSource = strSource.substring(intPos + intFromLen);	// 修改源串为匹配子串后的子串
		}
		strDest = strDest + strSource;	// 加上没有匹配的子串
		return strDest;
	}

}
