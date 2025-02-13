package ca.bc.gov.open.jag.efilingcsoclient.csoReviewServiceImpl;

import ca.bc.gov.ag.csows.filing.FilingFacadeBean;
import ca.bc.gov.ag.csows.filing.NestedEjbException_Exception;
import ca.bc.gov.open.jag.efilingcommons.exceptions.EfilingReviewServiceException;
import ca.bc.gov.open.jag.efilingcommons.model.RushDocumentRequest;
import ca.bc.gov.open.jag.efilingcsoclient.CsoReviewServiceImpl;
import ca.bc.gov.open.jag.efilingcsoclient.config.CsoProperties;
import ca.bc.gov.open.jag.efilingcsoclient.mappers.FilePackageMapperImpl;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("GetRushDocumentTest")
public class GetRushDocumentTest {

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    FilingFacadeBean filingFacadeBeanMock;

    private static CsoReviewServiceImpl sut;

    @BeforeEach
    public void beforeEach() throws NestedEjbException_Exception {

        MockitoAnnotations.openMocks(this);

        byte[] someBytes = "test".getBytes();

        Mockito.when(filingFacadeBeanMock.getActiveSuppDocURL(Mockito.eq(BigDecimal.TEN), Mockito.eq(BigDecimal.TEN), Mockito.eq(BigDecimal.TEN))).thenThrow(new NestedEjbException_Exception());

        Mockito.when(filingFacadeBeanMock.getActiveSuppDocURL(Mockito.eq(BigDecimal.ONE), Mockito.eq(BigDecimal.ONE), Mockito.eq(BigDecimal.ONE))).thenReturn("http://localhost/acdc/1");


        Mockito.when(restTemplateMock.getForEntity(
                Mockito.eq("http://localhost/acdc/1"), Mockito.eq(byte[].class), Mockito.any(HttpEntity.class)))
                .thenReturn(new ResponseEntity(someBytes, HttpStatus.OK));

        CsoProperties csoProperties = new CsoProperties();
        csoProperties.setCsoBasePath("http://locahost:8080");

        sut = new CsoReviewServiceImpl(null, null, filingFacadeBeanMock, new FilePackageMapperImpl(), null, restTemplateMock, null);

    }

    @DisplayName("OK: it is working")
    @Test
    public void testWithFoundResult() {

        Optional<byte[]> actual = sut.getRushDocument(
                RushDocumentRequest.builder()
                .procReqId(BigDecimal.ONE)
                .docSeqNo(BigDecimal.ONE)
                .procItemSeqNo(BigDecimal.ONE)
                .create());
        Assertions.assertEquals("test", new String(actual.get()));

    }

    @DisplayName("Error: it can't generate URL")
    @Test
    public void testWhenCantGenerateUrl() {

        Assertions.assertThrows(EfilingReviewServiceException.class, () -> {
            sut.getRushDocument(
                    RushDocumentRequest.builder()
                    .procItemSeqNo(BigDecimal.TEN)
                    .docSeqNo(BigDecimal.TEN)
                    .procReqId(BigDecimal.TEN)
                    .create());
        });

    }

}
