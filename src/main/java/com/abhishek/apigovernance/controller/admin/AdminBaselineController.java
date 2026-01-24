package com.abhishek.apigovernance.controller.admin;

import com.abhishek.apigovernance.abuse.AbuseDetectionService;
import com.abhishek.apigovernance.ai.BaselineLearningService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/baseline")
public class AdminBaselineController {

    private final BaselineLearningService baselineService;
    private final AbuseDetectionService abuseDetectionService;

    public AdminBaselineController(
            BaselineLearningService baselineService,
            AbuseDetectionService abuseDetectionService
    ) {
        this.baselineService = baselineService;
        this.abuseDetectionService = abuseDetectionService;
    }

    @PostMapping("/{apiKey}/reset")
    public String reset(@PathVariable String apiKey) {

        baselineService.resetBaseline(apiKey);
        abuseDetectionService.clearBaselineRetrain(apiKey);

        return "redirect:/admin/abuse";
    }

    @PostMapping("/{apiKey}/retrain")
    public String retrain(@PathVariable String apiKey) {

        baselineService.resetBaseline(apiKey);

        // Disable AI enforcement temporarily
        abuseDetectionService.startBaselineRetrain(apiKey, 15);

        return "redirect:/admin/abuse";
    }
}
