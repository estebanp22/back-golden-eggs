package com.goldeneggs.Egg;

import java.util.List;

/**
 * Service interface for Egg management.
 */
public interface EggService {
    List<Egg> getAll();
    Egg get(Long id);
    Egg save(Egg egg);
    Egg update(Long id, Egg updatedEgg);
    void delete(Long id);
}
