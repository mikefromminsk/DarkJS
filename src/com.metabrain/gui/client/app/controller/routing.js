app.config(function($routeProvider) {
    $routeProvider
        .when("/", {
            templateUrl : "app/template/main.html",
            controller: "main"
        })/*
        .when("/enter_space", {
            templateUrl : "app/template/enter_space.html",
            controller: "enter_space"
        })*/;
});