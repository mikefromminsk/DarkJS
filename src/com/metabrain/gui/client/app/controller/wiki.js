app.controller("wiki", function ($scope, $mdDialog) {

    setTimeout(function () {

        let canvasElement = document.getElementById("wiki_background");
        let width = canvasElement.offsetWidth;
        let height = canvasElement.offsetHeight;

        let centerPos = [width / 2, height / 2];

        let hypScreen = hyp(width, height);
        let h = width * height / hypScreen;
        let offsetX = - h / Math.pow(2, 0.5);
        let offsetY = height / 2;

        let root = d3.select("#wiki_background")
            .append("svg")
            .attr("fill", "white")
            .attr("width", width)
            .attr("height", height)
            .append("g")
            .attr('transform', tr([offsetX, offsetY], null, 45));


        var data = [];


        var lineCount = getRandomInt(50, 80);
        for (var i = 0; i < lineCount; i++) {
            var lineWidth = getRandomInt(20, 60);
            var lineHeight = getRandomInt(200, 800);
            var line = {
                x: getRandomInt(0, hypScreen - lineWidth),
                y: getRandomInt(0, (h*2) + height),
                width: lineWidth,
                height: lineHeight,
                color: "hsl(" + Math.random() * 360 + ",100%,50%)",
                speed: getRandomInt(30000, 50000),
            };
            data.push(line);
        }

        let back = root.selectAll("rect")
            .data(data)
            .enter()
            .append("rect")
            .attr("x", function (d) {
                return d.x;
            })
            .attr("y", function (d) {
                return d.y;
            })
            .attr("width", function (d) {
                return d.width;
            })
            .attr("height", function (d) {
                return d.height;
            })
            .attr("fill", function (d) {
                return d.color
            });

        repeat(back);

        function repeat(node) {
            var select = Array.isArray(node) ? node : d3.select(this);
            select.attr("transform", tr([0, 0]))
                .transition()
                .ease(d3.easeLinear)
                .duration(function (d) {
                    return d.speed
                }).attr("transform", function (d) {
                return tr([0, - (hypScreen + d.height + d.y)]);
            })
                .each("end", repeat);
        };
    })

});