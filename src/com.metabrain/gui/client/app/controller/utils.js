
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

function posMul(a, b) {
    return [a[0] * b, a[1] * b];
}

function posDiv(a, b) {
    return [a[0] / b, a[1] / b];
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

function rad(val) {
    return val * (Math.PI / 180)
}

function url(path){
    return "/client/" + path
}
