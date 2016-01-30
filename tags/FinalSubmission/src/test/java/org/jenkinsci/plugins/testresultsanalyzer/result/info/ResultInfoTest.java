package org.jenkinsci.plugins.testresultsanalyzer.result.info;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by gears on 12/9/15.
 */
public class ResultInfoTest {

    ResultInfo resultInfo;
    PackageInfo pkg;
    ArrayList<TestCaseInfo> testCaseInfos;
    ClassInfo classInfo;
    String filePath;

    public static TestCaseInfo setData(String name) {
        TestCaseInfo testCaseInfo = new TestCaseInfo();
        testCaseInfo.setName(name);
        testCaseInfo.setPriority(-1);
        testCaseInfo.setDescription("");
        return testCaseInfo;
    }


    @Before
    public void setUp() throws Exception {
        resultInfo = new ResultInfo();
        testCaseInfos = new ArrayList<TestCaseInfo>();
        testCaseInfos.add(setData("testRestoreCollection1"));
        testCaseInfos.add(setData("testGetStringRepresentation1"));
        testCaseInfos.add(setData("testAddElement1"));
        testCaseInfos.add(setData("testAddElement2"));
        testCaseInfos.add(setData("testDeleteElement1"));
        testCaseInfos.add(setData("testDeleteElement2"));
        testCaseInfos.add(setData("secret"));
        classInfo = new ClassInfo();
        classInfo.setDescription("");
        pkg = new PackageInfo();

        filePath = System.getProperty("user.dir") + "/src/test/java/org/jenkinsci/plugins/testresultsanalyzer/result/info/CollectionTest.txt";
    }

    @Test
    public void testSetPriorities() throws Exception {
        resultInfo.setPriorities(filePath, testCaseInfos, classInfo);
        assertEquals(-1, testCaseInfos.get(0).getPriority());
        assertEquals(-1, testCaseInfos.get(1).getPriority());
        assertEquals(-1, testCaseInfos.get(2).getPriority());
        assertEquals(1, testCaseInfos.get(3).getPriority());
        assertEquals(-1, testCaseInfos.get(4).getPriority());
        assertEquals(2, testCaseInfos.get(5).getPriority());
        assertEquals(-1, testCaseInfos.get(6).getPriority());
    }

    @Test
    public void testGetCommentTags() throws Exception {
        resultInfo.getCommentTags(filePath, testCaseInfos, classInfo);
        assertEquals("\t\tCollection cs411 = new Collection(\"cs411\"); //@TAG: blah2", testCaseInfos.get(0).getDescription());
        assertEquals("", testCaseInfos.get(1).getDescription());
        assertEquals("", testCaseInfos.get(2).getDescription());
        assertEquals("\t\tassertTrue(true); //@TAG: blah4", testCaseInfos.get(3).getDescription());
        assertEquals("", testCaseInfos.get(4).getDescription());
        assertEquals("", testCaseInfos.get(5).getDescription());
        assertEquals("", testCaseInfos.get(6).getDescription());
        assertEquals("//@TAG: blah5//@TAG: blah1    //@TAG: blah3 * @TAG: Test for multi-line.//@TAG: blah6", classInfo.getDescription());
    }

    @Test
    public void testGetFilePath() throws Exception {
        String result = resultInfo.getFilePath("CollectionTest.txt", ".");
        assertEquals("./src/test/java/org/jenkinsci/plugins/testresultsanalyzer/result/info/CollectionTest.txt", result);
        String emptyInput = resultInfo.getFilePath("emptyInput", ".");
        assertEquals("", emptyInput);
        assertNotEquals("./src/test/java/org/jenkinsci/plugins/testresultsanalyzer/result/info/CollectionTest.txt", emptyInput);
    }

    @Test
    public void testCountBraces() throws Exception {
        String str0 = "{sgsgdsfgdsf{{{sdfsdf}";
        assertEquals(3, resultInfo.countBraces(str0));
        String str1 = "{}";
        assertEquals(0, resultInfo.countBraces(str1));
    }
}