package com.mcp.ochess.controller;

import com.mcp.ochess.dao.LobbyService;
import com.mcp.ochess.dao.UserPlayerService;
import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.game.Game;
import com.mcp.ochess.model.Lobby;
import com.mcp.ochess.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/")
public class RootController {
    private PasswordEncoder passwordEncoder;
    private LobbyService lobbyService;
    private UserPlayerService userService;

    @Autowired
    public void setLobbyService(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @Autowired
    public void setUserService(UserPlayerService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String root() {
        return "redirect:/lobby";
    }

    @GetMapping("/lobby")
    public String listLobbies(WebRequest request, Model model, HttpSession session) {
        initModel(model, session);
        model.addAttribute("activeTab", "lobby");
        String pageStr = request.getParameter("page");
        String limitStr = request.getParameter("limit");
        String search = request.getParameter("search");

        if (pageStr == null || pageStr.equals("^[1-9]+")) {
            pageStr = "1";
        }

        if (limitStr == null || limitStr.equals("^[1-9]+")) {
            limitStr = "10";
        }

        int limit = Integer.parseInt(limitStr);
        int page = Integer.parseInt(pageStr);

        model.addAttribute("search", search);
        model.addAttribute("page", page);
        model.addAttribute("limit", limit);
        model.addAttribute("lobbies", lobbyService.listLobbies(search, limit, page));
        model.addAttribute("lobby", new Lobby());
        model.addAttribute("error", "");
        if (request.getParameter("error") != null) {
            int errorLobbyId = Integer.parseInt(request.getParameter("errorLobbyId"));
            model.addAttribute("errorLobbyId", errorLobbyId);
            model.addAttribute("error", request.getParameter("error"));
        }

        return "lobby";
    }

    @PostMapping("/lobby")
    public String createLobby(@Valid @ModelAttribute("Lobby") Lobby lobby, Model model, Errors errors, HttpSession session) throws OChessBaseException {
        initModel(model, session);
        lobbyService.newLobby(lobby);

        if (errors == null) {
            Game.createNewGame(lobby.getId());
            model.addAttribute("ingame", "true");
            return "redirect:/gamev2";
        }

        return "redirect:/gamev2";
    }

    @PostMapping("/join")
    public String joinLobby(@ModelAttribute("Lobby") Lobby lobby, BindingResult result, Model model, Errors errors, HttpSession session) {
        initModel(model, session);

        int joinId = lobby.getId();
        lobby = lobbyService.getLobby(joinId, lobby.getPassword());

        if (lobby == null) {
            model.addAttribute("error", "Invalid password!");
            model.addAttribute("errorLobbyId", joinId);
            return "redirect:/lobby";
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (lobby.getWhiteUser() == null) {
            lobby.setWhiteUser(user);
        } else if (lobby.getBlackUser() == null) {
            lobby.setBlackUser(user);
        } else {
            model.addAttribute("error", "Lobby full!");
            model.addAttribute("errorLobbyId", lobby.getId());
        }

        lobbyService.save(lobby);
        user.getPlayer().setInGameLobby(lobby);
        userService.updatePlayer(user.getPlayer());
        userService.updateUser(user);

        if (errors == null) {
            model.addAttribute("error", "");
        }

        return "redirect:/gamev2";
    }

    @RequestMapping("/accessdenied")
    public String accessdenied() {
        return "accessdenied";
    }

    @GetMapping("/login")
    public String login(Model m, HttpSession session) {
        if (isAuthenticated()) {
            return "redirect:/profile";
        }

        return "login";
    }

    @GetMapping("/loginsuccess")
    public String loginSuccess() {
        return "redirect:/gamev2";
    }

    @GetMapping("/gameover")
    public String gameOver() {
        return "redirect:/gamev2";
    }

    @GetMapping("/gamev2")
    public String gameV2(WebRequest request, Model model, HttpSession session) {
        initModel(model, session);

        Object inGame = model.getAttribute("ingame");
        if (inGame == null || ! (Boolean) inGame) {
            listLobbies(request, model, session);
        }

        return "gamev2";
    }

    @PostMapping(value = "/login")
    public String login(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Errors errors,
                        Model model, HttpSession session) {
        if (! bindingResult.hasErrors()) {
            userService.loadUserByUsername(user.getName());
        } else {
            model.addAttribute("error", "User and/or password is incorrect!");
        }

        return "login";
    }

    @GetMapping(value = "/register")
    public String register(WebRequest request, Model model, HttpSession session) {
        if (isAuthenticated()) {
            return "redirect:/profile";
        }
        User u = new User();
        model.addAttribute("user", u);
        return "register";
    }


    @RequestMapping("/ingame")
    public String ingame(Model model, HttpSession session) {
        initModel(model, session);
        SecurityContextHolder.getContext().getAuthentication().getCredentials();
        model.addAttribute("activeTab", "ingame");
        return "game";
    }

    @PostMapping(value = "/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, WebRequest request,
                           Errors errors, HttpSession session) {
        if (! user.getMatchingPassword().equals(user.getPassword())) {
            errors.rejectValue("password", "400", "Passwords must match!");
        } else if (! errors.hasErrors()) {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            userService.newUser(user);
        }

        return "register";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    public void initModel(Model m, HttpSession session) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.loadUserByUsername(user.getName()); // refresh to get updated lobby

        m.addAttribute("User", user);
        m.addAttribute("LoggedPlayer", user.getPlayer());

        if (user.getPlayer().getInGameLobby() != null) {
            m.addAttribute("ingame", true);
            m.addAttribute("lobbyId", user.getPlayer().getInGameLobby().getId());
            m.addAttribute("lobby", user.getPlayer().getInGameLobby());

            int whiteId = user.getPlayer().getInGameLobby().getWhiteUser().getId();

            m.addAttribute("side", whiteId == user.getId() ? "wh" : "bl");
        }
    }
}
