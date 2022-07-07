package ru.nsu.store.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.nsu.store.dto.RegistrationRequest;
import ru.nsu.store.dto.order.OrderRequest;
import ru.nsu.store.dto.order.OrderResponse;
import ru.nsu.store.entity.Order;
import ru.nsu.store.exception.InputFieldException;
import ru.nsu.store.service.interfaces.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    @InjectMocks
    private OrderMapper orderMapper;

    @Mock
    private CommonMapper commonMapper;

    @Mock
    private OrderService orderService;

    @Test
    void findAllOrders() {
        List<OrderResponse> orderResponses = List.of(new OrderResponse(), new OrderResponse());
        List<Order> orders = List.of(new Order(), new Order());

        when(orderService.findAll()).thenReturn(orders);
        when(commonMapper.convertToResponseList(orders, OrderResponse.class)).thenReturn(orderResponses);

        assertIterableEquals(orderResponses, orderMapper.findAllOrders());
    }

    @Test
    void findOrderByEmail() {
        List<OrderResponse> orderResponses = List.of(new OrderResponse(), new OrderResponse());
        List<Order> orders = List.of(new Order(), new Order());

        when(orderService.findOrderByEmail(any())).thenReturn(orders);
        when(commonMapper.convertToResponseList(orders, OrderResponse.class)).thenReturn(orderResponses);

        assertIterableEquals(orderResponses, orderMapper.findOrderByEmail("email"));
    }

    @Test
    void deleteOrder() {
        List<OrderResponse> orderResponses = List.of(new OrderResponse());
        List<Order> orders = List.of(new Order());

        when(orderService.deleteOrder(any())).thenReturn(orders);
        when(commonMapper.convertToResponseList(orders, OrderResponse.class)).thenReturn(orderResponses);

        assertIterableEquals(orderResponses, orderMapper.deleteOrder(1L));
    }

    @Test
    void postOrderThrowsInputFieldException() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "name");
        bindingResult.addError(new ObjectError("error", "error message"));
        OrderRequest orderRequest = new OrderRequest();

        assertThrows(InputFieldException.class, () -> orderMapper.postOrder(orderRequest, bindingResult));
    }

    @Test
    void postOrder() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "name");
        OrderRequest orderRequest = new OrderRequest();

        assertNull(orderMapper.postOrder(orderRequest, bindingResult));
    }
}