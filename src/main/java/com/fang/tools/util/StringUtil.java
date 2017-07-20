package com.fang.tools.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class StringUtil {
    // ------------
    public  static final Logger log  = LoggerFactory.getLogger(StringUtil.class);

    public static boolean isNull(String s) {
	return s == null || "".equals(s.trim());
    }

    public static boolean valedatePrarameter(String... args) {
	for (String a : args) {
	    if (StringUtil.isNull(a)) {
		return false;
	    }
	}
	return true;
    }

    public static boolean isHex(String s) {
	s = s.toUpperCase();
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    if (((c >= 'A') && (c <= 'F')) || ((c >= '0') && (c <= '9')))
		continue;
	    else
		return false;
	}
	return true;
    }

    public static String getCaptcha() {
	String captcha = "";
	String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefjhijklmnopqrstuvwsyz";
	for (int i = 0; i < 6; i++) {
	    int rand = (int) (Math.random() * 62);
	    captcha = captcha + chars.charAt(rand);

	}
	return captcha;
    }

    public static void sendSms(String phone, String message) {

	try {
	    message = URLEncoder.encode(message, "utf-8");
	    String url = "http://wireless.fang.com/sms_validate/sendsms.do?name=jiankong&pwd=a7c9506c&dst="
		    + phone + "&msg=" + message;

	    getUrlTxtByUTF(url);
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

    }

    public static String getUrlTxtByUTF(String strurl) throws Exception {
	StringBuffer sb = new StringBuffer();
	BufferedReader reader = null;
	String line = null;
	try {
	    URL url = new URL(strurl);
	    log.info(strurl);
	    HttpURLConnection connection = (HttpURLConnection) url
		    .openConnection();
	    connection.setConnectTimeout(1500);
	    connection.setReadTimeout(3000);
	    reader = new BufferedReader(new InputStreamReader(
		    connection.getInputStream(), "UTF-8"));
	    while ((line = reader.readLine()) != null) {
		sb.append(line);
	    }

	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	} finally {
	    try {
		if (reader != null)
		    reader.close();
	    } catch (Exception e) {
		log.error(e.getMessage());
	    }

	}

	return sb.toString();
    }

    public static boolean isEmpty(String arg) {
	return (arg == null) || (arg.trim().equals(""));
    }

    /**
     * 验证邮箱是否正确
     *
     * @param searchPhrase
     * @return
     */
    public static boolean isEmail(final String searchPhrase) {
	if (!isEmpty(searchPhrase)) {
	    final String check = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	    final Pattern regex = Pattern.compile(check);
	    final Matcher matcher = regex.matcher(searchPhrase);
	    return matcher.matches();
	}
	return false;
    }

    public static String getCurrMemInfo() {
	Runtime rt = Runtime.getRuntime(); // 获得系统的Runtime对象rt
	String beginUse = "Total Memory= " + rt.totalMemory() / (1024 * 1024)
		+ "M" + // 打印总内存大小
		" Free Memory = " + rt.freeMemory() / (1024 * 1024) + "M"; // 打印空闲内存大小
	return beginUse;
    }

    /**
     * 验证手机号是否正确
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
	if (!isEmpty(mobiles)) {
	    Pattern p = Pattern
		    .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(14[0-9]))\\d{8}$");
	    Matcher m = p.matcher(mobiles);
	    return m.matches();
	}
	return false;
    }

    private static final SimpleDateFormat format = new SimpleDateFormat(
	    "yyyy-MM-dd HH:mm:ss");

    public static String getNowTime() {
	return format.format(new Date());
    }

    public static String getDateByOffset(String sFormat, int offset) {
	Calendar c = Calendar.getInstance();
	c.setTime(new Date());
	c.add(c.DATE, offset);
	Date date = c.getTime();
	SimpleDateFormat format = new SimpleDateFormat(sFormat);
	return format.format(date);
    }

    public static String get15Time() {

	Calendar c = Calendar.getInstance();

	c.setTime(new Date());
	c.add(c.DATE, -15);

	Date date15 = c.getTime();

	return format.format(date15);
    }

    public static List<String> getIpAddress() {
	List<String> list = new ArrayList<String>();
	try {
	    for (Enumeration<NetworkInterface> interfaces = NetworkInterface
		    .getNetworkInterfaces(); interfaces.hasMoreElements();) {
		NetworkInterface networkInterface = interfaces.nextElement();
		if (networkInterface.isLoopback()
			|| networkInterface.isVirtual()
			|| !networkInterface.isUp()) {
		    continue;
		}
		Enumeration<InetAddress> addresses = networkInterface
			.getInetAddresses();
		if (addresses.hasMoreElements()) {
		    list.add(addresses.nextElement().getLocalHost().toString()
			    .split("\\/")[1]);

		    // list.add(addresses.nextElement().toString().split("\\/")[1]);
		}
	    }
	} catch (SocketException e) {
	    log.error(e.getMessage(), e);
	} catch (UnknownHostException e) {
	    log.error(e.getMessage(), e);
	}
	return list;
    }

    public static String get30DayBefore() {

	Calendar c = Calendar.getInstance();

	c.setTime(new Date());
	c.add(c.DATE, -30);

	Date date15 = c.getTime();

	return format.format(date15);

    }

    public static String getDaysBefore(int i) {

	Calendar c = Calendar.getInstance();

	c.setTime(new Date());
	c.add(c.DATE, -i);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	return sdf.format(c.getTime());
    }

    /**
     * 获取reg匹配的内容
     * 
     * @param sReg
     * @param s
     * @param groupId
     * @return
     */
    public static List<String> getRegGroupContents(String sReg, String s,
                                                   int groupId) {
	Pattern p = Pattern.compile(sReg);
	Matcher m = p.matcher(s);
	List<String> list = new ArrayList<String>();
	if (groupId <= m.groupCount() && groupId > 0) {
	    while (m.find()) {
		list.add(m.group(groupId));
	    }
	}
	return list;
    }

    /**
     * 获取reg匹配的内容
     * 
     * @param sReg
     * @param str
     * @param
     * @return
     */
    public static List<Queue<String>> getRegGroupMatchedList(String sReg,
                                                             String str) {
	Pattern p = Pattern.compile(sReg);
	Matcher m = p.matcher(str);
	List<Queue<String>> ret = new ArrayList<Queue<String>>();
	while (m.find()) {
	    Queue<String> t = new ConcurrentLinkedQueue<String>();
	    for (int i = 1; i <= m.groupCount(); i++) {
		t.offer(m.group(i));
	    }
	    if (t.size() > 0) {
		ret.add(t);
	    }
	}
	return ret;
    }

    /**
     * 获取reg匹配的内容
     * 
     * @param sReg
     * @param str
     * @param
     * @return
     */
    public static String getRegGroupMatchedByIndex(String sReg, String str,
                                                   int index) {
	List<Queue<String>> ret = getRegGroupMatchedList(sReg, str);
	if (ret == null || ret.size() <= 0) {
	    return "";
	}
	Queue<String> q = ret.get(index);
	if (q == null || q.size() <= 0) {
	    return "";
	}
	Iterator<String> iterator = q.iterator();
	StringBuffer sb = new StringBuffer();
	while (iterator.hasNext()) {
	    sb.append(iterator.next());
	}
	return sb.toString();
    }

    public static String getRegGroupContentByIndex(String sReg, String s,
                                                   int groupId) {
	Pattern p = Pattern.compile(sReg);
	Matcher m = p.matcher(s);
	while (m.find()) {
	    if (groupId > m.groupCount())
		return "";
	    else if (groupId == 0) {
		return m.group();
	    } else {
		return m.group(groupId);
	    }
	}
	return "";
    }

    public static String getUrlTxtByGBK(String strurl) throws Exception {
	StringBuffer sb = new StringBuffer();
	BufferedReader reader = null;
	StopWatch sw = new StopWatch();
	sw.start();
	String line = null;
	try {
	    URL url = new URL(strurl);
	    HttpURLConnection connection = (HttpURLConnection) url
		    .openConnection();
	    connection.setConnectTimeout(2000);
	    connection.setReadTimeout(3000);
	    connection.setRequestProperty("Accept-Encoding", "gzip");
	    connection.connect();
	    String encodetype = connection.getContentEncoding();
	    if (encodetype != null && encodetype.contains("gzip")) {
		GZIPInputStream gzin = new GZIPInputStream(
			connection.getInputStream());
		reader = new BufferedReader(new InputStreamReader(gzin, "GBK"));
	    } else
		reader = new BufferedReader(new InputStreamReader(
			connection.getInputStream(), "gbk"));
	    while ((line = reader.readLine()) != null) {
		sb.append(line);
	    }
	} catch (Exception e) {
	    String msg = "" + e.getMessage() + ",url=" + strurl;
	    if (msg.contains("timed out")) {
		log.error(msg);
	    } else {
		log.error(msg, e);
	    }

	} finally {
	    try {
		if (reader != null)
		    reader.close();
	    } catch (Exception e) {
		log.error(e.getMessage());
	    }
	}
	sw.stop();
	log.info("URL=" + strurl + ",takes " + sw.prettyPrint());
	return sb.toString();
    }

    public static String getContentFromXmlNodeByXpath(String xmlStr,
                                                      String sPath, String sEncodeType) {
	DocumentBuilder builder = null;
	Document document = null;
	InputStream strm = null;
	try {
	    builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
	try {
	    strm = new ByteArrayInputStream(xmlStr.getBytes(sEncodeType));
	    document = builder.parse(strm);
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	// 生成XPath对象
	XPath xpath = XPathFactory.newInstance().newXPath();

	// 获取节点值
	String sNodeString = null;
	try {
	    sNodeString = (String) xpath.evaluate(sPath, document,
		    XPathConstants.STRING);
	} catch (XPathExpressionException e) {
	    e.printStackTrace();
	}
	return sNodeString;
    }


    public static List<Map<String, String>> getListFromXmlNodeByXpath(
            String xmlStr, String sPath, String sEncodeType) {
	DocumentBuilder builder = null;
	Document document = null;
	InputStream strm = null;
	NodeList list = null;
	List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
	try {
	    builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
	try {
	    strm = new ByteArrayInputStream(xmlStr.getBytes(sEncodeType));
	    document = builder.parse(strm);
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	// 生成XPath对象
	XPath xpath = XPathFactory.newInstance().newXPath();

	// 获取节点值
	String sNodeString = null;
	try {
	    list = (NodeList) xpath.evaluate(sPath, document,
		    XPathConstants.NODESET);
	} catch (XPathExpressionException e) {
	    e.printStackTrace();
	}
	String key, value;
	for (int i = 0; i < list.getLength(); i++) {
	    org.w3c.dom.Node node = list.item(i);
	    if (node.hasChildNodes()) {
		NodeList subnodes = node.getChildNodes();
		Map<String, String> tmp = new HashMap<String, String>();
		for (int j = 0; j < subnodes.getLength(); j++) {
		    org.w3c.dom.Node subnode = subnodes.item(j);
		    key = subnode.getNodeName();
		    value = subnode.getTextContent();
		    if (!isNull(key)) {
			tmp.put(key, value);
		    }
		}
		retList.add(tmp);
	    }
	}
	return retList;
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws IOException
     */
    public final static String getIpAddress(HttpServletRequest request) {
	// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
	/*
	 * String ip = request.getHeader("X-Forwarded-For"); if (ip == null ||
	 * ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { if (ip == null
	 * || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip =
	 * request.getHeader("Proxy-Client-IP"); } if (ip == null || ip.length()
	 * == 0 || "unknown".equalsIgnoreCase(ip)) { ip =
	 * request.getHeader("WL-Proxy-Client-IP"); } if (ip == null ||
	 * ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip =
	 * request.getHeader("HTTP_CLIENT_IP"); } if (ip == null || ip.length()
	 * == 0 || "unknown".equalsIgnoreCase(ip)) { ip =
	 * request.getHeader("HTTP_X_FORWARDED_FOR"); } if (ip == null ||
	 * ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip =
	 * request.getRemoteAddr(); } } else if (ip.length() > 15) { String[]
	 * ips = ip.split(","); for (int index = 0; index < ips.length; index++)
	 * { String strIp = (String) ips[index]; if
	 * (!("unknown".equalsIgnoreCase(strIp))) { ip = strIp; break; } } }
	 */
	String ip = request.getRemoteAddr();
	return ip;
    }

    public static String join(Collection var0, String var1) {
	StringBuffer var2 = new StringBuffer();

	for (Iterator var3 = var0.iterator(); var3.hasNext(); var2
		.append((String) var3.next())) {
	    if (var2.length() != 0) {
		var2.append(var1);
	    }
	}

	return var2.toString();
    }

    public static void main(String args[]) throws Exception {
	/*
	 * String result =
	 * StringUtil.getUrlTxtByGBK(Constant.sUrlProductDescription +
	 * "cid=75&pid=3364576&did=69657"); org.dom4j.Element propertys =
	 * StringUtil
	 * .getElementFromXmlByXpathDom4J(result,"//productinfo/Propertys"
	 * ,"gbk");
	 */
	// System.out.println(getDaysBefore(2));
	// String
	// a=dateDiff("2015-12-15 14:30:00","2015-12-17 13:45:00","yyyy-MM-dd HH:mm:ss");
	String a = getNowTime();
	a = StringUtil.timeToStr(a, "yyyy-MM-dd HH:mm:ss");
	String b = "2016-01-04 16:33:00";
	b = StringUtil.timeToStr(b, "yyyy-MM-dd HH:mm:ss");

	System.out.println(a.compareTo(b));
	// System.out.print(getNowTime());
    }

    public static List<String> executeShell(String shellCommand)
	    throws Exception {
	List<String> strList = runShell(shellCommand);
	return strList;
    }

    public static List<String> runShell(String shStr) throws Exception {
	List<String> strList = new ArrayList<String>();

	Process process;
	process = Runtime.getRuntime().exec(
		new String[] { "/bin/sh", "-c", shStr }, null, null);
	InputStreamReader ir = new InputStreamReader(process.getInputStream());
	LineNumberReader input = new LineNumberReader(ir);
	String line;
	process.waitFor();
	while ((line = input.readLine()) != null) {
	    strList.add(line);
	}
	input.close();
	System.out.println("runShell result:" + strList.get(0));
	return strList;
    }

    public static String getLocalPort() {
	String name = ManagementFactory.getRuntimeMXBean().getName();
	System.out.println("pid:" + name);
	String pid = name.split("@")[0];
	String sCmdGetPort = "sed '/<!--/{:a;/-->/!{N;ba}};/<!--/d'  `ps aux | grep "
		+ pid
		+ " | awk -F '-conf' '{print $2}' | awk '{print $1}'` | grep '<http.*address.*' | awk -F 'port=' '{print $2}'  | awk -F '\"' '{print $2}'";
	String sPort = "";
	try {
	    sPort = executeShell(sCmdGetPort).get(0);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return sPort;
    }

    public static String getWAN_Ip() {
	String sCmd = "cat /etc/sysconfig/network-scripts/ifcfg-eth1 | grep IPADDR | awk -F '=' '{print $2}'";
	String sLocalIp = "";
	try {
	    sLocalIp = executeShell(sCmd).get(0);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return sLocalIp;
    }

    public static String getLAN_Ip() {
	String sCmd = "cat /etc/sysconfig/network-scripts/ifcfg-eth0 | grep IPADDR | awk -F '=' '{print $2}'";
	String sLocalIp = "";
	try {
	    sLocalIp = executeShell(sCmd).get(0);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return sLocalIp;
    }

    public static String getTcpConnectingCount(String localPort) {
	String sCmd = " /usr/sbin/ss -o state established '( sport = :"
		+ localPort + " )' | wc -l ";
	String sConnectionCount = "";
	try {
	    sConnectionCount = executeShell(sCmd).get(0);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return sConnectionCount;
    }

    public static String dateDiff(String startTime, String endTime,
                                  String format) {
	// 按照传入的格式生成一个simpledateformate对象
	SimpleDateFormat sd = new SimpleDateFormat(format);
	long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
	long nh = 1000 * 60 * 60;// 一小时的毫秒数
	long nm = 1000 * 60;// 一分钟的毫秒数
	long ns = 1000;// 一秒钟的毫秒数long diff;try {
	// 获得两个时间的毫秒时间差异
	long diff = 0;
	try {
	    diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
	} catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	String re = "";
	if (diff < 0) {
	    re = "结束时间不得小于开始时间";
	} else {
	    long day = diff / nd;// 计算差多少天
	    long hour = diff % nd / nh;// 计算差多少小时
	    long min = diff % nd % nh / nm;// 计算差多少分钟
	    // long sec = diff%nd%nh%nm/ns;//计算差多少秒//输出结果
	    // System.out.println("时间相差："+day+"天"+hour+"小时"+min+"分钟"+sec+"秒。");
	    re = day + "天" + hour + "小时" + min + "分钟";
	}

	return re;
    }



    /**
     * 把同时包含中英文的字符串截取特定长度，中文点两个字节，英文占一个字节
     * 
     * @param str
     * @param length
     * @return
     */
    public static String getSubString(String str, int length) {
	int count = 0;
	int offset = 0;
	char[] c = str.toCharArray();
	for (int i = 0; i < c.length; i++) {
	    if (c[i] > 256) {
		offset = 2;
		count += 2;
	    } else {
		offset = 1;
		count++;
	    }
	    if (count == length) {
		return str.substring(0, i + 1);
	    }
	    if ((count == length + 1 && offset == 2)) {
		return str.substring(0, i);
	    }
	}
	return str;
    }

    /**
     * 将时间型字符串转化成时间戳
     */
    public static String timeToStr(String time, String format) {
	SimpleDateFormat sd = new SimpleDateFormat(format);
	long str = 0;
	try {
	    str = sd.parse(time).getTime();
	} catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return str + "";
    }

    // 随机产生6位数
    public static String getRandomOfSix() {
	Random random = new Random();
	StringBuilder bud = new StringBuilder();
	for (int i = 0; i < 6; i++) {
	    bud.append(random.nextInt(10));
	}
	return bud.toString();
    }

    /**
     * 版本号比较方法
     * 
     * @param localVersion
     *            自定义版本
     * @return 线上大于等于自定义时，返回true；线上小于自定义时，返回false
     */
    public static boolean compareVersion(String localVersion, String version) {
	if (isNull(version)) {// 线上版本没有版本号时，默认true，方便调试
	    return true;
	}
	if (localVersion.equals(version)) {
	    return true;
	}
	String[] localArray = localVersion.split("\\.");
	String[] onlineArray = version.split("\\.");
	int length = localArray.length < onlineArray.length ? localArray.length
		: onlineArray.length;
	for (int i = 0; i < length; i++) {
	    if (Integer.parseInt(onlineArray[i]) > Integer
		    .parseInt(localArray[i])) {
		return true;
	    } else if (Integer.parseInt(onlineArray[i]) < Integer
		    .parseInt(localArray[i])) {
		return false;
	    }
	}
	if (localArray.length > onlineArray.length) {
	    return false;
	}
	return true;
    }
}
