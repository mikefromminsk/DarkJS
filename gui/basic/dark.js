function objToFormArgs(element, key, list) {
    var list = list || [];
    if (typeof(element) == 'object') {
        for (var idx in element)
            objToFormArgs(element[idx], key ? key + '[' + idx + ']' : idx, list);
    } else if (typeof(element) == 'string') {
        list.push(key + '=!' + encodeURIComponent(element));
    } else if (typeof(element) == 'number') {
        list.push(key + '=' + element);
    } else if (typeof(element) == 'boolean') {
        list.push(key, (element) ? "true" : "false")
    }
    return list.join('&');
}

function get(nodePath, params, success) {
    if (DEBUG)
        return null
    var xhr = new XMLHttpRequest();
    if (nodePath[0] != "/")
        nodePath = "/" + nodePath;
    xhr.open("POST", nodePath, false);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.contentTypeIs = function (type) {
        var contentType = this.getResponseHeader('content-type');
        return (contentType.split(";").indexOf(type) != -1)
    };
    xhr.onload = function () {
        if (xhr.readyState === 4)
            if (xhr.status === 200) {
                var response = xhr.response;
                if (this.contentTypeIs("application/json"))
                    response = JSON.parse(response);
                success(response);
            } else
                console.error(xhr.statusText);
    };
    xhr.onerror = function (e) {
        console.error(xhr.statusText);
    };
    xhr.send(objToFormArgs(params));
}

// TODO put all leaded modes to one storage
function optimizeNodes(nodes) {

    function convert(linkValue) {
        if (linkValue.charAt(0) == 'n') {
            var linkNode = nodes[linkValue];
            if (linkNode != null)
                return linkNode;
            return function () {
                // TODO request to localNode
            }
        }
        if (linkValue.charAt(0) == '!')
            return linkValue.substr(1);
        if (linkValue.charCodeAt(0) >= "0".charCodeAt(0) && linkValue.charCodeAt(0) <= "9".charCodeAt(0))
            return parseFloat(linkValue);

        //console.log("unnoute link type " + linkValue);
        return linkValue;
    }

    for (var nodeName in nodes) {
        var node = nodes[nodeName];
        for (var link in node) {
            var linkValue = node[link];
            if (typeof  linkValue === "string") {
                node[link] = convert(linkValue);
            } else if (linkValue instanceof Array) {
                var links = linkValue;
                for (var i = 0; i < links.length; i++)
                    links[i] = convert(links[i])
            }
        }
    }
}

function getFirstNode(data) {
    return data[Object.keys(data)[0]];
}

function getResultNode(data) {
    data = JSON.parse(data);
    optimizeNodes(data);
    return getFirstNode(data)
}

function loadNode(loadedNode, serializePath) {

    if (Object.keys(loadedNode).length == 0)
        get("/node/local.node.js/serialize", {path: serializePath}, function (data) {
            var firstNode = getResultNode(data)

            if (firstNode.type == null || firstNode.type == "thread") {
                for (var i in firstNode.local) {
                    let local = firstNode.local[i];
                    let title = local.title;
                    if (local.param != null || (local.next != null && local.parser != "node.js")) {
                        loadedNode[title] = function () {
                            return runNode(serializePath + "/" + title, local, arguments)
                        };
                    } else if (local.cell != null) {
                        // TODO parse array
                    } else if (local.local != null) {
                        loadedNode["$" + title] = {};
                        Object.defineProperty(loadedNode, title, {
                            get: function () {
                                return loadNode(loadedNode["$" + title], serializePath + "/" + title);
                            }
                        });
                    } else if (local.value != null) {
                        loadedNode[title] = local.value
                    }
                }
            } else if (firstNode.data != null) {
                loadedNode = firstNode.data
            }

        });

    return loadedNode;
}

function runNode(path, scheme, args) {
    var argObj = {};
    for (var i = 0; i < args.length; i++)
        argObj[scheme.param[i].title] = args[i];
    var result = null;
    get(path, argObj, function (data) {
        result = data
    })
    return result;
}


var loadedRoots = {};

Object.defineProperty(window, 'master', {
    get: function () {
        return loadNode(loadedRoots, "");
    }
});

function clearMaster() {
    loadedRoots = {};
}

Object.defineProperty(window, 'localnode', {
    get: function () {
        var props = (window.location.hash.substr(2) + "/local").split("/");
        var itemnode = master;
        props.forEach(function (prop) {
            itemnode = itemnode[prop];
        })
        return itemnode || {};
    }
});


// TODO get ws port from node
if (!DEBUG){
    var defaultHttpPort = 8080;
    var defaultWsPort = 9500;

    var currentWsPort = (window.location.port - defaultHttpPort) + defaultWsPort;
    var socket = new WebSocket("ws://localhost:" + currentWsPort + "/gui");

    socket.onmessage = function (event) {
        var node = getResultNode(event.data)
        var observeId = node.param[0].value
        var caller = observers[observeId]
        if (caller != null)
            caller.apply(null, node.param.slice(1))
    };

    var observers = {};

    function observe(key, callback) {
        observers[key] = callback;
    }
}