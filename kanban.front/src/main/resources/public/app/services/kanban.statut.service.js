function KanbanStatutService($http) {    
    this.insertStatut= 		function(statut) 	{ return $http.post("/api/state/insert", statut);}
    this.insertPriority=	function(priority) 	{ return $http.post("/api/priority/insert",priority);}
}

angular.module("DKanbanApp").factory("statutService", function($http) {
    return new KanbanStatutService($http);
});