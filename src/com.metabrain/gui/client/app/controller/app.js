let app = angular.module("myApp", ["ngRoute", "ngMaterial", "ngMessages"]);

let remoteHost = '//localhost:9080/';

let isMobile = navigator.userAgent.match(/(iPad)|(iPhone)|(iPod)|(android)|(webOS)/i);

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

});