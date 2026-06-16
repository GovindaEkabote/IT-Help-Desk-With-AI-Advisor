package com.help.desk.user.repository;

import com.help.desk.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findByPhone(String phone);

    // Add this method
    Optional<User> findByIdAndDeletedFalse(Long id);

    // Add this for getAllUsers to respect soft delete
    List<User> findAllByDeletedFalse();

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByEmployeeIdAndIdNot(String employeeId, Long id);


}
