var stompClient = null;
var jwtToken = null;

function storeToken() {
    jwtToken = document.getElementById("jwt-token").value;
    alert("Token set!");
}

function connect() {
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe("/topic/public", function (message) {
            showMessage(JSON.parse(message.body));
        });
    });
}

function sendMessage() {
    var messageContent = document.getElementById("message").value;

    if (messageContent.trim().length === 0) return;

    var msg = {
        text: messageContent,
        token: jwtToken   // ⭐ send token to backend
    };

    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(msg));

    document.getElementById("message").value = "";
}

function showMessage(message) {
    var chatBox = document.getElementById("chat-box");
    var messageElement = document.createElement("p");
    messageElement.textContent = message.from + ": " + message.text;
    chatBox.appendChild(messageElement);
}

connect();
