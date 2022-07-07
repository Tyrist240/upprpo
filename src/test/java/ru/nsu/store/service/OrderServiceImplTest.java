package ru.nsu.store.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.nsu.store.entity.Order;
import ru.nsu.store.entity.Perfume;
import ru.nsu.store.exception.ApiRequestException;
import ru.nsu.store.repository.OrderItemRepository;
import ru.nsu.store.repository.OrderRepository;
import ru.nsu.store.repository.PerfumeRepository;
import ru.nsu.store.service.email.MailSender;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PerfumeRepository perfumeRepository;

    @Mock
    private MailSender mailSender;

    @Test
    void findAll() {
        when(orderRepository.findAllByOrderByIdAsc()).thenReturn(List.of(new Order(), new Order()));

        assertIterableEquals(List.of(new Order(), new Order()), orderService.findAll());
    }

    @Test
    void getAllOrdersByQuery() {
        assertNotNull(orderService.getAllOrdersByQuery());
    }

    @Test
    void getUserOrdersByEmailQuery() {
        assertNotNull(orderService.getUserOrdersByEmailQuery());
    }

    @Test
    void findOrderByEmail() {
        when(orderRepository.findOrderByEmail(any())).thenReturn(List.of(new Order(), new Order()));

        assertIterableEquals(List.of(new Order(), new Order()), orderService.findOrderByEmail("email"));
    }

    @Test
    void postOrder() {
        Perfume perfume = new Perfume();
        perfume.setPrice(200);

        when(perfumeRepository.findById(any())).thenReturn(Optional.of(perfume));
        orderService.postOrder(new Order(), Map.of(1L, 2L));

        verify(mailSender).sendMessageHtml(any(), any(), any(), any());
        verify(orderRepository).save(any());
    }

    @Test
    void deleteOrderThrowsApiRequestException() {
        when(orderRepository.findById(any()))
                .thenThrow(new ApiRequestException("Order not found.", HttpStatus.NOT_FOUND));

        assertThrows(ApiRequestException.class, () -> orderService.deleteOrder(1L));
    }

    @Test
    void deleteOrder() {
        when(orderRepository.findById(any()))
                .thenReturn(Optional.of(new Order()));

        assertEquals(List.of(), orderService.deleteOrder(1L));
    }
}