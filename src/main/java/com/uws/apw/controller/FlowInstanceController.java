package com.uws.apw.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;
import com.uws.apw.model.FlowHistoryPo;
import com.uws.apw.model.FlowInstancePo;
import com.uws.apw.model.MulApproveResult;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.apw.util.ApwConstants;
import com.uws.apw.util.JsonUtils;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.IdUtil;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;
import com.uws.user.service.IUserService;
import com.uws.util.ProjectConstants;

/**
 * 流程实例审批历史
 */
@Controller
@SuppressWarnings("all")
public class FlowInstanceController extends BaseController{
	
	@Autowired
	private IFlowInstanceService flowInstanceService;
	
	@Autowired
	private IUserService userService;
	
	//数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	//sessionUtil工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(ApwConstants.APW_NAMESPACE);
	
	@RequestMapping(value = {ApwConstants.APW_APPROVE_PREFIX+"/opt-query/selectNextApprover" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String selectNextApprover(ModelMap model,HttpServletRequest request,String objectId,String processKey){
		ApproveResult result = new ApproveResult();
		if(ApwConstants.ISACCESS){
			User curUser = new User(this.sessionUtil.getCurrentUserId());
			List<Approver> approverList = 
					this.flowInstanceService.getNextApproverList(objectId, processKey, curUser, ApwConstants.ISACCESS);
			result.setNextApproverList(approverList);
			result.setResultFlag("success");
		}else{
			result.setResultFlag("deprecated");
		}

        JSONObject json=JsonUtils.getJsonObject(result);
	    return JsonUtils.jsonObject2Json(json);
	}
	
	@RequestMapping(value = {ApwConstants.APW_APPROVE_PREFIX+"/opt-query/isFinalTask" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String isFinalTask(ModelMap model,HttpServletRequest request,String objectId){
		ApproveResult result = new ApproveResult();
		if(ApwConstants.ISACCESS){
			boolean isFinalTask = this.flowInstanceService.isFinalTask(objectId,this.sessionUtil.getCurrentUserId());
			if(isFinalTask){
				result.setResultFlag("final");
			}else{
				result.setResultFlag("approving");
			}
		}else{
			result.setResultFlag("deprecated");
		}
		
		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	
	@RequestMapping(value = {ApwConstants.APW_APPROVE_PREFIX+"/opt-query/isAccessProcess" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String isAccessProcess(ModelMap model,HttpServletRequest request,String processKey){
		ApproveResult result = new ApproveResult();
		if(ApwConstants.ISACCESS){
			boolean isAccess = this.flowInstanceService.isAccessProcess(processKey);
			if(isAccess){
				result.setResultFlag("YES");
			}else{
				result.setResultFlag("NO");
			}
		}else{
			result.setResultFlag("deprecated");
		}
		
		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	
	@RequestMapping({"/apw/history/opt-query/getProcessHistory"})
	public String getProcessHistory(ModelMap model,HttpServletRequest request,String processStatus,String initiatorId,String startTime,String endTime){
       //获取流程状态
		List<Dic>processStatusList=dicUtil.getDicInfoList("ROD_APPROVE_STATUS");
		model.addAttribute("processStatusList", processStatusList);
		model.addAttribute("processStatus", processStatus);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		if(null!=initiatorId){
			User user=this.userService.getUserById(initiatorId);
			if(null!=user){
				model.addAttribute("userName", user.getName());
			}
		}
		
		String approverId = this.sessionUtil.getCurrentUserId();
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page =this.flowInstanceService.geUserProcessHistory(
				pageNo,Page.DEFAULT_PAGE_SIZE,"",processStatus,initiatorId,"",startTime,endTime,ApwConstants.ISACCESS);
	    model.addAttribute("page", page);
		return ApwConstants.APW_NAMESPACE+"/apwHistoryList";
	}
	
	@RequestMapping({ApwConstants.APW_NAMESPACE+"/opt-query/getInstanceList"})
	public String getInstanceList(ModelMap model,HttpServletRequest request,ApproveResult result,String mulResults){
		 this.coutJsonArray(mulResults);
	     List<FlowInstancePo> instanceList = this.flowInstanceService.getApwInstancePoList(sessionUtil.getCurrentUserId());
	     Page page = new Page();
		 page.setPageSize(Page.DEFAULT_PAGE_SIZE);
		 int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		 page.setStart((pageNo - 1) * Page.DEFAULT_PAGE_SIZE);
		 page.setTotalCount(instanceList.size());
	     page.setResult(instanceList);
	     model.addAttribute("page", page);
		 return ApwConstants.APW_NAMESPACE+"/apwInstanceList";
	}
	
	private void coutJsonArray(String mulResults) {
		List<ApproveResult> list  = this.flowInstanceService.getFormatedResult(mulResults,ApwConstants.ISACCESS);
		for(ApproveResult result:list){
			System.out.println(result.getApproveResultCode()+"~"+result.getNextTaskName()+"~"+result.getApproveStatus()+"~"+result.getProcessStatusCode());
		}
	}
	
	@RequestMapping({ApwConstants.APW_APPROVE_PREFIX+"/nsm/getInstanceNsmList"})
	public String getInstanceNsmList(ModelMap model,HttpServletRequest request,ApproveResult result,String mulResults){
		List<FlowInstancePo> instanceList = this.flowInstanceService.getApwInstancePoList(sessionUtil.getCurrentUserId());
		Page page = new Page();
		page.setPageSize(Page.DEFAULT_PAGE_SIZE);
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		page.setStart((pageNo - 1) * Page.DEFAULT_PAGE_SIZE);
		page.setTotalCount(instanceList.size());
		page.setResult(instanceList);
		model.addAttribute("page", page);
		return ApwConstants.APW_NAMESPACE+"/apwInstanceList";
	}
	
	@RequestMapping({ApwConstants.APW_APPROVE_PREFIX+"/nsm/approvers"})
	public String selectApprovers(ModelMap model,HttpServletRequest request,ApproveResult result){

		return ApwConstants.APW_NAMESPACE+"/approvers";
	}
	
	
	/**
	 * 异步加载审批历史1
	 * @param model
	 * @param request
	 * @param processKey
	 * @return
	 */
	@RequestMapping(value = {ApwConstants.APW_NAMESPACE+"/opt-query/getApproveHistory" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String getApproveHistory(ModelMap model,HttpServletRequest request){
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		return "{\"success\":\"success\",\"pageNo\":\""+pageNo+"\"}";
	}
	
	/**
	 * 异步加载审批历史2
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({ApwConstants.APW_NAMESPACE+"/nsm/loadApproveHistory"})
	public String loadApproveHistory(ModelMap model,HttpServletRequest request,HttpServletResponse response,String objectId){
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.flowInstanceService.getCurProcessHistory(objectId,pageNo,ApwConstants.PAGE_NO_POSITION,ApwConstants.ISACCESS);
	    model.addAttribute("historyPage", page);
	    model.addAttribute("objectId", objectId);
		return "/apw/instance/approveHistoryList";
	}
	
	@RequestMapping({"/apw/instance/opt-add/editProcess"})
	public String editProcess(ModelMap model,HttpServletRequest request,String objectId){
		 
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.flowInstanceService.getCurProcessHistory(objectId,pageNo,ApwConstants.PAGE_NO_POSITION,ApwConstants.ISACCESS);
	    model.addAttribute("historyPage", page);
	    model.addAttribute("objectId", objectId);
		return "/apw/instance/box";
	}
	
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/doPass"},produces={"text/plain;charset=UTF-8"})
	public String doPass(ModelMap model,HttpServletRequest request,String suggest,String objectId){
	   FlowInstancePo instancePo = new FlowInstancePo();
	   	instancePo.setSuggest(suggest);
		instancePo.setApproveResultDic(this.dicUtil.getDicInfo("ROD_APPROVE_STATUS", "PASS"));//审批通过
		boolean isAccess=ApwConstants.ISACCESS;
		ApproveResult  result = this.flowInstanceService.saveProcessApproveResult(instancePo, objectId, sessionUtil.getCurrentUserId(), isAccess);
		if(!isAccess){
			result.setResultFlag("deprecated");
		}else{
			result.setResultFlag("success");
		}
        JSONObject json=JsonUtils.getJsonObject(result);
	    return JsonUtils.jsonObject2Json(json);
	}
	
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/doNotPass"},produces={"text/plain;charset=UTF-8"})
	public String doNotPass(ModelMap model,HttpServletRequest request,String suggest,String objectId){
	    FlowInstancePo instancePo = new FlowInstancePo();
	    instancePo.setSuggest(suggest);
		instancePo.setApproveResultDic(this.dicUtil.getDicInfo("ROD_APPROVE_STATUS", "NOT_PASS"));//不通过
		boolean isAccess=ApwConstants.ISACCESS;
		ApproveResult  result = this.flowInstanceService.saveProcessApproveResult(instancePo, objectId, sessionUtil.getCurrentUserId(), isAccess);
		if(!isAccess){
			result.setResultFlag("deprecated");
		}else{
			result.setResultFlag("success");
		}
        JSONObject json=JsonUtils.getJsonObject(result);
	    return JsonUtils.jsonObject2Json(json);
	}
	
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/doReject"},produces={"text/plain;charset=UTF-8"})
	public String doReject(ModelMap model,HttpServletRequest request,String suggest,String objectId){
	    FlowInstancePo instancePo = new FlowInstancePo();
	    instancePo.setSuggest(suggest);
		instancePo.setApproveResultDic(this.dicUtil.getDicInfo("ROD_APPROVE_STATUS", "REJECT"));//驳回
		boolean isAccess=ApwConstants.ISACCESS;
		ApproveResult  result = this.flowInstanceService.saveProcessApproveResult(instancePo, objectId, sessionUtil.getCurrentUserId(), isAccess);
		if(!isAccess){
			result.setResultFlag("deprecated");
		}else{
			result.setResultFlag("success");
		}
        JSONObject json=JsonUtils.getJsonObject(result);
	    return JsonUtils.jsonObject2Json(json);
	}
   
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/initNextApprover"},produces={"text/plain;charset=UTF-8"})
   public String initNextApprover(ModelMap model,HttpServletRequest request,String userId,String objectId){
	   boolean isAccess=ApwConstants.ISACCESS;
	   ApproveResult  result = this.flowInstanceService.initNextApprover(objectId, userId, isAccess);
	   if(!isAccess){
		   result.setResultFlag("deprecated");
	   }else{
		   result.setResultFlag("success");
	   }
	   JSONObject json=JsonUtils.getJsonObject(result);
	   return JsonUtils.jsonObject2Json(json);
   }
	
	@RequestMapping({ApwConstants.APW_APPROVE_PREFIX+"/opt-add/editMulProcess"})
	public String editMulProcess(ModelMap model,HttpServletRequest request,FlowInstancePo instancePo,String selectedBox){
		List<FlowInstancePo> instanceList = new ArrayList<FlowInstancePo>();
		String objectIdArray[] = selectedBox.split(",");
		for(String objectId:objectIdArray){
			FlowInstancePo fipo = this.flowInstanceService.getFlowInstancePo(objectId,sessionUtil.getCurrentUserId());
			instanceList.add(fipo);
		}
		
	     Page page = new Page();
	     if(instanceList.size()>0){
	    	 page.setPageSize(Page.DEFAULT_PAGE_SIZE);
	    	 int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
	    	 page.setStart((pageNo - 1) * Page.DEFAULT_PAGE_SIZE);
	    	 page.setTotalCount(instanceList.size());
	    	 page.setResult(instanceList);
	     }
	     
	     model.addAttribute("page", page);
	     model.addAttribute("objectIds", selectedBox);
		return ApwConstants.APW_NAMESPACE+"/mulBox";
	}
	
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/muldoPass"},produces={"text/plain;charset=UTF-8"})
	public String muldoPass(ModelMap model,HttpServletRequest request,String suggest,String objectIdArray,String approveKey){
	    FlowInstancePo instancePo = new FlowInstancePo();
	    instancePo.setSuggest(suggest);
	    instancePo.setApproveResultDic(this.dicUtil.getDicInfo("ROD_APPROVE_STATUS", "PASS"));//审批通过
		List<String> objectIds = new ArrayList<String>();
		String approverId = sessionUtil.getCurrentUserId();
		String objectArray[] = objectIdArray.split(",");
		for(String objectId:objectArray){
			objectIds.add(objectId);
		}
		
		boolean isAccess=ApwConstants.ISACCESS;
		MulApproveResult  mulResult = this.flowInstanceService.saveMulApproveResult(approveKey,instancePo, objectIds, approverId, isAccess);
		if(!isAccess){
			mulResult.setResultFlag("deprecated");
		}else{
			mulResult.setResultFlag("success");
		}
		
        JSONObject json=JsonUtils.getJsonObject(mulResult);
	    return JsonUtils.jsonObject2Json(json);
	}
   
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/muldoNotPass"},produces={"text/plain;charset=UTF-8"})
   public String muldoNotPass(ModelMap model,HttpServletRequest request,String suggest,String objectIdArray,String approveKey){
	    FlowInstancePo instancePo = new FlowInstancePo();
	    instancePo.setSuggest(suggest);
	    instancePo.setApproveResultDic(this.dicUtil.getDicInfo("ROD_APPROVE_STATUS", "NOT_PASS"));//审批不通过
	    
	   List<String> objectIds = new ArrayList<String>();
	   String approverId = sessionUtil.getCurrentUserId();
	   String objectArray[] = objectIdArray.split(",");
	   for(String objectId:objectArray){
		   objectIds.add(objectId);
	   }
	   
	   boolean isAccess=ApwConstants.ISACCESS;
	   MulApproveResult  mulResult = this.flowInstanceService.saveMulApproveResult(approveKey,instancePo, objectIds, approverId, isAccess);
	   if(!isAccess){
		   mulResult.setResultFlag("deprecated");
	   }else{
		   mulResult.setResultFlag("success");
	   }
	   
	   JSONObject json=JsonUtils.getJsonObject(mulResult);
	   return JsonUtils.jsonObject2Json(json);
   }
   
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/muldoReject"},produces={"text/plain;charset=UTF-8"})
   public String muldoReject(ModelMap model,HttpServletRequest request,String suggest,String objectIdArray,String approveKey){
	    FlowInstancePo instancePo = new FlowInstancePo();
	    instancePo.setSuggest(suggest);
	    instancePo.setApproveResultDic(this.dicUtil.getDicInfo("ROD_APPROVE_STATUS", "REJECT"));//审批驳回
	   List<String> objectIds = new ArrayList<String>();
	   String approverId = sessionUtil.getCurrentUserId();
	   String objectArray[] = objectIdArray.split(",");
	   for(String objectId:objectArray){
		   objectIds.add(objectId);
	   }
	   
	   boolean isAccess=ApwConstants.ISACCESS;
	   MulApproveResult  mulResult = this.flowInstanceService.saveMulApproveResult(approveKey,instancePo, objectIds, approverId, isAccess);
	   if(!isAccess){
		   mulResult.setResultFlag("deprecated");
	   }else{
		   mulResult.setResultFlag("success");
	   }
	   
	   JSONObject json=JsonUtils.getJsonObject(mulResult);
	   return JsonUtils.jsonObject2Json(json);
   }
	
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/initProcess"},produces={"text/plain;charset=UTF-8"})
	public String initProcess(ModelMap model,HttpServletRequest request,String processKey,String userId){
	   JSONObject json=null;
		try {
			//封装发起人
			User initiator = new User();
			initiator.setId(this.sessionUtil.getCurrentUserId());
			//封装第一级审核人
			User nextApprover = new User();
			nextApprover.setId(userId);
			ApproveResult result = 
					this.flowInstanceService.initProcessInstance(IdUtil.getUUIDHEXStr(), processKey,initiator,nextApprover,ApwConstants.ISACCESS);
			json=JsonUtils.getJsonObject(result);
		} catch (Exception e) {
			ApproveResult result = new ApproveResult();
			result.setResultFlag("error");
			json=JsonUtils.getJsonObject(result);
		}
	    return JsonUtils.jsonObject2Json(json);
	}
   
   @ResponseBody
   @RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-add/checkNextTaskApprover"},produces={"text/plain;charset=UTF-8"})
   public String checkNextTaskApprover(ModelMap model,HttpServletRequest request,String boxes){
	    ApproveResult result = new ApproveResult();
	    List<String> objectIds = new ArrayList<String>();
	    String selectedObjs [] = boxes.split("_");
	    for(String objectId:selectedObjs){
	    	objectIds.add(objectId);
	    }
		//批量审批时校验下一审批环节办理人
	    boolean isValidInterface=true;
		List<String>  illegalObjectIds = this.flowInstanceService.checkTaskApprover(objectIds,this.sessionUtil.getCurrentUserId(),isValidInterface);
		if(!isValidInterface){
			result.setResultFlag("deprecated");
		}else if(objectIds.size() == 1){
			result.setResultFlag("singleApprove");
		}else if(objectIds.size()>1 && illegalObjectIds.size()>0){
			result.setResultFlag("mulApprover");
		}else{
			result.setResultFlag("oneApprover");
		}
		
        JSONObject json=JsonUtils.getJsonObject(result);
	    return JsonUtils.jsonObject2Json(json);
   }
	
	@RequestMapping({ApwConstants.APW_APPROVE_PREFIX+"/opt-add/maodian"})
	public String maodian(ModelMap model,HttpServletRequest request){
		
		return ApwConstants.APW_NAMESPACE+"/maodian";
	}
	
	@ResponseBody
	@RequestMapping(value={ApwConstants.APW_APPROVE_PREFIX+"/opt-delete/rollback2LastStep"},produces={"text/plain;charset=UTF-8"})
	public String rollback2LastStep(ModelMap model,HttpServletRequest request,String objectId,String processKey){
		String returnValue="";
		try {
			if(ApwConstants.ISACCESS){
				this.flowInstanceService.rollback2LastStep(objectId,processKey);
				returnValue = "success";
			}else{
				returnValue="deprecated";
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnValue = "error";
		}
		return returnValue;
	}
	
	/**
	 * 
	 * @Title: viewProject
	 * @Description: 审批流 审核意见信息查看
	 * @param model
	 * @param request
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping(ApwConstants.APW_APPROVE_PREFIX+"/nsm/viewApproveHisory")
	public String viewApproveHistory(ModelMap model,HttpServletRequest request,String id)
	{
		if(StringUtils.isNotEmpty(id))
		{
			List<FlowHistoryPo> historyList = this.flowInstanceService.getCurProcessHistory(id,ProjectConstants.IS_APPROVE_ENABLE);
			model.addAttribute("historyList",historyList);
		}
		return "/apw/instance/approveHistoryView";
	}
	
}
