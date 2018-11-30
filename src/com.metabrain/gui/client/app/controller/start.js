app.controller("start", function ($scope, $mdDialog) {

    let N = "n";
    let W = "w";

    var width = 700,
        height = 500;

    var startTime;
    var endTime;

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
                createNewNode();
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

    /*$scope.request('getNode', {
        nodeId: 0
    }, function () {

    });*/

    function showCrrentNode() {
        var currentNode = nodes[N + currentNodeId];
        if (currentNode != null) {
            circles.selectAll("*").remove();
            if (currentNode.local != null){
                var locals = [];
                for (var i = 0; i < currentNode.local.length; i++)
                    locals.push(currentNode.local[i])
                circles.selectAll("circle")
                    .data(locals)
                    .enter().append("circle")
                    .attr("r", 20)
                    .attr("cx", function (d) {
                        return d.x;
                    })
                    .attr("cy", function (d) {
                        return d.y;
                    })
                    .call(drag);
            }
        }
    }

    /*var data = d3.range(20).map(function () {
        return {x: Math.random() * width, y: Math.random() * height};
    });*/

    var circles = canvas.append("g")
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


    let nodes = {};
    var currentNodeId;

    function setCurrentNode(nodeId) {
        $scope.request('getNode', {
            nodeId: nodeId
        }, function (data) {
            merge(nodes, data.body, true);
            currentNodeId = nodeId;
        });
    }

    setCurrentNode(0);

    function setLink() {
    }

    function addLink(nodeId, linkName, attachId) {
        if (nodeId instanceof Number)
            nodeId = N + nodeId;
        if (attachId instanceof Number)
            attachId = N + attachId;
        var node = nodes[nodeId];
        if (node[linkName] == null || !(node[linkName] instanceof Array))
            node[linkName] = [];
        node[linkName].push(attachId)
    }

    function createNewNode() {
        addLink(currentNodeId, "local", W + 0);
        $scope.request('setNode', {
            nodeId: currentNodeId,
            body: nodes[N + currentNodeId]
        }, function (data) {
            merge(nodes, data.body, true);
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
    }
});