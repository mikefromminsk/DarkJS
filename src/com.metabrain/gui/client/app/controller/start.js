app.controller("start", function ($scope, $mdDialog) {

    $scope.openDialog = function (number) {
        $mdDialog.show({
            controller: function ($scope, number) {
                $scope.number = number;
                $scope.answer = function (result) {
                    alert(result);
                }
            },
            templateUrl: 'app/template/start_dialog.html',
            locals: {
                number: number
            }
        })
            .then(function (answer) {
                $scope.status = 'You said the information was "' + answer + '".';
            }, function () {
                $scope.status = 'You cancelled the dialog.';
            });
    }
});