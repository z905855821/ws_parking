package com.njupt.ws_cxf_spring.ws.tools;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Tools {
	
	/**
	 * 生成32位不重复随机id
	 * 
	 * @return
	 */
	public static String GetRandomNumber() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}

	/**
	 * 获得当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 比较t1，t2的大小，t1大则返回1，相等则返回0，否则-1
	 * 判断时间的先后
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static int timeCompare(String t1, String t2) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(formatter.parse(t1));
			c2.setTime(formatter.parse(t2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int result = c1.compareTo(c2);
		return result;
	}

	/**
	 * 获得md5码
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	/**
	 * 通过TCP/IP方式发送数据给服务端
	 * 
	 * @param reqMessage
	 * @throws Exception
	 */
	static void send(String reqMessage) throws Exception {
		Socket sock = null;
		BufferedOutputStream out = null;
		try {
			sock = new Socket();

			SocketAddress sockAdd = new InetSocketAddress("10.10.22.90", 10300);
			sock.connect(sockAdd, 2000); // 客户端设置连接建立超时时间

			out = new BufferedOutputStream(sock.getOutputStream());
			out.write(reqMessage.getBytes());
			out.flush();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			// log.error("网络连接异常"+Strings.getStackTrace(e));
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// log.error("网络连接异常\n"+Strings.getStackTrace(e));
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (sock != null) {
				try {
					sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 通过TCP/IP方式发送数据给服务端
	 * 
	 * @param reqMessage
	 * @throws Exception
	 */
	static void send1(String reqMessage) throws Exception {
		System.out.println("接收到的数据:" + reqMessage);
		Socket sock = null;
		BufferedOutputStream out = null;
		try {
			sock = new Socket();

			SocketAddress sockAdd = new InetSocketAddress("10.10.22.220", 11000);
			sock.connect(sockAdd, 2000); // 客户端设置连接建立超时时间

			out = new BufferedOutputStream(sock.getOutputStream());
			out.write(reqMessage.getBytes());
			out.flush();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			// log.error("网络连接异常"+Strings.getStackTrace(e));
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// log.error("网络连接异常\n"+Strings.getStackTrace(e));
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (sock != null) {
				try {
					sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean isIlegal(String[] str, int a, int b) {

		for (int i = a; i < b; i++) {
			if (isNull(str[i]) || !isFloatPointNumber(str[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isNull(String number) {
		if (number == null || number.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	// 判断字符串是不是浮点数
	public static boolean isFloatPointNumber(String number) {
		number = number.trim();
		String pointPrefix = "(\\-|\\+){0,1}\\d*\\.\\d+";// 浮点数的正则表达式-小数点在中间与前面
		String pointSuffix = "(\\-|\\+){0,1}\\d+\\.";// 浮点数的正则表达式-小数点在后面
		if (number.matches(pointPrefix) || number.matches(pointSuffix))
			return true;
		else
			return false;
	}
	
	/**
	 * TCP方式往网关发送控制指令
	 * 
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public static boolean tcpSendToServer(String msg, String ip,
			int port) throws IOException {		
			Socket s = null;
			OutputStream os = null;
			try {
				s = new Socket(ip, port);
				os = s.getOutputStream();
				os.write(msg.getBytes());
				os.flush();	
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				if (os != null)
					os.close();
				if (s != null)
					s.close();
			}
		}

	/**
	 * TCP方式接收网关发送的数据
	 * 
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public static String tcpSendWithServer(String msg, String ip,
			int port) throws IOException {	
			String rev=null;
			Socket s = null;
			OutputStream os = null;
			InputStream is=null;
			try {
				s = new Socket(ip, port);
				os = s.getOutputStream();
				is=s.getInputStream();
				os.write(msg.getBytes());
				os.flush();	
				rev=readFromServer(is,s);
				return rev;
			} catch (Exception e) {
				rev="failed";
				e.printStackTrace();
			} finally {
				if (os != null)
					os.close();
				if (s != null)
					s.close();
			}
			return rev;
		}

	public static String readFromServer(InputStream in,Socket socket){
		String msg=null;
		try{  
			socket.setSoTimeout(3000);
        	byte[] buffer = new byte[1024];
        	int len = in.read(buffer);
        	msg = new String(buffer, 0, len);
        	return msg;     	
        }catch(SocketTimeoutException e){  
        	//从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常  
            System.out.println("Time out, No response");  
        } catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
}
