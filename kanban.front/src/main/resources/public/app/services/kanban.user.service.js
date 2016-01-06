function KanbanUserService($http) {

    this.createNewUser =	function() 		
    { 
    	return $http.get("/public/user/new"); 
    }        
    this.userList = 		function() 		{ return $http.get("/api/user/list"); } 
    this.adminUserList =	function()		{ return $http.get("/api/admin/user/list");}
    
    this.saveNewUser= 		function(user) 	{ return $http.post("/api/user/insert", user);}
    
    var currentUser = null;
    this.getCurrentUser  = function() 		{ return currentUser; }
    this.setCurrentUser = function(data) 	{ currentUser = data; }
    
}

angular.module("DKanbanApp").factory("userService", function($http) {
    return new KanbanUserService($http);
});