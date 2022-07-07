package ru.nsu.store.controller;

import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.store.dto.GraphQLRequest;
import ru.nsu.store.dto.perfume.PerfumeResponse;
import ru.nsu.store.dto.perfume.PerfumeSearchRequest;
import ru.nsu.store.mapper.PerfumeMapper;
import ru.nsu.store.service.graphql.GraphQLProvider;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/perfumes")
public class PerfumeController {

    private final PerfumeMapper perfumeMapper;
    private final GraphQLProvider graphQLProvider;

    @GetMapping
    public ResponseEntity<List<PerfumeResponse>> getAllPerfumes() {
        return ResponseEntity.ok(perfumeMapper.findAllPerfumes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfumeResponse> getPerfume(@PathVariable("id") Long perfumeId) {
        return ResponseEntity.ok(perfumeMapper.findPerfumeById(perfumeId));
    }

    @PostMapping("/ids")
    public ResponseEntity<List<PerfumeResponse>> getPerfumesByIds(@RequestBody List<Long> perfumesIds) {
        return ResponseEntity.ok(perfumeMapper.findPerfumesByIds(perfumesIds));
    }

    @PostMapping("/search")
    public ResponseEntity<List<PerfumeResponse>> findPerfumesByFilterParams(@RequestBody PerfumeSearchRequest filter) {
        return ResponseEntity.ok(perfumeMapper.filter(
                filter.getPerfumers(),
                filter.getGenders(),
                filter.getPrices(),
                filter.isSortByPrice(),
                filter.getYears(),
                filter.getVolumes(),
                filter.getTypes()
        ));
    }

    @PostMapping("/search/gender")
    public ResponseEntity<List<PerfumeResponse>> findByPerfumeGender(@RequestBody PerfumeSearchRequest filter) {
        return ResponseEntity.ok(perfumeMapper.findByPerfumeGenderOrderByPriceDesc(filter.getPerfumeGender()));
    }

    @PostMapping("/search/perfumer")
    public ResponseEntity<List<PerfumeResponse>> findByPerfumer(@RequestBody PerfumeSearchRequest filter) {
        return ResponseEntity.ok(perfumeMapper.findByPerfumerOrderByPriceDesc(filter.getPerfumer()));
    }

    @PostMapping("/graphql/ids")
    public ResponseEntity<ExecutionResult> getPerfumesByIdsQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/graphql/perfumes")
    public ResponseEntity<ExecutionResult> getAllPerfumesByQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/graphql/perfume")
    public ResponseEntity<ExecutionResult> getPerfumeByQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }
}
