package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.junit.CaseResult;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.TestCaseResultData;

import net.sf.json.JSONObject;

public class TestCaseInfo extends Info {

	/**
	 * Description of putTestCaseResult
	 * Insert a particular build test case result to a testcase info object
	 * @param buildNumber - the build number for the insert test case result
	 * @param testCaseResult - the test case result for the insert test case result
	 */
	public void putTestCaseResult(Integer buildNumber, CaseResult testCaseResult) {
		TestCaseResultData testCaseResultData = new TestCaseResultData(testCaseResult);
		evaluateStatusses(testCaseResult);
		this.type = "Test Case";
		this.description = "";
		this.priority = -1;
		this.buildResults.put(buildNumber, testCaseResultData);
	}

	@Override
	protected JSONObject getChildrensJson() {

		return new JSONObject();
	}

}