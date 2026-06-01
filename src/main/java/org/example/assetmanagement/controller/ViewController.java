package org.example.assetmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.example.assetmanagement.dto.LoanRequest;
import org.example.assetmanagement.service.AssetService;
import org.example.assetmanagement.service.LoanService;
import org.example.assetmanagement.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final AssetService assetService;
    private final UserService userService;
    private final LoanService loanService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("assets", assetService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("loans", loanService.findAll());
        model.addAttribute("activeLoans", loanService.findActiveLoans());
        model.addAttribute("overdueLoans", loanService.findOverdueLoans());
        return "dashboard";
    }

    @GetMapping("/assets-view")
    public String assets(Model model) {
        model.addAttribute("assets", assetService.findAll());
        return "assets/list";
    }

    @GetMapping("/users-view")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/loans-view")
    public String loans(Model model) {
        model.addAttribute("loans", loanService.findAll());
        return "loans/list";
    }

    @GetMapping("/loans-view/new")
    public String newLoan(Model model) {
        model.addAttribute("loanRequest", new LoanRequest());
        model.addAttribute("assets", assetService.findByStatus("AVAILABLE"));
        model.addAttribute("users", userService.findAll());
        return "loans/new";
    }
}