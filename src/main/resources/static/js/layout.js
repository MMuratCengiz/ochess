function showPopup(popup) {
    document.getElementById("popupBackground").style.display = "block";
    document.getElementById(popup).style.display = "block";
}

function hidePopup(popup) {
    document.getElementById("popupBackground").style.display = "none";
    document.getElementById(popup).style.display = "none";
}

function showNav(text) {
    document.getElementById("navigationDetails").style.display = "inline";
    document.getElementById("navigationDetails").innerText = text;
}

function hideNav() {
    document.getElementById("navigationDetails").style.display = "none";
}

function bodyKeyPress() {
    document.getElementById("messageInput").focus();
}

let messageCount = 1;
function addMessage(from, message) {
    document.getElementById("messages").innerHTML +=
        "<div class=\"message" + (messageCount % 2 === 0 ? " dark" : "") + "\">" +
        "   <span class='sender'>" + from +":</span> " +
        "   <span class='content'>" + message +"</span> " +
        "</div>";

    ++messageCount;
}

function layoutLoad() {
    let messageInput = document.getElementById("messageInput");

    messageInput.addEventListener("keyup", (ev) => {
        if (ev.key === "Enter") {
            sendChatMessage(messageInput.value);
            messageInput.value = "";
        }
    });
}