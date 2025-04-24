package com.goldeneggs.Egg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing eggs.
 */
@RestController
@RequestMapping("/api/v1/egg")
public class EggController {

    @Autowired
    private EggService eggService;

    /**
     * Saves a new egg.
     *
     * @param egg Egg information.
     * @return Saved egg entity.
     */
    @PostMapping("/save")
    public ResponseEntity<Egg> save(@RequestBody Egg egg) {
        return ResponseEntity.ok(eggService.save(egg));
    }

    /**
     * Gets an egg by its ID.
     *
     * @param id Egg ID.
     * @return Egg entity.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Egg> get(@PathVariable Long id) {
        return ResponseEntity.ok(eggService.get(id));
    }

    /**
     * Gets all eggs.
     *
     * @return List of eggs.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Egg>> getAll() {
        return ResponseEntity.ok(eggService.getAll());
    }

    /**
     * Updates an existing egg.
     *
     * @param id ID of the egg to update.
     * @param egg Updated egg data.
     * @return Updated egg entity.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Egg> update(@PathVariable Long id, @RequestBody Egg egg) {
        return ResponseEntity.ok(eggService.update(id, egg));
    }

    /**
     * Deletes an egg by ID.
     *
     * @param id Egg ID.
     * @return HTTP 200 if deleted.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eggService.delete(id);
        return ResponseEntity.ok().build();
    }
}
