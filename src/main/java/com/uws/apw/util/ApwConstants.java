package com.uws.apw.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;

public class ApwConstants {
	
	public static final String APW_NAMESPACE="/apw/instance";
	
	public static final String APW_NAMESPACE_DEFINE="/apw/define";
	
	public static final String APW_NAMESPACE_CONFIG="/apw/config";
	
	public static int PAGE_NO_POSITION=5;
	
	public static final boolean ISACCESS=true;
	
	public static final String APW_APPROVE_PREFIX="/apw/amsapprove";

	/**
	 * 审批令牌
	 */
	public static enum APPROVETOKEN{
		/**
		 * 有效令牌
		 */
		AVAILABLE,
		/**
		 * 无效令牌
		 */
		INAVAILABLE
	}
	
	/**
	 * 流程状态
	 */
	public static enum PROCESS_STATUS{
		/**
		 * 不通过
		 */
		NOT_PASS,
		
		/**
		 * 待审核
		 */
		CURRENT_APPROVE,
		
		/**
		 * 审核中
		 */
		APPROVEING,
		
		/**
		 * 通过
		 */
		PASS,
		
		/**
		 * 驳回
		 */
		REJECT
	}
	
}
