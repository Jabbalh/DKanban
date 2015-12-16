function KanbanUpdateService($http) {

    this.updateTicketZone = function(data) {
    	return $http.post("/api/ticket/update/zone",data);    	
    }
    
    this.updateTicket = function(data){
    	return $http.post("/api/ticket/update/all",data);
    }
    
    this.emptyTicket = function() {
    	return $http.get("/api/ticket/new/empty");
    }
    
    this.archiveTicket = function(data) {
    	return $http.post("/api/ticket/update/archive",data);
    }
    
    this.deleteTicket = function(data) {
    	return $http.post("/api/ticket/delete", data);
    }
}

angular.module("DKanbanApp").factory("updateService", function($http) {
    return new KanbanUpdateService($http);
});