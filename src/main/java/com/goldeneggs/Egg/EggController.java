package com.goldeneggs.Egg;

import com.goldeneggs.Dto.Egg.EggSummaryDto;
import com.goldeneggs.Exception.InvalidEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @PostMapping("/save/{id}")
    public ResponseEntity<?> save(
            @PathVariable Long id,
            @RequestBody Egg egg) {
        try{
            Egg saved =  eggService.save(egg, id);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }catch (InvalidEggDataException e){
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gets an egg by its ID.
     *
     * @param id Egg ID.
     * @return Egg entity.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try{
            return  ResponseEntity.ok(eggService.get(id));
        }catch (ResourceNotFoundException e){
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
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
     * Gets all eggs.
     *
     * @return List of eggs.
     */
    @GetMapping("/getAllEggDto")
    public ResponseEntity<List<EggSummaryDto>> getAllEggDto() {
        return ResponseEntity.ok(eggService.findEggSummaries());
    }

    /**
     * Updates an existing egg.
     *
     * @param id ID of the egg to update.
     * @param egg Updated egg data.
     * @return Updated egg entity.
     */
    @PutMapping("/update/{id}/{idUser}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Egg egg,
            @PathVariable Long idUser) {
        try{
            Egg updated = eggService.update(id, egg, idUser);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }catch (ResourceNotFoundException e){
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (InvalidEggDataException e){
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes an egg by ID.
     *
     * @param id Egg ID.
     * @return HTTP 200 if deleted.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            eggService.delete(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidEggDataException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
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

    /**
     * This method help to send a message error
     * @param message message of the error
     * @param status the status of the error
     * @return responseEntity
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }
}
