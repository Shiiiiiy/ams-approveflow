package com.uws.apw.model;

import com.uws.sys.model.Dic;

public class ResultConfigPo {
	private String id;
	//���̶���id 
	
	//������������
	private String taskName;
	//��λ����
	private String  positionName;
	private String positionId;
	//������λ
	private String agentPosition;
	//������λ����
	private String agentPosname;
	//����˳��
	private int taskSeq;
	
	private String fdfid;
	
	private String  pname;
	
	
	
	
	
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getFdfid() {
		return fdfid;
	}
	public void setFdfid(String fdfid) {
		this.fdfid = fdfid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
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
	
	
}
