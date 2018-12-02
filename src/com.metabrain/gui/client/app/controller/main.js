app.controller("main", function ($scope, $mdDialog) {

    let N = "n";
    let W = "w";

    var width = 700,
        height = 500;

    var startTime;
    var endTime;

    let nodes = {};
    var currentNodeLink;

    var svg = d3.select("#canvas").append("svg")
        .attr("width", width)
        .attr("height", height)
        .call(d3.behavior.zoom()
            .scaleExtent([1, 10])
            .on("zoom", function () {
                canvas.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
            }));

    svg.append("rect")
        .attr("width", width)
        .attr("height", height)
        .style("fill", "none")
        .style("pointer-events", "all")
        .on('mousedown', function () {
            startTime = new Date();
            //console.log(d3.mouse(this));
        })
        .on('mouseup', function () {
            endTime = new Date();
            if ((endTime - startTime) > 300) {
                var pos = d3.mouse(this);
                createNewNode(pos);
                console.log("long click");
            } else {
                console.log("regular click");
            }
        });

    var canvas = svg.append("g")
        .attr("class", "canvas");

    canvas.append("g")
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

    canvas.append("g")
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


    function showNode(nodeLink) {
        circlesContainer.selectAll("*").remove();
        currentNodeLink = nodeLink;
        var showNode = nodes[currentNodeLink];
        var circles = circlesContainer.selectAll("circle")
            .data(showNode.local || []);

        circles.exit().remove();
        circles.enter().append("circle");
        circles
            .attr("r", function (localLink) {
                return getStyle(localLink, "r", 20);
            })
            .attr("cx", function (localLink) {
                return getStyle(localLink, "x", 0);
            })
            .attr("cy", function (localLink) {
                return getStyle(localLink, "y", 0);
            })
            .call(drag);
    }

    function loadNode(nodeLink) {
        $scope.request('node', {
            nodeLink: nodeLink
        }, function (data) {
            merge(nodes, data.nodes);
            var replaceNodeLink = data.replacements[nodeLink] || data.nodeLink;
            showNode(replaceNodeLink);
        });
    }

    // show first node
    loadNode(N + 0);


    var circlesContainer = canvas.append("g")
        .attr("class", "circles");

    let drag = d3.behavior.drag()
        .origin(function (d) {
            return d;
        })
        .on("dragstart", dragstarted)
        .on("drag", dragged)
        .on("dragend", dragended);

    function dragstarted(d) {
        startTime = new Date();
        d3.event.sourceEvent.stopPropagation();
        d3.select(this).classed("dragging", true);
    }

    function dragged(d) {
        d3.select(this).attr("cx", d.x = d3.event.x).attr("cy", d.y = d3.event.y);
    }

    function dragended(d) {
        d3.select(this).classed("dragging", false);
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
            if (node.style != null){
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

    function isNumeric(num){
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
        nodes[styleLink] = {title: encodeValue(styleTitle)};
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


    setTimeout(function () {
        createNewNode([200, 200]);
    }, 1000);

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
    }
});