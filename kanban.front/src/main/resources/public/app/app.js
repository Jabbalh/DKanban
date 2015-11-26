angular.module("DKanbanApp", ['ngRoute','ngDraggable','ngAnimate','ngMaterial'])
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