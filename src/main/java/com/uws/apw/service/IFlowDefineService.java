package com.uws.apw.service;

import com.uws.apw.model.FlowDefinePo;
import com.uws.core.hibernate.dao.support.Page;

public abstract interface IFlowDefineService {
	
		/**
		 * 获取分页的流程定义信息
		 * @param pageNo
		 * @param pageSize
		 * @param fdf
		 * @return
		 */
		public Page getDeFineList(int pageNo,int pageSize,FlowDefinePo fdf);
		
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
		 * 判断定义的流程是否存在
		 * @param id
		 * @return
		 */
		public boolean  isProcessShutdown(String id);
		
		/**
		 * 启用当前流程
		 * @param id
		 */
		public void  editDefineEnable(FlowDefinePo fdfPo);
		
		/**
		 * 禁用当前流程
		 * @param id
		 */
		public void  editDefineDisable(String id);
		
		/**
		 * 流程key是否存在
		 * @param pocessKey
		 * @return
		 */
		public boolean isExitDefineByKey(String pocessKey,String id);
		
		/**
		 * 分页获取流程信息
		 * @param pageNo
		 * @param pageSize
		 * @param fdf
		 * @return
		 */
		public Page getDeFineListInfo(int pageNo,int pageSize);
		
		
		/**
		 * 根据主键获取流程定义
		 * @param id
		 * @return
		 */
		public FlowDefinePo getDefineById(String id);
		
		/**
		 * 根据流程定义key获取流程
		 * @param processKey
		 * @return
		 */
		public FlowDefinePo getFlowDefineByKey(String processKey);
		
		/**
		 * 判断流程名称是否重复
		 * @param processName
		 * @param id
		 * @return
		 */
		 public boolean isExitProcessName(String processName,String id);
		
		 /**
		  * 判断调用的流程是否可用
		  * @param processKey		流程定义key[唯一]
		  * @return									[true、false]
		  */
		public boolean isAccessProcess(String processKey);

		/**
		 * 流程开关
		 * @param processId		流程Id
		 */
		public FlowDefinePo processSwitch(String processId);
}
