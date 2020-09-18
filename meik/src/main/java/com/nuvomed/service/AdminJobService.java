package com.nuvomed.service;

import java.util.List;

import com.nuvomed.dto.TadminJob;
import com.nuvomed.dto.TadminUser;
import com.nuvomed.dto.TuserData;
import com.nuvomed.dto.TuserInfo;
import com.nuvomed.model.DataTableParamter;
import com.nuvomed.model.PagingData;

public interface AdminJobService {
	
	TadminJob getAdminJobById(int adminJobId);
			
	TadminJob getAdminJobByCode(String code);
	
	void createAdminJob(TadminJob adminJob);
	
	void updateAdminJob(TadminJob adminJob);
	
	void deleteAdminJob(TadminJob adminJob);
	
	void deleteAdminJobById(Integer id);
	
	void deleteAdminJobByIds(Integer[] ids);
	
	/**
	 * 查询所有分配给医生的任务记录
	 * @param doctorId
	 * @return
	 */	
	public List<TadminJob> loadDataAssignedJobList(String doctorId);
	
	/**
	 * 查询所有分配给系统医生的报表任务记录
	 * @param doctorId 
	 * @return
	 */	
	public List<TadminJob> loadReportAssignedJobList();
	
	List<TadminJob> loadAdminJobList(String adminId,int type);
	
	TadminJob loadDownloadedJobByUserCode(String adminId,String code);
	TadminJob loadDoneJobByUserCode(String adminId,String code);
	TadminJob loadDoneJobByUserCode(String code);
	
	public PagingData loadAdminJobList(DataTableParamter rdtp,TadminUser adminUser);
	
	PagingData loadDoctorJobList(DataTableParamter rdtp,TadminUser adminUser); 
	List<TadminJob> loadDoctorJobList(TadminJob searchObj,TadminUser adminUser);
	
	
	List<TadminJob> loadOperatorJobList(TadminJob searchObj,TadminUser adminUser);	
		
	public PagingData loadClientJobList(DataTableParamter rdtp,TadminUser adminUser);
	
	public void createScreenData(TuserInfo userInfo,TuserData userData,String createdBy);
	public void createReportData(TuserInfo userInfo,TuserData userData,TadminUser createdBy);
	
	public void updateReportData(TuserInfo userInfo,TuserData userData,TadminJob adminJob);
}
