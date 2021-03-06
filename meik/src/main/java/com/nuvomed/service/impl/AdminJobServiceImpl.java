/**   
 * @Title: AdminJobServiceImpl.java 
 * @Package com.uswop.service 
 *
 * @Description: AdminJob Points Management System
 * 
 * @date Sep 11, 2014 7:21:25 PM
 * @version V1.0   
 */ 
package com.nuvomed.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nuvomed.commons.EMailTool;
import com.nuvomed.commons.SecurityTools;
import com.nuvomed.commons.SystemConfig;
import com.nuvomed.dao.AdminJobDao;
import com.nuvomed.dao.AdminRoleDao;
import com.nuvomed.dao.AdminUserDao;
import com.nuvomed.dao.GroupUserDao;
import com.nuvomed.dao.UserDao;
import com.nuvomed.dao.UserDataDao;
import com.nuvomed.dao.UserInfoDao;
import com.nuvomed.dto.TadminInfo;
import com.nuvomed.dto.TadminJob;
import com.nuvomed.dto.TadminUser;
import com.nuvomed.dto.TemaiMessage;
import com.nuvomed.dto.Tuser;
import com.nuvomed.dto.TuserData;
import com.nuvomed.dto.TuserInfo;
import com.nuvomed.model.DataTableParamter;
import com.nuvomed.model.PagingData;
import com.nuvomed.service.AdminJobService;
import com.nuvomed.service.AdminUserService;
import com.nuvomed.service.UserDataService;
import com.nuvomed.service.UserService;

/** 
 * <p>Types Description</p>
 * @ClassName: AdminJobServiceImpl 
 * @author Phills Li 
 * 
 */
@Service
public class AdminJobServiceImpl implements AdminJobService {
	@Autowired
	private AdminUserDao adminUserDao;
	@Autowired
	private AdminJobDao adminJobDao;
	@Autowired
	private AdminRoleDao adminRoleDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private UserDataDao userDataDao;	
	@Autowired
	private GroupUserDao groupUserDao;
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserDataService userDataService;
	@Autowired
	private AdminUserService adminUserService;
				

	/**
	 * (non-Javadoc)
	 * <p>Title: getAdminJobById</p> 
	 * <p>Description: </p> 
	 * @param AdminJobId
	 * @return 
	 * @see com.bps.service.AdminJobService#getAdminJobById(java.lang.String) 
	 */
	public TadminJob getAdminJobById(int adminJobId) {
		return adminJobDao.get(adminJobId);	
	}
	
	/**
	 * (non-Javadoc)
	 * <p>Title: getAdminJobByCode</p> 
	 * <p>Description: </p> 
	 * @param AdminJobId
	 * @return 
	 * @see com.bps.service.AdminJobService#getAdminJobById(java.lang.String) 
	 */
	public TadminJob getAdminJobByCode(String code) {
		Criteria criteria=adminJobDao.createCriteria();
		if(code==null||code.isEmpty()){
			return null;
		}
		else{
			List<Tuser> users=userDao.findBy("code", code);
			List<Integer> ids=new ArrayList<Integer>();
			for (Tuser user : users) {
				ids.add(user.getUserId());
			}
			if(ids.size()>0){
				criteria=criteria.add(Restrictions.in("user.userId", ids));
				Object obj=criteria.uniqueResult();
				if(obj!=null){
					return (TadminJob)obj;
				}
			}
			return null;
		}					
		
	}

	/**
	 * (non-Javadoc)
	 * <p>Title: createAdminJob</p> 
	 * <p>Description: </p> 
	 * @param AdminJob 
	 * @see com.bps.service.AdminJobService#createAdminJob(com.bps.dto.TadminJob) 
	 */
	public void createAdminJob(TadminJob adminJob) {		
		adminJobDao.create(adminJob);		
	}

	/**
	 * (non-Javadoc)
	 * <p>Title: updateAdminJob</p> 
	 * <p>Description: </p> 
	 * @param AdminJob 
	 * @see com.bps.service.AdminJobService#updateAdminJob(com.bps.dto.TadminJob) 
	 */
	public void updateAdminJob(TadminJob adminJob) {
	
		adminJobDao.update(adminJob);

	}

	/**
	 * (non-Javadoc)
	 * <p>Title: deleteAdminJob</p> 
	 * <p>Description: </p> 
	 * @param AdminJob 
	 * @see com.bps.service.AdminJobService#deleteAdminJob(com.bps.dto.TadminJob) 
	 */
	public void deleteAdminJob(TadminJob adminJob) {
		if(adminJob!=null){
			int userId=adminJob.getUserId();
			adminJobDao.delete(adminJob);			
			userDao.delete(userId);		
			userDataDao.deleteAll(userDataDao.findBy("userId", userId));		
			userInfoDao.deleteAll(userInfoDao.findBy("userId", userId));
		}		
	}

	public void deleteAdminJobById(Integer id) {
		TadminJob job=adminJobDao.get(id);
		if(job!=null){
			int userId=job.getUserId();
			adminJobDao.delete(job);
			
			userDao.delete(userId);
			//List<TuserData> userDataList=userDataDao.findBy("userId", userId);
			userDataDao.deleteAll(userDataDao.findBy("userId", userId));		
			userInfoDao.deleteAll(userInfoDao.findBy("userId", userId));
		}
	}

	public void deleteAdminJobByIds(Integer[] ids) {
		List<TadminJob> jobList=new ArrayList<TadminJob>();	
		List<Integer> userIdList=new ArrayList<Integer>();
		for (Integer id : ids) {
			TadminJob job=adminJobDao.get(id);
			jobList.add(job);			
			userIdList.add(job.getUser().getUserId());
		}
		adminJobDao.deleteAll(jobList);		
		for (int userId : userIdList) {			
			userInfoDao.deleteAll(userInfoDao.findBy("user.userId",userId ));
			userDataDao.deleteAll(userDataDao.findBy("userId", userId));
			userDao.delete(userId);
		}
		
	}
	
	/**
	 * 查询所有分配给医生的任务记录
	 * @param doctorId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TadminJob> loadDataAssignedJobList(String doctorId) {
		Criteria criteria=adminJobDao.createCriteria();
		if(doctorId==null){
			return null;
		}
		else{
			criteria=criteria.add(Restrictions.eq("adminUser.adminId", doctorId));
		}
		criteria=criteria.add(Restrictions.eq("status", false))
		.add(Restrictions.eq("type", 2));				
		return criteria.list();
				
	}
		
	/**
	 * 查询所有分配给系统医生的报表任务记录
	 * @param doctorId 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TadminJob> loadReportAssignedJobList() {
		Criteria criteria=adminJobDao.createCriteria();		
		criteria=criteria.add(Restrictions.eq("status", false))
		.add(Restrictions.eq("type", 5));				
		return criteria.list();
				
	}	
	
	/**
	 * 查詢所有分配給指定人員的报表任务记录
	 */
	@SuppressWarnings("unchecked")
	public List<TadminJob> loadAdminJobList(String adminId,int type) {
		Criteria criteria=adminJobDao.createCriteria();
		if(adminId!=null){
			criteria=criteria.add(Restrictions.eq("adminUser.adminId", adminId));
		}		
		criteria=criteria.add(Restrictions.eq("status", false))
		.add(Restrictions.eq("type", type));		
//		if(type!=3){
//			if(adminId==null){
//				criteria=criteria.add(Restrictions.isNull("adminUser"));
//			}
//			else{
//				criteria=criteria.add(Restrictions.eq("adminUser.adminId", adminId));
//			}
//			criteria=criteria.add(Restrictions.eq("status", false))
//			.add(Restrictions.eq("type", type));
//		}
//		else{
//			criteria=criteria.add(Restrictions.eq("createdBy", adminId))
//					.add(Restrictions.eq("status", false))
//					.add(Restrictions.eq("type", type));
//		}
		return criteria.list();
				
	}		
	
	/**
	 * 加載所有醫生下載數據或報表的任務
	 * @param adminId
	 * @param code
	 * @return
	 */
	public TadminJob loadDownloadedJobByUserCode(String adminId,String code) {				
		List<Tuser> users=userDao.findBy("code", code);
		List<Integer> ids=new ArrayList<Integer>();
		for (Tuser user : users) {
			ids.add(user.getUserId());
		}
		if(ids.size()>0){
			Criteria criteria=adminJobDao.createCriteria();
			if(adminId!=null){
				criteria=criteria.add(Restrictions.eq("adminUser.adminId", adminId))			
				.add(Restrictions.eq("status", false))
				.add(Restrictions.or(Restrictions.eq("type", 3),Restrictions.eq("type", 6)));						
			}
			else{
				criteria=criteria.add(Restrictions.eq("status", false))
				.add(Restrictions.or(Restrictions.eq("type", 3),Restrictions.eq("type", 6)));
			}
			criteria=criteria.add(Restrictions.in("user.userId", ids));
			return (TadminJob)criteria.uniqueResult();
		}
		else{
			return null;
		}
		
				
	}
	
	/**
	 * 加載由指定的医生报告某个指定检测用户的已完成的任務
	 * @param adminId
	 * @param code
	 * @return
	 */
	public TadminJob loadDoneJobByUserCode(String adminId,String code) {
		
		List<Tuser> users=userDao.findBy("code", code);
		List<Integer> ids=new ArrayList<Integer>();
		for (Tuser user : users) {
			ids.add(user.getUserId());
		}
		if(ids.size()>0){
			Criteria criteria=adminJobDao.createCriteria();
			if(adminId!=null){
				criteria=criteria.add(Restrictions.eq("adminUser.adminId", adminId))
				.add(Restrictions.eq("status", true))
				.add(Restrictions.eq("type", 7));								
			}		
			
			criteria=criteria.add(Restrictions.in("user.userId", ids));
			return (TadminJob)criteria.uniqueResult();
		}
		else{
			return null;
		}		
				
	}
	
	/**
	 * 加載由指定的医生报告某个指定检测用户的已完成的任務
	 * @param adminId
	 * @param code
	 * @return
	 */
	public TadminJob loadDoneJobByUserCode(String code) {				
		List<Tuser> users=userDao.findBy("code", code);
		List<Integer> ids=new ArrayList<Integer>();
		for (Tuser user : users) {
			ids.add(user.getUserId());
		}
		if(ids.size()>0){
			Criteria criteria=adminJobDao.createCriteria()
					.add(Restrictions.eq("status", true))
					.add(Restrictions.eq("type", 7));
				
			criteria=criteria.add(Restrictions.in("user.userId", ids));
			return (TadminJob)criteria.uniqueResult();
		}
		else{
			return null;
		}						
	}
	
	
	/**
	 * 根据创建者查询工作任務數列表
	 * @param rdtp
	 * @param adminId
	 */	
	public PagingData loadAdminJobList(DataTableParamter rdtp,TadminUser adminUser) {
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("MM/dd/yyyy");
		String searchJsonStr=rdtp.getsSearch();
		List<Criterion> criterionsList=new ArrayList<Criterion>();
		if(adminUser!=null){
			if(adminUser.getAdminRole().getRoleId()!=1&&adminUser.getAdminRole().getRoleId()!=4){
				criterionsList.add(Restrictions.eq("createdBy", adminUser.getAdminId()));
			}
			else if(adminUser.getAdminRole().getRoleId()==4){//当前用户是系统医生时，可以显示所有同一组用户上传的数据				
				List<String> adminIdList=groupUserDao.getAdminIdsByUser(adminUser);
				if(adminIdList.size()>0){
					criterionsList.add(Restrictions.in("createdBy", adminIdList));
				}
			}			
		}
		if(searchJsonStr!=null&&!searchJsonStr.isEmpty()){			
			JSONObject jsonObj= (JSONObject)JSON.parse(searchJsonStr);						
			
			Set<String> keys=jsonObj.keySet();						
			for (String key : keys) {
				String val=jsonObj.getString(key);
				if(val!=null&&!val.isEmpty()){
					if(key=="status"){
						criterionsList.add(Restrictions.eq(key, jsonObj.getBoolean(key)));
					}
					else if(key=="type"){
						criterionsList.add(Restrictions.eq(key, jsonObj.getInteger(key)));
					}
					else if(key=="code"){
						List<Tuser> users=userDao.findBy("code", val);
						List<Integer> ids=new ArrayList<Integer>();
						for (Tuser user : users) {
							ids.add(user.getUserId());
						}
						if(ids.size()>0){
							criterionsList.add(Restrictions.in("user.userId", ids));
						}
						else{
							criterionsList.add(Restrictions.eq("user.userId", 0));
						}
					}
					else if(key=="startTime"){
						Date sdate = null;
						try {
							sdate = simpleDateFormat.parse(val);							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Long startLong=sdate.getTime();						
						criterionsList.add(Restrictions.ge("createdTime", startLong));
					}
					else if(key=="endTime"){
						Date edate = null;
						try {
							edate = simpleDateFormat.parse(val);							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Long endLong=edate.getTime();						
						criterionsList.add(Restrictions.le("createdTime", endLong));
					}
					else{
						criterionsList.add(Restrictions.eq(key, jsonObj.get(key)));
					}
				}
			}						
		}
		
		if(criterionsList.size()>0){
			Criterion[] criterions=new Criterion[criterionsList.size()];
			int i=0;
			for (Criterion criterion : criterionsList) {
				criterions[i]=criterion;	
				i++;
			}
			return adminJobDao.findPage("createdTime",false,criterions,rdtp.iDisplayStart, rdtp.iDisplayLength);
		}
		return adminJobDao.findPage("createdTime",false,rdtp.iDisplayStart, rdtp.iDisplayLength);
	}
	
	/**
	 * 醫生數據統計列表查詢
	 */
	public PagingData loadDoctorJobList(DataTableParamter rdtp,TadminUser adminUser) {				
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("MM/dd/yyyy");
		String searchJsonStr=rdtp.getsSearch();
		List<Criterion> criterionsList=new ArrayList<Criterion>();
		if(adminUser!=null){
			if(adminUser.getAdminRole().getRoleId()!=1&&adminUser.getAdminRole().getRoleId()!=4){
				criterionsList.add(Restrictions.eq("adminUser.adminId", adminUser.getAdminId()));
			}
			else if(adminUser.getAdminRole().getRoleId()==4){//当前用户是系统医生时，可以显示所有同一组用户上传的数据				
				List<String> adminIdList=groupUserDao.getAdminIdsByUser(adminUser);
				if(adminIdList.size()>0){
					criterionsList.add(Restrictions.in("createdBy", adminIdList));
				}
			}
			
		}
		if(searchJsonStr!=null&&!searchJsonStr.isEmpty()){			
			JSONObject jsonObj= (JSONObject)JSON.parse(searchJsonStr);						
			
			Set<String> keys=jsonObj.keySet();						
			for (String key : keys) {
				String val=jsonObj.getString(key);
				if(val!=null&&!val.isEmpty()){
					if(key=="status"){
						criterionsList.add(Restrictions.eq(key, jsonObj.getBoolean(key)));
					}
					else if(key=="type"){
						criterionsList.add(Restrictions.eq(key, jsonObj.getInteger(key)));
					}
					else if(key=="code"){
						List<Tuser> users=userDao.findBy("code", val);
						List<Integer> ids=new ArrayList<Integer>();
						for (Tuser user : users) {
							ids.add(user.getUserId());
						}
						if(ids.size()>0){
							criterionsList.add(Restrictions.in("user.userId", ids));
						}
						else{
							criterionsList.add(Restrictions.eq("user.userId", 0));
						}
					}
					else if(key=="startTime"){
						Date sdate = null;
						try {
							sdate = simpleDateFormat.parse(val);							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Long startLong=sdate.getTime();						
						criterionsList.add(Restrictions.ge("doneTime", startLong));
					}
					else if(key=="endTime"){
						Date edate = null;
						try {
							edate = simpleDateFormat.parse(val);							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Long endLong=edate.getTime();						
						criterionsList.add(Restrictions.le("doneTime", endLong));
					}
					else{
						criterionsList.add(Restrictions.eq(key, jsonObj.get(key)));
					}
				}
			}						
		}

		if(criterionsList.size()>0){
			Criterion[] criterions=new Criterion[criterionsList.size()];
			int i=0;
			for (Criterion criterion : criterionsList) {
				criterions[i]=criterion;	
				i++;
			}
			return adminJobDao.findPage(criterions,rdtp.iDisplayStart, rdtp.iDisplayLength);
		}
		return adminJobDao.findPage(rdtp.iDisplayStart, rdtp.iDisplayLength);
	}
	
	/**
	 * 醫生數據統計列表查詢
	 */
	@SuppressWarnings("unchecked")
	public List<TadminJob> loadDoctorJobList(TadminJob searchObj,TadminUser adminUser) {
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("MM/dd/yyyy");		
		List<Criterion> criterionsList=new ArrayList<Criterion>();	
		if(adminUser!=null){
			if(adminUser.getAdminRole().getRoleId()!=1&&adminUser.getAdminRole().getRoleId()!=4){
				criterionsList.add(Restrictions.eq("adminUser.adminId", adminUser.getAdminId()));
			}
			else if(adminUser.getAdminRole().getRoleId()==4){//当前用户是系统医生时，可以显示所有同一组用户上传的数据				
				List<String> adminIdList=groupUserDao.getAdminIdsByUser(adminUser);
				if(adminIdList.size()>0){
					criterionsList.add(Restrictions.in("createdBy", adminIdList));
				}
			}
			
		}
		if(searchObj!=null){
			if(searchObj.getCreatedBy()!=null&&!searchObj.getCreatedBy().isEmpty()){
				criterionsList.add(Restrictions.eq("createdBy", searchObj.getCreatedBy()));
			}
			if(searchObj.getDoctor()!=null&&!searchObj.getDoctor().isEmpty()){
				criterionsList.add(Restrictions.eq("adminUser.adminId", searchObj.getDoctor()));
			}
			if(searchObj.getType()>0){
				criterionsList.add(Restrictions.eq("type", searchObj.getType()));
			}			
			if(searchObj.getCode()!=null&&!searchObj.getCode().isEmpty()){
				List<Tuser> users=userDao.findBy("code", searchObj.getCode());
				List<Integer> ids=new ArrayList<Integer>();
				for (Tuser user : users) {
					ids.add(user.getUserId());
				}
				if(ids.size()>0){
					criterionsList.add(Restrictions.in("user.userId", ids));
				}
				else{
					criterionsList.add(Restrictions.eq("user.userId", 0));
				}
			}
			if(searchObj.getStartTime()!=null&&!searchObj.getStartTime().isEmpty()){
				Date sdate = null;
				try {
					sdate = simpleDateFormat.parse(searchObj.getStartTime());	
					Long startLong=sdate.getTime();						
					criterionsList.add(Restrictions.ge("doneTime", startLong));
				} catch (ParseException e) {
					
				}
				
			}
			if(searchObj.getEndTime()!=null&&!searchObj.getEndTime().isEmpty()){
				Date edate = null;
				try {
					edate = simpleDateFormat.parse(searchObj.getEndTime());		
					Long endLong=edate.getTime();						
					criterionsList.add(Restrictions.le("doneTime", endLong));
				} catch (ParseException e) {					
				}
				
			}							
		}

		if(criterionsList.size()>0){
			Criterion[] criterions=new Criterion[criterionsList.size()];
			int i=0;
			for (Criterion criterion : criterionsList) {
				criterions[i]=criterion;	
				i++;
			}
			return adminJobDao.createCriteria(criterions).list();
		}
		return adminJobDao.createCriteria().list();
	}
		
	
	
	/**
	 * 检测员數據統計列表查詢
	 */
	@SuppressWarnings("unchecked")
	public List<TadminJob> loadOperatorJobList(TadminJob searchObj,TadminUser adminUser) {
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("MM/dd/yyyy");		
		List<Criterion> criterionsList=new ArrayList<Criterion>();	
		if(adminUser!=null){
			if(adminUser.getAdminRole().getRoleId()!=1&&adminUser.getAdminRole().getRoleId()!=4){
				criterionsList.add(Restrictions.eq("createdBy", adminUser.getAdminId()));
			}
			else if(adminUser.getAdminRole().getRoleId()==4){//当前用户是系统医生时，可以显示所有同一组用户上传的数据				
				List<String> adminIdList=groupUserDao.getAdminIdsByUser(adminUser);
				if(adminIdList.size()>0){
					criterionsList.add(Restrictions.in("createdBy", adminIdList));
				}
			}
			
		}
		if(searchObj!=null){			
			if(searchObj.getCreatedBy()!=null&&!searchObj.getCreatedBy().isEmpty()){
				criterionsList.add(Restrictions.eq("createdBy", searchObj.getCreatedBy()));
			}
			
			if(searchObj.getDoctor()!=null&&!searchObj.getDoctor().isEmpty()){
				criterionsList.add(Restrictions.eq("adminUser.adminId", searchObj.getDoctor()));
			}			
			if(searchObj.getType()>0){
				criterionsList.add(Restrictions.eq("type", searchObj.getType()));
			}			
			if(searchObj.getCode()!=null&&!searchObj.getCode().isEmpty()){
				List<Tuser> users=userDao.findBy("code", searchObj.getCode());
				List<Integer> ids=new ArrayList<Integer>();
				for (Tuser user : users) {
					ids.add(user.getUserId());
				}
				if(ids.size()>0){
					criterionsList.add(Restrictions.in("user.userId", ids));
				}
				else{
					criterionsList.add(Restrictions.eq("user.userId", 0));
				}
			}
			if(searchObj.getStartTime()!=null&&!searchObj.getStartTime().isEmpty()){
				Date sdate = null;
				try {
					sdate = simpleDateFormat.parse(searchObj.getStartTime());	
					Long startLong=sdate.getTime();						
					criterionsList.add(Restrictions.ge("createdTime", startLong));
				} catch (ParseException e) {
					
				}
				
			}
			if(searchObj.getEndTime()!=null&&!searchObj.getEndTime().isEmpty()){
				Date edate = null;
				try {
					edate = simpleDateFormat.parse(searchObj.getEndTime());		
					Long endLong=edate.getTime();						
					criterionsList.add(Restrictions.le("createdTime", endLong));
				} catch (ParseException e) {					
				}
				
			}							
		}

		if(criterionsList.size()>0){
			Criterion[] criterions=new Criterion[criterionsList.size()];
			int i=0;
			for (Criterion criterion : criterionsList) {
				criterions[i]=criterion;	
				i++;
			}
			return adminJobDao.createCriteria(criterions).list();
		}
		return adminJobDao.createCriteria().list();
	}
	
	
	/**
	 * 根据条件搜索指定客户的任选列表
	 */
	public PagingData loadClientJobList(DataTableParamter rdtp,TadminUser adminUser) {
		String searchJsonStr=rdtp.getsSearch();
		List<Criterion> criterionsList=new ArrayList<Criterion>();		
		String firstName=null;
		String lastName=null;
		String otherName=null;
		String mobile=null;
		String email=null;
		String code=null;
		String birthday=null;
		String infoId=null;
		String cid=null;
		
		if(searchJsonStr!=null&&!searchJsonStr.isEmpty()){			
			JSONObject jsonObj= (JSONObject)JSON.parse(searchJsonStr);			
			Set<String> keys=jsonObj.keySet();
						
			for (String key : keys) {
				String val=jsonObj.getString(key);
				if(val!=null&&!val.isEmpty()){					
					if(key=="birthday"){
						birthday=jsonObj.getString(key);
					}
					else if(key=="firstName"){
						firstName=jsonObj.getString(key);
					}
					else if(key=="lastName"){
						lastName=jsonObj.getString(key);
					}
					else if(key=="otherName"){
						otherName=jsonObj.getString(key);
					}
					else if(key=="mobile"){
						mobile=jsonObj.getString(key);
					}
					else if(key=="email"){
						email=jsonObj.getString(key);
					}
					else if(key=="code"){
						code=jsonObj.getString(key);
					}
					else if(key=="infoId"){
						infoId=jsonObj.getString(key);
					}
					else if(key=="cid"){
						cid=jsonObj.getString(key);
					}
					else{
						criterionsList.add(Restrictions.eq(key, jsonObj.get(key)));
					}
				}
			}									
		}
		
		List<Tuser> userList=userService.loadAllUserByInfo(infoId, cid, code, firstName, lastName, otherName, birthday, email, mobile,adminUser);		
		if(userList!=null&&userList.size()>0){
			criterionsList.add(Restrictions.in("user", userList));
		}

						
		if(criterionsList.size()>0){
			Criterion[] criterions=new Criterion[criterionsList.size()];
			int i=0;
			for (Criterion criterion : criterionsList) {
				criterions[i]=criterion;	
				i++;
			}
			return adminJobDao.findPage("createdTime",false,criterions,rdtp.iDisplayStart, rdtp.iDisplayLength);
		}
		else{
			return new PagingData();
		}
		

	}
	
	  
   
   /**
    * 上传扫描数据时处理的业务
    */
   public void createScreenData(TuserInfo userInfo,TuserData userData,String createdBy){
	   Tuser user=userDao.findUnique("code", userInfo.getCode());
	   if(user==null){
		   user=new Tuser();
	   }
	   user.setCid(userInfo.getCid());
	   user.setCode(userInfo.getCode());	
	   user.setIdNumber(userInfo.getIdNumber());
	   user.setFirstName(userInfo.getFirstName());
	   user.setLastName(userInfo.getLastName());		
	   user.setOtherName(userInfo.getOtherName());
	   user.setBirthday(userInfo.getBirthday());
	   user.setMobile(userInfo.getMobile());	
	   user.setEmail(userInfo.getEmail());					
	   user.setLocation(userInfo.getVenue());		
	   user.setResult(userInfo.getResult());
	   user.setGender(true);
	   user.setCreatedBy(createdBy);	
	   userDao.saveOrUpdate(user);
   
	   userData.setUserId(user.getUserId());
	   userDataDao.create(userData);	  
	   //保存用户信息表
	   TuserInfo userInfoDb=userInfoDao.findUnique("user", user);	
	   if(userInfoDb!=null){	
		  userInfo.setInfoId(userInfoDb.getInfoId());					
	   }
	   else{
		   userInfo.setUser(user);			
	   }			
	   userInfoDao.saveOrUpdate(userInfo);	 
	   
	 //保存工作任务
	TadminJob adminJob=new TadminJob();			
	adminJob.setType(2);
	adminJob.setStatus(false);
	adminJob.setUser(user);
	adminJob.setCreatedBy(createdBy);
	adminJob.setCreatedTime(System.currentTimeMillis());
	//查询操作员所在组的系统医生（报表管理员）帐号		
	List<TadminUser> systemDocotrList=groupUserDao.getManagerIdsByUser(createdBy);
	TadminUser systemDoctor=null;
	if(systemDocotrList.size()>0){
		systemDoctor=systemDocotrList.get(0);
		adminJob.setAdminUser(systemDoctor);
		adminJob.setAssignTime(System.currentTimeMillis());
	}
	else{//如果用户组中没有管理員帐号，则指定Screen数据给系统配置的系统医生
		String doctorId=SystemConfig.Admin_Setting_Map.get("System_Doctor_Id");
		systemDoctor=adminUserDao.get(doctorId);
		if(systemDoctor!=null){
			adminJob.setAdminUser(systemDoctor);
			adminJob.setAssignTime(System.currentTimeMillis());
		}
	}
	adminJobDao.create(adminJob);
		
	//如果系统医生帐号設置了接收通知电邮，则发送通知电邮		
	if(systemDoctor.getAdminInfo()==null||systemDoctor.getAdminInfo().getNotify()){
		try{
			//发送通知电邮					
			TemaiMessage message=new TemaiMessage();
			message.setTo(systemDoctor.getEmail());
			message.setText("The MEIK screen data of client "+adminJob.getUser().getCode()+" is uploaded, please log in to your account in the MEIK Report Tool Application and receive the screen data for this client.");
			message.setSubject("MEIK screen data notification");
			EMailTool.send(message);
		}
		catch(Exception e){}
	}
	   	   	 			 		 		 		
	   	   
	   //如果設置了需要自動創建帳號
	   if(SystemConfig.Admin_Setting_Map.get("Create_Account").equalsIgnoreCase("true")){
		   TadminUser userAccount=null;
		   if(!StringUtils.isEmpty(user.getCid())&&!StringUtils.isEmpty(user.getEmail())){
			   userAccount=adminUserService.getAdminUserByIdOrEmail(user.getCid(), user.getEmail());
		   }
		   else if(!StringUtils.isEmpty(user.getCid())){
			   userAccount=adminUserService.getAdminUserById(user.getCid());
		   }
		   else if(!StringUtils.isEmpty(user.getEmail())){
			   userAccount=adminUserService.getAdminUserByIdOrEmail(user.getEmail());
		   }
		   //如果数据库中没有对应的帐号记录，则创建新帐号
		   if(userAccount==null){
			   if(!StringUtils.isEmpty(user.getCid())||!StringUtils.isEmpty(user.getEmail())){
				   userAccount=new TadminUser();
				   if(!StringUtils.isEmpty(userInfo.getCid())){
					   userAccount.setAdminId(userInfo.getCid());
				   }
				   else if(!StringUtils.isEmpty(userInfo.getEmail())){
					   userAccount.setAdminId(user.getEmail());
				   }
				   userAccount.setEmail(user.getEmail());
				   userAccount.setPassword(SecurityTools.SHA1(user.getEmail()));
				   userAccount.setAdminRole(adminRoleDao.get(4));
				   userAccount.setCreatedTime(System.currentTimeMillis());
				   userAccount.setStatus(true);
				   TadminInfo adminInfo=new TadminInfo();
				   adminInfo.setBirthday(user.getBirthday());
				   adminInfo.setFirstName(user.getFirstName());
				   adminInfo.setLastName(user.getLastName());
				   adminInfo.setMobile(user.getMobile());
				   adminInfo.setEmail(user.getEmail());
				   userAccount.setAdminInfo(adminInfo);
				   adminUserDao.create(userAccount);
			   }
		   }		   		   
		   
		   try{
				TemaiMessage message=new TemaiMessage();
				message.setTo(user.getEmail());
				StringBuffer sb=new StringBuffer();
				sb.append("<p>"+user.getLastName()+",<br /><br />");
				sb.append("You have got a new account on the MEIKAsia website <a href='http://www.meikasia.com'>www.meikasia.com</a>.");
				sb.append("Below are your account details:<br />Login ID: "+userInfo.getCid()+" / "+user.getEmail()+"<br />Password: "+user.getEmail()+"<br /><br />");
				sb.append("You can check your screen data and report by logging into your account.<br />");
				sb.append("</p>");					
				EMailTool.send(message);
			}
			catch(Exception e){}		   		   		   
	    }	    
				
	   																					
   }
   
   
   /**
    * 上传报告数据时处理的新增业务(国内Screen和Report合并版)
    * 
    */
   public void createReportData(TuserInfo userInfo,TuserData userData,TadminUser createdBy){	   
	   Tuser user=userDao.findUnique("code", userInfo.getCode());
	   if(user==null){
		   user=new Tuser();
	   }
	   user.setCid(userInfo.getCid());
		user.setCode(userInfo.getCode());	
		user.setIdNumber(userInfo.getIdNumber());
		user.setFirstName(userInfo.getFirstName());
		user.setLastName(userInfo.getLastName());		
		user.setOtherName(userInfo.getOtherName());
		user.setBirthday(userInfo.getBirthday());
		user.setMobile(userInfo.getMobile());	
		user.setEmail(userInfo.getEmail());					
		user.setLocation(userInfo.getVenue());		
		user.setResult(userInfo.getResult());
		user.setGender(true);
		user.setCreatedBy(createdBy.getAdminId());	
		userDao.saveOrUpdate(user);
   
		 
		List<TuserData> userDataList=userDataService.loadUserReportDataList(user.getUserId());
		   if(userDataList==null||userDataList.size()==0){
			   userData.setUserId(user.getUserId());		   
		   }
		   else{
			   int dataId=userDataList.get(0).getDataId();
			   userData.setDataId(dataId);
			   userData.setUserId(user.getUserId());
		   }
		   userDataDao.saveOrUpdate(userData);
		
		TuserInfo userInfoDb=userInfoDao.findUnique("user", user);	
		if(userInfoDb!=null){	
			userInfoDb.setCode(userInfo.getCode());
			userInfoDb.setCid(userInfo.getCid());
			//身份证号
			userInfoDb.setIdNumber(userInfo.getIdNumber());
			userInfoDb.setVenue(userInfo.getVenue());
			userInfoDb.setFirstName(userInfo.getFirstName());
			userInfoDb.setLastName(userInfo.getLastName());			
			userInfoDb.setBirthday(userInfo.getBirthday());
			userInfoDb.setMobile(userInfo.getMobile());
			userInfoDb.setEmail(userInfo.getEmail());	
			//门诊号
			userInfoDb.setOutPatientNumber(userInfo.getOutPatientNumber());
			//科室
			userInfoDb.setDepartment(userInfo.getDepartment());
			//住院号
			userInfoDb.setHospitalNumber(userInfo.getHospitalNumber());
			//病床号
			userInfoDb.setHospitalBedNumber(userInfo.getHospitalBedNumber());
			userInfoDb.setCreatedTime(System.currentTimeMillis());
			userInfoDb.setResult(userInfo.getResult());		
			userInfoDao.saveOrUpdate(userInfoDb);
		}
		else{
			userInfo.setUser(user);	
			userInfoDao.saveOrUpdate(userInfo);
		}					
		   
		long currentTime=System.currentTimeMillis();
					
		TadminJob adminJob=new TadminJob();			
		adminJob.setType(7);
		adminJob.setStatus(true);
		adminJob.setUser(user);
		adminJob.setCreatedBy(createdBy.getAdminId());
		adminJob.setAdminUser(createdBy);
		adminJob.setAssignTime(currentTime);
		adminJob.setCreatedTime(currentTime);		
		//医生接受任务，开始做报表时间
		adminJob.setReportTime(currentTime);
		//医生任务完成时间
		adminJob.setDoneTime(currentTime);
		//检测员接收报告，任务关闭时间
		adminJob.setCloseTime(currentTime);
		adminJobDao.create(adminJob);																												
   }

   /**
    * 上传报告数据时处理的修改业务(国内Screen和Report合并版)
    * 
    */
   public void updateReportData(TuserInfo userInfo,TuserData userData,TadminJob adminJob){
	   Tuser user=userDao.findUnique("code", userInfo.getCode());
	   if(user==null){
		   user=new Tuser();
	   }
	   user.setCid(userInfo.getCid());
	   user.setCode(userInfo.getCode());	
		user.setIdNumber(userInfo.getIdNumber());
	   user.setFirstName(userInfo.getFirstName());
	   user.setLastName(userInfo.getLastName());		
	   user.setOtherName(userInfo.getOtherName());
	   user.setBirthday(userInfo.getBirthday());
	   user.setMobile(userInfo.getMobile());	
	   user.setEmail(userInfo.getEmail());					
	   user.setLocation(userInfo.getVenue());
	   user.setResult(userInfo.getResult());		   
	   if(user.getCreatedBy()==null){
		   user.setCreatedBy(adminJob.getCreatedBy());	
	   }
	   userDao.saveOrUpdate(user);
	   
	   List<TuserData> userDataList=userDataService.loadUserReportDataList(user.getUserId());
	   if(userDataList==null||userDataList.size()==0){
		   userData.setUserId(user.getUserId());		   
	   }
	   else{
		   int dataId=userDataList.get(0).getDataId();
		   userData.setUserId(user.getUserId());
		   userData.setDataId(dataId);
	   }
	   userDataDao.saveOrUpdate(userData);	  
	   
	   long currentTimeMillis=System.currentTimeMillis();
		adminJob.setType(7);
		adminJob.setStatus(true);
		adminJob.setAssignTime(currentTimeMillis);
		adminJob.setReportTime(currentTimeMillis);
		adminJob.setDoneTime(currentTimeMillis);
		adminJob.setCloseTime(currentTimeMillis);
		adminJobDao.update(adminJob);
		
		TuserInfo userInfoDb=userInfoDao.findUnique("user", user);	
		if(userInfoDb!=null){	
			userInfoDb.setCode(userInfo.getCode());
			userInfoDb.setCid(userInfo.getCid());
			//身份证号
			userInfoDb.setIdNumber(userInfo.getIdNumber());
			userInfoDb.setVenue(userInfo.getVenue());
			userInfoDb.setFirstName(userInfo.getFirstName());
			userInfoDb.setLastName(userInfo.getLastName());			
			userInfoDb.setBirthday(userInfo.getBirthday());
			userInfoDb.setMobile(userInfo.getMobile());
			userInfoDb.setEmail(userInfo.getEmail());	
			//门诊号
			userInfoDb.setOutPatientNumber(userInfo.getOutPatientNumber());
			//科室
			userInfoDb.setDepartment(userInfo.getDepartment());
			//住院号
			userInfoDb.setHospitalNumber(userInfo.getHospitalNumber());
			//病床号
			userInfoDb.setHospitalBedNumber(userInfo.getHospitalBedNumber());
			userInfoDb.setCreatedTime(System.currentTimeMillis());
			userInfoDb.setResult(userInfo.getResult());		
			userInfoDao.saveOrUpdate(userInfoDb);
		}
		else{
			userInfo.setUser(user);	
			userInfoDao.saveOrUpdate(userInfo);
		}			
		
		
   }
	   	
}
