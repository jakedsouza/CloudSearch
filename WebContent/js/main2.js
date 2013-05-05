$(document).ready(function() {
	decideLoginType();
	// setup elastic.js client for jQuery
	ejs.client = ejs.jQueryClient('http://localhost:9200');
	// renderSettingsButtons();
});

function SearchResult(data) {
	this.index = data._index;
	this.type = data._type;
	this.id = data._id;
	this.score = data._score;
	this.alternateLink = data._source.alternateLink;
	this.createdDate = data._source.createdDate;
	this.exportLinks = data._source.exportLinks;
	this.iconLink = data._source.iconLink;
	this.modifiedDate = data._source.modifiedDate;
	this.thumbnailLink = data._source.thumbnailLink;
	this.title = data._source.title;
	this.type = data._source.type;
}

function AppViewModel() {
	var self = this;
	self.projectName = 'CloudSearch';
	self.isUserLoggedIn = ko.observable(false);
	// user info after login
	self.email = ko.observable();
	self.id = ko.observable();
	self.link = ko.observable();
	self.name = ko.observable();
	self.picture = ko.observable();

	// local storage info
	self.localStorageEmail = ko.observable();
	self.localStorageuserID = ko.observable();
	self.localStorageDriveuserId = ko.observable();

	self.gdBtnClass = ko.observable('btn');
	self.gdIconClass = ko.observable('icon-plus-sign');
	self.gdBtnText = ko.observable('Connect');
	self.isGoogleDriveConnected = ko.observable(false);
	// pages
	self.chosenPage = ko.observable();
	self.gotoPage = function(b) {
		console.log("Going to page " + b);
		self.chosenPage(b);
		location.hash = self.chosenPage();
		 window.history.pushState('', 'CloudSearch Login',
		 '/'+self.chosenPage());
	};
	// functions
	self.login = function() {
		login(null, null, null);
	};
	self.logout = function() {
		logout();
	};
	self.loginGoogleDrive = function(data, event) {
		debugger;
		if (self.isGoogleDriveConnected()) {
			self.logoutGoogleDrive();
			return;
		}
		loginGoogleDrive(null, null, null);
	};
	self.logoutGoogleDrive = function() {
		localStorage.removeItem('gDriveuserId');
		self.updateGDdata(null);
	};

	self.updateUserInfo = function(data) {
		self.email(data.email);
		self.id(data.id);
		self.link(data.link);
		self.name(data.name);
		self.picture(data.picture);
		self.localStorageEmail = localStorage.email;
		self.localStorageuserID = localStorage.userId;
		self.localStorageDriveuserId = localStorage.gDriveuserId;
	};

	self.updateGDdata = function(data) {
		var connected = false;
		if (localStorage.gDriveuserId != null && localStorage.email != null) {
			connected = true;
		}
		if (connected || data === 'connected') {
			self.gdBtnClass('btn btn-danger');
			self.gdIconClass('icon-remove');
			self.gdBtnText('Disconnect');
			self.isGoogleDriveConnected(true);
		} else if (data === 'refresh') {
			self.gdBtnClass('btn btn-info');
			self.gdIconClass('icon-refresh icon-spin');
			self.gdBtnText('Indexing Data...');
			self.isGoogleDriveConnected(false);
		} else {
			self.gdBtnClass('btn');
			self.gdIconClass('icon-plus');
			self.gdBtnText('Connect');
			self.isGoogleDriveConnected(false);
		}
		self.localStoragegDriveuserId = localStorage.gDriveuserId;
	};

	self.search = function() {
		getSearchResults();
	};

	self.searchResults = ko.observableArray();

};
var model = new AppViewModel();
ko.applyBindings(model);

model.updateGDdata(null);
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

function getSearchResults() {
	var query = $('#searchInput').val();
	var indexID = model.localStorageDriveuserId.toLowerCase();
	var searchURL = "http://localhost:9200/" + indexID + "/user/_search";

	// setup the indices and types to search across
	var index = model.localStorageDriveuserId.toLowerCase();
	var type = 'user';
	var request = ejs.Request({
		indices : index,
		types : type
	});
	request.query(ejs.QueryStringQuery(query || '*')).doSearch(displayResults);
	//	$.getJSON(searchURL, query, function(allData) {
	//		console.log(allData);

	//	});

}

// renders the results page
displayResults = function (results) {
	model.searchResults.removeAll();
	// model.searchResults.push(allData.hits.hits);
	var hits = results.hits.hits;
	for ( var i = 0; i < hits.length; i++) {
		var result = new SearchResult(hits[i]);
		model.searchResults.push(result);
	}
	var unmapped = ko.mapping.toJS(model);
	debugger;
};
