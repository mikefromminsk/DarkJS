app.config(function($routeProvider) {
    $routeProvider
        .when("/", {
            templateUrl : "app/template/menu.html",
            controller: "menu"
        })
        .when("/editor", {
            templateUrl : "app/template/editor_node.html",
            controller: "editor_node"
        })
        .when("/diagrams", {
            templateUrl : "app/template/diagrams.html",
            controller: "diagrams"
        })
        .when("/wiki", {
            templateUrl : "app/template/wiki.html",
            controller: "wiki"
        });
});