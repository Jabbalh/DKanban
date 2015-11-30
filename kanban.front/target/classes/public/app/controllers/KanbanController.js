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

	.controller("KanbanController", function ($scope,$http,$filter,$mdDialog,updateService,listService,ticketService) {	
	
	
    var self = this;
    var currentUser = 'user1';
    
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
    	
    	
    	updateService.updateTicketZone(result);
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
    listService.applicationList().success(function (data){
    	self.listes.applications = data;
    });
    
    /**
	 * Initialisation du kanban
	 */
    listService.headerList().success(function(headers){
		self.headers = headers;
		self.filtreOnAll();
		
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
	  
	  this.filtreOnUs = function() {

		  $http.get("/api/user/"+currentUser).success(function(result){
			 var data = JSON.parse(result);
			
			 self.kanban.values = [];
			 self.kanban.users = [];
			 
			 self.kanban.users.push(data);
			 listService.kanbanByUser(data.login).success(function(tickets){					
					self.kanban.values.push(tickets);
					
				});
		  });		  		 
	  }
	  
	  this.filtreOnAll = function() {
		  self.kanban.values = [];
		  self.kanban.users = [];
		  listService.userList().success(function(users) {
				self.kanban.users = users;
				self.listes.users = users;
				users.forEach(function(uValue,uKey) {
					listService.kanbanByUser(uValue.login).success(function(tickets){					
						self.kanban.values.push(tickets);
						
					});
				});
				
			});
	  }
	  
	 /**
	  * Ajout d'un nouveau ticket (ouverture de la popup)
	  */
	  
	 this.openNewTicket = function(ev) {		 
		 updateService.emptyTicket().success(function(data){			
			 var send = {
						listes : self.listes,
						ticket :  {
								title : "Nouveau ticket",
								insert : true,
								zone : "BackLog",
								card : data,
								user : ""
						},
						headers : self.headers					
					};			 
			 self.openPopup(ev,send);			
		 });
	 }
	 
	 
	 /**
	  * Sauvegarde d'un ticket (modification ou insertion)
	  */
	 this.saveTicket = function(data) {
		 console.log(data);
		 updateService.updateTicket(data).success(function(resultData){
			 if (resultData != "OK"){								 
				 alert("Zut !!!");
			 }
		 });
		 
	 }
	 
	/**
	 * Mise à jour d'un ticket (ouverture de la popup)
	 */
	this.updateTicket = function($event,ticket, id){				
		var send = {
				listes : self.listes,
				ticket :  {
						title : "Ticket " + ticket.ref,
						insert : false,
						zone : id.split('$')[1],
						card : ticket,
						user : ticket.owner
				},
				headers : self.headers					
			};				
		this.openPopup(null,send)			          	 
	}
	
	/**
	 * Ouverture de la popup
	 */
	this.openPopup = function(ev,send) {
		var cloneTicket = null;
		if (send.ticket.insert == false) {
			cloneTicket = send.ticket.card;
			send.ticket.card = ticketService.cloneTicket(send.ticket.card);
		}		
		$mdDialog.show({
		      controller: DialogController,
		      templateUrl: '/app/views/kanbanPopup.html',
		      parent: angular.element(document.body),
		      targetEvent: ev,
		      clickOutsideToClose:true,
		      locals: {
		           item: send
		         },
		    }).then(function(answer) {
		    	if (send.ticket.insert == false) {
					ticketService.restorTicket(cloneTicket,send.ticket.card);					
				}
		    	console.log("save -> " + JSON.stringify(answer));
		    	self.saveTicket(answer);
		     }, function() {
		          
		        });
	}
	
	/**
	 * Ecoute de l'evènemement après le déplacement via drag & drop
	 */
	$scope.$on('handleDrop',function(event,data){	
		
		var cardData = 
		{
				card : {					
					id : data.originId
					},
				zone : data.targetId.split('$')[1],
				user : data.targetId.split('$')[0]
		};
		
		updateService.updateTicketZone(cardData);
				
		//$http.post("/api/ticket/update/zone",cardData);
		
	});
	
	$scope.$on('handleDropDelete',function(event,data){
		var cardData = 
		{
				card : {id : data.originId},
				zone : data.targetId.split('$')[1],
				user : data.targetId.split('$')[0]
		};		
		var card = self.extracAfterChange(cardData,false);
		updateService.archiveTicket(card);		
	});
	
	

}); // END KanbanController

function DialogController($scope, $mdDialog,item) {
	
	$scope.listes = item.listes;
	$scope.ticket = item.ticket;
	$scope.headers = item.headers;
	
	  $scope.hide = function() {
	    $mdDialog.hide();
	  };
	  $scope.cancel = function() {
	    $mdDialog.cancel();
	  };
	  $scope.answer = function(answer) {
		answer.user = answer.card.owner;
	    $mdDialog.hide(answer);
	  };
	}

