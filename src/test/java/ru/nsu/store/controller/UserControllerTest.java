package ru.nsu.store.controller;

import graphql.GraphQL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import ru.nsu.store.dto.GraphQLRequest;
import ru.nsu.store.dto.order.OrderRequest;
import ru.nsu.store.dto.order.OrderResponse;
import ru.nsu.store.dto.perfume.PerfumeResponse;
import ru.nsu.store.dto.review.ReviewRequest;
import ru.nsu.store.dto.user.UserRequest;
import ru.nsu.store.dto.user.UserResponse;
import ru.nsu.store.mapper.OrderMapper;
import ru.nsu.store.mapper.UserMapper;
import ru.nsu.store.security.UserPrincipal;
import ru.nsu.store.service.graphql.GraphQLProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;
import static ru.nsu.store.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserMapper userMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GraphQLProvider graphQLProvider;

    @Mock
    private GraphQL graphQL;

    private  BindingResult bindingResult;

    @Test
    void getUserInfo() {
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(USER_EMAIL);
        UserPrincipal userPrincipal = new UserPrincipal(1L, USER_EMAIL, null, null);

        when(userMapper.findUserByEmail(USER_EMAIL)).thenReturn(userResponse);

        assertEquals(USER_EMAIL, Objects.requireNonNull(userController.getUserInfo(userPrincipal).getBody()).getEmail());
    }

    @Test
    void updateUserInfo() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(USER2_EMAIL);
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(USER2_EMAIL);

        when(userMapper.updateProfile(USER_EMAIL, userRequest, bindingResult)).thenReturn(userResponse);

        assertEquals(USER2_EMAIL, Objects.requireNonNull(userController.updateUserInfo(
                        new UserPrincipal(null, USER_EMAIL, null, null), userRequest,
                        bindingResult).getBody()).getEmail());
    }

    @Test
    void getCart() {
        List<Long> perfumesId = new ArrayList<>();
        perfumesId.add(1L);
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();
        perfumeResponseList.add(new PerfumeResponse());

        when(userMapper.getCart(perfumesId)).thenReturn(perfumeResponseList);

        assertEquals(1, Objects.requireNonNull(userController.getCart(perfumesId).getBody()).size());
    }

    @Test
    void getUserOrders() {
        UserPrincipal userPrincipal = new UserPrincipal(null, USER_EMAIL, null, null);
        List<OrderResponse> orderResponseList = new ArrayList<>();
        orderResponseList.add(new OrderResponse());

        when(orderMapper.findOrderByEmail(USER_EMAIL)).thenReturn(orderResponseList);

        assertEquals(1, Objects.requireNonNull(userController.getUserOrders(userPrincipal).getBody()).size());
    }

    @Test
    void postOrder() {
        OrderRequest orderRequest = new OrderRequest();
        OrderResponse orderResponse = new OrderResponse();

        when(orderMapper.postOrder(orderRequest, bindingResult)).thenReturn(orderResponse);

        assertEquals(orderResponse, userController.postOrder(orderRequest, bindingResult).getBody());
    }

    @Test
    void addReviewToPerfume() {
        ReviewRequest reviewRequest = new ReviewRequest();
        PerfumeResponse perfumeResponse = new PerfumeResponse();
        perfumeResponse.setId(1L);

        when(userMapper.addReviewToPerfume(reviewRequest, null, bindingResult)).thenReturn(perfumeResponse);
        doNothing().when(messagingTemplate).convertAndSend("/topic/reviews/1", perfumeResponse);

        assertEquals(perfumeResponse, userController.addReviewToPerfume(reviewRequest, bindingResult).getBody());
    }

    @Test
    void getUserInfoByQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(userController.getUserInfoByQuery(new GraphQLRequest()).getBody());
    }

    @Test
    void getUserOrdersByQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(userController.getUserOrdersByQuery(new GraphQLRequest()).getBody());
    }
}
