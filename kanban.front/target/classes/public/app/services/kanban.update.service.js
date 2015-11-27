function KanbanUpdateService($http) {

    this.updateTicketZone = function(data) {
    	return $http.put("/api/ticket/update/zone",data);
    }
    
    this.updateTicket = function(data){
    	return $http.put("/api/ticket/update/all",data);
    }
    
    this.emptyTicket = function() {
    	return $http.get("/api/ticket/new/empty");
    }
    
    this.archiveTicket = function(data) {
    	return $http.put("/api/ticket/update/archive",data);
    }
}

angular.module("DKanbanApp").factory("kanbanUpdateService", function($http) {
    return new KanbanUpdateService($http);
});