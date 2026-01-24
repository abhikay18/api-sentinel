package com.abhishek.apigovernance.controller.admin;

import com.abhishek.apigovernance.domain.RateLimitPolicy;
import com.abhishek.apigovernance.repository.RateLimitPolicyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/rate-limits")
public class RateLimitAdminController {

    private final RateLimitPolicyRepository repository;

    public RateLimitAdminController(RateLimitPolicyRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("policies", repository.findAll());
        return "rate-limits";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam int maxRequests,
                         @RequestParam int windowSeconds) {

        RateLimitPolicy policy = repository.findById(id)
                .orElseThrow();

        policy.setMaxRequests(maxRequests);
        policy.setWindowSeconds(windowSeconds);
        repository.save(policy);

        return "redirect:/admin/rate-limits";
    }
}
