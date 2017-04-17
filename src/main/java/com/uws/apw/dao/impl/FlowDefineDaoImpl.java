package com.uws.apw.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.apw.dao.IFlowDefineDao;
import com.uws.apw.model.FlowDefinePo;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

@Repository("flowDefineDao")
@SuppressWarnings("all")
public class FlowDefineDaoImpl extends BaseDaoImpl implements IFlowDefineDao {

	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	/**
	 * 获取流程定义列表
	 */
	public Page getDeFineList(int pageNo, int pageSize, FlowDefinePo fdf)
	{
		StringBuffer sbhql=new StringBuffer("from  FlowDefinePo fdf  where  1=1 ");
		List<String>values=new ArrayList<String>();
		if(DataUtil.isNotNull(fdf))
		{
			if(DataUtil.isNotNull(fdf.getProcessName()))
			{
				sbhql.append(" and fdf.processName like ? ");
				values.add("%"+fdf.getProcessName()+"%");
			}
			
			if(DataUtil.isNotNull(fdf.getStatusId()))
			{
				sbhql.append(" and fdf.statusId = ? ");
				values.add(fdf.getStatusId());
			}
			
			if(DataUtil.isNotNull(fdf.getProcessKey())){
				sbhql.append(" and fdf.processKey=?");
				values.add(fdf.getProcessKey());
			}
		}
		
//		sbhql.append(" order by fdf.updateTime desc");
		if(values.size()>0)
		  return this.pagedQuery(sbhql.toString(), pageNo, pageSize, values.toArray());
		else		  
		  return this.pagedQuery(sbhql.toString(), pageNo, pageSize);
	}

	private void clearSessionFactroy(Object object)
	{
		this.sessionFactory.getCurrentSession().flush();
		this.sessionFactory.getCurrentSession().evict(object);
	}
	
	/**
	 * 修改流程定义
	 */
	public void updateDefinePo(FlowDefinePo fdf) 
	{
			this.update(fdf);	
	}
	
	/**
	 * 新增流程定义
	 */
	public void createDefinePo(FlowDefinePo fdf) 
	{
		this.save(fdf);	
	}

	/**
	 * 判断当前流程是否【启用】
	 */
	public boolean isProcessShutdown(String processId) {
		Dic stutasDic=dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", "DISABLE");
	    FlowDefinePo fdpo=(FlowDefinePo)this.queryUnique("from FlowDefinePo where id=? and status.id=?", new Object[]{processId,stutasDic.getId()});
	    if(DataUtil.isNotNull(fdpo)){
	    	return true;
	    }else{
	    	return false;
	    }
	}

	/**
	 * 流程状态【启用/停用】
	 */
	public void editDefineEnable(FlowDefinePo fdfPo) {
		//启用流程编码
		Dic enableDic=dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", "ENABLE");
		//停用流程编码
		Dic disableDic=dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", "DISABLE");
		//当前流程状态
		String curStatus = fdfPo.getStatus().getCode();
		 if(DataUtil.isNotNull(fdfPo)){
			 if(curStatus.equals(enableDic.getCode())){//停用【当前启用状态】的流程
				fdfPo.setStatus(disableDic);
			 }else if(curStatus.equals(disableDic.getCode())){//启用【当前停用状态】的流程
			     fdfPo.setStatus(enableDic);
			 }
			   this.update(fdfPo);
		 }
	}

	/**
	 * 校验流程key是否重复
	 */
	public boolean isExitDefineByKey(String pocessKey,String id) {
		StringBuffer sbhql=new StringBuffer("from  FlowDefinePo fdf  where  1=1");
		List<String>values=new ArrayList<String>();
		boolean flag=false;
		if(StringUtils.isNotEmpty(pocessKey))
		{
			sbhql.append(" and fdf.processKey = ? ");
			values.add(pocessKey);
		}
		if(values.size()>0)
		{
			List<FlowDefinePo> fdfList=this.query(sbhql.toString(), values.toArray());
			if(fdfList.size()>0)
			{
				for(FlowDefinePo fdf:fdfList)
				{
					if(null!=fdf){
						flag=true;
					}
					if(null!=id||!"".equals(id))
					{
					  if(id.equals(fdf.getId())||id==fdf.getId())
					  flag=false;	
					}
				}
			}
		}
		return flag;
	}

	public Page getDeFineListInfo(int pageNo, int pageSize) {
		StringBuffer sbhql=new StringBuffer("from  FlowDefinePo fdf");
		return this.pagedQuery(sbhql.toString(), pageNo, pageSize);
	}

	public FlowDefinePo getDefineById(String id) {
		FlowDefinePo fdfPo=null;
		if(null!=id||!"".equals(id))
		{
			fdfPo=(FlowDefinePo) this.get(FlowDefinePo.class, id);
		}
		return fdfPo!=null?fdfPo:new FlowDefinePo();
	}

	@Override
	public FlowDefinePo getFlowDefineByKey(String processKey) {
		return (FlowDefinePo)this.queryUnique("from FlowDefinePo fdp where fdp.processKey=?", new Object[]{processKey});
	}

	@Override
	public void editDefineDisable(String id) {
		
	}

	/**
	 * 校验流程名称是否重复
	 */
	public boolean isExitProcessName(String processName, String id) {
		StringBuffer sbhql=new StringBuffer("from  FlowDefinePo fdf  where  1=1 ");
		List<String>values=new ArrayList<String>();
		boolean flag=false;
		if(StringUtils.isNotEmpty(processName))
		{
			sbhql.append(" and fdf.processName = ? ");
			values.add(processName);
		}
		if(values.size()>0)
		{
			List<FlowDefinePo> fdfList=this.query(sbhql.toString(), values.toArray());
			if(fdfList.size()>0)
			{
				for(FlowDefinePo fdf:fdfList)
				{
					if(null!=fdf){
						flag=true;
					}
					if(null!=id||!"".equals(id))
					{
					  if(id.equals(fdf.getId())||id==fdf.getId())
					  flag=false;	
					}
				}
			}
		}
		return flag;
	}

	@Override
	public boolean isAccessProcess(String processKey) {
		boolean returnValue = false;
		if(DataUtil.isNotNull(processKey)){
			List<String> values = new ArrayList<String>();
			StringBuffer hql = new StringBuffer(" from FlowDefinePo fdpo where fdpo.processKey=? and fdpo.status.code=?");
			values.add(processKey);
			values.add("ENABLE");
			FlowDefinePo definePo =  (FlowDefinePo)this.queryUnique(hql.toString(), values.toArray());
			if(DataUtil.isNotNull(definePo)){
				returnValue = true;
			}
		}
		return returnValue;
	}

}
