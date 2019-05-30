function openLogin($mdDialog, $scope) {

    $mdDialog.show({
        controller: function ($scope) {
            $scope.title = "Скачивание";

            $scope.close = function () {
                $mdDialog.hide();
            };
            $scope.login = function () {
                $scope.go("editor")
            };

        },
        templateUrl: 'app/template/login.html',
        locals: {
        },
        scope: $scope.$new(),
        clickOutsideToClose: true,
    });
}