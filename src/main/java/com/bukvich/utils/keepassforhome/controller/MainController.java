package com.bukvich.utils.keepassforhome.controller;

import com.bukvich.utils.keepassforhome.dto.CredentialsDto;
import com.bukvich.utils.keepassforhome.service.KdbxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final KdbxService kdbxService;

    @GetMapping("/")
    public String showIndexPage(Model model) {
        model.addAttribute("dbList", kdbxService.showDbList());
        model.addAttribute("credentials", new CredentialsDto());
        return "index";
    }

    @PostMapping("/open")
    public String openDb(Model model, CredentialsDto credentials) {
        try {
            kdbxService.openDb(credentials.getName(), credentials.getPassword());
        } catch (Exception ex) {
            log.error("An error was occurred while opening the DB: ", ex);
            model.addAttribute(
                    "exception",
                    ex.getMessage());
            return "exception";
        }
        model.addAttribute("credentials", credentials);
        model.addAttribute("entityName", "");
        return "search";
    }
}
