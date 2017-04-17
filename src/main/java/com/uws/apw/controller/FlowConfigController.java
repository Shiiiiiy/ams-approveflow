package com.uws.apw.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.apw.model.FlowConfigPo;
import com.uws.apw.model.FlowDefinePo;
import com.uws.apw.service.IFlowConfigService;
import com.uws.apw.util.ApwConstants;
import com.uws.common.service.IRankService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.Position;
import com.uws.user.model.Role;
import com.uws.user.model.User;
import com.uws.user.service.IPositionService;

/**
 * 审批流程配置类
 */
@Controller
@SuppressWarnings("all")
@RequestMapping(ApwConstants.APW_NAMESPACE_CONFIG)
public class FlowConfigController extends BaseController{
	
	@Autowired
	private IFlowConfigService flowConfigService;
	@Autowired
	private IRankService rankService;
	@Autowired
	private IPositionService positionService;
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private SessionUtil sessionUtil = SessionFactory.getSession(com.uws.sys.util.Constants.MENUKEY_SYSCONFIG);
	
	@RequestMapping({"/opt-query/getConfigurationList"})
	public String getConfigurationList(ModelMap model,HttpServletRequest request,FlowConfigPo configPo,String fid){
	     int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
	     Page page = this.flowConfigService.getIflowConfigList(pageNo, Page.DEFAULT_PAGE_SIZE,null, fid);
	     model.addAttribute("page", page);
		 model.addAttribute("fcpo", new FlowConfigPo());
	     model.addAttribute("positionPage", new Page());
	     model.addAttribute("fdfid", fid);
	     model.addAttribute("positioTypeList", dicUtil.getDicInfoList("POSITION_TYPE"));
		 return ApwConstants.APW_NAMESPACE_CONFIG+"/apwConfigList";
	}
	
   @RequestMapping(value={"/opt-query/ajaxGetConfigurationList"},produces={"text/plain;charset=UTF-8"})
   @ResponseBody
	public String ajaxGetConfigurationList(ModelMap model,HttpServletRequest request){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		model.addAttribute("page", new Page());
		model.addAttribute("fcpo", new FlowConfigPo());
		model.addAttribute("positionPage", new Page());
		model.addAttribute("positioTypeList", dicUtil.getDicInfoList("POSITION_TYPE"));
		return "{\"success\":\"success\",\"pageTarget\":\"CONFIGLIST\",\"pageNo\":\""+pageNo+"\"}";
	}
	
	@RequestMapping({"/nsm/getConfigurationList"})
	public String getConfigurationListInfo(ModelMap model,HttpServletRequest request,String processId){
	     int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
	     Page page = this.flowConfigService.getIflowConfigListIno(pageNo, Page.DEFAULT_PAGE_SIZE, processId);
	     model.addAttribute("page", page);
	     model.addAttribute("fcpo", new FlowConfigPo());
	     model.addAttribute("positionPage", new Page());
	     model.addAttribute("positioTypeList", dicUtil.getDicInfoList("POSITION_TYPE"));
		 return ApwConstants.APW_NAMESPACE_CONFIG+"/configLoadList";
	}
	
   @RequestMapping({"/nsm/editConfiguration"})
   public String editConfig(ModelMap model,HttpServletRequest request,String processId,String configId){
	   	   FlowConfigPo fcpo = new FlowConfigPo();
	   	   if(DataUtil.isNotNull(configId)){
	   		   fcpo =  this.flowConfigService.getIFlowConfigById(configId);
	   		  
	   	   }
	   	   model.addAttribute("fcpo", fcpo);
	   	   model.addAttribute("processId", processId);
	   	   Dic positionType = dicUtil.getDicInfo("POSITION_TYPE", "POSITION_SYS");
	       Dic roleType = dicUtil.getDicInfo("POSITION_TYPE", "ROLE_SYS");
	   	   if(null != fcpo.getPtype() &&  positionType.getId().equals(fcpo.getPtype().getId()))
	   	   {
	   		   Page page = this.flowConfigService.queryPosition(1,2, new Position());
	   		   model.addAttribute("positionPage",page);
	   		   model.addAttribute("approveType", "POSITION_SYS");
	   		   model.addAttribute("agentPosition", fcpo.getAgentPosition());
	   	   }else if(null != fcpo.getPtype() &&  roleType.getId().equals(fcpo.getPtype().getId())){
		   	   Page rolePage = this.flowConfigService.queryRole(1,ApwConstants.PAGE_NO_POSITION,new Role());
		       model.addAttribute("rolePage", rolePage);
		       model.addAttribute("approveType", "ROLE_SYS");
			   model.addAttribute("agentPosition", fcpo.getAgentPosition());
	   	   }
	   	   model.addAttribute("positioTypeList", dicUtil.getDicInfoList("POSITION_TYPE"));
		   return ApwConstants.APW_NAMESPACE_CONFIG+"/addapwConfig";
   }
   
   @RequestMapping(value={"/opt-edit/submitConfigInfo"},produces={"text/plain;charset=UTF-8"})
   @ResponseBody
   public String submitConfigInfo(ModelMap model,HttpServletRequest request,FlowConfigPo fcpo) throws Exception{
	   try {
				   //保存当前节点的配置信息
				   this.setFlowConfig(fcpo);
		} catch (Exception e) {
			 e.printStackTrace();
			 return "error";
		}
	   
	   return "success";
   }
   
   /**
    * 设置流程配置节点的信息
    * @param fcfPo					页面录入的节点信息
    * @throws Exception		异常抛出
    */
	private void setFlowConfig(FlowConfigPo fcfPo) throws Exception{
		
		//新增时初始化的配置信息
		if(DataUtil.isNotNull(fcfPo) && DataUtil.isNotNull(fcfPo.getId())){
			FlowConfigPo existConfigPo = this.formateUpdatePo(fcfPo);
			this.flowConfigService.updateFlowConfig(existConfigPo);
		}else{
			//封装流程定义对象
			FlowConfigPo creatConfigPo=this.formateCreatPo(fcfPo);
			this.flowConfigService.createFlowConfig(creatConfigPo);
		}
	}
	
	/**
	 * 封装待创建的节点对象
	 * @param fcfPo				节点对象
	 * @return							封装好的节点对象
	 * @throws Exception
	 */
	private FlowConfigPo formateCreatPo(FlowConfigPo fcfPo) throws Exception {
		
		FlowConfigPo newConfigPo = new FlowConfigPo();
		BeanUtils.copyProperties(newConfigPo, fcfPo);
		
		//封装任务节点顺序
		int maxSeq = this.flowConfigService.getMaxtaskSeq(fcfPo.getProcessId());
		int taskSeq = (maxSeq==0)?1:(maxSeq+1);
		newConfigPo.setTaskSeq(taskSeq);
		
		//封装岗位类型
		Dic positionDic=new Dic();
		String positionId = DataUtil.isNotNull(fcfPo.getPositionId())?fcfPo.getPositionId().split(";")[0]:"";
		positionDic.setId(positionId);
		newConfigPo.setPtype(positionDic);
		
		//封装流程定义对象
		FlowDefinePo definePo=new FlowDefinePo();
		definePo.setId(fcfPo.getProcessId());
		newConfigPo.setFlowDefinePo(definePo);
		
		//封装创建人
		User creator=new User();
		creator.setId(sessionUtil.getCurrentUserId());
		newConfigPo.setCreater(creator);
		
		//封装创建时间
		newConfigPo.setCreateTime(new Date());
		
		//封装逻辑删除状态
		newConfigPo.setDelStatus(dicUtil.getStatusNormal());
		
		return newConfigPo;
	}

	/**
	 * 封装待修改的配置对象
	 * @param fcfPo				页面配置对象信息
	 * @return							待修改的配置对象
	 */
	private FlowConfigPo formateUpdatePo(FlowConfigPo fcfPo) {
		FlowConfigPo existConfigPo = this.flowConfigService.getIFlowConfigById(fcfPo.getId());
		existConfigPo.setTaskName(fcfPo.getTaskName());
		//封装岗位类型
		Dic positionDic=new Dic();
		String positionId = DataUtil.isNotNull(fcfPo.getPositionId())?fcfPo.getPositionId().split(";")[0]:"";
		positionDic.setId(positionId);
		existConfigPo.setPtype(positionDic);
		existConfigPo.setAgentPosition(fcfPo.getAgentPosition());
		existConfigPo.setAgentPosname(fcfPo.getAgentPosname());
		return existConfigPo;
	}
	
	/**
	 * 异步加载岗位列表1
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value={"/opt-query/getApprovePosition"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String getApprovePosition(ModelMap model,HttpServletRequest request,HttpServletResponse response,
				   String approveType){
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		return "{\"success\":\"success\",\"pageNo\":\""+pageNo+"\",\"approveType\":\""+approveType+"\"}";
	}
	
	/**
	 * 异步加载岗位列表2
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({"/nsm/loadPositionList"})
	public String loadPositionList(ModelMap model,HttpServletRequest request,HttpServletResponse response,
				  String positionName,String approveType){
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Position position  = new Position();
		position.setName(positionName);
		Page page = this.flowConfigService.queryPosition(pageNo,ApwConstants.PAGE_NO_POSITION,position);
		model.addAttribute("positionPage", page);
		model.addAttribute("approveType", approveType);
		model.addAttribute("agentPosition", request.getParameter("agentPosition"));
		return ApwConstants.APW_NAMESPACE_CONFIG+"/approvePotionList";
	}
	
	/**
	 * 异步加载角色列表
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping({"/nsm/loadRoleList"})
	public String loadRoleList(ModelMap model,HttpServletRequest request,HttpServletResponse response,
			      String roleName,String approveType){
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Role role  = new Role();
		role.setName(roleName);
		Page page = this.flowConfigService.queryRole(pageNo,ApwConstants.PAGE_NO_POSITION,role);
		model.addAttribute("rolePage", page);
		model.addAttribute("approveType", approveType);
		model.addAttribute("agentPosition", request.getParameter("agentPosition"));
		return ApwConstants.APW_NAMESPACE_CONFIG+"/approveRoleList";
	}
	
	
	/**
	 * 上移节点
	 * @param request
	 * @param response
	 * @return
	 * @throws ClassNotFoundException 
	 */
	@RequestMapping(value={"/opt-modify/moveUpItem"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String moveUpItem(HttpServletRequest request,HttpServletResponse response,String id){
		String returnValue="";
		FlowConfigPo fcpo = this.flowConfigService.getIFlowConfigById(id);
		try {
			if(DataUtil.isNotNull(fcpo)){
				String rankColumn="taskSeq";//排序字段属性
				int rankValue=fcpo.getTaskSeq();//当前对象序号
				String fkColumn="flowDefinePo";//外键对象属性
				String fkValue=fcpo.getFlowDefinePo().getId();//外键值
				boolean  isMinSeq = this.rankService.isMinSeq(fcpo,rankColumn,rankValue,fkColumn,fkValue);
				if(isMinSeq){
					returnValue="min";
				}else{
					this.rankService.moveUpObject(fcpo, rankColumn, rankValue, fkColumn, fkValue);
					returnValue="success";
				}
			}else{
				returnValue="null";
			}
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}
	
	/**
	 * 下移节点
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value={"/opt-modify/moveDownItem"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String moveDownItem(HttpServletRequest request,HttpServletResponse response,String id){
		String returnValue="";
		FlowConfigPo fcpo = this.flowConfigService.getIFlowConfigById(id);
		try {
			if(DataUtil.isNotNull(fcpo)){
				String rankColumn="taskSeq";//排序字段属性
				int rankValue=fcpo.getTaskSeq();//当前对象序号
				String fkColumn="flowDefinePo";//外键对象属性
				String fkValue=fcpo.getFlowDefinePo().getId();//外键值
				boolean  isMaxSeq = this.rankService.isMaxSeq(fcpo,rankColumn,rankValue,fkColumn,fkValue);
				if(isMaxSeq){
					returnValue="max";
				}else{
					this.rankService.moveDownObject(fcpo,rankColumn,rankValue,fkColumn,fkValue);
					returnValue="success";
				}
			}else{
				returnValue="null";
			}
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}
	
	/**
	 * 删除当前节点
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/opt-modify/deleteItem"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String deleteItem(ModelMap model,HttpServletRequest request,HttpServletResponse response,String pk){
		String returnValue="";
		FlowConfigPo fcpo = this.flowConfigService.getIFlowConfigById(pk);
		String rankColumn="taskSeq";//排序字段属性
		int rankValue=fcpo.getTaskSeq();//当前对象序号
		String fkColumn="flowDefinePo";//外键对象属性
		String fkValue=fcpo.getFlowDefinePo().getId();//外键值
		try {
			if(DataUtil.isNotNull(fcpo)){
				//删除节点
				this.rankService.deleteCurObject(fcpo,rankColumn, rankValue, fkColumn, fkValue);
			}
			returnValue = "success";
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}
	
}
