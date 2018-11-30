let app = angular.module("myApp", ["ngRoute", "ngMaterial", "ngMessages"]);

let remoteHost = '//localhost:9080/';

let isMobile = navigator.userAgent.match(/(iPad)|(iPhone)|(iPod)|(android)|(webOS)/i);

function merge(source, object, append) {
    if (append || false !== true) {
        if (source instanceof Array)
            source.splice(0, source.length);
        if (source instanceof Object)
            Object.keys(source).forEach(function (key) {
                delete source[key];
            });
    }
    if (source != null)
        for (let key in object)
            if (object.hasOwnProperty(key)) {
                if (source instanceof Array)
                    source.push(object[key]);
                else
                    source[key] = object[key];
            }
}

app.controller("app", function ($scope, $location, $window, $http) {

    $scope.go = function (path) {
        $location.url(path);
    };

    $scope.back = function () {
        $window.history.back();
    };

    $scope.reload = function () {
        location.reload();
    };


    $scope.http = function (method, endpoint, params, success, error, async) {
        if (error == null)
            error = function (status, response) {
                console.log(status);
                console.log(response);
            };

        let xhr = XMLHttpRequest ? new XMLHttpRequest() :
            new ActiveXObject("Microsoft.XMLHTTP");

        xhr.onload = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    let object = null;
                    try {
                        object = JSON.parse(xhr.response);
                    } catch (e) {
                        error("Json parse error", xhr.response);
                    }
                    if (object != null)
                        success(object)
                } else {
                    error(xhr.status);
                }
            }
        };
        xhr.onerror = function () {
            error(xhr.status);
        };

        xhr.open(method, endpoint, async || true);
        xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
        xhr.send((params == null) ? null : JSON.stringify(params));
    };

    $scope.request = function (endpoint, params, success, error) {
        $scope.http("POST", remoteHost + endpoint, params, success, error, true);
    };

});