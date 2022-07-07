package ru.nsu.store.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.nsu.store.entity.Perfume;
import ru.nsu.store.repository.PerfumeRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.nsu.store.Constants.*;

@ExtendWith(MockitoExtension.class)
class PerfumeServiceImplTest {

    @InjectMocks
    private PerfumeServiceImpl perfumeService;

    @Mock
    private PerfumeRepository perfumeRepository;

    @Mock
    private AmazonS3 amazonS3client;

    @Test
    void findPerfumeById() {
        Perfume perfume = new Perfume();
        perfume.setId(123L);

        when(perfumeRepository.findById(123L)).thenReturn(Optional.of(perfume));
        perfumeService.findPerfumeById(123L);
        assertEquals(123L, perfume.getId());
        assertNotEquals(1L, perfume.getId());
        verify(perfumeRepository, times(1)).findById(123L);
    }

    @Test
    void findAllPerfumes() {
        List<Perfume> perfumeList = new ArrayList<>();
        perfumeList.add(new Perfume());
        perfumeList.add(new Perfume());

        when(perfumeRepository.findAllByOrderByIdAsc()).thenReturn(perfumeList);
        perfumeService.findAllPerfumes();
        assertEquals(2, perfumeList.size());
        verify(perfumeRepository, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    void filter() {
        Perfume perfumeChanel = new Perfume();
        perfumeChanel.setPerfumer(PERFUMER_CHANEL);
        perfumeChanel.setPerfumeGender(PERFUME_GENDER);
        perfumeChanel.setPrice(101);
        perfumeChanel.setYear(2001);
        perfumeChanel.setVolume("100");
        perfumeChanel.setType("toilet water");
        Perfume perfumeCreed = new Perfume();
        perfumeCreed.setPerfumer(PERFUMER_CREED);
        perfumeCreed.setPerfumeGender(PERFUME_GENDER);
        perfumeCreed.setPrice(102);
        perfumeCreed.setYear(2020);
        perfumeCreed.setVolume("50");
        perfumeCreed.setType("toilet water");

        List<Perfume> perfumeList = new ArrayList<>();
        perfumeList.add(perfumeChanel);
        perfumeList.add(perfumeCreed);

        List<String> perfumers = new ArrayList<>();
        perfumers.add(PERFUMER_CHANEL);
        perfumers.add(PERFUMER_CREED);

        List<String> genders = new ArrayList<>();
        genders.add(PERFUME_GENDER);

        List<Integer> prices = new ArrayList<>();
        prices.add(perfumeChanel.getPrice());
        prices.add(perfumeCreed.getPrice());

        List<Integer> years = new ArrayList<>();
        years.add(perfumeChanel.getYear());
        years.add(perfumeCreed.getYear());

        List<String> volumes = new ArrayList<>();
        volumes.add(perfumeChanel.getVolume());
        volumes.add(perfumeCreed.getVolume());

        List<String> types = new ArrayList<>();
        types.add(perfumeChanel.getType());

        when(perfumeRepository.findByPerfumerIn(perfumers)).thenReturn(perfumeList);
        when(perfumeRepository.findByPerfumeGenderIn(genders)).thenReturn(perfumeList);
        when(perfumeRepository.findByPriceBetween(prices.get(0), prices.get(1))).thenReturn(perfumeList);
        perfumeService.filter(perfumers, genders, prices, true, years,
                volumes, types);
        assertEquals(2, perfumeList.size());
        assertEquals(PERFUMER_CHANEL, perfumeList.get(0).getPerfumer());
        verify(perfumeRepository, times(1)).findByPerfumerIn(perfumers);

        perfumeService.filter(new ArrayList<>(), genders, new ArrayList<>(), false, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());
        assertEquals(2, perfumeList.size());
        verify(perfumeRepository, times(1)).findByPerfumeGenderIn(genders);
    }

    @Test
    void findByPerfumerOrderByPriceDesc() {
        Perfume perfumeChanel = new Perfume();
        perfumeChanel.setPerfumer(PERFUMER_CHANEL);
        Perfume perfumeCreed = new Perfume();
        perfumeCreed.setPerfumer(PERFUMER_CREED);
        List<Perfume> perfumeList = new ArrayList<>();
        perfumeList.add(perfumeChanel);
        perfumeList.add(perfumeCreed);

        when(perfumeRepository.findByPerfumerOrderByPriceDesc(PERFUMER_CHANEL)).thenReturn(perfumeList);
        perfumeService.findByPerfumerOrderByPriceDesc(PERFUMER_CHANEL);
        assertEquals(PERFUMER_CHANEL, perfumeList.get(0).getPerfumer());
        assertNotEquals(PERFUMER_CREED, perfumeList.get(0).getPerfumer());
        verify(perfumeRepository, times(1)).findByPerfumerOrderByPriceDesc(PERFUMER_CHANEL);
    }

    @Test
    void findByPerfumeGenderOrderByPriceDesc() {
        Perfume perfumeChanel = new Perfume();
        perfumeChanel.setPerfumeGender(PERFUME_GENDER);
        List<Perfume> perfumeList = new ArrayList<>();
        perfumeList.add(perfumeChanel);

        when(perfumeRepository.findByPerfumeGenderOrderByPriceDesc(PERFUME_GENDER)).thenReturn(perfumeList);
        perfumeService.findByPerfumeGenderOrderByPriceDesc(PERFUME_GENDER);
        assertEquals(PERFUME_GENDER, perfumeList.get(0).getPerfumeGender());
        assertNotEquals("male", perfumeList.get(0).getPerfumeGender());
        verify(perfumeRepository, times(1)).findByPerfumeGenderOrderByPriceDesc(PERFUME_GENDER);
    }

    @Test
    void findPerfumesByIds() {
        List<Perfume> perfumeList = new ArrayList<>();
        List<Long> perfumesId = new ArrayList<>();

        when(perfumeRepository.findByIdIn(perfumesId)).thenReturn(perfumeList);

        assertEquals(perfumeList, perfumeService.findPerfumesByIds(perfumesId));
    }

    @Test
    void deletePerfume() {
        Perfume perfume = new Perfume();
        perfume.setReviews(new ArrayList<>());
        Long perfumeId = 1L;
        List<Perfume> perfumeList = new ArrayList<>();

        when(perfumeRepository.findById(perfumeId)).thenReturn(Optional.of(perfume));
        when(perfumeRepository.findAllByOrderByIdAsc()).thenReturn(perfumeList);

        assertEquals(perfumeList, perfumeService.deletePerfume(perfumeId));
    }

    @Test
    void savePerfumeMultipartFileNull() throws MalformedURLException {
        when(amazonS3client.getUrl(any(), any())).thenReturn(new URL("https://example.com/"));
        perfumeService.savePerfume(new Perfume(), null);

        verify(perfumeRepository).save(any());
    }

    @Test
    void savePerfume() throws MalformedURLException {
        when(amazonS3client.getUrl(any(), any())).thenReturn(new URL("https://example.com/"));
        perfumeService.savePerfume(new Perfume(), new MockMultipartFile("test", "test".getBytes(StandardCharsets.UTF_8)));

        verify(perfumeRepository).save(any());
    }
}
