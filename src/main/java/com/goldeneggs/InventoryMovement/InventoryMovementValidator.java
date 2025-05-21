package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Egg.Egg;
import com.goldeneggs.User.User;


import java.util.Date;

public class InventoryMovementValidator {

    public static boolean validateMovementDate(Date movementDate) { return movementDate != null &&  (movementDate.getTime() <= System.currentTimeMillis());}

    public static boolean validateCombs(int combs) {return combs > 0;}

    public static boolean validateEgg(Egg egg) { return egg != null && egg.getId() != null;}

    public static boolean validateUser(User user) {return user != null && user.getId() != null;}
}
