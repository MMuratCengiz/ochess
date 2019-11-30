isACellActive = false;
activeRow = -1;
activeCell = -1;

function onLoad() {
    for (row = 0; row < 8; ++row) {
        for (cell = 0; cell < 8; ++cell) {
            element = document.getElementById("cell_" + row + "_" + cell);
            element.addEventListener("mouseover", cellHover);
            element.addEventListener("mouseleave", cellExit);
        }
    }
}

function cellHover(ev) {
    if (isACellActive) {
        ev.target.style.outline = "7px solid green";
    } else {
        ev.target.style.outline = "7px solid lightblue";
    }
}

function getBackgroundColor(row, cell) {
    return row % 2 === 0 ? (cell % 2 === 0 ? "white" : "lightbrown") : (cell % 2 === 0 ? "lightbrown" : "white");
}

function cellExit(ev) {
    id = ev.target.id;
    idRow  = id.substring(5, 6);
    idCell = id.substring(7, 8);

    ev.target.style.outline = "0";
}


function selectCell(row, cell) {
    if (isACellActive) {
        var letters = [ "A", "B", "C", "D", "E", "F", "G", "H" ];
        var from = letters[activeCell] + activeRow;
        var to   = letters[cell] + row;

        element = document.getElementById("cell_" + activeRow + "_" + activeCell);
        element.style.outline = "0";

        activeRow = -1;
        activeCell = -1;
        element.addEventListener("mouseover", cellHover);
        element.addEventListener("mouseleave", cellExit);
        // Back to first step again
        element = document.getElementById("cell_" + row + "_" + cell);
        element.style.outline = "0";

        alert(lobbyId);

        var http = new XMLHttpRequest();
        http.open("POST", "/ochess/play/lobby/${lobbyid}");
        http.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE)
            alert(http.responseText);
        };

        http.send("{ \"from\": \"" +from+ "\", \"to\": \"" + to + "\" }");
    } else {
        activeRow = row;
        activeCell = cell;
        element = document.getElementById("cell_" + row + "_" + cell);
        element.style.outline = "7px solid lightblue";
        element.removeEventListener("mouseover", cellHover);
        element.removeEventListener("mouseleave", cellExit);
    }

    isACellActive = !isACellActive;
}