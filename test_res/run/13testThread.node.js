var i = 0;

function thread(){
    i = 1;
}

thread();

var iPrev = i;
Thread.sleep(100);
var iLast = i;

var test = iPrev == 0 && iLast == 1;