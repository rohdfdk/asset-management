package org.example.assetmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.example.assetmanagement.dto.LoanRequest;
import org.example.assetmanagement.entity.AssetStatus;
import org.example.assetmanagement.service.AssetService;
import org.example.assetmanagement.service.LoanService;
import org.example.assetmanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    public String loans(Model model, Authentication authentication) {
        boolean admin = hasRole(authentication);

        if (admin) {
            model.addAttribute("loans", loanService.findAll());
        } else {
            model.addAttribute("loans", loanService.findByUsername(authentication.getName()));
        }

        return "loans/list";
    }

    private boolean hasRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    @GetMapping("/loans-view/new")
    public String newLoan(Model model, Authentication authentication) {
        boolean admin = hasRole(authentication);

        model.addAttribute("loanRequest", new LoanRequest());
        model.addAttribute("assets", assetService.findByStatus(AssetStatus.AVAILABLE));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("admin", admin);

        return "loans/new";
    }
}