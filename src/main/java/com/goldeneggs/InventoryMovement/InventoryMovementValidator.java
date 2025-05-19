package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Egg.EggRepository;
import com.goldeneggs.User.User;
import com.goldeneggs.User.UserRepository;


import java.util.Date;

public class InventoryMovementValidator {

    private final EggRepository eggRepository;
    private final UserRepository userRepository;

    public InventoryMovementValidator(EggRepository eggRepository, UserRepository userRepository) {
        this.eggRepository = eggRepository;
        this.userRepository = userRepository;
    }

    public static boolean validateMovementDate(Date movementDate) { return movementDate != null &&  (movementDate.getTime() <= System.currentTimeMillis());}

    public static boolean validateCombs(int combs) {return combs > 0;}

    public boolean validateEgg(Egg egg) { return egg != null && egg.getId() != null && eggRepository.existsById(egg.getId());}

    public boolean validateUser(User user) {return user != null && user.getId() != null && userRepository.existsById(user.getId());}
}
