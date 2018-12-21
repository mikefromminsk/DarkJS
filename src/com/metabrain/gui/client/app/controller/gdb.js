app.controller("gdb", function ($scope) {
    $scope.dbLog = [];

    $scope.start = function () {
        request("testStart", null, function () {
            var timer = setInterval(function () {
                request("testProgress", null, function (data) {
                    if (data.response == null)
                        clearTimeout(timer);
                    else
                        merge($scope.dbLog, data.response)
                });
            }, 1000)
        })
    }
});