angular.module("DKanbanApp")

.controller("AdminController", function ($state,updateService) {	
	
	var self = this;
	
	this.goTo = function(route) {
		$state.go(route);
	}
	
	
	this.buttonValue = "editer";
	this.isNoEdit = true;
	
	this.applicationTitle = "";
	
	updateService.getApplicationTitle().success(function(data){self.applicationTitle = data;})
	
	this.updateTitle = function() {
		if (this.isNoEdit == false){
			updateService.setApplicationTitle({title:this.applicationTitle});
			this.isNoEdit = true;
			this.buttonValue = "editer";
		} else {
			this.isNoEdit = false;
			this.buttonValue = "sauver";
		}
		
	}
	
})

.controller("AdminAppController", function ($scope,$state,listService) {	
	
	var self = this;
	this.paramList = [];
	this.ParamTitle = "Applications";
	
	listService.adminApplicationList().success(function(data) {self.paramList = data;})
	
	this.go = function(item) {
		$state.go("admin.application.up",{data:item});
	}
})

.controller("AdminStatutController", function ($scope,$state,listService,statutService,$mdDialog) {	
	var self = this;
	this.Paramtitle = "Statut";
	this.paramList = [];
	listService.adminStateList().success(function(data) {self.paramList = data;})
	
	this.go = function(item) {
		$state.go("admin.statut.up",{data:item});
	}
	
	this.add = function(ev){
		$mdDialog.show({
		      controller: StatutAddCtrl,
		      templateUrl: '/app/views/admin/param.color.add.html',
		      parent: angular.element(document.body),
		      targetEvent: ev,
		      clickOutsideToClose:true,
		      locals: {
		           item: {}
		         },
		    }).then(function(answer) {	
		    	if (answer != null) {
		    		statutService.insertStatut({data:answer}).success(function(data){
			    		console.log(data);
			    		self.paramList.push(data);			    		
			    	});
		    	}
		    	
		     }, function() { });
	}
	
	function StatutAddCtrl($scope, $mdDialog,item){
		$scope.internalData = item;
		$scope.add = function(){
			$mdDialog.hide($scope.internalData);
		}
		
		$scope.closeAdd = function () {
			$mdDialog.hide();
		}
	}
		
	
})

.controller("AdminZoneController", function ($scope,$state,listService) {	
	var self = this;
	this.Paramtitle = "Zone";
	this.paramList = [];
	
	listService.adminZoneList().success(function(data) {self.paramList = data;})
	
	this.go = function(item) {
		$state.go("admin.zones.up",{data:item});
	}
	
})

.controller("AdminUtilisateurController", function ($scope,$state,userService,$mdDialog) {	
	var self = this;
	this.users = [];
	userService.adminUserList().success(function(data){ self.users = data; });
	
	this.go = function(item) {
		$state.go("admin.utilisateur.up",{data:item});
	}
	
	this.add = function(ev){
		
		userService.createNewUser().error(function(error){
			console.log("error -> " + error);
		}).success(function(data){			
			$mdDialog.show({
			      controller: UserAddCtrl,
			      templateUrl: '/app/views/admin/utilisateur.add.html',
			      parent: angular.element(document.body),
			      targetEvent: ev,
			      clickOutsideToClose:true,
			      locals: {
			           item: {}
			         },
			    }).then(function(answer) {	
			    	if (answer != null) {
			    		userService.saveNewUser(answer).success(function(data){			    		
				    		self.users.push(data);			    		
				    	});
			    	}
			    	
			     }, function() { });
		});
		
		
	}
	
	function UserAddCtrl($scope, $mdDialog,item){
		$scope.user = item;
		$scope.addUser = function(){
			$mdDialog.hide($scope.user);
		}
		
		$scope.closeAddUser = function () {
			$mdDialog.hide();
		}
	}

})

.controller("AdminUtilisateurUpController", function ($scope,$http,$filter,$state,$stateParams,$mdDialog,updateService) {
	var self = this;
	this.user = $stateParams.data	
	
	this.save = function(){
		updateService.save($stateParams.key,{data:this.user}).success(function(data){
			console.log("User.save -> " + data);
		})
	}
	
	this.updatePassword = function(){

		$mdDialog.show({
        			      controller: UserUpdatePassword,
        			      templateUrl: '/app/views/admin/utilisateur.mdp.html',
        			      parent: angular.element(document.body),
        			      clickOutsideToClose:true,
        			      locals: {
        			           item: {login:self.user.login}
        			         },
        			    }).then(function(answer) {


        			     }, function() { });
	}


	function UserUpdatePassword($scope,$mdDialog,item){
    	    $scope.data = item;
    	    $scope.save = function() {
    	        updateService.updateUserPassword({data : $scope.data}).success(function(data) {
    	            if (data == true) {
    	                $mdDialog.hide($scope.data);
    	            } else {
    	                $scope.error = true;
    	                $scope.errorMsg = data;
    	            }
    	        });

    	    }

    	    $scope.cancel = function(){
    	        $mdDialog.hide();
    	    }
    	}
		
})

.controller("AdminPriorityController", function ($scope,$state,listService,statutService,$mdDialog) {	
	var self = this;
	this.paramList = [];
	this.Paramtitle = 'Priorités';
	
	listService.adminPriorityList().success(function(data) {self.paramList = data;})
	
	this.go = function(item) {
		$state.go("admin.priority.up",{data:item});
	}
	
	this.add = function(ev){
		$mdDialog.show({
		      controller: PriorityAddCtrl,
		      templateUrl: '/app/views/admin/param.color.add.html',
		      parent: angular.element(document.body),
		      targetEvent: ev,
		      clickOutsideToClose:true,
		      locals: {
		           item: {}
		         },
		    }).then(function(answer) {	
		    	if (answer != null) {
		    		statutService.insertPriority({data:answer}).success(function(data){
			    		console.log(data);
			    		self.paramList.push(data);			    		
			    	});
		    	}
		    	
		     }, function() { });
	}
	
	function PriorityAddCtrl($scope, $mdDialog,item){
		$scope.internalData = item;
		$scope.add = function(){
			$mdDialog.hide($scope.internalData);
		}
		
		$scope.closeAdd = function () {
			$mdDialog.hide();
		}
	}
		
	
})

.controller("AdminUpController", function ($scope,$http,$filter,$state,$stateParams,updateService) {	
	var self = this;
	this.internalData = $stateParams.data;	
	
	this.save = function(){
		updateService.save($stateParams.key,{data:this.internalData}).success(function(data){
			console.log("AdminUpController.save -> " + data);
		})
	}
	
	this.delete = function() {
		updateService.delete($stateParams.key,{data:this.internalData}).success(function(data){
			console.log("AdminUpController.save -> " + data);
		})
	}
	
		
})


