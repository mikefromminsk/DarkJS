controller('chat', function ($scope, $mdSidenav) {
    $scope.messages = [];
    $scope.nodename = "store.node";
    $scope.message = "test message";
    $scope.toggle = false;

    function addMessage(message){
        $scope.messages.push(new Date().getTime() + ":" + message)
    }

    /*observe("new message", function (message) {
        addMessage(message.value)
        $scope.$apply();
    })*/

    $scope.send = function (nodename, message) {
        addMessage(message)
        localnode.send(nodename, message)
    }

    $scope.toggleLeft = function () {
        $scope.toggle = !$scope.toggle;
    }
});