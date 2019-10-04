controller("summator", function ($scope) {

    $scope.message = "0"

    $scope.sum = function () {
        $scope.message = localnode.sum(4, 6);
    }
})