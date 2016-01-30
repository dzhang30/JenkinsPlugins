package org.jenkinsci.plugins.testresultsanalyzer.result.data;

import java.util.ArrayList;
import java.util.Collection;

import hudson.tasks.junit.CaseResult;
import hudson.tasks.test.TestResult;

public class TestCaseResultData extends ResultData {

	public TestCaseResultData(CaseResult testResult){
		setName(testResult.getName());
		setDescription("Test Case");
		setPassed(testResult.isPassed());
		setSkipped(testResult.getSkipCount() == testResult.getTotalCount());
		setTotalTests(testResult.getTotalCount());
		setTotalFailed(testResult.getFailCount());
		setTotalPassed(testResult.getPassCount());
		setTotalSkipped(testResult.getSkipCount());
		setTotalTimeTaken(testResult.getDuration());
		evaluateStatus();
		
		//setFailureMessage(testResult.getErrorStackTrace());
		Collection<? extends TestResult> failtests = testResult.getFailedTests();
		for (TestResult key:failtests)
		{
			String details = key.getErrorDetails();
			setFailureDetails(details);
		}
		//setFailureDetails(testResult.getErrorDetails());
	}
	
}
