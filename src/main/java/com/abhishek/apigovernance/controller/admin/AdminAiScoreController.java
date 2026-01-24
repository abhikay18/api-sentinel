package com.abhishek.apigovernance.controller.admin;

import com.abhishek.apigovernance.repository.AiScoreEventRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/ai")
public class AdminAiScoreController {

    private final AiScoreEventRepository repository;

    public AdminAiScoreController(AiScoreEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/scores/{apiKey}")
    public List<Map<String, Object>> scores(@PathVariable String apiKey) {

        return repository
                .findTop50ByApiKeyValueOrderByCreatedAtDesc(apiKey)
                .stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("time", e.getCreatedAt().toString());
                    m.put("score", e.getScore());
                    return m;
                })
                .toList();
    }

}
