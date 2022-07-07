package ru.nsu.store.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.store.dto.perfume.PerfumeRequest;
import ru.nsu.store.dto.perfume.PerfumeResponse;
import ru.nsu.store.entity.Perfume;
import ru.nsu.store.exception.InputFieldException;
import ru.nsu.store.service.interfaces.PerfumeService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PerfumeMapperTest {

    @InjectMocks
    private PerfumeMapper perfumeMapper;

    @Mock
    private CommonMapper commonMapper;

    @Mock
    private PerfumeService perfumeService;

    @Mock
    private BindingResult bindingResult;

    @Test
    void findPerfumeById() {
        PerfumeResponse perfumeResponse = new PerfumeResponse();

        when(commonMapper.convertToResponse(perfumeService.findPerfumeById(1L), PerfumeResponse.class))
                .thenReturn(perfumeResponse);

        assertEquals(perfumeResponse, perfumeMapper.findPerfumeById(1L));
    }

    @Test
    void findPerfumesByIds() {
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();
        List<Long> perfumesIds = new ArrayList<>();

        when(commonMapper.convertToResponseList(perfumeService.findPerfumesByIds(perfumesIds),
                PerfumeResponse.class)).thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeMapper.findPerfumesByIds(perfumesIds));
    }

    @Test
    void findAllPerfumes() {
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();

        when(commonMapper.convertToResponseList(perfumeService.findAllPerfumes(), PerfumeResponse.class))
                .thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeMapper.findAllPerfumes());
    }

    @Test
    void filter() {
        List<Perfume> perfumeList = new ArrayList<>();
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();

        when(commonMapper.convertToResponseList(perfumeList, PerfumeResponse.class)).thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeMapper.filter(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    void findByPerfumerOrderByPriceDesc() {
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();
        String perfumer = "Lancome";

        when(commonMapper.convertToResponseList(perfumeService.findByPerfumerOrderByPriceDesc(perfumer),
                PerfumeResponse.class)).thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeMapper.findByPerfumerOrderByPriceDesc(perfumer));
    }

    @Test
    void findByPerfumeGenderOrderByPriceDesc() {
        String perfumeGender = "female";
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();

        when(commonMapper.convertToResponseList(perfumeService.findByPerfumeGenderOrderByPriceDesc(perfumeGender),
                PerfumeResponse.class)).thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeMapper.findByPerfumeGenderOrderByPriceDesc(perfumeGender));
    }

    @Test
    void savePerfume() {
        PerfumeRequest perfumeRequest = new PerfumeRequest();
        Perfume perfume = new Perfume();
        MultipartFile file = new MockMultipartFile("abc", new byte[1]);
        PerfumeResponse perfumeResponse = new PerfumeResponse();
        bindingResult = new BindException(new Object(), "");

        when(commonMapper.convertToEntity(perfumeRequest, Perfume.class)).thenReturn(perfume);
        when(commonMapper.convertToResponse(perfumeService.savePerfume(perfume, file), PerfumeResponse.class))
                .thenReturn(perfumeResponse);

        assertEquals(perfumeResponse, perfumeMapper.savePerfume(perfumeRequest, file, bindingResult));
    }

    @Test
    void savePerfumeWithException() {
        PerfumeRequest perfumeRequest = new PerfumeRequest();
        MultipartFile file = new MockMultipartFile("abc", new byte[1]);

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(InputFieldException.class, () -> perfumeMapper.savePerfume(perfumeRequest, file, bindingResult));
    }

    @Test
    void deleteOrder() {
        Long perfumeId = 1L;
        List<PerfumeResponse> perfumeResponseList = new ArrayList<>();

        when(commonMapper.convertToResponseList(perfumeService.deletePerfume(perfumeId), PerfumeResponse.class))
                .thenReturn(perfumeResponseList);

        assertEquals(perfumeResponseList, perfumeMapper.deleteOrder(perfumeId));
    }
}
