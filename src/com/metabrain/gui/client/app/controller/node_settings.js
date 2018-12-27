function openNodeChange($mdDialog, $scope, currentLink, pos, success) {

    $mdDialog.show({
        controller: function ($scope) {
            $scope.submit = function () {
                createLocalNode(currentLink, function (link) {
                    setStyle(link, {
                        x: pos[0],
                        y: pos[1],
                        r: 20,
                    }, success)
                });
            };
        },
        templateUrl: 'app/template/node_settings.html',
        locals: {
        },
        scope: $scope.$new(),
        clickOutsideToClose: true,
    });
}