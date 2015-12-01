angular.module("DKanbanApp", ['ngRoute','ngDraggable','ngAnimate','ngAria','ngMaterial'])
	.config(function($mdThemingProvider) {
		$mdThemingProvider.theme('default')
	    .primaryPalette('indigo')
	    .accentPalette('green')
	    .warnPalette('red');
	})
    .config(function ($routeProvider) {

        $routeProvider
            .when("/kanban", {
                templateUrl: "/app/views/kanban.html",
                controller: "KanbanController",
                controllerAs: "ctrl"
            })            
            .otherwise({
                redirectTo: "/kanban"
            });

    })
    .run(function() {

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