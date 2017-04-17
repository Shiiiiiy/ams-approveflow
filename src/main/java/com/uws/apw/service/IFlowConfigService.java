package com.uws.apw.service;

import java.util.List;

import com.uws.apw.model.FlowConfigPo;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.user.model.Position;
import com.uws.user.model.Role;

public abstract interface IFlowConfigService {
	
	/**
	 * 根据流程定义获取流程配置对象列表
	 * @param processKey
	 * @return
	 */
	List<FlowConfigPo> getFlowConfigByDefine(String processKey);
	
	/**
	 * 根据流程定义获取流程配置对象列表
	 * @param processKey
	 * @return
	 */
	FlowConfigPo getNextConfigPoByIniator(String processKey,int taskSeq);

	/**
	 * 获取流程配置列表
	 * @param pageNo
	 * @param pageSize
	 * @param fcfPo
	 * @param fdfId
	 * @return
	 */
	public Page getIflowConfigList(int pageNo,int pageSize,FlowConfigPo fcfPo,String fdfId);
	
	/**
	 * 创建流程配置
	 */
	public void createFlowConfig(FlowConfigPo fcfPo);
	
	/**
	 * 修改流程配置
	 */
	public void updateFlowConfig(FlowConfigPo fcfPo);
	
	/**
	 * 获取单个流程配置信息
	 * @param id
	 * @return
	 */
	public FlowConfigPo getIFlowConfigById(String id);
	
	/**
	 * 获取审批顺序最大值
	 * @param fdfid
	 * @return
	 */
	 public int getMaxtaskSeq(String fdfid);
	 
	 /**
	  * @return
	  */
	 public List<FlowConfigPo> editTaskSep(String id,String fifid);
	 
	 /**
	  * 上移当前审批环节
	  * @param id
	  * @param taskSeq
	  * @param fifid
	  */
	 public void flowerConfig(String id,int taskSeq,String fifid);
	 
	 /**
	  * 下移当前审批环节
	  * @param id
	  * @param taskSeq
	  * @param fifid
	  */
	 public void godownConfig(String id,int taskSeq,String fifid);
	 
	 /**
	  * 获取流程配置列表
	  * @param pageNo
	  * @param pageSize
	  * @param fdfId
	  * @return
	  */
	public Page getIflowConfigListIno(int pageNo,int pageSize,String fdfId);
	
	/**
	 * 删除当前审批环节
	 * @param fcfPo
	 * @param fifid
	 * @param taskSeq
	 */
	public void delConfig(FlowConfigPo fcfPo);

	/**
	 * 删除节点后上移下级节点
	 * @param fcf
	 * @param seq
	 */
	public void replaceDelConfig(FlowConfigPo fcf);

	/**
	 * 获取审批岗位列表
	 * @param pageNo
	 * @param pageSize
	 * @param paramPosition
	 * @return
	 */
	public abstract Page queryPosition(int pageNo, int pageSize,Position paramPosition);
	
	/**
	 * 获取审批角色列表
	 * @param pageNo
	 * @param paramInt
	 * @param paramRole
	 * @return
	 */
    public abstract Page queryRole(int pageNo, int paramInt, Role paramRole);

}
