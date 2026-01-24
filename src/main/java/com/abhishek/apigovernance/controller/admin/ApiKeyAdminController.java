package com.abhishek.apigovernance.controller.admin;

import com.abhishek.apigovernance.domain.ApiPlan;
import com.abhishek.apigovernance.service.ApiKeyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/api-keys")
public class ApiKeyAdminController {

    private final ApiKeyService apiKeyService;

    public ApiKeyAdminController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("keys", apiKeyService.findAll());
        model.addAttribute("plans", ApiPlan.values());
        return "api-keys";
    }

    @PostMapping
    public String create(@RequestParam ApiPlan plan) {
        apiKeyService.create(plan);
        return "redirect:/admin/api-keys";
    }

    @PostMapping("/{id}/revoke")
    public String revoke(@PathVariable Long id) {
        apiKeyService.revoke(id);
        return "redirect:/admin/api-keys";
    }
}
