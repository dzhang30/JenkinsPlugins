package org.jenkinsci.plugins.testresultsanalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;

import net.sf.json.JSONObject;
import org.jenkinsci.plugins.testresultsanalyzer.result.info.ResultInfo;
import org.kohsuke.stapler.bind.JavaScriptMethod;


import jenkins.model.Jenkins;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.Run;
import hudson.security.Permission;
import hudson.tasks.junit.PackageResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.util.RunList;

public class TestResultsAnalyzerAction extends Actionable implements Action{
	@SuppressWarnings("rawtypes") AbstractProject project;
	private List<Integer> builds = new ArrayList<Integer>() ;

	ResultInfo resultInfo;
	/**
	 * Description of TestResultAnalyzerAction
	 * Constructor for TestResultAnalyzerAction
	 * @param project - the project that action will observe
	 */
	public TestResultsAnalyzerAction(@SuppressWarnings("rawtypes") AbstractProject project){
		this.project = project;
	}


	/**
	 * The display name for the action.
	 * 
	 * @return the name as String
	 */
	public final String getDisplayName() {
		return this.hasPermission() ? Constants.NAME : null;
	}

	/**
	 * The icon for this action.
	 * 
	 * @return the icon file as String
	 */
	public final String getIconFileName() {
		return this.hasPermission() ? Constants.ICONFILENAME : null;
	}

	/**
	 * The url for this action.
	 * 
	 * @return the url as String
	 */
	public String getUrlName() {
		return this.hasPermission() ? Constants.URL : null;
	}

	/**
	 * Search url for this action.
	 * 
	 * @return the url as String
	 */
	public String getSearchUrl() {
		return this.hasPermission() ? Constants.URL : null;
	}

	/**
	 * Checks if the user has CONFIGURE permission.
	 * 
	 * @return true - user has permission, false - no permission.
	 */
	private boolean hasPermission() {
		return project.hasPermission(Item.READ);
	}

	/**
	 * Returns the project field.
	 *
	 * @return project - the current AbstractProject instance.
	 */
	@SuppressWarnings("rawtypes")
	public AbstractProject getProject(){
		return this.project;
	}


	/**
	 * Description of getNoOfBuilds
	 * Get all need builds within the demand amount
	 * @param noOfbuildsNeeded - the amount for how many builds to get
	 * @return JSONArray - a list contains all need builds
	 */
	@JavaScriptMethod
	public JSONArray getNoOfBuilds(String noOfbuildsNeeded) {
		JSONArray jsonArray;
		int noOfBuilds = getNoOfBuildRequired(noOfbuildsNeeded);

		jsonArray = getBuildsArray(getBuildList(noOfBuilds));

		return jsonArray;
	}

	/**
	 * Collects all of the builds from buildList and returns them in JSON format.
	 * @param buildList - a list of build numbers to be collected and put into the JSONArray.
	 * @return jsonArray - a JSONArray with all of the builds in buildList included.
	 */
	private JSONArray getBuildsArray(List<Integer> buildList) {
		JSONArray jsonArray = new JSONArray();
		for (Integer build : buildList) {
			jsonArray.add(build);
		}
		return jsonArray;
	}

	/**
	 * Returns the acceptable build numbers given the number of builds.
	 *
	 * @param noOfBuilds - an integer containing the number of builds desired
	 * @return a List<Integer> containing the number of builds counting from noOfBuilds - 1 to zero.
	 */
	private List<Integer> getBuildList(int noOfBuilds) {
		if ((noOfBuilds <= 0) || (noOfBuilds >= builds.size())) {
			return builds;
		}
		List<Integer> buildList = new ArrayList<Integer>();
		for (int i = (noOfBuilds - 1); i >= 0; i--) {
			buildList.add(builds.get(i));
		}
		return buildList;
	}
	
	/**
	 * Parses a string into an integer to be used by other help methods.
	 * @param noOfBuildsNeeded - the string input to be parsed into an integer.
	 * @return an integer that is noOfBuildsNeeded - 1.
	 */
	private int getNoOfBuildRequired(String noOfBuildsNeeded){
		int noOfBuilds;
		try {
			noOfBuilds = Integer.parseInt(noOfBuildsNeeded);
		} catch (NumberFormatException e) {
			noOfBuilds = -1;
		}
		return noOfBuilds;
	}

	/**
	 * Description of getSelectBuilds
	 * This function takes in a string input of which specific builds the user wants to view
	 * It will parse individual builds with commas, or a range of builds with hyphens -
	 * @param selectBuild - String input form
	 * @return ArrayList of which builds to display
	 */
	public static List<Integer> getSelectBuilds(String selectBuild, List<Integer> buildList){
		if (selectBuild.equalsIgnoreCase("all") || selectBuild.equals("") || selectBuild.equals("-1"))
			return null;
		else {

			String input = selectBuild.replaceAll("\\s+",""); //Remove all white space
			String[] splitInput = input.split(",");
			ArrayList<Integer> finalList = new ArrayList<Integer>();

			for (int i = 0; i < splitInput.length; i++) {

				try {
					int parse = Integer.parseInt(splitInput[i]);

					if (parse > 0 && !finalList.contains(parse) && buildList.contains(parse))
						finalList.add(parse);
				} catch (NumberFormatException e) {
					if (splitInput[i].contains("-")) {
						try {
							int number1 = -1, number2 = -1;

							String[] splitRange = splitInput[i].split("-");

							number1 = Integer.parseInt(splitRange[0]);
							number2 = Integer.parseInt(splitRange[1]);

							if (number1 < number2)
								for (int j = number1; j <= number2; j++)
									if (!finalList.contains(j) && buildList.contains(j))
										finalList.add(j);

						} catch (Exception ex) {}
					}
				}
			}
			Collections.sort(finalList);
			return (finalList.isEmpty()) ? null : finalList;
		}
	}

	/**
	 * Description of isUpdated
	 * Check whether a project has been update or build recently
	 * @return boolean - whether a project has been build recently
	 */
	public boolean isUpdated(){
		int latestBuildNumber = project.getLastBuild().getNumber();
		return !(builds.contains(latestBuildNumber));
	}

	/**
	 * Description of getJsonLoadData
	 * Go through all builds to create a resultInfo for the package 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getJsonLoadData() {
		if (isUpdated()) {
			resultInfo = new ResultInfo();
			builds = new ArrayList<Integer>();
			RunList<Run> runs = project.getBuilds();
			Iterator<Run> runIterator = runs.iterator();
			while (runIterator.hasNext()) {
				Run run = runIterator.next();
				int buildNumber = run.getNumber();
				builds.add(run.getNumber());
				List<AbstractTestResultAction> testActions = run.getActions(hudson.tasks.test.AbstractTestResultAction.class);
				for (hudson.tasks.test.AbstractTestResultAction testAction : testActions) {
					TestResult testResult = (TestResult) testAction.getResult();
					Collection<PackageResult> packageResults = testResult.getChildren();
					for (PackageResult packageResult : packageResults) { 
						resultInfo.addPackage(buildNumber, packageResult);
					}
				}
			}
			resultInfo.setPrioritiesAndComments();

		}
	}

	/**
	 * Description of getTreeResult
	 * Using the resultInfo for the package to create Tree structure result data
	 * @param noOfBuildsNeeded - number of builds need to create tree structure result data
	 * @param teststatus - the particular test status need to store in the tree structure
	 * @param check - whether the data need sort by history
	 * @return JSONObject - the tree structure created by this package result Info
	 */
	@JavaScriptMethod
	public JSONObject getTreeResult(String noOfBuildsNeeded, String selectBuild, String teststatus, boolean check) {
		int noOfBuilds = getNoOfBuildRequired(noOfBuildsNeeded);
		List<Integer> buildList = getBuildList(noOfBuilds);
		List<Integer> selectedBuilds = getSelectBuilds(selectBuild, buildList);
		JsTreeUtil jsTreeUtils = new JsTreeUtil();
		return jsTreeUtils.getJsTree(buildList, check, teststatus, resultInfo, selectedBuilds);

	}
}
