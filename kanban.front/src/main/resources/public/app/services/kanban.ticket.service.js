function KanbanTicketService($http) {

    
	this.cloneTicket = function(ticket) {		
		var result = {
				ref 		: ticket.ref,
				id			: ticket.id,
				appli		: ticket.appli,
				summary		: ticket.summary,
				description	: ticket.description,
				caisse		: ticket.caisse,
				state		: ticket.state,
				owner		: ticket.owner,
				history		: ticket.history
		};
		return result;
		
	}
	
	this.restorTicket = function(origin, updated) {
		origin.ref 			= updated.ref;
		origin.id			= updated.id;
		origin.appli		= updated.appli;
		origin.summary		= updated.summary;
		origin.description	= updated.description;
		origin.caisse		= updated.caisse;
		origin.state		= updated.state;
		origin.owner		= updated.owner;	
		origin.history		= updated.history;
	}
	
	this.isModified = function(origin, returned) {
		var properties = ['ref', 'id','appli','summary','description','caisse','state','owner'];	
		var result = false;
		properties.forEach(function(value){
			if (origin[value] != returned[value]){
				result = true;
			}
		});
		
		if (returned.history != null && origin.history == null) {
			result = true;
		}		
		else if (returned.history.length != origin.history.legth){
			result = true;
		} else if (returned.history != null && origin.history != null){
			returned.history.forEach(function(value){
				origin.history.forEach(function(valueOrigin){
					if (value.id = valueOrigin.id){
						if (value.summary != valueOrgin.summary) result = true;
						else if (value.description = valueOrigin.description) result = true;
					}
					if (result = true) return result;
				});
				if (result = true) return result;
			});
		}
		
		return result;
		
	}
		
		
		
	
}

angular.module("DKanbanApp").factory("ticketService", function($http) {
    return new KanbanTicketService($http);
});