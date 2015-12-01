angular.module("DKanbanApp", ['ngRoute','ngDraggable','ngAnimate','ngAria','ngMaterial','angular-jwt'])
	.config(function($mdThemingProvider) {
		$mdThemingProvider.theme('default')
	    .primaryPalette('indigo')
	    .accentPalette('green')
	    .warnPalette('red');
	})
	
    .config(function ($routeProvider,$httpProvider,jwtInterceptorProvider,$locationProvider) {

        $routeProvider
            .when("/kanban", 	{ templateUrl: "/app/views/kanban.html", controller: "KanbanController", controllerAs: "ctrl" })
            .when("/login", 	{ templateUrl: "/app/views/login.html", controller: "LoginController", controllerAs: "ctrl" }) 
            .otherwise(			{ redirectTo: "/login" });
        /*
        $locationProvider.html5Mode({
        	  enabled: true,
        	  requireBase: false
        	});
        */
        jwtInterceptorProvider.tokenGetter = function() {
        	return localStorage.getItem('id_token');
          }

          $httpProvider.interceptors.push('jwtInterceptor');
          /*
          var interceptor = function($q,$location){
        	    return {
        	        response: function(response){
        	        	console.log("Response ->" + response);
        	            if (response.status === 401) {
        	                console.log("Response 401");
        	            }
        	            return response || $q.when(response);
        	        },
        	        responseError: function(rejection) {
        	        	
        	        	console.log("rejection ->" + rejection);
        	            if (rejection.status === 401) {
        	                console.log("Response Error 401",rejection);
        	                $location.path('/');
        	            }
        	            return $q.reject(rejection);
        	        }
        	    }}
         
          $httpProvider.interceptors.push(interceptor);
           */
          

    })
    
    .run(function($rootScope,jwtHelper,$location) {
    	/* $rootScope.$on('$routechangestart', function(e, to) {
    		    if (to.data && to.data.requiresLogin) {
    		      if (!localStorage.get('id_token') || jwtHelper.isTokenExpired(localStorage.get('jwt'))) {
    		        e.preventDefault();
    		        $location.path('/login');
    		      }
    		    }
    		  });*/
    })
    
    .controller('AppCtrl', function ($scope, $timeout, $mdSidenav, $log) {		    
		    this.toggleLeft = buildToggler('left');
			    
		    function buildToggler(navID) {
		      return function() {
		        $mdSidenav(navID)
		          .toggle()
		          .then(function () {
		            $log.debug("toggle " + navID + " is done");
		          });
		      }
		    }
  });