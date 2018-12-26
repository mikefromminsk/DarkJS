function openCodeEditor($mdDialog, $scope_new, currentLink, link, close) {

    $mdDialog.show({
        controller: function ($scope, moduleLink, link) {
            $scope.source_code = getStyleValue(moduleLink, "source_code", "");
            $scope.result_node_show = true;
            $scope.result_nodes = "";
            $scope.link = link;
            $scope.title = "Редактирование фунции";

            var codeEditor;
            var runEditor;
            var resultEditor;

            $scope.close = function () {
                close();
                $mdDialog.hide();
            };
            $scope.onload = function () {
                setTimeout(function () {
                    codeEditor = CodeMirror.fromTextArea(document.getElementById("code"), {
                        styleActiveLine: true,
                        matchBrackets: true,
                        lineNumbers: true,
                        scrollbarStyle: "simple",
                        theme: "darcula"
                    });
                    runEditor = CodeMirror.fromTextArea(document.getElementById("run_code"), {
                        matchBrackets: true,
                        scrollbarStyle: "simple",
                        theme: "darcula"
                    });
                    resultEditor = CodeMirror.fromTextArea(document.getElementById("result"), {
                        matchBrackets: true,
                        scrollbarStyle: "simple",
                        lineNumbers: true,
                        theme: "darcula"
                    });

                    var show = setInterval(function () {
                        codeEditor.refresh();
                        runEditor.refresh();
                        resultEditor.refresh();
                    }, 10);
                    setTimeout(function () {
                        clearInterval(show);
                    }, 500);
                });
            };

            $scope.save = function () {
                parseAndRunNode(moduleLink, codeEditor.getValue(),
                    function () {
                        $scope.close();
                    }, function (link, data) {
                        $scope.result_node_show = true;
                        resultEditor.setValue(JSON.stringify(data, null, '\t'));
                        $scope.$apply();
                    });
            };

        },
        templateUrl: 'app/template/editor_code.html',
        locals: {
            moduleLink: currentLink,
            link: link
        },
        scope: $scope_new,
        clickOutsideToClose: true,
        fullscreen: true,
    });
}