package com.nuvomed.action;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.nuvomed.dto.TadminJob;
import com.nuvomed.dto.TadminUser;
import com.nuvomed.dto.TuserData;
import com.nuvomed.model.DataTableParamter;
import com.nuvomed.model.PagingData;
import com.nuvomed.service.AdminJobService;
import com.nuvomed.service.AdminUserService;
import com.nuvomed.service.GroupUserService;
import com.nuvomed.service.UserDataService;


@Controller
@RequestMapping("/report")
public class ReportController extends BaseController{	
	@Autowired
	private AdminJobService adminJobService;
	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private UserDataService userDataService;
	
	@RequestMapping(value="/doctor",method=RequestMethod.GET)
	public ModelAndView doctorReport(HttpServletRequest request){
		ModelAndView mav=new ModelAndView();	
		TadminUser tUser=getSessionUser(request);
		if(tUser!=null){
			mav.addObject("doctorList", adminUserService.loadDoctorList());
			mav.setViewName("report/doctor");
		}
		else{
			tUser=new TadminUser();		
			mav.addObject("user", tUser);
			mav.setViewName("login");
		}
		return mav;
	}
	
	@RequestMapping(value="/doctor/list",method=RequestMethod.GET)
	@ResponseBody
	public String doctorList(HttpServletRequest request,DataTableParamter dtp){		
		TadminUser tUser=getSessionUser(request);		
		if(tUser!=null){
			PagingData pagingData=null;
			pagingData=adminJobService.loadDoctorJobList(dtp,tUser);		
			if(pagingData!=null){
				pagingData.setSEcho(dtp.sEcho);
				if(pagingData.getAaData()==null){
					Object[] objs=new Object[]{};
					pagingData.setAaData(objs);
				}
			}
			String doctorListJson= JSON.toJSONString(pagingData);			
			return doctorListJson;	
		}
		return "";
	}
	
	@RequestMapping(value="/doctor/excel",method=RequestMethod.POST)	
	public String doctorExcel(HttpServletRequest request,HttpServletResponse response){		
		TadminUser tUser=getSessionUser(request);					
		if(tUser!=null){
			TadminJob adminJob=new TadminJob();
			adminJob.setCode(request.getParameter("code"));
			adminJob.setCreatedBy(request.getParameter("createdBy"));
			if(!request.getParameter("type").isEmpty()){
				adminJob.setType(Integer.parseInt(request.getParameter("type")));
			}
			adminJob.setDoctor(request.getParameter("adminUser.adminId"));
			adminJob.setStartTime(request.getParameter("startTime"));
			adminJob.setEndTime(request.getParameter("endTime"));
			List<TadminJob> jobList=new ArrayList<TadminJob>();
			jobList=adminJobService.loadDoctorJobList(adminJob,tUser);									
			//创建HSSFWorkbook对象  
			HSSFWorkbook wkb = new HSSFWorkbook();  
			//创建HSSFSheet对象  
			HSSFSheet sheet = wkb.createSheet("sheet");  
			//创建HSSFRow对象  
			HSSFRow hrow = sheet.createRow(0);  
			hrow.createCell(0).setCellValue("ID");
			hrow.createCell(1).setCellValue("Screen Code");
			hrow.createCell(2).setCellValue("Client Name");
			hrow.createCell(3).setCellValue("Operator");
			hrow.createCell(4).setCellValue("Created Time");
			hrow.createCell(5).setCellValue("Doctor");
			hrow.createCell(6).setCellValue("Assigned Time");
			hrow.createCell(7).setCellValue("Reporting Time");
			hrow.createCell(8).setCellValue("Uploaded Time");
			hrow.createCell(9).setCellValue("Done Time");
			hrow.createCell(10).setCellValue("Download Time");
			hrow.createCell(11).setCellValue("Status");
			hrow.createCell(12).setCellValue("Is Free/Demo");
			  			
			int i=1;
			for (TadminJob job : jobList) {				
				HSSFRow row = sheet.createRow(i);
				row.createCell(0).setCellValue(job.getJobId());
				row.createCell(1).setCellValue(job.getCode());
				row.createCell(2).setCellValue(job.getClientName());
				row.createCell(3).setCellValue(job.getCreatedBy());
				row.createCell(4).setCellValue(job.getCreatedTimeStr());
				row.createCell(5).setCellValue(job.getDoctor());
				row.createCell(6).setCellValue(job.getAssignTimeStr());
				row.createCell(7).setCellValue(job.getReportTimeStr());
				row.createCell(8).setCellValue(job.getDoneTimeStr());
				row.createCell(9).setCellValue(job.getCloseTimeStr());
				row.createCell(10).setCellValue(job.getDownloadTimeStr());
				int tem = job.getType();
				String str = "";
				if(tem==1){
					str = "Data Uploaded";
				}else if(tem==2){
					str = "Data Assigned";
				}
				else if(tem==3){
					str = "Data Downloaded";
				}
				else if(tem==4){
					str = "Report Uploaded";
				}
				else if(tem==5){
					str = "Report Assigned";
				}
				else if(tem==6){
					str = "Report Downloaded";
				}
				else if(tem==7){
					str = "Report Ready";
				}
				else{
					str = "Data Uploaded";
				}    				
				row.createCell(11).setCellValue(str);
				row.createCell(12).setCellValue(job.isFree());
				i++;
			}
			OutputStream outputStream;
			try {
				outputStream = new BufferedOutputStream(response.getOutputStream());
				wkb.write(outputStream);				
				response.setHeader("Content-Disposition", "attachment; filename=\"DoctorReport.xls\"");
				//response.addHeader("Content-Length", ""+userData.getStream().length);
				response.setContentType("application/vnd.ms-excel;charset=UTF-8");								
				outputStream.flush();  	
				outputStream.close();
			} catch (IOException e) {
				
			}			
		}
		return null;
	}
				
	@RequestMapping(value="/operator",method=RequestMethod.GET)
	public ModelAndView operatorReport(HttpServletRequest request){
		ModelAndView mav=new ModelAndView();	
		TadminUser tUser=getSessionUser(request);
		if(tUser!=null){
			mav.addObject("doctorList", adminUserService.loadDoctorList());
			mav.setViewName("report/operator");
		}
		else{
			tUser=new TadminUser();		
			mav.addObject("user", tUser);
			mav.setViewName("login");
		}
		return mav;
	}
	
	@RequestMapping(value="/operator/list",method=RequestMethod.GET)
	@ResponseBody
	public String operatorList(HttpServletRequest request,DataTableParamter dtp){		
		TadminUser tUser=getSessionUser(request);		
		if(tUser!=null){
			PagingData pagingData=null;
			pagingData=adminJobService.loadAdminJobList(dtp,tUser);
			if(pagingData!=null){
				pagingData.setSEcho(dtp.sEcho);
				if(pagingData.getAaData()==null){
					Object[] objs=new Object[]{};
					pagingData.setAaData(objs);
				}
			}
			String doctorListJson= JSON.toJSONString(pagingData);			
			return doctorListJson;	
		}
		return "";
	}
	
	@RequestMapping(value="/operator/excel",method=RequestMethod.POST)	
	public String operatorExcel(HttpServletRequest request,HttpServletResponse response){		
		TadminUser tUser=getSessionUser(request);		
		if(tUser!=null){
			TadminJob adminJob=new TadminJob();			
			adminJob.setCode(request.getParameter("code"));
			adminJob.setCreatedBy(request.getParameter("createdBy"));
			if(!request.getParameter("type").isEmpty()){
				adminJob.setType(Integer.parseInt(request.getParameter("type")));
			}
			adminJob.setDoctor(request.getParameter("adminUser.adminId"));
			adminJob.setStartTime(request.getParameter("startTime"));
			adminJob.setEndTime(request.getParameter("endTime"));
			List<TadminJob> jobList=new ArrayList<TadminJob>();
			
			jobList=adminJobService.loadOperatorJobList(adminJob,tUser);									
			//创建HSSFWorkbook对象  
			HSSFWorkbook wkb = new HSSFWorkbook();  
			//创建HSSFSheet对象  
			HSSFSheet sheet = wkb.createSheet("sheet");  
			//创建HSSFRow对象  
			HSSFRow hrow = sheet.createRow(0);  
			hrow.createCell(0).setCellValue("ID");
			hrow.createCell(1).setCellValue("Screen Code");
			hrow.createCell(2).setCellValue("Client Name");
			hrow.createCell(3).setCellValue("Operator");
			hrow.createCell(4).setCellValue("Created Time");
			hrow.createCell(5).setCellValue("Doctor");
			hrow.createCell(6).setCellValue("Assigned Time");
			hrow.createCell(7).setCellValue("Reporting Time");
			hrow.createCell(8).setCellValue("Uploaded Time");
			hrow.createCell(9).setCellValue("Done Time");
			hrow.createCell(10).setCellValue("Download Time");
			hrow.createCell(11).setCellValue("Status");
			hrow.createCell(12).setCellValue("Is Free/Demo");
			  			
			int i=1;
			for (TadminJob job : jobList) {				
				HSSFRow row = sheet.createRow(i);
				row.createCell(0).setCellValue(job.getJobId());
				row.createCell(1).setCellValue(job.getCode());
				row.createCell(2).setCellValue(job.getClientName());
				row.createCell(3).setCellValue(job.getCreatedBy());
				row.createCell(4).setCellValue(job.getCreatedTimeStr());
				row.createCell(5).setCellValue(job.getDoctor());
				row.createCell(6).setCellValue(job.getAssignTimeStr());
				row.createCell(7).setCellValue(job.getReportTimeStr());
				row.createCell(8).setCellValue(job.getDoneTimeStr());
				row.createCell(9).setCellValue(job.getCloseTimeStr());
				row.createCell(10).setCellValue(job.getDownloadTimeStr());
				int tem = job.getType();
				String str = "";
				if(tem==1){
					str = "Data Uploaded";
				}else if(tem==2){
					str = "Data Assigned";
				}
				else if(tem==3){
					str = "Data Downloaded";
				}
				else if(tem==4){
					str = "Report Uploaded";
				}
				else if(tem==5){
					str = "Report Assigned";
				}
				else if(tem==6){
					str = "Report Downloaded";
				}
				else if(tem==7){
					str = "Report Ready";
				}
				else{
					str = "Data Uploaded";
				}    				
				row.createCell(11).setCellValue(str);
				row.createCell(12).setCellValue(job.isFree());
				i++;
			}
			OutputStream outputStream;
			try {
				outputStream = new BufferedOutputStream(response.getOutputStream());
				wkb.write(outputStream);
				response.setHeader("Content-Disposition", "attachment; filename=\"OperatorReport.xls\"");
				//response.addHeader("Content-Length", ""+userData.getStream().length);
				response.setContentType("application/vnd.ms-excel;charset=UTF-8");								
				outputStream.flush();  	
				outputStream.close();
			} catch (IOException e) {
				
			}			
		}
		return null;
	}
	
	/**
	 * 下载扫描后原始上传的数据Zip文件
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping(value="downloadScreenZip/{id}",method=RequestMethod.GET)	
	public String downloadScreenZip(HttpServletResponse response, @PathVariable String id){
			
		TadminJob job=adminJobService.getAdminJobById(Integer.parseInt(id));
		TuserData userData=userDataService.loadScreenZip(job.getUser().getUserId());		
							
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
	
	/**
	 * 下载医生最终完成的Zip文件
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping(value="downloadDocotrZip/{id}",method=RequestMethod.GET)	
	public String downloadDoctorZip(HttpServletResponse response, @PathVariable String id){
			
		TadminJob job=adminJobService.getAdminJobById(Integer.parseInt(id));
		TuserData userData=userDataService.loadDoctorZip(job.getUser().getUserId());		
							
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
