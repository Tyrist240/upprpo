package ru.nsu.store.controller;

import graphql.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nsu.store.dto.GraphQLRequest;
import ru.nsu.store.dto.perfume.PerfumeResponse;
import ru.nsu.store.dto.perfume.PerfumeSearchRequest;
import ru.nsu.store.mapper.PerfumeMapper;
import ru.nsu.store.service.graphql.GraphQLProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PerfumeControllerTest {

    @InjectMocks
    private PerfumeController perfumeController;

    @Mock
    private PerfumeMapper perfumeMapper;

    @Mock
    private GraphQLProvider graphQLProvider;

    @Mock
    private GraphQL graphQL;

    @Test
    void getAllPerfumes() {
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();
        perfumeResponseList.add(new PerfumeResponse());

        when(perfumeMapper.findAllPerfumes()).thenReturn(perfumeResponseList);

        assertEquals(1, Objects.requireNonNull(perfumeController.getAllPerfumes().getBody()).size());
    }

    @Test
    void getPerfume() {
        PerfumeResponse perfumeResponse = new PerfumeResponse();

        when(perfumeMapper.findPerfumeById(any())).thenReturn(perfumeResponse);

        assertEquals(perfumeResponse, perfumeController.getPerfume(1L).getBody());
    }

    @Test
    void getPerfumesByIds() {
        List<Long> perfumesIds = new ArrayList<>();
        perfumesIds.add(1L);
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();

        when(perfumeMapper.findPerfumesByIds(perfumesIds)).thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeController.getPerfumesByIds(perfumesIds).getBody());
    }

    @Test
    void findPerfumesByFilterParams() {
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();
        PerfumeSearchRequest perfumeSearchRequest = new PerfumeSearchRequest();
        perfumeSearchRequest.setPerfumers(new ArrayList<>());
        perfumeSearchRequest.setGenders(new ArrayList<>());
        perfumeSearchRequest.setPrices(new ArrayList<>());
        perfumeSearchRequest.setSortByPrice(true);
        perfumeSearchRequest.setYears(new ArrayList<>());
        perfumeSearchRequest.setVolumes(new ArrayList<>());
        perfumeSearchRequest.setTypes(new ArrayList<>());

        when(perfumeMapper.filter(perfumeSearchRequest.getPerfumers(), perfumeSearchRequest.getGenders(),
                perfumeSearchRequest.getPrices(), perfumeSearchRequest.isSortByPrice(),
                perfumeSearchRequest.getYears(), perfumeSearchRequest.getVolumes(), perfumeSearchRequest.getTypes()))
                .thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeController.findPerfumesByFilterParams(perfumeSearchRequest).getBody());
    }

    @Test
    void findByPerfumeGender() {
        PerfumeSearchRequest perfumeSearchRequest = new PerfumeSearchRequest();
        perfumeSearchRequest.setPerfumeGender("female");
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();

        when(perfumeMapper.findByPerfumeGenderOrderByPriceDesc(perfumeSearchRequest.getPerfumeGender()))
                .thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeController.findByPerfumeGender(perfumeSearchRequest).getBody());
    }

    @Test
    void findByPerfumer() {
        PerfumeSearchRequest perfumeSearchRequest = new PerfumeSearchRequest();
        perfumeSearchRequest.setPerfumer("Tom Ford");
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();

        when(perfumeMapper.findByPerfumerOrderByPriceDesc(perfumeSearchRequest.getPerfumer()))
                .thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeController.findByPerfumer(perfumeSearchRequest).getBody());
    }

    @Test
    void getPerfumesByIdsQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(perfumeController.getPerfumesByIdsQuery(new GraphQLRequest()).getBody());
    }

    @Test
    void getAllPerfumesByQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(perfumeController.getAllPerfumesByQuery(new GraphQLRequest()).getBody());
    }

    @Test
    void getPerfumeByQuery() {
        when(graphQLProvider.getGraphQL()).thenReturn(graphQL);
        when(graphQL.execute((String) null)).thenReturn(null);

        assertNull(perfumeController.getPerfumeByQuery(new GraphQLRequest()).getBody());
    }
}
