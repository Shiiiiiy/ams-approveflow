package com.uws.apw.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.apw.model.FlowDefinePo;
import com.uws.apw.model.ResultDefinePo;
import com.uws.apw.service.IFlowDefineService;
import com.uws.apw.util.ApwConstants;
import com.uws.apw.util.JsonUtils;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;

/**
 * 流程定义
 */
@Controller
@SuppressWarnings("all")
public class FlowDefineController extends BaseController{

	@Autowired
	private IFlowDefineService flowDefineService;
	//数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();
	//sessionUtil工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(com.uws.sys.util.Constants.MENUKEY_SYSCONFIG);
	
	@RequestMapping({ApwConstants.APW_NAMESPACE_DEFINE+"/opt-query/getDefinitionList"})
	public String getDefinitionList(ModelMap model,HttpServletRequest request,FlowDefinePo definePo){
	     int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
	     Page page=this.flowDefineService.getDeFineList(pageNo, Page.DEFAULT_PAGE_SIZE, definePo);
		 model.addAttribute("page", page);
	     //流程状态
	     List<Dic>definDicList=this.dicUtil.getDicInfoList("STATUS_ENABLE_DISABLE");
	     model.addAttribute("definDicList", definDicList);
	 	 model.addAttribute("fdpo", new FlowDefinePo());
	 
		 return ApwConstants.APW_NAMESPACE_DEFINE+"/apwDefineList";
	}
	
	@RequestMapping({ApwConstants.APW_NAMESPACE_DEFINE+"/nsm/getDefinitionList"})
	public String getDefinitionInfoList(ModelMap model,HttpServletRequest request,FlowDefinePo definePo){
	    
		 int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
	     Page page=this.flowDefineService.getDeFineList(pageNo, Page.DEFAULT_PAGE_SIZE, definePo);
	     //流程状态
	     List<Dic>definDicList=this.dicUtil.getDicInfoList("STATUS_ENABLE_DISABLE");
	     model.addAttribute("definDicList", definDicList);
	     model.addAttribute("fdpo", definePo);
	     model.addAttribute("page", page);
		 return ApwConstants.APW_NAMESPACE_DEFINE+"/defineLoadList";
	}
	
	@RequestMapping(value={ApwConstants.APW_NAMESPACE_DEFINE+"/opt-check/isProcessShutdown"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String isProcessShutdown(ModelMap model,HttpServletRequest request,String processId)
	{
		boolean flag=this.flowDefineService.isProcessShutdown(processId);
		if(flag){
			return "success";
		}else{
			return "false";
		}
	}
	
	@RequestMapping(value={ApwConstants.APW_NAMESPACE_DEFINE+"/nsm/editProcessDefine"})
	public String editDefineStatus(ModelMap model,HttpServletRequest request,String processId){
		FlowDefinePo fdpo = new FlowDefinePo();
		if(DataUtil.isNotNull(processId)){
			fdpo = this.flowDefineService.getDefineById(processId);
		}else{
			fdpo.setStatus(this.dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", "DISABLE"));
		}
		List<Dic>definDicList=this.dicUtil.getDicInfoList("STATUS_ENABLE_DISABLE");
		model.addAttribute("definDicList", definDicList);
		model.addAttribute("fdpo", fdpo);
		return ApwConstants.APW_NAMESPACE_DEFINE+"/addapwDefine";
	}
	
	@RequestMapping(value={ApwConstants.APW_NAMESPACE_DEFINE+"/opt-isexit/isExitDefineKey"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String isExitDefineKey(@RequestParam String id,@RequestParam String processKey)
	{
	   String success="true";
	   boolean flag=this.flowDefineService.isExitDefineByKey(processKey, id);
	   if(flag)
	    success="false";   
	    return success;
	}
	
	@RequestMapping(value={ApwConstants.APW_NAMESPACE_DEFINE+"/opt-isexit/isExitDefineName"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String isExitDefineName(@RequestParam String id,@RequestParam String processName)
	{
	   String success="true";
	   boolean flag=this.flowDefineService.isExitProcessName(processName, id);
	   if(flag)
	    success="false";   
	    return success;
	}
	
	@RequestMapping(value={ApwConstants.APW_NAMESPACE_DEFINE+"/opt-edit/submitDefinition"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
    public String submitDefinition(ModelMap model,HttpServletRequest request,FlowDefinePo fdpo)
    {
		  //流程状态
	      List<Dic>definDicList=this.dicUtil.getDicInfoList("STATUS_ENABLE_DISABLE");
		  model.addAttribute("definDicList", definDicList);
		  FlowDefinePo newFdpo = this.setFlowDefinePo(fdpo);
		  if(DataUtil.isNotNull(newFdpo) && DataUtil.isNotNull(newFdpo.getId())){
			  this.flowDefineService.updateDefinePo(newFdpo);
		  }else{
			  this.flowDefineService.createDefinePo(newFdpo);
		  }
          return "success";
    }
	
	private FlowDefinePo setFlowDefinePo(FlowDefinePo fdfPo){
		FlowDefinePo newFdpo=new FlowDefinePo();
		if(DataUtil.isNotNull(fdfPo)){
			if(DataUtil.isNotNull(fdfPo.getId())){
				FlowDefinePo existFdpo =this.flowDefineService.getDefineById(fdfPo.getId());
				newFdpo = existFdpo;
			}
			Dic stutasDic=new Dic();
			stutasDic.setId(fdfPo.getStatusId());
			newFdpo.setStatus(stutasDic);
			newFdpo.setProcessName(fdfPo.getProcessName());
			newFdpo.setProcessKey(fdfPo.getProcessKey());
		}
		User user=new User();
		user.setId(sessionUtil.getCurrentUserId());
		newFdpo.setCreator(user);
		newFdpo.setCreateTime(new Date());
		
		return newFdpo;
	}
	
	@RequestMapping(value={"apw/define/opt-edit/processSwitch"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String processSwitch(ModelMap model,HttpServletRequest request,String processId)
	{
		/**
		 * 流程开关
		 */
		FlowDefinePo fdpo = this.flowDefineService.processSwitch(processId);
		return "{\"processName\":\""+fdpo.getProcessName()+"\",\"id\":\""+fdpo.getId()+"\"}";
	}
	
}
