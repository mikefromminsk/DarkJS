

function openNodeChange($mdDialog, $scope, currentLink, pos, success) {

    $mdDialog.show({
        controller: function ($scope) {
            $scope.title = "";

            $scope.close = function () {
                $mdDialog.hide();
            };

            $scope.submit = function () {
                var codeTitle = replace($scope.title, " ", "_");
                addLink(currentLink, "local", newNodeLink(), function (link) {
                    setLink(link, "title", newDataNode(codeTitle), function () {
                        setStyle(link, {
                            x: pos[0],
                            y: pos[1],
                            r: 20,
                        }, function (link) {
                            $scope.close();
                            success(link);
                        })
                    });
                    setStyle(currentLink, {
                        source_code: "var " + codeTitle+ ";\n" + getStyleValue(currentLink, "source_code", "")
                    })
                });
            };
        },
        templateUrl: 'app/template/node_settings.html',
        scope: $scope.$new(),
        clickOutsideToClose: true,
    });
}