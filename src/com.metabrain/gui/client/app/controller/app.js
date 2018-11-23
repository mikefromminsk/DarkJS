let app = angular.module("myApp", ["ngRoute"]);

let remoteHost = '/';

let isMobile = navigator.userAgent.match(/(iPad)|(iPhone)|(iPod)|(android)|(webOS)/i);

function merge(source, object) {
    if (source instanceof Array)
        source.splice(0, source.length);
    if (source instanceof Object)
        Object.keys(source).forEach(function (key) {
            delete source[key];
        });
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
            error = function (response) {
            };

        let xhr = new XMLHttpRequest();

        xhr.onload = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    success(JSON.parse(xhr.response))
                } else {
                    error();
                }
            }
        };

        xhr.open(method, endpoint, async);
        xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
        xhr.send((params == null) ? null : JSON.stringify(params));
    };

    $scope.request = function (endpoint, params, success, error) {
        $scope.http("POST", remoteHost + endpoint, params, success, error);
    };

});