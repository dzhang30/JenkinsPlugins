QUnit.test( "_parseTags detects @TAG:", function (assert) {
	assert.equal(_lookForTag("@TAG: This should be 1."), 1, "@TAG is in this string.");
	assert.equal(_lookForTag("No tag."), 0, "@TAG is not in this string.");
});

QUnit.test( "_extractTaggedLines should only extract lines with @TAG:", function (assert) {
	var one = "var testLine1; // @TAG: This should be ok.\n var testLine2;";
	var zero = "var hi;\n var bye;";
	var twoStandard = "var tagOne: // @TAG: First tag.\n// @TAG: Second tag.";
	var twoMulti = "/**\n" +
			"* One @TAG: one\n" +
			"* Two @TAG: two\n";

	var expectedOne = "<br>var testLine1; // @TAG: This should be ok.<br>";
	var expectedTwo = "<br>";
	var expectedThree = "<br>var tagOne: // @TAG: First tag.<br><br>// @TAG: Second tag.<br>";
	var expectedFour = "<br>* One @TAG: one<br><br>* Two @TAG: two<br>";

	var actualOne = _extractTaggedLines(one);
	var actualTwo = _extractTaggedLines(zero);
	var actualThree = _extractTaggedLines(twoStandard);
	var actualFour = _extractTaggedLines(twoMulti);

	assert.equal(actualOne, expectedOne);
	assert.equal(actualTwo, expectedTwo);
	assert.equal(actualThree, expectedThree);
	assert.equal(actualFour, expectedFour);
});
