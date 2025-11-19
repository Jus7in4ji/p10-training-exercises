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
    localStorage.setItem("jwtToken",jwtToken);
    alert("Token set successfully!");

    document.getElementById("jwt-token").value = ""; //clear jwt textbox
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
async function subscribeToRoom(roomName) {
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
    let requestedRoom = document.getElementById("chat-room").value.trim();

    if (requestedRoom.length === 0) {
        requestedRoom = "public";
    }

    const token = localStorage.getItem("jwtToken");

    // Send request to backend
    fetch("/subscribe-room", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            room: requestedRoom,
            token: token,
            isGroup: getIsGroup()
        })
    })
    .then(res => res.json())
    .then(data => {

        // Display ONLY status + room
        alert("Status: " + data.Status + "\nRoom: " + data.Room);

        // UPDATE currentRoom secretly using roomid
        currentRoom = data.roomid;

        console.log("Current room internally set to:", currentRoom);

        // Resubscribe cleanly
        unsubscribeAll();
        document.getElementById("chat-box").innerHTML = "";//clear previous chat

        subscribeToRoom(currentRoom);
    })
    .catch(err => console.error("Error:", err));
}

function getIsGroup() {
    return document.querySelector('input[name="toggle"]:checked').value === "true";
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

    // Send
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
