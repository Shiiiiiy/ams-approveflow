package com.uws.apw.model;

import java.util.List;


/**
 * 流程审批结果
 */
public class MulApproveResult {

	private List<ApproveResult>  results;
	
	private String resultFlag;

	public List<ApproveResult> getResults() {
		return results;
	}

	public void setResults(List<ApproveResult> results) {
		this.results = results;
	}

	public String getResultFlag() {
		return resultFlag;
	}

	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}
	
	
}
