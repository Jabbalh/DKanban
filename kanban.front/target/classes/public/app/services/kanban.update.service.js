function KanbanUpdateService($http) {

    this.updateTicketZone = function(data) {
    	var result =  $http.post("/api/ticket/update/zone",data);
    	
    	result.success(function(data){
    		console.log("updateTicketZone -> success -> " + result);
    	});
    	result.error(function(data){
    		console.log("updateTicketZone -> error -> " + result);
    	});
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
}

angular.module("DKanbanApp").factory("updateService", function($http) {
    return new KanbanUpdateService($http);
});