package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Egg.EggRepository;
import com.goldeneggs.User.User;
import com.goldeneggs.User.UserRepository;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
public class InventoryMovementValidator {

    private final EggRepository eggRepository;
    private final UserRepository userRepository;

    public InventoryMovementValidator(EggRepository eggRepository, UserRepository userRepository) {
        this.eggRepository = eggRepository;
        this.userRepository = userRepository;
    }

    public boolean validateMovementDate(Date movementDate) { return movementDate != null &&  (movementDate.getTime() <= System.currentTimeMillis());}

    public boolean validateCombs(int combs) {return combs > 0;}

    public boolean validateEgg(Egg egg) { return egg != null && egg.getId() != null && eggRepository.existsById(egg.getId());}

    public boolean validateUser(User user) {return user != null && user.getId() != null && userRepository.existsById(user.getId());}
}
