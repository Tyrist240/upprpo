package ru.nsu.store.controller;

import graphql.GraphQL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.nsu.store.dto.GraphQLRequest;
import ru.nsu.store.dto.order.OrderResponse;
import ru.nsu.store.dto.perfume.PerfumeRequest;
import ru.nsu.store.dto.perfume.PerfumeResponse;
import ru.nsu.store.dto.user.UserRequest;
import ru.nsu.store.dto.user.UserResponse;
import ru.nsu.store.mapper.OrderMapper;
import ru.nsu.store.mapper.PerfumeMapper;
import ru.nsu.store.mapper.UserMapper;
import ru.nsu.store.service.graphql.GraphQLProvider;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PerfumeMapper perfumeMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private GraphQLProvider graphQLProvider;

    @Mock
    private GraphQL graphQL;

    @Test
    void addPerfume() {
        PerfumeResponse response = new PerfumeResponse();
        when(perfumeMapper.savePerfume(any(), any(), any())).thenReturn(response);

        assertEquals(response,
                adminController.addPerfume(
                                new MockMultipartFile("name", "name".getBytes(StandardCharsets.UTF_8)),
                                new PerfumeRequest(),
                                null)
                        .getBody());
    }

    @Test
    void deletePerfume() {
        List<PerfumeResponse> responseList = List.of(new PerfumeResponse());
        when(perfumeMapper.deleteOrder(any())).thenReturn(responseList);

        assertEquals(responseList, adminController.deletePerfume(1L).getBody());
    }

    @Test
    void getAllOrders() {
        List<OrderResponse> orderResponseList = List.of(new OrderResponse(), new OrderResponse());
        when(orderMapper.findAllOrders()).thenReturn(orderResponseList);

        assertEquals(orderResponseList, adminController.getAllOrders().getBody());
    }

    @Test
    void getUserOrdersByEmail() {
        List<OrderResponse> orderResponseList = List.of(new OrderResponse(), new OrderResponse());
        when(orderMapper.findOrderByEmail(any())).thenReturn(orderResponseList);

        assertEquals(orderResponseList, adminController.getUserOrdersByEmail(new UserRequest()).getBody());
    }

    @Test
    void deleteOrder() {
        List<OrderResponse> orderResponseList = List.of(new OrderResponse());
        when(orderMapper.deleteOrder(any())).thenReturn(orderResponseList);

        assertEquals(orderResponseList, adminController.deleteOrder(1L).getBody());
    }

    @Test
    void getUser() {
        UserResponse userResponse = new UserResponse();
        when(userMapper.findUserById(any())).thenReturn(userResponse);

        assertEquals(userResponse, adminController.getUser(1L).getBody());
    }

    @Test
    void getAllUsers() {
        List<UserResponse> userResponses = List.of(new UserResponse(), new UserResponse());
        when(userMapper.findAllUsers()).thenReturn(userResponses);

        assertEquals(userResponses, adminController.getAllUsers().getBody());
    }

    @Test
    void updateUserStatus() {
        adminController.updateUserStatus(1L);
        verify(userMapper, times(1)).updateUserStatus(1L);
    }

    @Test
    void getUserByQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(adminController.getUserByQuery(new GraphQLRequest()).getBody());
    }

    @Test
    void getAllUsersByQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(adminController.getAllUsersByQuery(new GraphQLRequest()).getBody());
    }

    @Test
    void getAllOrdersQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(adminController.getAllOrdersQuery(new GraphQLRequest()).getBody());
    }

    @Test
    void getUserOrdersByEmailQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(adminController.getUserOrdersByEmailQuery(new GraphQLRequest()).getBody());
    }
}