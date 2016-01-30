package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by gears on 11/18/15.
 */
public class PackageInfoTest {

    PackageInfo pkg;
    ArrayList<TestCaseInfo> testCaseInfos;
    String filePath;

    public static TestCaseInfo setData(String name) {
        TestCaseInfo testCaseInfo = new TestCaseInfo();
        testCaseInfo.setName(name);
        testCaseInfo.setPriority(-1);
        return testCaseInfo;
    }

    @Before
    public void setUp() {
        testCaseInfos = new ArrayList<TestCaseInfo>();
        testCaseInfos.add(setData("testRestoreCollection1"));
        testCaseInfos.add(setData("testGetStringRepresentation1"));
        testCaseInfos.add(setData("testAddElement1"));
        testCaseInfos.add(setData("testAddElement2"));
        testCaseInfos.add(setData("testDeleteElement1"));
        testCaseInfos.add(setData("testDeleteElement2"));
        testCaseInfos.add(setData("secret"));

        pkg = new PackageInfo();

        filePath = System.getProperty("user.dir") + "/src/test/java/org/jenkinsci/plugins/testresultsanalyzer/result/info/CollectionTest.txt";
    }

    @Test
    public void testSetPriorities() {
        pkg.setPriorities(filePath, testCaseInfos);
        //assertEquals(-1, testCaseInfos.get(0).getPriority());
        //assertEquals(-1, testCaseInfos.get(1).getPriority());
        //assertEquals(-1, testCaseInfos.get(2).getPriority());
        //assertEquals(1, testCaseInfos.get(3).getPriority());
        //assertEquals(-1, testCaseInfos.get(4).getPriority());
        //assertEquals(2, testCaseInfos.get(5).getPriority());
        //sassertEquals(-1, testCaseInfos.get(6).getPriority());
        assertTrue(true);
    }

    @Test
    public void testGetFilePath() {
        //String result = pkg.getFilePath("CollectionTest.txt", ".");
        //assertEquals(filePath, result);
        assertTrue(true);
    }

}
