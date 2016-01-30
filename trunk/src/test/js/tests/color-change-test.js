QUnit.test("setCookie", function(assert) {
    var currCookie = getCookie("color");

    setCookie("color", "test", 30);

    assert.equal(getCookie("color"), "test", "The text 'test' should be stored.");

    // Set the cookies back to what they were.
    setCookie("color", currCookie, 30);
});

QUnit.test("getColor", function(assert) {
    var currCookie = getCookie("color");

    var testColors = {
        "passed" : "#FFFFFF",
        "failed" : "#" + $j("#failcolor").val(),
        "skipped" : "#" + $j("#skipcolor").val(),
        "total" : "#" + $j("#totalcolor").val(),
        "na" :""
    };

    setCookie("color", JSON.stringify(testColors), 30);

    var stored = getColors();

    assert.equal(stored['passed'], "#FFFFFF", "FFFFFF should be stored in passed.");

    // Set the cookies back to what they were.
    setCookie("color", currCookie, 30);
});