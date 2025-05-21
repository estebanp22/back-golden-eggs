package com.goldeneggs.OrderEgg;

import com.goldeneggs.Exception.InvalidOrderDataException;
import com.goldeneggs.Exception.InvalidOrderEggDataException;
import com.goldeneggs.Exception.InvalidPayDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing order eggs.
 */
@RestController
@RequestMapping("/api/v1/orderEggs")
@CrossOrigin("*")
public class OrderEggController {

    @Autowired
    private OrderEggService orderEggService;

    /**
     * Saves a new orderEgg.
     *
     * @param orderEgg OrderEgg data.
     * @return Saved orderEgg.
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody OrderEgg orderEgg){
        try{
            OrderEgg saved = orderEggService.save(orderEgg);
            return new  ResponseEntity<>(saved, HttpStatus.CREATED);
        }catch (InvalidOrderEggDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a orderEggs by its ID.
     *
     * @param id OrderEgg ID.
     * @return OrderEgg if found, or 404 Not Found.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<OrderEgg> get(@PathVariable("id") Long id){
        try{
            OrderEgg orderEgg = orderEggService.get(id);
            return ResponseEntity.ok(orderEgg);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all orderEggs.
     *
     * @return List of all orderEggs.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<OrderEgg>> getAll(){return ResponseEntity.ok(orderEggService.getAll());}

    /**
     * Updates an existing orderEgg.
     *
     * @param id  ID of the orderEgg to update.
     * @param orderEgg Updated orderEgg data.
     * @return Updated orderEgg if found, or 404 Not Found.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody OrderEgg orderEgg){
        try{
            OrderEgg updated = orderEggService.update(id, orderEgg);
            return ResponseEntity.ok(updated);
        }catch (InvalidOrderEggDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a orderEgg by its ID.
     *
     * @param id OrderEgg ID.
     * @return HTTP 200 OK if deleted, or 404 Not Found.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        try{
            orderEggService.delete(id);
            return ResponseEntity.ok().build();
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
