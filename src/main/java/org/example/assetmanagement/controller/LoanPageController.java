package org.example.assetmanagement.controller;

import jakarta.validation.Valid;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/loans-view")
public class LoanPageController {

    private final LoanService loanService;
    private final AssetService assetService;
    private final UserService userService;

    @PostMapping
    public String createLoan(
            @Valid @ModelAttribute LoanRequest request,
            BindingResult bindingResult,
            Model model,
            Authentication authentication
    ) {
        boolean admin = hasRole(authentication);

        if (bindingResult.hasErrors()) {
            model.addAttribute("assets", assetService.findByStatus(AssetStatus.AVAILABLE));
            model.addAttribute("users", userService.findAll());
            model.addAttribute("admin", admin);
            return "loans/new";
        }

        loanService.createLoanAsUser(request, authentication.getName(), admin);
        return "redirect:/loans-view";
    }

    @PostMapping("/{id}/return")
    public String returnLoan(@PathVariable Long id, Authentication authentication) {
        boolean admin = hasRole(authentication);
        loanService.returnLoanAsUser(id, authentication.getName(), admin);
        return "redirect:/loans-view";
    }

    private boolean hasRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}