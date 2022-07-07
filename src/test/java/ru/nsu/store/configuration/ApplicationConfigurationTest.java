package ru.nsu.store.configuration;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigurationTest {

    private final ApplicationConfiguration  applicationConfiguration = new ApplicationConfiguration();

    @Test
    void getPasswordEncoder() {
        PasswordEncoder passwordEncoder = applicationConfiguration.getPasswordEncoder();

        assertNotNull(passwordEncoder);
    }

    @Test
    void s3Client() {
        applicationConfiguration.setAwsAccessKey("YCAJEA4Z2n7N6KDBhuknh5D9h");
        applicationConfiguration.setAwsAccessSecret("YCOJrOQXzR7O5J1T1pdm2DXKoHKCubTNr4RfnYud");
        AmazonS3 amazonS3 = applicationConfiguration.s3Client();

        assertNotNull(amazonS3);
    }

    @Test
    void modelMapper() {
        ModelMapper mapper = applicationConfiguration.modelMapper();

        assertNotNull(mapper);
    }
}