function KanbanTicketService($http) {

	
	this.getSearchQuery = function(){
	    return $http.get("/api/ticket/search/get");
	}
    
	this.cloneTicket = function(ticket) {		
		var result = {
				reference 	: ticket.reference,
				_id			: ticket._id,
				application	: ticket.application,
				summary		: ticket.summary,
				description	: ticket.description,
				caisse		: ticket.caisse,
				statut		: ticket.statut,
				owner		: ticket.owner,
				zone		: ticket.zone,
				histories	: ticket.histories,
				priority	: ticket.priority,
				archive     : ticket.archive
		};
		return result;
		
	}
	
	this.restorTicket = function(origin, updated) {
		origin.reference	= updated.reference;
		origin._id			= updated._id;
		origin.application	= updated.application;
		origin.summary		= updated.summary;
		origin.description	= updated.description;
		origin.caisse		= updated.caisse;
		origin.statut		= updated.statut;
		origin.owner		= updated.owner;
		origin.zone			= updated.zone;	
		origin.histories	= updated.histories;
		origin.priority		= updated.priority;
		origin.archive      = updated.archive;
	}
	
	this.isModified = function(origin, returned) {
		var properties = ['reference', '_id','application.code','summary','description','caisse','statut.code','owner','priority.code', 'archive'];
		var result = false;
		properties.forEach(function(value){
			if (origin[value] != returned[value]){
				result = true;
			}
		});
		
		if (returned.histories != null && origin.histories == null) {
			result = true;
		}		
		else if (returned.histories.length != origin.histories.legth){
			result = true;
		} else if (returned.histories != null && origin.histories != null){
			returned.histories.forEach(function(value){
				origin.histories.forEach(function(valueOrigin){
					if (value._id = valueOrigin._id){
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