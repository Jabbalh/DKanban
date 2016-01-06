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
    
    this.getApplicationTitle = function() {
    	return $http.get("/public/global/title");
    }
    
    this.setApplicationTitle = function(data) {
    	return $http.post("/api/global/title",data);
    }
    
    
    this.save = function(pathKey,data){
    	switch(pathKey){
	    	case "APP": 	return $http.post("/api/app/save",data);
	    	case "STATE": 	return $http.post("/api/state/save",data);    
			case "ZONE": 	return $http.post("/api/zone/save",data);		
			case "USER": 	return $http.post("/api/user/save",data);
			case "PRIORITY":return $http.post("/api/priority/save",data);   
    	}
    	
    }
    
    this.delete = function(pathKey,data){
    	switch(pathKey){
	    	case "APP": 	return $http.post("/api/app/delete",data);
	    	case "STATE": 	return $http.post("/api/state/delete",data);    
			case "ZONE": 	return $http.post("/api/zone/delete",data);		
			case "USER": 	return $http.post("/api/user/delete",data);
			case "PRIORITY": 	return $http.post("/api/priority/delete",data);
    	}
    	
    }
}

angular.module("DKanbanApp").factory("updateService", function($http) {
    return new KanbanUpdateService($http);
});