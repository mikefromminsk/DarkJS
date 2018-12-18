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
            inner: 180,
            outer: 200,
            speed: 15,
            start: 90,
            angle: 90,
            color: "#0000ff",
            reverse: true
        }, {
            inner: 180,
            outer: 200,
            speed: 18,
            start: 0,
            angle: 90,
            color: "#ff0000",
            reverse: true
        }, {
            inner: 180,
            outer: 200,
            speed: 32,
            start: 90,
            angle: 90,
            color: "#00ff00",
            reverse: false
        }];

        var data = [{
            "color": "#00ff00",
            "label": "one",
            "value": 30
        }, {
            "color": "#ff0000",
            "label": "two",
            "value": 30
        }, {
            "color": "#0000ff",
            "label": "three",
            "value": 30
        }, {
            "color": "#00ffff",
            "label": "three",
            "value": 30
        }];

        var arc = d3.svg.arc()
            .innerRadius(50)
            .outerRadius(120);
        var arcOver = d3.svg.arc()
            .innerRadius(50)
            .outerRadius(120 + 40);
        var pie = d3.layout.pie()
            .value(function(d) {
                return d.value;
            });
        var renderarcs = root.append('g')
            .attr('transform', tr(centerPos))
            .selectAll('.arc')
            .data(pie(data))
            .enter()
            .append('g')
            .attr('class', "arc");

        renderarcs.append('path')
            .attr('d', arc)
            .attr('fill', function(d, i) {
                return d.data.color;
            })
            .on("mouseover", function(d) {
                d3.select(this).transition()
                    .duration(300)
                    .attr("d", arcOver);
            })
            .on("mouseout", function(d) {
                d3.select(this).transition()
                    .duration(300)
                    .attr("d", arc);
            });



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

        let logoRect = [130, 130];
        root.append("image")
            .attr("xlink:href", url("img/js.svg"))
            .attr("width", logoRect[0])
            .attr("height", logoRect[1])
            .attr("transform", tr(posSub(centerPos, posDiv(logoRect, 2))));
    })
});