package ru.practicum.user.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Page<User> findByIdIn(Collection<Long> ids, Pageable pageable);
}
