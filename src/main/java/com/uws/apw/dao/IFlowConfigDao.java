package com.uws.apw.dao;

import java.util.List;

import com.uws.apw.model.FlowConfigPo;
import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.user.model.Position;
import com.uws.user.model.Role;

public interface IFlowConfigDao extends IBaseDao{
	
	/**
	 * 查询流程定义的流程配置
	 * @param processKey
	 * @return
	 */
	public List<FlowConfigPo> getFlowConfigByDefine(String processKey);
	
	/**
	 * 查询流程定义的第一个流程配置
	 * @param processKey
	 * @param taskSeq
	 * @return
	 */
	public FlowConfigPo getNextConfigPoByIniator(String processKey,int taskSeq);

	/**
	 * @param pageNo
	 * @param pageSize
	 * @param fcfPo
	 * @param fdfId
	 * @return
	 */
	public Page getIflowConfigList(int pageNo,int pageSize,FlowConfigPo fcfPo,String fdfId);
	
	/**
	 * @param pageNo
	 * @param pageSize
	 * @param fcfPo
	 * @param fdfId
	 * @return
	 */
	public Page getIflowConfigListIno(int pageNo,int pageSize,String fdfId);
	
	/**
	 * 创建流程配置
	 */
	public void createFlowConfig(FlowConfigPo fcfPo);
	
	/**
	 * 修改流程配置
	 */
	public void updateFlowConfig(FlowConfigPo fcfPo);

	/**
	 * 删除流程配置
	 * @param id
	 */
	public void delIFlowConfig(String id);
	
	/**
	 * 获取单个流程配置信息
	 * @param id
	 * @return
	 */
	public FlowConfigPo getIFlowConfigById(String id);
	
	/**
	 * 获取审批顺序的最大值
	 * @param fdfid
	 * @return
	 */
	 public int getMaxtaskSeq(String fdfid);
	 
	 /**
	  * 编辑流程审批环节
	  * @param id
	  * @param fifid
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
	  * 删除当前审批环节
	  * @param fcfPo
	  * @param fifid
	  * @param taskSeq
	  */
	 public void delConfig(FlowConfigPo fcfPo);

	 /**
	  * 删除节点后上移其他节点
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
	public Page queryPosition(int pageNo, int pageSize, Position position);

	/**
	 * 获取分页角色信息
	 * @param pageNo
	 * @param paramInt
	 * @param role
	 * @return
	 */
	public Page queryRole(int pageNo, int paramInt, Role role);
	 
}
