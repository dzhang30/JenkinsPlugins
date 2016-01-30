QUnit.test( "_getUrl should generate url", function (assert) {
    var currentUrl = "https://fa15-cs427-102.cs.illinois.edu:8083/job/Jenkins_build/org.jenkins-ci.plugins$test-results-analyzer/test_results_analyzer/";
    var packageName = "org.jenkinsci.plugins.testresultsanalyzer";
    var filename = "JsTreeUtilTest";

    var expected = 'https://fa15-cs427-102.cs.illinois.edu:8083/job/Jenkins_build/org.jenkins-ci.plugins$test-results-analyzer/' +
        'ws/src/test/java/org/jenkinsci/plugins/testresultsanalyzer/JsTreeUtilTest.java';

    assert.equal(getUrl(currentUrl, packageName, filename), expected);
});

