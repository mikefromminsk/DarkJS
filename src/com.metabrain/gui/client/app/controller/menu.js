app.controller("menu", function ($scope, $mdDialog) {

    setTimeout(function () {
        let canvasElement = document.getElementById("menu");
        let width = canvasElement.offsetWidth;
        let height = canvasElement.offsetHeight;

        let centerPos = [width / 2, height / 2];

        let root = d3.select("#menu")
            .append("svg")
            .attr("fill", "white")
            .attr("width", width)
            .attr("height", height);

        var backCirclesData = [{
            inner: 13,
            outer: 20,
            speed: 2,
            start: 90,
            angle: 90,
            color: "#0000ff",
            reverse: true
        }, {
            inner: 13,
            outer: 20,
            speed: 5,
            start: 0,
            angle: 90,
            color: "#ff0000",
        }, {
            inner: 13,
            outer: 20,
            speed: 12,
            start: 90,
            angle: 90,
            color: "#00ff00",
        }];

        let back = root.selectAll(".back")
            .data(backCirclesData);

        back.enter()
            .append("path")
            .attr("class", "back")
            .attr("d", d3.svg.arc()
                .innerRadius(function (data) {
                    return data.inner;
                })
                .outerRadius(function (data) {
                    return data.outer;
                })
                .startAngle(function (data) {
                    return rad(data.start)
                })
                .endAngle(function (data) {
                    return rad(data.start + data.angle)
                }))
            .attr("fill", function (data) {
                return data.color
            });
        let t = d3.timer(function (elapsed) {
            back.attr("transform", function (data) {
                return "translate(" + centerPos + ") rotate(" +
                    (data.reverse || false ? -1 : 1) * Math.floor(elapsed / data.speed) % 360 + ")";
            });
            if (elapsed > 15000) t.stop()
        });
    })
});