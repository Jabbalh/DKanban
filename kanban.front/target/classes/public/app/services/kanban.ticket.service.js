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
		
		
		
	
}

angular.module("DKanbanApp").factory("ticketService", function($http) {
    return new KanbanTicketService($http);
});