package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.EnvVars;
import hudson.tasks.junit.PackageResult;
import hudson.tasks.junit.ClassResult;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jenkinsci.plugins.testresultsanalyzer.result.data.PackageResultData;
import org.jenkinsci.plugins.testresultsanalyzer.result.data.ResultData;

import net.sf.json.JSONObject;

public class PackageInfo extends Info {
	protected Map<String, ClassInfo> classes = new TreeMap<String, ClassInfo>();

	/**
	 * Description of putPackageResult
	 * Insert a particular package build with its result into a packageInfo
	 * @param buildNumber - the build number for a build
	 * @param packageResult - the build result for a build
	 */
	public void putPackageResult(Integer buildNumber, PackageResult packageResult){
		PackageResultData packageResultData = new PackageResultData(packageResult);
		evaluateStatusses(packageResult);

		addClasses(buildNumber, packageResult);
		this.buildResults.put(buildNumber, packageResultData);
	}

	/**
	 * Description of getPackageResult
	 * Get a package build result for a particular build number in a packageInfo
	 * @param buildNumber - the number for which build to get
	 * @return - the build result for the build number in a packageInfo 
	 */
	public ResultData getPackageResult(Integer buildNumber){
		if(this.buildResults.containsKey(buildNumber)){
			return this.buildResults.get(buildNumber);
		}
		return null;
	}

	/**
	 * Description of addClasses
	 * Insert a particular package result for a package info
	 * @param buildNumber - the number for which build to insert
	 * @param packageResult - the build result for the build number to insert
	 */
	public void addClasses(Integer buildNumber, PackageResult packageResult){
		for (ClassResult classResult : packageResult.getChildren()) {
			String className = classResult.getName();
			ClassInfo classInfo;
			if (classes.containsKey(className)) {
				classInfo = classes.get(className);
			} else {
				classInfo = new ClassInfo();
				classInfo.setName(className);
			}
			classInfo.putBuildClassResult(buildNumber, classResult);
			this.description = "";
			this.type = "Package";
			this.priority = -1;
			classes.put(className, classInfo);
		}
	}

	protected JSONObject getChildrensJson(){
		JSONObject json = new JSONObject();
		for(String className : classes.keySet()){
			json.put(className, classes.get(className).getJsonObject());
		}
		return json;
	}

}
