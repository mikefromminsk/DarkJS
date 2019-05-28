function openDownload($mdDialog, $scope) {

    $mdDialog.show({
        controller: function ($scope) {
            $scope.title = "Скачивание";

            $scope.close = function () {
                $mdDialog.hide();
            };

            function downloadLink(link) {
                var anchor = angular.element('<a/>');
                anchor.css({display: 'none'}); // Make sure it's not visible
                angular.element(document.body).append(anchor); // Attach to document

                anchor.attr({
                    href: link,
                    target: '_blank',
                    download: link.substring(link.lastIndexOf('/') + 1)
                })[0].click();
                anchor.remove();
            }

            $scope.download = function () {
                downloadLink(url('DarkJs.jar'))
            };
        },
        templateUrl: 'app/template/download.html',
        locals: {
        },
        scope: $scope.$new(),
        clickOutsideToClose: true,
    });
}