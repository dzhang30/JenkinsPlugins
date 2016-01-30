var colorDiv = $j("#colors");

$j("#hideedit").click(function() {
	colorDiv.hide();
});

$j("#showedit").click(function() {
	getColors();
	colorDiv.show();
});

$j("#savecolors").click(function() {

	var setColors = {
			"passed" : "#" + $j("#passcolor").val(),
			"failed" : "#" + $j("#failcolor").val(),
			"skipped" : "#" + $j("#skipcolor").val(),
			"total" : "#" + $j("#totalcolor").val(),
			"na" :""
	};

	setCookie("color",  JSON.stringify(setColors), 30);

	var chartType = $j('#reporttype').val();
	generateChart(chartType);
});

function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
}

function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}

		if (c.indexOf(name) == 0) { 
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

function getColors() {

	var defaultColors = {
			"passed" :"#92D050",
			"failed" :"#F37A7A",
			"skipped" :"#FDED72",
			"total" :"#67A4F5",
			"na" :""
	};

	var cookie = getCookie("color");

	if (cookie !== "") {
		//alert("Cookie: " + cookie);
		defaultColors = JSON.parse(cookie);
		$j("#passcolor").val(defaultColors["passed"].replace('#',''));
		$j("#skipcolor").val(defaultColors["skipped"].replace('#',''));
		$j("#failcolor").val(defaultColors["failed"].replace('#',''));
		$j("#totalcolor").val(defaultColors["total"].replace('#',''));
	} else {
		setCookie("color",  JSON.stringify(defaultColors), 30);
	}	

	return defaultColors;
}
