package ru.nsu.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.store.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
