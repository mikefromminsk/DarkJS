function openSettings($mdDialog, $scope) {

    $mdDialog.show({
        controller: function ($scope) {

            $scope.close = function () {
                $mdDialog.hide();
            };


        },
        templateUrl: 'app/template/settings.html',
        scope: $scope.$new(),
        clickOutsideToClose: true,
    });
}