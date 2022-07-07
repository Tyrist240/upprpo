package ru.nsu.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.store.entity.Perfume;

import java.util.List;

@Repository
public interface PerfumeRepository extends JpaRepository<Perfume, Long> {

    List<Perfume> findAllByOrderByIdAsc();

    List<Perfume> findByPerfumerIn(List<String> perfumers);

    List<Perfume> findByPerfumeGenderIn(List<String> genders);

    List<Perfume> findByYearIn(List<Integer> years);

    List<Perfume> findByVolumeIn(List<String> volumes);

    List<Perfume> findByTypeIn(List<String> types);

    List<Perfume> findByPriceBetween(Integer startingPrice, Integer endingPrice);

    List<Perfume> findByPerfumerOrderByPriceDesc(String perfumer);

    List<Perfume> findByPerfumeGenderOrderByPriceDesc(String perfumeGender);

    List<Perfume> findByIdIn(List<Long> perfumesIds);
}