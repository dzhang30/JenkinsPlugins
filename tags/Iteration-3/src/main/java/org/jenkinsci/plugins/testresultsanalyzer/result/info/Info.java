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
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Map<Integer, ResultData> getBuildPackageResults() {
		return buildResults;
	}
	public void setBuildPackageResults(Map<Integer, ResultData> buildResults) {
		this.buildResults = buildResults;
	}
	
	protected abstract JSONObject getChildrensJson();
	
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
		for(Integer buildNumber : buildResults.keySet()){
			// when prev is not -1, so when there is more than one build
			ResultData curr_result = buildResults.get(buildNumber);
			if (prev.intValue() != -1)
			{
				//get the previous build result
				ResultData prev_result = buildResults.get(prev);
				//get the current build result
				//comepare the result with previous result and put in new status
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
			if (curr_result.isPassed())
			{
				total_p += 1;
			}
			else if (!curr_result.isPassed() && !curr_result.isSkipped())
			{
				total_f += 1;
			}
			//counting total failed crossing builds
			json.put(buildNumber.toString(), buildResults.get(buildNumber).getJsonObject());
			//int total_fail_1 = buildResults.get(buildNumber).getTotalFailed()+total_fail.intValue();
			//total_fail = new Integer(total_fail_1);
			//int total_pass_1 = buildResults.get(buildNumber).getTotalPassed()+total_pass.intValue();
			//total_pass = new Integer(total_pass_1);
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
	
	
	public JSONArray getBuildStatuses(){
		JSONArray buildStatuses = new JSONArray();
		buildStatuses.addAll(statuses);
		return buildStatuses;
	}
	
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
