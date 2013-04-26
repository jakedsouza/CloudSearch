/*
 * ===================================================
 * JS script for google authentication and login
 * 
 * 
 * ==========================================================
 */

// when the document is ready set the login url on google login button 
//$(document).ready(function() {
//	setGoogleLoginURL();
//});
// 
// function to get the google login url and set it on the google login button
//function setGoogleLoginURL(){
//	$.ajax({
//        type: 'GET',
//        url: 'rest/cloud/getLoginURL',
//        success: function(data) {
//        	$("#googleLogin").attr('href',data);  	
//        }	
//	});
//}
window.onload = function() {
	decideLoginType();
};



function decideLoginType() {
	var uri = document.URL;
	var result = parseUri(uri);
	var code = result.queryKey.code;
	if (localStorage.email != null && localStorage.userId != null) {
		login(localStorage.email, localStorage.userId, null);
	} else if (code != null) {
		login(null, null, code);
	}
}

function login(email, userId, code) {
	$.ajax({
		type : 'GET',
		url : 'rest/validate/login',
		data : {
			'email' : email,
			'userId' : userId,
			'code' : code
		},
		success : function(data) {
			if (data.http_code == "307") {
				var googleRedirectUrl = data.url;
				window.location = googleRedirectUrl;
			} else if (data.http_code == "200") {
				if (data.email != null && data.userId != null) {
					var successFullLoginUrl = data.url;
					localStorage.email = data.email;
					localStorage.userId = data.userId;
					window.location = successFullLoginUrl;
				}
			}
			// window.location = data;
		}
	});
}
