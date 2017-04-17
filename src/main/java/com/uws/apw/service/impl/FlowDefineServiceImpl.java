package com.uws.apw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.apw.dao.IFlowDefineDao;
import com.uws.apw.model.FlowDefinePo;
import com.uws.apw.service.IFlowDefineService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;

@Service("flowDefineService")
@SuppressWarnings("all")
public class FlowDefineServiceImpl implements IFlowDefineService{

	@Autowired
	private IFlowDefineDao flowDefineDao;
	
	public Page getDeFineList(int pageNo, int pageSize, FlowDefinePo fdf) {
		return this.flowDefineDao.getDeFineList(pageNo, pageSize, fdf);
	}

	public void updateDefinePo(FlowDefinePo fdf) {
		this.flowDefineDao.updateDefinePo(fdf);
	}
	
	public void createDefinePo(FlowDefinePo fdf) {
		this.flowDefineDao.createDefinePo(fdf);
	}
	
	public void editDefineEnable(FlowDefinePo fdfPo) {
		this.flowDefineDao.editDefineEnable(fdfPo);
	}
	
	public boolean isExitDefineByKey(String pocessKey, String id) {
		return this.flowDefineDao.isExitDefineByKey(pocessKey, id);
	}

	public Page getDeFineListInfo(int pageNo, int pageSize) {
		return this.flowDefineDao.getDeFineListInfo(pageNo, pageSize);
	}
	
	public FlowDefinePo getDefineById(String id) {
		return this.flowDefineDao.getDefineById(id);
	}

	@Override
	public FlowDefinePo getFlowDefineByKey(String processKey) {
		return this.flowDefineDao.getFlowDefineByKey(processKey);
	}

	@Override
	public void editDefineDisable(String id) {
	}
	
	public boolean isExitProcessName(String processName, String id) {
		return this.flowDefineDao.isExitProcessName(processName, id);
	}

	@Override
	public boolean isAccessProcess(String processKey) {
		return this.flowDefineDao.isAccessProcess(processKey);
	}

	@Override
	public boolean isProcessShutdown(String processId) {
		return this.flowDefineDao.isProcessShutdown(processId);
	}

	@Override
	public FlowDefinePo processSwitch(String processId) {
		FlowDefinePo fdpo = this.flowDefineDao.getDefineById(processId);
		if(DataUtil.isNotNull(fdpo)){
			this.flowDefineDao.editDefineEnable(fdpo);
			return fdpo;
		}
		
		return new FlowDefinePo();
	}
}
