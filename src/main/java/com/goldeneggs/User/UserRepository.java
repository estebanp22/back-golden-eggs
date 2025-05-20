package com.goldeneggs.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Retrieves a user from the database by their unique username.
     *
     * @param username The username of the user to retrieve. Must not be null or empty.
     * @return An Optional containing the User object if found, or an empty Optional if no user exists with the given username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user exists in the database based on their unique username.
     *
     * @param username The username to check for existence. Must not be null or empty.
     * @return A Boolean value: true if a user with the specified username exists, otherwise false.
     */
    Boolean existsByUsername(String username);

    /**
     * Counts the number of users associated with a specific role name.
     *
     * @param roleName The name of the role to filter users by. Must not be null or empty.
     * @return The number of users associated with the specified role name.
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Long countUsersByRoleName(@Param("roleName") String roleName);

    /**
     * Checks if a user exists in the database based on their unique email address.
     *
     * @param email The email address to check for existence. Must not be null or empty.
     * @return A boolean value: true if a user with the specified email exists, otherwise false.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user exists in the database based on their unique phone number.
     *
     * @param phoneNumber The phone number to check for existence. Must not be null or empty.
     * @return A boolean value: true if a user with the specified phone number exists, otherwise false.
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Retrieves a list of users that are associated with a specific role name and are enabled.
     *
     * @param roleName The name of the role to filter the users by. Must not be null or empty.
     * @return A list of User objects that have the specified role name and are enabled.
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.enabled = true")
    List<User> findAllByRoleNameAndEnabledIsTrue(@Param("roleName") String roleName);

}
