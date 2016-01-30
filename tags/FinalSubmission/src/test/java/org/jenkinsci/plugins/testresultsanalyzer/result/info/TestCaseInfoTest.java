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

public class TestCaseInfoTest {
	TestCaseInfo test;

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

		Map<Integer,ResultData> builds = new HashMap<Integer, ResultData>();

		ResultData data1 = setdata(12, false);
		Integer temp = new Integer(1);
		builds.put(temp,data1);

		ResultData data2 = setdata(15, false);
		temp = new Integer(2);
		builds.put(temp,data2);

		test = new TestCaseInfo();
		test.setBuildPackageResults(builds);

	}

	@Test
	public void test_total_failed() {
		JSONObject result = test.getBuildJson();
		Integer total = (Integer)(result.get("Total_fail"));
		assertEquals(total.intValue(), (int)2);
	}

}
