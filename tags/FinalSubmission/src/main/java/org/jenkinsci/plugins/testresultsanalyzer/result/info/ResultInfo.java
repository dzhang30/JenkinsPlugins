package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import hudson.tasks.junit.PackageResult;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

public class ResultInfo {

    private Map<String, PackageInfo> packageResults = new TreeMap<String, PackageInfo>();

    /**
     * Description of addPackage
     * Add a particular package result to ResultInfo object
     *
     * @param buildNumber   - the build number for the insert packageresult
     * @param packageResult - the package result want to insert
     */
    public void addPackage(Integer buildNumber, PackageResult packageResult) {
        String packageName = packageResult.getName();
        PackageInfo packageInfo;
        if (packageResults.containsKey(packageName)) {
            packageInfo = packageResults.get(packageName);
        } else {
            packageInfo = new PackageInfo();
            packageInfo.setName(packageName);

        }
        packageInfo.putPackageResult(buildNumber, packageResult);
        packageResults.put(packageName, packageInfo);
    }

    /**
     * Description of getJsonObject
     * Get JSONObject for a particular ResultInfo object
     *
     * @return JSONObject - a JSONObject created based on the resultInfo object
     */
    public JSONObject getJsonObject() {
        JSONObject json = new JSONObject();
        for (String packageName : packageResults.keySet()) {
            json.put(packageName, packageResults.get(packageName).getJsonObject());
        }
        return json;
    }

    /**
     * Description of getPackageResults
     * Get all package result for a resultInfo object
     *
     * @return Map<String,PackageInfo> - all build number with its package result for a resultInfo object
     */
    public Map<String, PackageInfo> getPackageResults() {
        return this.packageResults;
    }

    /**
     * Description of setPrioritiesAndComments
     * Method that begins the process of setting priorities and getting the comment tags for resultInfo object.
     */
    public void setPrioritiesAndComments() {
        for (PackageInfo packageInfo : packageResults.values()) {
            for (ClassInfo classInfo : packageInfo.classes.values()) {
                String fileName = classInfo.getName() + ".java";
                String fromDirectory = System.getProperty("user.home") + "/.jenkins/jobs/";
                String filePath = getFilePath(fileName, fromDirectory);
                setPriorities(filePath, classInfo.getTests().values(), classInfo);
                getCommentTags(filePath, classInfo.getTests().values(), classInfo);
            }
        }
    }

    /**
     * Description of setPriorities
     * Set priority to a particular testcase based on the user's criteria
     * @param filePath - the URL path to a particular test class
     * @param testCaseInfos - the test case info for a particular test class
     * @param classInfo - the testClass for which you are getting the comment tags
     */
    public void setPriorities(String filePath, Collection<TestCaseInfo> testCaseInfos, ClassInfo classInfo) {
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
            classInfo.description += " FileNotFoundException(setPriorities) = " + e + " Message = " + e.getMessage() + " Error = " + e.getStackTrace();
        } catch (IOException e) {
            classInfo.description += " IOException1(setPriorities) = " + e + " Message = " + e.getMessage() + " Error = " + e.getStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                classInfo.description += " IOException2(setPriorities) = " + e + " Message = " + e.getMessage() + " Error = " + e.getStackTrace();
            }
        }
    }

    /**
     * Description of getCommentTags
     * Parses the file and extracts the comment tags into the description field for testcases
     * @param filePath - the URL path to a particular test class
     * @param testCaseInfos - the test case info for a particular test class
     * @param classInfo - the testClass for which you are getting the comment tags
     */
    public void getCommentTags(String filePath, Collection<TestCaseInfo> testCaseInfos, ClassInfo classInfo) {
        File file = new File(filePath);
        BufferedReader reader = null;
        TestCaseInfo currentTestCase = null;
        int bracesCounter = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text;
            while ((text = reader.readLine()) != null) {
                if(bracesCounter > 0) {
                    bracesCounter += countBraces(text);
                }

                for (TestCaseInfo testCaseInfo : testCaseInfos) {
                    String testCase = testCaseInfo.getName();
                    if (text.toUpperCase().contains(testCase.toUpperCase())) {
                        currentTestCase = testCaseInfo;
                        bracesCounter = countBraces(text);
                    }
                }

                // Evaluate text to see if @TAG exists.
                if (currentTestCase != null && bracesCounter > 0) {
                    currentTestCase.description += retrieveTag(text);
                } else if (bracesCounter == 0) {
                    classInfo.description += retrieveTag(text);
                }
            }
        } catch (FileNotFoundException e) {
            classInfo.description += " FileNotFoundException(getCommentTags) = " + e + " Message = " + e.getMessage() + " Error = " + e.getStackTrace();
        } catch (IOException e) {
            classInfo.description += " IOException1(getCommentTags) = " + e + " Message = " + e.getMessage() + " Error = " + e.getStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                classInfo.description += " IOException2(getCommentTags) = " + e + " Message = " + e.getMessage() + " Error = " + e.getStackTrace();
            }
        }
    }

    /**
     * Description of retrieveTag
     * Given a line of text, if it contains "@TAG:" then return the line. Otherwise return
     * and empty string.
     * @param line - The string line in which to evaluate whether "@TAG:" exists.
     * @return String - If a tag exists, the line. Otherwise an empty string.
     */
	private String retrieveTag(String line) {
		// If the line contains @TAG then return that line.
		if (line.contains("@TAG:")) {
			return line;
		}
		// Otherwise return blank String.
		return "";
	}

    /**
     * Description of countBraces
     * Get file URL path for a particular package
     * @param line - the string line in which to count the braces
     * @return int - returns the total number of '{' - total number of '}'
     */
    public int countBraces(String line) {
        int count = 0;
        for(int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == '{') count++;
            if(line.charAt(i) == '}') count--;
        }
        return count;
    }

    /**
     * Description of getFilePath
     * Get file URL path for a particular package
     * @param name - the package name want to get
     * @param fromDirectory - the root directory that contains the package
     * @return String - filepath contain package name equals to name
     */
    public String getFilePath(String name, String fromDirectory) {
        String s;
        String filePath = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec(new String[]{"find", fromDirectory, "-name", name});
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null) {
                filePath += s;
            }
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            //cl.description += " Exception(getFilePath) = " + e + " Message = " + e.getMessage() + " Error = " + e.getStackTrace();
        }
        return filePath;
    }

}
