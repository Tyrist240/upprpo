package ru.nsu.store.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.nsu.store.dto.perfume.PerfumeResponse;
import ru.nsu.store.dto.review.ReviewRequest;
import ru.nsu.store.dto.user.UserRequest;
import ru.nsu.store.dto.user.UserResponse;
import ru.nsu.store.entity.Review;
import ru.nsu.store.entity.User;
import ru.nsu.store.exception.InputFieldException;
import ru.nsu.store.service.interfaces.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final CommonMapper commonMapper;
    private final UserService userService;

    public UserResponse findUserById(Long userId) {
        return commonMapper.convertToResponse(userService.findUserById(userId), UserResponse.class);
    }

    public UserResponse findUserByEmail(String email) {
        return commonMapper.convertToResponse(userService.findUserByEmail(email), UserResponse.class);
    }

    public List<PerfumeResponse> getCart(List<Long> perfumesIds) {
        return commonMapper.convertToResponseList(userService.getCart(perfumesIds), PerfumeResponse.class);
    }

    public List<UserResponse> findAllUsers() {
        return commonMapper.convertToResponseList(userService.findAllUsers(), UserResponse.class);
    }

    public void updateUserStatus(Long userId) {
        userService.updateUserStatus(userId);
    }

    public UserResponse updateProfile(String email, UserRequest userRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        User user = commonMapper.convertToEntity(userRequest, User.class);
        return commonMapper.convertToResponse(userService.updateProfile(email, user), UserResponse.class);
    }

    public PerfumeResponse addReviewToPerfume(ReviewRequest reviewRequest, Long perfumeId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        Review review = commonMapper.convertToEntity(reviewRequest, Review.class);
        return commonMapper.convertToResponse(userService.addReviewToPerfume(review, perfumeId), PerfumeResponse.class);
    }
}

