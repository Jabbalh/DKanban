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

	.controller("KanbanController", function ($scope,$http) {	
	
	
    var self = this;

    
    // *********************************
    // Internal methods
    // *********************************

        
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
		    	actionTicket(result,true,objCard);
		    	
	    	}
	    	console.log(card);	    	
	    })
	  }
	  
	  function actionTicket(result, add, obj){
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
	   
	 this.cardId = "b5b4e433-5dce-48e6-bc5e-2eb3dada829b";
	 this.targetId = "user1$VFO";
	 
	 this.ticket = {};	 
	 
	 this.addTicket = function() {
		 
		 this.ticket.title = "Nouveau ticket";
		 this.ticket.insert = true;
		 this.ticket.zone = "Backlog";
		 $http.get("/api/ticket/new/empty").success(function(data){
			
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
		 $http.put("/api/ticket/update/all",data);
	 }
	 
	 
	this.test = function() {
		
		var cardData = {};
		cardData.cardId = this.cardId;
		cardData.zone = this.targetId.split('$')[1];
		cardData.userLogin = this.targetId.split('$')[0];
		
		$http.put("/api/ticket/update/zone",cardData).success(function(data){
			console.log("result -> " +data);
		});
	}
	
	$scope.$on('handleDrop',function(event,data){
		
		console.log("handleDrop -> " +data.targetId + " -> " +data.originId);
		
		var cardData = {};
		cardData.cardId = data.originId;
		cardData.zone = data.targetId.split('$')[1];
		cardData.userLogin = data.targetId.split('$')[0];
		
		$http.put("/api/ticket/update/zone",cardData).success(function(data){
			console.log("result -> " +data);
		});
		
	});
	
	this.kanban = {values:[]};
	this.headers = {};
	
	$http.get("/api/kanban/headers").success(function(headers){
		self.headers = headers;
		
		$http.get("/api/user/list").success(function(users) {
			self.kanban.users = users;
			
			users.forEach(function(uValue,uKey) {
				$http.get("/api/kanban/by/user/"+ uValue.login).success(function(tickets){
					
					self.kanban.values.push(tickets);
					
				});
			});
			
		});
	});
	


}); // END KanbanController


