package com.uws.apw.model;

import com.uws.core.base.BaseModel;
import com.uws.sys.model.Dic;
import com.uws.user.model.User;
/**
 * 定义流程ModelPo
 */
public class FlowDefinePo extends BaseModel {

	private static final long serialVersionUID = -1136987042231984725L;
	
	/**流程定义Key**/
	private String processKey;
	/**流程定义名称**/
	private String processName;
	/**流程定义状态**/
	private Dic status;
	private String statusId;
	/**创建人**/
	private User creator;
	private String creatorId;
	
	/**json 返回**/
	private String success;
	


	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Dic getStatus() {
		return status;
	}

	public void setStatus(Dic status) {
		this.status = status;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	
	


	

}
