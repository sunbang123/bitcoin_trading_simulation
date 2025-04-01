package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Fill;
import org.example.backend.repository.FillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FillService {

    private final FillRepository fillRepository;

    public List<Fill> getUserFills(Long userId) {
        return fillRepository.findByUserId(userId);
    }

    public Fill save(Fill fill) {
        return fillRepository.save(fill);
    }
}
