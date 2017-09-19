package com.nuvomed.dao;

import java.util.List;
import org.springframework.stereotype.Repository;

import com.nuvomed.dao.base.BaseDao;
import com.nuvomed.dto.TuserData;

/**
 * Home object for domain model class TuserData.
 * @see com.bps.dto.TuserData
 * @author Hibernate Tools
 */
@Repository
public class UserDataDao extends BaseDao<TuserData> {
	
	/**
	 * 查询出没有文件流字段的UserData数据
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TuserData> findUserDataWithoutSteam(int userId,int dataType){
		return this.findByHqlName("findUserData", new Integer[]{userId,dataType});		
	}	
	
	@SuppressWarnings("unchecked")
	public List<TuserData> findUserDataWithoutSteam(int userId){
		return this.findByHqlName("findAllUserData", new Integer[]{userId});		
	}
}
