package com.goldeneggs.TypeEgg;

import com.goldeneggs.Exception.InvalidTypeEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing egg types.
 */
@RestController
@RequestMapping("/api/v1/egg-types")
@CrossOrigin("*")
public class TypeEggController {

    @Autowired
    private TypeEggService typeEggService;

    /**
     * Saves a new TypeEgg entity to the database. If successful, returns the
     * created TypeEgg entity along with an HTTP status of 201 (Created). Handles
     * exceptions such as InvalidTypeEggDataException, ResourceNotFoundException, and
     * other unexpected errors, returning appropriate HTTP status codes and messages.
     *
     * @param typeEgg the TypeEgg object to be saved
     * @return a ResponseEntity containing the saved TypeEgg object and HTTP status if
     *         successful, or an error message with the corresponding HTTP status in
     *         case of an exception
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody TypeEgg typeEgg) {
        try {
            TypeEgg saved = typeEggService.save(typeEgg);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (InvalidTypeEggDataException | ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
     * Retrieves an egg type by its unique ID. If the ID is found, returns the corresponding
     * TypeEgg entity along with an HTTP status of 200 (OK). If the ID is not found, an error
     * message with HTTP status 404 (Not Found) is returned.
     *
     * @param id the unique identifier of the TypeEgg to retrieve
     * @return ResponseEntity containing the TypeEgg object and HTTP status if found, or an
     *         error message with HTTP status 404 (Not Found) if the resource is not found
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            TypeEgg typeEgg = typeEggService.getById(id);
            return new ResponseEntity<>(typeEgg, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Deletes a TypeEgg entity by its unique ID. If the specified ID is found, the
     * corresponding entity is deleted, and an HTTP status of 204 (No Content) is returned.
     * If the ID is not found, an error message with HTTP status 404 (Not Found) is returned.
     *
     * @param id the unique identifier of the TypeEgg to delete
     * @return ResponseEntity with HTTP status 204 (No Content) if the deletion is successful,
     *         or an error message with HTTP status 404 (Not Found) if the resource is not found
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            typeEggService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates an existing TypeEgg entity with the provided data. If a TypeEgg with the specified
     * ID is found, updates its details and returns the updated entity. If the ID is not found, a
     * 404 (Not Found) status is returned. If the provided data is invalid, a 400 (Bad Request)
     * status is returned.
     *
     * @param id the unique identifier of the TypeEgg to update
     * @param updated the TypeEgg object containing the updated details
     * @return ResponseEntity containing the updated TypeEgg object and HTTP status if successful,
     *         or the relevant HTTP status in case of an error
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<TypeEgg> update(@PathVariable Long id, @RequestBody TypeEgg updated) {
        try {
            TypeEgg updatedTypeEgg = typeEggService.update(id, updated);
            return new ResponseEntity<>(updatedTypeEgg, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidTypeEggDataException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
