package ru.nsu.store.service.interfaces;

import graphql.schema.DataFetcher;
import ru.nsu.store.entity.Perfume;
import ru.nsu.store.entity.Review;
import ru.nsu.store.entity.User;

import java.util.List;

public interface UserService {


    User findUserById(Long userId);

    User findUserByEmail(String email);

    DataFetcher<List<User>> getAllUsersByQuery();

    DataFetcher<User> getUserByQuery();

    List<User> findAllUsers();

    void updateUserStatus(Long userId);

    List<Perfume> getCart(List<Long> perfumeIds);

    User updateProfile(String email, User user);

    Perfume addReviewToPerfume(Review review, Long perfumeId);
}