let holdingPiece = false;
let holdingPieceCoor = {
    x: -1, y: -1
};

let alivePieces = [
    {side: "wh", kind: "rook", posCol: 1, posRow: 1},
    {side: "wh", kind: "knight", posCol: 2, posRow: 1},
    {side: "wh", kind: "bishop", posCol: 3, posRow: 1},
    {side: "wh", kind: "queen", posCol: 4, posRow: 1},
    {side: "wh", kind: "king", posCol: 5, posRow: 1},
    {side: "wh", kind: "bishop", posCol: 6, posRow: 1},
    {side: "wh", kind: "knight", posCol: 7, posRow: 1},
    {side: "wh", kind: "rook", posCol: 8, posRow: 1},

    {side: "wh", kind: "pawn", posCol: 1, posRow: 2},
    {side: "wh", kind: "pawn", posCol: 2, posRow: 2},
    {side: "wh", kind: "pawn", posCol: 3, posRow: 2},
    {side: "wh", kind: "pawn", posCol: 4, posRow: 2},
    {side: "wh", kind: "pawn", posCol: 5, posRow: 2},
    {side: "wh", kind: "pawn", posCol: 6, posRow: 2},
    {side: "wh", kind: "pawn", posCol: 7, posRow: 2},
    {side: "wh", kind: "pawn", posCol: 8, posRow: 2},

    {side: "bl", kind: "rook", posCol: 1, posRow: 8},
    {side: "bl", kind: "knight", posCol: 2, posRow: 8},
    {side: "bl", kind: "bishop", posCol: 3, posRow: 8},
    {side: "bl", kind: "queen", posCol: 4, posRow: 8},
    {side: "bl", kind: "king", posCol: 5, posRow: 8},
    {side: "bl", kind: "bishop", posCol: 6, posRow: 8},
    {side: "bl", kind: "knight", posCol: 7, posRow: 8},
    {side: "bl", kind: "rook", posCol: 8, posRow: 8},

    {side: "bl", kind: "pawn", posCol: 1, posRow: 7},
    {side: "bl", kind: "pawn", posCol: 2, posRow: 7},
    {side: "bl", kind: "pawn", posCol: 3, posRow: 7},
    {side: "bl", kind: "pawn", posCol: 4, posRow: 7},
    {side: "bl", kind: "pawn", posCol: 5, posRow: 7},
    {side: "bl", kind: "pawn", posCol: 6, posRow: 7},
    {side: "bl", kind: "pawn", posCol: 7, posRow: 7},
    {side: "bl", kind: "pawn", posCol: 8, posRow: 7},
];

let mouseX;
let mouseY;
let letters = ["A", "B", "C", "D", "E", "F", "G", "H"];
let gameSocket;
let whitePlaying = false;
let currentMove = {};

const imageSize = 60;

function update(ctx) {
    for (let row = 0; row < 8; ++row) {
        for (let cell = 0; cell < 8; ++cell) {
            /*if (Math.floor(mouseX / imageSize) === cell && Math.floor(mouseY / imageSize) === row) {
                ctx.fillStyle = "rgba(20,20,20,0.5)";
            } else {
            }*/

            ctx.fillStyle = getBackgroundColor(row, cell);

            ctx.fillRect(imageSize * cell, imageSize * row, imageSize, imageSize);
        }
    }

    for (let pieceI = 0; pieceI < alivePieces.length; ++pieceI) {
        let piece = alivePieces[pieceI];
        let pieceImg = document.getElementById(piece.side + "_" + piece.kind);

        if (holdingPiece && holdingPieceCoor.x + 1 === piece.posCol && holdingPieceCoor.y === piece.posRow) {
            ctx.drawImage(pieceImg, mouseX - (imageSize / 2), mouseY - (imageSize / 2), imageSize, imageSize);
        } else {
            ctx.drawImage(pieceImg, (piece.posCol - 1) * imageSize - 2, (Math.abs(8 - piece.posRow)) * imageSize,
                imageSize, imageSize);
        }
    }
}

function onLoad() {
    var client = new SockJS("/ingame/ws");
    gameSocket = Stomp.over(client);
    gameSocket.connect({}, onConnected, onError);

    let canvas = document.getElementById("board");
    let ctx = canvas.getContext("2d");

    canvas.onmousemove = function (e) {
        mouseX = e.offsetX;
        mouseY = e.offsetY;
    };

    canvas.onmousedown = function (e) {
        holdingPiece = true;
        holdingPieceCoor.x = Math.floor(e.offsetX / imageSize);
        holdingPieceCoor.y = Math.abs(8 - Math.floor(e.offsetY / imageSize));
    };

    canvas.onmouseup = onMouseUp;

    setInterval(function () {
        update(ctx);
    }, 10);

    onNextTurn();
}

function onNextTurn() {
    whitePlaying = !whitePlaying;

    if (whitePlaying) {
        document.getElementById("playingWhite").classList.remove("hidden");
        document.getElementById("playingBlack").classList.add("hidden");
    } else {
        document.getElementById("playingWhite").classList.add("hidden");
        document.getElementById("playingBlack").classList.remove("hidden");
    }
}

function onMouseUp(e) {
    holdingPiece = false;

    currentMove.posCol = Math.floor(e.offsetX / imageSize) + 1;
    currentMove.posRow = Math.abs(8 - Math.floor(e.offsetY / imageSize));

    currentMove.from = letters[holdingPieceCoor.x] + "" + holdingPieceCoor.y;
    currentMove.to = letters[currentMove.posCol - 1] + "" + currentMove.posRow;

    currentMove.id = lobbyId + ":" + user + ":" + Math.random();
    currentMove.x = holdingPieceCoor.x + 1;
    currentMove.y = holdingPieceCoor.y;

    gameSocket.send("/app/lobby.move", {},
        JSON.stringify(
            {
                actionType: "MOVE",
                lobbyId: lobbyId,
                sender: "me",
                from: currentMove.from,
                moveId: currentMove.id,
                to: currentMove.to
            }
        )
    );
};

function onConnected() {
    gameSocket.subscribe("/ingame", onReceive);
}

function onError(err) {
    alert(err);
}

function onReceive(payload) {
    let message = JSON.parse(payload.body);

    if (message.type === "MoveResult") {
        onMoveResultReceived(message);
    }
}

function onMoveResultReceived(message) {
    if (message.actionResult === "InvalidMove") {
        alert("Invalid Move!");
    } else if (message.actionResult === "OutOfTurn") {
        alert("Not your turn!");
    } else if (message.actionResult === "InvalidMoveKingThreatened") {
        alert("Your king is in danger!");
    } else if (message.actionResult === "PieceDoesNotExist") {
        alert("Not sure how but you move a non-existent piece!");
    } else if (message.moveId === currentMove.id) {
        let indicesToRemove = [];

        for (let pieceI = 0; pieceI < alivePieces.length; ++pieceI) {
            let piece = alivePieces[pieceI];

            if (currentMove.x === piece.posCol && currentMove.y === piece.posRow) {
                piece.posCol = currentMove.posCol;
                piece.posRow = currentMove.posRow;
            } else if (currentMove.posCol === piece.posCol && currentMove.posRow === piece.posRow) {
                indicesToRemove.push(pieceI);
            }
        }

        for (let toRemoveI = indicesToRemove.length - 1; toRemoveI >= 0; toRemoveI--) {
            alivePieces.splice(indicesToRemove[toRemoveI], 1);
        }

        if (message.actionResult === "Check") {
            alert("Check");
        }

        if (message.actionResult === "Checkmate") {
            alert("Check");
        }
    }
}

function getBackgroundColor(row, cell) {
    return row % 2 === 0 ? (cell % 2 === 0 ? "#FFFFFF" : "#1440a9") : (cell % 2 === 0 ? "#1440a9" : "#FFFFFF");
}
