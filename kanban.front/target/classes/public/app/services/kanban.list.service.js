function KanbanListService($http) {

    this.applicationList = function() {
    	return $http.get("/api/application/list");
    }
    
    this.userList = function(){
    	return $http.get("/api/user/list");
    }
    
    this.headerList = function(){
    	return $http.get("/api/kanban/headers");
    }
    
    this.kanbanByUser = function(login) {
    	return $http.get("/api/kanban/by/user/"+login);
    }
}

angular.module("DKanbanApp").factory("kanbanListService", function($http) {
    return new KanbanListService($http);
});