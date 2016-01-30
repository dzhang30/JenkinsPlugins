/**
 * Returns true of false depending on whether not the line has a @TAG: set of characters.
 */
function _lookForTag(lineText) {
	return lineText.indexOf("@TAG:") > -1;
}

function _extractTaggedLines(text) {
	var fileLines = text.split("\n");
	
	// Variables for iteration.
	var i;
	var line;
	// HTML that contains all tagged lines.
	var taggedComments = '<br>';

	var seenLine = false;

	for (i = 0; i < fileLines.length; ++i) {
		line = fileLines[i];

		if (_lookForTag(line)) {
			if (seenLine) {
				taggedComments += "<br>";
			}

			taggedComments += line + "<br>";
			seenLine = true;
		}
	}

	return taggedComments;
}
//
///**
// * Extract the relevant lines for the function name.
// * @param fileString
// */
//function relevantLines(fileString, functionName) {
//
//}
//
//function parseTagsFunction(filePath, functionName) {
//    var taggedComments = '<br>';
//    var fileLines;
//
//}

/**
 * Parse the file described in filePath to detect @TAG comments.
 */
function parseTags(filePath) {
	var taggedComments = '<br>';

	var xhr = new XMLHttpRequest();

	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {	// File is loaded.
			if (xhr.status === 200) {	// File is found.
				
				taggedComments += _extractTaggedLines(xhr.responseText);

			}
		}
	};

	xhr.open('GET', filePath, false);
	xhr.send('');

	

	return taggedComments;
}
