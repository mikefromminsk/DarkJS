var N = "n";
var W = "w";
var nodes = {};

function merge(source, object, append) {
    if (append || true !== true) {
        if (source instanceof Array)
            source.splice(0, source.length);
        if (source instanceof Object)
            Object.keys(source).forEach(function (key) {
                delete source[key];
            });
    }
    if (source != null)
        for (let key in object)
            if (object.hasOwnProperty(key)) {
                if (source instanceof Array)
                    source.push(object[key]);
                else
                    source[key] = object[key];
            }
}

function encodeValue(value) {
    if (typeof value === "string")
        return "!" + value;
    return value;
}

function getStyleValue(link, styleTitle, defValue) {
    if (link != null && link.startsWith(N)) {
        let node = nodes[link];
        if (node != null && node.style != null) {
            styleTitle = "!" + styleTitle;
            for (let i = 0; i < node.style.length; i++) {
                let styleLink = node.style[i];
                let styleNode = nodes[styleLink];
                if (styleNode.title === styleTitle && styleNode.value != null) {
                    if (typeof styleNode.value === "string") {
                        if (styleNode.value.startsWith("!"))
                            return styleNode.value.substr(1);
                    } else {
                        return styleNode.value;
                    }
                }
            }
        }
    }
    return defValue;
}

function setStyleValue(nodeLink, styleTitle, styleValue) {
    styleTitle = "!" + styleTitle;
    let node = nodes[nodeLink];
    if (node != null && node.style != null)
        for (let i = 0; i < node.style.length; i++) {
            let styleLink = node.style[i];
            let styleNode = nodes[styleLink];
            if (styleNode.title === styleTitle) {
                styleNode.value = encodeValue(styleValue);
                return styleLink;
            }
        }
    if (node.style == null)
        node.style = [];
    let styleLink = newNodeLink();
    nodes[styleLink] = {title: styleTitle};
    nodes[styleLink].value = encodeValue(styleValue);
    node.style.push(styleLink);
    return styleLink;
}

let http = function (method, endpoint, params, success, error, async) {
    if (error == null)
        error = function (status, response) {
            console.log(response);
        };

    let xhr = XMLHttpRequest ? new XMLHttpRequest() :
        new ActiveXObject("Microsoft.XMLHTTP");

    xhr.onload = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                let object = null;
                try {
                    object = JSON.parse(xhr.response);
                } catch (e) {
                    error("Json parse error", xhr.response);
                }
                if (object != null) {
                    if (object.error != null || object.stack != null)
                        error(object.error, xhr.response);
                    else
                        success(object)
                }
            } else {
                error(xhr.status, xhr.response);
            }
        }
    };
    xhr.onerror = function () {
        error(xhr.status);
    };

    xhr.open(method, endpoint, async || true);
    xhr.setRequestHeader('Content-Type', 'text/plain; charset=utf-8');
    let stringParams = (params == null) ? null : JSON.stringify(params);
    xhr.send(stringParams);
};

let request = function (url, params, success, error) {
    http("POST", remoteHost + url, params, success, error, true);
};

let nodeRequest = function (params, success, error) {
    request('node', params, success, error);
};

let lastNewId = 0;

function newDataNode(data) {
    return encodeValue(data);
}

function newNodeLink() {
    let nodeLink = W + lastNewId++;
    nodes[nodeLink] = {};
    return nodeLink;
}

function deleteReplacements(replacements) {
    for (let key in replacements)
        if (replacements.hasOwnProperty(key))
            delete nodes[key]
}

function successResponse(data) {
    merge(nodes, data.nodes);
    deleteReplacements(data.replacements);
}

function setStyle(link, styleObj, success, error) {
    if (typeof styleObj === "object") {
        let changes = {};
        changes[link] = nodes[link];
        for (let key in styleObj)
            if (styleObj.hasOwnProperty(key)) {
                let keyLink = setStyleValue(link, key, styleObj[key]);
                changes[keyLink] = nodes[keyLink];
            }
        nodeRequest({
            nodeLink: link,
            nodes: changes
        }, function (data) {
            successResponse(data, success);
            if (success != null)
                success(data.replacements[data.nodeLink] || data.nodeLink);
        }, error);
    }
}


function setLink(fromNode, linkName, toNode, success, error) {
    let changes = {};
    changes[fromNode] = nodes[fromNode];
    changes[fromNode][linkName] = toNode;
    nodeRequest({
        nodeLink: fromNode,
        nodes: changes
    }, function (data) {
        successResponse(data, success);
        success(data.replacements[toNode]);
    }, error);
}

function addLink(fromNode, linkName, toNode, success, error) {
    let changes = {};
    changes[fromNode] = nodes[fromNode];
    if (changes[fromNode][linkName] == null)
        changes[fromNode][linkName] = [];
    changes[fromNode][linkName].push(toNode);
    changes[toNode] = {};
    nodeRequest({
        nodeLink: fromNode,
        nodes: changes
    }, function (data) {
        successResponse(data, success);
        success(data.replacements[toNode]);
    }, error);
}

function loadNode(link, success, error) {
    nodeRequest({
        nodeLink: link
    }, function (data) {
        successResponse(data, success);
        success(link);
    }, error);
}

function runNode(link, success, error) {
    nodeRequest({
        nodeLink: link,
        run: true
    }, function (data) {
        successResponse(data, success);
        success(link, data);
    }, error);
}

function parseAndRunNode(link, code, success, error) {
    nodeRequest({
        nodeLink: link,
        source_code: code,
        run: true
    }, function (data) {
        successResponse(data, success);
        success(link);
    }, error);
}


function getTitle(link) {
    if (nodes[link] != null && nodes[link].title != null)
        return nodes[link].title.substr(1);
    return "";
}
