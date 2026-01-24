package com.abhishek.apigovernance.controller.admin;

import com.abhishek.apigovernance.abuse.AbuseDetectionService;
import com.abhishek.apigovernance.rate_limit.RateLimiterService;
import com.abhishek.apigovernance.repository.BlockedEntityRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/abuse")
public class AbuseAdminController {

    private final BlockedEntityRepository repository;
    private final RedisTemplate<String, String> redisTemplate;
    private final AbuseDetectionService abuseDetectionService;
    private final RateLimiterService rateLimiterService;

    public AbuseAdminController(
            BlockedEntityRepository repository,
            RedisTemplate<String, String> redisTemplate,
            AbuseDetectionService abuseDetectionService,
            RateLimiterService rateLimiterService
    ) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.abuseDetectionService = abuseDetectionService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute(
                "blockedList",
                repository.findAll()
                        .stream()
                        .sorted((a, b) -> b.getBlockedUntil().compareTo(a.getBlockedUntil()))
                        .toList()
        );
        return "abuse";
    }

    @PostMapping("/{id}/unblock")
    public String unblock(@PathVariable Long id) {

        repository.findById(id)
                .map(entity -> entity.getValue())
                .ifPresent(abuseDetectionService::fullyUnblock);

        return "redirect:/admin/abuse";
    }

}

