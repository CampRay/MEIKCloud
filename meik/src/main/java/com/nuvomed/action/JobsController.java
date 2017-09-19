package com.nuvomed.action;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nuvomed.commons.ConvertTools;
import com.nuvomed.commons.EMailTool;
import com.nuvomed.commons.MyException;
import com.nuvomed.dto.TadminJob;
import com.nuvomed.dto.TadminUser;
import com.nuvomed.dto.TemaiMessage;
import com.nuvomed.dto.Tlanguage;
import com.nuvomed.dto.Tlicense;
import com.nuvomed.dto.Tuser;
import com.nuvomed.dto.TuserData;
import com.nuvomed.model.DataTableParamter;
import com.nuvomed.model.PagingData;
import com.nuvomed.service.AdminJobService;
import com.nuvomed.service.AdminUserService;
import com.nuvomed.service.LanguageService;
import com.nuvomed.service.LicenseService;
import com.nuvomed.service.UserDataService;


@Controller
@RequestMapping("/jobs")
public class JobsController extends BaseController{
	private Logger logger = Logger.getLogger(JobsController.class);
	@Autowired
	private LanguageService languageService;
	@Autowired
	private AdminJobService adminJobService;
	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private UserDataService userDataService;
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView jobs(HttpServletRequest request){
		ModelAndView mav=new ModelAndView();	
		TadminUser tUser=getSessionUser(request);
		if(tUser!=null){
			mav.addObject("doctorList", adminUserService.loadDoctorList());
			mav.setViewName("jobs/jobs");
		}
		else{
			tUser=new TadminUser();		
			mav.addObject("user", tUser);
			mav.setViewName("login");
		}
		return mav;
	}
	
	@RequestMapping(value="list",method=RequestMethod.GET)
	@ResponseBody
	public String jobList(HttpServletRequest request,DataTableParamter dtp){		
		TadminUser tUser=getSessionUser(request);		
		if(tUser!=null){
			PagingData pagingData=null;
			if(tUser.getAdminRole().getRoleId()==1){
				pagingData=adminJobService.loadAdminJobList(dtp,null);
			}
			else{
				pagingData=adminJobService.loadAdminJobList(dtp,tUser.getAdminId());
			}
			
			pagingData.setSEcho(dtp.sEcho);
			if(pagingData.getAaData()==null){
				Object[] objs=new Object[]{};
				pagingData.setAaData(objs);
			}
			String rightsListJson= JSON.toJSONString(pagingData);			
			return rightsListJson;	
		}
		return "";
	}
		
	
	/**
	 * 指定编辑报表的医生
	 * @param request
	 * @param jsonStr
	 * @return
	 */
	@RequestMapping(value="/assign",method=RequestMethod.POST)
	@ResponseBody
	public String assignDoctor(HttpServletRequest request,String ids,String doctor){		
		JSONObject respJson = new JSONObject();	
		if (ids == null || ids.isEmpty()||doctor==null||doctor.isEmpty()) {
			respJson.put("status", 0);
			respJson.put("info", "The pamameter is required.");
			return JSON.toJSONString(respJson);
		}
																
		try{			
			String[] idstrArr=ids.split(",");	
			TadminUser tDoctor=adminUserService.getAdminUserById(doctor);
			for (String id : idstrArr) {
				TadminJob job=adminJobService.getAdminJobById(Integer.parseInt(id));
				job.setType(2);				
				job.setAdminUser(tDoctor);
				job.setAssignTime(System.currentTimeMillis());
				adminJobService.updateAdminJob(job);
				//发送通知电邮		
				if(tDoctor.getAdminInfo()==null||tDoctor.getAdminInfo().getNotify()){
					TemaiMessage message=new TemaiMessage();
					message.setTo(tDoctor.getEmail());
					message.setText("The MEIK screen data of client "+job.getUser().getCode()+" is uploaded, please log in to your account in the MEIK Report Application and receive the screen data for this client.");
					message.setSubject("MEIK screen data notification");
					EMailTool.send(message);
				}
			}
			respJson.put("status", true);
		}
		catch(Exception be){
			respJson.put("status", false);
			respJson.put("info", getMessage(request,"job.exception.msg",be.getMessage()));
		}	
		return JSON.toJSONString(respJson);	
	}
	
	@RequestMapping(value="delete/{ids}",method=RequestMethod.DELETE)
	@ResponseBody
	public String deleteJobs(@PathVariable String ids,HttpServletRequest request){
		String[] idstrArr=ids.split(",");		
		Integer[] idArr=ConvertTools.stringArr2IntArr(idstrArr);		
		JSONObject respJson = new JSONObject();
		try{			
			adminJobService.deleteAdminJobByIds(idArr);
			respJson.put("status", true);
		}
		catch(MyException be){
			respJson.put("status", false);
			respJson.put("info", getMessage(request,be.getErrorID(),be.getMessage()));
		}	
		return JSON.toJSONString(respJson);	
	}
	
	/**
	 * 下载医生报告
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping(value="download/{id}",method=RequestMethod.GET)	
	public String downloadReport(HttpServletResponse response, @PathVariable String id){
			
		TadminJob job=adminJobService.getAdminJobById(Integer.parseInt(id));
		TuserData userData=userDataService.loadUserPdfReport(job.getUser().getUserId());		
							
		try{
			if(userData!=null){
				if(job.getDownloadTime()==null){
					job.setDownloadTime(System.currentTimeMillis());
					adminJobService.updateAdminJob(job);
				}
				//String fileName=URLEncoder.encode(userData.getFileName(),"UTF-8");
				
				response.setHeader("Content-Disposition", "attachment; filename=\"" + userData.getFileName() + "\"");
				response.addHeader("Content-Length", ""+userData.getStream().length);
				response.setContentType("application/octet-stream;charset=UTF-8");
				OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());  
				outputStream.write(userData.getStream());  
				outputStream.flush();  	
				outputStream.close();
									
//					HttpHeaders headers=new HttpHeaders();
//					headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//					headers.setContentDispositionFormData("attachment", fileName);
//					headers.setContentLength(userData.getStream().length);
//					return new ResponseEntity<byte[]>(userData.getStream(),headers,HttpStatus.OK);
			}
			
		}catch (Exception e) {
			
        }			
		return null;
	}
	
	/**
	 * 下载自动生成的报告
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping(value="downloadScreenPdf/{id}",method=RequestMethod.GET)	
	public String downloadScreenReport(HttpServletResponse response, @PathVariable String id){
			
		TadminJob job=adminJobService.getAdminJobById(Integer.parseInt(id));
		TuserData userData=userDataService.loadScreenPdfReport(job.getUser().getUserId());		
							
		try{
			if(userData!=null){
				if(job.getDownloadTime()==null){
					job.setDownloadTime(System.currentTimeMillis());
					adminJobService.updateAdminJob(job);
				}
				//String fileName=URLEncoder.encode(userData.getFileName(),"UTF-8");
				
				response.setHeader("Content-Disposition", "attachment; filename=\"" + userData.getFileName() + "\"");
				response.addHeader("Content-Length", ""+userData.getStream().length);
				response.setContentType("application/octet-stream;charset=UTF-8");
				OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());  
				outputStream.write(userData.getStream());  
				outputStream.flush();  	
				outputStream.close();
			}
			
		}catch (Exception e) {
			
        }			
		return null;
	}
		
	
}
