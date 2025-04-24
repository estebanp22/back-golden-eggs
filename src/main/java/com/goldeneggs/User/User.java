package com.goldeneggs.User;

import com.goldeneggs.Role.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

/**
 * Entity class representing a User in the system.
 * It includes user details such as name, phone number, email, username, and associated roles.
 */
@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /**
     * Many-to-many relationship with the Role entity.
     * A user can have multiple roles.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    public User() {
    }

    /**
     * Enables the user account by setting 'enabled' to true.
     */
    public void enabled() {
        this.enabled = true;
    }

    /**
     * Disables the user account by setting 'enabled' to false.
     */
    public void disabled() {
        this.enabled = false;
    }

    /**
     * Computes a hash code based on the user ID and username.
     * Used to ensure uniqueness in collections like HashSet.
     *
     * @return The hash code of the user object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    /**
     * Compares this user with another object for equality.
     * Two users are considered equal if they have the same ID and username.
     *
     * @param o The object to compare with.
     * @return True if the users are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && username.equals(user.username);
    }
}
