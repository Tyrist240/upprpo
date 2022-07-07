package ru.nsu.store.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nsu.store.entity.Perfume;
import ru.nsu.store.entity.Review;
import ru.nsu.store.entity.User;
import ru.nsu.store.repository.PerfumeRepository;
import ru.nsu.store.repository.ReviewRepository;
import ru.nsu.store.repository.UserRepository;

import java.util.*;

import static ru.nsu.store.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PerfumeRepository perfumeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    void findUserById() {
        User user = new User();
        user.setId(122L);

        when(userRepository.findById(122L)).thenReturn(java.util.Optional.of(user));
        userService.findUserById(122L);
        assertEquals(122L, user.getId());
        verify(userRepository, times(1)).findById(122L);
    }

    @Test
    void findUserByEmail() {
        User user = new User();
        user.setEmail(USER_EMAIL);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        userService.findUserByEmail(USER_EMAIL);

        assertEquals(USER_EMAIL, user.getEmail());
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    void findAllUsers() {
        List<User> usersList = new ArrayList<>();

        when(userRepository.findAllByOrderByIdAsc()).thenReturn(usersList);

        assertEquals(usersList, userService.findAllUsers());
    }

    @Test
    void getCart() {
        List<Perfume> perfumeList = new ArrayList<>();
        List<Long> perfumeIds = new ArrayList<>();

        when(perfumeRepository.findByIdIn(perfumeIds)).thenReturn(perfumeList);

        assertEquals(perfumeList, userService.getCart(perfumeIds));
    }

    @Test
    void updateProfile() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setFirstName(FIRST_NAME);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        userService.updateProfile(USER_EMAIL, user);
        assertEquals(USER_EMAIL, user.getEmail());
        assertEquals(FIRST_NAME, user.getFirstName());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addReviewToPerfume() {
        List<Review> reviewList = new ArrayList<>();
        Review review = new Review();
        review.setRating(5);
        reviewList.add(review);
        Perfume perfume = new Perfume();
        perfume.setId(123L);
        perfume.setReviews(reviewList);

        when(perfumeRepository.getOne(123L)).thenReturn(perfume);
        when(reviewRepository.save(review)).thenReturn(review);
        userService.addReviewToPerfume(review, 123L);
        assertEquals(123L, perfume.getId());
        assertNotNull(perfume.getReviews());
        verify(perfumeRepository, times(1)).getOne(123L);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void updateUserStatus() {
        User user = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUserStatus(1L);
    }

    @Test
    void getUserByQuery() {
        userService.getUserByQuery();
    }

    @Test
    void getAllUsersByQuery() {
        userService.getAllUsersByQuery();
    }
}
