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
	
	TestCaseInfo test_c;

	public static ResultData setdata(int t, boolean ispass)
	{
		ResultData data = mock(ResultData.class);
		JSONObject temp1 = new JSONObject();
		Integer temp = new Integer(t);
		when(data.isPassed()).thenReturn(ispass);
		when(data.isSkipped()).thenReturn(false);
		when(data.getTotalFailed()).thenReturn(temp);
		when(data.getJsonObject()).thenReturn(temp1);
		return data;
	}
	
	@Before
	public void setUp() throws Exception {
		//set test
		data1 = setdata(12, true);
		data2 = setdata(15, false);
		Map<Integer,ResultData> builds = new HashMap<Integer, ResultData>();
		JSONObject temp1 = new JSONObject();
		
		Integer temp = new Integer(1);
		builds.put(temp,data1);

		temp = new Integer(2);
		builds.put(temp,data2);

		test = new TestCaseInfo();
		test.setBuildPackageResults(builds);
		
		//set test1
		data1_p = setdata(12, false);
		data2_p = setdata(15, true);
		Map<Integer,ResultData> builds1 = new HashMap<Integer, ResultData>();
		temp1 = new JSONObject();
		
		temp = new Integer(1);
		builds1.put(temp,data1_p);
		
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
	
	@Test
	public void test_total_combine() {
		boolean[] testca = new boolean[] {false, false, false, true, true, false, true, false, false, true, true};

		Map<Integer,ResultData> builds = new HashMap<Integer, ResultData>();
		JSONObject temp1 = new JSONObject();
		
		for (int i = 0; i < 11; i++)
		{
			Integer temp = new Integer(i+1);
			ResultData test_datac = setdata(12, testca[i]);
			builds.put(temp,test_datac);
		}
		
		test_c = new TestCaseInfo();
		test_c.setBuildPackageResults(builds);
		JSONObject result = test_c.getBuildJson();
		assertEquals(((Integer)result.get("New_fail")).intValue(), 2);
		assertEquals(((Integer)result.get("New_pass")).intValue(), 3);
		assertEquals(((Integer)result.get("Total_pass")).intValue(), 5);
		assertEquals(((Integer)result.get("Total_fail")).intValue(), 6);
	}

}
