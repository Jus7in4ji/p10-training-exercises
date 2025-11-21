var stompClient = null;
var jwtToken = null;
var currentRoom = null; // default room
var activeSubscriptions = [];
var username = null;


async function storeToken() {
    jwtToken = document.getElementById("jwt-token").value.trim();
    username = null;

    if (!jwtToken) {
        alert("Please enter a valid JWT token!");
        return;
    }

    // Save token
    localStorage.setItem("jwtToken", jwtToken);

    //get username from jwt token
    const res = await fetch("/getusername?token=" + encodeURIComponent(jwtToken), {
        method: "GET",
        headers: {
            "Accept": "application/json"
        }
    });

    const data = await res.json();
    username = data.username;

    if (!username) {
        alert("Invalid Token. Could not extract username.");
        return;
    }

    // Save username also
    localStorage.setItem("username", username);

    alert("Token set .\nLogged in as: " + username);

    document.getElementById("jwt-token").value = "";
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

// user enters room name and clicks "Set Room"
async function setRoom() {
    let requestedRoom = document.getElementById("chat-room").value.trim();


    unsubscribeAll();
    const chatBox = document.getElementById("chat-box").innerHTML = "";
    if (requestedRoom.length === 0) {
        currentRoom= null;
        alert("Status: Disconnected from chat.\nRoom: [None]");
        console.log("Current room internally set to:", currentRoom);
        return;
    }

    const token = localStorage.getItem("jwtToken");

    // Send request to backend
    const res = await fetch("/subscribe-room", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            room: requestedRoom,
            user: username,
            isGroup: getIsGroup()
        })
    });

    const data = await res.json();

    alert("Status: " + data.Status + "\nRoom: " + data.Room);

    // Backend gives the true chat id:
    currentRoom = data.roomid;
    console.log("Current room internally set to:", currentRoom);


    // LOAD CHAT HISTORY
    if (currentRoom) {
        try {
            const historyResponse = await fetch("/gethistory", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    chatid: currentRoom,
                    user: username
                })
            });

            const history = await historyResponse.json();

            if (history && Array.isArray(history)) {
                history.forEach(entry => {
                    showMessage({
                        from: entry.sender,
                        text: entry.message,
                        sentTime: entry.sentTime
                    });
                });
            }
        } catch (err) {
            console.error("Failed to load history:", err);
        }
    }

    subscribeToRoom(currentRoom);
    
}

function getIsGroup() {
    return document.querySelector('input[name="toggle"]:checked').value === "true";
}

// SEND MESSAGE
function sendMessage() {
    var messageContent = document.getElementById("message").value.trim();

    // Do NOT send without token
    if (!username || username.trim().length === 0) {
        alert("Please enter a valid Token before sending messages.");
        return;
    }
    if(!currentRoom){
        alert("You are not connected to any chats.");
        return
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
        from: username,
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

    var sender = message.from;
    if(sender === username ){
        sender+=" (You)";
    }

    wrapper.innerHTML = `
        <div class="message-header">${sender}</div>
        <div class="message-text">${message.text}</div>
        <div class="message-time">${message.sentTime}</div>
    `;

    chatBox.appendChild(wrapper);
    chatBox.scrollTop = chatBox.scrollHeight; // keep scroll at bottom
}

connect();
