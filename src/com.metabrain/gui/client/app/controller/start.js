app.controller("start", function ($scope, $mdDialog) {

    $scope.openDialog = function (number) {
        $mdDialog.show({
            controller: function (){

            },
            templateUrl: 'app/template/start_dialog.html',
            clickOutsideToClose:false,
            fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
        })
            .then(function(answer) {
                $scope.status = 'You said the information was "' + answer + '".';
            }, function() {
                $scope.status = 'You cancelled the dialog.';
            });
    }
});