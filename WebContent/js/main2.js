var typeEnum = ["gc", "gd", "db"];

$(document).ready(function() {
	decideLoginType();
	// setup elastic.js client for jQuery
	ejs.client = ejs.jQueryClient('http://localhost:9200');
	// renderSettingsButtons();
});

SearchQuery = function (userQuery) {
    this.userQuery = userQuery.toLowerCase();
    this.query = "";
    this.types = new Array();
    this.exts = new Array();

    var tokens = this.userQuery.split(/[\s,]/);
    var isIn = false;
    var isExt = false;
    var tempStr = "";
    for (var i = 0; i < tokens.length; i++) {
        if (tokens[i] != "") {
            if (tokens[i] == "in" && tokens[i] != "ext:" && !isIn) {
                tempStr = "in";
                isIn = true;
            } else if (tokens[i] != "in" && tokens[i] != "ext:" && isIn) {
                if (checkIfType(tokens[i])) {
                    this.types.push(tokens[i]);
                    tempStr = "";
                } else {
                    var extArr = breakStrExt(tokens[i]);
                    if (extArr.length > 1) {
                        this.exts.push(extArr[1]);
                    }
                    tempStr = tempStr == "" ? extArr[0] : " " + extArr[0];
                    this.query += this.query == "" ? tempStr : " " + tempStr;
                    tempStr = "";
                    isIn = false;
                }
            } else if (tokens[i] == "ext:") {
                isIn = false;
                isExt = true;
            } else if (isExt) {
                tempStr = "";
                var isPresent = false;
                for (var j = 0; j < this.exts.length; j++) {
                    if (this.exts[j] == tokens[i]) {
                        isPresent = true;
                        break;
                    }
                }
                if (!isPresent) this.exts.push(tokens[i]);
            } else if (tokens[i] != "in" && tokens[i] != "ext" && !isIn && !isExt) {
                tempStr = "";
                var extArr = breakStrExt(tokens[i]);
                if (extArr.length > 1) {
                    this.exts.push(extArr[1]);
                }
                tempStr = tempStr == "" ? extArr[0] : " " + extArr[0];
                this.query += this.query == "" ? tempStr : " " + tempStr;
                tempStr = "";
            }

        }
    }

    if (this.types.length == 0) {
        //this.types.push("gc");
        this.types.push("gd");
        this.types.push("db");
    }

    function checkIfType(str) {
        for (var i = 0; i < typeEnum.length; i++) {
            if (str == typeEnum[i]) {
                return true;
            }
        }
        return false;
    }

    function breakStrExt(str) {
        return (str.split("."));
    }
};

function SearchResult(data) {
	this.index = data._index;
	this._type = data._type;
	this.id = data._id;
	this.score = data._score;
	if(this._type === "db" ){
//		this.modifiedDate = moment(data._source.dateModified);
		this.modifiedDate = data._source.dateModified;
		this.icon = data._source.icon;
		if(this.icon === null){
			this.icon = "img/dropbox-api-icons/48x48/page_white48.gif";
		}else{
			this.icon ="img/dropbox-api-icons/48x48/" +this.icon+"48.gif" ; 
		}
		this.isDeleted = data._source.isDeleted;
		this.mimeType = data._source.mimeType;
		this.name = data._source.name ;
		this.path = "https://www.dropbox.com/home"+data._source.parentPath ;
		this.isDir = data._source.isDir;
	}else if(this._type === "gd"){
		this.alternateLink = data._source.alternateLink;
		this.createdDate = data._source.createdDate;
		this.exportLinks = data._source.exportLinks;
		this.iconLink = data._source.iconLink;
		//this.modifiedDate = moment(data._source.modifiedDate);
		this.modifiedDate = data._source.modifiedDate;
		this.thumbnailLink = data._source.thumbnailLink;
		this.title = data._source.title;
		this.type = data._source.type;		
	}else if(this._type === "gc"){
		
	}
	
}

function AppViewModel() {
	var self = this;
	self.projectName = 'CloudSearch';
	self.isUserLoggedIn = ko.observable(false);
	self.timeTakenForSearch = ko.observable();
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
	self.dbBtnClass = ko.observable('btn');
	self.dbIconClass = ko.observable('icon-plus-sign');
	self.dbBtnText = ko.observable('Connect');
	self.isDropBoxConnected = ko.observable(false);
	
	// pages
	self.chosenPage = ko.observable();
	self.gotoPage = function(b) {
		console.log("Going to page " + b);
		self.chosenPage(b);
		location.hash = self.chosenPage();
		 window.history.pushState('', 'CloudSearch Login',
		 '/CloudSearch/index.jsp#'+self.chosenPage());
	};
	// functions
	self.login = function() {
		login(null, null, null);
	};
	self.logout = function() {
		logout();
	};
	self.loginGoogleDrive = function(data, event) {
		//debugger;
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
	
	self.loginDropBox = function(data, event) {
		debugger;
		if (self.isDropBoxConnected()) {
			self.logoutDropBox();
			return;
		}
		loginDropBox(null,null);
	};
	
	self.logoutDropBox = function() {
		localStorage.removeItem('isDropBoxConnected');
		self.updateDBdata(null);
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

	self.updateDBdata = function(data){
		var connected = false;
		if(localStorage.isDropBoxConnected != null ){
			connected = true;
		}
		if (connected || data === 'connected') {
			self.dbBtnClass('btn btn-danger');
			self.dbIconClass('icon-remove');
			self.dbBtnText('Disconnect');
			self.isDropBoxConnected(true);
			localStorage.isDropBoxConnected = true;
		} else if (data === 'refresh') {
			self.dbBtnClass('btn btn-info');
			self.dbIconClass('icon-refresh icon-spin');
			self.dbBtnText('Indexing Data...');
			self.isDropBoxConnected(false);
		} else {
			self.dbBtnClass('btn');
			self.dbIconClass('icon-plus');
			self.dbBtnText('Connect');
			self.isDropBoxConnected(false);
		}
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
		
		//self.localStoragegDriveuserId = localStorage.gDriveuserId;
	};

	self.search = function() {
		getSearchResults();
	};

	self.searchResults = ko.observableArray();

};
var model = new AppViewModel();
ko.applyBindings(model);

model.updateGDdata(null);
model.updateDBdata(null);
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
	//var query = $('#searchInput').val();
	
	var query = new SearchQuery($('#searchInput').val());
	var userQuery = query.query;
	var ext = query.exts;
	var type = query.types;
	if(userQuery.length < 3 ){
		return;
	}
//	var index = new Array();
//	for(var i = 0 ; i < type.length;i++){
//		index.push(model.localStorageuserID.toLowerCase()+"/"+type[i]);
//	}
	var indexID = model.localStorageuserID.toLowerCase();
	//var searchURL = "http://localhost:9200/" + indexID + "/_search";

	// setup the indices and types to search across
	//var type = ['gd','db'];
	var r = ejs.Request().size(20).explain(true).indices(indexID).types(type).query(ejs.QueryStringQuery(userQuery || '*'));
//	request.explain(true);
//	request.types = type;
	//request.query(ejs.QueryStringQuery(userQuery || '*'));
	console.log(r.toString());
	r.doSearch(displayResults);
	//	$.getJSON(searchURL, query, function(allData) {
	//		console.log(allData);

	//	});

}

// renders the results page
var displayResults = function (results) {
	model.searchResults.removeAll();
	model.timeTakenForSearch("The Search took : " + results.took + " ms");
	// model.searchResults.push(allData.hits.hits);
	var hits = results.hits.hits;
	for ( var i = 0; i < hits.length; i++) {
		var result = new SearchResult(hits[i]);
		model.searchResults.push(result);
	}
//	moment().format();
	var unmapped = ko.mapping.toJS(model);
	debugger;
};
