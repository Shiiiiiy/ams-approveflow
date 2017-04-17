package com.uws.apw.model;

import com.uws.core.base.BaseModel;
import com.uws.sys.model.Dic;
import com.uws.user.model.User;

public class FlowConfigPo extends BaseModel {

	private static final long serialVersionUID = -8943385446648699426L;

	private FlowDefinePo flowDefinePo;
	private String processId;
	private String taskName;//任务名称
	private Dic ptype;//岗位类型
	private String positionId;//岗位类型id
	private String agentPosition;//待办人岗位
	private String agentPosname;//待办人岗位名称
	private int taskSeq;//审批顺序
	private User creater;//创建人
	private Dic delStatus;//删除状态

	public FlowDefinePo getFlowDefinePo() {
		return flowDefinePo;
	}

	public void setFlowDefinePo(FlowDefinePo flowDefinePo) {
		this.flowDefinePo = flowDefinePo;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	

	public Dic getPtype() {
		return ptype;
	}

	public void setPtype(Dic ptype) {
		this.ptype = ptype;
	}

	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

	public String getAgentPosition() {
		return agentPosition;
	}

	public void setAgentPosition(String agentPosition) {
		this.agentPosition = agentPosition;
	}

	public String getAgentPosname() {
		return agentPosname;
	}

	public void setAgentPosname(String agentPosname) {
		this.agentPosname = agentPosname;
	}

	public int getTaskSeq() {
		return taskSeq;
	}

	public void setTaskSeq(int taskSeq) {
		this.taskSeq = taskSeq;
	}

	public User getCreater() {
		return creater;
	}

	public void setCreater(User creater) {
		this.creater = creater;
	}

	public Dic getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(Dic delStatus) {
		this.delStatus = delStatus;
	}

}
