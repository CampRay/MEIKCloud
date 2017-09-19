package com.nuvomed.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.nuvomed.dao.base.BaseDao;
import com.nuvomed.dto.TadminUser;
import com.nuvomed.dto.Tgroup;
import com.nuvomed.dto.TgroupUser;
import com.nuvomed.dto.Tlanguage;

@Repository
public class GroupUserDao extends BaseDao<TgroupUser> {
	@SuppressWarnings("unchecked")
	public List<TgroupUser> getGroupUserByUser(TadminUser adminUser) {
		Criteria criteria=createCriteria();
		return criteria.add(Restrictions.eq("adminUser", adminUser)).list();						
	}
	
	@SuppressWarnings("unchecked")
	public List<TgroupUser> getGroupUserByGroup(Tgroup group) {
		Criteria criteria=createCriteria();
		return criteria.add(Restrictions.eq("group", group)).list();						
	}
	
	@SuppressWarnings("unchecked")
	public List<TadminUser> getAdminUserByGroup(Tgroup group) {
		String hql="select g.adminUser from TgroupUser g where g.group=?";
		Query query=currentSession().createQuery(hql);
		query.setParameter(0, group);		
		List<TadminUser> adminUserList= query.list();
		return adminUserList;					
	}
	
	@SuppressWarnings("unchecked")
	public List<TadminUser> getAdminUserByGroupList(List<Tgroup> groupList) {
		String hql="select g.adminUser from TgroupUser g where g.group in elements(:groups)";
		Query query=currentSession().createQuery(hql);
		query.setParameterList("groups", groupList);		
		List<TadminUser> adminUserList= query.list();
		return adminUserList;					
	}
	
	/**
	 * 查找出所有和指定用户是一个组的所有用户
	 * @param adminUser
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TadminUser> getAdminUserByUser(TadminUser adminUser) {
		String hql="select g.adminUser from TgroupUser g where g.group.id in (select gu.group.id from TgroupUser gu where gu.adminUser=?)";
		Query query=currentSession().createQuery(hql);
		query.setParameter(0, adminUser);		
		List<TadminUser> adminUserList= query.list();
		return adminUserList;					
	}
	
	/**
	 * 查找出所有和指定用户是一个组的所有用户
	 * @param adminUser
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAdminIdsByUser(TadminUser adminUser) {
		String hql="select g.adminUser.adminId from TgroupUser g where g.group.id in (select gu.group.id from TgroupUser gu where gu.adminUser=?)";
		Query query=currentSession().createQuery(hql);
		query.setParameter(0, adminUser);		
		List<String> adminIdList= query.list();
		return adminIdList;					
	}
}
