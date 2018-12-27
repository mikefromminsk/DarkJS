function openNodeChange($mdDialog, $scope, currentLink, pos, success) {

    $mdDialog.show({
        controller: function ($scope) {
            $scope.title = "";

            $scope.close = function () {
                $mdDialog.hide();
            };

            $scope.submit = function () {
                addLink(currentLink, "local", newNodeLink(), function (link) {
                    setLink(link, "title", newDataNode($scope.title), function () {
                        setStyle(link, {
                            x: pos[0],
                            y: pos[1],
                            r: 20,
                        }, function (link) {
                            $scope.close();
                            success(link);
                        })
                    })
                });
            };
        },
        templateUrl: 'app/template/node_settings.html',
        scope: $scope.$new(),
        clickOutsideToClose: true,
    });
}