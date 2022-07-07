package ru.nsu.store.controller;

import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final GraphQLProvider graphQLProvider;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/info")
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(userMapper.findUserByEmail(user.getEmail()));
    }

    @PostMapping("/graphql/info")
    public ResponseEntity<ExecutionResult> getUserInfoByQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PutMapping("/edit")
    public ResponseEntity<UserResponse> updateUserInfo(@AuthenticationPrincipal UserPrincipal user,
                                                       @Valid @RequestBody UserRequest request,
                                                       BindingResult bindingResult) {
        return ResponseEntity.ok(userMapper.updateProfile(user.getEmail(), request, bindingResult));
    }

    @PostMapping("/cart")
    public ResponseEntity<List<PerfumeResponse>> getCart(@RequestBody List<Long> perfumesIds) {
        return ResponseEntity.ok(userMapper.getCart(perfumesIds));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(orderMapper.findOrderByEmail(user.getEmail()));
    }

    @PostMapping("/graphql/orders")
    public ResponseEntity<ExecutionResult> getUserOrdersByQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> postOrder(@Valid @RequestBody OrderRequest order, BindingResult bindingResult) {
        return ResponseEntity.ok(orderMapper.postOrder(order, bindingResult));
    }

    @PostMapping("/review")
    public ResponseEntity<PerfumeResponse> addReviewToPerfume(@Valid @RequestBody ReviewRequest review, BindingResult bindingResult) {
        PerfumeResponse perfume = userMapper.addReviewToPerfume(review, review.getPerfumeId(), bindingResult);
        messagingTemplate.convertAndSend("/topic/reviews/" + perfume.getId(), perfume);
        return ResponseEntity.ok(perfume);
    }
}
