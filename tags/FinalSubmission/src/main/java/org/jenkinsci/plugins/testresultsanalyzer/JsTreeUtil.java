package org.jenkinsci.plugins.testresultsanalyzer;

import java.util.List;
import java.util.*;
import java.util.Set;

import hudson.EnvVars;
import hudson.model.Run;
import hudson.util.StreamTaskListener;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.ResultInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsTreeUtil {

	/** Description of getJsTree
	 * This function will create a tree structure stores in JSONOBject which uses the infomation from all past test info
	 * If check box has been checked which means it need sort by history, it will sort the info here
	 * If teststatus has been selected for a particular test status, it will filter data here
	 * 
	 * @param builds 	A list contains each stable build number
	 * @param check 	Whether the check box for "sorted by history has been checked"
	 * @param teststatus	Which test status need to be shown on the table
	 * @param resultInfo	A JSONObject that contain all test result info
	 * @return	A JSONObject that contains the tree data structure 
	 */

	public JSONObject getJsTree(List<Integer> builds, boolean check, String teststatus, ResultInfo resultInfo, List<Integer> selectedBuilds) {
		JSONObject tree = new JSONObject();

		JSONArray buildJson = new JSONArray();
		if(selectedBuilds == null) {
			for (Integer buildNumber : builds) {
				buildJson.add(buildNumber.toString());
			}
		} else {
			for (Integer buildNumber : selectedBuilds) {
				buildJson.add(buildNumber.toString());
			}	
		}
		tree.put("builds", buildJson);
		JSONObject packageResults = resultInfo.getJsonObject();
		JSONArray results = new JSONArray();
		for (Object packageName : packageResults.keySet()) {
			JSONObject packageJson = packageResults.getJSONObject((String) packageName);
			
			if(selectedBuilds == null)
				results.add(createJson(builds, check, teststatus, packageJson));
			else {
				results.add(createJson(selectedBuilds, check, teststatus, packageJson));
			}
		}
		tree.put("results", results);
		return tree;
	}

	/**
	 * Description of getBaseJson
	 * Creates a JSONObject that contains all of the necessary default keys for later use
	 * @return JSONObject - the base base JSONObject created
	 */
	private JSONObject getBaseJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("text", "");
		jsonObject.put("description", "");
		//a priority of -1 means the display priority is not set by the user in the test source file.
		jsonObject.put("priority", -1);
		jsonObject.put("buildResults", new JSONArray());
		return jsonObject;
	}

	/**
	 * Description of sort_by_history
	 * Sorts the test results table in order of ascending/descending history and returns the array as a JSONObject
	 * @param len - Length of the table array
	 * @param select - The history array
	 * @param child_unsort - The unsorted child array
	 * @return - JSONArray Object of the child array sorted by history
	 */
	private static JSONArray sort_by_history(int len, ArrayList select, ArrayList child_unsort)
	{
		JSONArray children_history = new JSONArray();
		for (int i = 0; i < len; i++)
		{
			for (int j = i+1; j < len; j++)
			{
				int left = ((Integer)select.get(i)).intValue();
				int right = ((Integer)select.get(j)).intValue();
				if (left < right)
				{
					Collections.swap(select,i,j);
					Collections.swap(child_unsort,i,j);
				}
			}
		}

		for (int i = 0; i < len; i++)
		{
			children_history.add(child_unsort.get(i));
		}

		return children_history;
	}

	/**
	 * Description of sort_by_priority
	 * Sorts the test results table in order of ascending/descending priority and returns the array as a JSONObject
	 * @param len - Length of the table array
	 * @param childrens - The unsorted child array
	 * @return - JSONArray Object of the child array sorted by priority tags
	 */
	private static JSONArray sort_by_priority(int len, JSONArray childrens)
	{
		for(int i = 0; i < len; i++) {
			Object temp = ((JSONObject) childrens.get(i)).get("priority");
			if(temp != null) {
				int priority = ((Integer) temp).intValue();
				//if priority is bigger than -1, meaning it is set by users
				if (priority >= 0) {
					//reorder the data structure by the priority set by user
					JSONObject tmp = (JSONObject) childrens.set(priority-1, childrens.get(i));
					childrens.set(i, tmp);
				}
			}
		}

		return childrens;
	}

	/**
	 * Description of getbuild
	 * Generates a JSONObject of the individual build result including its Pass/Fail tag
	 * @param teststatus - Status of the previous build - Pass/Fail/Skip/New_Pass/New_Fail
	 * @param status - Status of the current build
	 * @param buildResult - JSONObject of the past build results
	 * @param buildNumber - Build number of the current build
	 * @return JSONObject containing the current build's status tag and build number
	 */
	private static JSONObject getbuild(String teststatus, String status, JSONObject buildResult, Integer buildNumber)
	{
		JSONObject build = new JSONObject();
		switch(teststatus)
		{
		case "FAILED":
			if (status.equals("NEWFAILED") || status.equals("FAILED"))
			{
				buildResult.put("buildNumber", buildNumber.toString());
				build = buildResult;
			}
			break;
		case "PASSED":
			if (status.equals("NEWPASSED") || status.equals("PASSED"))
			{
				buildResult.put("buildNumber", buildNumber.toString());
				build = buildResult;
			}
			break;
		case "SKIPPED":
			buildResult.put("buildNumber", buildNumber.toString());
			build = buildResult;
			break;
		case "NEWPASSED":
			if (status.equals("NEWPASSED"))
			{
				buildResult.put("buildNumber", buildNumber.toString());
				build = buildResult;
			}
			break;
		case "NEWFAILED":
			if (status.equals("NEWFAILED"))
			{
				buildResult.put("buildNumber", buildNumber.toString());
				build = buildResult;
			}
			break;
		case "all":
			buildResult.put("buildNumber", buildNumber.toString());
			build = buildResult;
			break;
		}
		return build;
	}

	/**
	 * Description of createJson
	 * This is the main helper function that does all of the creating of the JSON tree to be used by the plugin
	 * @param builds 	A list contains each stable build number
	 * @param check 	Whether the check box for "sorted by history has been checked"
	 * @param teststatus	Which test status need to be shown on the table
	 * @param dataJon	A JSONObject that contain all test data
	 * @return	A JSONObject that contains the tree data structure 
	 */

	private JSONObject createJson(List<Integer> builds, boolean check, String teststatus, JSONObject dataJson) {

		JSONObject baseJson = getBaseJson();
		baseJson.put("text", dataJson.get("name"));
		baseJson.put("type", dataJson.get("type"));
		baseJson.put("description", dataJson.get("description"));
		baseJson.put("priority", dataJson.get("priority"));
		baseJson.put("buildStatuses", dataJson.get("buildStatuses"));

		JSONObject packageBuilds = dataJson.getJSONObject("builds");
		JSONArray treeDataJson = new JSONArray();
		ArrayList total_fail = new ArrayList();
		ArrayList total_pass = new ArrayList();
		ArrayList new_fail = new ArrayList();
		ArrayList new_pass = new ArrayList();
		for (Integer buildNumber : builds) {
			JSONObject build = new JSONObject();
			if (packageBuilds.containsKey(buildNumber.toString())) {
				JSONObject buildResult = packageBuilds.getJSONObject(buildNumber.toString());
				String status = buildResult.getString("status");
				build = getbuild(teststatus, status, buildResult, buildNumber);
			} else {
				build.put("status", "N/A");
				build.put("buildNumber", buildNumber.toString());
			}
			treeDataJson.add(build);
		}
		baseJson.put("buildResults", treeDataJson);

		ArrayList child_unsort = new ArrayList();
		if (dataJson.containsKey("children")) {
			JSONArray childrens = new JSONArray();
			JSONObject childrenJson = dataJson.getJSONObject("children");
			@SuppressWarnings("unchecked")
			Set<String> childeSet = (Set<String>) childrenJson.keySet();
			for (String childName : childeSet) {
				JSONObject t = createJson(builds, check, teststatus, childrenJson.getJSONObject(childName));
				childrens.add(t);
				//get child test history
				JSONObject child_temp = childrenJson.getJSONObject(childName);
				child_unsort.add(t);
				total_fail.add((Integer)(child_temp.getJSONObject("builds").get("Total_fail")));
				total_pass.add((Integer)(child_temp.getJSONObject("builds").get("Total_pass")));
				new_fail.add((Integer)(child_temp.getJSONObject("builds").get("New_fail")));
				new_pass.add((Integer)(child_temp.getJSONObject("builds").get("New_pass")));
			}

			int len = childrens.size();
			//The selected items for sort by history function to use
			ArrayList select = new ArrayList();
			select = total_fail;
			switch(teststatus)
			{
			case "PASSED":
				select = total_pass;
				break;
			case "FAILED":
				select = total_fail;
				break;
			case "NEWFAILED":
				select = new_fail;
				break;
			case "NEWPASSED":
				select = new_pass;
				break;
			};

			JSONArray children_priority = sort_by_priority(len, childrens);        
			JSONArray children_history = sort_by_history(len, select, child_unsort);
			if (check)
				baseJson.put("children", children_history);
			else
				baseJson.put("children", children_priority);
		}

		return baseJson;
	}
}
