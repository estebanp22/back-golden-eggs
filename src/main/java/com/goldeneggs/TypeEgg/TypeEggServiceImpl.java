package com.goldeneggs.TypeEgg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing egg types.
 */
@Service
public class TypeEggServiceImpl implements TypeEggService {

    @Autowired
    private TypeEggRepository typeEggRepository;

    @Override
    public TypeEgg save(TypeEgg typeEgg) {
        return typeEggRepository.save(typeEgg);
    }

    @Override
    public List<TypeEgg> getAll() {
        return typeEggRepository.findAll();
    }

    @Override
    public TypeEgg getById(Long id) {
        Optional<TypeEgg> optional = typeEggRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public void delete(Long id) {
        typeEggRepository.deleteById(id);
    }
}

