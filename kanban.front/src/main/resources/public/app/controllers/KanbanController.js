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

	.controller("KanbanController", function ($scope,$http,kanbanUpdateService,kanbanListService) {	
	
	
    var self = this;

    
    // *********************************
    // Internal methods
    // *********************************
// Initialisation des listes
    
    this.listes = {};
    kanbanListService.applicationList().success(function (data){
    	self.listes.applications = data;
    });
    
    
        
 // Initialisation du Bus d'écoute
	var eb = new EventBus("/eventbus");

	  eb.onopen = function () {
	    eb.registerHandler("update-card", function (err, msg) {	    	
	    	var result = JSON.parse(msg.body);
	    	
	    	var parent = document.getElementById(result.user+'$'+result.zone);
	    	var card = document.getElementById(result.ticketId);
	    	
	    	if (card.parentElement.id == parent.id){
	    		console.log("do nothing");
	    	} else {
	    		//$animate.move(card,parent);
		    	var objCard = actionTicket(result, false, null);
		    	self.actionTicket(result,true,objCard);
		    	
	    	}
	    	console.log(card);	    	
	    })
	  }
	  
	  this.actionTicket = function actionTicket(result, add, obj){
		  var objCard = obj;
		  var toFind = (result.user+"$"+result.state);
	    	self.kanban.values.forEach(function(value,key){	    		
	    		value.other.forEach(function(oValue,oKey){	    				    			
	    			if (add && oValue.id == toFind) {	    				
	    				oValue.cards.push(objCard);
	    			}	    			
	    			if (!add) {
		    			oValue.cards.forEach(function(cValue,cKey){		    				
		    				if (cValue.id == result.ticketId) {		    							    							    					
	    						objCard = oValue.cards[cKey];
		    					oValue.cards.splice(cKey,1);		    					
		    				}
		    			});
	    			}
	    		});
	    	});
	    	return objCard;
	  }
	   
	 	 
	 this.ticket = {};	 
	 
	 this.addTicket = function() {
		 
		 this.ticket.title = "Nouveau ticket";
		 this.ticket.insert = true;
		 this.ticket.zone = "BackLog";
		 kanbanUpdateService.emptyTicket().success(function(data){
			
			 self.ticket.ticket = data;
			 
			 $('#modal1').openModal({
			      dismissible: true, // Modal can be dismissed by clicking outside of the modal
			      opacity: .5, // Opacity of modal background
			      in_duration: 300, // Transition in duration
			      out_duration: 200, // Transition out duration
			      //ready: function() { alert('Ready'); }, // Callback for Modal open
			      complete: function() { self.ticket = {};	 } // Callback for Modal close
			    }
			  );
			
		 });
	 }
	 
	 this.saveTicket = function(data) {
		 console.log(data);
		 kanbanUpdateService.updateTicket(data).success(function(resultData){
			 if (resultData == "OK"){	
				 if (data.insert == true) {
					 var result = {};
					 result.user = data.ticket.owner;
					 result.state = data.zone;
					 self.actionTicket(result,true,data.ticket);
				 }
				 
			 } else {
				 alert("Zut !!!");
			 }
		 });
		 
	 }
	 
	this.updateTicket = function(ticket, id){
		console.log(id);
		
		this.ticket.title = "Ticket " + ticket.ref;
		 this.ticket.insert = false;
		 var tmp = id.split('$');
		 this.ticket.zone = tmp[1];
		 this.ticket.ticket = ticket;
		 
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
	
	
	$scope.$on('handleDrop',function(event,data){		
		var cardData = {};
		cardData.cardId = data.originId;
		cardData.zone = data.targetId.split('$')[1];
		cardData.userLogin = data.targetId.split('$')[0];
		
		kanbanUpdateService.updateTicketZone(cardData).success(function(data){
			console.log("result -> " +data);
		});
		
	});
	
	this.kanban = {values:[]};
	this.headers = {};
	
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
	


}); // END KanbanController


