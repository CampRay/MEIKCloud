package com.nuvomed.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.nuvomed.dto.Tsetting;
import com.nuvomed.model.PagingData;

public interface SystemSettingService {
	
	Tsetting getSystemsettingById(Integer Id);
	
	void createSystemsetting(Tsetting setting);
	
	void updateSystemsetting(Tsetting setting);
	
	void deleteSystemsetting(Tsetting setting);
	
	void deleteSystemsetting(int id);
	
	void deleteSystemsettingByIds(Integer[] ids);
	
	public PagingData loadSystemsetting();

	public void cachedSystemSettingData() throws UnsupportedEncodingException, IOException;
	
	public PagingData getStoreSetting();
	
	Tsetting getSystemSettingByName(String name);

}
