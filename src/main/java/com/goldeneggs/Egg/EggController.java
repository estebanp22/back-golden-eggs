package com.goldeneggs.Egg;

import com.goldeneggs.Exception.InvalidEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing eggs.
 */
@RestController
@RequestMapping("/api/v1/eggs")
@CrossOrigin("*")
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
    public ResponseEntity<?> save(@RequestBody Egg egg) {
        try{
            Egg saved =  eggService.save(egg);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }catch (InvalidEggDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets an egg by its ID.
     *
     * @param id Egg ID.
     * @return Egg entity.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Egg> get(@PathVariable Long id) {
        try{
            return  ResponseEntity.ok(eggService.get(id));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Egg egg) {
        try{
            Egg updated = eggService.update(id, egg);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (InvalidEggDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes an egg by ID.
     *
     * @param id Egg ID.
     * @return HTTP 200 if deleted.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            eggService.delete(id);
            return ResponseEntity.ok().build();
        }catch (ResourceNotFoundException e){
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Retrieves the total quantity of eggs across all records in the system.
     *
     * @return A {@code ResponseEntity} containing the total number of eggs as a {@code Long}.
     */
    @GetMapping("/totalQuantity")
    public ResponseEntity<Long> getTotalEggQuantity() {
        Long total = eggService.getTotalEggQuantity();
        return ResponseEntity.ok(total);
    }

}
