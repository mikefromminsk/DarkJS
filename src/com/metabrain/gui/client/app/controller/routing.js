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
        .when("/plans", {
            templateUrl : "app/template/plans.html",
            controller: "plans"
        })
        .when("/wiki", {
            templateUrl : "app/template/wiki.html",
            controller: "wiki"
        })
        .when("/gdb", {
            templateUrl : "app/template/gdb.html",
            controller: "gdb"
        })
        .when("/vs", {
            templateUrl : "app/template/vs.html",
            controller: "vs"
        });
});