//
//$(document).ready(function() {
//	var uri = document.URL;
//	var result = parseUri(uri);
//	if (result) {
//		var code = result.queryKey.code;
//		var state = result.queryKey.state;
//		if (state == "google" && code != null) {
//			getUserInfo(code);
//		}else{
//			window.location = "index.html";
//		}
//	}else{
//		window.location = "index.html";
//	}
//
//});
//
//
//
//function getUserInfo(code) {
//	$.ajax({
//		type : 'GET',
//		url : 'rest/cloud/getUserInfo',
//		data : {
//			'code' : code,
//		},
//		success : function(a) {
//			displayUserInfo(a);
//		}
//
//	});
//}
//
//function isUserRegistered(){
//	
//}
//
//function displayUserInfo(userInfo) {
//	$("#userinfo").html(userInfo.name);
//};
//
$("#logoutBtn").on("click",function(){
	localStorage.removeItem("email");
	localStorage.removeItem("userId");
	window.location = "index.html";
});