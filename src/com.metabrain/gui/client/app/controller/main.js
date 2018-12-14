app.controller("main", function ($scope, $mdDialog) {


    let width = 700,
        height = 500;

    var nodeRadius = 20;

    let startTime;

   var  currentLink = N + 0;

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
            hideMenu();
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
                        $scope.showNode(currentLink)
                    })
                });
            }
            var ss = $scope.close;
            d3.select(this).transition()
                .style("fill", "black")
                .transition()
                .style("fill", "white");
        });


    /*
            .on("click", arcTween(outerRadius, 0))
            .on("mouseout", arcTween(outerRadius - 20, 150));*/


    let view = root.append("g")
        .attr("class", "circles");


    var resizeBtn;

    function initResizeBtn() {
        resizeBtn = view.append("circle")
            .attr("class", "resize")
            .style("opacity", 0)
            .attr("r", nodeRadius)
    }

    initResizeBtn();

    function showMenu(ths) {
        var translate = tr(posSum(getTranslate(ths), [0, -nodeRadius * 2 - 10]));
        resizeBtn.attr("transform", translate)
            .transition()
            .duration(300)
            .style("opacity", 1);
    }

    function hideMenu() {
        if (resizeBtn.attr("opacity") !== 0)
            resizeBtn.transition()
                .duration(300)
                .style("opacity", 0)
    }

    function zoomed() {
        view.attr("transform", tr(d3.event.translate, d3.event.scale));
    }

    function nozoom() {
        d3.event.preventDefault();
    }


    let drag = d3.behavior.drag()
        .origin(function (d) {
            return d;
        })
        .on("dragstart", dragstarted)
        .on("drag", dragged)
        .on("dragend", dragended);

    let centerOffset;
    var startDragPos;

    function dragstarted(d) {
        hideMenu();
        startTime = new Date();
        d3.event.sourceEvent.stopPropagation();
        startDragPos = getTranslate(this);
        centerOffset = d3.mouse(this);
        d3.select(this).classed("dragging", true);
    }

    function tr(x, y, s) {
        if (typeof x === "object") {
            s = y;
            y = x[1];
            x = x[0];
        }
        return "translate(" + Math.floor(x) + "," + Math.floor(y) + ")" + (s === undefined ? "" : "scale(" + s + ")");
    }

    function posSum(a, b) {
        return [a[0] + b[0], a[1] + b[1]];
    }

    function posSub(a, b) {
        return [a[0] - b[0], a[1] - b[1]];
    }

    function posDst(a, b) {
        return Math.pow(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2), 0.5);
    }

    function getTranslate(ths) {
        return d3.transform(d3.select(ths).attr("transform")).translate
    }

    function dragged() {
        var translate = tr(
            posSum(
                posSub(getTranslate(this), centerOffset),
                d3.mouse(this)));
        d3.select(this).attr("transform", translate);
    }

    function dragended(link) {
        var translate = getTranslate(this);
        let node = d3.select(this);
        node.classed("dragging", false);
        if (new Date() - startTime > 300 //long click{
            && posDst(startDragPos, translate) < 20) {
            showMenu(this);
        }
        setStyle(link, {
            x: translate[0],
            y: translate[1],
        }, function () {
            $scope.showNode(currentLink)
        });
    }

    $scope.showNode = function (link) {
        currentLink = link;
        let showNode = nodes[currentLink];
        view.selectAll(".node").remove();
        let nodeList = view.selectAll(".node")
            .data(showNode.local || []).enter()
            .append("g")
            .attr("class", "node")
            .attr("transform", function (link) {
                var x = getStyleValue(link, "x", 0);
                var y = getStyleValue(link, "y", 0);
                return tr(x, y);
            })
            .on("dblclick", function (link) {
                d3.event.stopPropagation();
                $scope.openDialog(link)
            })
            .call(drag);

        nodeList.append("circle")
            .attr("r", function (link) {
                return getStyleValue(link, "r", 20);
            });
        nodeList.append("text")
            .attr("dx", nodeRadius + 10)
            .text(showNode.title);
    }


    loadNode(currentLink, $scope.showNode);


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
            .attr("transform", tr(x, y))
            .on("click", function () {
                x = width / 2;
                y = height / 2;
                arc.innerRadius(0.01);
                arc.outerRadius(nodeRadius);
                titleArc.transition()
                    .duration(1000)
                    .attr("d", arc)
                    .attr("transform", tr(x, y));
            });
    }

    toolbarAnimation();

    $scope.openDialog = function (link) {

        $mdDialog.show({
            controller: function ($scope, moduleLink, link) {
                $scope.source_code = getStyleValue(moduleLink, "source_code", "");
                $scope.result_node_show = true;
                $scope.result_nodes = "";
                $scope.link = link;
                $scope.title = "Title";

                var codeEditor;
                var runEditor;
                var resultEditor;

                $scope.close = function () {
                    loadNode(moduleLink, $scope.showNode);
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
                    parseJs(moduleLink, codeEditor.getValue(),
                        function () {
                            $scope.close();
                        }, function (link, data) {
                            $scope.result_node_show = true;
                            resultEditor.setValue(JSON.stringify(data, null, '\t'));
                            $scope.$apply();
                        });
                };

                $scope.run = function () {
                };

            },
            templateUrl: 'app/template/main_dialog.html',
            locals: {
                moduleLink: currentLink,
                link: link
            },
            scope: $scope.$new(),
            clickOutsideToClose: true,
            fullscreen: true,
        }).then(function (answer) {
            $scope.status = 'You said the information was "' + answer + '".';
        }, function () {
            $scope.status = 'You cancelled the dialog.';
        });

    }
});