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
	ResultInfo resultInfo;
	JSONObject class1;
	JSONObject class2;
	JSONObject class3;
	JSONObject package1;
	JSONObject package_result;


	@Before
	public void setUp() throws Exception {
		build = new ArrayList();
		resultInfo = mock(ResultInfo.class);
		Integer index1 = new Integer(1);
		Integer index2 = new Integer(2);
		Integer index3 = new Integer(3);
		
		package_result = new JSONObject();
		package1 = new JSONObject();
		class1 = new JSONObject();
		class2 = new JSONObject();
		class3 = new JSONObject();
			
		//define package1
		package1.put("name","package1");
		package1.put("type","package");
		package1.put("description","test_package1");
		package1.put("buildStatuses","success");
		JSONObject builds =  new JSONObject();
		package1.put("builds",builds);
		
		Integer test_f = new Integer(27);
		builds.put("Total_fail",test_f);
		
		//define class1
		class1.put("name","class1");
		class1.put("type","class");
		class1.put("description","test_class1");
		class1.put("buildStatuses","success");
		JSONObject builds_c1 =  new JSONObject();
		test_f = new Integer(11);
		builds_c1.put("Total_fail",test_f);
		class1.put("builds",builds_c1);
		
		
		//define class2
		class2.put("name","class2");
		class2.put("type","class");
		class2.put("description","test_class2");
		class2.put("buildStatuses","success");
		JSONObject builds_c2 =  new JSONObject();
		test_f = new Integer(16);
		builds_c2.put("Total_fail",test_f);
		class2.put("builds",builds_c2);
	
		//define class3
		class3.put("name","class3");
		class3.put("type","class");
		class3.put("description","test_class3");
		class3.put("buildStatuses","success");
		JSONObject builds_c3 =  new JSONObject();
		test_f = new Integer(25);
		builds_c3.put("Total_fail",test_f);
		class3.put("builds",builds_c3);
		
		
		//set main variable
		build.add(index1);
		build.add(index2);
		build.add(index3);
		
		
	}

	@Test
	public void test_generate_class1() {
		package_result.put("class1",class1);
		when(resultInfo.getJsonObject()).thenReturn(package_result);
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,resultInfo).getJSONArray("results").getJSONObject(0);
		assertEquals("class1",tree.get("text"));
	}

	@Test
	public void test_generate_class2() {
		package_result.put("class2",class2);
		when(resultInfo.getJsonObject()).thenReturn(package_result);
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,resultInfo).getJSONArray("results").getJSONObject(0);
		assertEquals("class2",tree.get("text"));
	}
	
	@Test
	public void test_generate_class3() {
		package_result.put("class3",class3);
		when(resultInfo.getJsonObject()).thenReturn(package_result);
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,resultInfo).getJSONArray("results").getJSONObject(0);
		assertEquals("class3",tree.get("text"));
	}
	
	@Test
	public void test_generate_package1() {
		package_result.put("package1",package1);
		when(resultInfo.getJsonObject()).thenReturn(package_result);
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,resultInfo).getJSONArray("results").getJSONObject(0);
		assertEquals("package1",tree.get("text"));
	}
	
	@Test
	public void test_sort_by_history() {
		//set up package structure
		check = true;
		JSONObject children = new JSONObject();
		children.put("class1",class1);
		children.put("class2",class2);
		children.put("class3",class3);
		package1.put("children",children);
		package_result.put("package1",package1);
		when(resultInfo.getJsonObject()).thenReturn(package_result);
		
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,resultInfo).getJSONArray("results").getJSONObject(0);
		assertEquals("package1",tree.get("text"));
		assertEquals(true,package1.containsKey("children"));
		JSONObject childrens = package1.getJSONObject("children");
		assertEquals(3,children.keySet().size());
		assertEquals(3,childrens.keySet().size());
		assertEquals(true,tree.containsKey("children"));
		JSONArray child_list = tree.getJSONArray("children");
		 
		assertEquals("class3",child_list.getJSONObject(0).get("text"));
		assertEquals("class2",child_list.getJSONObject(1).get("text"));
		assertEquals("class1",child_list.getJSONObject(2).get("text"));
	}
	
	@Test
	public void test_sort_by_normal() {
		check = false;
		JSONObject children = new JSONObject();
		children.put("class1",class1);
		children.put("class2",class2);
		children.put("class3",class3);
		package1.put("children",children);
		package_result.put("package1",package1);
		when(resultInfo.getJsonObject()).thenReturn(package_result);
		
		JsTreeUtil temp_tree = new JsTreeUtil();
		JSONObject tree = temp_tree.getJsTree(build,check,resultInfo).getJSONArray("results").getJSONObject(0);
		assertEquals("package1",tree.get("text"));
		assertEquals(true,package1.containsKey("children"));
		JSONObject childrens = package1.getJSONObject("children");
		assertEquals(3,children.keySet().size());
		assertEquals(3,childrens.keySet().size());
		assertEquals(true,tree.containsKey("children"));
		JSONArray child_list = tree.getJSONArray("children");
		 
		assertEquals("class1",child_list.getJSONObject(0).get("text"));
		assertEquals("class2",child_list.getJSONObject(1).get("text"));
		assertEquals("class3",child_list.getJSONObject(2).get("text"));
	}	
}
