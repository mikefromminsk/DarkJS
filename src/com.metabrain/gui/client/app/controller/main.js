app.controller("main", function ($scope, $mdDialog) {


    var width = 700,
        height = 500;

    var startTime;

    var currentLink = N + 0;

    var data = d3.range(20).map(function () {
        return [Math.random() * width, Math.random() * height];
    });

    var zoom = d3.behavior.zoom()
        .on("zoom", zoomed);

    var svg = d3.select("#canvas")
        .on("touchstart", nozoom)
        .on("touchmove", nozoom)
        .append("svg")
        .attr("width", width)
        .attr("height", height);

    var g = svg.append("g")
        .call(zoom);

    g.append("rect")
        .attr("width", width)
        .attr("height", height)
        .attr("fill", "white")
        .on("mousedown", function () {
            startTime = new Date();
        })
        .on("click", function clicked(d, i) {
            if (d3.event.defaultPrevented) return; // zoomed

            if (new Date() - startTime > 300) {
                let pos = d3.mouse(this);
                createLocalNode(currentLink, function (link) {
                    setStyle(link, {
                        x: pos[0],
                        y: pos[1],
                        r: 20,
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

    var view = g.append("g")
        .attr("class", "circles");


    var drag = d3.behavior.drag()
        .origin(function (d) {
            return d;
        })
        .on("dragstart", dragstarted)
        .on("drag", dragged)
        .on("dragend", dragended);

    function dragstarted(d) {
        d3.event.sourceEvent.stopPropagation();
        d3.select(this)
            .classed("dragging", true);
    }

    function dragged() {
        var pos = d3.mouse(this);
        d3.select(this)
            .attr("cx", pos[0])
            .attr("cy", pos[1]);
    }

    function dragended(localLink) {
        d3.select(this)
            .classed("dragging", false);
        //updateNodePosition(localLink, d3.mouse(this));
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
            .call(drag);
    }

    loadNode(currentLink, showNode);

    /*
        var zoom = d3.behavior.zoom()
            .on("zoom", function zoomed() {
                view.attr("transform", "translate(" + d3.event.translate + ") scale(" + d3.event.scale + ")");
            });

        var svg = d3.select("#canvas")
            .on("touchstart", nozoom)
            .on("touchmove", nozoom)
            .append("svg")
            .attr("fill", "none")
            .attr("width", width)
            .attr("height", height);

        function nozoom() {
            d3.event.preventDefault();
        }

        var g = svg.append("g")
            .call(zoom);

        g.append("rect")
            .attr("width", width)
            .attr("height", height)
            .on('mousedown', function () {
                startTime = new Date();
            })
            .on("click", function clicked(d, i) {
                if (d3.event.defaultPrevented) return; // zoomed

                if (new Date() - startTime > 200) {
                    createNewNode(d3.mouse(this));
                }
            });

        var view = g.append("g")
            .attr("class", "view");


        var distance = function (pos1, pos2) {
            return Math.sqrt(Math.pow(pos1[0] - pos2[0], 2) + Math.pow(pos1[1] - pos2[1], 2));
        };

        view.append("g")
            .attr("class", "x axis")
            .selectAll("line")
            .data(d3.range(0, width, 10))
            .enter().append("line")
            .attr("x1", function (d) {
                return d;
            })
            .attr("y1", 0)
            .attr("x2", function (d) {
                return d;
            })
            .attr("y2", height);

        view.append("g")
            .attr("class", "y axis")
            .selectAll("line")
            .data(d3.range(0, height, 10))
            .enter().append("line")
            .attr("x1", 0)
            .attr("y1", function (d) {
                return d;
            })
            .attr("x2", width)
            .attr("y2", function (d) {
                return d;
            });




        var circlesContainer = view.append("g")
            .attr("class", "circles");

        let drag = d3.behavior.drag()
            .origin(function (d) {
                return d;
            })
            .on("dragstart", dragstarted)
            .on("drag", dragged)
            .on("dragend", dragended);

        function dragstarted(d) {
            d3.event.sourceEvent.stopPropagation();
            d3.select(this)
                .classed("dragging", true);
        }

        function dragged() {
            var pos = d3.mouse(this);
            d3.select(this)
                .attr("cx", pos[0])
                .attr("cy", pos[1]);
        }

        function dragended(localLink) {
            d3.select(this)
                .classed("dragging", false);
            updateNodePosition(localLink, d3.mouse(this));
        }


        function updateNodePosition(nodeLink, pos) {
            var xLink = setStyle(nodeLink, "x", pos[0]);
            var yLink = setStyle(nodeLink, "y", pos[1]);
            var changes = {};
            changes[nodeLink] = nodes[nodeLink];
            changes[xLink] = nodes[xLink];
            changes[yLink] = nodes[yLink];
            $scope.request('node', {
                nodeLink: nodeLink,
                nodes: changes
            }, function (data) {
                merge(nodes, data.nodes);
                showNode(currentNodeLink);
            });
        }

        function addLink(nodeId, linkName, attachId) {
            var node = nodes[nodeId];
            if (node[linkName] == null || !(node[linkName] instanceof Array))
                node[linkName] = [];
            node[linkName].push(attachId)
        }

        var lastNewId = 0;

        function newNodeLink() {
            var nodeLink = W + lastNewId++;
            nodes[nodeLink] = {};
            return nodeLink;
        }

        function getStyle(nodeLink, styleTitle, defValue) {
            if (nodeLink.startsWith(N)) {
                var node = nodes[nodeLink];
                if (node.style != null) {
                    styleTitle = "!" + styleTitle;
                    for (var i = 0; i < node.style.length; i++) {
                        var styleLink = node.style[i];
                        var styleNode = nodes[styleLink];
                        if (styleNode.title === styleTitle)
                            return decodeValue(styleNode.value);
                    }
                }
            }
            return defValue;
        }

        function encodeValue(value) {
            if (typeof value === "number")
                return "" + value;
            if (typeof value === "string")
                return "!" + value;
            if (typeof value === "boolean")
                return value ? "true" : "false";
        }

        function isNumeric(num) {
            return !isNaN(num)
        }

        function decodeValue(value) {
            if (isNumeric(value))
                return parseFloat(value);
            if (value.startsWith("!"))
                return value.substr(1);
            if (value === "true")
                return true;
            if (value === "false")
                return false;
        }

        function setStyle(nodeLink, styleTitle, styleValue) {
            styleTitle = "!" + styleTitle;
            var node = nodes[nodeLink];
            if (node.style != null)
                for (var i = 0; i < node.style.length; i++) {
                    var styleLink = node.style[i];
                    var styleNode = nodes[styleLink];
                    if (styleNode.title === styleTitle) {
                        styleNode.value = encodeValue(styleValue);
                        return styleLink;
                    }
                }
            if (node.style == null)
                node.style = [];
            var styleLink = newNodeLink();
            nodes[styleLink] = {title: styleTitle};
            nodes[styleLink].value = encodeValue(styleValue);
            node.style.push(styleLink);
            return styleLink;
        }

        function createNewNode(pos) {
            var nodeLink = newNodeLink();
            var xLink = setStyle(nodeLink, "x", pos[0]);
            var yLink = setStyle(nodeLink, "y", pos[1]);
            var rLink = setStyle(nodeLink, "r", 20);
            addLink(currentNodeLink, "local", nodeLink);
            var changes = {};
            changes[nodeLink] = nodes[nodeLink];
            changes[currentNodeLink] = nodes[currentNodeLink];
            changes[xLink] = nodes[xLink];
            changes[yLink] = nodes[yLink];
            changes[rLink] = nodes[rLink];
            $scope.request('node', {
                nodeLink: currentNodeLink,
                nodes: changes
            }, function (data) {
                merge(nodes, data.nodes);
                showNode(currentNodeLink);
            });
        }

        $scope.openDialog = function (number) {
            $mdDialog.show({
                controller: function ($scope, number) {
                    $scope.number = number;
                    $scope.answer = function (result) {
                        alert(result);
                    }
                },
                templateUrl: 'app/template/start_dialog.html',
                locals: {
                    number: number
                }
            })
                .then(function (answer) {
                    $scope.status = 'You said the information was "' + answer + '".';
                }, function () {
                    $scope.status = 'You cancelled the dialog.';
                });
        }*/
});