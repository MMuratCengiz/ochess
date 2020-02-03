class Board {
    constructor() {
        this.layout = {
            A1: { image: "wh_rook",     posX: 1, posY: 1 },
            B1: { image: "wh_knight",   posX: 2, posY: 1 },
            C1: { image: "wh_bishop",   posX: 3, posY: 1 },
            D1: { image: "wh_queen",    posX: 4, posY: 1 },
            E1: { image: "wh_king",     posX: 5, posY: 1 },
            F1: { image: "wh_bishop",   posX: 6, posY: 1 },
            G1: { image: "wh_knight",   posX: 7, posY: 1 },
            H1: { image: "wh_rook",     posX: 8, posY: 1 },

            A2: { image: "wh_pawn",     posX: 1, posY: 2 },
            B2: { image: "wh_pawn",     posX: 2, posY: 2 },
            C2: { image: "wh_pawn",     posX: 3, posY: 2 },
            D2: { image: "wh_pawn",     posX: 4, posY: 2 },
            E2: { image: "wh_pawn",     posX: 5, posY: 2 },
            F2: { image: "wh_pawn",     posX: 6, posY: 2 },
            G2: { image: "wh_pawn",     posX: 7, posY: 2 },
            H2: { image: "wh_pawn",     posX: 8, posY: 2 },

            A7: { image: "bl_pawn",     posX: 1, posY: 7 },
            B7: { image: "bl_pawn",     posX: 2, posY: 7 },
            C7: { image: "bl_pawn",     posX: 3, posY: 7 },
            D7: { image: "bl_pawn",     posX: 4, posY: 7 },
            E7: { image: "bl_pawn",     posX: 5, posY: 7 },
            F7: { image: "bl_pawn",     posX: 6, posY: 7 },
            G7: { image: "bl_pawn",     posX: 7, posY: 7 },
            H7: { image: "bl_pawn",     posX: 8, posY: 7 },

            A8: { image: "bl_rook",     posX: 1, posY: 8 },
            B8: { image: "bl_knight",   posX: 2, posY: 8 },
            C8: { image: "bl_bishop",   posX: 3, posY: 8 },
            D8: { image: "bl_queen",    posX: 4, posY: 8 },
            E8: { image: "bl_king",     posX: 5, posY: 8 },
            F8: { image: "bl_bishop",   posX: 6, posY: 8 },
            G8: { image: "bl_knight",   posX: 7, posY: 8 },
            H8: { image: "bl_rook",     posX: 8, posY: 8 }
        };
    }

    load(marshalledBoard) {
        this.layout = {}; // Reset layout

        let mbc = Array.from(marshalledBoard);
        for (let index = 0; index < mbc.length; index += 4) {
            let pos = mbc[index] + mbc[index + 1];
            let piece = {};

            piece.posX = this.letterToCol(mbc[index]);
            piece.posY = parseInt(mbc[index + 1]  + "", 10);

            let side = mbc[index + 2] == 'B' ? "bl" : "wh";

            switch (mbc[index + 3]) {
                case 'P':
                    piece.image = side + "_" + "pawn";
                    break;
                case 'Q':
                    piece.image = side + "_" + "queen";
                    break;
                case 'X':
                    piece.image = side + "_" + "king";
                    break;
                case 'R':
                    piece.image = side + "_" + "rook";
                    break;
                case 'B':
                    piece.image = side + "_" + "bishop";
                    break;
                case 'K':
                    piece.image = side + "_" + "knight";
                    break;
            }

            this.layout[pos] = piece;
        }
    }

    move(from, to) {
        this.layout[to] = this.layout[from];
        this.layout[to] = this.layout[from];

        this.layout[to].posX = this.letterToCol(to.substr(0, 1));
        this.layout[to].posY = to.substr(1, 1);

        delete this.layout[from];
    }

    transform(pos, toPiece) {
        this.layout[pos].image = this.layout[pos].image.substr(0, 3) + toPiece.toLowerCase();
    }

    kill(pos) {
        delete this.layout[pos];
    }

    getPositions() {
        let result = [];

        Object.entries(this.layout).forEach(([key, value]) => {
            result.push(value);
        });

        return result;
    }

    letterToCol(letter) {
        for (let i = 0; i < letters.length; ++i) {
            if (letter === letters[i]) {
                return i + 1;
            }
        }
    }
}