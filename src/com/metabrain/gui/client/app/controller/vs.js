app.controller("vs", function ($scope, $mdDialog) {

    setTimeout(function () {

        let canvasElement = document.getElementById("vsback");
        let width = canvasElement.offsetWidth;
        let height = canvasElement.offsetHeight;

        let centerPos = [width / 2, height / 2];

        let root = d3.select("#vsback")
            .append("svg")
            .attr("fill", "white")
            .attr("width", width)
            .attr("height", height)
            .append("g")
            .attr('transform', tr(centerPos));



    })

});