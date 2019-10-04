function receive(message){
    gui("new message", message)
}

function send(nodename, message){
    get(nodename, "chat/local/receive", message)
}