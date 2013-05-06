// Could be stored in a separate utility library
ko.bindingHandlers.fadeVisible = {
	init : function(element, valueAccessor) {
		// Initially set the element to be instantly visible/hidden depending on
		// the value
		var value = valueAccessor();
		$(element).toggle(ko.utils.unwrapObservable(value)); // Use
		// "unwrapObservable"
		// so we can
		// handle values
		// that may or
		// may not be
		// observable
	},
	update : function(element, valueAccessor) {
		// Whenever the value subsequently changes, slowly fade the element in
		// or out
		var value = valueAccessor();
		ko.utils.unwrapObservable(value) ? $(element).fadeIn("slow") : $(
				element).fadeOut();
	}
};

// login functions

function decideLoginType() {
	debugger;
	var uri = document.URL;
	var result = parseUri(uri);
	var code = result.queryKey.code;
	var state = result.queryKey.state;
	// for dropBox
	var oauthToken = result.queryKey.oauth_token;
	var uid = result.queryKey.uid;
	
	if (result.query === "") {
		model.gotoPage('login');
	}
	if ((state == null || state=='login')&& uid==null) {
		state = 'login';
	}else if(uid != null && oauthToken != null){
		state = 'dropbox';
	}

	switch (state) {
	case 'login':
	//	debugger;
		if (localStorage.email != null && localStorage.userId != null) {
			login(localStorage.email, localStorage.userId, null);
		} else if (code != null) {
			login(null, null, code);
		}
		break;
	case 'gdrive':
	//	debugger;
		if (localStorage.email != null && localStorage.gDriveuserId != null) {
			loginGoogleDrive(localStorage.email, localStorage.gDriveuserId,
					null);
		} else if (code != null) {
			loginGoogleDrive(null, null, code);
		} else {
			model.gotoPage('settings');
		}
		break;
	case 'dropbox':
		loginDropBox(uid,oauthToken);
	default:
	//	debugger;
	}

	// if (localStorage.email != null && localStorage.userId != null) {
	// login(localStorage.email, localStorage.userId, null);
	// } else if (code != null) {
	// login(null, null, code);
	// }
};

function login(email, userId, code) {
	$.ajax({
		type : 'GET',
		url : 'rest/validate/login',
		data : {
			'email' : email,
			'userId' : userId,
			'code' : code,
			'state' : 'login'
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
							'/');
					model.gotoPage('main');

					// window.location = successFullLoginUrl;
				}
			}
		}
	});
};
function loginGoogleDrive(email, gDriveuserId, code) {

	model.isUserLoggedIn(true);
	window.history.pushState('', 'CloudSearch Login', '/#settings');
	model.gotoPage('settings');
	model.updateGDdata('refresh');
	$.ajax({
		type : 'GET',
		url : 'rest/file/index',
		data : {
			'email' : email,
			'userId' : localStorage.userId,			
			'gDriveuserId' : gDriveuserId,
			'code' : code,
			'state' : 'gdrive'
		},
		success : function(data) {
			if (data.http_code == "307") {
				var googleRedirectUrl = data.url;
				window.location = googleRedirectUrl;
			} else if (data.http_code == "200") {
				if (data.email != null && data.userId != null) {
					var successFullLoginUrl = data.url;
					localStorage.email = data.email;
					localStorage.gDriveuserId = data.userId;

					model.isUserLoggedIn(true);
					window.history.pushState('', 'CloudSearch Login',
							'/#settings');
					//model.gotoPage('settings');
					model.updateGDdata('connected');
					// window.location = successFullLoginUrl;
				}
			}
		}

	});
}
function loginDropBox(uid,oauthToken){
	model.isUserLoggedIn(true);
	window.history.pushState('', 'CloudSearch Login', '/#settings');
	model.gotoPage('settings');
	model.updateDBdata('refresh');
	if(uid ==null){
		$.get('rest/dropbox/getDropboxloginUrl', function(data) {
			debugger;
			window.location = data;
		});
	}else{
		$.ajax({
			type : 'GET',
			url : 'rest/dropbox/index',
			data : {
				'uid' : uid,
				'oauth_token' : oauthToken,
				'userId':localStorage.userId
			},
			success : function(data) {
				if (data.http_code == "200") {					
						model.isUserLoggedIn(true);
						window.history.pushState('', 'CloudSearch Login',
								'/#settings');
						//model.gotoPage('settings');
						model.updateDBdata('connected');
						// window.location = successFullLoginUrl;					
				}
			}

		});
	}
	
	debugger;
}

function logout() {
	localStorage.removeItem("email");
	localStorage.removeItem("userId");
	model.isUserLoggedIn(false);
	model.gotoPage('login');

	// window.history.pushState('', 'CloudSearch Logout', '/CloudSearch/');
}