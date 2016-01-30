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
	
	public void putPackageResult(Integer buildNumber, PackageResult packageResult){
		PackageResultData packageResultData = new PackageResultData(packageResult);
		evaluateStatusses(packageResult);
		
		addClasses(buildNumber, packageResult);
		this.buildResults.put(buildNumber, packageResultData);
	}
	
	public ResultData getPackageResult(Integer buildNumber){
		if(this.buildResults.containsKey(buildNumber)){
			return this.buildResults.get(buildNumber);
		}
		return null;		
	}

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

			String fileName = classResult.getDisplayName() + ".java";
			String filePath = getFilePath(fileName);
			setPriorities(filePath, classInfo.getTests().values());

			this.type = "Package";
			this.priority = -1;
			classes.put(className, classInfo);
		}
	}

	public void setPriorities(String filePath, Collection<TestCaseInfo> testCaseInfos) {

		Boolean includeNextLine = false;
		File file = new File(filePath);
		BufferedReader reader = null;
		Pattern priorityExpr = Pattern.compile(".*Priority (\\d+).*"); 
		int priority = -1;
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			while ((text = reader.readLine()) != null) {
				Matcher priorityMatcher = priorityExpr.matcher(text);
				if(includeNextLine) {
					for (TestCaseInfo testCaseInfo : testCaseInfos) {
						String testCase = testCaseInfo.getName();
						testCaseInfo.description = "text = " + text + "priority = " + priority + "testCase = " + testCase;
						if(text.toUpperCase().contains(testCase.toUpperCase())) {
							testCaseInfo.priority = priority;
						}
					}
					includeNextLine = false;
				}
				if(priorityMatcher.matches()) {
					includeNextLine = true;
					priority = Integer.parseInt(priorityMatcher.group(1));
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public String getFilePath(String fileName) {
		String s;
		String filePath = "";
		Process p;
		try {
			p = Runtime.getRuntime().exec(new String[]{"find", "./.jenkins/jobs/", "-name", fileName});
			BufferedReader br = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null) {
				filePath += s;
			}
			p.waitFor();
			p.destroy();
		} catch (Exception e) {
		}
		return filePath;
	}

	protected JSONObject getChildrensJson(){
		JSONObject json = new JSONObject();
		for(String className : classes.keySet()){
			json.put(className, classes.get(className).getJsonObject());
		}
		return json;
	}

}
