function openDownload($mdDialog, $scope_new) {

    $mdDialog.show({
        controller: function ($scope) {
            $scope.title = "Скачивание";

            $scope.close = function () {
                $mdDialog.hide();
            };
            $scope.download = function () {

            };
        },
        templateUrl: 'app/template/download.html',
        locals: {
        },
        scope: $scope_new,
        clickOutsideToClose: true,
    });
}