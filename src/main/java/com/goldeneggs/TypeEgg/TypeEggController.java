package com.goldeneggs.TypeEgg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing egg types.
 */
@RestController
@RequestMapping("/api/v1/type-egg")
@CrossOrigin("*")
public class TypeEggController {

    @Autowired
    private TypeEggService typeEggService;

    /**
     * Saves or updates an egg type.
     *
     * @param typeEgg the type of egg to save
     * @return the saved TypeEgg
     */
    @PostMapping("/save")
    public TypeEgg save(@RequestBody TypeEgg typeEgg) {
        return typeEggService.save(typeEgg);
    }

    /**
     * Retrieves all egg types.
     *
     * @return list of all TypeEgg entries
     */
    @GetMapping("/getAll")
    public List<TypeEgg> getAll() {
        return typeEggService.getAll();
    }

    /**
     * Retrieves an egg type by ID.
     *
     * @param id the ID of the egg type
     * @return the corresponding TypeEgg
     */
    @GetMapping("/get/{id}")
    public TypeEgg getById(@PathVariable Long id) {
        return typeEggService.getById(id);
    }

    /**
     * Deletes an egg type by ID.
     *
     * @param id the ID of the egg type to delete
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        typeEggService.delete(id);
    }
}
