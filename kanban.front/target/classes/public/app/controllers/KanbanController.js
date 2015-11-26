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
     * Méthode de test
     */
    this.testCardId = "ARS03";
    this.testTargetId = "user1$PROD";
    
    this.testUpdate = function() {
    	
    	var result = {
    			user : self.testTargetId.split("$")[0],
    			zone : self.testTargetId.split("$")[1],
    			card : {id : self.testCardId}
    	};
    	
    	//var data = self.extracAfterChange(result,false);
    	
    	kanbanUpdateService.updateTicketZone(result);
    }
    
    this.testDelete = function() {
    	/*
    	  {
      "ref" : "ARS03",
      "id" : "ARS03",
      "appli" : "DEI PRO",
      "summary" : "Test ARS3",
      "description" : "Desc ARS3",
      "caisse" : "14445",
      "state" : null,
      "owner" : "user1"
    }
    	 */
    }
    
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
	    	
	    	if (card == null){
	    		card = document.getElementById(result.card.id);
	    	}	    		
	    	
	    	if (card != null && parent != null && card.parentElement.id == parent.id){
	    		console.log("do nothing");
	    	} else {	    		
		    	self.moveAfterChange(result,self.extracAfterChange(result,true));
		    	$scope.$digest();
	    	}	    	
	    	
	    });
	    
	    eb.registerHandler("insert-card", function(err, msg) {
	    	var result = JSON.parse(msg.body);	    		    	
    		self.moveAfterChange(result, result.card);
    		$scope.$digest();
	    });
	  }
	  
	  /**
	   * Renvois un ticket de la collection
	   */
	  this.extracAfterChange = function(result,remove) {
		  var objCard = null;
		  var toFind = (result.user+"$"+result.zone);
	    	self.kanban.values.forEach(function(value,key){	    		
	    		value.other.forEach(function(oValue,oKey){	    				    				    			    			    			
	    			oValue.cards.forEach(function(cValue,cKey){		    				
	    				if (cValue.id == result.card.id) {	
	    					objCard = oValue.cards[cKey];
	    					console.log("extracAfterChange -> Obj finded");
	    					if (remove) {
	    						oValue.cards.splice(cKey,1);
	    						console.log("extracAfterChange -> Obj removed");
	    					}
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
		  var toFind = (result.user+"$"+result.zone);
	    	self.kanban.values.forEach(function(value,key){	    		
	    		value.other.forEach(function(oValue,oKey){	    				    			
	    			if (oValue.id == toFind) {	   
	    				console.log("moveAfterChange -> Obj finded");
	    				oValue.cards.push(objCard);	    				
	    				console.log("moveAfterChange -> Obj added");
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
				/* if (data.insert != true) {					
					 var result = {};
					 result.user = data.ticket.owner;
					 result.zone = data.zone;
					 result.ticketId = data.ticket.ref;
					 var tmp = self.extracAfterChange(result, true);
					 self.moveAfterChange(result,tmp);
				 }*/
				 
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
		      dismissible: false, // Modal can be dismissed by clicking outside of the modal
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
		var cardData = 
		{
				card : {id : data.originId},
				zone : data.targetId.split('$')[1],
				user : data.targetId.split('$')[0]
		};
		/*cardData.ticketId = ;
		cardData.zone = data.targetId.split('$')[1];
		cardData.userLogin = data.targetId.split('$')[0];*/
		
		kanbanUpdateService.updateTicketZone(cardData).success(function(data){
			console.log("handleDrop -> updateTicketZone -> "  +data);
		});
		
	});
	
	$scope.$on('handleDropDelete',function(event,data){
		var cardData = 
		{
				card : {id : data.originId},
				zone : data.targetId.split('$')[1],
				user : data.targetId.split('$')[0]
		};
		/*
		cardData.ticketId = data.originId;
		cardData.zone = data.targetId.split('$')[1];
		cardData.userLogin = data.targetId.split('$')[0];
		*/
		var card = self.extracAfterChange(cardData,false);
		kanbanUpdateService.archiveTicket(card);		
	});
	
	
	this.changeTab = function(id) {
		$(document).ready(function(){
		    $('ul.tabs').tabs('select_tab', '#'+id);
		  });
	}
	

}); // END KanbanController


