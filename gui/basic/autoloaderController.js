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


let pathToRootDir = window.location.pathname
if (pathToRootDir.endsWith("index.html"))
    pathToRootDir = pathToRootDir.substr(0, pathToRootDir.length - "index.html".length)
/*if (!pathToRootDir.endsWith("/"))
    pathToRootDir = "/"*/

function page(appName, params) {
    params = params || {};
    extend(params, {
        templateUrl: pathToRootDir + appName + "/index.html",
        controller: appName,
        resolve: loader([pathToRootDir + appName + "/controller.js"])
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

angularApplication.controller('autoloaderController', function ($rootScope, $scope, $mdSidenav, $mdDialog, $location) {

    $scope.open = function (appName) {
        angularApplication.routeProvider.when("/" + appName, page(appName))
        // set global path for all urls
        document.getElementsByTagName("base")[0].href = (pathToRootDir + appName + "/")
        document.title = appName
        $location.path(appName)
    };
    $scope.open($location.path().substr(1) || "launcher");
});
