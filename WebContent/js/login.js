/*
 * ===================================================
 * JS script for google authentication and login
 * 
 * 
 * ==========================================================
 */

// when the document is ready set the login url on google login button 
$(document).ready(function() {
	setGoogleLoginURL();
});


// function to get the google login url and set it on the google login button
function setGoogleLoginURL(){
	$.ajax({
        type: 'GET',
        url: 'rest/oauth/google',
        success: function(data) {
        	$("#googleLogin").attr('href',a["url"]);  	
        }	
	});
}