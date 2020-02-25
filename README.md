# OChess

Fully fledged multiplayer chess game, supports the almost-full feature set of Chess, supports a lobby and a stat system. Built with the following tools:

- Spring boot(Included components: WebMVC, Hibernate, Security and WebSockets).
- Plain Javascript
- Plain CSS

# Application Flow

## Login

User authentication system is implemented to keep track of the users rank, this mechanism is also used to ensure the authentication of the move messages sent to the web socket, the login screen:

![Login Screen](/readme/login.png)

## Lobby Screen

Game searches are done through a lobby system, anyone can create a lobby(with or without a password), once a user joins a lobby, he/she needs to play the game to the end or surrender to be able to join another game, the lobby screen:

![Lobby Screen](/readme/lobby.png)

## Game Screen

Once a game is joined, the user is automatically redirected to the game screen, the game starts once both players join:

![In Game Screen](/readme/in_game.png)

## Game Moves

Communication is done through spring frameworks messaging template, the game is played completely real-time and features a chat-box for players to communicate as-well.

![Move](/readme/move.gif)
