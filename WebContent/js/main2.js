$(document).ready(function() {
	decideLoginType();
});




function AppViewModel() {
	// data
	
	var self = this;
	 this.people = [
	                { name: 'Franklin', credits: 250 },
	                { name: 'Mario', credits: 5800 }
	            ];	self.projectName = 'CloudSearch';
	self.isUserLoggedIn = ko.observable(false);
	self.birthday = ko.observable();
	self.email = ko.observable();
	self.family_name = ko.observable();
	self.gender = ko.observable();
	self.given_name = ko.observable();
	self.id = ko.observable();
	self.link = ko.observable();
	self.locale = ko.observable();
	self.name = ko.observable();
	self.picture = ko.observable();
	self.verified_email = ko.observable();
	// pages
	self.chosenPage = ko.observable();
//	self.gotoSettings = function() {
//		self.chosenPage('settings');
//		location.hash = self.chosenPage();
//	};
//	self.gotoLogin = function(data,page) {
//		alert(page);
//		self.chosenPage('login');
//		location.hash = self.chosenPage();
//	};
	self.gotoPage = function(b){
		console.log("Going to page " + b);
		self.chosenPage(b);
		location.hash = self.chosenPage();
//		window.history.pushState('', 'CloudSearch Login',
//		'/CloudSearch/'+self.chosenPage());
	};
	// functions
	self.login = function() {
		login(null, null, null);
	};
	self.logout = function() {
		logout();
	};

	self.updateUserInfo = function(data) {
		self.birthday(data.birthday);
		self.email(data.email);
		self.family_name(data.family_name);
		self.gender(data.gender);
		self.given_name(data.given_name);
		self.id(data.id);
		self.link(data.link);
		self.locale(data.locale);
		self.name(data.name);
		self.picture(data.picture);
		self.verified_email(data.verified_email);
	};
};
var model = new AppViewModel();
ko.applyBindings(model);

model.gotoPage('login');
model.chosenPage.subscribe(function(newValue) {
//	alert(newValue);
});

// on login successfull get user info and update model
model.isUserLoggedIn.subscribe(function(newValue) {
	if (newValue == true) {
		var data = {
			'email' : localStorage.email,
			'userId' : localStorage.userId
		};
		$.getJSON("rest/userService/getUserInfo", data, function(allData) {
			model.updateUserInfo(allData);
			var unmapped = ko.mapping.toJS(model);
			console.log(unmapped);
		});
	}

});
