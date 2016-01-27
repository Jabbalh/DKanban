function KanbanVersionService($http) {
    this.insertVersion = function(app) 	{ return $http.post("/api/version/insert", app);}

}

angular.module("DKanbanApp").factory("versionService", function($http) {
    return new KanbanVersionService($http);
});