package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.ExtendedAdDto;

import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdController {

    @GetMapping
    public ResponseEntity<List<AdDto>> getAllAds() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAdDto> getAds(@PathVariable Long id) {
        return ResponseEntity.ok(new ExtendedAdDto());
    }

    @PostMapping
    public ResponseEntity<AdDto> addAd(@RequestPart("properties") String properties,
                                       @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(new AdDto());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdDto> updateAds(@PathVariable Long id, @RequestBody AdDto adDto) {
        return ResponseEntity.ok(new AdDto());
    }

    @PatchMapping("/{id}/image")
    public ResponseEntity<byte[]> updateImage(@PathVariable Integer id,
                                              @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(new byte[0]);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}