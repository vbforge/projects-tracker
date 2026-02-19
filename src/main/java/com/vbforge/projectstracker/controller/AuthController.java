package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.dto.RegisterDTO;
import com.vbforge.projectstracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    //login
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Authentication authentication,
            Model model) {

        // Already logged in → go to dashboard
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/projects";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }

        return "auth/login";
    }

    //register
    @GetMapping("/register")
    public String registerPage(Authentication authentication, Model model) {
        // Already logged in → go to dashboard
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/projects";
        }
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerDTO") RegisterDTO dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Bean validation errors (blank fields, wrong email format, etc.)
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // Passwords match check
        if (!dto.passwordsMatch()) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "auth/register";
        }

        try {
            userService.register(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Account created successfully! Please log in.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Username/email already taken
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

}
