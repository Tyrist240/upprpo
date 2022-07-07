package ru.nsu.store.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import ru.nsu.store.dto.perfume.PerfumeResponse;
import ru.nsu.store.dto.review.ReviewRequest;
import ru.nsu.store.dto.user.UserRequest;
import ru.nsu.store.dto.user.UserResponse;
import ru.nsu.store.entity.Review;
import ru.nsu.store.entity.User;
import ru.nsu.store.service.interfaces.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.nsu.store.Constants.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Mock
    private CommonMapper commonMapper;

    @Mock
    private UserService userService;

    @Test
    void findUserById() {
        UserResponse userResponse = new UserResponse();
        Long userId = 1L;

        when(commonMapper.convertToResponse(userService.findUserById(userId), UserResponse.class))
                .thenReturn(userResponse);

        assertEquals(userResponse, userMapper.findUserById(userId));
    }

    @Test
    void findUserByEmail() {
        UserResponse userResponse = new UserResponse();

        when(commonMapper.convertToResponse(userService.findUserByEmail(USER_EMAIL), UserResponse.class))
                .thenReturn(userResponse);

        assertEquals(userResponse, userMapper.findUserByEmail(USER_EMAIL));
    }

    @Test
    void getCart() {
        List<Long> perfumesIds = new ArrayList<>();
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();

        when(commonMapper.convertToResponseList(userService.getCart(perfumesIds), PerfumeResponse.class))
                .thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, userMapper.getCart(perfumesIds));
    }

    @Test
    void findAllUsers() {
        List<UserResponse> userResponses = new ArrayList<>();

        when(commonMapper.convertToResponseList(userService.findAllUsers(), UserResponse.class))
                .thenReturn(userResponses);

        assertEquals(userResponses, userMapper.findAllUsers());
    }

    @Test
    void updateUserStatus() {
        userMapper.updateUserStatus(1L);
        verify(userService, times(1)).updateUserStatus(1L);
    }

    @Test
    void updateProfile() {
        UserResponse userResponse = new UserResponse();
        User user = new User();
        UserRequest userRequest = new UserRequest();
        BindingResult bindingResult = new BindException(new Object(), "abc");

        when(commonMapper.convertToEntity(userRequest, User.class)).thenReturn(user);
        when(commonMapper.convertToResponse(userService.updateProfile(USER_EMAIL, user), UserResponse.class))
                .thenReturn(userResponse);

        assertEquals(userResponse, userMapper.updateProfile(USER_EMAIL, userRequest, bindingResult));
    }

    @Test
    void addReviewToPerfume() {
        PerfumeResponse perfumeResponse = new PerfumeResponse();
        Review review = new Review();
        ReviewRequest reviewRequest = new ReviewRequest();
        Long perfumeId = 1L;
        BindingResult bindingResult = new BindException(new Object(), "abc");

        when(commonMapper.convertToEntity(reviewRequest, Review.class)).thenReturn(review);
        when(commonMapper.convertToResponse(userService.addReviewToPerfume(review, perfumeId), PerfumeResponse.class))
                .thenReturn(perfumeResponse);

        assertEquals(perfumeResponse, userMapper.addReviewToPerfume(reviewRequest, perfumeId, bindingResult));
    }
}
