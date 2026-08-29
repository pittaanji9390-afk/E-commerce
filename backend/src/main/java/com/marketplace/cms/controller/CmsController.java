package com.marketplace.cms.controller;

import com.marketplace.cms.domain.Banner;
import com.marketplace.cms.repository.BannerRepository;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "CMS & Marketing Banners", description = "Endpoints for storefront hero banners and content promotions")
@RestController
@RequestMapping("/api/v1/cms")
@RequiredArgsConstructor
public class CmsController {

    private final BannerRepository bannerRepository;

    @Operation(summary = "Get active storefront hero banners")
    @GetMapping("/banners")
    public ResponseEntity<Result<List<Banner>>> getActiveBanners() {
        List<Banner> banners = bannerRepository.findByActiveTrueOrderByDisplayOrderAsc();
        return ResponseEntity.ok(Result.ok(banners));
    }
}
