function getUrl(currentUrl, packageInfo, filename) {

    return 	'href="' + currentUrl.replace('/test_results_analyzer/','/ws/src/test/java/') + packageInfo.replace(/\./g,'/') + '/' + filename.replace(/\./g,'/') + '.java"';
}
