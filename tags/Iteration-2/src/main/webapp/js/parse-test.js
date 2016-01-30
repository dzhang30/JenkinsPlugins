QUnit.test( "_parseTags detects @TAG:", function (assert) {
	assert.equal(_lookForTag("@TAG: This should be 1."), 1, "@TAG is in this string.");
	assert.equal(_lookForTag("No tag."), 0, "@TAG is not in this string.");
});

QUnit.test( "_extractTaggedLines should only extract lines with @TAG:", function (assert) {
	var taggedLine = "var testLine1; // @TAG: This should be ok.\n var testLine2;";
	var notLine = "var hi;\n var bye;";
	var multipleTags = "var tagOne: // @TAG: First tag.\n// @TAG: Second tag.";

	var expectedOne = "<br>var testLine1; // @TAG: This should be ok.<br>";
	var expectedTwo = "<br>";
	var expectedThree = "<br>var tagOne: // @TAG: First tag.<br><br>// @TAG: Second tag.<br>";

	assert.equal(_extractTaggedLines(taggedLine), expectedOne);
	assert.equal(_extractTaggedLines(notLine), expectedTwo);
	assert.equal(_extractTaggedLines(multipleTags), expectedThree);
});
