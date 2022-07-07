package ru.nsu.store.dto.perfume;

import lombok.Data;

import java.util.List;

@Data
public class PerfumeSearchRequest {

    private List<String> perfumers;

    private List<String> genders;

    private List<Integer> prices;

    private boolean sortByPrice;

    private List<Integer> years;

    private List<String> volumes;

    private List<String> types;

    private String perfumer;

    private String perfumeGender;

    private Integer year;

    private String volume;

    private String type;
}
