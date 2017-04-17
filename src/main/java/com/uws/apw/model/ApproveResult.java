package com.uws.apw.model;

import java.util.List;

/**
 * 流程审批结果
 */
public class ApproveResult {

	/**
	 * 业务主键
	 */
	private String objectId;
	
	/**
	 * 流程定义key
	 */
	private String processKey;
	
	/**
	 * 审批操作【PASS、NOT_PASS、REJECT】
	 */
	private String approveKey;

	/**
	 * 审批状态【岗位+审批结果】
	 */
	private String approveStatus;

	/**
	 * 流程状态【审批结果】
	 */
	private String processStatusCode;

	/**
	 * 审批结果
	 */
	private String approveResultCode;
	
	/**
	 * 下一环节名称
	 */
	private String nextTaskName;

	/**
	 * 下一审批环节待办人列表
	 */
	private List<Approver> nextApproverList;

	/**
	 * 流程【操作结果】【success、failed】
	 */
	private String resultFlag;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getApproveKey() {
		return approveKey;
	}

	public void setApproveKey(String approveKey) {
		this.approveKey = approveKey;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getProcessStatusCode() {
		return processStatusCode;
	}

	public void setProcessStatusCode(String processStatusCode) {
		this.processStatusCode = processStatusCode;
	}

	public String getApproveResultCode() {
		return approveResultCode;
	}

	public void setApproveResultCode(String approveResultCode) {
		this.approveResultCode = approveResultCode;
	}

	public String getNextTaskName() {
		return nextTaskName;
	}

	public void setNextTaskName(String nextTaskName) {
		this.nextTaskName = nextTaskName;
	}

	public List<Approver> getNextApproverList() {
		return nextApproverList;
	}

	public void setNextApproverList(List<Approver> nextApproverList) {
		this.nextApproverList = nextApproverList;
	}

	public String getResultFlag() {
		return resultFlag;
	}

	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}

}
