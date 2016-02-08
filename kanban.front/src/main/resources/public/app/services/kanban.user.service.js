function KanbanUserService($http,$base64) {

    this.createNewUser =	function() 		
    { 
    	return $http.get("/public/user/new"); 
    }        
    this.userList = 		function() 		{ return $http.get("/api/user/list"); } 
    this.adminUserList =	function()		{ return $http.get("/api/admin/user/list");}
    
    this.saveNewUser= 		function(user) 	{ return $http.post("/api/user/insert", user);}
    
    var currentUser = null;
    this.getCurrentUser  = function()
    {
        var token = localStorage.getItem('id_token');
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace('-', '+').replace('_', '/');
        var decoded = JSON.parse((atob(base64)));
        return decoded.user.login;

     }
    this.setCurrentUser = function(data) 	{ this.currentUser = data; }
    
}

angular.module("DKanbanApp").factory("userService", function($http,$base64) {
    return new KanbanUserService($http,$base64);
});