package com.mcp.ochess.controller;

import com.mcp.ochess.dao.LobbyDao;
import com.mcp.ochess.dao.OChessUserDetailsService;
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
    OChessUserDetailsService userService;
    private PasswordEncoder passwordEncoder;
    private LobbyDao lobbyDao;

    @Autowired
    public void setLobbyDao(LobbyDao lobbyDao) {
        this.lobbyDao = lobbyDao;
    }

    @Autowired
    public void setUserService(OChessUserDetailsService userService) {
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
        model.addAttribute("lobbies", lobbyDao.listLobbies(limit, page, search));
        model.addAttribute("lobby", new Lobby());
        return "lobby";
    }

    @PostMapping("/lobby")
    public String createLobby(@Valid @ModelAttribute("Lobby") Lobby lobby, Model model, Errors errors, HttpSession session) throws OChessBaseException {
        initModel(model, session);
        lobbyDao.saveLobby(lobby);

        if (errors == null) {
            Game.createNewGame(lobby.getId());
            return "redirect:/ingame";
        }

        return "redirect:/lobby";
    }

    @PostMapping("/join")
    public String joinLobby(@ModelAttribute("Lobby") Lobby lobby, BindingResult result, Model model, Errors errors, HttpSession session) {
        initModel(model, session);

        lobby = lobbyDao.getLobby(lobby.getId());

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (lobby.getWhiteUser() == null) {
            lobby.setWhiteUser(user);
        } else if (lobby.getBlackUser() == null) {
            lobby.setBlackUser(user);
        } else {
            // Todo lobby full
        }

        lobbyDao.updateLobby(lobby);
        userService.getDao().updatePlayer(user.getPlayer());
        user.getPlayer().setInGameLobby(lobby);

        if (errors != null) {
            return "redirect:/ingame";
        }

        return "redirect:/lobby";
    }

    @GetMapping(value = "/profile")
    public String showProfile(Model model, HttpSession session) {
        initModel(model, session);
        model.addAttribute("activeTab", "profile");
        return "profile";
    }

    @RequestMapping("/help")
    public String help(Model model, HttpSession session) {
        initModel(model, session);
        model.addAttribute("activeTab", "help");
        return "help";
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
        return "redirect:/profile";
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
    public String home(Model model, HttpSession session) {
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
            userService.storeUser(user);
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

        m.addAttribute("User", user);
        m.addAttribute("LoggedPlayer", user.getPlayer());

        if (user.getPlayer().getInGameLobby() != null) {
            m.addAttribute("ingame", true);
            m.addAttribute("lobbyId", user.getPlayer().getInGameLobby().getId());
        }
    }
}
