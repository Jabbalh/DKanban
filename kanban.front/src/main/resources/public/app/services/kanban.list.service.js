function KanbanListService($http) {

    this.applicationList = 			function() { return $http.get("/api/application/list"); }        
       
    this.headerList = 				function(){ return $http.get("/api/kanban/headers"); }    
    this.kanbanByUser = 			function(login) { return $http.get("/api/kanban/by/user/"+login); }    
    this.stateList = 				function() { return $http.get("/api/state/list"); }    
    this.zoneList = 				function() { return $http.get("/api/zone/list"); }
    
    this.adminZoneList = 			function(){ return $http.get("/api/admin/zone/list");}
    
    this.adminApplicationList = 	function(){ return $http.get("/api/admin/application/list"); }
    this.adminStateList = 			function(){ return $http.get("/api/admin/state/list"); }
    
    this.adminPriorityList = 			function(){ return $http.get("/api/admin/priority/list"); }
}

angular.module("DKanbanApp").factory("listService", function($http) {
    return new KanbanListService($http);
});