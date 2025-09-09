package com.hms.admin;

import com.hms.user.Role;
import com.hms.user.UserAccount;
import com.hms.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserManagementController(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", users.findAll());
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new UserAccount());
        model.addAttribute("roles", Role.values());
        return "admin/users/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<UserAccount> u = users.findById(id);
        if (u.isEmpty()) {
            ra.addFlashAttribute("error", "User not found");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", u.get());
        model.addAttribute("roles", Role.values());
        return "admin/users/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("user") UserAccount user, BindingResult result, 
                      @RequestParam(required = false) String newPassword, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/users/form";
        }
        
        // Handle password
        if (user.getId() == null) { // New user
            if (newPassword == null || newPassword.trim().isEmpty()) {
                newPassword = "temp123"; // Default password
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        } else if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        
        users.save(user);
        ra.addFlashAttribute("success", "User saved successfully");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        if (users.existsById(id)) {
            users.deleteById(id);
            ra.addFlashAttribute("success", "User deleted");
        } else {
            ra.addFlashAttribute("error", "User not found");
        }
        return "redirect:/admin/users";
    }
}
