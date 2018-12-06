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

function encodeValue(value) {
    if (typeof value === "number")
        return "" + value;
    if (typeof value === "string")
        return "!" + value;
    if (typeof value === "boolean")
        return value ? "true" : "false";
}

function getStyleValue(nodeLink, styleTitle, defValue) {
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

function setStyleValue(nodeLink, styleTitle, styleValue) {
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

let http = function (method, endpoint, params, success, error, async) {
    if (error == null)
        error = function (status, response) {
            console.log(status);
            console.log(response);
        };

    console.log(params);
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
                if (object != null)
                    success(object)
            } else {
                error(xhr.status);
            }
        }
    };
    xhr.onerror = function () {
        error(xhr.status);
    };

    xhr.open(method, endpoint, async || true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
    var stringParams = (params == null) ? null : JSON.stringify(params);
    xhr.send(stringParams);
};

let request = function (params, success, error) {
    http("POST", remoteHost + 'node', params, success, error, true);
};

function setStyle(nodeLink, styleObj, success, error) {
    if (typeof styleObj === "object") {
        let changes = {};
        changes[nodeLink] = nodes[nodeLink];
        for (key in styleObj)
            if (styleObj.hasOwnProperty(key)) {
                let keyLink = setStyleValue(nodeLink, key, styleObj[key]);
                changes[keyLink] = nodes[keyLink];
            }
        request({
            nodeLink: nodeLink,
            nodes: changes
        }, function (data) {
            merge(nodes, data.nodes);
            success(data.replacements[nodeLink] || data.nodeLink)
        }, error);
    }
}


let lastNewId = 0;

function newNodeLink() {
    let nodeLink = W + lastNewId++;
    nodes[nodeLink] = {};
    return nodeLink;
}

function createLocalNode(parent, success, error) {
    let nodeLink = newNodeLink();
    let changes = {};
    changes[parent] = nodes[parent];
    if (changes[parent].local == null)
        changes[parent].local = [];
    changes[parent].local.push(nodeLink);
    changes[nodeLink] = {};
    request({
        nodeLink: nodeLink,
        nodes: changes
    }, function (data) {
        merge(nodes, data.nodes);
        success(data.replacements[nodeLink] || data.nodeLink);
    }, error);
}

function loadNode(nodeLink, success, error) {
    request({
        nodeLink: nodeLink
    }, function (data) {
        merge(nodes, data.nodes);
        success(data.replacements[nodeLink] || data.nodeLink);
    }, error);
}
