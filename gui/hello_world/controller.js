controller("hello_world", function ($scope, $localnode) {
    $scope.message = $localnode.message;

    $scope.save = function () {
        $localnode.message = $scope.message;
    }
})