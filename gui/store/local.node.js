var storeNode = "store.node"

function getAppList(){
    return get(storeNode, "node/local/serialize", "/")
}


function getCode(path) {
    return Node.serialize(path)
}

function downloadApp(appName) {
    var code = get(storeNode, "store/local/getCode", appName)
    Node.eval(appName, code)
}
