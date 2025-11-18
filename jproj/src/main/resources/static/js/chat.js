var stompClient = null;
var jwtToken = null;
var currentRoom = "public"; // default room
var activeSubscriptions = [];


function storeToken() {
    jwtToken = document.getElementById("jwt-token").value.trim();

    if (!jwtToken) {
        alert("Please enter a valid JWT token!");
        return;
    }
    alert("Token set successfully!");
}

// Connect WebSocket and subscribe to default room
function connect() {
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        subscribeToRoom(currentRoom);
    });
}

// subscribe function wrapper
function subscribeToRoom(roomName) {
    console.log("Subscribing to: /topic/" + roomName);

    // Subscribe and store subscription handle
    let subscription = stompClient.subscribe("/topic/" + roomName, function (message) {
        showMessage(JSON.parse(message.body));
    });

    activeSubscriptions.push(subscription);
}

function unsubscribeAll() {
    activeSubscriptions.forEach(sub => {
        try {
            sub.unsubscribe();
        } catch (e) {
            console.warn("Failed to unsubscribe:", e);
        }
    });

    activeSubscriptions = []; // clear the list
    console.log("Unsubscribed from all rooms");
}

// Called when user enters a new room name and clicks "Set Room"
function setRoom() {
    let newRoom = document.getElementById("chat-room").value.trim();

    if (newRoom.length === 0) {
        newRoom = "public";
    }

    currentRoom = newRoom;
    alert("Chat room set to: " + currentRoom);

    // Unsubscribe from all previous rooms
    unsubscribeAll();

    // Subscribe to new room
    subscribeToRoom(currentRoom);
}

// SEND MESSAGE
function sendMessage() {
    var messageContent = document.getElementById("message").value.trim();

    // Do NOT send without token
    if (!jwtToken || jwtToken.trim().length === 0) {
        alert("Please enter a valid Token before sending messages.");
        return;
    }

    // Do NOT send empty message
    if (messageContent.length === 0) return;

    // Do NOT send if disconnected
    if (!stompClient || !stompClient.connected) {
        alert("WebSocket is disconnected. Refresh the page.");
        return;
    }

    // Build message object
    var msg = {
        text: messageContent,
        token: jwtToken,
        room: currentRoom
    };

    // 5️⃣ Send
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(msg));

    document.getElementById("message").value = "";
}

// DISPLAY MESSAGE
function showMessage(message) {
    var chatBox = document.getElementById("chat-box");

    var wrapper = document.createElement("div");
    wrapper.classList.add("message-bubble");

    wrapper.innerHTML = `
        <div class="message-header">${message.from}</div>
        <div class="message-text">${message.text}</div>
        <div class="message-time">${message.sentTime}</div>
    `;

    chatBox.appendChild(wrapper);
    chatBox.scrollTop = chatBox.scrollHeight; // keep scroll at bottom
}

connect();
