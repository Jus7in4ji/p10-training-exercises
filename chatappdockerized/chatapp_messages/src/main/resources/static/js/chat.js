var stompClient = null;
var jwtToken = null;
var currentRoom = null; // default room
var activeSubscriptions = [];
var username = null;
const gatewayurl = "http://localhost:8080"

function openFilePicker() {
    document.getElementById("file-input").click();
}

async function handleFileSelected(input) {
    const file = input.files[0];
    if (!file) return;

    // Validate login & room
    if (!username || !currentRoom) {
        alert("You cannot upload files. login and connect to a chat to do so");
        input.value = ""; // reset
        return;
    }

    console.log("Selected file:", file);

    // Optional: show file name in message box
    document.getElementById("message").value = `[${file.name}]`;

    // Build multipart payload
    const formData = new FormData();
    formData.append("file", file);

    // data part → JSON
    formData.append(
        "data",
        new Blob(
            [JSON.stringify({
                sender: username,
                chatid: currentRoom
            })],
            { type: "application/json" }
        )
    );

    try {
        const res = await fetch(gatewayurl + "/files/upload", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + jwtToken
            },
            body: formData
        });

        if (!res.ok) {
            throw new Error("Upload failed");
        }

        const fileResponse = await res.json();
        console.log("Upload success:", fileResponse);

        // 🔹 Build chat message using file response
        const fileMsg = {
            msgid: fileResponse.fileid,         // 
            text: fileResponse.name,             // file name
            from: username,
            room: currentRoom,
            msgread: false,
            sentTime: Intl.DateTimeFormat().resolvedOptions().timeZone,
            isfile: true
        };

        stompClient.send("/app/chat.sendMessage",{},JSON.stringify(fileMsg));

        // Clear message input
        document.getElementById("message").value = "";

    } catch (err) {
        console.error(err);
        alert("File upload failed");
    } finally {
        // Reset input so same file can be selected again
        input.value = "";
    }
}


async function storeToken() {
    jwtToken = document.getElementById("jwt-token").value.trim();
    username = null;

    if (!jwtToken) {
        alert("Please enter a valid JWT token!");
        return;
    }

    // Save token
    localStorage.setItem("jwtToken", jwtToken);
    document.getElementById("chat-box").innerHTML = "";

    //get username from jwt token
    const res = await fetch(gatewayurl + "/userchat/getname", {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Authorization": "Bearer " + jwtToken
        }
    });

    const data = await res.json();
    username = data.username;


    if (!username) {
        alert("Invalid Token. Could not extract username.");
        document.getElementById("logged-user").innerText = "";
        localStorage.setItem("username", null);
        return;
    }

    document.getElementById("logged-user").innerText = username;
    localStorage.setItem("username", username);
    alert("Token set .\nLogged in as: " + username);

    document.getElementById("jwt-token").value = "";
}

function logoutUser() {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("username");
    jwtToken = null;
    username = null;

    unsubscribeAll();

    if (stompClient && stompClient.connected) {
        stompClient.disconnect(() => console.log("Disconnected"));
    }

    currentRoom = null;
    document.getElementById("logged-user").innerText = "Not Logged In";
    document.getElementById("chat-box").innerHTML = "";
    alert("Logged out.");
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
    let subscription = stompClient.subscribe("/topic/" + roomName, function(response) {
        var message = JSON.parse(response.body);
        console.log("new message received: "+message.text);
        showMessage(message, true); // first display the message

        // THEN call read function if incoming message
        if (message.from !== username) {
            stompClient.send("/app/chat.read", {}, JSON.stringify({msgId: message.msgid}));
        }
    });


    let readSub = stompClient.subscribe("/topic/read", function(message) {
        const msgId = message.body;      // id from backend
        markMessageAsRead(msgId);        // function to flip tick
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
    if (!jwtToken) {
        alert("Please login before connecting to any room");
        return;
    }

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
    const res = await fetch(gatewayurl+"/userchat/subscribe-room", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + jwtToken
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
            const historyResponse = await fetch(gatewayurl+"/msg/gethistory", {
                method: "POST",
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + jwtToken
                },
                body: JSON.stringify({
                    chatid: currentRoom,
                    user: username,
                    timezone: Intl.DateTimeFormat().resolvedOptions().timeZone
                })
            });

            const history = await historyResponse.json();

            if (history && Array.isArray(history)) {
                history.forEach(entry => {
                    showMessage({
                        msgid:entry.msgid,
                        from: entry.from,
                        text: entry.text,
                        sentTime: entry.sentTime,
                        msgread: entry.msgread
                    }, false);
                    if (entry.from !== username) {
                        stompClient.send("/app/chat.read", {}, JSON.stringify({msgId: entry.msgid}));
                    }
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

function markMessageAsRead(msgId) {
    const messageElement = document.querySelector(`[data-id='${msgId}']`);
    if (messageElement) {
        const tickElement = messageElement.querySelector(".tick");
        if (tickElement) {
            tickElement.style.color = "#008000"; // green tick
            console.log("tick found");
        }
        else {
            console.log("no tick found");
        }
    }
}

// SEND MESSAGE
async function sendMessage() {
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

    const res = await fetch(gatewayurl+"/userchat/isactive", {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Authorization": "Bearer " + jwtToken
        }
    });

    const data = await res.json();
    username = data.username;
    active = data.active;

    if (!username||!stompClient || !stompClient.connected|| active!= "true") {
        const chatBox = document.getElementById("chat-box").innerHTML = "";
        jwtToken = null;
        unsubscribeAll();
        alert("Message was not sent!\n You are disconnected. make sure you are connected to the internet ");
        return;
    }

    // Build message object
    var msg = {
        text: messageContent,
        from: username,
        room: currentRoom,
        msgread : false,
        sentTime: Intl.DateTimeFormat().resolvedOptions().timeZone,
        isfile: false
    };

    // Send
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(msg));

    document.getElementById("message").value = "";
}

// DISPLAY MESSAGE
async function showMessage(message, local) {
    var chatBox = document.getElementById("chat-box");

    var wrapper = document.createElement("div");
    wrapper.classList.add("message-bubble");
    wrapper.setAttribute("data-id", message.msgid); // UNIQUE IDENTIFIER

    let sender = message.from === username ? message.from + " (You)" : message.from;

    let timeString = local
        ? new Date().toLocaleTimeString([], { hour:"2-digit", minute:"2-digit", second:"2-digit", hour12:true })
        : message.sentTime;

    const tickColor = message.msgread ? "#008000" : "#000000"; 
    const tickIcon = `<span class="tick" style="color:${tickColor};">✔</span>`;

    wrapper.innerHTML = `
        <div class="message-header">${sender}</div>
        <div class="message-text">${message.text}</div>

        <div class="message-footer">
            <span class="time-left">${timeString}</span>
            <span class="status-right">${tickIcon}</span>
        </div>
    `;

    chatBox.appendChild(wrapper);
    chatBox.scrollTop = chatBox.scrollHeight; // keep scroll at bottom
}

connect();
console.log("timezone is "+Intl.DateTimeFormat().resolvedOptions().timeZone)