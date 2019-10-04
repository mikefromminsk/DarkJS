controller('launcher', function ($scope) {
    $scope.apps = []

    if (DEBUG){
        $scope.apps = [
            "store",
            "chat"
        ]
    }else{
        for (var key in master)
            if (key[0] === '$' && key !== "$launcher")
                $scope.apps.push(key.substr(1));
    }
})