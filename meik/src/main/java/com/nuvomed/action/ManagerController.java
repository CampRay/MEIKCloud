package com.nuvomed.action;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nuvomed.commons.MyException;
import com.nuvomed.commons.SecurityTools;
import com.nuvomed.dto.TadminInfo;
import com.nuvomed.dto.TadminUser;
import com.nuvomed.model.DataTableParamter;
import com.nuvomed.model.PagingData;
import com.nuvomed.service.AdminInfoService;
import com.nuvomed.service.AdminNodesService;
import com.nuvomed.service.AdminRoleService;
import com.nuvomed.service.AdminUserService;

@Controller
@RequestMapping(value="manager")
public class ManagerController extends BaseController {
	private Logger logger = Logger.getLogger(ManagerController.class);	
	
	@Resource
	private AdminUserService adminUserService;
	
	@Resource
	private AdminInfoService adminInfoService;
	
	@Resource
	private AdminNodesService adminNodesService;
	
	@Resource
	private AdminRoleService adminRoleService;
	
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView adminusers(HttpServletRequest request){
		ModelAndView mav=new ModelAndView();		
		TadminUser tUser=getSessionUser(request);
		if(tUser!=null){
			mav.addObject("rolesList", adminRoleService.getAllAdminRoles());
			mav.setViewName("manager/Adminusers");
		}
		else{
			tUser=new TadminUser();		
			mav.addObject("user", tUser);
			mav.setViewName("login");
		}
		return mav;
	}
	
	@RequestMapping(value="managersList",method=RequestMethod.GET)
	@ResponseBody
	public String AdminusersList(HttpServletRequest request,DataTableParamter dtp){		
		PagingData pagingData=adminUserService.loadAdminUserList(dtp);
		
		pagingData.setSEcho(dtp.sEcho);
		if(pagingData.getAaData()==null){
			Object[] objs=new Object[]{};
			pagingData.setAaData(objs);
		}
		
		String rightsListJson= JSON.toJSONString(pagingData);
		return rightsListJson;
			
	}
		
	
	/**
	 * <p>Description: 处理新增数据的ajax请求</p>
	 * @Title: addRights 
	 * @param jsonStr
	 * @param request
	 * @return String
	 * @throws
	 */
	@RequestMapping(value="addUsers",method=RequestMethod.POST)
	@ResponseBody
	public String addAdmins(HttpServletRequest request,TadminUser adminuser){
		TadminUser ad=getSessionUser(request);
		JSONObject respJson = new JSONObject();
		try{
			TadminUser user=adminUserService.getAdminUserByIdOrEmail(adminuser.getAdminId(),adminuser.getEmail());
			if(user==null){
				adminuser.setCreatedBy(ad.getAdminId());
				adminuser.setStatus(true);
				adminuser.setPassword(SecurityTools.SHA1(adminuser.getPassword()));
				adminuser.setCreatedTime(System.currentTimeMillis());					
				adminUserService.createAdminUser(adminuser);
				TadminInfo adminInfo=new TadminInfo();
				adminInfo.setAdminId(adminuser.getAdminId());
				adminInfo.setNotify(true);
				adminuser.setAdminInfo(adminInfo);
				adminInfoService.createAdminInfo(adminInfo);
				respJson.put("status", true);
			}			
			else{
				respJson.put("status", false);
				respJson.put("info", getMessage(request,"system.management.user.existing"));
			}						
		}
		catch(MyException be){
			respJson.put("status", false);
			respJson.put("info", getMessage(request,be.getErrorID(),be.getMessage()));
		}		
		return JSON.toJSONString(respJson);
	}
	
	@RequestMapping(value="editUsers",method=RequestMethod.POST)
	@ResponseBody
	public String updateAdmin(HttpServletRequest request,TadminUser adminuser){		

		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		TadminUser ad=getSessionUser(request);
		JSONObject respJson = new JSONObject();
		Date sdate = null;
		TadminUser dbAdminUser=adminUserService.getAdminUserById(adminuser.getAdminId());
		if(dbAdminUser!=null){					
			try{
				try {
					String ss=adminuser.getCreatedTimeStr();
					sdate = simpleDateFormat.parse(ss);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				dbAdminUser.setCreatedTime(sdate.getTime());
				dbAdminUser.setUpdatedBy(ad.getAdminId());
				dbAdminUser.setUpdatedTime(System.currentTimeMillis());			
				if(adminuser.getPassword()!=null&&!adminuser.getPassword().isEmpty()){
					dbAdminUser.setPassword(SecurityTools.SHA1(adminuser.getPassword()));
				}
				
				dbAdminUser.setAdminRole(adminRoleService.getAdminRoleById(adminuser.getAdminRole().getRoleId()));
				adminUserService.updateAdminUser(dbAdminUser);
				respJson.put("status", true);
			}
			catch(MyException be){
				respJson.put("status", false);
				respJson.put("info", getMessage(request,be.getErrorID(),be.getMessage()));
			}	
		}
		else{
			respJson.put("status", false);
			respJson.put("info", "The user do not exist or have been deleted!");
		}
		String str=JSON.toJSONString(respJson);		
		return str;
	}

	@RequestMapping(value="managers/{ids}",method=RequestMethod.DELETE)
	@ResponseBody
	public String deleteAdmins(@PathVariable String ids,HttpServletRequest request){
		String[] idstrArr=ids.split(",");		
	//	Integer[] idArr=ConvertTools.stringArr2IntArr(idstrArr);		
		JSONObject respJson = new JSONObject();
		try{
			adminUserService.deleteAdminUserByIds(idstrArr);
			respJson.put("status", true);
		}
		catch(MyException be){
			respJson.put("status", false);
			respJson.put("info", getMessage(request,be.getErrorID(),be.getMessage()));
		}	
		return JSON.toJSONString(respJson);	
	}
	@RequestMapping(value="activateusers/{ids}",method=RequestMethod.POST)
	@ResponseBody
	public String activateRules(@PathVariable String ids,HttpServletRequest request){
		String[] idstrArr=ids.split(",");		
	
		JSONObject respJson = new JSONObject();
		try{
			adminUserService.activateUsersByIds(idstrArr);
			respJson.put("status", true);
		}
		catch(MyException be){
			respJson.put("status", false);
			respJson.put("info", getMessage(request,be.getErrorID(),be.getMessage()));
		}	
		return JSON.toJSONString(respJson);	
	}
	
	@RequestMapping(value="deactivateusers/{ids}",method=RequestMethod.POST)
	@ResponseBody
	public String deactivateRules(@PathVariable String ids,HttpServletRequest request){
		String[] idstrArr=ids.split(",");				
		JSONObject respJson = new JSONObject();
		try{
			adminUserService.deactivateUsersByIds(idstrArr);
			respJson.put("status", true);
		}
		catch(MyException be){
			respJson.put("status", false);
			respJson.put("info", getMessage(request,be.getErrorID(),be.getMessage()));
		}	
		return JSON.toJSONString(respJson);	
	}

	@RequestMapping(value="doctorList",method=RequestMethod.GET)
	@ResponseBody
	public String doctorList(HttpServletRequest request){	
		List<TadminUser> doctorList=adminUserService.loadDoctorList();
		JSONObject respJson = new JSONObject();
		respJson.put("status", true);
		respJson.put("data", doctorList);
		String doctorListJson= JSON.toJSONString(respJson);
		return doctorListJson;			
	}
}
