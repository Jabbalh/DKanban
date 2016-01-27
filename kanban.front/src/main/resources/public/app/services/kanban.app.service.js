function KanbanAppService($http) {
    this.insertApp = function(app) 	{ return $http.post("/api/app/insert", app);}

}

angular.module("DKanbanApp").factory("appService", function($http) {
    return new KanbanAppService($http);
});