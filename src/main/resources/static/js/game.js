let holdingPiece = false;
let holdingPieceCoor = {
    x: -1, y: -1
};

let alivePieces = [
    { side: "wh", kind: "pawn", posCol: 1, posRow: 2 },
    { side: "wh", kind: "pawn", posCol: 2, posRow: 2 },
    { side: "wh", kind: "pawn", posCol: 3, posRow: 2 },
    { side: "wh", kind: "pawn", posCol: 4, posRow: 2 },
    { side: "wh", kind: "pawn", posCol: 5, posRow: 2 },
    { side: "wh", kind: "pawn", posCol: 6, posRow: 2 },
    { side: "wh", kind: "pawn", posCol: 7, posRow: 2 },
    { side: "wh", kind: "pawn", posCol: 8, posRow: 2 },

    { side: "bl", kind: "pawn", posCol: 1, posRow: 7 },
    { side: "bl", kind: "pawn", posCol: 2, posRow: 7 },
    { side: "bl", kind: "pawn", posCol: 3, posRow: 7 },
    { side: "bl", kind: "pawn", posCol: 4, posRow: 7 },
    { side: "bl", kind: "pawn", posCol: 5, posRow: 7 },
    { side: "bl", kind: "pawn", posCol: 6, posRow: 7 },
    { side: "bl", kind: "pawn", posCol: 7, posRow: 7 },
    { side: "bl", kind: "pawn", posCol: 8, posRow: 7 },
];

let mouseX;
let mouseY;
let letters = [ "A", "B", "C", "D", "E", "F", "G", "H" ];

function update(ctx) {
    for (let row = 0; row < 8; ++row) {
        for (let cell = 0; cell < 8; ++cell) {
            if (Math.floor(mouseX / 100) === cell && Math.floor(mouseY / 100) === row) {
                ctx.fillStyle = "rgba(20,20,20,0.5)";
            } else {
                ctx.fillStyle = getBackgroundColor(row, cell);
            }

            ctx.fillRect(100 * cell, 100 * row, 100, 100);
        }
    }

    for (let pieceI = 0; pieceI < alivePieces.length; ++pieceI) {
        let piece = alivePieces[pieceI];
        let pieceImg = document.getElementById(piece.side + "_" + piece.kind);

        if (holdingPiece && holdingPieceCoor.x + 1 === piece.posCol && holdingPieceCoor.y === piece.posRow) {
            ctx.drawImage(pieceImg, mouseX - 50, mouseY - 50, 100, 100)
        } else {
            ctx.drawImage(pieceImg, (piece.posCol - 1) * 100 - 5, (Math.abs(8 - piece.posRow)) * 100, 100, 100)
        }
    }
}

function onLoad() {
    let canvas = document.getElementById("board");
    let ctx = canvas.getContext("2d");

    canvas.onmousemove = function(e){
        mouseX = e.offsetX;
        mouseY = e.offsetY;
    };

    canvas.onmousedown = function(e){
        holdingPiece = true;
        holdingPieceCoor.x = Math.floor(e.offsetX / 100);
        holdingPieceCoor.y = Math.abs(8 - Math.floor(e.offsetY / 100));
    };

    canvas.onmouseup = function(e){
        holdingPiece = false;

        let posCol = Math.floor(e.offsetX / 100) + 1;
        let posRow = Math.abs(8 - Math.floor(e.offsetY / 100));

        let http = new XMLHttpRequest();
        http.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                alert(http.responseText);
            }
        };

        let from = letters[holdingPieceCoor.x] + "" + holdingPieceCoor.y;
        let to = letters[posCol - 1] + "" + posRow;

        let request = "{ \"from\": \"" +from+ "\", \"to\": \"" + to + "\" }";
        alert(request);

        // http.open("POST", "/ochess/play/lobby/${lobbyid}");
        // http.send(request);

        for (let pieceI = 0; pieceI < alivePieces.length; ++pieceI) {
            let piece = alivePieces[pieceI];

            if (holdingPieceCoor.x + 1 === piece.posCol && holdingPieceCoor.y === piece.posRow) {
                piece.posCol = posCol;
                piece.posRow = posRow;
                break;
            }
        }
    };

    setInterval(function () {
        update(ctx);
    }, 10);
}

function getBackgroundColor(row, cell) {
    return row % 2 === 0 ? (cell % 2 === 0 ? "#FFFFFF" : "#1440a9") : (cell % 2 === 0 ? "#1440a9" : "#FFFFFF");
}
