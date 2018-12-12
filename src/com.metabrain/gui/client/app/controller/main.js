app.controller("main", function ($scope, $mdDialog) {


    let width = 700,
        height = 500;

    var nodeRadius = 20;

    let startTime;

    let currentLink = N + 0;

    let data = d3.range(20).map(function () {
        return [Math.random() * width, Math.random() * height];
    });

    let zoom = d3.behavior.zoom()
        .on("zoom", zoomed);

    let svg = d3.select("#canvas")
        .on("touchstart", nozoom)
        .on("touchmove", nozoom)
        .append("svg")
        .attr("width", width)
        .attr("height", height);

    let root = svg.append("g")
        .call(zoom);

    root.append("rect")
        .attr("width", width)
        .attr("height", height)
        .attr("fill", "white")
        .on("mousedown", function () {
            startTime = new Date();
        })
        .on("click", function clicked(d, i) {
            if (d3.event.defaultPrevented) return; // zoomed

            if (new Date() - startTime > 300) {
                let pos = d3.mouse(view.node());
                createLocalNode(currentLink, function (link) {
                    setStyle(link, {
                        x: pos[0],
                        y: pos[1],
                        r: nodeRadius,
                    }, function () {
                        showNode(currentLink)
                    })
                });
            }
            d3.select(this).transition()
                .style("fill", "black")
                .transition()
                .style("fill", "white");
        });


    /*
            .on("click", arcTween(outerRadius, 0))
            .on("mouseout", arcTween(outerRadius - 20, 150));*/

    function rad(val) {
        return val * (Math.PI / 180)
    }

    function toolbarAnimation() {
        var r = 10000;
        var x = width / 2;
        var y = r + 40;

        var arc = d3.svg.arc()
            .innerRadius(r)
            .outerRadius(r + 1000)
            .startAngle(rad(0))
            .endAngle(rad(360));

        var titleArc = root.append("path")
            .attr("d", arc)
            .attr("transform", "translate(" + x + "," + y + ") ");

        titleArc.on("click", function () {
            x = width / 2;
            y = height / 2;
            arc.innerRadius(0.1);
            arc.outerRadius(nodeRadius);
            titleArc.transition()
                .duration(1000)
                .attr("d", arc)
                .attr("transform", "translate(" + x + "," + y + ")");
        });
    }

    toolbarAnimation();


    let view = root.append("g")
        .attr("class", "circles");


    let drag = d3.behavior.drag()
        .origin(function (d) {
            return d;
        })
        .on("dragstart", dragstarted)
        .on("drag", dragged)
        .on("dragend", dragended);

    let centerOffset;

    function dragstarted(d) {
        d3.event.sourceEvent.stopPropagation();

        let clickPos = d3.mouse(this);
        let circle = d3.select(this);
        centerOffset = [circle.attr("cx") - clickPos[0], circle.attr("cy") - clickPos[1]];
        circle.classed("dragging", true);
    }

    function dragged() {
        let pos = d3.mouse(this);
        d3.select(this)
            .attr("cx", pos[0] + centerOffset[0])
            .attr("cy", pos[1] + centerOffset[1]);
    }

    function dragended(link) {
        d3.select(this).classed("dragging", false);
        let pos = d3.mouse(this);
        setStyle(link, {
            x: pos[0] + centerOffset[0],
            y: pos[1] + centerOffset[1],
        }, function () {
            showNode(currentLink)
        });
    }

    view.selectAll("circle")
        .data(data)
        .enter().append("circle")
        .attr("cx", function (d) {
            return d[0];
        })
        .attr("cy", function (d) {
            return d[1];
        })
        .attr("r", 32)
        .call(drag);

    function zoomed() {
        view.attr("transform", "translate(" + d3.event.translate + ") scale(" + d3.event.scale + ")");
    }

    function nozoom() {
        d3.event.preventDefault();
    }

    function showNode(link) {
        currentLink = link;
        let showNode = nodes[currentLink];
        let circles = view.selectAll("circle")
            .data(showNode.local || []);

        circles.exit().remove();
        circles.enter().append("circle");
        circles
            .attr("r", function (link) {
                return getStyleValue(link, "r", 20);
            })
            .attr("cx", function (link) {
                return getStyleValue(link, "x", 0);
            })
            .attr("cy", function (link) {
                return getStyleValue(link, "y", 0);
            })
            .on("dblclick", function (link) {
                d3.event.stopPropagation();
                openDialog(link)
            })
            .call(drag);
    }


    loadNode(currentLink, function (link) {
        showNode(link);
    });


    let openDialog = function (link) {

        $mdDialog.show({
            controller: function ($scope, link) {
                $scope.source_code =
                    "function showNode(link) {\n" +
                    "    currentLink = link;\n" +
                    "    let showNode = nodes[currentLink];\n" +
                    "    let circles = view.selectAll(\"circle\")\n" +
                    "        .data(showNode.local || []);\n" +
                    "    circles.exit().remove();\n" +
                    "    circles.enter().append(\"circle\");\n" +
                    "    circles\n" +
                    "        .attr(\"r\", function (link) {\n" +
                    "            return getStyleValue(link, \"r\", 20);\n" +
                    "        })\n" +
                    "        .attr(\"cx\", function (link) {\n" +
                    "            return getStyleValue(link, \"x\", 0);\n" +
                    "        })\n" +
                    "        .attr(\"cy\", function (link) {\n" +
                    "            return getStyleValue(link, \"y\", 0);\n" +
                    "        })\n" +
                    "        .on(\"dblclick\", function (link) {\n" +
                    "            d3.event.stopPropagation();\n" +
                    "            openDialog(link)\n" +
                    "        })\n" +
                    "        .call(drag);\n" +
                    "}\n";

                $scope.link = link;
                $scope.close = function () {
                    $mdDialog.hide();
                };
                $scope.onload = function () {
                    setTimeout(function () {
                        var codeEditor = CodeMirror.fromTextArea(document.getElementById("code"), {
                            styleActiveLine: true,
                            matchBrackets: true,
                            lineNumbers: true,
                            scrollbarStyle: "simple",
                            theme: "darcula"
                        });
                        var runEditor = CodeMirror.fromTextArea(document.getElementById("run_code"), {
                            matchBrackets: true,
                            scrollbarStyle: "simple",
                            theme: "darcula"
                        });
                        var show = setInterval(function () {
                            codeEditor.refresh();
                            runEditor.refresh();
                        }, 10);
                        setTimeout(function () {
                            clearInterval(show);
                        }, 500);
                    });
                };
            },
            templateUrl: 'app/template/main_dialog.html',
            locals: {
                link: link
            },
            clickOutsideToClose: true,
            fullscreen: true,
        }).then(function (answer) {
            $scope.status = 'You said the information was "' + answer + '".';
        }, function () {
            $scope.status = 'You cancelled the dialog.';
        });

    }
});