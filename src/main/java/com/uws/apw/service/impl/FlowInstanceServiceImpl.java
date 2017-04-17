package com.uws.apw.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.apw.dao.IFlowInstanceDao;
import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;
import com.uws.apw.model.FlowConfigPo;
import com.uws.apw.model.FlowDefinePo;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.model.FlowInstancePo;
import com.uws.apw.model.MulApproveResult;
import com.uws.apw.service.IFlowConfigService;
import com.uws.apw.service.IFlowDefineService;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.apw.util.ApwConstants;
import com.uws.apw.util.DateUtils;
import com.uws.apw.util.JsonUtils;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStuJobTeamSetCommonService;
import com.uws.common.service.IStudentCommonService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;
import com.uws.core.util.IdUtil;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.integrate.StudentApproveSetModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.Org;
import com.uws.user.model.User;
import com.uws.user.model.UserOrgPosition;
import com.uws.user.model.UserRole;
import com.uws.user.service.IUserService;

@Service("flowInstanceService")
@SuppressWarnings("all")
public class FlowInstanceServiceImpl implements IFlowInstanceService{
	
	private static Logger logger = new LoggerFactory(FlowInstanceServiceImpl.class);
	
	//sessionUtil工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(ApwConstants.APW_NAMESPACE);
	
	@Autowired
	private IFlowInstanceDao flowInstanceDao;
	
	@Autowired
	private IFlowDefineService flowDefineService;
	
	@Autowired
	private IFlowConfigService flowConfigService;
	
	@Autowired
	private IStuJobTeamSetCommonService stuJobTeamSetCommonService;
	/**
	 * 耦合业务系统接口【查询学生信息】
	 */
	@Autowired
	private IStudentCommonService studentCommonService;
	
	/**
	 * 耦合业务系统接口【查询基础信息】
	 */
	@Autowired
	private IBaseDataService baseDataService;
	
	@Autowired
	private IUserService userService;
	
	//数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	@Override
    public List<Approver> getNextApproverList(String objectId,
            String processKey, User curUser, boolean isAccess)
    {
		List<Approver> nextApproverList = new ArrayList<Approver>();
		if(isAccess){
			List<FlowInstancePo> fipoList = this.flowInstanceDao.getObjInstanceListByKey(objectId,processKey);
			if(DataUtil.isNotNull(fipoList) && fipoList.size()==0){//未发起流程时，选择下一节点办理人
				
				FlowConfigPo nextConfigPo = this.flowConfigService.getNextConfigPoByIniator(processKey, 1);
				nextApproverList = this.getTaskUser(nextConfigPo.getPtype(),nextConfigPo.getAgentPosition(),curUser);
			}else if(DataUtil.isNotNull(fipoList) && fipoList.size()>0){//已发起流程后，选择下一节点办理人
				FlowInstancePo fipo = this.flowInstanceDao.getCurApproveTaskPo(objectId, this.sessionUtil.getCurrentUserId());
				if(DataUtil.isNotNull(fipo)){
					int nextTaskSeq = fipo.getFlowConfigPo().getTaskSeq()+1;
					FlowInstancePo nextTaskPo_ = this.flowInstanceDao.getNextTaskByIniator(objectId,processKey,nextTaskSeq);
					Dic nextPostionType = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPostionType():null;//下一节点审批岗位类型
					String nextPositionId = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPositionId():"";//下一节点审批岗位ID
					nextApproverList = this.getTaskUser(nextPostionType,nextPositionId,curUser);
				}
			}
			return nextApproverList;
		}else{
			return nextApproverList;
		}
    }

	@Override
	public ApproveResult initProcessInstance(String objectId,String processKey, User initiator,User nextApprover,boolean isAccess) {
		ApproveResult  result = new ApproveResult();
		if(isAccess){
				FlowDefinePo flowDefinePo = this.flowDefineService.getFlowDefineByKey(processKey);
				if(DataUtil.isNotNull(flowDefinePo)){
					List<FlowConfigPo> flowConfigList = this.flowConfigService.getFlowConfigByDefine(processKey);
					for(FlowConfigPo fcpo:flowConfigList){
						FlowInstancePo fipo = this.formateFlowInstancePo(result,fcpo,objectId,initiator,nextApprover);
						this.flowInstanceDao.saveProcessInstance(fipo);
					}
					result.setResultFlag("success");
				}else{
					result.setResultFlag("null");
				}
		}else{
			result.setResultFlag("deprecated");
		}
		
		result.setProcessKey(processKey);//回显流程定义key
		return result;
	}
	
	@Override
	public ApproveResult modifyProcessInstance(String objectId,String processKey,boolean isAccess) {
		if(isAccess){
			ApproveResult  result = new ApproveResult();
			List<FlowConfigPo> flowConfigList = this.flowConfigService.getFlowConfigByDefine(processKey);
			for(FlowConfigPo fcpo:flowConfigList){
				FlowInstancePo fipo = this.updateFlowInstancePo(result,objectId,processKey,fcpo.getId());
				this.flowInstanceDao.update(fipo);
			}
			return result;
		}else{
			return new ApproveResult();
		}
	}

	@Override
	public List<FlowInstancePo> geCurProcessHistory(String objectId,boolean isAccess) {
		if(isAccess){
			return this.flowInstanceDao.geCurProcessHistory(objectId);
		}else{
			return new ArrayList<FlowInstancePo>();
		}
	}

	@Override
	public Page geUserProcessHistory(
			int pageNo,int pageSize,String objectId,String processStatus,
			String initiatorId, String approverId, String startTime,
			String endTime, boolean isAccess) {
		if(isAccess){
			return this.flowInstanceDao.geUserProcessHistory(pageNo,pageSize,objectId,processStatus,initiatorId, approverId, startTime,endTime);
		}else{
			return new Page();
		}
	}
	
	@Override
	public ApproveResult saveProcessApproveResult(FlowInstancePo fipo,String objectId,String approverId,boolean isAccess) {
		if(isAccess){
			ApproveResult result = new ApproveResult();
			FlowInstancePo curTaskPo =  this.flowInstanceDao.getCurApproveTaskPo(objectId, approverId);
			String curApproveFlag = fipo.getApproveResultDic().getCode();
			if("PASS".equals(curApproveFlag)){
				result = this.executePassAction(fipo,objectId,curTaskPo,isAccess);
			}else{
				result = executeNoPassAction(fipo,objectId,curTaskPo,isAccess);
			}
			result.setProcessKey(curTaskPo.getProcessKey());
			result.setObjectId(objectId);
			return result;
		}else{
			return new ApproveResult();
		}
	}

	/**
	 * 执行审批通过操作
	 * @param fipo						页面审批内容
	 * @param objectId			业务主键
	 * @param curTaskPo		当前流程实例
	 * @param isAccess			[true、false]
	 * @return								审批结果
	 */
	private ApproveResult executePassAction(FlowInstancePo fipo,String objectId,FlowInstancePo curTaskPo,boolean isAccess) {
		ApproveResult result = new ApproveResult();
		FlowInstancePo nextTaskPo_ =  this.flowInstanceDao.getNextTaskByCurTask(curTaskPo);
		Dic nextPostionType = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPostionType():null;//下一节点审批岗位类型
		String nextPositionId = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPositionId():"";//下一节点审批岗位ID
		List<Approver> nextApproverList = this.getTaskUser(nextPostionType,nextPositionId,curTaskPo.getApprover());
		String processStatus = fipo.getApproveResultDic().getCode();//初始当前流程审批状态
		
		if(DataUtil.isNotNull(nextTaskPo_)){//下一个节点存在时
			if(DataUtil.isNotNull(fipo) && DataUtil.isNotNull(fipo.getApproveResultDic())){//流程发起时，不做审批内容保存的操作
				result = this.saveCurApproveInfo(fipo,objectId);
			}
			//当前节点审批结果
			result.setApproveStatus(curTaskPo.getApprover().getName()+"审批_"+fipo.getApproveResultDic().getName());
			//判断当前流程审批状态是否为【审批中】
			processStatus = this.getCurProcessStatus(processStatus,nextTaskPo_);
			//接口返回下一节点办理人列表
			result.setNextApproverList(nextApproverList);
			//初始化下一节点办理人
			if(DataUtil.isNotNull(nextApproverList) && nextApproverList.size()==1){
				Approver  nextUser = nextApproverList.get(0);
				this.initNextApprover(objectId, nextUser.getUserId(), isAccess);
			}
		}else{//当前节点为末节点时，直接流程结束
			
			result = this.finishCurFlowInstance(fipo,objectId,curTaskPo,isAccess);
		}
		
		//设置流程的当前审批状态
		result.setProcessStatusCode(processStatus);
		if(DataUtil.isNotNull(nextTaskPo_)){//设置下一环节名称
			result.setNextTaskName(nextTaskPo_.getTaskName());
		}
		return result;
	}
	
	/**
	 * 结束流程的审批
	 * @param fipo
	 * @param objectId
	 * @param curTaskPo
	 * @param isAccess
	 * @return
	 */
	private ApproveResult finishCurFlowInstance(FlowInstancePo fipo,String objectId,FlowInstancePo curTaskPo,boolean isAccess) {
		ApproveResult result = new ApproveResult();
		FlowInstancePo nextTaskPo_ =  this.flowInstanceDao.getNextTaskByCurTask(curTaskPo);
		Dic nextPostionType = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPostionType():null;//下一节点审批岗位类型
		String nextPositionId = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPositionId():"";//下一节点审批岗位ID
		List<Approver> nextApproverList = this.getTaskUser(nextPostionType,nextPositionId,curTaskPo.getApprover());
		Dic approveResult = fipo.getApproveResultDic();
		String approverId = sessionUtil.getCurrentUserId();
		FlowInstancePo curApproveTaskPo = this.saveApproveAction(fipo,objectId,approverId);
		this.saveApproveHistory(curApproveTaskPo);
		if(DataUtil.isNotNull(curTaskPo)){
			User  curApprover = curTaskPo.getApprover();
			result.setApproveStatus(curApprover.getName()+"审批_"+approveResult.getName());
		}
		String processStatus = this.getCurProcessStatus(approveResult.getCode(),nextTaskPo_);
		result.setProcessStatusCode(processStatus);
		result.setNextApproverList(nextApproverList);
		
		return result;
	}

	/**
	 * 执行非审批通过操作
	 * @param fipo
	 * @param objectId
	 * @param curTaskPo
	 * @param isAccess
	 * @return
	 */
	private ApproveResult executeNoPassAction(FlowInstancePo fipo,String objectId,FlowInstancePo curTaskPo,boolean isAccess) {
		ApproveResult result = new ApproveResult();
		FlowInstancePo nextTaskPo_ =  this.flowInstanceDao.getNextTaskByCurTask(curTaskPo);
		Dic nextPostionType = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPostionType():null;//下一节点审批岗位类型
		String nextPositionId = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPositionId():"";//下一节点审批岗位ID
		List<Approver> nextApproverList = this.getTaskUser(nextPostionType,nextPositionId,curTaskPo.getApprover());
		Dic approveResult = fipo.getApproveResultDic();
		String approverId = sessionUtil.getCurrentUserId();
		FlowInstancePo curApproveTaskPo = this.saveApproveAction(fipo,objectId,approverId);//先保存当前操作
		this.saveApproveHistory(curApproveTaskPo);
		if(DataUtil.isNotNull(curTaskPo)){
			User  curApprover = curTaskPo.getApprover();
			result.setApproveStatus(curApprover.getName()+"审批_"+approveResult.getName());
		}
		String processStatus = this.getCurProcessStatus(approveResult.getCode(),nextTaskPo_);
		result.setProcessStatusCode(processStatus);
//		result.setNextApproverList(nextApproverList);
		
		return result;
	}

	/**
	 * 判断当前流程的状态
	 * @param code								当前预置的流程状态
	 * @param nextConfigPo				下个审批环节对象
	 * @return											确定后的流程状态
	 */
	private String getCurProcessStatus(String code,FlowInstancePo nextTaskPo) {
		String returnValue=code;
		if(ApwConstants.PROCESS_STATUS.PASS.toString().equals(code)){//审批通过
			if(DataUtil.isNotNull(nextTaskPo)){//不是最后一个审批节点
				returnValue=ApwConstants.PROCESS_STATUS.APPROVEING.toString();
			}
		}
		return returnValue;
	}

	@Override
	public MulApproveResult saveMulApproveResult(String approveKey,FlowInstancePo fipo,List<String> objectIds,String approverId,boolean isAccess) {
		MulApproveResult mulResult = new MulApproveResult();
		if(isAccess){
			List<ApproveResult> mulResults = new ArrayList<ApproveResult>();
			for(String objectId:objectIds){
				ApproveResult result = this.saveProcessApproveResult(fipo, objectId, approverId,isAccess);
				result.setApproveKey(approveKey);
				mulResults.add(result);
			}
			mulResult.setResults(mulResults);
		}
		
		return mulResult;
	}
	
	/**
	 * 保存本次流程审批历史
	 * @param curTaskPo		当前流程实例
	 */
	private void saveApproveHistory(FlowInstancePo curTaskPo) {
		FlowHistoryPo  historyPo = new FlowHistoryPo();
		try {
			BeanUtils.copyProperties(historyPo, curTaskPo);
			historyPo.setId(IdUtil.getUUIDHEXStr());
			historyPo.setIsValid(this.dicUtil.getDicInfo("Y&N", "Y"));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		this.flowInstanceDao.save(historyPo);
	}

	@Override
	public ApproveResult initNextApprover(String objectId,String userId,boolean isAccess) {
		ApproveResult result = new ApproveResult();
		if(isAccess){
			User  nextApprover = this.flowInstanceDao.getUserByID(userId);
			this.flowInstanceDao.initNextApprover(objectId,nextApprover);
		}
		result.setResultFlag("success");
		return result;
	}

	/**
	 * 保存当前的审批操作
	 * @param fipo
	 * @param objectId
	 */
	private ApproveResult saveCurApproveInfo(FlowInstancePo fipo,String objectId) {
		ApproveResult result = new ApproveResult();
		String approverId = sessionUtil.getCurrentUserId();
		FlowInstancePo curTaskPo =  this.flowInstanceDao.getCurApproveTaskPo(objectId,approverId);
//		FlowConfigPo nextConfigPo = this.flowInstanceDao.getNextConfigPo(curTaskPo.getFlowConfigPo());
		FlowInstancePo curApproveTaskPo = this.saveApproveAction(fipo,objectId,approverId);
		this.saveApproveHistory(curApproveTaskPo);
		if(DataUtil.isNotNull(curTaskPo)){
			Dic approveResult = curTaskPo.getApproveResultDic();
			User  curApprover = curTaskPo.getApprover();
			result.setApproveStatus(curApprover.getName()+"审批_"+approveResult.getName());
		}
		
		return  result;
	}

	@Override
	public List<String> checkTaskApprover(List<String> objectIds,String curApproverId,boolean isAccess) {
		if(isAccess){
			List<String> illegalProcessList = new ArrayList<String>();
			String condition = this.getCondition(objectIds);
			List<FlowInstancePo> fipoList = this.flowInstanceDao.getCurApproverTaskPo(condition,curApproverId);
			for(FlowInstancePo curTaskPo:fipoList){
				FlowInstancePo nextTaskPo_ = this.flowInstanceDao.getNextTaskByCurTask(curTaskPo);
				Dic nextPostionType = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPostionType():null;//下一节点审批岗位类型
				String nextPositionId = DataUtil.isNotNull(nextTaskPo_)?nextTaskPo_.getTaskPositionId():"";//下一节点审批岗位ID
				List<Approver> nextTaskApprovers = this.getTaskUser(nextPostionType,nextPositionId,curTaskPo.getApprover());
				if(nextTaskApprovers!=null && nextTaskApprovers.size()>1){
					illegalProcessList.add(curTaskPo.getObjectId());
				}
			}
			return illegalProcessList;
		}else{
			return new ArrayList<String>();
		}
	}

	@Override
	public boolean isValidProcess(String processKey) {
		FlowDefinePo fdpo = this.flowInstanceDao.getFlowDefinePoByKey(processKey);
		String validStatus = this.dicUtil.getStatusEnable().getCode();
		if(DataUtil.isNotNull(fdpo) && DataUtil.isNotNull(fdpo.getStatus()) && fdpo.getStatus().getCode().equals(validStatus)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 封装流程实例信息
	 * @param result						审批结果
	 * @param fcpo						流程实例
	 * @param objectId				业务主键
	 * @param nextApprover	下一节点办理人
	 * @param initiatorId			发起人
	 * @return									内容封装完成后的流程实例
	 */
	private FlowInstancePo formateFlowInstancePo(ApproveResult  result,FlowConfigPo fcpo,String objectId, User initiator,User nextApprover) {
		FlowInstancePo fipo  = new FlowInstancePo();
		fipo.setObjectId(objectId);//设置业务主键
		fipo.setFlowDefinePo(fcpo.getFlowDefinePo());//设置流程定义信息
		fipo.setProcessKey(fcpo.getFlowDefinePo().getProcessKey());
		fipo.setFlowConfigPo(fcpo);//设置审批环节信息
		fipo.setIsValid(this.dicUtil.getDicInfo("Y&N", "Y"));//设置当前流程是否有效【Y：有效、N：无效】
		if(fcpo.getTaskSeq()==1){
			fipo.setApprover(nextApprover);//设置下一节点办理人
			List<Approver> curTaskApprovers = this.getUserByTaskConfig(fcpo.getPtype(),fcpo.getAgentPosition(),initiator);
			if(curTaskApprovers!=null && curTaskApprovers.size()>0){
				result.setNextApproverList(curTaskApprovers);
				result.setApproveResultCode(ApwConstants.PROCESS_STATUS.CURRENT_APPROVE.toString());
			}else{//当无节点办理人时，待办人列表为空
				result.setNextApproverList(new ArrayList<Approver>());
			}
			fipo.setApproveToken(ApwConstants.APPROVETOKEN.AVAILABLE.toString());
		}
		fipo.setApproveSeq(fcpo.getTaskSeq());//设置审批顺序
		fipo.setTaskName(fcpo.getTaskName());//审批环节名称
		fipo.setInitiator(initiator);//设置发起人信息
		fipo.setSubmitTime(DateUtils.toTime(DateUtils.getCurTime()));//设置流程提交时间
		fipo.setTaskPostionType(fcpo.getPtype());//审批岗位类型
		fipo.setTaskPositionId(fcpo.getAgentPosition());//审批岗位ID
		fipo.setTaskPositionName(fcpo.getAgentPosname());//审批岗位名称
		return fipo;
	}
	
	/**
	 * 封装待修改的流程实例信息
	 * @param result					审批结果
	 * @param objectId			业务主键
	 * @param processKey		流程定义key
	 * @param taskSeqId			审批环节id
	 * @return
	 */
	private FlowInstancePo updateFlowInstancePo(ApproveResult  result,String objectId,String processKey,String taskSeqId) {
		FlowInstancePo fipo  = this.flowInstanceDao.getInstancePoByObjKey(objectId,processKey,taskSeqId);
		FlowConfigPo fcpo = DataUtil.isNotNull(fipo)?fipo.getFlowConfigPo():new FlowConfigPo();
		this.clearInstancePo(fipo,fcpo,result);
		return fipo;
	}
	
	/**
	 * 清空当前节点审批实例信息
	 * @param fipo		当前节点流程审批实例
	 */
	private void clearInstancePo(FlowInstancePo fipo,FlowConfigPo fcpo,ApproveResult  result) {
		fipo.setApproveResult(null);//清除审批结果
		fipo.setSuggest(null);//清除意见
		fipo.setIsValid(this.dicUtil.getDicInfo("Y&N", "Y"));//设置当前流程是否有效【Y：有效、N：无效】
		fipo.setApproveToken(null);
		if(fcpo.getTaskSeq()==1){//初始化令牌
			List<Approver> curTaskApprovers = this.getUserByTaskConfig(fcpo.getPtype(),fcpo.getAgentPosition(),fipo.getInitiator());
			if(curTaskApprovers!=null && curTaskApprovers.size()>0){
				result.setNextApproverList(curTaskApprovers);
				if(curTaskApprovers.size()==1){//第一级审批岗位对应人员为一个时，直接入库保存
					fipo.setApprover(this.userService.getUserById(curTaskApprovers.get(0).getUserId()));
				}
			}
			fipo.setApproveToken(ApwConstants.APPROVETOKEN.AVAILABLE.toString());
		}
		fipo.setUpdateTime(DateUtils.toTime(DateUtil.getCurTime()));//更新流程发起时间
		fipo.setApproveTime(null);
	}

	/**
	 * 根据岗位类型获取岗位对应的用户
	 * @param taskConfigId		节点配置[角色/岗位]ID
	 * @param curUser					当前用户id
	 * @return									岗位对应的用户列表
	 */
	private List<Approver> getUserByTaskConfig(Dic positionType, String taskConfigId,User curUser) {
		List<Approver>   positionUsers = new ArrayList<Approver>();
		if(positionType.getCode().equals("POSITION_SYS")){
			
			positionUsers = this.findUserBySysPosition(taskConfigId,curUser);
		}else if(positionType.getCode().equals("ROLE_SYS")){
			
			positionUsers = this.findUserBySysRole(taskConfigId,curUser);
		}else if(positionType.getCode().equals("POSITION_MONITOR")){
			
			StudentApproveSetModel  monitor = this.studentCommonService.getClassMonitor(curUser);
			String positionName = this.dicUtil.getDicInfo("POSITION_TYPE", "POSITION_MONITOR").getName();
			if(DataUtil.isNotNull(monitor.getId())){
				positionUsers = this.formatePositionUsers(monitor,positionUsers,positionName);
                }
		}else if(positionType.getCode().equals("POSITION_INSTRUCTOR")){
			
			StudentApproveSetModel  monitor = this.studentCommonService.getClassMonitor(curUser);
			String classId = (monitor!=null && monitor.getClassId()!=null)?monitor.getClassId().getId():"";
			BaseTeacherModel  teacher = this.stuJobTeamSetCommonService.queryTeacherCounsellorByClassId(classId);
			String positionName = this.dicUtil.getDicInfo("POSITION_TYPE", "POSITION_INSTRUCTOR").getName();
			if(DataUtil.isNotNull(teacher)){
				positionUsers = this.formateInstructorUsers(teacher,positionUsers,positionName);
			}
		}
		
		return positionUsers;
	}
	
	/**
	 * 封装教学辅导员信息
	 * @param teacher	教师实体类
	 * @param positionUsers		审批人列表
	 * @param positionName	岗位名称
	 * @return
	 */
	private List<Approver> formateInstructorUsers(BaseTeacherModel teacher,
			List<Approver> positionUsers, String positionName) {
		Approver approver = new Approver();
		approver.setUserId(teacher.getId());
		approver.setUserName(teacher.getName());
		approver.setPositionName(positionName);
		approver.setOrgName(teacher.getOrg().getName());
		positionUsers.add(approver);
		return positionUsers;
	}

	/**
	 * 封装审批人列表信息
	 * @param monitor			 用户信息
	 * @return positionUsers 审批人列表
	 * @param positionName 岗位名称
	 * @return
	 */
	private List<Approver> formatePositionUsers(StudentApproveSetModel monitor,List<Approver> positionUsers,String positionName) {
		Approver approver = new Approver();
		approver.setUserId(monitor.getStudentId().getId());
		approver.setUserName(monitor.getStudentId().getName());
		approver.setPositionName(positionName);
		approver.setOrgName(monitor.getClassId().getClassName());
		positionUsers.add(approver);
		return positionUsers;
	}

	/**
	 * 根据【审批节点、配置的角色】获取审批人信息
	 * @param taskConfigId		节点配置[角色]ID
	 * @return
	 */
	private List<Approver> findUserBySysRole(String roleId,User initiator) {
		List<Approver> positionUsers = new ArrayList<Approver>();
		String pathCondition = this.getOrgPath(initiator);
		List<UserRole> sysRoleUser = this.flowInstanceDao.getUserByRole(roleId,pathCondition);
		for(UserRole userRole:sysRoleUser){
			Approver approver = new Approver();
			approver.setUserId(userRole.getUser().getId());
			approver.setUserName(userRole.getUser().getName());
			approver.setPositionName(userRole.getRole().getName());
			List<UserOrgPosition> uopList = this.flowInstanceDao.getUserOrgPosition(userRole.getUser().getId());
			for(UserOrgPosition uop:uopList){
				if(DataUtil.isNotNull(uop) && DataUtil.isNotNull(uop.getOrg()) && DataUtil.isNotNull(uop.getOrg().getName())){
					approver.setOrgName(uop.getOrg().getName());
					break;
				}
			}
			positionUsers.add(approver);
		}
		return positionUsers;
	}

	/**
	 * 根据【系统岗位+发起人所在的组织机构分支】获取岗位人员信息
	 * @param taskConfigId	节点配置[岗位]ID
	 * @param curUser
	 * @return
	 */
	private List<Approver> findUserBySysPosition(String positionId,User curUser) {
		List<Approver> positionUsers = new ArrayList<Approver>();
		String pathCondition = this.getOrgPath(curUser);
		List<UserOrgPosition> sysPositionUser = new ArrayList<UserOrgPosition>();
		if(DataUtil.isNotNull(pathCondition)){
			sysPositionUser = this.flowInstanceDao.getUserByPosition_(positionId,pathCondition);
		}
		for(UserOrgPosition userOrg:sysPositionUser){
			Approver approver = new Approver();
			approver.setUserId(userOrg.getUser().getId());
			approver.setUserName(userOrg.getUser().getName());
			approver.setPositionName(userOrg.getOrgPositon().getPosition().getName());
			approver.setOrgName(userOrg.getOrg().getName());
			positionUsers.add(approver);
		}
		return positionUsers;
	}

	/**
	 * 获取组织机构路径
	 * @param initiator	发起人
	 * @return						格式化后的组织机构
	 */
	private String getOrgPath(User initiator) {
		String pathCondition=null;
		Org initiatorOrg = this.getOrgByUserType(initiator);
		if(DataUtil.isNotNull(initiatorOrg)&&DataUtil.isNotNull(initiatorOrg.getPath())){
			pathCondition = this.getPathCondition(initiatorOrg.getPath());
		}
		return pathCondition;
	}

	/**
	 * 根据用户类型获取用户所在组织结构
	 * @param initiator	发起人
	 * @return						组织机构信息
	 */
	private Org getOrgByUserType(User initiator) {
		Org initiatorOrg = new Org();
		String userId = (DataUtil.isNotNull(initiator))?initiator.getId():"";
		StudentInfoModel stuInfo = this.studentCommonService.queryStudentById(userId);
		BaseTeacherModel teacherInfo = this.baseDataService.findTeacherById(userId);
		if(DataUtil.isNotNull(stuInfo)){//获取学生所在学院的组织机构信息
			initiatorOrg = this.flowInstanceDao.getOrgById(stuInfo.getCollege().getId());
		}else if(DataUtil.isNotNull(teacherInfo)){//获取教师所在的组织机构信息
			initiatorOrg = teacherInfo.getOrg();
		}else{//获取非学生所在学院的组织机构
			initiatorOrg = this.flowInstanceDao.getUserOrg(initiator);
		}
		return initiatorOrg;
	}

	/**
	 * 递归获取组织机构分级查询条件
	 * @param path	发起人所在的组织机构分支
	 * @return	组织机构查询路径
	 */
	private static String getPathCondition(String path) {
		StringBuffer sbff = new StringBuffer();
		String pathArray[]=path.replace("|", ",").split(",");
		StringBuffer tempSbff=new StringBuffer();
		sbff.append("(");
		for(int i=0;i<pathArray.length;i++){
			String param = pathArray[i];
			if(i==pathArray.length-1){
				sbff.append("'").append(tempSbff).append(param).append("'");
			}else{
				sbff.append("'").append(tempSbff).append(param).append("',");
				tempSbff.append(param).append("|");
			}
		}
		sbff.append(")");
		return sbff.toString();
	}
	
	/**
	 * 保存当前审批环节的审批操作
	 * @param fipo								页面审批内容
	 * @param objectId					业务主键
	 * @param approverId				审批人Id
	 * @return										审批后的流程实例
	 */
	private FlowInstancePo saveApproveAction(FlowInstancePo fipo,String objectId,String approverId) {
		FlowInstancePo curTaskPo =  this.flowInstanceDao.getCurApproveTaskPo(objectId,approverId);
		FlowInstancePo nextTaskPo_ = this.flowInstanceDao.getNextTaskByCurTask(curTaskPo);
		String curStatus = this.getCurProcessStatus(fipo.getApproveResultDic().getCode(),nextTaskPo_);
		curTaskPo.setApproveResultDic(fipo.getApproveResultDic());
		curTaskPo.setSuggest(fipo.getSuggest());
		curTaskPo.setApproveTime(DateUtils.toTime(DateUtil.getCurTime()));
		this.flowInstanceDao.saveProcessApprove(curTaskPo);
		this.approveTokenHandler(curStatus,curTaskPo);
		return curTaskPo;
	}
	
	/**
	 * 审批令牌处理
	 * @param curStatus			流程当前审批状态
	 * @param curTaskPo		当前流程实例
	 */
	private void approveTokenHandler(String curStatus,FlowInstancePo curTaskPo) {
		
		if(curStatus.equals(ApwConstants.PROCESS_STATUS.APPROVEING.toString())){//审批中
			
			this.nextTaskHandler(curTaskPo);//把令牌交个下一个环节
		}else if(curStatus.equals(ApwConstants.PROCESS_STATUS.NOT_PASS.toString())||
				curStatus.equals(ApwConstants.PROCESS_STATUS.PASS.toString())){//仅释放令牌

			this.releaseTokenOnly(curTaskPo);
		}else if(curStatus.equals(ApwConstants.PROCESS_STATUS.REJECT.toString())){//驳回操作
			
			this.rollBackAllAction_1(curTaskPo);
		}
	}
	
	/**
	 * 回滚所有审批操作【物理删除】
	 * @param curTaskPo	当前流程实例
	 */
	private void rollBackAllAction_1(FlowInstancePo curTaskPo) {
		if(DataUtil.isNotNull(curTaskPo)){
			String objectId = curTaskPo.getObjectId();
			String processKey = curTaskPo.getProcessKey();
			this.flowInstanceDao.deleteAllAction(objectId,processKey);
		}
	}

	/**
	 * 将当前环节【审批令牌】交于下一环节
	 * @param curTaskPo
	 * @return
	 */
	private void nextTaskHandler(FlowInstancePo curTaskPo) {
		this.releaseTokenOnly(curTaskPo);
		FlowInstancePo nextTaskPo = this.flowInstanceDao.getNextTaskPo(curTaskPo);
		this.setNextToken(nextTaskPo); 
	}

	/**
	 * 获取下一环节的待办人列表
	 * @param positionType		审批岗位类型
	 * @param positionId			岗位ID【用户岗位】
	 * @param curUser					发起人
	 * @return									下一节点办理人列表
	 */
	public List<Approver> getTaskUser(Dic positionType,String positionId,User curUser){
		if(DataUtil.isNotNull(positionType) && DataUtil.isNotNull(positionId) && DataUtil.isNotNull(curUser)){
			return this.getUserByTaskConfig(positionType,positionId,curUser);
		}else{
			return new ArrayList<Approver>();
		}
	}
	
	/**
	 * 为下一节点设置令牌
	 * @param nextTaskPo
	 */
	private void setNextToken(FlowInstancePo nextTaskPo) {
		nextTaskPo.setApproveToken(ApwConstants.APPROVETOKEN.AVAILABLE.toString());
		this.flowInstanceDao.update(nextTaskPo);
	}
	
	/**
	 * 仅释放当前审批节点令牌
	 * @param curTaskPo
	 */
	private void releaseTokenOnly(FlowInstancePo curTaskPo) {
		curTaskPo.setApproveToken(ApwConstants.APPROVETOKEN.INAVAILABLE.toString());
		this.flowInstanceDao.update(curTaskPo);
	}

	/**
	 * 当前审批节点释放令牌、流程失效
	 * @param curTaskPo
	 */
	private void releaseToken(FlowInstancePo curTaskPo) {
		curTaskPo.setApproveToken(ApwConstants.APPROVETOKEN.INAVAILABLE.toString());
		curTaskPo.setIsValid(this.dicUtil.getDicInfo("Y&N", "N"));//流程实例失效
		this.flowInstanceDao.update(curTaskPo);
	}

	/**
	 * 获取查询条件
	 * @param objectIds
	 * @return
	 */
	private String getCondition(List<String> objectIds) {
		StringBuffer sbff = new StringBuffer();
		sbff.append("(");
		for(int i=0;i<objectIds.size();i++){
			if(i==objectIds.size()-1){
				sbff.append("'"+objectIds.get(i)+"'");
			}else{
				sbff.append("'"+objectIds.get(i)+"',");
			}
		}
		sbff.append(")");
		return sbff.toString();
	}

	@Override
	public List<FlowInstancePo> getApwInstancePoList(String curApprover) {
		return this.flowInstanceDao.getApwInstancePoList(curApprover);
	}

	@Override
	public FlowInstancePo getFlowInstancePo(String objectId,String curUserId) {
		return this.flowInstanceDao.getFlowInstancePo(objectId,curUserId);
	}

	@Override
    public boolean isFinalTask_(String objectId,String curUserId)
    {
		FlowInstancePo curNextApproverPo = this.flowInstanceDao.getCurTaskPoByToken(objectId);
		FlowInstancePo curApproveTask = null;
		if(DataUtil.isNotNull(curNextApproverPo))
			return false;
		return true;
    }
	
	@Override
	public boolean isFinalTask(String objectId,String approverId)
	{
		FlowInstancePo fipo = this.flowInstanceDao.getCurApproveTaskPo(objectId,approverId);
		if(DataUtil.isNotNull(fipo)){
			
			FlowInstancePo nextFipo = 
					this.flowInstanceDao.getTaskPoByTaskSeq(objectId,fipo.getFlowConfigPo().getTaskSeq()+1);
			if(!DataUtil.isNotNull(nextFipo)){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}

	@Override
	public boolean isAccessProcess(String processKey) {
		return this.flowDefineService.isAccessProcess(processKey);
	}

	@Override
	public Page getCurProcessHistory(String objectId,int pageNo,int pageSize,boolean isAccess) {
		if(isAccess){
			return this.flowInstanceDao.getCurProcessHistory(objectId,pageNo,pageSize);
		}else{
			return new Page();
		}
	}
	
	public List<FlowHistoryPo> getCurProcessHistory(String objectId, boolean isAccess){
		if(isAccess){
			return this.flowInstanceDao.getCurProcessHistory(objectId);
		}else{
			return new ArrayList<FlowHistoryPo>();
		}
	}

	@Override
	public void rollback2LastStep(String objectId, String processKey) {
		FlowInstancePo  fipo = this.flowInstanceDao.getCurTaskPoByKey(objectId, processKey);
		if(DataUtil.isNotNull(fipo) && DataUtil.isNotNull(fipo.getId())){
			//收回令牌
			fipo.setApproveToken(null);
			this.flowInstanceDao.update(fipo);
			
			//获取上一节点
			FlowInstancePo  lastTaskPo = this.flowInstanceDao.getLastTaskPo(fipo); 
			this.flowInstanceDao.rollbackApproveToken(objectId,processKey,lastTaskPo.getApproveSeq());
		}
	}

	@Override
	public void deprecatedCurProcess(String objectId, boolean isAccess) {
		if(DataUtil.isNotNull(objectId) && isAccess){
			this.flowInstanceDao.deprecatedCurProcess(objectId);
		}
	}

	@Override
	public List<ApproveResult> getFormatedResult(String mulResults,boolean isAccess) {
		List<ApproveResult> resultList = new ArrayList<ApproveResult>();
		if(DataUtil.isNotNull(mulResults)){
			JSONArray  jsonArray = JsonUtils.string2JsonArray(mulResults);
			for(Object obj:jsonArray){
				JSONObject jsonObject = JsonUtils.getJsonObject(obj);
				ApproveResult result = JsonUtils.jsonObj2ApproveResult(jsonObject, ApproveResult.class);
				resultList.add(result);
			}
		}
		return resultList;
	}

	@Override
	public String[] getObjectIdByProcessKey(String processKey,String userId) {
		List<FlowHistoryPo> historyList = this.flowInstanceDao.getFlowHistoryByProcessKey(processKey,userId) ;
		int len = null == historyList ? 0 : historyList.size();
		if(len > 0)
		{
			String[] objectIds = new String[len];
			for(int i=0;i<len;i++)
				objectIds[i]= historyList.get(i).getObjectId();
			return objectIds;
		}
		return  new String[]{""};
	}
}
