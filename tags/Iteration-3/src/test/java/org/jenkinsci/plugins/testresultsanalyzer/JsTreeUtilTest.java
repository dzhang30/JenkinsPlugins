package org.jenkinsci.plugins.testresultsanalyzer;

import static org.junit.Assert.*;

import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.ResultInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class JsTreeUtilTest {

	List<Integer> build;
	boolean check = true;
	String testStatus_p = "all";
	ResultInfo resultInfo;
	JSONObject class1;
	JSONObject class2;
	JSONObject class3;

	JSONObject package1;
	JSONObject package_result;

	JSONObject package_filter;
	JSONObject package_result_filter;

	public static JSONObject buildobject(String name, String type, String description, String buildStatus, JSONObject builds)
	{
		JSONObject object = new JSONObject();
		object.put("name",name);
		object.put("type",type);
		object.put("description",description);
		object.put("buildStatuses",buildStatus);
		object.put("builds",builds);
		return object;
	}

	public static JSONObject setbuild(String status, int total_pass, int total_fail, int new_pass, int new_fail)
	{
		JSONObject build_t = new JSONObject();
		Integer tp = new Integer(total_pass);
		build_t.put("Total_pass",tp);

		Integer tf = new Integer(total_fail);
		build_t.put("Total_fail",tf);

		Integer np = new Integer(new_pass);
		build_t.put("New_pass",np);

		Integer nf = new Integer(new_fail);
		build_t.put("New_fail",nf);

		build_t.put("status", status);
		return build_t;
	}
	
	@Before
	public void setUp() throws Exception {
		build = new ArrayList();
		resultInfo = mock(ResultInfo.class);
		Integer index1 = new Integer(1);
		Integer index2 = new Integer(2);
		Integer index3 = new Integer(3);
		
		package_result = new JSONObject();
		package_result_filter = new JSONObject();
			
		//define package1
		JSONObject builds =  new JSONObject();
		package1 = buildobject("package1", "package", "test_package1", "success", builds);
		
		Integer test_f = new Integer(52);
		builds.put("Total_fail",test_f);
		
		//define class1
		JSONObject builds_c1 =  setbuild("PASSED", 11, 15, 5, 3);
		class1 = buildobject("class1", "class", "test_class1", "success", builds_c1);
		
		//define class2
		JSONObject builds_c2 =  setbuild("FAILED", 16, 18, 2, 2);
		class2 = buildobject("class2", "class", "test_class2", "success", builds_c2);
		
		//define class3
		JSONObject builds_c3 =  setbuild("NEWPASSED",25, 13, 7, 4);
		class3 = buildobject("class3", "class", "test_class3", "success", builds_c3);
		
		//set main variable
		build.add(index1);
		build.add(index2);
		build.add(index3);	
	}
	
	public String getteststring(String name, JSONObject value)
	{
		when(resultInfo.getJsonObject()).thenReturn(package_result);
		package_result.put(name,value);
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,testStatus_p,resultInfo).getJSONArray("results").getJSONObject(0);
		return tree.get("text").toString();
	}
	
	
	public JSONArray getchildlist(boolean check, String testStatus)
	{
		JSONObject children = new JSONObject();
		children.put("class1",class1);
		children.put("class2",class2);
		children.put("class3",class3);
		package1.put("children",children);
		package_result.put("package1",package1);
		when(resultInfo.getJsonObject()).thenReturn(package_result);
		
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,testStatus,resultInfo).getJSONArray("results").getJSONObject(0);
		assertEquals("package1",tree.get("text"));
		assertEquals(true,package1.containsKey("children"));
		JSONObject childrens = package1.getJSONObject("children");
		assertEquals(3,children.keySet().size());
		assertEquals(3,childrens.keySet().size());
		assertEquals(true,tree.containsKey("children"));
		JSONArray child_list = tree.getJSONArray("children");
		return child_list;
	}

	public JSONArray set_filter_test(boolean check, String testStatus)
	{
		build = new ArrayList();
		String[] status_a = new String[] {"PASSED", "FAILED", "NEWPASSED", "NEWFAILED", "FAILED", "PASSED", "NEWPASSED", "NEWFAILED"};
		JSONObject children = new JSONObject();
		JSONObject builds =  new JSONObject();

		for (int i = 0; i < 8; i++)
		{
			Integer index = new Integer(i+1);
			build.add(index);
			JSONObject status = new JSONObject();
			status.put("status", status_a[i]);
			builds.put(index.toString(), status);
		}
		//assertNotNull(builds);
		package_filter = buildobject("package1", "package", "test_package1", "success", builds);
		package_filter.put("children",children);
		//assertNotNull(package_filter);
		package_result_filter.put("package1",package_filter);
		when(resultInfo.getJsonObject()).thenReturn(package_result_filter);
		
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,testStatus,resultInfo).getJSONArray("results").getJSONObject(0);
		JSONArray build_list = tree.getJSONArray("buildResults");
		return build_list;
	}

	@Test
	public void test_generate_class1() {
		assertEquals("class1",this.getteststring("class1", class1));
	}

	@Test
	public void test_generate_class2() {
		assertEquals("class2",this.getteststring("class2", class2));
	}
	
	@Test
	public void test_generate_class3() {
		assertEquals("class3",this.getteststring("class3", class3));
	}
	
	@Test
	public void test_generate_package1() {
		assertEquals("package1",this.getteststring("package1", package1));
	}
	
	@Test
	public void test_sort_all_by_history() {
		//set up package structure
		JSONArray child_list = getchildlist(true, "all");
		 
		assertEquals("class2",child_list.getJSONObject(0).get("text"));
		assertEquals("class1",child_list.getJSONObject(1).get("text"));
		assertEquals("class3",child_list.getJSONObject(2).get("text"));
	}

	@Test
	public void test_sort_fail_by_history() {
		//set up package structure
		JSONArray child_list = getchildlist(true, "FAILED");
		 
		assertEquals("class2",child_list.getJSONObject(0).get("text"));
		assertEquals("class1",child_list.getJSONObject(1).get("text"));
		assertEquals("class3",child_list.getJSONObject(2).get("text"));
	}

	@Test
	public void test_sort_pass_by_history() {
		//set up package structure
		JSONArray child_list = getchildlist(true, "PASSED");
		 
		assertEquals("class3",child_list.getJSONObject(0).get("text"));
		assertEquals("class2",child_list.getJSONObject(1).get("text"));
		assertEquals("class1",child_list.getJSONObject(2).get("text"));
	}

	@Test
	public void test_sort_newpass_by_history() {
		//set up package structure
		JSONArray child_list = getchildlist(true, "NEWPASSED");
		 
		assertEquals("class3",child_list.getJSONObject(0).get("text"));
		assertEquals("class1",child_list.getJSONObject(1).get("text"));
		assertEquals("class2",child_list.getJSONObject(2).get("text"));
	}

	@Test
	public void test_sort_newfail_by_history() {
		//set up package structure
		JSONArray child_list = getchildlist(true, "NEWFAILED");
		 
		assertEquals("class3",child_list.getJSONObject(0).get("text"));
		assertEquals("class1",child_list.getJSONObject(1).get("text"));
		assertEquals("class2",child_list.getJSONObject(2).get("text"));
	}
	
	@Test
	public void test_filter_fail() {
		//set up package structure
		JSONArray filter_list = set_filter_test(false, "FAILED");
		JSONObject temp = new JSONObject();
		for (int i = 0; i < 8; i ++)
		{
			if (i != 1 && i != 3 && i != 4 && i != 7)
				assertEquals(temp, filter_list.getJSONObject(i));
		}
		assertEquals("2",filter_list.getJSONObject(1).getString("buildNumber"));
		assertEquals("4",filter_list.getJSONObject(3).getString("buildNumber"));
		assertEquals("5",filter_list.getJSONObject(4).getString("buildNumber"));
		assertEquals("8",filter_list.getJSONObject(7).getString("buildNumber"));
	}

	@Test
	public void test_filter_pass() {
		//set up package structure
		JSONArray filter_list = set_filter_test(false, "PASSED");
		JSONObject temp = new JSONObject();
		for (int i = 0; i < 8; i ++)
		{
			if (i != 0 && i != 2 && i != 5 && i != 6)
				assertEquals(temp, filter_list.getJSONObject(i));
		}
		assertEquals("1",filter_list.getJSONObject(0).getString("buildNumber"));
		assertEquals("3",filter_list.getJSONObject(2).getString("buildNumber"));
		assertEquals("6",filter_list.getJSONObject(5).getString("buildNumber"));
		assertEquals("7",filter_list.getJSONObject(6).getString("buildNumber"));
	}

	@Test
	public void test_filter_newpass() {
		//set up package structure
		JSONArray filter_list = set_filter_test(false, "NEWPASSED");
		JSONObject temp = new JSONObject();
		for (int i = 0; i < 8; i ++)
		{
			if (i != 6 && i != 2)
				assertEquals(temp, filter_list.getJSONObject(i));
		}
		assertEquals("3",filter_list.getJSONObject(2).getString("buildNumber"));
		assertEquals("7",filter_list.getJSONObject(6).getString("buildNumber"));
	}

	@Test
	public void test_filter_newfail() {
		//set up package structure
		JSONArray filter_list = set_filter_test(false, "NEWFAILED");
		JSONObject temp = new JSONObject();
		for (int i = 0; i < 8; i ++)
		{
			if (i != 3 && i != 7)
				assertEquals(temp, filter_list.getJSONObject(i));
		}
		assertEquals("4",filter_list.getJSONObject(3).getString("buildNumber"));
		assertEquals("8",filter_list.getJSONObject(7).getString("buildNumber"));
	}

	@Test
	public void test_sort_all_by_normal() {
		JSONArray child_list = getchildlist(false, "all");
		 
		assertEquals("class1",child_list.getJSONObject(0).get("text"));
		assertEquals("class2",child_list.getJSONObject(1).get("text"));
		assertEquals("class3",child_list.getJSONObject(2).get("text"));
	}
}
