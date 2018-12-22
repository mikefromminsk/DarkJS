app.controller("gdb", function ($scope) {
    $scope.dbLog = [];
    var timer;
    $scope.start = function () {
        if (timer == null) {
            timer = setInterval(function () {
                request("test", null, function (data) {
                    if (data.response == null) {
                        clearTimeout(timer);
                        timer = null;
                    }
                    else {
                        $scope.dbLog.splice(0, $scope.dbLog.length);
                        merge($scope.dbLog, data.response);
                        $scope.$apply();
                    }

                });
            }, 1000)
        }
    }
});