var angularApplication = angular.module('AngularApp',
    [
        'ngRoute',
        'ngMaterial',
        'ngAnimate',
        'ngMessages',
    ]);

function loadScript(path) {
    var result = $.Deferred(), // TODO remove using jquery
        script = document.createElement("script");
    script.async = "async";
    script.type = "text/javascript";
    script.src = path;
    script.onload = script.onreadystatechange = function (_, isAbort) {
        if (!script.readyState || /loaded|complete/.test(script.readyState)) {
            if (isAbort)
                result.reject();
            else
                result.resolve();
        }
    };
    script.onerror = function () {
        result.reject();
    };
    document.querySelector("head").appendChild(script);
    return result.promise();
}

function loader(arrayName) {

    var scripts = document.getElementsByTagName("script");
    for (var i = 0; i < scripts.length; ++i)
        if (scripts[i].getAttribute('src') == arrayName)
            return null;
    return {
        load: function ($q) {
            var deferred = $q.defer(),
                map = arrayName.map(function (name) {
                    return loadScript(name);
                });

            $q.all(map).then(function (r) {
                deferred.resolve();
            });

            return deferred.promise;
        }
    };
}

function extend(dest, src) {
    if (dest == null)
        dest = {};
    for (var key in src)
        if (src.hasOwnProperty(key))
            if (dest[key] !== null && typeof dest[key] === 'object')
                extend(dest[key], src[key]);
            else
                dest[key] = src[key];
}

function dir() {
    let path = window.location.pathname
    let indexName = "/index.html"
    if (path.endsWith(indexName))
        path = path.substr(0, path.length - indexName.length)
    if (path == "")
        path = "/"
    return path
}


function page(page_name, params) {
    params = params || {};
    extend(params, {
        templateUrl: dir() + page_name + "/index.html",
        controller: page_name,
        resolve: loader([dir() + page_name + "/controller.js"])
    });
    return params;
}

angularApplication.config(function ($routeProvider, $controllerProvider) {
    angularApplication.register = $controllerProvider.register;
    angularApplication.routeProvider = $routeProvider;
});

function controller(controllerId, callback) {
    // Object.keys(observeTree).forEach(function (key) { delete observeTree[key]; }); // remove all observers
    angularApplication.register(controllerId, callback);
}

// !!! important !!!
function setbasehref(basehref) {
    document.getElementsByTagName("base")[0].href = basehref;
}

angularApplication.controller('mainController', function ($rootScope, $scope, $mdSidenav, $mdDialog, $location) {

    $scope.open = function (app) {
        if (app[0] != "/")
            app = "/" + app;
        angularApplication.routeProvider.when(app, page(app.substr(1)))
        setbasehref(dir() + app + "/")
        document.title = app.substr(1)
        // TODO timeout for ripple animation
        $location.path(app)
    };
    $scope.open($location.path() || "launcher");
});
