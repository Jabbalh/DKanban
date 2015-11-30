angular.module("DKanbanApp", ['ngRoute','ngDraggable','ngAnimate','ngAria','ngMaterial'])
	.config(function($mdThemingProvider) {
		$mdThemingProvider.theme('default')
	    .primaryPalette('indigo')
	    .accentPalette('red');
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

    });