angular.module("DKanbanApp")
	.animation('.animate-repeat', function() {
		  return {
			  enter: function(element, done) {
		      element.css('display', 'none');
		      $(element).fadeOut(1000, function() {
		        done();
		      });
		    	done();
		    	
		    },
		    leave: function(element, done) {
		      $(element).fadeOut(1000, function() {
		        done();
		      });
		      done();
		    },
		    move: function(element, done) {
		      element.css('display', 'none');	 
		      $(element).fadeOut(500, function(){
		    	  $(element).fadeIn(500, function() {
		  	        done();
		  	      }); 
		      });
		      
		    }
		  }
		})

	.controller("KanbanController", function ($scope,$http,$filter,kanbanUpdateService,kanbanListService) {	
	
	
    var self = this;

    
	/**
	 * Objets internes
	 */
    this.ticket = {};
    this.listes = {};
    this.kanban = {values:[]};
	this.headers = {};
    var eb = new EventBus("/eventbus");
    
    /**
     * Initialisation des listes
     */
    kanbanListService.applicationList().success(function (data){
    	self.listes.applications = data;
    });
    
    /**
	 * Initialisation du kanban
	 */
	kanbanListService.headerList().success(function(headers){
		self.headers = headers;
		
		kanbanListService.userList().success(function(users) {
			self.kanban.users = users;
			self.listes.users = users;
			users.forEach(function(uValue,uKey) {
				kanbanListService.kanbanByUser(uValue.login).success(function(tickets){					
					self.kanban.values.push(tickets);
					
				});
			});
			
		});
	});
    
    /**
     * Initialisation du Bus d'écoute
     */
	  eb.onopen = function () {
	    eb.registerHandler("update-card", function (err, msg) {	    	
	    	var result = JSON.parse(msg.body);
	    	
	    	var parent = document.getElementById(result.user+'$'+result.zone);
	    	var card = document.getElementById(result.ticketId);
	    	
	    	if (card.parentElement.id == parent.id){
	    		console.log("do nothing");
	    	} else {	    		
		    	self.moveAfterChange(result,extracAfterChange(result,true));		    	
	    	}
	    	console.log(card);	    	
	    })
	  }
	  
	  /**
	   * Renvois un ticket de la collection
	   */
	  this.extracAfterChange = function(result,remove) {
		  var objCard = null;
		  var toFind = (result.user+"$"+result.state);
	    	self.kanban.values.forEach(function(value,key){	    		
	    		value.other.forEach(function(oValue,oKey){	    				    				    			    			    			
	    			oValue.cards.forEach(function(cValue,cKey){		    				
	    				if (cValue.id == result.ticketId) {	
	    					objCard = oValue.cards[cKey];
	    					if (remove) oValue.cards.splice(cKey,1);		    					
	    				}
	    			});	    			
	    		});
	    	});
	    	return objCard;
	  }
	  
	  /**
	   * Insere dans la collection après un changement
	   */
	  this.moveAfterChange = function(result,obj) {
		  var objCard = obj;
		  var toFind = (result.user+"$"+result.state);
	    	self.kanban.values.forEach(function(value,key){	    		
	    		value.other.forEach(function(oValue,oKey){	    				    			
	    			if (oValue.id == toFind) {	    				
	    				oValue.cards.push(objCard);
	    			}	    			
	    		});
	    	});
	    	return objCard;
	  }
	  
	  
	 /**
	  * Ajout d'un nouveau ticket (ouverture de la popup)
	  */
	 this.addTicket = function() {
		 
		 this.ticket.title = "Nouveau ticket";
		 this.ticket.insert = true;
		 this.ticket.zone = "BackLog";
		 kanbanUpdateService.emptyTicket().success(function(data){			
			 self.ticket.ticket = data;			 
			 self.openPopup();			
		 });
	 }
	 
	 /**
	  * Sauvegarde d'un ticket (modification ou insertion)
	  */
	 this.saveTicket = function(data) {
		 console.log(data);
		 kanbanUpdateService.updateTicket(data).success(function(resultData){
			 if (resultData == "OK"){	
				 if (data.insert == true) {
					 var result = {};
					 result.user = data.ticket.owner;
					 result.state = data.zone;
					 self.moveAfterChange(result,data.ticket);
				 } else {
					 var result = {};
					 result.user = data.ticket.owner;
					 result.state = data.zone;
					 result.ticketId = data.ticket.ref;
					 var tmp = self.extracAfterChange(result, true);
					 self.moveAfterChange(result,tmp);
				 }
				 
			 } else {
				 alert("Zut !!!");
			 }
		 });
		 
	 }
	 
	/**
	 * Mise à jour d'un ticket (ouverture de la popup)
	 */
	this.updateTicket = function(ticket, id){		
		this.ticket.title = "Ticket " + ticket.ref;
		this.ticket.insert = false;		
		this.ticket.zone = id.split('$')[1];
		this.ticket.ticket = ticket;
		 
		this.openPopup();		 
	}
	
	/**
	 * Ouverture de la popup
	 */
	this.openPopup = function() {
		$('#modal1').openModal({
		      dismissible: true, // Modal can be dismissed by clicking outside of the modal
		      opacity: .5, // Opacity of modal background
		      in_duration: 300, // Transition in duration
		      out_duration: 200, // Transition out duration
		      //ready: function() { alert('Ready'); }, // Callback for Modal open
		      complete: function() { self.ticket = {};	 } // Callback for Modal close
		    }
		  );
	}
	
	/**
	 * Ecoute de l'evènemement après le déplacement via drag & drop
	 */
	$scope.$on('handleDrop',function(event,data){		
		var cardData = {};
		cardData.cardId = data.originId;
		cardData.zone = data.targetId.split('$')[1];
		cardData.userLogin = data.targetId.split('$')[0];
		
		kanbanUpdateService.updateTicketZone(cardData).success(function(data){
			console.log("result -> " +data);
		});
		
	});
	

}); // END KanbanController


