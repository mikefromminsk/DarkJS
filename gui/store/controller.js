controller('store', function ($scope) {

    $scope.apps = {}

    if (DEBUG) {
        $scope.apps = {
            "chat": {
                exist: true,
                description: "This is simple chat, that test transport system.",
                logo: ""
            },
            "summator": {
                exist: false,
                description: "This application just sum two numbers. It was created for testing store app.",
                logo: ""
            },
        }

    } else {
        var storeMaster = getResultNode(localnode.getAppList())

        for (var key in storeMaster.local) {
            var app = storeMaster.local[key]
            var appName = app.title;
            if (appName != "index")
                $scope.apps[appName] = {
                    exist: master["$" + appName] != null,
                    description: app.description,
                    logo: app.logo
                };
        }
    }


    $scope.download = function (appName) {
        localnode.downloadApp(appName)
        $scope.apps[appName].exist = true;
        clearMaster()
    }
});