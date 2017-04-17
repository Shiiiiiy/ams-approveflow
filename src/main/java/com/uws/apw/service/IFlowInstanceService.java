package com.uws.apw.service;

import java.util.List;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.model.FlowInstancePo;
import com.uws.apw.model.MulApproveResult;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.user.model.User;

public abstract interface IFlowInstanceService {
	
	/**
	 * 检测下一节点办理人【对外接口】
	 * @param objectId							业务主键ID
	 * @param processKey						流程定义key
	 * @param initiator							发起人
	 * @param isAccess							是否使用当前接口
	 * @return List<Approver>					下一节点办理人
	 */
	public List<Approver> getNextApproverList(String objectId,String processKey,User initiator,boolean isAccess);

	/**
	 * 初始化流程实例【对外接口】
	 * @param objectId							业务主键ID
	 * @param processKey						流程定义key
	 * @param initiator							发起人
	 * @param nextApprover				下一节点办理人
	 * @param isAccess							是否使用当前接口
	 * @return ApproveResult				流程审批结果
	 */
	public ApproveResult initProcessInstance(String objectId,String processKey,User initiator,User nextApprover,boolean isAccess);
	
	/**
	 * 修改流程实例【对外接口】
	 * @param objectId							业务主键ID
	 * @param processKey						流程定义key
	 * @param initiator							发起人
	 * @param isAccess							是否使用当前接口
	 * @return ApproveResult				流程审批结果
	 */
	public ApproveResult modifyProcessInstance(String objectId,String processKey,boolean isAccess);
	
	/**
	 * 【用户相关】流程审批历史查询【对外接口】
	 * @param pageNo						分页查询的起始索引
	 * @param pageSize							分页大小
	 * @param objectId							业务主键
	 * @param processStatus					流程审批状态Id
	 * @param initiatorId						发起人用户Id
	 * @param approverId						审批人用户Id
	 * @param startTime							流程发起时间【开始时间】
	 * @param endTime							流程发起时间【结束时间】
	 * @param isAccess							是否使用当前接口
	 * @return												流程审批历史列表
	 */
	public Page geUserProcessHistory(
			int pageNo,int pageSize,String objectId,String processStatus,
			String initiatorId, String approverId, String startTime,
			String endTime, boolean isAccess);
	
	/**
	 * 当前流程审批历史【对外接口】
	 * @param objectId							业务主键ID
	 * @param isAccess							是否使用当前接口
	 * @return												流程实例审批历史列表
	 */
	public List<FlowInstancePo> geCurProcessHistory(String objectId, boolean isAccess);
	
	/**
	 * 单次流程审批【对外接口】
	 * @param fipo										审批内容【结果+意见】
	 * @param objectId							业务主键
	 * @param approverId						审批人用户Id
	 * @param isAccess							是否使用当前接口
	 * @return												审批结果
	 */
	public ApproveResult saveProcessApproveResult(FlowInstancePo fipo,String objectId,String approverId,boolean isAccess);
	
	/**
	 * 批量流程审批【对外接口】
	 * @param approveKey					审批操作【PASS、NOT_PASS、REJECT】
	 * @param objectIds							业务主键列表
	 * @param fipo										审批内容【结果+意见】
	 * @param approverId						审批人用户Id
	 * @param isAccess							是否使用当前接口
	 * @return												批量审批结果
	 */
	public MulApproveResult saveMulApproveResult(String approveKey,FlowInstancePo fipo,List<String> objectIds,String approverId,boolean isAccess);
	
	/**
	 * 废弃当前发起的流程
	 * @param objectId		业务主键
	 * @param isAccess		是否使用当前接口
	 */
	public void deprecatedCurProcess(String objectId,boolean isAccess);
	
	/**
	 * 初始化下一审批环节办理人【对外接口】
	 * @param fipo										审批内容【结果+意见】
	 * @param objectId							业务主键ID
	 * @param userId								下一环节审批人用户Id
	 * @param isAccess							是否使用当前接口
	 * return 													下一环节办理人
	 */
	public ApproveResult initNextApprover(String objectId,String userId,boolean isAccess);
	
	/**
	 * 批量审批时校验下一审批环节办理人【对外接口】
	 * @param objectIds							业务主键Id列表
	 * @param curApproverId				当前审批人
	 * @param isAccess							是否使用当前接口
	 * @return												存在多个办理人的【业务主键列表】
	 */
	public List<String> checkTaskApprover(List<String> objectIds,String curApproverId,boolean isAccess);
	
	/**
	 * 验证流程是否可用
	 * @param processKey
	 * @return
	 */
	public boolean isValidProcess(String processKey);

	/**
	 * 审批人查看可见的待审批流程
	 * @param curApprover					当前审批人
	 * @return												流程代办列表
	 */
	public List<FlowInstancePo> getApwInstancePoList(String curApprover);

	/**
	 * 获取流程实例
	 * @param objectId						业务主键
	 * @param curUserId					当前用户Id
	 * @return											当前流程实例
	 */
	public FlowInstancePo getFlowInstancePo(String objectId,String curUserId);

	/**
	 * 判断当前节点是否流程末节点
	 * @param objectId						业务主键
	 * @param curUserId					当前用户
	 * @return											[true、false]
	 */
	public boolean isFinalTask_(String objectId,String curUserId);
	
	/**
	 * 判断当前节点是否流程末节点
	 * @param objectId						业务主键
	 * @param approverId					当前用户Id
	 * @return											[true、false]
	 */
	public boolean isFinalTask(String objectId, String approverId);

	/**
	 * 当前流程是否启用
	 * @param processKey					流程key
	 * @return											[true、false]
	 */
	public boolean isAccessProcess(String processKey);
	
	/**
	 * 当前流程审批历史查询
	 * @param objectId		业务主键
	 * @param pageNo			当前页
	 * @param pageSize		分页大小
	 * @param isAccess		接口是否可用
	 * @return							分页审批历史信息
	 */
	public Page getCurProcessHistory(String objectId,int pageNo,int pageSize,boolean isAccess);
	
	/**
	 * 获取流程历史
	 * @param objectId		业务主键
	 * @param isAccess		接口是否可用
	 */
	public List<FlowHistoryPo> getCurProcessHistory(String objectId, boolean isAccess);

	/**
	 * 回滚当前审批到上一步
	 * @param objectId			业务主键
	 * @param processKey		流程定义key
	 */
	public void rollback2LastStep(String objectId, String processKey);
	
	/**
	 * 获取批量审批后的结果列表
	 * @param mulResults						审批流返回的批量审批结果
	 * @param isAccess							是否使用当前接口
	 * @return												审批结果集合
	 */
	public List<ApproveResult> getFormatedResult(String mulResults,boolean isAccess);
	
	/**
	 * 根据流程定义key获取业务审批列表
	 * @param processKey	流程定义key
	 * @return 业务审批列表
	 */
	public String[] getObjectIdByProcessKey(String processKey,String userId);

}
