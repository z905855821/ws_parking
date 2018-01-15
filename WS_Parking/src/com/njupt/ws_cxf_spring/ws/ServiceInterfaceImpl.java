package com.njupt.ws_cxf_spring.ws;

import javax.jws.WebService;

import com.njupt.ws_cxf_spring.ws.dao.ParkingDao;
import com.njupt.ws_cxf_spring.ws.tools.Tools;

@WebService
public class ServiceInterfaceImpl implements ServiceInterface {

	ParkingDao db;

	public ServiceInterfaceImpl()throws Exception {
		System.out.println("ServiceInterfaceImpl()");
		db = new ParkingDao();
	}

	/*
	 * 判断用户是否合法，合法则分配具有时效的handle值
	 * 
	 * @see com.nupt.cloud.ServiceInterface#getPermission(java.lang.String,
	 * java.lang.String)
	 */
	public String getPermission(String username, String passwords)
			throws Exception {
//		System.out.println(username);
		int a = db.islegalUser(username, passwords);
		System.out.println("用户id:"+a);
		System.out.println("***************************************************************");
		//若用户名和密码匹配，则返回用户对应的ID，其必然大于0，说明用户合法，给其分配权限，即一个UUID值
		//若为0，则用户不合法，返回"你输入的用户不合法"
		if (a > 0) {
			String b;
			b = Tools.GetRandomNumber();
			System.out.println("生成的句柄："+b);
			db.inserthandle(b, a);//给对应用户插入生成的handle值
			System.out.println("句柄插入成功");
			return b;
		} else
			return "你输入的用户不合法";

	}
	
	/*
	 * 执行注销操作 (non-Javadoc)     
	 * 
	 * @see com.nupt.cloud.ServiceInterface#logout(java.lang.String)
	 */
	@Override
	public String logout(String handle) throws Exception {
		if (db.islegalhandle(handle)) {//判断handle值有没有失去时效，即是否为一个合法的handle值
			db.updatehandle(handle);
			return "成功注销！";
		} else
			return "未能成功注销！";
	}
	
	/*用户进行注册，注册信息姓名、密码和电话号码
	 * 若注册成功则返回success，否则返回failed，
	 * 如果handle值超过时效就返回“你未得到注册授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#userRegister(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String userRegister(String handle,String username, String password, String telnum) {
//		System.out.println("ceshi:"+handle+"--"+username+"--"+telnum);
		if (db.islegalhandle(handle)) {
//			System.out.println("进入if");
			String value = db.userRegister(username, password,  telnum);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到注册授权";
	}
	
	/*用户进行登录，通过用户名和密码是否匹配进行判断
	 * 先判断handle值是否失效，若失效则返回“你未得到授权”
	 * 若登陆成功则返回username，否则返回“用户名或密码错误”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#userLogin(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String userLogin(String handle,String username, String password) {
//		System.out.println(handle+username+password);
		if (db.islegalhandle(handle)) {
			String value = db.userLogin(username, password);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 普通车位查询，返回格式14/17，14表示空余的车位数，17表示普通车位空余的总数；若获取失败则返回17/17
	 * handle值失效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#parkingStatesQuery(java.lang.String)
	 */
	@Override
	public String parkingStatesQuery(String handle) throws Exception{
		if (db.islegalhandle(handle)) {
			String value = db.parkingStatesQuery();
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 可以预约的车位查询，返回格式为1/2/3
	 * handle值失效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#parkingReservationQuery(java.lang.String)
	 */
	@Override 
	public String parkingReservationQuery(String handle) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.parkingReservationQuery();
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 预约指定的车位号，成功返回success，失败返回failed
	 * 若该车位已被预约则返回“该车位已被预约”
	 * 若该车位已经在使用则返回“该车位正在使用”
	 * handle失效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#parkingReservation(java.lang.String, java.lang.String, int)
	 */
	@Override
	public String parkingReservation(String handle, String username, int lockid) throws Exception{
		if (db.islegalhandle(handle)) {
			String value = db.parkingReservation(username,lockid);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 取消车位预约，取消成功返回success，取消失败返回failed
	 * handle失效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#parkingReservationCancel(java.lang.String, java.lang.String, int)
	 */
	@Override
	public String parkingReservationCancel(String handle,String username, int lockid)throws Exception{
		if (db.islegalhandle(handle)) {
			String value = db.parkingReservationCancel(username,lockid);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 对车牌进行登记注册，若登记成功则返回success，否则返回failed
	 * handle值失效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#carNumReg(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String carNumReg(String handle, String username, String carnum) throws Exception{
		if (db.islegalhandle(handle)) {
			String value = db.carNumReg(username,carnum);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}

	/*
	 * 用户到达后控制车位锁下降，具体的下发逻辑和判断看具体的Dao层的方法
	 * handle超过时效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#parkingLockControl(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String parkingLockControl(String handle, String username, String control) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.parkingLockControl(username,control);
			System.out.println("---------------------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 获取用户信息，用户名/手机号/余额
	 * handle失效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#userInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public String userInfo(String handle, String username) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.userInfo(username);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 返回停车消费的信息，格式为停车时长/消费金额
	 * handle失效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#consumeInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public String consumeInfo(String handle, String username) throws Exception {
		if (db.islegalhandle(handle)) {
			long consumeTime=db.consumeInfo(username);//查询当前的停车时间
			long consumeMoney=consumeTime*1;//一小时一元
			String value = consumeTime+"/"+consumeMoney;//返回的格式
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 真正进行支付的方法，支付成功后要将车位的状态还原，同时升起车位锁等
	 * handle值失效则返回“你未得到授权”
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#parkingConsume(java.lang.String, java.lang.String)
	 */
	@Override
	public String parkingConsume(String handle, String username) throws Exception{
		if (db.islegalhandle(handle)) {
			String value = db.parkingConsume(username);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 进行充值，充值成功返回success，否则返回failed
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#rechargeAccount(java.lang.String, java.lang.String, int)
	 */
	@Override
	public String rechargeAccount(String handle, String username,int money) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.rechargeAccount(username,money);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#getAuthorizationByUsername(java.lang.String, java.lang.String)
	 */
	@Override
	public String getAuthorizationByUsername(String handle, String username) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.getAuthorizationByUsername(username);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#getFreeNum(java.lang.String)
	 */
	@Override
	public String getFreeNum(String handle) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.getFreeNum();
			System.out.println(value);
			System.out.println("--------------------");
			
			return value;
		} else
			return "你未得到授权";
	}

	@Override
	public String getParkedNum(String handle) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.getParkedNum();
			System.out.println(value);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}

	@Override
	public String getResvID(String handle, String username) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.getResvID(username);
			System.out.println("--------------------");
			return value;
		} else
			return "你未得到授权";
	}
	/*
	 * 单独APP测试使用
	 * (non-Javadoc)
	 * @see com.njupt.ws_cxf_spring.ws.ServiceInterface#parkingLockControlForTest(java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public String parkingLockControlForTest(String handle, int userid,int lockid, String control) throws Exception {
		if (db.islegalhandle(handle)) {
			String value = db.parkingLockControlForTest(userid,lockid,control);
			System.out.println("---------------------------------");
			return value;
		} else
			return "你未得到授权";
	}

	

}
