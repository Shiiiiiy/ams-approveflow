package com.uws.apw.dao;

import java.util.List;

import com.uws.apw.model.FlowConfigPo;
import com.uws.apw.model.FlowDefinePo;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.model.FlowInstancePo;
import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.user.model.Org;
import com.uws.user.model.User;
import com.uws.user.model.UserOrgPosition;
import com.uws.user.model.UserRole;

public interface IFlowInstanceDao extends IBaseDao{

	/**
	 * 保存流程实例
	 * @param fipo
	 */
	public void saveProcessInstance(FlowInstancePo fipo);
	
	/**
	 * 修改流程实例
	 * @param fipo
	 */
	public void modifyProcessInstance(FlowInstancePo fipo);
	
	/**
	 * 【用户相关】流程审批历史查询
	 * @param pageNo						分页查询的起始索引
	 * @param pageSize							分页大小
	 * @param objectId							业务主键
	 * @param processStatus					流程审批状态Id
	 * @param initiatorId						发起人用户Id
	 * @param approverId						审批人用户Id
	 * @param startTime							流程发起时间【开始时间】
	 * @param endTime							流程发起时间【结束时间】
	 * @return												流程审批历史列表
	 */
	public Page geUserProcessHistory(int pageNo,int pageSize,String objectId,
			String processStatus,String initiatorId, String approverId,String startTime,String endTime);
	
	/**
	 * 当前流程审批历史
	 * @param objectId
	 * @param isAccess
	 * @return
	 */
	public List<FlowInstancePo> geCurProcessHistory(String objectId);
	
	/**
	 * 流程审批保存
	 * @param po	流程实例
	 * @return			流程审批结果
	 */
	public void saveProcessApprove(FlowInstancePo po);
	
	/**
	 * 初始化下一审批环节办理人
	 * @param objectId				业务Id
	 * @param nextApprover	下一环节办理人
	 */
	public void initNextApprover(String objectId,User nextApprover);
	
	/**
	 * 审批人查看可见的待审批流程
	 * @param curApprover
	 * @return	可见的流程列表
	 */
	public List<FlowInstancePo> getApwInstancePoList(String curApprover);

	/**
	 * 获取流程实例审批记录
	 * @param objectId
	 * @param curUserId
	 * @return
	 */
	public FlowInstancePo getFlowInstancePo(String objectId,String curUserId);

	/**
	 * 获取当前组织机构下对应岗位的用户信息
	 * @param positionId
	 * @param path
	 * @return
	 */
	public List<UserOrgPosition> getUserByPosition_(String positionId,String path);
	
	/**
	 * 获取当前节点的审批角色信息
	 * @param roleId						角色ID
	 * @param pathCondition		发起人组织机构条件
	 * @return
	 */
	public List<UserRole> getUserByRole(String roleId,String pathCondition);

	/**
	 * 获取当前审批节点流程实例【带审批令牌】
	 * @param objectId
	 * @param approverId
	 * @return
	 */
	public FlowInstancePo getCurApproveTaskPo(String objectId, String approverId);
	
	/**
	 * 获取当前审批节点流程实例【不带审批令牌】
	 * @param objectId
	 * @param approverId
	 * @return
	 */
	public FlowInstancePo getCurApproveTaskPo_(String objectId, String approverId);
	
	/**
	 * 获取下一节点的流程实例
	 * @param curTaskPo
	 * @return
	 */
	public FlowInstancePo getNextTaskPo(FlowInstancePo curTaskPo);
	
	/**
	 * 获取上一节点的流程实例
	 * @param nextTaskPo
	 * @return
	 */
	public FlowInstancePo getLastTaskPo(FlowInstancePo nextTaskPo);
	
	/**
	 * 根据用户id获取用户信息
	 * @param userId
	 */
	public User getUserByID(String userId);

	/**
	 * 获取当前审批人批量勾选的流程实例
	 * @param objectIds
	 * @param curApproverId
	 * @return
	 */
	public List<FlowInstancePo> getCurApproverTaskPo(String objectIds,String curApproverId);

	/**
	 * 根据发起人获取发起人所在组织机构
	 * @param initiator	发起人
	 * @return		发起人组织机构
	 */
	public Org getUserOrg(User initiator);

	/**
	 * 获取下一节点配置信息
	 * @param curTaskPo
	 * @return
	 */
	public FlowConfigPo getNextConfigPo(FlowConfigPo curTaskPo);

	/**
	 * 根据业务主键、流程定义key获取流程实例信息
	 * @param objectId				业务主键
	 * @param processKey			流程定义key
	 * @param taskId				审批环节id
	 * @return
	 */
	public FlowInstancePo getInstancePoByObjKey(String objectId,String processKey,String taskId);

	/**
	 * 验证流程是否可用
	 * @param processKey
	 * @return
	 */
	public FlowDefinePo getFlowDefinePoByKey(String processKey);

	/**
	 * 获取当前业务下审批流的审批环节
	 * @param objectId		业务主键
	 * @param processKey	审批流key
	 * @return
	 */
	public List<FlowInstancePo> getObjInstanceListByKey(String objectId,String processKey);

	/**
	 * 获取当前业务下审批流的审批环节
	 * @param objectId		业务主键
	 * @param taskSeq		下一审批环节序号
	 * @return
	 */
	public FlowInstancePo getTaskPoByTaskSeq(String objectId, int taskSeq);
	
	/**
	 * 当前流程审批历史查询
	 * @param objectId		业务主键
	 * @param pageNo			当前页
	 * @param pageSize		分页大小
	 * @return							分页审批历史信息
	 */
	public Page getCurProcessHistory(String objectId,int pageNo,int pageSize);
	
	/**
	 * 当前流程审批历史
	 * @param objectId		业务主键
	 * @return							流程审批历史
	 */
	public List<FlowHistoryPo> getCurProcessHistory(String objectId);

	/**
	 * 清空当前流程发起的实例
	 * @param objectId				业务主键
	 * @param processKey			流程key
	 */
	public void deleteAllAction(String objectId, String processKey);

	/**
	 * 根据当前节点获取下一节点
	 * @param curTaskPo		当前任务节点
	 * @return								下一任务节点
	 */
	public FlowInstancePo getNextTaskByCurTask(FlowInstancePo curTaskPo);

	/**
	 * 获取下一审批节点流程对象
	 * @param processKey		流程key
	 * @param approveSeq		审批顺序
	 * @param objectId			业务主键ID
	 * @return								下一审批环节对象
	 */
	public FlowInstancePo getNextTaskByIniator(String objectId,String processKey, int approveSeq);

	/**
	 * 获取当前流程实例
	 * @param objectId				业务主键
	 * @param processKey			流程key
	 * @return									当前流程实例
	 */
	public FlowInstancePo getCurTaskPoByKey(String objectId, String processKey);

	/**
	 * 重新为实例授权令牌
	 * @param objectId				业务主键
	 * @param processKey			流程key
	 * @param approveSeq			节点顺序
	 */
	public void rollbackApproveToken(String objectId, String processKey,int approveSeq);

	/**
	 * 获取学生所在的学院组织结构信息
	 * @param orgId			学院组织机构
	 * @return
	 */
	public Org getOrgById(String orgId);

	/**
	 * 废弃当前流程
	 * @param objectId	业务主键
	 */
	public void deprecatedCurProcess(String objectId);

	/**
	 * 获取用户所在的组织机构
	 * @param userId
	 * @return
	 */
	public List<UserOrgPosition> getUserOrgPosition(String userId);

	/**
	 * 根据流程定义key获取流程历史列表
	 * @param processKey	流程定义key
	 * @return 流程历史列表
	 */
	public List<FlowHistoryPo> getFlowHistoryByProcessKey(String processKey,String userId);

	/**
	 * 获取当前流程审批节点
	 * @param objectId		 业务主键
	 * @return	当前审批节点
	 */
	public FlowInstancePo getCurTaskPoByToken(String objectId);

	/**
	 * 根据审批顺序，获取审任务节点
	 * @param objectId	业务主键
	 * @param taskSeq		审批顺序
	 * @return	审批实例
	 */
	public FlowInstancePo getApproveTaskBySeq(String objectId, int taskSeq);

}
