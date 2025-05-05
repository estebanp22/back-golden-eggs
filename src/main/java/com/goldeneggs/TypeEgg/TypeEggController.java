package com.goldeneggs.TypeEgg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @return ResponseEntity containing the saved TypeEgg and HTTP status
     */
    @PostMapping("/save")
    public ResponseEntity<TypeEgg> save(@RequestBody TypeEgg typeEgg) {
        TypeEgg savedTypeEgg = typeEggService.save(typeEgg);
        return new ResponseEntity<>(savedTypeEgg, HttpStatus.CREATED);
    }

    /**
     * Retrieves all egg types.
     *
     * @return ResponseEntity containing a list of all TypeEgg entries and HTTP status
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<TypeEgg>> getAll() {
        List<TypeEgg> typeEggs = typeEggService.getAll();
        return new ResponseEntity<>(typeEggs, HttpStatus.OK);
    }

    /**
     * Retrieves an egg type by ID.
     *
     * @param id the ID of the egg type
     * @return ResponseEntity containing the corresponding TypeEgg and HTTP status
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<TypeEgg> getById(@PathVariable Long id) {
        TypeEgg typeEgg = typeEggService.getById(id);
        return new ResponseEntity<>(typeEgg, HttpStatus.OK);
    }

    /**
     * Deletes an egg type by ID.
     *
     * @param id the ID of the egg type to delete
     * @return ResponseEntity with HTTP status indicating success
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        typeEggService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
