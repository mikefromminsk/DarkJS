function tr(translate, scale, rotate) {
    return (translate == null ? "" : "translate(" + translate + ")")
        + (scale == null ? "" : "scale(" + scale + ")")
        + (rotate == null ? "" : "rotate(" + rotate % 360 + ")");
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

function hyp(a, b) {
    return Math.pow(Math.pow(a, 2) + Math.pow(b, 2), 0.5);
}

function getTranslate(ths) {
    return d3.transform(d3.select(ths).attr("transform")).translate
}

function rad(val) {
    return val * (Math.PI / 180)
}

function url(path) {
    return "/client/" + path
}

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}

function replace(string, from, to) {
    return string.split(from).join(to);
}

function getStoreValue(key, def){
    return key == null ? def : key
}

function setStoreValue(key, def){
    return key == null ? def : key
}