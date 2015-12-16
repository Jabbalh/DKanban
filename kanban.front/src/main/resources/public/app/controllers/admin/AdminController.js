angular.module("DKanbanApp")

.controller("AdminController", function ($state) {	
	
	this.goTo = function(route) {
		$state.go(route);
	}
	
})

.controller("AdminAppController", function ($scope,$http,$mdDialog,updateService,listService) {	
	
	var self = this;
	this.applications = [];
	listService.applicationList().success(function(data) {self.applications = data;})
})

.controller("AdminStatutController", function ($scope,$http,$mdDialog,updateService,listService) {	
	
	
	
})

.controller("AdminZoneController", function ($scope,$http,$filter,$mdDialog,updateService,listService) {	
	
	
	
})

.controller("AdminUtilisateurController", function ($scope,$http,$filter,$mdDialog,updateService,listService) {	
	var self = this;
	this.users = [];
	listService.userList().success(function(data){self.users = data;})
	
	
})