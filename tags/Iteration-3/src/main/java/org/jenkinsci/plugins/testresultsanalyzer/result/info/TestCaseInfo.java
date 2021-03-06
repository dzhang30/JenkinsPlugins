package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.junit.CaseResult;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.TestCaseResultData;

import net.sf.json.JSONObject;

public class TestCaseInfo extends Info {

	public void putTestCaseResult(Integer buildNumber, CaseResult testCaseResult) {
		TestCaseResultData testCaseResultData = new TestCaseResultData(testCaseResult);
		evaluateStatusses(testCaseResult);
		//testCaseResultData.setDescription("Tezzt caseeee");
		this.type = "Test Case";
		this.priority = -1;
		this.buildResults.put(buildNumber, testCaseResultData);
	}

	@Override
	protected JSONObject getChildrensJson() {

		return new JSONObject();
	}

}