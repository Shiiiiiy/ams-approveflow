package com.uws.apw.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.uws.apw.dao.IFlowConfigDao;
import com.uws.apw.model.FlowConfigPo;
import com.uws.apw.model.FlowDefinePo;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.Position;
import com.uws.user.model.Role;

@Repository("flowConfigDao")
@SuppressWarnings("all")
public class FlowConfigDaoImpl extends BaseDaoImpl implements IFlowConfigDao{
	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	@Override
	public List<FlowConfigPo> getFlowConfigByDefine(String processKey) {
		if(DataUtil.isNotNull(processKey)){
			return this.query("from FlowConfigPo fcp where fcp.flowDefinePo.processKey=?", new Object[]{processKey});
		}else{
			return new ArrayList<FlowConfigPo>();
		}
	}
	
	@Override
    public FlowConfigPo getNextConfigPoByIniator(String processKey,int taskSeq)
    {
		if(DataUtil.isNotNull(processKey)){
			return (FlowConfigPo)this.queryUnique("from FlowConfigPo fcp where fcp.flowDefinePo.processKey=? and fcp.taskSeq=?", new Object[]{processKey,taskSeq});
		}else{
			return new FlowConfigPo();
		}
    }

	/**
	 * 获取配置流程列表信息
	 */
	public Page getIflowConfigList(int pageNo, int pageSize,
			FlowConfigPo fcfPo, String fdfId) {
		StringBuffer sbhql=new StringBuffer(" from FlowConfigPo fcf  where 1=1 ");
		List<String>values=new ArrayList<String>();
		if(StringUtils.isNotEmpty(fdfId))
		{
			sbhql.append(" and fcf.flowDefinePo.id = ? ");
			values.add(fdfId);
		}
	    sbhql.append(" and fcf.delStatus.id = ? ");
	    values.add(dicUtil.getStatusNormal().getId());
	    sbhql.append(" order by fcf.taskSeq");
	   if(values.size()>0)
		   return this.pagedQuery(sbhql.toString(), pageNo, pageSize, values.toArray());
	   else
		   return this.pagedQuery(sbhql.toString(), pageNo, pageSize);
	}

	/**
	 * 创建流程配置
	 */
	public void createFlowConfig(FlowConfigPo fcfPo) {
			this.save(fcfPo);
	}
	
	/**
	 * 修改流程配置
	 */
	public void updateFlowConfig(FlowConfigPo fcfPo) {
		 this.update(fcfPo);	
	}

	@Override
	public FlowConfigPo getIFlowConfigById(String id) {
		FlowConfigPo fcfPo=null;
		if(id!=null&&!"".equals(id))
		{
			fcfPo=(FlowConfigPo) this.get(FlowConfigPo.class, id);
		}
		return fcfPo;
	}
	
	public int getMaxtaskSeq(String fdfid) {
		Session session=this.sessionFactory.openSession();
		String sql="SELECT MAX(TASK_SEQ) AS maxValues FROM rod_ap_config WHERE PROCESS_ID = ? " +
				" AND DEL_STATUS= ? ";
		SQLQuery query=session.createSQLQuery(sql);
		query.setString(0, fdfid);
		query.setString(1, dicUtil.getStatusNormal().getId());
		Integer maxValues=(Integer)query.addScalar("maxValues",Hibernate.INTEGER).uniqueResult();
		return null==maxValues?0:maxValues;
	}

	public List<FlowConfigPo> editTaskSep(String id,String fifid) {
		StringBuffer sbhql=new StringBuffer(" from FlowConfigPo fcf  where 1=1 ");
		List<String>values=new ArrayList<String>();
		if(StringUtils.isNotEmpty(fifid))
		{
			sbhql.append(" and fcf.flowDefinePo.id = ? ");
			values.add(fifid);
		}
	    sbhql.append(" and fcf.delStatus.id = ? ");
	    values.add(dicUtil.getStatusNormal().getId());
	    sbhql.append(" order by fcf.taskSeq");
		return this.query(sbhql.toString(), values.toArray());
		
	}
	public Page getIflowConfigListIno(int pageNo, int pageSize, String fdfId) {
		StringBuffer sbhql=new StringBuffer(" from FlowConfigPo fcf  where 1=1 ");
		List<String>values=new ArrayList<String>();
		if(StringUtils.isNotEmpty(fdfId))
		{
			sbhql.append(" and fcf.flowDefinePo.id = ? ");
			values.add(fdfId);
		}
		 sbhql.append(" and fcf.delStatus.id = ? ");
		    values.add(dicUtil.getStatusNormal().getId());
		 sbhql.append(" order by fcf.taskSeq");
		return this.pagedQuery(sbhql.toString(), pageNo, pageSize, values.toArray());
	  
	}
	
	public void delConfig(FlowConfigPo fcfPo) {
		this.delete(fcfPo);
	}
	

	@Override
	public void replaceDelConfig(FlowConfigPo fcf) {
		fcf.setTaskSeq((fcf.getTaskSeq()-1));
		this.update(fcf);
	}
	
	public void delIFlowConfig(String id) {
		
		
	}

	
	//上移
	public void flowerConfig(String id,int taskSeq,String fifid) {
		
		int newtaskSeq=taskSeq;
		
		//更新相邻的上一位
		List<Object>param=new ArrayList<Object>();
		String newhql="UPDATE FlowConfigPo fcfPo SET fcfPo.taskSeq=? WHERE fcfPo.taskSeq =? " +
				"AND delStatus.id = ? AND fcfPo.flowDefinePo.id= ? ";
		param.add(newtaskSeq);
		param.add(newtaskSeq-1);
		param.add(dicUtil.getStatusNormal().getId());
		param.add(fifid);
		this.executeHql(newhql, param.toArray());
		
		//更新自己本身减一位
		List<Object>values=new ArrayList<Object>();
		String hql="UPDATE FlowConfigPo fcfPo SET fcfPo.taskSeq=? WHERE fcfPo.id=? " +
				"AND delStatus.id = ? AND fcfPo.flowDefinePo.id= ? ";
		values.add((taskSeq-1));
		values.add(id);
		values.add(dicUtil.getStatusNormal().getId());
		values.add(fifid);
		this.executeHql(hql, values.toArray());

	}

	//下移
	public void godownConfig(String id,int taskSeq,String fifid) {
		int newtaskSeq=taskSeq;
		
		//更新相邻的上一位
		List<Object>param=new ArrayList<Object>();
		String newhql="UPDATE FlowConfigPo fcfPo SET fcfPo.taskSeq=? WHERE fcfPo.taskSeq =? " +
				"AND delStatus.id = ? AND fcfPo.flowDefinePo.id= ? ";
		param.add(newtaskSeq);
		param.add(newtaskSeq+1);
		param.add(dicUtil.getStatusNormal().getId());
		param.add(fifid);
		this.executeHql(newhql, param.toArray());
		
		//更新自己本身减一位
		List<Object>values=new ArrayList<Object>();
		String hql="UPDATE FlowConfigPo fcfPo SET fcfPo.taskSeq=? WHERE fcfPo.id=? " +
				"AND delStatus.id = ? AND fcfPo.flowDefinePo.id= ? ";
		values.add((taskSeq+1));
		values.add(id);
		values.add(dicUtil.getStatusNormal().getId());
		values.add(fifid);
		this.executeHql(hql, values.toArray());
		
	}

	@Override
	public Page queryPosition(int pageNo, int pageSize, Position position) {
		List values = new ArrayList();
		String hql = "from Position where deleteStatus.id='" + DicFactory.getDicUtil().getStatusNormal().getId() + "'";
	     if ((position.getName() != null) && (!"".equals(position.getName()))) {
	       hql = hql + " and name like ?";
	 
	       if (HqlEscapeUtil.IsNeedEscape(position.getName())) {
		         values.add("%" + HqlEscapeUtil.escape(position.getName()) + "%");
		         hql = hql + HqlEscapeUtil.HQL_ESCAPE;
	       }else {
	    	   values.add("%" + position.getName() + "%");
	       }
	     }
	     if ((position.getLevel() != null) && (!"".equals(position.getLevel()))) {
	    	 hql = hql + " and levelDic.id='" + position.getLevel() + "'";
	     }
	     if ((position.getStatus() != null) && (!"".equals(position.getStatus()))) {
	    	 hql = hql + " and status = '" + position.getStatus() + "'";
	     }
     	hql = hql + " order by createTime desc";
     	Page page = pagedQuery(hql, pageNo,pageSize, values.toArray());
     
     	return page;
	}
	
	public Page queryRole(int pageNo,int pageSize,Role role)
	{
	     List values = new ArrayList();
	     StringBuffer hql = new StringBuffer("from Role where 1=1");
	  
	     if ((role.getName() != null) && (!"".equals(role.getName())))
	  {
	       hql.append(" and name like ?");
	  
	       if (HqlEscapeUtil.IsNeedEscape(role.getName()))
	    {
	         values.add("%" + HqlEscapeUtil.escape(role.getName()) + "%");
	         hql.append(HqlEscapeUtil.HQL_ESCAPE);
	    }
	    else {
	         values.add("%" + role.getName() + "%");
	    }
	  }
	     if ((role.getType() != null) && (!"".equals(role.getType())))
	  {
	       hql.append(" and type = ?");
	      values.add(role.getType());
	  }
	     if ((role.getStatus() != null) && (!"".equals(role.getStatus())))
	  {
	       hql.append(" and statusDic.id = ?");
	       values.add(role.getStatus());
	  }
	     hql.append(" order by statusDic.code desc,roleTypeDic.code,code,name asc ");
	    
	     Page page = null;
	     if (values.size() == 0){
	         page = pagedQuery(hql.toString(), pageNo, pageSize, new Object[0]);
	     }else{
	    	 page = pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	     }
	     return page;
	}
}
