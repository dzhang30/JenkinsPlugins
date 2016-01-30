package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.ClassResult;

import java.util.Map;
import java.util.TreeMap;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.ClassResultData;

import net.sf.json.JSONObject;

public class ClassInfo extends Info{
	
	private Map<String, TestCaseInfo> tests = new TreeMap<String, TestCaseInfo>();
	
	
	public void putBuildClassResult(Integer buildNumber, ClassResult classResult){
		ClassResultData classResultData = new ClassResultData(classResult);
		
		evaluateStatusses(classResult);
		addTests(buildNumber, classResult);
		this.type = "Class";
		this.priority = -1;
		this.buildResults.put(buildNumber, classResultData);		
	}
	
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

			//testCaseInfo.setDescription("Test Case: " + testCaseResult.getUrl());
			testCaseInfo.putTestCaseResult(buildNumber, testCaseResult);
			tests.put(testCaseName, testCaseInfo);
		}
	}

	public Map<String, TestCaseInfo> getTests() {
		return tests;
	}

	protected JSONObject getChildrensJson(){
		JSONObject json = new JSONObject();
		for(String testName : tests.keySet()){
			json.put(testName, tests.get(testName).getJsonObject());
		}
		return json;
	}
}
