package org.jenkinsci.plugins.testresultsanalyzer.result.data;


import hudson.tasks.junit.PackageResult;

public class PackageResultData extends ResultData{

	/**
	 * Description of packageResultData
	 * The constructor for PackageResultData	
	 * @param the PackageResult need to be stored in PackageResultData Object 
	 */
	public PackageResultData(PackageResult packageResult){
		setName(packageResult.getName());
		setDescription("Package");
		setPassed(packageResult.getFailCount()==0);
		setSkipped(packageResult.getSkipCount() == packageResult.getTotalCount());
		setTotalTests(packageResult.getTotalCount());
		setTotalFailed(packageResult.getFailCount());
		setTotalPassed(packageResult.getPassCount());
		setTotalSkipped(packageResult.getSkipCount());
		setTotalTimeTaken(packageResult.getDuration());
		evaluateStatus();
	}

}
