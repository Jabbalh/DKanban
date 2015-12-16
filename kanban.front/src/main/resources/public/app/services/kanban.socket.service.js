function KanbanSocketService($http) {

	this.openFunction = null;
	
	this.onOpen = function(eventBus,data) {
		if (this.openFunction == null) {
			this.openFunction = data;
			eventBus.onopen = data;
		}
		
	}
    
	
		
		
	
		
		
		
	
}

angular.module("DKanbanApp").factory("socketService", function($http) {
    return new KanbanSocketService($http);
});