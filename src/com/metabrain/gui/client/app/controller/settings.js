function openSettings($mdDialog, $scope) {

    $scope.host = remoteHost;

    $scope.language = "RU";
    $scope.languages = [
        {code: "RU", name: "Русский"},
        {code: "EN", name: "English"},
    ];

    $mdDialog.show({
        controller: function ($scope) {

            $scope.close = function () {
                $mdDialog.hide();
            };
            $scope.save = function () {
                $scope.close();
            };
        },
        templateUrl: 'app/template/settings.html',
        scope: $scope.$new(),
        clickOutsideToClose: true,
    });
}