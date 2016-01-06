function KanbanHelper(kanban)
{
	var self = this;

	/**
	   * Renvois un ticket de la collection
	   */
	  this.extracAfterChange = function(result,remove) {
		  var objCard = null;
		  
	    	kanban.values.forEach(function(value,key){	    		
	    		value.columns.forEach(function(oValue,oKey){	    				    				    			    			    			
	    			oValue.tickets.forEach(function(cValue,cKey){		    				
	    				if (cValue._id == result._id) {	
	    					objCard = oValue.tickets[cKey];	    					
	    					if (remove) {
	    						oValue.tickets.splice(cKey,1);	    						
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
	    		value.columns.forEach(function(oValue,oKey){	    				    				    			    			    			
	    			oValue.tickets.forEach(function(cValue,cKey){		    				
	    				if (cValue._id == ticketId) {	
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
		  var toFind = (result.owner.code+"$"+result.zone.code);
	    	kanban.values.forEach(function(value,key){	    		
	    		value.columns.forEach(function(oValue,oKey){	    				    			
	    			if (oValue.id == toFind) {	   	    				
	    				oValue.tickets.push(objCard);	    					    				
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
		  return document.getElementById(result.owner.code +'$'+result.zone.code);
	  }
	  
	  /**
	   * Renvois la ticket provenant du document HTML
	   */
	  this.getCardFromDocument = function(result) {
		  var card = document.getElementById(result._id);
      	
      	//if (card == null){ card = document.getElementById(result.card.id); }
      	
      	return card;
	  }

}