package com.uws.apw.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.apw.dao.IFlowConfigDao;
import com.uws.apw.model.FlowConfigPo;
import com.uws.apw.service.IFlowConfigService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.user.dao.IPositionDao;
import com.uws.user.model.Position;
import com.uws.user.model.Role;

@Service("flowConfigService")
@SuppressWarnings("all")
public class FlowConfigServiceImpl implements IFlowConfigService{

	@Autowired
	private IFlowConfigDao flowConfigDao;
	
	@Autowired
	private IPositionDao positionDao;
	
	@Override
	public List<FlowConfigPo> getFlowConfigByDefine(String processKey) {
		return this.flowConfigDao.getFlowConfigByDefine(processKey);
	}

	@Override
	public Page getIflowConfigList(int pageNo, int pageSize,FlowConfigPo fcfPo, String fdfId) {
		return this.flowConfigDao.getIflowConfigList(pageNo, pageSize, fcfPo, fdfId);
	}
	
	/**
	 * 创建流程配置
	 */
	@Override
	public void createFlowConfig(FlowConfigPo fcfPo) {
			this.flowConfigDao.createFlowConfig(fcfPo);
	}
	
	/**
	 * 修改流程配置
	 */
	@Override
	public void updateFlowConfig(FlowConfigPo fcfPo) {
		 this.flowConfigDao.updateFlowConfig(fcfPo);
	}
	
	@Override
	public FlowConfigPo getIFlowConfigById(String id) {
		return this.flowConfigDao.getIFlowConfigById(id);
	}

	@Override
	public int getMaxtaskSeq(String fdfid) {
		return this.flowConfigDao.getMaxtaskSeq(fdfid);
	}

	@Override
	public List<FlowConfigPo> editTaskSep(String id,String fdfid) {
		return this.flowConfigDao.editTaskSep(id, fdfid);
	}
	
	@Override
	public Page getIflowConfigListIno(int pageNo, int pageSize, String fdfId) {
		return this.flowConfigDao.getIflowConfigListIno(pageNo, pageSize, fdfId);
	}
	
	@Override
	public void delConfig(FlowConfigPo fcfPo) {
		this.flowConfigDao.delConfig(fcfPo);
	}
	
	@Override
	public void replaceDelConfig(FlowConfigPo fcf) {
		this.flowConfigDao.replaceDelConfig(fcf);
	}

	@Override
	public void flowerConfig(String id, int taskSeq,String fifid) {
		this.flowConfigDao.flowerConfig(id, taskSeq,fifid);
	}

	@Override
	public void godownConfig(String id, int taskSeq,String fifid) {
		this.flowConfigDao.godownConfig(id, taskSeq,fifid);
	}
	
	@Override
	public Page queryPosition(int pageNo, int pageSize,Position position)
	{
	     return this.flowConfigDao.queryPosition(pageNo,pageSize,position);
     }

	@Override
	public FlowConfigPo getNextConfigPoByIniator(String processKey,int taskSeq)
    {
		return this.flowConfigDao.getNextConfigPoByIniator(processKey,taskSeq);
    }

	@Override
	public Page queryRole(int pageNo, int paramInt, Role role){
	    return this.flowConfigDao.queryRole(pageNo,paramInt,role);
	}
}
