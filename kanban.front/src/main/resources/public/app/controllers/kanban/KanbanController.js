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

	.controller("KanbanController", function ($scope,$http,$filter,$mdDialog,updateService,listService,ticketService,userService, socketService,$animate) {


    var self = this;
    this.currentUser = userService.getCurrentUser();

	/**
	 * Objets internes
	 */
    this.ticket = {};
    this.listes = {};
    this.kanban = {values:[]};
	this.headers = {};
	this.zones = [];
    var eb = new EventBus("/eventbus");
    var helper = new KanbanHelper(this.kanban);
    helper.setFindZone(function(result)
    {
        return result.owner.code+"$"+result.zone.code;
    })



    /**
     * Initialisation des listes
     */
    listService.applicationList().success(function (data) 	{ self.listes.applications = data;});
    listService.stateList().success(function(data)			{ self.listes.states = data; });
    listService.zoneList().success(function(data)			{ self.zones = data;})
    listService.priorityList().success(function(data)		{ self.listes.priorities = data; });
    listService.versionList().success(function(data)
    {
        self.listes.versions = data;
    });
    /**
	 * Initialisation du kanban
	 */
    listService.headerList().success(function(headers)		{ self.headers = headers; self.filtreOnAll(); });



    /**
     * Initialisation du Bus d'écoute
     */
    eb.onopen = function() {

    	if (localStorage.getItem('id_token') != null) {
    		// Evènement sur la mise à jour d'un ticket
    		eb.registerHandler("update-card",function (err, msg) {
                self.updateCard(msg);
            });

    		// Evènement sur l'insertion d'un ticket
    	    eb.registerHandler("insert-card", function(err, msg) {
            	self.insertCard(msg);
            });

    	    // Evènement sur la suppression ou l'archivage d'un ticket
    	    eb.registerHandler("delete-card", function(err,msg){
    	    	self.deleteCard(msg);
    	    });
    	}


    }

    this.updateCard = function(msg) {
        var result = JSON.parse(msg.body);
            var parent = helper.getCardZoneFromDoc(result.owner.code +'$'+result.zone.code);

            var card = helper.getCardFromDocument(result);

            if (card == null) {
                self.insertCard(msg);
            } else {
                if (result.archive == true) {
                    self.deleteCard(msg);
                } else {
                    var origin = helper.extracAfterChange(result,false);

                    if (origin != null && result != null && ticketService.isModified(origin,result)){
                        ticketService.restorTicket(origin,result);
                        if (card.parentElement.id != parent.id){
                            helper.moveAfterChange(result,helper.extracAfterChange(result,true));
                        }
                        $scope.$digest();

                    } else if (card.parentElement.id != parent.id){
                        helper.moveAfterChange(result,helper.extracAfterChange(result,true));
                        $scope.$digest();
                    }

                    $animate.enabled(true);
                }


            }


    }

    this.insertCard = function(msg) {
        var result = JSON.parse(msg.body);
        helper.moveAfterChange(result, result);
        $scope.$digest();
    }

    this.deleteCard = function(msg) {
        var result = JSON.parse(msg.body);
        var origin = helper.extracAfterChange(result,true);
        $scope.$digest();
    }

    /**
     * Filtre sur le current user
     */
	  this.filtreOnUs = function() {
		  $http.get("/api/user/"+this.currentUser).success(function(result){
			 var data = JSON.parse(result);
			 self.kanban.values = [];
			 self.kanban.users = [];
			 self.kanban.users.push(data);
			 listService.kanbanByUser(data.login).success(function(tickets){
					self.kanban.values.push(tickets);
				});
		  });
	  }

	  /**
	   * Pas de filtre
	   */
	  this.filtreOnAll = function() {
		  self.kanban.values = [];
		  self.kanban.users = [];
		  userService.userList().success(function(users) {
				self.kanban.users = users;
				self.listes.users = users;
				users.forEach(function(uValue,uKey) {
					listService.kanbanByUser(uValue.code).success(function(tickets){
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
								user : this.currentUser
						},
						headers : self.zones
					};
			 self.openPopup(ev,send);
		 });
	 }


	 /**
	  * Sauvegarde d'un ticket (modification ou insertion)
	  */
	 this.saveTicket = function(data) {
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
						title : "Ticket " + ticket.reference,
						insert : false,
						zone : id.split('$')[1],
						card : ticket,
						user : ticket.owner
				},
				headers : self.zones
			};
		this.openPopup(null,send)
	}

	/**
	 * Recherche d'un ticket
	 */
	this.searchTicket = function(ev) {
		ticketService.getSearchQuery().success(function(data){
		    var send = {
            				listes : self.listes,
            				ticket :  {
            						title : "Recherche d'un ticket",
            						insert : false,
            						zone : 0,
            						card : data,
            						user : self.currentUser
            				},
            				headers : self.zones
            			};

            		$mdDialog.show({
            		      controller: DialogSearchController,
            		      templateUrl: '/app/views/kanban/kanbanPopupSearch.html',
            		      parent: angular.element(document.body),
            		      targetEvent: ev,
            		      clickOutsideToClose:true,
            		      locals: {
            		           item: send
            		         },
            		    }).then(function(answer) {
            		        if (answer == null ) return;
            		    	var  zone = helper.searchZone(answer._id);
            		    	if (zone == null) {
                                zone = answer.zone.code;
            		    	} else {
            		    	   zone = zone.split("$")[1];
            		    	}
                            var send = {
                                listes : self.listes,
                                ticket :  {
                                        title : "Ticket " + answer.reference,
                                        insert : false,
                                        zone : zone,
                                        card : answer,
                                        user : answer.owner
                                },
                                headers : self.zones
                            };
                            self.openPopup(null,send)


            		     }, function() { });
		});

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
		$mdDialog
			.show({
		      controller: DialogController,
		      templateUrl: '/app/views/kanban/kanbanPopup.html',
		      parent: angular.element(document.body),
		      targetEvent: ev,
		      clickOutsideToClose:true,
		      locals: {  item: send },
		    })
		    .then(function(answer) {
		    	if (answer.delete != 'undefined' && answer.delete == true){
		    		updateService.deleteTicket(answer.card).success(function(data){

		    		});
		    	} else if (answer.archive != 'undefined' && answer.archive == true) {
                    updateService.archiveTicket(answer.card);

		    	} else {
			    	if (send.ticket.insert == false) {
						ticketService.restorTicket(cloneTicket,send.ticket.card);
					}
			    	self.saveTicket(answer);
		    	}
		     }, function() { });
	}

	/**
	 * Ecoute de l'evènemement après le déplacement via drag & drop
	 */
	this.handleDrop = function(data, event,id){
		if (data != null && data.data != null)
		{
			zone = helper.searchZone(data.data.card.id);
			if (zone != id){
				$animate.enabled(false);
				$http.post("/api/ticket/update/zone",{data : self.cardForDropEvent(data,id)});
			}
		}
	};

	/**
	 * Handler sur l'archivage d'un ticket
	 */
	this.handleDropDelete = function(data,event, id){
		var card = helper.extracAfterChange(self.cardForDropEvent(data,id),false);
		updateService.archiveTicket(card);
	};



	/**
	 * Renvois le ticket lors d'un evènement de drop
	 */
	this.cardForDropEvent = function(data,id) {
		return cardData =
		{
				card : data.data.card,
				zone : id.split("$")[1],
				user : id.split("$")[0]
		};
	}


}) // END KanbanController

.controller('TicketHistoryCreateCtrl', function($scope, $mdBottomSheet) {

	$scope.history = {};
	moment.locale('fr');
	//$scope.history.dateCreation = moment(moment()).format('DD/MM/YYYY HH:mm');

	  $scope.addHistory = function() {

		  //$scope.history.dateCreation = moment($scope.history.dateCreation).format('DD/MM/YYYY HH:mm')
		  $mdBottomSheet.hide($scope.history);
	  };
	})

function DialogController($scope, $mdDialog,item,$mdToast,$mdBottomSheet) {

	$scope.listes = item.listes;
	$scope.ticket = item.ticket;
	$scope.headers = item.headers;

	/**
	 * Ajout d'un nouveau historique
	 */
	$scope.addNew = function(event){
		    $mdBottomSheet.show({
		      templateUrl: '/app/views/kanban/ticketHistoryCreate.html',
		      controller: 'TicketHistoryCreateCtrl',
		      clickOutsideToClose: true,
		      targetEvent: event,
		      parent : angular.element(document.getElementById('ticketDialog'))
		    }).then(function(answer) {
		      //console.log(JSON.stringify($scope.ticket));
		      $scope.ticket.card.histories.push(answer);
		    });
	}

	/**
	 * Suppression d'un ticket
	 */
	$scope.delete = function(ticket){
		ticket.delete = true;
		$mdDialog.hide(ticket);
	}

	/**
	 * SUppression d'un élément de l'historique
	 */
	$scope.deleteHistory = function(item) {
		var index = $scope.ticket.card.histories.indexOf(item);
		$scope.ticket.card.histories.splice(index,1);
	}

	$scope.archive = function(ticket) {
	    ticket.archive = true;
	    $mdDialog.hide(ticket);
	}

	/**
	 * Fermeture de la popup (cancel)
	 */
	$scope.hide = function() {
		$mdDialog.hide();
	};

	/**
	 * Fermetire de la popup cancel
	 */
	$scope.cancel = function() {
		$mdDialog.cancel();
	};

	/**
	 * Renvois du résultat à l'appelant
	 */
	$scope.answer = function(answer) {
		answer.user = answer.card.owner;
		$mdDialog.hide(answer);
	};


}

/**
 * Controller pour la recherche de ticket
 * @param $scope
 * @param $mdDialog
 * @param $http
 * @param item
 */
function DialogSearchController($scope, $mdDialog,$http,item) {

	$scope.listes = item.listes;
	$scope.tickets = [];
	$scope.headers = item.headers;
	$scope.search = {};


	$scope.hasFind = false;

	/**
	 * Fermeture de la popup
	 */
	$scope.hide = function() {
		$mdDialog.hide();
	};

	/**
	 * Fermeture de la popup
	 */
	$scope.cancel = function() {
	  $mdDialog.cancel();
	};

	/**
	 * Recherhe du ticket (execution de la requète)
	 */
	$scope.answer = function(answer) {
		$http.post("/api/ticket/search", {data:$scope.search}).success(function(data){
			$scope.tickets = data;
		})
	};

	/**
	 * Renvois du ticket recherché (demande d'ouverture)
	 */
	$scope.openTicket = function(ticket) {
		$mdDialog.hide(ticket);
	}

}

