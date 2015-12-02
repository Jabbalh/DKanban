angular.module("DKanbanApp")

.controller("LoginController", function ($rootScope,$http,$location) {	
	this.login = "";
	this.password = "";
	
	var self = this;
	
	this.loginAction = function(){
		$http.post("/public/login", {l : self.login, p : self.password}).success(function(data){			
			localStorage.setItem("id_token",data);
			$rootScope.$broadcast("authenticate",true);			
			$location.path("/kanban");
		});
	}
	
});