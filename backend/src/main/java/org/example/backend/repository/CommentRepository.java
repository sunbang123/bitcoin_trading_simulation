package org.example.backend.repository;

import org.example.backend.entity.Comment;
import org.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByUser(User user);
}