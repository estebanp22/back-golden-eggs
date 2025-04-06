package com.goldeneggs.Egg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of EggService interface.
 */
@Service
public class EggServiceImpl implements EggService {

    @Autowired
    private EggRepository eggRepository;

    @Override
    public List<Egg> getAll() {
        return eggRepository.findAll();
    }

    @Override
    public Egg get(Long id) {
        return eggRepository.findById(id).orElse(null);
    }

    @Override
    public Egg save(Egg egg) {
        return eggRepository.save(egg);
    }

    @Override
    public Egg update(Long id, Egg updatedEgg) {
        Egg existing = get(id);
        if (existing == null) return null;

        existing.setType(updatedEgg.getType());
        existing.setColor(updatedEgg.getColor());
        existing.setExpirationDate(updatedEgg.getExpirationDate());
        existing.setCategory(updatedEgg.getCategory());

        return eggRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        eggRepository.deleteById(id);
    }
}
