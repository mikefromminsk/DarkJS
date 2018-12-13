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
        let node = nodes[nodeLink];
        if (node.style != null) {
            styleTitle = "!" + styleTitle;
            for (let i = 0; i < node.style.length; i++) {
                let styleLink = node.style[i];
                let styleNode = nodes[styleLink];
                if (styleNode.title === styleTitle && styleNode.value != null
                    && !styleNode.value.startsWith(N) && !styleNode.value.startsWith(W))
                    return decodeValue(styleNode.value);
            }
        }
    }
    return defValue;
}

function setStyleValue(nodeLink, styleTitle, styleValue) {
    styleTitle = "!" + styleTitle;
    let node = nodes[nodeLink];
    if (node.style != null)
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
    xhr.setRequestHeader('Content-Type', 'text/plain; charset=utf-8');
    let stringParams = (params == null) ? null : JSON.stringify(params);
    xhr.send(stringParams);
};

let request = function (params, success, error) {
    http("POST", remoteHost + 'node', params, success, error, true);
};


let lastNewId = 0;

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
        request({
            nodeLink: link,
            nodes: changes
        }, function (data) {
            successResponse(data, success);
            success(data.replacements[data.nodeLink] || data.nodeLink);
        }, error);
    }
}

function createLocalNode(parent, success, error) {
    let link = newNodeLink();
    let changes = {};
    changes[parent] = nodes[parent];
    if (changes[parent].local == null)
        changes[parent].local = [];
    changes[parent].local.push(link);
    changes[link] = {};
    request({
        nodeLink: parent,
        nodes: changes
    }, function (data) {
        successResponse(data, success);
        success(data.replacements[link]);
    }, error);
}

function loadNode(link, success, error) {
    request({
        nodeLink: link
    }, function (data) {
        successResponse(data, success);
        success(link);
    }, error);
}

function runNode(link, success, error) {
    request({
        nodeLink: link,
        run: true
    }, function (data) {
        successResponse(data, success);
        success(link, data);
    }, error);
}

function parseJs(link, code, success, error) {
    request({
        nodeLink: link,
        source_code: code
    }, function (data) {
        successResponse(data, success);
        success(link);
    }, error);
}
