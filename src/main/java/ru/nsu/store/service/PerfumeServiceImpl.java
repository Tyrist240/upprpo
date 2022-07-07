package ru.nsu.store.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.store.entity.Perfume;
import ru.nsu.store.exception.ApiRequestException;
import ru.nsu.store.repository.PerfumeRepository;
import ru.nsu.store.repository.ReviewRepository;
import ru.nsu.store.service.interfaces.PerfumeService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final ReviewRepository reviewRepository;
    private final AmazonS3 amazonS3client;

    @Value("${amazon.s3.bucket.name}")
    private String bucketName;

    @Override
    public DataFetcher<Perfume> getPerfumeByQuery() {
        return dataFetchingEnvironment -> {
            Long perfumeId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            return perfumeRepository.findById(perfumeId).get();
        };
    }

    @Override
    public DataFetcher<List<Perfume>> getAllPerfumesByQuery() {
        return dataFetchingEnvironment -> perfumeRepository.findAllByOrderByIdAsc();
    }

    @Override
    public DataFetcher<List<Perfume>> getAllPerfumesByIdsQuery() {
        return dataFetchingEnvironment -> {
            List<String> objects = dataFetchingEnvironment.getArgument("ids");
            List<Long> perfumesId = objects.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            return perfumeRepository.findByIdIn(perfumesId);
        };
    }

    @Override
    public Perfume findPerfumeById(Long perfumeId) {
        return perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new ApiRequestException("Perfume not found.", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Perfume> findAllPerfumes() {
        return perfumeRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<Perfume> findPerfumesByIds(List<Long> perfumesId) {
        return perfumeRepository.findByIdIn(perfumesId);
    }

    @Override
    public List<Perfume> filter(List<String> perfumers, List<String> genders, List<Integer> prices,
                                boolean sortByPrice, List<Integer> years, List<String> volumes,
                                List<String> types) {
        List<Perfume> perfumeList = new ArrayList<>();

        if (!perfumers.isEmpty() || !genders.isEmpty() || !prices.isEmpty() ||
            !years.isEmpty() || !volumes.isEmpty() || !types.isEmpty()) {
            if (!perfumers.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> perfumersList = new ArrayList<>();
                    for (String perfumer : perfumers) {
                        perfumersList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getPerfumer().equals(perfumer))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = perfumersList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByPerfumerIn(perfumers));
                }
            }
            if (!genders.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> gendersList = new ArrayList<>();
                    for (String gender : genders) {
                        gendersList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getPerfumeGender().equals(gender))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = gendersList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByPerfumeGenderIn(genders));
                }
            }
            if (!prices.isEmpty()) {
                perfumeList = perfumeRepository.findByPriceBetween(prices.get(0), prices.get(1));
            }
            if (!years.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> yearsList = new ArrayList<>();
                    for (Integer year : years) {
                        yearsList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getYear().equals(year))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = yearsList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByYearIn(years));
                }
            }
            if (!volumes.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> volumesList = new ArrayList<>();
                    for (String volume : volumes) {
                        volumesList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getVolume().equals(volume))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = volumesList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByVolumeIn(volumes));
                }
            }
            if (!types.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> typesList = new ArrayList<>();
                    for (String type : types) {
                        typesList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getType().equals(type))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = typesList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByTypeIn(types));
                }
            }
        } else {
            perfumeList = perfumeRepository.findAllByOrderByIdAsc();
        }
        if (sortByPrice) {
            perfumeList.sort(Comparator.comparing(Perfume::getPrice));
        } else {
            perfumeList.sort((perfume1, perfume2) -> perfume2.getPrice().compareTo(perfume1.getPrice()));
        }
        return perfumeList;
    }

    @Override
    public List<Perfume> findByPerfumerOrderByPriceDesc(String perfumer) {
        return perfumeRepository.findByPerfumerOrderByPriceDesc(perfumer);
    }

    @Override
    public List<Perfume> findByPerfumeGenderOrderByPriceDesc(String perfumeGender) {
        return perfumeRepository.findByPerfumeGenderOrderByPriceDesc(perfumeGender);
    }

    @Override
    public Perfume savePerfume(Perfume perfume, MultipartFile multipartFile) {
        if (multipartFile == null) {
            perfume.setFilename(amazonS3client.getUrl(bucketName, "empty.jpg").toString());
        } else {
            File file = new File(multipartFile.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileName = UUID.randomUUID().toString() + "." + multipartFile.getOriginalFilename();
            amazonS3client.putObject(new PutObjectRequest(bucketName, fileName, file));
            perfume.setFilename(amazonS3client.getUrl(bucketName, fileName).toString());
            file.delete();
        }
        return perfumeRepository.save(perfume);
    }

    @Override
    @Transactional
    public List<Perfume> deletePerfume(Long perfumeId) {
        Perfume perfume = perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new ApiRequestException("Perfume not found.", HttpStatus.NOT_FOUND));
        perfume.getReviews().forEach(review -> reviewRepository.deleteById(review.getId()));
        perfumeRepository.delete(perfume);
        return perfumeRepository.findAllByOrderByIdAsc();
    }
}
