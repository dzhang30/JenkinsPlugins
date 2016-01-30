package org.jenkinsci.plugins.testresultsanalyzer;

import static org.junit.Assert.*;

import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jenkinsci.plugins.testresultsanalyzer.TestResultsAnalyzerAction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TestResultsAnalyzerActionTest {
	
	private List<Integer> createList(int[] array) {
		List<Integer> toReturn = new ArrayList<Integer>();
		for (int i = 0; i < array.length; i++)
			toReturn.add(array[i]);
		
		return toReturn;
	}
	
	private boolean arrayEquals(List<Integer> one, int[] two) {
		for (int i = 0; i < one.size(); i++)
			if (one.get(i) != two[i])
				return false;
		
		return true;
	}

	@Test
	//Checks basic functionality
	public void getSelectBuildsTest1() {
		String input = "1,3,5-7, 10, 13-14, 17,18";
		int[] expected = {1,3,5,6,7,10,13,14,17,18};
		assertEquals(true,arrayEquals(TestResultsAnalyzerAction.getSelectBuilds(input, createList(expected)), expected));
	}
	
	@Test
	//Checks for garbage input, negative numbers and 0
	public void getSelectBuildsTest2() {
		String input = "a;sldfgh7,16,1,8,12-15,-9,121,0";
		int[] expected = {1, 8, 12, 13, 14, 15, 16, 121};
		assertEquals(true,arrayEquals(TestResultsAnalyzerAction.getSelectBuilds(input, createList(expected)), expected));
	}
	
	@Test
	//Checks for numbers that aren't in the valid build list
	public void getSelectBuildsTest3() {
		String input = "1-20";
		int[] expected = {10,11,12,13,14,15,16,17,18,19,20};
		assertEquals(true,arrayEquals(TestResultsAnalyzerAction.getSelectBuilds(input, createList(expected)), expected));
	}
	
	@Test
	//Checks for the default word All to return null
	public void getSelectBuildsTest4() {
		String input = "All";
		assertEquals(true,TestResultsAnalyzerAction.getSelectBuilds(input, null) == null);
	}	
	
	@Test
	//Checks for overuse of delimiters
	public void getSelectBuildsTest5() {
		String input = "9,,,,,,,-23,18,,-2-8-1-6,14-9,2-0,1-5";
		int[] expected = {1, 2, 3, 4, 5, 9, 18};
		assertEquals(true,arrayEquals(TestResultsAnalyzerAction.getSelectBuilds(input, createList(expected)), expected));
	}	
	
	@Test
	//Checks for removing empty space and across multiple items
	public void getSelectBuildsTest6() {
		String input = "5 -     7";
		int[] expected = {5, 6, 7};
		assertEquals(true,arrayEquals(TestResultsAnalyzerAction.getSelectBuilds(input, createList(expected)), expected));
	}	
}
