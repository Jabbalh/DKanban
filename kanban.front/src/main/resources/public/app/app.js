angular.module("DKanbanApp", ['ui.router','ngDraggable','ngAnimate','ngAria','ngMaterial','angular-jwt'])
	.config(function($mdThemingProvider) {
		$mdThemingProvider.theme('default')
	    .primaryPalette('indigo')
	    .accentPalette('green')
	    .warnPalette('red');
	})
	
    .config(function ($stateProvider, $urlRouterProvider,$httpProvider,jwtInterceptorProvider) {
        
        //
        // Now set up the states
        $stateProvider
        	.state('kanban', 	    { url: "/kanban", 	        templateUrl: "/app/views/kanban/kanban.html", 	controller: 'KanbanController as ctrl' 	})
        	.state('kanbanPriority',{ url: "/kanbanPriority", 	templateUrl: "/app/views/kanban/kanbanSynthese.html", 	controller: 'KanbanPriorityController as ctrl' 	})


          	.state('login', 	{ url: "/login", 	templateUrl: "/app/views/login/login.html", 	controller: 'LoginController as ctrl' 	})
          	
          	.state('admin', 	{ url: "/admin", 	templateUrl: "/app/views/admin/admin.html", 	controller: 'AdminController as ctrl' 	})
          	
          	.state('admin.application', 	{ url: "/application", 		templateUrl: "/app/views/admin/param.html", 										        controller: 'AdminAppController as ctrl' 			})
          	.state('admin.application.up', 	{ url: "/application/up", 	templateUrl: "/app/views/admin/param.up.html",params:{data:null, key:'APP'},		        controller: 'AdminUpController as ctrl' 	})
          	
          	.state('admin.statut', 			{ url: "/statut", 			templateUrl: "/app/views/admin/param.color.html", 											controller: 'AdminStatutController as ctrl' 		})
          	.state('admin.statut.up', 		{ url: "/statut/up", 		templateUrl: "/app/views/admin/param.color.up.html", 	params:{data:null, key:'STATE'},	controller: 'AdminUpController as ctrl' 		})
          	
          	.state('admin.zones', 			{ url: "/zones", 			templateUrl: "/app/views/admin/zones.html", 											    controller: 'AdminZoneController as ctrl' 			})
          	.state('admin.zones.up', 		{ url: "/zones/up", 		templateUrl: "/app/views/admin/zones.up.html", 		params:{data:null, key:'ZONE'},		    controller: 'AdminUpController as ctrl' 		})
        
          	.state('admin.utilisateur', 	{ url: "/utilisateur", 		templateUrl: "/app/views/admin/utilisateur.html", 										    controller: 'AdminUtilisateurController as ctrl' 	})
          	.state('admin.utilisateur.up', 	{ url: "/utilisateur/up", 	templateUrl: "/app/views/admin/utilisateur.up.html", params:{data:null, key:'USER'},	    controller: 'AdminUtilisateurUpController as ctrl' 	})
          	
          	.state('admin.priority', 		{ url: "/priority", 		templateUrl: "/app/views/admin/param.color.html", 										    controller: 'AdminPriorityController as ctrl' 	})
          	.state('admin.priority.up', 	{ url: "/priority/up", 		templateUrl: "/app/views/admin/param.color.up.html", 	params:{data:null, key:'PRIORITY'},	controller: 'AdminUpController as ctrl' 	})

          	.state('admin.version', 		{ url: "/version", 		    templateUrl: "/app/views/admin/version.html", 										        controller: 'AdminVersionController as ctrl' 	})
            .state('admin.version.up',   	{ url: "/version/up", 		templateUrl: "/app/views/admin/version.up.html", 	    params:{data:null, key:'VERSION'},	controller: 'AdminUpController as ctrl' 	    });
          	          	
          	
        $urlRouterProvider.otherwise("/login");
        
        jwtInterceptorProvider.tokenGetter = function() { return localStorage.getItem('id_token'); }
        $httpProvider.interceptors.push('jwtInterceptor');
          
          
        var interceptor = ['$location', '$q', '$injector', function($location, $q, $injector) {
        	  return {	        	
	            'responseError': function (rejection) {                                
	                if (rejection.status == 401) {
	                	$injector.get('$state').transitionTo('login');
	                }
	                return $q.reject(rejection );
	            }
        	  }
        }];
        
        $httpProvider.interceptors.push(interceptor);
    })
   
    
    .run(function($rootScope,jwtHelper,$state) {
    	 $rootScope.$on('$stateChangeStart', function(e, to) {    		 
		      if (to.url != '/login') {
		    	  if (localStorage.getItem('id_token') == null) {
		    		  	e.preventDefault();
    		        	$state.go('login');
		    	  } else if (jwtHelper.isTokenExpired(localStorage.getItem('id_token'))){
		    		  e.preventDefault();
    		        	$state.go('login');
		    	  }
		        
		      }    		    
		  });   	    
    })
    
    .controller('AppCtrl', function ($scope, $http,$timeout, $mdSidenav, $log,$state, updateService) {		    
		    
    	var self = this;    		
    	this.toggleLeft = function() { $mdSidenav('left').toggle(); }
		this.isAuth = false;
		this.applicationTitle = "";
		
		updateService.getApplicationTitle().success(function(data){self.applicationTitle = '{'+ data+ '}';})
		
		
	    $http.get("/public/is/auth").success(	function(data){ self.isAuth = data.auth;  	});		    
	    $scope.$on("authenticate", 				function(data){	self.isAuth = data;			});
	    
	    this.signOut = function($event) {
	    	$http.get("/api/signout").success(function(data){
	    		localStorage.removeItem("id_token");
		    	self.isAuth = false;	    	
		    	$state.go('login');
	    	});
	    	
	    }
		    	    
  });