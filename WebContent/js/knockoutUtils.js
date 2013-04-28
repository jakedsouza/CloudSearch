// Could be stored in a separate utility library
ko.bindingHandlers.fadeVisible = {
	init : function(element, valueAccessor) {
		// Initially set the element to be instantly visible/hidden depending on the value
		var value = valueAccessor();
		$(element).toggle(ko.utils.unwrapObservable(value)); // Use "unwrapObservable" so we can handle values that may or may not be observable
	},
	update : function(element, valueAccessor) {
		// Whenever the value subsequently changes, slowly fade the element in or out
		var value = valueAccessor();
		ko.utils.unwrapObservable(value) ? $(element).fadeIn("slow") : $(
				element).fadeOut();
	}
};

// login functions 


function decideLoginType() {
	var uri = document.URL;
	var result = parseUri(uri);
	var code = result.queryKey.code;
	if (localStorage.email != null && localStorage.userId != null) {
		login(localStorage.email, localStorage.userId, null);
	} else if (code != null) {
		login(null, null, code);
	}
};

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

					model.isUserLoggedIn(true);
					window.history.pushState('', 'CloudSearch Login',
					'/CloudSearch/');
					model.gotoPage('main');



					//window.location = successFullLoginUrl;
				}
			}
		}
	});
};

function logout()
{
	localStorage.removeItem("email");
	localStorage.removeItem("userId");
	model.isUserLoggedIn(false);
	model.gotoPage('login');

//	window.history.pushState('', 'CloudSearch Logout', '/CloudSearch/');
}