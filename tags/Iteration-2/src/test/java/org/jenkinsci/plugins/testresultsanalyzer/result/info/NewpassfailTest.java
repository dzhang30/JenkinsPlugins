package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class NewpassfailTest {
	TestCaseInfo test;
	ResultData data1;
	ResultData data2;

	TestCaseInfo test1;
	ResultData data1_p;
	ResultData data2_p;

	@Before
	public void setUp() throws Exception {
		//set test
		data1 = mock(ResultData.class);
		data2 = mock(ResultData.class);
		Map<Integer,ResultData> builds = new HashMap<Integer, ResultData>();
		JSONObject temp1 = new JSONObject();
		
		Integer temp = new Integer(12);
		when(data1.getTotalFailed()).thenReturn(temp);
		when(data1.isPassed()).thenReturn(true);
		when(data1.getJsonObject()).thenReturn(temp1);
		temp = new Integer(1);
		builds.put(temp,data1);
		
		temp = new Integer(15);
		when(data2.getTotalFailed()).thenReturn(temp);
		when(data2.isPassed()).thenReturn(false);
		when(data2.getJsonObject()).thenReturn(temp1);

		temp = new Integer(2);
		builds.put(temp,data2);

		test = new TestCaseInfo();
		test.setBuildPackageResults(builds);
		
		//set test1
		data1_p = mock(ResultData.class);
		data2_p = mock(ResultData.class);
		Map<Integer,ResultData> builds1 = new HashMap<Integer, ResultData>();
		temp1 = new JSONObject();
		
		temp = new Integer(12);
		when(data1_p.getTotalFailed()).thenReturn(temp);
		when(data1_p.isPassed()).thenReturn(false);
		when(data1_p.getJsonObject()).thenReturn(temp1);
		temp = new Integer(1);
		builds1.put(temp,data1_p);
		
		temp = new Integer(15);
		when(data2_p.getTotalFailed()).thenReturn(temp);
		when(data2_p.isPassed()).thenReturn(true);
		when(data2_p.getJsonObject()).thenReturn(temp1);
		temp = new Integer(2);
		builds1.put(temp,data2_p);
		
		test1 = new TestCaseInfo();
		test1.setBuildPackageResults(builds1);
		
	}

	@Test
	public void test_total_failed() {
		JSONObject result = test.getBuildJson();
		assertEquals(((Integer)result.get("New_fail")).intValue(), 1);
	}

	@Test
	public void test_total_passed() {
		JSONObject result = test1.getBuildJson();
		assertEquals(((Integer)result.get("New_pass")).intValue(), 1);
	}

}
