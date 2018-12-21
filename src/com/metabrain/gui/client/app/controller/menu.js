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
            .attr("height", height)
            .append("g")
            .attr('transform', tr(centerPos));

        initBackground();
        initDonutMenu();
        initCenter();


        function initBackground() {
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
                    return tr(null, null, (data.reverse || false ? -1 : 1) * Math.floor(elapsed / data.speed));
                });
                if (elapsed > 15000) t.stop()
            });
        }


        function initDonutMenu() {

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

            $scope.download_link = url("darkjs.jar");
            var newMenu = root.append('g');

            var donutData = [
                {
                    name: "Wiki", value: 10,
                    click: function () {
                        $scope.go("wiki")
                    }
                },
                {
                    name: "Plans", value: 10,
                    click: function () {
                        $scope.go("diagrams")
                    }
                },
                {
                    name: "GrapfDB", value: 10,
                    click: function () {
                        $scope.go("gdb")
                    }
                },
                {
                    name: "Download", value: 10,
                    click: function () {
                        downloadLink(url("darkjs.jar"))
                    }
                },
                {
                    name: "DarkJS vs JS", value: 10,
                    click: function () {
                        $scope.go("wiki")
                    }
                }
            ];

            //Create a color scale
            var colorScale = d3.scale.linear()
                .domain([1, 3.5, 6])
                .range(["#2c7bb6", "#ffffbf", "#d7191c"])
                .interpolate(d3.interpolateHcl);

            //Create an arc function
            var arc = d3.svg.arc()
                .innerRadius(0)
                .outerRadius(150);

            var arcOver = d3.svg.arc()
                .innerRadius(0)
                .outerRadius(200);

            //Turn the pie chart 90 degrees counter clockwise, so it starts at the left
            var pie = d3.layout.pie()
                .startAngle(rad(-90))
                .endAngle(rad(270))
                .value(function (d) {
                    return d.value;
                })
                .sort(null);

            //Create the donut slices and also the invisible arcs for the text
            newMenu.selectAll(".donutArcs")
                .data(pie(donutData))
                .enter().append("path")
                .attr("class", "donutArcs")
                .attr("d", arc)
                .style("fill", function (d, i) {
                    if (i === 7) return "#CCCCCC";
                    else return colorScale(i);
                })
                .each(function (d, i) {
                    //Search pattern for everything between the start and the first capital L
                    var firstArcSection = /(^.+?)L/;

                    //Grab everything up to the first Line statement
                    var newArc = firstArcSection.exec(d3.select(this).attr("d"))[1];
                    //Replace all the comma's so that IE can handle it
                    newArc = newArc.replace(/,/g, " ");

                    //If the end angle lies beyond a quarter of a circle (90 degrees or pi/2)
                    //flip the end and start position
                    if (d.endAngle > 90 * Math.PI / 180) {
                        var startLoc = /M(.*?)A/,		//Everything between the first capital M and first capital A
                            middleLoc = /A(.*?)0 0 1/,	//Everything between the first capital A and 0 0 1
                            endLoc = /0 0 1 (.*?)$/;	//Everything between the first 0 0 1 and the end of the string (denoted by $)
                        //Flip the direction of the arc by switching the start en end point (and sweep flag)
                        //of those elements that are below the horizontal line
                        var newStart = endLoc.exec(newArc)[1];
                        var newEnd = startLoc.exec(newArc)[1];
                        var middleSec = middleLoc.exec(newArc)[1];

                        //Build up the new arc notation, set the sweep-flag to 0
                        newArc = "M" + newStart + "A" + middleSec + "0 0 0 " + newEnd;
                    }//if

                    //Create a new invisible arc that the text can flow along
                    newMenu.append("path")
                        .attr("class", "hiddenDonutArcs")
                        .attr("id", "donutArc" + i)
                        .attr("d", newArc)
                        .style("fill", "none");
                })
                .on("mouseover", function (d) {
                    d3.select(this).transition()
                        .duration(300)
                        .attr("d", arcOver);
                })
                .on("mouseout", function (d) {
                    d3.select(this).transition()
                        .duration(300)
                        .attr("d", arc);
                })
                .on("click", function (d) {
                    d.data.click();
                });

            //Append the label names on the outside
            newMenu.selectAll(".donutText")
                .data(pie(donutData))
                .enter().append("text")
                .attr("fill", "black")
                .attr("class", "donutText")
                //Move the labels below the arcs for those slices with an end angle greater than 90 degrees
                .attr("dy", function (d, i) {
                    return (d.endAngle > 90 * Math.PI / 180 ? 18 : -11);
                })
                .append("textPath")
                .attr("startOffset", "50%")
                .style("text-anchor", "middle")
                .attr("xlink:href", function (d, i) {
                    return "#donutArc" + i;
                })
                .text(function (d) {
                    return d.data.name;
                });
        }

        function initCenter() {

            let scale = 1.5;
            let centerNodePoints = [[0, -50], [-45, -25], [-45, 25], [0, 50], [45, 25], [45, -25], [0, -50]];
            let lineGenerator = d3.svg.line();
            let pathString = lineGenerator(centerNodePoints);
            var center = root.append("g")
                .on("mouseover", function (d) {
                    d3.select(this).transition()
                        .duration(300)
                        .attr("transform", tr(null, 1.5));
                })
                .on("mouseout", function (d) {
                    d3.select(this).transition()
                        .duration(300)
                        .attr("transform", tr(null, 1));
                })
                .on("click", function () {
                    $scope.go("editor")
                });
            center.append('path')
                .attr('d', pathString)
                .attr("fill", "#000000")
                .attr("transform", tr(null, scale));
            center.append("text")
                .attr("fill", "white")
                .attr("dy", 10)
                .style("text-anchor", "middle")
                .style("font-size", 40)
                .text("DarkJS");
        }


    })
});