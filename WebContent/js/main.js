$(document).ready(function() {
	setGoogleLoginURL();
	var uri = document.URL;
	var result = parseUri(uri);
	if (result) {
		var code = result.queryKey.code;
		var state = result.queryKey.state;
		if (state == "google" && code != null) {
			getUserInfo(code);
		}
	}

});

function setGoogleLoginURL() {
	$.ajax({
		type : 'GET',
		url : 'rest/cloud/getLoginURL',
		data : {
		// 'request': 'getGoogleLoginURL',
		},
		success : function(a) {
			//	alert();
			//	var data = JSON.parse(a);
			//alert(data);
			$("#googleLogin").attr('href', a);
			//        	return (a["url"]);
		}

	});
}

function getUserInfo(code) {
	$.ajax({
		type : 'GET',
		url : 'rest/cloud/getUserInfo',
		data : {
			'code' : code,
		},
		success : function(a) {
			alert(a);
			displayUserInfo(a);
		}

	});
}

function isUserRegistered(){
	
}

function displayUserInfo(userInfo) {
	var text = userInfo.toString();
	$("#userinfo").text(text);
	alert(userInfo);
};

