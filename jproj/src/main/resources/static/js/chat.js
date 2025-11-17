var stompClient = null;

function connect() {
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/public', function (message) {
            showMessage(JSON.parse(message.body));
        });
    });
}

function sendMessage() {
    var messageContent = document.getElementById("message").value;
    if (messageContent) {
        var message = {
            from: "User",
            text: messageContent
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
        document.getElementById("message").value = '';
    }
}

function showMessage(message) {
    var chatBox = document.getElementById("chat-box");
    var messageElement = document.createElement("p");
    messageElement.textContent = message.from + ": " + message.text;
    chatBox.appendChild(messageElement);
}

connect();