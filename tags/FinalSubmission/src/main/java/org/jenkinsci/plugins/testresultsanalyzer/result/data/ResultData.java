package org.jenkinsci.plugins.testresultsanalyzer.result.data;

import hudson.tasks.junit.PackageResult;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class ResultData {
	private String name;
	private String description;
	private boolean isPassed;
	private boolean isSkipped;
	private transient PackageResult packageResult;
	private int totalTests;
	private int totalFailed;
	private int totalPassed;
	private int totalSkipped;
	private List<ResultData> children = new ArrayList<ResultData>();
	private float totalTimeTaken;
	private String status;

	private String failureDetails = new String();


	/**
	 * Description of getFailureDetails
	 * get the failing details for a particular test case
	 * @return String - failing details for a test case  
	 */
	public String getFailureDetails(){
		return failureDetails;
	}

	/**
	 * Description of setFailureDetails
	 * set a test case failing reason to a particular string
	 * @param failureDetails the reason user want to set for a test
	 */
	public void setFailureDetails(String failureDetails){
		if (failureDetails != null)
			this.failureDetails = failureDetails;
	}

	/**
	 * Description of getName
	 * get name for a particular test/class/package
	 * @return String - name for a test/class/package
	 */
	public String getName() {
		return name;
	}

	/**
	 * Description of setName
	 * set name for a particular test/class/package
	 * @param the name user want give for a test/class/package
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Description of getDescription
	 * get user defined description for a particular test/class/package
	 * @return String - user defined description for a particular test/class/package
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Description of setDescription
	 * set user defined description to a string
	 * @param description description user want set for a particular test/class/package
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Description of isPassed()
	 * Check whether a particular test/class/package is passed or not
	 * @return boolean - whether a particular test/class/package pass or not
	 */
	public boolean isPassed() {
		return isPassed;
	}

	/**
	 * Description of setPassed
	 * set a particular test/class/package pass or not pass
	 * @param isPassed the pass value user want set for a particular test/class/package
	 */
	public void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}

	/**
	 * Description of isSkipped
	 * Check whether a particular test/class/package skipped or not
	 * @return boolean - whether a particular test/class/package passed or not
	 */
	public boolean isSkipped() {
		return isSkipped;
	}

	/**
	 * Description of setSkipped 
	 * set a particular test/class/package skipped or not
	 * @param the skip value user want to set for a particular test/class/package
	 */
	public void setSkipped(boolean isSkipped) {
		this.isSkipped = isSkipped;
	}

	/**
	 * Description of getPackageResult
	 * get a particular package's test result
	 * @return the package result user want to get
	 */
	public PackageResult getPackageResult() {
		return packageResult;
	}

	/**
	 * Description of setPackageResult
	 * set a particular package's test result
	 * @param packageResult - the package result user want set
	 */
	public void setPackageResult(PackageResult packageResult) {
		this.packageResult = packageResult;
	}

	/**
	 * Description of getTotalTests
	 * get total test number for a particular test/class/package
	 * @return int - total number for a particular test/class/package
	 */
	public int getTotalTests() {
		return totalTests;
	}

	/**
	 * Description of setTotalTest
	 * set total test number for a particular test/class/package
	 * @param totalTests - the number to set a particular test/class/package
	 */
	public void setTotalTests(int totalTests) {
		this.totalTests = totalTests;
	}

	/**
	 * Description of getTotalFailed
	 * get total failing test number for a particular test/class/package
	 * @return int - total failing test number for a particular test/class/package
	 */
	public int getTotalFailed() {
		return totalFailed;
	}

	/**
	 * Description of setTotalFailed
	 * set total failing test number for a particular test/class/package
	 * @param totalFailed - the number to set total failing test number
	 */
	public void setTotalFailed(int totalFailed) {
		this.totalFailed = totalFailed;
	}

	/**
	 * Description of getTotalPassed
	 * get total passing test number for a particular test/class/package
	 * @return total passing test number for a particular test/class/package
	 */
	public int getTotalPassed() {
		return totalPassed;
	}

	/**
	 * Description of setTotalPassed
	 * set total passing test number for a particular test/class/package
	 * @param int - the number to set total passing test number
	 */
	public void setTotalPassed(int totalPassed) {
		this.totalPassed = totalPassed;
	}

	/**
	 * Description of setTotalSkipped
	 * get total skipped test number for a particular test/class/package
	 * @return int - thetotal skipped test number for a particular test/class/package
	 */
	public int getTotalSkipped() {
		return totalSkipped;
	}

	/**
	 * Description of setTotalSkipped
	 * set total skipped test number for a particular test/class/package
	 * @param totalSkipped - the number to set total skipped test number
	 */
	public void setTotalSkipped(int totalSkipped) {
		this.totalSkipped = totalSkipped;
	}

	/**
	 * Description of getChildren
	 * get the Children for a package/class
	 * for a package, the children are the classes under this package
	 * for a class, the children are the test cases that under this class
	 * @return List<ResultData> - a list of children for a package/class
	 */
	public List<ResultData> getChildren() {
		return this.children;
	}

	/**
	 * Description of addChildResult
	 * add a childResultData for a package/class
	 * @param childResultData - the childResultData to add for a package/class
	 */
	public void addChildResult(ResultData childResultData) {
		this.children.add(childResultData);
	}

	/**
	 * Description of addChildResult
	 * add a list of childResultData for a package/class
	 * @param childResults - the list of childResultData to add for a package/class
	 */
	public void addChildResult(List<ResultData> childResults) {
		this.children.addAll(childResults);
	}

	/**
	 * Description of getTotalTimeTaken
	 * get the spending time for running a particular test/class/package
	 * @return float - the time for running a particular test/class/package
	 */
	public float getTotalTimeTaken() {
		return totalTimeTaken;
	}

	/**
	 * Description of setTotalTimeTaken
	 * set the total running time for a particular test/class/package
	 * @param totalTimeTaken - the total running time to set a particular test/class/package
	 */
	public void setTotalTimeTaken(float totalTimeTaken) {
		this.totalTimeTaken = totalTimeTaken;
	}

	/**
	 * A method that sets status field based on isSkipped or isPassed fields.
	 */
	protected void evaluateStatus(){
		if(isSkipped){
			status = "SKIPPED";
		} else if(isPassed){
			status = "PASSED";
		} else {
			status = "FAILED";
		}		
	}

	/**
	 * Description of setStatus
	 * set status for a particular test/class/package
	 * @param set_status - the status to set for a particular test/class/package
	 */
	public void setStatus(String set_status){
		status = set_status;
	}

	/**
	 * Description of getStatus
	 * get status for a particular test/class/package
	 * @return String - the status for a particular test/class/package
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Description of getJsonObject
	 * create a JsonObject for a particular test/class/package
	 * @return JSONObject - the JsonObject create based on a particular test/class/package
	 */
	public JSONObject getJsonObject(){
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("description", description);
		json.put("totalTests", totalTests);
		json.put("totalFailed", totalFailed);
		json.put("totalPassed", totalPassed);
		json.put("totalSkipped", totalSkipped);
		json.put("isPassed", isPassed);
		json.put("isSkipped", isSkipped);
		json.put("totalTimeTaken", totalTimeTaken);
		json.put("status", status);
		JSONArray testsChildren = new JSONArray();
		for(ResultData childResult : children){
			testsChildren.add(childResult.getJsonObject());
		}

		if(!(isPassed) && !isSkipped)
			json.put("failureDetails", failureDetails);

		json.put("children", testsChildren);
		return json;
	}

}
