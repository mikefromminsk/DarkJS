function openDeveloper($mdDialog, $scope_new) {

    $mdDialog.show({
        controller: function ($scope) {
            $scope.title = "Скачивание";

            $scope.close = function () {
                $mdDialog.hide();
            };
            $scope.onload = function () {
            };

        },
        templateUrl: 'app/template/developer.html',
        locals: {
        },
        scope: $scope_new,
        clickOutsideToClose: true,
    });
}