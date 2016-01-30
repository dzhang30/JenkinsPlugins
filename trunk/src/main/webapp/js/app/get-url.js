function getUrl(currentUrl, packageInfo, filename) {

	return currentUrl.replace('/test_results_analyzer/','/ws/src/test/java/') + packageInfo.replace(/\./g,'/') + '/' + filename.replace(/\./g,'/') + '.java';
}
