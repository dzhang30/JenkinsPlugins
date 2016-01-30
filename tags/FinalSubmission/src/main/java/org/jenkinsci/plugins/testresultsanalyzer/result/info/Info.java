package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.test.TestResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;

public abstract class Info {


	protected String name, description, type;
	protected int priority;
	protected Map<Integer,ResultData> buildResults = new HashMap<Integer, ResultData>();

	protected List<String> statuses = new ArrayList<String>(); 

	/**
	 * Description of getName
	 * get name for a particular test/class/package info
	 * @return String - name for a particular test/class/package info
	 */
	public String getName() {
		return name;
	}

	/**
	 * Description of setName
	 * set name for a particular test/class/package info
	 * @param name - name to set a particular test/class/package info
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Description of getDescription
	 * get description for a particular test/class/package info
	 * @return String - description for a particular test/class/package info
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Description of setDescription
	 * set description for a particular test/class/package info
	 * @param description - the description to set a particular test/class/package info
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Description of getType
	 * get type for a particular test/class/package info
	 * @return String - the type for a particular test/class/package info
	 */
	public String getType() {
		return type;
	}

	/**
	 * Description of setType
	 * set type for a particular test/class/package info
	 * @param type - the type to set a particular test/class/package info
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Description of getPriority
	 * get priority for a particular test/class/package info
	 * @return int - the priority for a particular test/class/package info
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Description of getPriority
	 * set priority for a particular test/class/package info
	 * @param priority - the priority to set a particular test/class/package info
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Description of getBuildPackageResults
	 * get all package results from a particular package info
	 * Integer means build number
	 * @return Map<Integer, ResultData> - all package results for each build for a particular package info
	 */
	public Map<Integer, ResultData> getBuildPackageResults() {
		return buildResults;
	}

	/**
	 * Description of setBuildPackageResults
	 * set all package results for a particular package info
	 * @param buildResults - the buildResults to set for a particular package info
	 */
	public void setBuildPackageResults(Map<Integer, ResultData> buildResults) {
		this.buildResults = buildResults;
	}

	protected abstract JSONObject getChildrensJson();

	/**
	 * Description of getBuildJson
	 * Generates the JSON object that contains information about total pass/fail and newly passing and failing tests
	 * @return JSONObject with fields total_pass, total_fail, new_pass, new_fail
	 */
	protected JSONObject getBuildJson(){
		JSONObject json = new JSONObject();
		Integer total_fail = new Integer(0);
		Integer total_pass = new Integer(0);

		//creating a integer to hold the previous number
		//starting at -1 so that if it's the only build, we don't compare
		int total_newf = 0;
		int total_newp = 0;
		int total_p = 0;
		int total_f = 0;
		Integer prev = new Integer(-1);
		// loop through buildNumbers to decide if test is newly passing or newly failing
		for(Integer buildNumber : buildResults.keySet()){
			// when prev is not -1, so when there is more than one build
			ResultData curr_result = buildResults.get(buildNumber);
			if (prev.intValue() != -1)
			{
				//get the previous build result
				ResultData prev_result = buildResults.get(prev);
				//get the current build result
				//compare the result with previous result and put in new status
				if (prev_result.isPassed() && !curr_result.isPassed() && !curr_result.isSkipped())
				{
					total_newf += 1;
					curr_result.setStatus("NEWFAILED");
				}
				else if (!prev_result.isPassed() && curr_result.isPassed() && !prev_result.isSkipped())
				{
					total_newp += 1;
					curr_result.setStatus("NEWPASSED");
				}
			}
			//counting total passes crossing builds
			if (curr_result.isPassed())
			{
				total_p += 1;
			}
			//counting total fails crossing builds
			else if (!curr_result.isPassed() && !curr_result.isSkipped())
			{
				total_f += 1;
			}
			
			json.put(buildNumber.toString(), buildResults.get(buildNumber).getJsonObject());
			prev = new Integer(buildNumber.intValue());
		}
		Integer newfail = new Integer(total_newf);
		Integer newpass = new Integer(total_newp);
		total_fail = new Integer(total_f);
		total_pass = new Integer(total_p);
		json.put("Total_fail", total_fail);
		json.put("Total_pass", total_pass);
		json.put("New_fail", newfail);
		json.put("New_pass", newpass);
		return json;
	}

	/**
	 * Description of getBuildStatuses
	 * get all buildStatuses under a particular test/class/package info
	 * @return JSONArray - all buildStatuses under a particular test/class/package info
	 */
	public JSONArray getBuildStatuses(){
		JSONArray buildStatuses = new JSONArray();
		buildStatuses.addAll(statuses);
		return buildStatuses;
	}


	/**
	 * Description of getJsonObject
	 * create a JSONObject for a particular test/class/package info
	 * @return JSONObject - the JSONOjbect create based on a particular test/class/package info
	 */
	public JSONObject getJsonObject(){
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("description", description);
		json.put("priority", priority);
		json.put("type", type);
		json.put("buildStatuses", getBuildStatuses());
		json.put("builds", getBuildJson());
		json.put("children", getChildrensJson());
		return json;
	}

	/**
	 * Description of evaluateStatusses
	 * Takes the Passes/Fails/Skips from the last test result and compares to the overall build results
	 * All statuses from the past testResult that aren't in the overall build are added
	 * @param testResult - the TestResult object of the last build
	 */
	protected void evaluateStatusses(TestResult testResult){
		List<String> tStatusses = new ArrayList<String>();
		if (testResult.getFailCount() > 0) {
			tStatusses.add("FAILED");
		}
		if (testResult.getPassCount() > 0) {
			tStatusses.add("PASSED");
		}
		if (testResult.getSkipCount() > 0) {
			tStatusses.add("SKIPPED");
		}
		for(String tStatus: tStatusses){
			if(!(statuses.contains(tStatus))){
				statuses.add(tStatus);
			}
		}
	}

}
