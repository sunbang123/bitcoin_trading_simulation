package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.asset.response.TotalAssetResponseDto;
import org.example.backend.service.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;

    @GetMapping("/me")
    public ResponseEntity<TotalAssetResponseDto> getMyAssets() {
        TotalAssetResponseDto response = assetService.getMyAssets();
        return ResponseEntity.ok(response);
    }

}