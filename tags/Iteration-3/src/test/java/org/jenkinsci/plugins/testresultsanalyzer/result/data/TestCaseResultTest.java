package org.jenkinsci.plugins.testresultsanalyzer.result.data;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.test.TestResult;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.TestCaseResultData;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.TestCaseInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TestCaseResultTest{
	TestCaseResultData test;
	String result;	
	/*public static CaseResult setdata(boolean ispass)
	{
		CaseResult data;
		TestResult data2;
		Collection<TestResult> failtests;
		data = mock(CaseResult.class);
		Boolean isError = true;
		when(data.getName()).thenReturn("test");
		when(data.isPassed()).thenReturn(ispass);
		when(data.getSkipCount()).thenReturn(0);
		when(data.getTotalCount()).thenReturn(1);
		when(data.getPassCount()).thenReturn(0);
		when(data.getFailCount()).thenReturn(1);
		when(data.isSkipped()).thenReturn(false);
		Float temp = new Float(1.1);
		when(data.getDuration()).thenReturn(temp);
		
		data2 = mock(TestResult.class);
		when(data2.getErrorDetails()).thenReturn("We failed!");
		failtests = new ArrayList<TestResult>();
		failtests.add(data2);
		Collection<TestResult> temp1 = (Collection< TestResult>) failtests;
		Mockito.doReturn(temp1).when(data2).getFailedTests();
	//	when(data.getFailedTests()).thenReturn(temp1);
		return data;
	}*/
	
	@Before
	public void setUp() throws Exception {
	//	CaseResult data3 = setdata(false);
		CaseResult data;
		TestResult data2;
		Collection<TestResult> failtests;
		data = mock(CaseResult.class);
		Boolean isError = true;
		when(data.getName()).thenReturn("test");
		when(data.isPassed()).thenReturn(false);
		when(data.getSkipCount()).thenReturn(0);
		when(data.getTotalCount()).thenReturn(1);
		when(data.getPassCount()).thenReturn(0);
		when(data.getFailCount()).thenReturn(1);
		when(data.isSkipped()).thenReturn(false);
		Float temp = new Float(1.1);
		when(data.getDuration()).thenReturn(temp);
		
		data2 = mock(TestResult.class);
		when(data2.getErrorDetails()).thenReturn("We failed!");
		failtests = new ArrayList<TestResult>();
		failtests.add(data2);
		Collection<TestResult> temp1 = (Collection< TestResult>) failtests;
		Mockito.doReturn(temp1).when(data).getFailedTests();
	//	when(data.getFailedTests()).thenReturn(temp1);
//		return data;

		test = new TestCaseResultData(data);
		result = test.getFailureDetails();
	}

	@Test
	public void test_get_error_details(){
		assertEquals(result, "We failed!");
	}
	
	@Test
	public void test_set_and_get_details(){
//		TestCaseResultData testObject = new TestCaseResultData(data);
		test.setFailureDetails("We failed hard!");
		assertEquals(test.getFailureDetails(), "We failed hard!");
	}	

}
