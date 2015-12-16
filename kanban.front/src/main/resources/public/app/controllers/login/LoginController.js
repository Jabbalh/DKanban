angular.module("DKanbanApp")

.controller("LoginController", function ($rootScope,$http,$location) {	
	this.login = "";
	this.password = "";
	
	var self = this;
	this.message = "";
	this.auth_ko = false;
	
	this.loginAction = function(){
		this.message = "";
		$http.post("/public/user/authenticate", {login : self.login, password : self.password}).success(function(data){
			
			if (data != "KO"){
				localStorage.setItem("id_token",data);
				$rootScope.$broadcast("authenticate",true);			
				$location.path("/kanban");
			} else {
				self.message = "Nom d'utilisateur ou mot de passe incorrect";
				self.auth_ko = true;
			}
			
			
		});
	}
	
});