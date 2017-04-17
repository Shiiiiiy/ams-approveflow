package com.uws.apw.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.apw.dao.IFlowInstanceDao;
import com.uws.apw.model.FlowConfigPo;
import com.uws.apw.model.FlowDefinePo;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.model.FlowInstancePo;
import com.uws.apw.util.ApwConstants;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.Org;
import com.uws.user.model.User;
import com.uws.user.model.UserOrgPosition;
import com.uws.user.model.UserRole;

@Repository("flowInstanceDao")
@SuppressWarnings("all")
public class FlowInstanceDaoImpl extends BaseDaoImpl implements IFlowInstanceDao{
	
	private DicUtil dicUtil = DicFactory.getDicUtil();

	/**
	 * 保存流程实例
	 * @FlowInstancePo fipo   流程实例对象
	 */
	@Override
	public void saveProcessInstance(FlowInstancePo fipo) {
		this.save(fipo);
	}
	
	/**
	 * 修改流程实例
	 * @FlowInstancePo fipo   流程实例对象
	 */
	@Override
	public void modifyProcessInstance(FlowInstancePo fipo) {
		this.update(fipo);
	}

	/**
	 * 用户相关流程审批历史查询
	 * @param pageNo						分页查询的起始索引
	 * @param pageSize							分页大小
	 * @param objectId							业务主键
	 * @param processStatus					流程审批状态Id
	 * @param initiatorId						发起人用户Id
	 * @param approverId						审批人用户Id
	 * @param startTime							流程发起时间【开始时间】
	 * @param endTime							流程发起时间【结束时间】
	 * @return												流程审批历史
	 */
	@Override
	public Page geUserProcessHistory(int pageNo,int pageSize,String objectId,
			String processStatus,String initiatorId, String approverId,String startTime,String endTime) {
		List<String> values = new ArrayList<String>();//审批历史查询条件
		StringBuffer sbhql=new StringBuffer(" from FlowHistoryPo fhpo");
		sbhql.append(" where 1=1 ");
		
		if(DataUtil.isNotNull(objectId))
		{
			sbhql.append(" and fhpo.objectId= ? ");
			values.add(objectId);
		}
		
		if(StringUtils.isNotEmpty(processStatus))
		{
			sbhql.append(" and"); 
			sbhql.append(" fhpo.approveResultDic.id = ? ");
			values.add(processStatus);
		}
		
		if(StringUtils.isNotEmpty(approverId))
		{
			sbhql.append(" and"); 
			sbhql.append(" fhpo.approver.id = ? "); 
			values.add(approverId);
		}
		
		if(StringUtils.isNotEmpty(initiatorId))
		{   sbhql.append(" and");
	    	sbhql.append(" fhpo.initiator.id = ? "); 
		    values.add(initiatorId);
		}
		
		if(StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime))
		{
			 sbhql.append(" and ");
			 sbhql.append(" DATE_FORMAT(fhpo.submitTime,'%Y%-%m-%d') BETWEEN  ? AND ?  "); 
			 values.add(startTime);
			 values.add(endTime);
		}
		sbhql.append(" order by fhpo.submitTime,fhpo.approveTime desc");
		return pagedQuery(sbhql.toString(), pageNo, pageSize, values.toArray());
	}
	
	/**
	 * 当前流程审批历史查询
	 * @objectId											业务主键
	 * @return												流程实例进度历史
	 */
	@Override
	public List<FlowInstancePo> geCurProcessHistory(String objectId) {
		List<String> values = new ArrayList<String>();//审批历史查询条件
		values.add(objectId);
		values.add("Y");
		return query("select fipo from FlowInstancePo fipo where  fipo.objectId=? and fipo.isValid.code=?  " +
				" and fipo.approveResultDic is not null and fipo.approveToken<>'"+ApwConstants.APPROVETOKEN.AVAILABLE+"' order by fipo.approveSeq asc", values.toArray());
	}

	/**
	 * 流程审批保存
	 * @param objectId				业务主键ID
	 * @param processKey			流程定义key
	 * @param approverId			审批人userId
	 * @return									流程审批结果
	 */
	@Override
	public void saveProcessApprove(FlowInstancePo po) {
		this.update(po);
	}

	/**
	 * 初始化下一审批环节办理人
	 * @param objectId					业务主键ID
	 * @param nextApproverId	下一环节审批人userId
	 */
	@Override
	public void initNextApprover(String objectId, User nextApprover) {
		String hql="select fipo from FlowInstancePo fipo where  fipo.objectId=? and fipo.approveToken=?";
		FlowInstancePo nextTaskPo = (FlowInstancePo)this.queryUnique(hql, new Object[]{objectId,ApwConstants.APPROVETOKEN.AVAILABLE.toString()});
		if(DataUtil.isNotNull(nextTaskPo)){
			nextTaskPo.setApprover(nextApprover);
			this.update(nextTaskPo);
		}
	}
	
	@Override
	public User getUserByID(String userId){
		User  approver = (User)this.queryUnique(" from User where id=?", new Object[]{userId});
		return approver;
	}

	@Override
	public List<FlowInstancePo> getApwInstancePoList(String curApprover) {
		String hql="select fipo from FlowInstancePo fipo where fipo.approver.id=? and fipo.isValid.code=? and fipo.approveToken=? order by fipo.submitTime desc";
	     return query(hql, new Object[]{curApprover,"Y",ApwConstants.APPROVETOKEN.AVAILABLE.toString()});
	}

	@Override
	public FlowInstancePo getFlowInstancePo(String objectId,String curUserId) {
		return (FlowInstancePo) this.queryUnique("select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.approver.id=?", new Object[]{objectId,curUserId});
	}

	@Override
	public List<UserOrgPosition> getUserByPosition_(String positionId,String pathCondition)
	{
	     String hql = "select uop from UserOrgPosition uop " +
	     						" where uop.orgPositon.position.id=? " +
	     						" and uop.user.deleteStatus.id=? "+
	     						" and uop.org.path in "+pathCondition;
	     return this.query(hql, new Object[]{positionId,dicUtil.getStatusNormal().getId()});
    }

	@Override
	public List<UserOrgPosition> getUserOrgPosition(String userId) {
		if(DataUtil.isNotNull(userId)){
			String hql = "select uop from UserOrgPosition uop " +
					" where uop.user.id=? "+
					" and uop.user.deleteStatus.id=? ";
			return this.query(hql, new Object[]{userId,dicUtil.getStatusNormal().getId()});
		}else{
			return new ArrayList<UserOrgPosition>();
		}
	}
	
	@Override
	public List<UserRole> getUserByRole(String roleId,String pathCondition)
	{
		String hql = "select ur from UserRole ur where 1=1 and ur.role.id=? and ur.user.deleteStatus.id=?";
		if(DataUtil.isNotNull(roleId)){
			return this.query(hql, new Object[]{roleId,dicUtil.getStatusNormal().getId()});
		}else{
			return new ArrayList<UserRole>();
		}
	}
	
	@Override
	public Org getUserOrg(User initiator) {
	     String hql = "select uop.org from UserOrgPosition uop where uop.user.id=? and uop.mainPos.code=?";
	     return (Org)this.queryUnique(hql, new Object[] { initiator.getId(), "Y"});
	}

	@Override
	public FlowInstancePo getCurTaskPoByToken(String objectId) {
		return (FlowInstancePo) this.queryUnique("select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.approveToken=?", new Object[]{objectId,ApwConstants.APPROVETOKEN.AVAILABLE.toString()});
	}

	@Override
	public FlowInstancePo getCurApproveTaskPo(String objectId, String approverId) {
		return (FlowInstancePo) this.queryUnique("select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.approver.id=? and fipo.approveToken=?", new Object[]{objectId,approverId,ApwConstants.APPROVETOKEN.AVAILABLE.toString()});
	}
	
	@Override
	public FlowInstancePo getCurApproveTaskPo_(String objectId, String approverId) {
		return (FlowInstancePo) this.queryUnique("select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.approver.id=? ", new Object[]{objectId,approverId});
	}

	@Override
	public FlowInstancePo getApproveTaskBySeq(String objectId, int taskSeq) {
		return (FlowInstancePo) this.queryUnique("select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.approveSeq=? ", new Object[]{objectId,taskSeq});
	}
	
	@Override
	public FlowInstancePo getNextTaskPo(FlowInstancePo curTaskPo) {
		return (FlowInstancePo) this.queryUnique("select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.approveSeq=?", new Object[]{curTaskPo.getObjectId(),curTaskPo.getApproveSeq()+1});
	}
	
	@Override
	public FlowInstancePo getLastTaskPo(FlowInstancePo nextTaskPo) {
		return (FlowInstancePo) this.queryUnique("select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.approveSeq=?", new Object[]{nextTaskPo.getObjectId(),nextTaskPo.getApproveSeq()-1});
	}

	@Override
	public List<FlowInstancePo> getCurApproverTaskPo(String objectIds,String curApproverId) {
		String hql = "select fipo from FlowInstancePo fipo where  fipo.objectId in "+objectIds+" and fipo.approver.id=? and fipo.approveToken=?";
		return query(hql,new Object[]{curApproverId,ApwConstants.APPROVETOKEN.AVAILABLE.toString()});
	}

	@Override
	public FlowConfigPo getNextConfigPo(FlowConfigPo curConfigPo) {
		List values = new ArrayList();
		values.add(curConfigPo.getFlowDefinePo().getId());
		values.add(curConfigPo.getTaskSeq()+1);
		String hql="from FlowConfigPo  where flowDefinePo.id=? and taskSeq=?";
		return DataUtil.isNotNull(this.queryUnique(hql, values.toArray()))?(FlowConfigPo) this.queryUnique(hql, values.toArray()):null;
	}

	@Override
	public FlowInstancePo getInstancePoByObjKey(String objectId,String processKey,String taskId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public FlowDefinePo getFlowDefinePoByKey(String processKey) {
		List values = new ArrayList();
		String hql="from FlowDefinePo fdpo where fdpo.processKey=? and fdpo.status.id=?";
		values.add(processKey);
		values.add(this.dicUtil.getStatusEnable());
		return (FlowDefinePo)this.queryUnique(hql, values);
	}

	@Override
    public List<FlowInstancePo> getObjInstanceListByKey(String objectId,String processKey)
    {
		List values = new ArrayList();
		if(DataUtil.isNotNull(objectId)&&DataUtil.isNotNull(processKey)){
			
			String hql = "select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.processKey=?";
			values.add(objectId);
			values.add(processKey);
			return query(hql,values.toArray());
		}else{
			return new ArrayList<FlowInstancePo>();
		}
	}

	@Override
    public FlowInstancePo getTaskPoByTaskSeq(String objectId, int taskSeq)
    {
		List values = new ArrayList();
		if(DataUtil.isNotNull(objectId) && DataUtil.isNotNull(taskSeq)){
			String hql = "select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.approveSeq=?";
			values.add(objectId);
			values.add(taskSeq);
			return (FlowInstancePo)this.queryUnique(hql, values.toArray());
		}else{
			return null;
		}
    }
	
	/**
	 * 当前流程审批历史查询
	 * @param objectId		业务主键
	 * @param pageNo			当前页
	 * @param pageSize		分页大小
	 * @return							分页审批历史信息
	 */
	@Override
	public Page getCurProcessHistory(String objectId,int pageNo,int pageSize) {
		List<String> values = new ArrayList<String>();//审批历史查询条件
		StringBuffer hql=new StringBuffer("select fhpo from FlowHistoryPo fhpo where  1=1 ");
		if(DataUtil.isNotNull(objectId)){
			hql.append(" and fhpo.objectId=? and fhpo.isValid.code='Y' order by fhpo.approveTime desc,fhpo.approveSeq asc");
			values.add(objectId);
			return pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
		}
		return new Page();
	}
	

	@Override
	public List<FlowHistoryPo> getFlowHistoryByProcessKey(String processKey,String userId) {
		List<String> values = new ArrayList<String>();//审批历史查询条件
		StringBuffer hql=new StringBuffer("select  distinct fhpo from FlowHistoryPo fhpo where  1=1 ");
		if(DataUtil.isNotNull(processKey)){
			hql.append(" and fhpo.processKey=? and approver.id = ? ");
			values.add(processKey);
			values.add(userId);
			return this.query(hql.toString(), values.toArray());
		}
		return new ArrayList<FlowHistoryPo>();
	}
	
	/**
	 * 当前流程审批历史查询
	 * @param objectId		业务主键
	 * @return							审批历史信息
	 */
	public List<FlowHistoryPo> getCurProcessHistory(String objectId){
		List<String> values = new ArrayList<String>();//审批历史查询条件
		StringBuffer hql=new StringBuffer("select fhpo from FlowHistoryPo fhpo where  1=1 ");
		if(DataUtil.isNotNull(objectId)){
			hql.append(" and fhpo.objectId=? and fhpo.isValid.code='Y' order by fhpo.approveTime desc,fhpo.approveSeq asc");
			values.add(objectId);
			return this.query(hql.toString(),values.toArray());
		}
		
		return  new ArrayList<FlowHistoryPo>();
	}

	@Override
	public void deleteAllAction(String objectId, String processKey) {
		if(DataUtil.isNotNull(objectId) && DataUtil.isNotNull(processKey)){
			this.executeHql("delete FlowInstancePo where  objectId=?  and processKey=?", new Object[]{objectId,processKey});
		}
	}

	@Override
	public FlowInstancePo getNextTaskByCurTask(FlowInstancePo curTaskPo) {
		if(DataUtil.isNotNull(curTaskPo) && 
			 DataUtil.isNotNull(curTaskPo.getObjectId()) && 
		     DataUtil.isNotNull(curTaskPo.getProcessKey()) && 
		     DataUtil.isNotNull(curTaskPo.getApproveSeq())){
			
			List values = new ArrayList();
			StringBuffer hql = new StringBuffer("select fipo from FlowInstancePo fipo where 1=1 ") ;
			
			hql.append(" and fipo.objectId= ? ");
			values.add(curTaskPo.getObjectId());
		
			hql.append(" and fipo.processKey=? ");
			values.add(curTaskPo.getProcessKey());
		
			hql.append(" and fipo.approveSeq=? ");
			values.add(curTaskPo.getApproveSeq()+1);
			
			return (FlowInstancePo)this.queryUnique(hql.toString(), values.toArray());
		}
		return new FlowInstancePo();
	}

	@Override
	public FlowInstancePo getNextTaskByIniator(String objectId,String processKey, int approveSeq) {
		if(DataUtil.isNotNull(objectId) && 
			 DataUtil.isNotNull(processKey) && 
			 DataUtil.isNotNull(approveSeq)){
			List values = new ArrayList();
			StringBuffer hql = new StringBuffer("select fipo from FlowInstancePo fipo where 1=1 ") ;
			hql.append(" and fipo.objectId= ? ");
			hql.append(" and fipo.processKey=? ");
			hql.append(" and fipo.approveSeq=? ");
			values.add(objectId);
			values.add(processKey);
			values.add(approveSeq);
			return (FlowInstancePo)this.queryUnique(hql.toString(), values.toArray());
		}
		return new FlowInstancePo();
	}
	
	@Override
	public FlowInstancePo getCurTaskPoByKey(String objectId, String processKey) {
		List values = new ArrayList();
		if(DataUtil.isNotNull(objectId)&&DataUtil.isNotNull(processKey)){
			
			String hql = "select fipo from FlowInstancePo fipo where fipo.objectId=? and fipo.processKey=? and fipo.approveToken=?";
			values.add(objectId);
			values.add(processKey);
			values.add(ApwConstants.APPROVETOKEN.AVAILABLE.toString());
			return (FlowInstancePo)this.queryUnique(hql,values.toArray());
		}else{
			return new FlowInstancePo();
		}
	}

	@Override
	public void rollbackApproveToken(String objectId, String processKey,int approveSeq) {
		if(DataUtil.isNotNull(objectId) && DataUtil.isNotNull(processKey) && DataUtil.isNotNull(approveSeq)){
			
			List values = new ArrayList();
			StringBuffer hql = new StringBuffer("update FlowInstancePo fipo set ");
			hql.append(" fipo.approveResultDic.id=null,");
			hql.append(" fipo.suggest=null, ");
			hql.append(" fipo.approveToken='"+ApwConstants.APPROVETOKEN.AVAILABLE+"' ");
			hql.append(" where fipo.objectId=?  ");
			hql.append(" and fipo.processKey=? ");
			hql.append(" and fipo.approveSeq=? ");
			values.add(objectId);
			values.add(processKey);
			values.add(approveSeq);
			this.executeHql(hql.toString(), values.toArray());
		}
	}

	@Override
	public Org getOrgById(String orgId) {
		if(DataUtil.isNotNull(orgId)){
			return (Org)this.queryUnique("from Org where id=?", new Object[] {orgId});
		}else{
			return new Org();
		}
	}

	@Override
	public void deprecatedCurProcess(String objectId) {
		if(DataUtil.isNotNull(objectId)){
			List values = new ArrayList();
			StringBuffer hql = new StringBuffer("update FlowInstancePo fipo set ");
			hql.append(" fipo.isValid.id=?");
			hql.append(" where fipo.objectId=?  ");
			values.add(this.dicUtil.getDicInfo("Y&N", "N").getId());
			values.add(objectId);
			this.executeHql(hql.toString(), values.toArray());
		}
	}

}
