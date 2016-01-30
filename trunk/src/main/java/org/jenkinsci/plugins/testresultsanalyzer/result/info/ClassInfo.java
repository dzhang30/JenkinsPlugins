package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.ClassResult;

import java.util.Map;
import java.util.TreeMap;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.ClassResultData;

import net.sf.json.JSONObject;

public class ClassInfo extends Info{

	private Map<String, TestCaseInfo> tests = new TreeMap<String, TestCaseInfo>();

	/**
	 * Description of putBuildClassResult
	 * insert a particular class build result to a class info object
	 * @param buildNumber - the build number for the insert class result
	 * @param classResult - the build result for the insert class result
	 */
	public void putBuildClassResult(Integer buildNumber, ClassResult classResult){
		ClassResultData classResultData = new ClassResultData(classResult);

		evaluateStatusses(classResult);
		addTests(buildNumber, classResult);
		this.type = "Class";
		this.priority = -1;
		this.buildResults.put(buildNumber, classResultData);		
	}

	/**
	 * A helper method that adds all tests to a class in a specific build number.
	 * @param buildNumber - the build number to insert with tests.
	 * @param classResult - the class that contains test results.
	 */
	private void addTests(Integer buildNumber, ClassResult classResult) {
		for (CaseResult testCaseResult : classResult.getChildren()) {
			String testCaseName = testCaseResult.getName();
			TestCaseInfo testCaseInfo;
			if (tests.containsKey(testCaseName)) {
				testCaseInfo = tests.get(testCaseName);
			} else {
				testCaseInfo = new TestCaseInfo();
				testCaseInfo.setName(testCaseName);
			}

			testCaseInfo.putTestCaseResult(buildNumber, testCaseResult);
			tests.put(testCaseName, testCaseInfo);
		}
	}

	/**
	 * Description of getTests
	 * get all test results info for a particular class
	 * @return Map<String, TestCaseInfo> - a map for all test info under this class info
	 */
	public Map<String, TestCaseInfo> getTests() {
		return tests;
	}
	
	/**
	 * Returns the children of this object in a new JSONObject.
	 * @return json - all json under the current object in a new JSONObject.
	 */
	protected JSONObject getChildrensJson(){
		JSONObject json = new JSONObject();
		for(String testName : tests.keySet()){
			json.put(testName, tests.get(testName).getJsonObject());
		}
		return json;
	}
}
