const debug = false;

let holdingPiece = false;
let holdingPieceCoor = {
    x: -1, y: -1
};

const letters = ["A", "B", "C", "D", "E", "F", "G", "H"];

let mouseX;
let mouseY;
let gameSocket;
let whitePlaying = false;
let textCountDownSecs = 0.0;
let textToShow = "";

const imageHeight = 110;
const squareSize = 130;
const imageWidth = 63;

let board = new Board();

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

    let pieces = board.getPositions();

    for (let pieceI = 0; pieceI < pieces.length; ++pieceI) {
        let piece = pieces[pieceI];
        let pieceImg = document.getElementById(piece["image"]);

        let pieceX;
        let pieceY;

        if (holdingPiece && holdingPieceCoor.x + 1 === piece["posX"] && holdingPieceCoor.y === piece["posY"]) {
            pieceX = mouseX - (squareSize / 2) + 30;
            pieceY = mouseY - (squareSize / 2) + 5;
        } else {
            pieceX = (piece["posX"] - 1) * squareSize + 30;
            pieceY = (Math.abs(8 - piece["posY"])) * squareSize + 10;
        }

        ctx.drawImage(pieceImg, pieceX, pieceY, imageWidth, imageHeight);
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

    if (message.type === "NoOpponent") {
        showText("No opponent!");
    }
}

function onMoveResultReceived(message) {
    if (message.actionResult === "InvalidMove" || message.actionResult === "PieceDoesNotExist") {
        showText("Invalid Move!");
    } else if (message.actionResult === "OutOfTurn") {
        showText("Not your turn!");
    } else if (message.actionResult === "InvalidMoveKingThreatened") {
        showText("Your king is in danger!");
    }  else if (message.actionResult === "MovingOpponentPiece") {
        showText("That's not yours!");
    } else {
        board.move(message.from, message.to);

        if (message.kill != null) {
            board.kill(message.kill);
        }

        handleCastling(message);

        if (message.actionResult === "Check") {
            showText("Check");
        }

        if (message.actionResult === "Checkmate") {
            showText("Checkmate");
        }

        onNextTurn();
    }
}

function handleCastling(message) {
    if (message.actionResult === "CastlingMove") {

        if (message.to === "G1") {
            board.move("H1", "F1");
        }

        if (message.to === "C1") {
            board.move("A1", "D1");
        }

        if (message.to === "G8") {
            board.move("H8", "F8");
        }

        if (message.to === "C8") {
            board.move("A8", "D8");
        }
    }
}

function showText(text) {
    if (debug) {
        textToShow = text;
        textCountDownSecs = 2.0;
    }
}

function getBackgroundColor(row, cell) {
    return row % 2 === 0 ?
        (cell % 2 === 0 ? "rgba(232, 235, 239, 1.0)" : "rgba(125, 135, 150, 1.0)") :
        (cell % 2 === 0 ? "rgba(125, 135, 150, 1.0)" : "rgba(232, 235, 239, 1.0)");
}
