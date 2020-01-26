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

const letters = ["A", "B", "C", "D", "E", "F", "G", "H"];

let mouseX;
let mouseY;
let gameSocket;
let whitePlaying = false;
let textCountDownSecs = 0.0;
let textToShow = "";
let imageHeight = 110;
let squareSize = 130;
let imageWidth = 63;

function update(ctx, deltaTime) {
    for (let row = 0; row < 8; ++row) {
        for (let cell = 0; cell < 8; ++cell) {
            if (Math.floor(mouseX / squareSize) === cell && Math.floor(mouseY / squareSize) === row) {
                ctx.fillStyle = "rgba(20,20,20,0.6)";
            }

            ctx.fillStyle = getBackgroundColor(row, cell);
            ctx.fillRect(squareSize * cell, squareSize * row, squareSize, squareSize);
        }
    }

    for (let pieceI = 0; pieceI < alivePieces.length; ++pieceI) {
        let piece = alivePieces[pieceI];
        let pieceImg = document.getElementById(piece.side + "_" + piece.kind);

        if (holdingPiece && holdingPieceCoor.x + 1 === piece.posCol && holdingPieceCoor.y === piece.posRow) {
            ctx.drawImage(pieceImg, mouseX - (squareSize / 2) + 30, mouseY - (squareSize / 2) + 5, imageWidth, imageHeight);
        } else {
            ctx.drawImage(pieceImg,
                (piece.posCol - 1) * squareSize + 30,
                (Math.abs(8 - piece.posRow)) * squareSize + 10,
                imageWidth, imageHeight);
        }
    }


    textCountDownSecs -= deltaTime;

    if (textCountDownSecs <= 0) {
        textToShow = "";
    }

    if (textToShow.length !== 0) {
        ctx.font = "bold 48pt Tahoma, Geneva, sans-serif";
        ctx.fillStyle = "darkred";
        ctx.fillText(textToShow, 0, squareSize * 3.95);
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
        holdingPieceCoor.x = Math.floor(e.offsetX / squareSize);
        holdingPieceCoor.y = Math.abs(8 - Math.floor(e.offsetY / squareSize));
    };

    canvas.onmouseup = onMouseUp;

    let delta;
    let deltaBefore;

    setInterval(function () {
        delta = ((new Date()).getTime() - deltaBefore) / 1000;
        deltaBefore = ((new Date()).getTime());

        update(ctx, delta);
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

    let currentMove = {};

    currentMove.posCol = Math.floor(e.offsetX / squareSize) + 1;
    currentMove.posRow = Math.abs(8 - Math.floor(e.offsetY / squareSize));

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
    // todo handle
}

function onReceive(payload) {
    let message = JSON.parse(payload.body);

    if (message.type === "MoveResult") {
        onMoveResultReceived(message);
    }
}

function letterToCol(letter) {
    for (let i = 0; i < letters.length; ++i) {
        if (letter === letters[i]) {
            return i + 1;
        }
    }
}

function onMoveResultReceived(message) {
    if (message.actionResult === "InvalidMove" || message.actionResult === "PieceDoesNotExist") {
        showText("Invalid Move!");
    } else if (message.actionResult === "OutOfTurn") {
        showText("Not your turn!");
    } else if (message.actionResult === "InvalidMoveKingThreatened") {
        showText("Your king is in danger!");
    } else {
        let indicesToRemove = [];

        let fromX = letterToCol(message.from.substr(0, 1));
        let fromY = message.from.substr(1, 1);

        let toX = letterToCol(message.to.substr(0, 1));
        let toY = message.to.substr(1, 1);


        for (let pieceI = 0; pieceI < alivePieces.length; ++pieceI) {
            let piece = alivePieces[pieceI];

            if (fromX == piece.posCol && fromY == piece.posRow) {
                piece.posCol = toX;
                piece.posRow = toY;
            } else if (fromX === piece.posCol && fromY === piece.posRow) {
                indicesToRemove.push(pieceI);
            }
        }

        for (let toRemoveI = indicesToRemove.length - 1; toRemoveI >= 0; toRemoveI--) {
            alivePieces.splice(indicesToRemove[toRemoveI], 1);
        }

        if (message.actionResult === "Check") {
            showText("Check");
        }

        if (message.actionResult === "Checkmate") {
            showText("Checkmate");
        }
    }
}

function showText(text) {
    textToShow = text;
    textCountDownSecs = 2.0;
}
function getBackgroundColor(row, cell) {
    return row % 2 === 0 ?
        (cell % 2 === 0 ? "rgba(232, 235, 239, 1.0)" : "rgba(125, 135, 150, 1.0)") :
        (cell % 2 === 0 ? "rgba(125, 135, 150, 1.0)" : "rgba(232, 235, 239, 1.0)");
}
