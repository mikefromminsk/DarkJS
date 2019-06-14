var i = 0;

function threadTest(){
    sleep(100);
    i = 1;
}

threadTest();

var iPrev = i;
sleep(100);
var iLast = i;

var test = iPrev == 0 && iLast == 1;