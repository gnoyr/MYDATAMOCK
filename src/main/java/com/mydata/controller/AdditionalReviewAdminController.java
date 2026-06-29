package com.mydata.controller;

import com.mydata.dto.AdditionalReviewDetailDto;
import com.mydata.service.AdditionalReviewAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/review")
public class AdditionalReviewAdminController {

    private final AdditionalReviewAdminService adminService;

    public AdditionalReviewAdminController(AdditionalReviewAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("reviews", adminService.getPendingList());
        return "review/list";
    }

    @GetMapping("/{creditAppId}")
    public String detail(@PathVariable Long creditAppId, Model model) {
        AdditionalReviewDetailDto detail = adminService.getDetail(creditAppId);
        model.addAttribute("detail", detail);
        return "review/detail";
    }

    @PostMapping("/{creditAppId}")
    public String submit(@PathVariable Long creditAppId,
                         @RequestParam(defaultValue = "false") boolean incomeDocOk,
                         @RequestParam(defaultValue = "false") boolean assetDocOk,
                         @RequestParam(defaultValue = "false") boolean jobDocOk,
                         @RequestParam(required = false) Long estimatedMonthlyIncome,
                         RedirectAttributes redirectAttributes) {
        try {
            adminService.submitReview(creditAppId, incomeDocOk, assetDocOk, jobDocOk, estimatedMonthlyIncome);
            redirectAttributes.addFlashAttribute("successMsg", "심사 결과가 BNK 서버로 전송되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "오류: " + e.getMessage());
            return "redirect:/admin/review/" + creditAppId;
        }
        return "redirect:/admin/review";
    }
}
