function KanbanHelper(kanban)
{
	var self = this;

	/**
	   * Renvois un ticket de la collection
	   */
	  this.extracAfterChange = function(result,remove) {
		  var objCard = null;
		  var toFind = (result.user+"$"+result.zone);
	    	kanban.values.forEach(function(value,key){	    		
	    		value.other.forEach(function(oValue,oKey){	    				    				    			    			    			
	    			oValue.cards.forEach(function(cValue,cKey){		    				
	    				if (cValue.id == result.card.id) {	
	    					objCard = oValue.cards[cKey];	    					
	    					if (remove) {
	    						oValue.cards.splice(cKey,1);	    						
	    					}
	    				}
	    			});
	    			if (objCard != null) return objCard;
	    		});
	    		if (objCard != null) return objCard;
	    	});
	    	return objCard;
	  }
	  
	  /**
	   * Recherche de la zone
	   */
	  this.searchZone = function(ticketId) {
		  var result = null;
	    	kanban.values.forEach(function(value,key){	    		
	    		value.other.forEach(function(oValue,oKey){	    				    				    			    			    			
	    			oValue.cards.forEach(function(cValue,cKey){		    				
	    				if (cValue.id == ticketId) {	
	    					result =  oValue.id;
	    					return result;
	    				}
	    			});
	    			if (result != null) return;
	    		});
	    		if (result != null) return;
	    	});
	    	return result;
	  }
	  
	  /**
	   * Insere dans la collection après un changement
	   */
	  this.moveAfterChange = function(result,obj) {
		  var objCard = obj;
		  var done = false;
		  var toFind = (result.user+"$"+result.zone);
	    	kanban.values.forEach(function(value,key){	    		
	    		value.other.forEach(function(oValue,oKey){	    				    			
	    			if (oValue.id == toFind) {	   	    				
	    				oValue.cards.push(objCard);	    					    				
	    				done = true;
	    			}	    			
	    		});
	    		if (done) return;
	    	});
	    	return objCard;
	  }
	  
	  /**
	   * Renvois la zone du parent d'un ticket
	   */
	  this.getCardZoneFromDoc = function(result) {
		  return document.getElementById(result.user+'$'+result.zone);
	  }
	  
	  /**
	   * Renvois la ticket provenant du document HTML
	   */
	  this.getCardFromDocument = function(result) {
		  var card = document.getElementById(result.ticketId);
      	
      	if (card == null){ card = document.getElementById(result.card.id); }
      	
      	return card;
	  }

}