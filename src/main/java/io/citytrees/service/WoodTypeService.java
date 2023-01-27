package io.citytrees.service;

import io.citytrees.model.WoodType;
import io.citytrees.repository.WoodTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WoodTypeService {

    private final WoodTypeRepository repository;
    private final SecurityService securityService;

    public UUID create(String name) {
        return repository.create(UUID.randomUUID(), name, securityService.getCurrentUserId());
    }

    public List<WoodType> getAllWoodTypes() {
        return repository.findAll();
    }

    @Cacheable("woodTypeByIdCache")
    public WoodType getById(UUID woodTypeId) {
        return repository.findById(woodTypeId).orElseThrow();
    }
}
