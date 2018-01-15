package com.njupt.ws_cxf_spring.ws.dao;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.njupt.ws_cxf_spring.ws.tools.Tools;

public class ParkingDao {
	
	private static final String url = "jdbc:oracle:thin:@10.10.22.6:1521:orcl";
	private static final String user = "scott";
	private static final String userpass = "123456";
	private static ComboPooledDataSource ds;
	private ResultSet rs;
	private Connection conn;
	private PreparedStatement pstmt;
	private static final String driver = "oracle.jdbc.driver.OracleDriver";
	/*public Dao() throws Exception {
		initDB();
	}*/
	
	/**
     * 初始化连接池代码块,全局静态代码块
     */
	static{
		initDB();
	}
	
	/**
	 * 初始化数据库操作，建立连接池,使用的是C3PO数据源
	 * 
	 * @throws Exception
	 */
	private static final void initDB() {
		ds = new ComboPooledDataSource();
		try {
			ds.setDriverClass(driver);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ds.setJdbcUrl(url);
		ds.setUser(user);
		ds.setPassword(userpass);
		ds.setMaxPoolSize(100);
		ds.setInitialPoolSize(10);
		ds.setMinPoolSize(5);
		ds.setMaxStatements(300);
		ds.setMaxIdleTime(10);
		ds.setMaxConnectionAge(100);
		//连接的最大空闲时间，如果超过这个时间，某个数据库连接还没有被使用，则会断开掉这个连接,单位秒  
		//ds.setMaxIdleTime(10);
		//配置连接的生存时间，超过这个时间的连接将由连接池自动断开丢弃掉。当然正在使用的连接不会马上断开，而是等待它close再断开。
		//ds.setMaxConnectionAge(50);
	}
	
	/*final String url = "jdbc:oracle:thin:@10.10.22.35:1521:xe";
	final String user = "iotdb";
	final String userpass = "system";

	ComboPooledDataSource ds;
	ResultSet rs;
	Connection conn;
	PreparedStatement pstmt;
	String driver = "oracle.jdbc.driver.OracleDriver";

	public DBTool() throws Exception {
		initDB();
	}

	*/
	
	
	/**
	 * 初始化数据库操作，建立连接池,使用的是C3PO数据源
	 * 
	 * @throws Exception
	 *//*
	public void initDB() throws Exception {
		ds = new ComboPooledDataSource();
		ds.setDriverClass(driver);
		ds.setJdbcUrl(url);
		ds.setUser(user);
		ds.setPassword(userpass);
		ds.setMaxPoolSize(30);
		ds.setInitialPoolSize(3);
		ds.setMinPoolSize(3);
		ds.setMaxStatements(300);
		//连接的最大空闲时间，如果超过这个时间，某个数据库连接还没有被使用，则会断开掉这个连接,单位秒  
		//ds.setMaxIdleTime(10);
		//配置连接的生存时间，超过这个时间的连接将由连接池自动断开丢弃掉。当然正在使用的连接不会马上断开，而是等待它close再断开。
		//ds.setMaxConnectionAge(50);
	}*/

	
	/**
	 * 查询指定传感器实时数据
	 * 
	 * @param deviceID
	 * @return
	 */
	public String getSenorPara(int deviceID) {
		String result = "";
		//String sql = "select value from(select * from w_data where deviceid=? order by savetime desc) where rownum=1";
		String sql = "select value from(select value from w_data where deviceid=? order by savetime desc) where rownum=1";
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, deviceID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result = rs.getString("value");
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 查询指定传感器最近一条数据的存储时间
	 * 
	 * @param deviceID
	 * @return
	 */
	public String getSenorTimePara(int deviceID) {
		//String result = "";
		StringBuilder sb = new StringBuilder();
		String sql = "select to_char(savetime,'YYYY-MM-DD HH24:MI:SS') from(select * from w_data where deviceid=? order by savetime desc) where rownum=1";
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, deviceID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString(1));
				//System.out.println("获取时间"+sb.toString());
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 查询某处所有传感器的最新数据
	 * 
	 * @param locationID
	 * @return
	 */
	public String getSensorRecords(int deviceID_start,int deviceID_end) {
		StringBuilder sb = new StringBuilder();
		int count=(deviceID_end-deviceID_start+1);
		String sql = "select * from (select deviceid, value, savetime from w_data where deviceid between ? and ? order by savetime desc) where rownum<="+count+" order by deviceid";
		try {
			long start_0=System.currentTimeMillis();
			conn = ds.getConnection();
			long start_1=System.currentTimeMillis();			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, deviceID_start);
			pstmt.setInt(2, deviceID_end);
			long start_2=System.currentTimeMillis();
			rs = pstmt.executeQuery();
			long start_3=System.currentTimeMillis();
			while (rs.next()) {
				sb.append(rs.getString(1) + ":" + rs.getString(2) +":"+rs.getString(3)+ ";");
			}
			long start_4=System.currentTimeMillis();
			System.out.println("建立连接时间："+(start_1-start_0));
			System.out.println("创建pstmt以及赋值时间："+(start_2-start_1));
			System.out.println("执行sql语句获取rs时间："+(start_3-start_2));
			System.out.println("拼凑成字符串时间："+(start_4-start_3));
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sb.toString();

	}
	
	/**
	 * 查询某个传感器最近十条的数据
	 * 
	 * @param deviceID
	 * @param time
	 *            YYYY-MM-DD
	 * @return
	 */
	public String getHistoryRecord(int deviceID) {
		StringBuilder sb = new StringBuilder();
		String sql = "select * from (select to_char(savetime,'YYYY-MM-DD HH24:MI:SS'), value from w_data where deviceid=? order by savetime desc) where rownum<11";
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, deviceID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString(1) + "#" + rs.getString(2) + ";");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sb.toString();

	}
	
	/**
	 * 查询某个传感器某一天的数据
	 * 
	 * @param deviceID
	 * @param time
	 *            YYYY-MM-DD
	 * @return
	 */
	public String getSensorRecord(int deviceID, String time) {
		StringBuilder sb = new StringBuilder();
		String sql = "select to_char(savetime,'YYYY-MM-DD HH24:MI:SS'), value from w_data where  trunc(savetime, 'DD')= to_date(?,'YYYY-MM-DD')  and deviceid=? order by savetime";
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, time);
			pstmt.setInt(2, deviceID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString(1) + " " + rs.getString(2) + ";");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sb.toString();

	}
	
	/**
	 *  通过位置ID查询子地址ID
	 * 
	 * @param locationID
	 * @return
	 */
	public String getAllChildLocationID(int deviceID) {
		StringBuilder sb = new StringBuilder();
		String sql = "select LOCATIONID,LOCATION from(select L1.LOCATIONID,L1.LOCATION,L1.PARENTID,L2.LOCATIONID as loc2id,L2.LOCATION as loc2,L2.PARENTID as loc2pid from W_LOCATION L1,W_LOCATION L2 where L1.PARENTID=L2.LOCATIONID order by L1.LOCATIONID)where loc2id=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, deviceID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString(1) + ":" + rs.getString(2) + ";");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 *  通过地址ID获取传感器属性ID，类型，名称
	 * 
	 * @param locationID
	 * @return
	 */
	public String getAllSensorsAttrByLocID(int locationID) {
		StringBuilder sb = new StringBuilder();
		String sql = "select deviceid,name,devicetype from(select di.deviceid,di.name,ds.name as devicetype,di.locationid from W_DEVICEINSTANCE di,W_DEVICESPEC ds where di.specid=ds.id)where locationid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, locationID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString(1) + ":" + rs.getString(2) + ":"+rs.getString(3)+";");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取所有的传感器类型
	 * @return
	 */
	public String getAllSensorsType() {
		StringBuilder sb = new StringBuilder();
		String sql = "select id,name from W_DEVICESPEC order by id";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString(1) + ":" + rs.getString(2) +";");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 根据传感器类型的编码获取部署了该类传感器的位置ID与位置描述信息
	 * @param devspec
	 * @return
	 */
	public String getAllLocationIDByDeviceSpec(int devspec){
		StringBuilder sb = new StringBuilder();
		String sql = "select distinct locationid, location  from (select di.locationid, lc.location,di.specid from W_DEVICEINSTANCE di,W_LOCATION lc where di.locationid=lc.locationid) where specid=? order by locationid ";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, devspec);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getInt(1) +":"+rs.getString(2) + ";");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取w_deviceinstance表数据
	 * @return
	 */
	public String getAllDeviceInstance() {
		StringBuilder sb = new StringBuilder();
		String sql = "select deviceid,name,specid,locationid from W_DEVICEINSTANCE order by deviceid";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getInt(1) + ":" + rs.getString(2) +":" + rs.getString(3) + ":" +rs.getInt(4) +";");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 根据deviceID获取该设备信息
	 * @param deviceID
	 * @return
	 */
	public String getDevInfoByDeviceID(int deviceID){
		StringBuilder sb = new StringBuilder();
		String sql = "select deviceid,name,specid,locationid from W_DEVICEINSTANCE where deviceid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, deviceID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getInt(1) + ":" + rs.getString(2) + ":" + rs.getString(3) + ":" + rs.getInt(4));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * 查询某个传感器某一小时的数据
	 * 
	 * @param deviceID
	 * @param time
	 *            YYYY-MM-DD
	 * @return
	 */
	/*public String getOneHourRecord(int deviceID, String starttime, String endtime) {
		StringBuilder sb = new StringBuilder();
		String sql = "select to_char(savetime,'YYYY-MM-DD HH24:MI:SS'), value from w_data where  trunc(savetime, 'hh')> to_date(?,'YYYY-MM-DD HH24') and trunc(savetime, 'hh')< to_date(?,'YYYY-MM-DD HH24') and deviceid=? order by savetime";
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, time);
			pstmt.setInt(2, deviceID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString(1) + " " + rs.getString(2) + ";");
			}
			return sb.toString();
		} catch (Exception e) {

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}

		return sb.toString();

	}*/
	
	/**
	 * 查询某一天的耗电量
	 * 
	 * @param deviceID
	 * @param time
	 *            YYYY-MM-DD
	 * @return
	 */
	public String getPowerRecord(int deviceID, String time) {
		StringBuilder sb = new StringBuilder();
		String sql = "select value from w_data where  trunc(savetime, 'DD')= to_date(?,'YYYY-MM-DD')  and deviceid=? order by savetime";
		double sum=0;
		Double d;
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, time);
			pstmt.setInt(2, deviceID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				d=Double.parseDouble(rs.getString(1));
				sum+=d.doubleValue();
				//System.out.println(sum);
				//sb.append(rs.getString(1) +";");
			}
			/*String[] buffer=sb.toString().split(";");
			for(int i=0;i<buffer.length;i++){
				System.out.println(buffer[i]);
     			Double value=Double.parseDouble(buffer[i]);
				sum+=value.doubleValue();
				System.out.println(sum);
			}*/
			System.out.println("一天耗电量："+sum*5);
			return (sum*5)+"";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return (sum*5)+"";

	}
	

	/**
	 * 向表w_athority1中插入句柄，句柄生成时间以及结束时间
	 * 
	 * @param handle
	 * @return
	 * @throws Exception
	 */
	public int inserthandle(String handle, int userid) {
		//第一个字段为handle值，第二个插入时间，第三个失效时间，第四个用户ID
		String sql = "insert into w_authority values(?,sysdate,sysdate+1/24,?,1)";
		int flag=0;
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, handle);
			pstmt.setInt(2, userid);
			flag = pstmt.executeUpdate();
			System.out.println("inserthandle(String handle, int userid)");
			return flag;
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try{
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return flag;
	}

	/**
	 * 判断用户是否合法
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @return
	 */
	public int islegalUser(String username, String password) {
		String sql = "select * from w_user where username=?";
		int c = 0;
		String b = Tools.getMD5Str(password);
		System.out.println("用户输入的密码："+b);
		String a = "";
		try {
			System.out.println("进入try块！！！");
			conn = ds.getConnection();
			System.out.println("1");
			pstmt = conn.prepareStatement(sql);
			System.out.println("2");
			pstmt.setString(1, username);
			System.out.println("3");
			rs = pstmt.executeQuery();
			System.out.println("运行sql语句");
			System.out.println("islegalUser(String username, String password)");
			if (rs.next()) {
				a = Tools.getMD5Str(rs.getString(3));
				System.out.println("用户注册在数据库中的密码："+a);
				if (a.equals(b)) {
					c = rs.getInt(1);//返回该用户对应的序号，序号必然大于0
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return c;
	}

	/**
	 * 判断句柄是否合法以及是否有效
	 * 
	 * @param handle
	 * @return
	 */
	public boolean islegalhandle(String handle) {
		String sql = "select * from w_authority where handle=?";
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, handle);
			rs = pstmt.executeQuery();
			System.out.println("islegalhandle(String handle)");
			if (rs.next()) {
				//判断handle是否超过了时效
				String a = rs.getString(3);//handle的有用时间

				String b = Tools.getCurrentTime();//系统目前的时间
				int c = Tools.timeCompare(b, a);
				if (c < 0) {//目前时间比时效时间要小则说明handle值是有效的
					return true;
				} else//否则handle失效
					return false;
			} else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;

	}

	/**
	 * 更新句柄对应表的数据
	 * 
	 * @param handle
	 * @return
	 * @throws Exception
	 */
	public int updatehandle(String handle) {
		String sql = "update w_authority set endtime=sysdate,survivaltime=(sysdate-begintime)*24 where handle=?";
		int flag=0;
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, handle);

			flag = pstmt.executeUpdate();
			System.out.println("updatehandle(String handle)");
			return flag;
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			try{
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return flag;
	}

	public String[] getMsg() throws Exception {
		String[] msg = new String[15];

		// 注意Min_Max_Query0()和Min_Max_Query()是不同的两个函数,截取位数不一样。
		String[] result1 = min_Max_Query0(13); // 查询机房当天最低温度和最高温度
		String[] result2 = min_Max_Query0(12); // 查询机房当天最低湿度和最高湿度
		String[] result3 = min_Max_Query(18);// B相最低和最高电压
		String[] result4 = min_Max_Query(20);// A相最低和最高电流
		String[] result5 = min_Max_Query(29);// A相视在功率
		String[] result6 = min_Max_Query(30);// B相视在功率
		String[] result7 = min_Max_Query(31);// C相视在功率

		String[] result8 = min_Max_Query1(76); // 查询备份机房当天最低温度和最高温度
		String[] result9 = min_Max_Query1(77); // 查询备份机房当天最低湿度和最高湿度

		String minTemperature = result1[0];// 一天中的最低温度
		String maxTemperature = result1[1];// 一天中的最高温度
		String avgTemperature = result1[2];// 一天的平均气温

		String minTemperature_second = result8[0] + "℃";// 备份机房一天中的最低温度
		String maxTemperature_second = result8[1] + "℃";// 一天中的最高温度
		String avgTemperature_second = result8[2] + "℃";// 一天的平均气温

		String minhumidity = result2[0];// 一天中的最低湿度
		String maxhumidity = result2[1];// 一天中的最高湿度
		String avghumidity = result2[2];// 一天的平均湿度

		String minhumidity_second = result9[0] + "%";// 备份机房一天中的最低湿度
		String maxhumidity_second = result9[1] + "%";// 一天中的最高湿度
		String avghumidity_second = result9[2] + "%";// 一天的平均湿度

		String minVoltage = result3[0];// 一天中的最低电压
		String maxVoltage = result3[1];// 一天中的最高电压
		String avgVoltage = result3[2];// 一天的平均电压

		String minCurrent = result4[0];// 一天中的最低电流
		String maxCurrent = result4[1];// 一天中的最高电流
		String avgCurrent = result4[2];// 一天的平均电流

		String avgApower = result5[2];// A相视在平均功率
		String avgBpower = result6[2];// B相视在平均功率
		String avgCpower = result7[2];// C相视在平均功率
		String[] power = { avgApower, avgBpower, avgCpower };
		Object energy;
		if (Tools.isIlegal(power, 0, 3)) {
			energy = Double.parseDouble(avgApower) * 24
					+ Double.parseDouble(avgBpower) * 24
					+ Double.parseDouble(avgCpower) * 24;
		} else
			energy = null;
		// 总电量
		String strenergy = energy + "";// 转变为字符串形式
		msg[0] = "机房最低温度为" + minTemperature + "℃,最高温度为" + maxTemperature
				+ "℃,平均温度为" + avgTemperature + "℃,最低湿度为" + minhumidity
				+ "%,最高湿度为" + maxhumidity + "%,平均湿度为" + avghumidity + "%";
		msg[1] = "机房最低电压为" + minVoltage + "v,最高电压为" + maxVoltage + "v,最小电流为"
				+ minCurrent + "A,最大电流为" + maxCurrent + "A";
		msg[2] = "机房耗电量为" + strenergy + "度(千瓦时)";
		msg[3] = "第二机房最低温度为" + minTemperature_second + "℃,最高温度为"
				+ maxTemperature_second + "℃,平均温度为" + avgTemperature_second
				+ "℃,最低湿度为" + minhumidity_second + "%,最高湿度为"
				+ maxhumidity_second + "%,平均湿度为" + avghumidity_second + "%";
		msg[4] = minTemperature;
		msg[5] = maxTemperature;
		msg[6] = avgTemperature;
		msg[7] = minhumidity;
		msg[8] = maxhumidity;
		msg[9] = avghumidity;
		msg[10] = minVoltage;
		msg[11] = maxVoltage;
		msg[12] = minCurrent;
		// msg[13]=maxVoltage;
		// msg[14]=minCurrent;
		msg[13] = maxCurrent;
		msg[14] = strenergy;
		return msg;
	}

	/**
	 * 温度和湿度查询函数(这里之所以跟下面函数基本一样，是因为有数据库中有部分数据，存进去时是错误数据，导致数据库avg（）不能求出正确数据，
	 * 所以另外单独写了个函数) 后面需要对存入数据库的数据格式进行格式验证，以免乱七八糟的数据存入数据库
	 * 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	private String[] min_Max_Query0(int id) throws Exception {

		String[] result = new String[3];
		String sql = "select min(value),max(value),substr(avg(value),0,4) from (select * from w_data where deviceid=?)where savetime between trunc(sysdate) and trunc(sysdate)+1";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			rs.next();
			result[0] = rs.getString(1);// min
			result[1] = rs.getString(2);// max
			// result[2] = rs.getString(3);//avg
			if (Tools.isIlegal(result, 0, 2)) {
				result[2] = (Double.parseDouble(result[0]) + Double
						.parseDouble(result[1])) / 2 + "";
			} else
				result[2] = null;

		} catch (Exception ex) {
			System.out.println("数据库查询温湿度出现异常");
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();

		}
		return result;
	}

	// 温度和湿度查询函数
	private String[] min_Max_Query1(int id) throws Exception {

		String[] result = new String[3];
		String sql = "select min(value),max(value) from (select * from w_data where deviceid=?)where savetime between trunc(sysdate) and trunc(sysdate)+1";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			rs.next();
			result[0] = rs.getString(1);// min
			System.out.println("result0" + result[0]);
			result[1] = rs.getString(2);// max
			System.out.println("result1" + result[1]);
			if (Tools.isIlegal(result, 0, 2)) {
				result[2] = (Double.parseDouble(result[0]) + Double
						.parseDouble(result[1])) / 2 + "";
			} else
				result[2] = null;
			System.out.println("result2" + result[2]);
		} catch (Exception ex) {
			System.out.println("数据库查询温湿度出现异常");
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();

		}
		return result;
	}

	// 电流电压耗电量参数查询函数
	private String[] min_Max_Query(int id) throws Exception {
		String[] result = new String[3];
		String sql = "select min(value),max(value),substr(avg(value),0,6) from (select * from w_data where deviceid=?) where savetime between trunc(sysdate) and trunc(sysdate)+1";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			rs.next();
			result[0] = rs.getString(1);// min
			result[1] = rs.getString(2);// max
			result[2] = rs.getString(3);// avg
		} catch (Exception ex) {
			System.out.println("数据库查询电参数出现异常");
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();

		}
		return result;
	}
	
	/**
	 * 注册时用来判断用户名是否存在
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public boolean findByUsername(String username){
		boolean value=false;
		String sql = "select count(*) from w_user_info where username=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			System.out.println("findByUsername(String username)");
			while (rs.next()) {
				int res=rs.getInt(1);
				if(res>0){
					value=true;
				}
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	/**
	 * 存储车位信息时用来判断车位信息是否存在
	 * @param deviceid
	 * @return
	 * @throws Exception
	 */
	public boolean findByDeviceID(int deviceid){
		boolean value=false;
		String sql = "select count(*) from w_parking_data where deviceid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, deviceid);
			rs = pstmt.executeQuery();
			System.out.println("findByDeviceID(int deviceid)");
			while (rs.next()) {
				int res=rs.getInt(1);
				if(res>0){
					value=true;
				}
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	/**
	 * 预约车位时用来判断用户记录是否存在
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public boolean findByUserID(int userid){
		boolean value=false;
		String sql = "select count(*) from w_reservation where userid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			rs = pstmt.executeQuery();
			System.out.println("findByUserID(int userid)");
			while (rs.next()) {
				int res=rs.getInt(1);
				if(res>0){
					value=true;
				}
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	/**
	 * 根据userid判断用户是否登记过车牌
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public boolean findPropertyByUserID(int userid){
		boolean value=false;
		String sql = "select count(*) from w_user_property where userid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			rs = pstmt.executeQuery();
			System.out.println("findPropertyByUserID(int userid)");
			while (rs.next()) {
				int res=rs.getInt(1);
				if(res>0){
					value=true;
				}
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	/**
	 * 根据用户名查找对应userid
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public int getUserIDByUsername(String username){
		int userID=0;
		String sql = "select userid from w_user_info where username=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			System.out.println("getUserIDByUsername(String username)");
			while (rs.next()) {
				userID=rs.getInt(1);
			}
			return userID;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userID;
	}
	/**
	 * 用户注册
	 * @param username
	 * @param password
	 * @param telnum
	 * @return
	 * @throws Exception
	 */
	public String userRegister(String username, String password, String telnum) {
		String res="用户名已存在";
		boolean exist=findByUsername(username);
		if(!exist){
			String sql = "insert into w_user_info values(userid_increase.nextval,?,?,?,?,sysdate)";
			try {
				conn = ds.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, username);
				pstmt.setString(2, password);
				pstmt.setString(3, telnum);
				pstmt.setInt(4, 5000);
				int flag = pstmt.executeUpdate();
				System.out.println("userRegister(String username, String password, String telnum)");
				if(flag==1){
					userAuthorization(conn,username,"1");
					return "success";
					
				}else{
					return "failed";
				}
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				try {
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return res;
		}else{
			return res;
		}
	}
	
	public void userAuthorization(Connection conn,String username,String priority){
		String sql = "insert into w_authorization values(?,?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, priority);
			pstmt.executeUpdate();
			System.out.println("userAuthorization(Connection conn,String username,String priority)");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 用户登录，成功则返回username
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String userLogin(String username, String password){
		String value="用户名或密码错误";
		String sql = "select username from w_user_info where username=? and password=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();
			System.out.println("userLogin(String username, String password)");
			while (rs.next()) {
				value=rs.getString(1);
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	

	/**
	 * 普通车位查询14/17
	 * @return
	 */
	public String parkingStatesQuery() {
		String res="17/17";
		String sql = "select count(*) from w_parking_data where value=1";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("parkingStatesQuery()");   
			while (rs.next()) {
				res=rs.getString(1)+"/17";
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * 可预约的车位查询，1/2/3/，需考虑到被预约掉的车位
	 * @return
	 */
	public String parkingReservationQuery() {
		StringBuilder sb=new StringBuilder();
		List<Integer> list=new ArrayList<Integer>();
		List<Integer> tempList=alreadyReserved();
		String sql = "select dataid from w_parking_resv_data where value=0 order by dataid";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("parkingReservationQuery()");   //*********执行到此处卡住**********
			while (rs.next()) {
				System.out.println("测试1");
				list.add(rs.getInt(1));
				System.out.println("测试2");
			}
			System.out.println("测试3");
			/*List<Integer> tempList=alreadyReserved();
			System.out.println("测试4");*/
			for(int i=0;i<tempList.size();i++){
				for(int j=0;j<list.size();j++){
					if(tempList.get(i)==list.get(j)){
						list.remove(j);
					}
				}
			}
			for (int k = 0; k < list.size(); k++) {
				sb.append(list.get(k)+"/");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 返回已经被预约的车位号
	 * @return
	 */
	public List<Integer> alreadyReserved() {
		List<Integer> list=new ArrayList<Integer>();
		String sql = "select lockid from w_reservation where resvstate='Y'";
		try {
			System.out.println("alreadyReserved1");
			conn = ds.getConnection();
			System.out.println("alreadyReserved2");
			pstmt = conn.prepareStatement(sql);
			System.out.println("alreadyReserved3");
			rs = pstmt.executeQuery();
			System.out.println("alreadyReserved()");
			while (rs.next()) {
				list.add(rs.getInt(1));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 预约车位，指定车位号
	 * @param username
	 * @param lockid
	 * @param resvstate
	 * @return
	 * @throws Exception
	 */
	public String parkingReservation(String username, int lockid) throws Exception {
		int userID=getUserIDByUsername(username);//根据username找到对应的ID
		//判断车位是否已经被预约
		if(hasReserved(lockid)){
			return "该车位已被预约";
		}
		//判断车位是否已经被使用
		if(hasParked(lockid)){
			return "该车位正在使用";
		}
		//判断用户是否已经预约过，不能出现预约多个车位的情况
		boolean exist=findByUserID(userID);
		//如果已经预约过，则将预约时间更改
		if(exist){
			String sql="update w_reservation set lockid=?,resvstate=?,starttime=sysdate,endtime=sysdate+1/48 where userid=?";
			try {
				conn = ds.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, lockid);
				pstmt.setString(2, "Y");
				pstmt.setInt(3, userID);
				int flag = pstmt.executeUpdate();
				System.out.println("parkingReservation(String username, int lockid)");
				if(flag==1){
					return "success";
		
				}else{
					return "failed";
				}
			} finally {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}else{
			//如果没有预约则直接预约，将记录插入表格
			String sql = "insert into w_reservation values(?,?,?,sysdate,sysdate+1/24)";
			try {
				conn = ds.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, userID);
				pstmt.setInt(2, lockid);
				pstmt.setString(3, "Y");
				int flag = pstmt.executeUpdate();
				System.out.println("parkingReservation(String username, int lockid)");
				if(flag==1){
					return "success";
					
				}else{
					return "failed";
				}
			} finally {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}
	}
	
	public boolean hasParked(int lockid) {
		boolean has=false;	
		String sql = "select value from w_parking_resv_data where dataid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, lockid);
			rs = pstmt.executeQuery();
			System.out.println("hasParked(int lockid)");
			while (rs.next()) {
				if(rs.getString(1).equals("1")){
					has=true;
				}
			}
			return has;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return has;
	}

	/**
	 * 判断该车位是否已经被预约
	 * @param lockid
	 * @return
	 * @throws Exception
	 */
	public boolean hasReserved(int lockid){
		boolean has=false;	
		String sql = "select resvstate from w_reservation where lockid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, lockid);
			rs = pstmt.executeQuery();
			System.out.println("hasReserved(int lockid)");
			while (rs.next()) {
				if(rs.getString(1).equals("Y")){
					has=true;
				}
			}
			return has;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return has;
	}
	
	/**
	 * 取消预约车位
	 * @param username
	 * @param lockid
	 * @param resvstate
	 * @return
	 * @throws Exception
	 */
	public String parkingReservationCancel(String username, int lockid) throws Exception {
		int userID=getUserIDByUsername(username);
		String resvstate="N";
		String sql="update w_reservation set lockid=?,resvstate=? where userid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, lockid);
			pstmt.setString(2, resvstate);
			pstmt.setInt(3, userID);
			int flag = pstmt.executeUpdate();
			System.out.println("parkingReservationCancel(String username, int lockid)");
			if(flag==1){
				return "success";
				
			}else{
				return "failed";
			}
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 登记车牌号
	 * @param username
	 * @param carnum
	 * @return
	 * @throws Exception
	 */
	public String carNumReg(String username, String carnum) throws Exception {
		int userID=getUserIDByUsername(username);//根据用户名称获取用户ID
		boolean exist=findPropertyByUserID(userID);//根据ID找到是否有对应的记录
		if(exist){
			//如果已经有了记录，则将原来的进行修改
			String sql="update w_user_property set carnum=? where userid=?";
			try {
				conn = ds.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, carnum);
				pstmt.setInt(2, userID);
				int flag = pstmt.executeUpdate();
				System.out.println("carNumReg(String username, String carnum)");
				if(flag==1){
					return "success";
		
				}else{
					return "failed";
				}
			} finally {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}else{
			//如果原来没有记录则新插入一条对应的记录
			String sql = "insert into w_user_property values(carnum_increase.nextval,?,?)";
			try {
				conn = ds.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, userID);
				pstmt.setString(2, carnum);
				int flag = pstmt.executeUpdate();
				System.out.println("carNumReg(String username, String carnum)");
				if(flag==1){
					return "success";
					
				}else{
					return "failed";
				}
			} finally {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}	
		}
	}

	/**
	 * 用户到达停车场，控制相应预约的车锁下降，同时开始计时计费
	 * @param username
	 * @param control
	 * @return
	 * @throws Exception
	 */
	public String parkingLockControl(String username,String control) throws Exception{
		//判断预约时间，超时则直接返回
		int userID=getUserIDByUsername(username);//根据用户名称查询对应的ID
		int lockID=findLockIDByUserID(userID);//查询用户已经预约的车位号，如没有预约则返回0
		//判断预约时间是否已过，若过了预约的时间，则将预约自动取消，返回“预约已超时”
		if (isOutOfTime(userID)){
			//修改预约状态，改为N
			parkingReservationCancel(username, lockID);
			return "预约已超时";
		}
		//判断用户是否进行了预约，若没有预约则返回“未预约车位”
		if(getReservationState(userID).equals("N")){
			return "未预约车位";
		}
		//未超时，继续执行
		String res=null;
		String orderUp="[{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\":\"e0001\",\"ukid\":\"11100"+ lockID+ "\",\"set\":\"2\"}]";
		String orderDown="[{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\":\"e0001\",\"ukid\":\"11100"+ lockID+ "\",\"set\":\"1\"}]";
		if(control.equals("up")){
			System.out.println(orderUp);
			res=Tools.tcpSendWithServer(orderUp,"10.10.23.91",40000);	
			System.out.println(res);
			if(res.equals("failed")){
				return "控制失败";
			}
			if(res.equals("{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\",\"e0001\",\"ack\":\"0\"}")){
/*				//用户离开，停止计时
				parkingEnd(userID);*/
				return "控制成功";
			}
		}else if(control.equals("down")){
			//控制锁下降，用户到达
			System.out.println(orderDown);
			res=Tools.tcpSendWithServer(orderDown,"10.10.23.91",40000);
			System.out.println(res);
			if(res.equals("failed")){
				return "控制失败";
			}
			if(res.equals("{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\",\"e0001\",\"ack\":\"0\"}")){
				//用户到达，开始计时,防止重复计时
				if(!parkState(username).equals("parked")){
					parkingStart(userID, lockID);					
//					parkingReservationCancel(username, lockID); //预约状态改为N
				}
				return "控制成功";
			}		
		}
		return "控制失败";
		
	}
	
	public String getReservationState(int userid){
		// 查询w_reservation中用户的预约状态
		StringBuilder sb = new StringBuilder();		
		String sql = "select resvstate from w_reservation where userid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			rs = pstmt.executeQuery();
			System.out.println("getReservationState(int userid)");
			while (rs.next()) {
				sb.append(rs.getString(1));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 判断预约时间是否超时
	 * 
	 * @param handle
	 * @return
	 */
	public boolean isOutOfTime(int userid) {
		String sql = "select * from w_reservation where userid=?";
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			rs = pstmt.executeQuery();
			System.out.println("isOutOfTime(int userid)");
			if (rs.next()) {
				String endtime = rs.getString(5);
				String currenttime = Tools.getCurrentTime();
				int compare = Tools.timeCompare(currenttime, endtime); //比较currenttime，endtime的大小，currenttime大则返回1，相等则返回0，否则-1
				if (compare > 0) {
					return true;
				} else
					return false;
			} else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;

	}
	/**
	 * w_reservation表中根据userid查找lockid
	 * @param userid
	 * @return
	 */
	public int findLockIDByUserID(int userid){
		int lockid=0;
		String sql = "select lockid from w_reservation where userid=?";
		try {
			conn = ds.getConnection();
			// ---------------------------------------------------
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			rs = pstmt.executeQuery();
			System.out.println("findLockIDByUserID(int userid)");
			if (rs.next()) {
				lockid = rs.getInt(1);
				return lockid;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lockid;
	}
	
	/**
	 * 停车时用来判断w_parking_state中对应用户记录是否存在
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public boolean existUserID(int userid)throws Exception{
		boolean value=false;
		String sql = "select count(*) from w_parked_state where userid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			rs = pstmt.executeQuery();
			System.out.println("existUserID(int userid)");
			while (rs.next()) {
				int res=rs.getInt(1);
				if(res>0){
					value=true;
				}
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	/**
	 * 记录开始停车时间
	 * @param username
	 * @param lockid
	 * @param resvstate
	 * @return
	 * @throws Exception
	 */
	public String parkingStart(int userid, int lockid) throws Exception {
		boolean exist=existUserID(userid);
		String tagstate="parked";
		if(exist){
			String sql="update w_parked_state set lockid=?,tagstate=?,parktime=sysdate where userid=?";
			try {
				conn = ds.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, lockid);
				pstmt.setString(2, tagstate);
				pstmt.setInt(3, userid);
				int flag = pstmt.executeUpdate();
				System.out.println("parkingStart(int userid, int lockid)");
				if(flag==1){
					return "success";
		
				}else{
					return "failed";
				}
			} finally {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}else{
			String sql = "insert into w_parked_state values(?,?,?,sysdate,null)";
			try {
				conn = ds.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, userid);
				pstmt.setInt(2, lockid);
				pstmt.setString(3, tagstate);
				int flag = pstmt.executeUpdate();
				System.out.println("parkingStart(int userid, int lockid)");
				if(flag==1){
					return "success";
					
				}else{
					return "failed";
				}
			} finally {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}
	}
	
	/**
	 * 记录离开时间，同时支付，升起锁
	 * @param username
	 * @param lockid
	 * @param resvstate
	 * @return
	 * @throws Exception
	 */
	public String parkingEnd(int userid) throws Exception {
		String tagstate="left";
		String sql="update w_parked_state set tagstate=?,leavetime=sysdate where userid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tagstate);
			pstmt.setInt(2, userid);
			int flag = pstmt.executeUpdate();
			System.out.println("parkingEnd(int userid)");
			if(flag==1){
				return "success";
				
			}else{
				return "failed";
			}
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 获取用户信息，用户名/手机号/余额
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public String userInfo(String username) throws Exception{
		StringBuilder sb = new StringBuilder();
		String sql = "select username,telnum,balance from w_user_info where username=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			System.out.println("userInfo(String username)");
			while (rs.next()) {
				sb.append(rs.getString(1) + "/"+rs.getString(2)+"/"+rs.getInt(3));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 查询当前停车时间
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public long consumeInfo(String username) throws Exception{
		// 查询w_parked_state表停车时间
		long time =0;
		String parkstate=parkState(username);//返回用户的当前停车状态
		if(parkstate.equals("parked")){
			int userID=getUserIDByUsername(username);
			String sql = "select to_char(parktime,'YYYY-MM-DD HH24:MI:SS') from w_parked_state where userid=?";
			try {
				conn = ds.getConnection();
				pstmt = conn.prepareStatement(sql);			
				pstmt.setInt(1, userID);
				rs = pstmt.executeQuery();
				System.out.println("consumeInfo(String username)");
				while (rs.next()) {
					String parktime=rs.getString(1);
					DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date=df.parse(parktime);
					long current=System.currentTimeMillis();
					time=current-date.getTime();
//					System.out.println("当前时间："+current+"--"+"停车时间："+date.getTime());
				}
				return time/60000;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return time;	
		}else{
			return time;
		}
	}
	
	/**
	 * 支付停车积分
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public String parkingConsume(String username) throws Exception{
		String parkingState=parkState(username);
		if(parkingState.equals("left")){
			return "未预约或未停车";
		}
		//算出w_parked_state表的停车时间,计算费用
		int userID=getUserIDByUsername(username);
		long consume=consumeInfo(username)*1;   //消费
		String res=accountOut(username,consume);		//扣除金额
		if(res.equals("success")){
			String upResult=parkingLockUp(username, "up");   //支付完成升起车位锁	
			parkingEnd(userID);    //状态置为left
			System.out.println("控制锁上升结果："+upResult);
			if(upResult.equals("控制成功")){
				return "控制成功，共支付："+consume;
			}else{
				return "控制失败";		
			}
		}else{
			return "failed";
		}		
	}
	
	public String parkingLockUp(String username,String control) throws Exception{
		int userID=getUserIDByUsername(username);
		int lockID=findLockIDByUserID(userID);
		//通过停车状态来判断
		String parkstate=parkState(username);
		System.out.println("parkState:"+parkstate);
		String orderUp="[{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\":\"e0001\",\"ukid\":\"11100"+ lockID+ "\",\"set\":\"2\"}]";
		if(parkstate.equals("parked")){
			//可支付
			String res=null;
			System.out.println("进入parked条件判断循环");
			//String orderUp="[{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\":\"n0001\",\"ukid\":\"70000"+ lockID+ "\",\"set\":\"2\"}]";
			if(control.equals("up")){
				System.out.println("进入up条件判断循环");
				System.out.println(orderUp);
				res=Tools.tcpSendWithServer(orderUp,"10.10.23.91",40000);	
				System.out.println(res);
				if(res.equals("failed")){
					return "控制失败";
				}
				if(res.equals("{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\",\"e0001\",\"ack\":\"0\"}")){
					/*				//用户离开，停止计时
				parkingEnd(userID);*/
					parkingReservationCancel(username, lockID); //预约状态改为N
					return "控制成功";
				}
			}
		}
		System.out.println("跳过循环");
		parkingReservationCancel(username, lockID); //预约状态改为N
		System.out.println(Tools.tcpSendWithServer(orderUp,"10.10.23.91",40000));	  //测试
		return "控制成功";
		
	}
	
	
	/**
	 * 扣除账户消费金额
	 * @param username
	 * @param consume
	 * @throws SQLException 
	 */
	private String accountOut(String username, long consume) throws SQLException {
		String sql="update w_user_info set balance=? where username=?";
		int balance_before=loadAccount(username);
		int balance_after=balance_before-(int)consume;
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, balance_after);
			pstmt.setString(2, username);
			int flag = pstmt.executeUpdate();
			System.out.println("accountOut(String username, long consume)");
			if(flag==1){
				return "success";
				
			}else{
				return "failed";
			}
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		
	}
	
	/**
	 * 账户充值
	 * @param username
	 * @param consume
	 * @throws SQLException 
	 */
	private String accountIn(String username, long consume) throws Exception {
		String sql="update w_user_info set balance=? where username=?";
		int balance_before=loadAccount(username);
		int balance_after=balance_before+(int)consume;
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, balance_after);
			pstmt.setString(2, username);
			int flag = pstmt.executeUpdate();
			System.out.println("accountIn(String username, long consume)");
			if(flag==1){
				return "success";
				
			}else{
				return "failed";
			}
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		
	}
	
	public int loadAccount(String username){
		int balance=0;
		String sql = "select balance from w_user_info where username=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			System.out.println("loadAccount(String username)");
			while (rs.next()) {
				balance=rs.getInt(1);
			}
			return balance;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return balance;
	}

	/**
	 * 返回用户当前停车状态，parked，left
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public String parkState(String username) throws Exception{
		// 查询w_parked_state表停车时间
		int userID=getUserIDByUsername(username);
		StringBuilder sb = new StringBuilder();		
		String sql = "select tagstate from w_parked_state where userid=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			rs = pstmt.executeQuery();
			System.out.println("parkState(String username)");
			while (rs.next()) {
				sb.append(rs.getString(1));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String rechargeAccount(String username,int money) throws Exception {
		String res=accountIn(username,money);		//充值
		return res;
	}

	
	/**
	 * 返回用户权限值
	 * @param username
	 * @return
	 */
	public String getAuthorizationByUsername(String username) {
		StringBuilder sb = new StringBuilder();		
		String sql = "select priority from w_authorization where username=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			System.out.println("getAuthorizationByUsername(String username)");
			while (rs.next()) {
				sb.append(rs.getString(1));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String getFreeNum() {
		StringBuilder sb=new StringBuilder();
		String sql = "select dataid from w_parking_data where value=0 order by dataid";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("getFreeNum()");
			while (rs.next()) {
				sb.append(rs.getInt(1)+"/");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String getParkedNum() {
		StringBuilder sb=new StringBuilder();
		String sql = "select dataid from w_parking_resv_data where value=1 order by dataid";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("getParkedNum()");
			while (rs.next()) {
				sb.append(rs.getInt(1)+"/");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String getResvID(String username) {
		StringBuilder sb=new StringBuilder();
		/*String sql = "select dataid from w_parking_resv_data where value=1 order by dataid";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("getParkedNum()");
			while (rs.next()) {
				sb.append(rs.getInt(1)+"/");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return sb.toString();
	}

	public String parkingLockControlForTest(int userid,int lockid, String control) throws Exception {
		String res=null;
		String orderUp="[{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\":\"e0001\",\"ukid\":\"11100"+ lockid+ "\",\"set\":\"2\"}]";
		String orderDown="[{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\":\"e0001\",\"ukid\":\"11100"+ lockid+ "\",\"set\":\"1\"}]";
		if(control.equals("up")){
			if(!hasParked(lockid)){
				System.out.println(orderUp);
				res=Tools.tcpSendWithServer(orderUp,"10.10.23.91",40000);	
				System.out.println(res);
				if(res.equals("failed")){
					return "控制失败";
				}
				if(res.equals("{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\",\"e0001\",\"ack\":\"0\"}")){
					//用户离开，停止计时
					parkingEnd(userid);
					return "控制成功";
				}
			}else{
				return "该车位有车";
			}
		}else if(control.equals("down")){
			//控制锁下降，用户到达
			System.out.println(orderDown);
			res=Tools.tcpSendWithServer(orderDown,"10.10.23.91",40000);
			System.out.println(res);
			if(res.equals("failed")){
				return "控制失败";
			}
			if(res.equals("{\"tag\":\"ulock\",\"aid\":\"07\",\"gwid\",\"e0001\",\"ack\":\"0\"}")){
				//用户到达，开始计时,防止重复计时
				parkingStart(userid, lockid);
				return "控制成功";
			}		
		}
		return "控制失败";
	}
	

}
