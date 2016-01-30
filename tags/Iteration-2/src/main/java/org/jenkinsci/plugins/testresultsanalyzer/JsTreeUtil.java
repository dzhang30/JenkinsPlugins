package org.jenkinsci.plugins.testresultsanalyzer;

import java.util.List;
import java.util.*;
import java.util.Set;

import org.jenkinsci.plugins.testresultsanalyzer.result.info.ResultInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsTreeUtil {

    public JSONObject getJsTree(List<Integer> builds, boolean check, ResultInfo resultInfo) {
        JSONObject tree = new JSONObject();

        JSONArray buildJson = new JSONArray();
        for (Integer buildNumber : builds) {
            buildJson.add(buildNumber.toString());
        }
        tree.put("builds", buildJson);
        JSONObject packageResults = resultInfo.getJsonObject();
        JSONArray results = new JSONArray();
        for (Object packageName : packageResults.keySet()) {
            JSONObject packageJson = packageResults.getJSONObject((String) packageName);
            results.add(createJson(builds, check, packageJson));
        }
        tree.put("results", results);
        return tree;
    }

    private JSONObject getBaseJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", "");
        jsonObject.put("description", "");
        jsonObject.put("buildResults", new JSONArray());
        return jsonObject;
    }

    private JSONObject createJson(List<Integer> builds, boolean check, JSONObject dataJson) {
        JSONObject baseJson = getBaseJson();
        baseJson.put("text", dataJson.get("name"));
        baseJson.put("type", dataJson.get("type"));
        baseJson.put("description", dataJson.get("description"));
        baseJson.put("buildStatuses", dataJson.get("buildStatuses"));
        JSONObject packageBuilds = dataJson.getJSONObject("builds");
        JSONArray treeDataJson = new JSONArray();
        ArrayList total_fail = new ArrayList();
        for (Integer buildNumber : builds) {
            JSONObject build = new JSONObject();
            if (packageBuilds.containsKey(buildNumber.toString())) {
                JSONObject buildResult = packageBuilds.getJSONObject(buildNumber.toString());
                //String status = buildResult.getString("status");
                buildResult.put("buildNumber", buildNumber.toString());
                build = buildResult;
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
            	JSONObject t = createJson(builds, check, childrenJson.getJSONObject(childName));
                childrens.add(t);
                //get child test history
                JSONObject child_temp = childrenJson.getJSONObject(childName);
                child_unsort.add(t);
                total_fail.add((Integer)(child_temp.getJSONObject("builds").get("Total_fail")));
            }
            
            int len = childrens.size();
            //sort by history, using bubble sort to sort
            JSONArray childrens_temp = new JSONArray();

            for (int i = 0; i < len; i++)
            {
            	for (int j = i+1; j < len; j++)
            	{
            		if ((((Integer)total_fail.get(i)).intValue()) <= (((Integer)total_fail.get(j)).intValue()))
            		{
            			Collections.swap(total_fail,i,j);
            			Collections.swap(child_unsort,i,j);
            		}
            	}
        	}
            
            //make new order for children
            //children is the unsort array and childrens_temp is the sort array
            for (int i = 0; i < len; i++)
            {
            	childrens_temp.add(child_unsort.get(i));
        	}
            //based on button choose for showing test result order
            if (check == true)
            {
            	 baseJson.put("children", childrens_temp);
            }
            else
            {
            	 baseJson.put("children", childrens);
            }
        }

        return baseJson;
    }
}
