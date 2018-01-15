package com.njupt.ws_cxf_spring.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface ServiceInterface {

	/**
	 * 验证用户是否合法，合法则给句柄
	 * @param username
	 * @param passwords
	 * @return
	 */
	@WebMethod
	String getPermission(String username,String passwords)throws Exception;
	

	/**
	 * 执行注销操作
	 * @param handle
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String logout(String handle) throws Exception;

	
	/**
	 * 车位用户注册
	 * @param username
	 * @param password
	 * @param telnum
	 * @return
	 * @throws Exception 
	 */
	@WebMethod
	String userRegister(String handle,String username,String password,String telnum);
	
	/**
	 * 车位用户登录
	 * @param username
	 * @param password
	 * @return
	 */
	@WebMethod
	String userLogin( String handle,String username,String password);
	
	/**
	 * 普通车位信息查询
	 * @param handle
	 * @return
	 */
	@WebMethod
	String parkingStatesQuery(String handle) throws Exception;
	
	/**
	 * 预约车位信息查询
	 * @param handle
	 * @return
	 */
	@WebMethod
	String parkingReservationQuery(String handle) throws Exception;

	/**
	 * 车位预约
	 * @param handle
	 * @param username
	 * @param lockid
	 * @return
	 */
	@WebMethod
	String parkingReservation(String handle,String username,int lockid) throws Exception;
	
	/**
	 * 取消预约
	 * @param handle
	 * @param username
	 * @param lockid
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String parkingReservationCancel(String handle,String username, int lockid)throws Exception;
	
	/**
	 * 车牌登记与修改
	 * @param handle
	 * @param username
	 * @param carnum
	 * @return
	 */
	@WebMethod
	String carNumReg(String handle ,String username,String carnum) throws Exception;

	
	/**
	 * 控制车位锁
	 * @param handle
	 * @param username
	 * @param control
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String parkingLockControl(String handle , String username,String control) throws Exception;
	
	/**
	 * 用户信息
	 * @param handle
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String userInfo(String handle , String username) throws Exception;
	
	/**
	 * 当前停车时间信息
	 * @param handle
	 * @param username
	 * @return
	 */
	@WebMethod
	String consumeInfo(String handle , String username) throws Exception;
	
	/**
	 * 支付停车费用
	 * @param handle
	 * @param username
	 * @return
	 */
	@WebMethod
	String parkingConsume(String handle,String username) throws Exception;
	
	/**
	 * 充值
	 * @param handle
	 * @param username
	 * @param money
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String rechargeAccount(String handle,String username,int money) throws Exception;

	/**
	 * 根据username获取用户权限
	 * @param handle
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String getAuthorizationByUsername(String handle,String username)throws Exception;
	
	/**
	 * 获取普通车位中的空闲车位号
	 * @param handle
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String getFreeNum(String handle)throws Exception;
	
	/**
	 * 获取预约车位中被占的车位号
	 * @param handle
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String getParkedNum(String handle)throws Exception;
	
	/**
	 * 根据用户名获取其预约的车位号
	 * @param handle
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String getResvID(String handle,String username)throws Exception;
	
	/**
	 * 控制车位锁,为了演示
	 * @param handle
	 * @param username
	 * @param control
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	String parkingLockControlForTest(String handle , int userid,int lockid,String control) throws Exception;
	
	
}
