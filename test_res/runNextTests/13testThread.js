var i = 0;

function thread(){
    Thread.sleep(100);
    i = 1;
}

thread();

var iPrev = i;
Thread.sleep(100);
var iLast = i;

var test = iPrev == 0 && iLast == 1;