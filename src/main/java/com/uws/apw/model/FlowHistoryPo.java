package com.uws.apw.model;

import java.util.Date;

import com.uws.core.base.BaseModel;
import com.uws.sys.model.Dic;
import com.uws.user.model.User;

public class FlowHistoryPo extends BaseModel {

	private static final long serialVersionUID = -2729322201313288400L;

	private String objectId;
	private FlowDefinePo flowDefinePo;
	private String processKey;
	private FlowConfigPo flowConfigPo;
	private String taskName;
	private User approver;
	private Dic approveResultDic;
	private String approveResult;
	private String suggest;
	private Dic isValid;
	private String approveToken;
	private int approveSeq;
	private User initiator;
	private Date submitTime;
	private Date approveTime;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public FlowDefinePo getFlowDefinePo() {
		return flowDefinePo;
	}

	public void setFlowDefinePo(FlowDefinePo flowDefinePo) {
		this.flowDefinePo = flowDefinePo;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public FlowConfigPo getFlowConfigPo() {
		return flowConfigPo;
	}

	public void setFlowConfigPo(FlowConfigPo flowConfigPo) {
		this.flowConfigPo = flowConfigPo;
	}

	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}

	public Dic getApproveResultDic() {
		return approveResultDic;
	}

	public void setApproveResultDic(Dic approveResultDic) {
		this.approveResultDic = approveResultDic;
	}

	public String getApproveResult() {
		return approveResult;
	}

	public void setApproveResult(String approveResult) {
		this.approveResult = approveResult;
	}

	public String getSuggest() {
		return suggest;
	}

	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}

	public Dic getIsValid() {
		return isValid;
	}

	public void setIsValid(Dic isValid) {
		this.isValid = isValid;
	}

	public String getApproveToken() {
		return approveToken;
	}

	public void setApproveToken(String approveToken) {
		this.approveToken = approveToken;
	}

	public int getApproveSeq() {
		return approveSeq;
	}

	public void setApproveSeq(int approveSeq) {
		this.approveSeq = approveSeq;
	}

	public User getInitiator() {
		return initiator;
	}

	public void setInitiator(User initiator) {
		this.initiator = initiator;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public Date getApproveTime() {
		return approveTime;
	}

	public void setApproveTime(Date approveTime) {
		this.approveTime = approveTime;
	}

}
