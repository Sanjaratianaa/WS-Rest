package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.DateTransportRequest;
import com.transport.transport.api.dto.response.DateTransportResponse;
import com.transport.transport.api.entity.DateTransport;
import com.transport.transport.api.repository.DateTransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DateTransportService {

    private final DateTransportRepository repo;

    public List<DateTransportResponse> findAll() {
        return repo.findByActifTrue().stream().map(this::toResponse).toList();
    }

    public DateTransportResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("DateTransport introuvable")));
    }

    @Transactional
    public DateTransportResponse create(DateTransportRequest request) {
        DateTransport entity = DateTransport.builder()
                .dateJour(request.getDateJour())
                .build();
        return toResponse(repo.save(entity));
    }

    public DateTransportResponse toResponse(DateTransport d) {
        return DateTransportResponse.builder()
                .id(d.getId())
                .dateJour(d.getDateJour())
                .actif(d.getActif())
                .build();
    }
}
