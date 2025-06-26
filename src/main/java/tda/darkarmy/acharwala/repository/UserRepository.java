package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by email
    Optional<User> findByEmail(String email);

    // Find user by reset token
//    Optional<User> findByResetToken(String resetToken);

    // Check if an email exists in the database
//    boolean existsByEmail(String email);
}
