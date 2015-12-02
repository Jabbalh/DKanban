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
        	.state('kanban', 	{ url: "/kanban", 	templateUrl: "/app/views/kanban.html", 	controller: 'KanbanController as ctrl' 	})
          	.state('login', 	{ url: "/login", 	templateUrl: "/app/views/login.html", 	controller: 'LoginController as ctrl' 	});
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
    
    .controller('AppCtrl', function ($scope, $http,$timeout, $mdSidenav, $log) {		    
		    
    	var self = this;    		
    	this.toggleLeft = function() { $mdSidenav('left').toggle(); }
		this.isAuth = false;
								
	    $http.get("/public/is/auth").success(	function(data){ self.isAuth = data.auth;  	});		    
	    $scope.$on("authenticate", 				function(data){	self.isAuth = data;			});
		    	    
  });