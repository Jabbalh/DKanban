angular.module("DKanbanApp")

.controller("LoginController", function ($scope,$http,$location) {	
	this.login = "";
	this.password = "";
	
	var self = this;
	
	this.loginAction = function(){
		$http.post("/public/login", {l : self.login, p : self.password}).success(function(data){
			alert(data);
			localStorage.setItem("id_token",data);
			$location.path('/kanban');
		});
	}
	
});