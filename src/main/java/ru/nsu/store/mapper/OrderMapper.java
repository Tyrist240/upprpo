package ru.nsu.store.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.nsu.store.dto.order.OrderRequest;
import ru.nsu.store.dto.order.OrderResponse;
import ru.nsu.store.entity.Order;
import ru.nsu.store.exception.InputFieldException;
import ru.nsu.store.service.interfaces.OrderService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final CommonMapper commonMapper;
    private final OrderService orderService;

    public List<OrderResponse> findAllOrders() {
        return commonMapper.convertToResponseList(orderService.findAll(), OrderResponse.class);
    }

    public List<OrderResponse> findOrderByEmail(String email) {
        return commonMapper.convertToResponseList(orderService.findOrderByEmail(email), OrderResponse.class);
    }

    public List<OrderResponse> deleteOrder(Long orderId) {
        return commonMapper.convertToResponseList(orderService.deleteOrder(orderId), OrderResponse.class);
    }

    public OrderResponse postOrder(OrderRequest orderRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        Order order = orderService.postOrder(commonMapper.convertToEntity(orderRequest, Order.class), orderRequest.getPerfumesId());
        return commonMapper.convertToResponse(order, OrderResponse.class);
    }
}
