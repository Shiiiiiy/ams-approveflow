package com.uws.apw.dao;

import com.uws.apw.model.FlowDefinePo;
import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;

public interface IFlowDefineDao extends IBaseDao{
	
	/**
	 * 分页获取流程定义列表
	 * @param pageNo
	 * @param pageSize
	 * @param fdf
	 * @return
	 */
	public Page getDeFineList(int pageNo,int pageSize,FlowDefinePo fdf);
	
	/**
	 * 分页获取流程定义列表
	 * @param pageNo
	 * @param pageSize
	 * @param fdf
	 * @return
	 */
	public Page getDeFineListInfo(int pageNo,int pageSize);
	
	/**
	 * 修改流程定义
	 * @param fdf
	 */
	public void updateDefinePo(FlowDefinePo fdf);
	
	/**
	 * 新增流程定义
	 * @param fdf
	 */
	public void createDefinePo(FlowDefinePo fdf) ;
	
	/**
	 * 流程定义是否存在
	 * @param processId
	 * @return
	 */
	public boolean  isProcessShutdown(String processId);
	
	/**
	 * 获取单个流程定义
	 * @param id
	 */
	public void  editDefineEnable(FlowDefinePo fdfPo);

	/**
	 * 编辑流程定义
	 * @param id
	 */
	public void  editDefineDisable(String id);
	
	/**
	 * 判断流程定义是否存在
	 * @param pocessKey		流程key
	 * @return								流程key是否存在
	 */
	public boolean isExitDefineByKey(String pocessKey,String id);
	
	/**
	 * 根据主键获取流程定义
	 * @param id			流程主键
	 * @return				流程实例
	 */
	public FlowDefinePo getDefineById(String id);
	
	/**
	 * 判断流程key是否存在
	 * @param processKey		流程key
	 * @return								流程实例
	 */
	public FlowDefinePo getFlowDefineByKey(String processKey);
	
	 /**
	  * 判断流程定义名称是否重复
	  * @param processName
	  * @param id
	  * @return
	  */
	 public boolean isExitProcessName(String processName,String id);

	 /**
	  * 判断调用流程是否可用
	  * @param processKey		流程主键
	  * @return									[true、false]
	  */
	public boolean isAccessProcess(String processKey);
	
}
