package ru.nsu.store.dto.order;

import lombok.Data;
import ru.nsu.store.dto.perfume.PerfumeResponse;

@Data
public class OrderItemResponse {

    private Long id;

    private Long amount;

    private Long quantity;

    private PerfumeResponse perfume;
}
