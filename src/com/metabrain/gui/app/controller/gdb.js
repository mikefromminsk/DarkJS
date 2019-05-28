function openDBTest($mdDialog, $scope) {

    $mdDialog.show({
        controller: function ($scope) {
            $scope.title = "Скачивание";

            $scope.close = function () {
                $mdDialog.hide();
            };
            $scope.onload = function () {
            };

        },
        templateUrl: 'app/template/gdb.html',
        locals: {
        },
        scope: $scope.$new(),
        clickOutsideToClose: true,
    });
}