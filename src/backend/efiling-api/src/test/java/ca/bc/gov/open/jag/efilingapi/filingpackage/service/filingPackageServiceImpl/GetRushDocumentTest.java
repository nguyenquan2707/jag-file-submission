package ca.bc.gov.open.jag.efilingapi.filingpackage.service.filingPackageServiceImpl;

import ca.bc.gov.open.jag.efilingapi.TestHelpers;
import ca.bc.gov.open.jag.efilingapi.account.service.AccountService;
import ca.bc.gov.open.jag.efilingapi.filingpackage.mapper.FilingPackageMapperImpl;
import ca.bc.gov.open.jag.efilingapi.filingpackage.model.SubmittedDocument;
import ca.bc.gov.open.jag.efilingapi.filingpackage.service.FilingPackageServiceImpl;
import ca.bc.gov.open.jag.efilingcommons.submission.EfilingReviewService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;

import java.math.BigDecimal;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("FilePackageServiceImplTest")
public class GetRushDocumentTest {
    private static final byte[] DOC_DATA = "TEST".getBytes();
    private static final BigDecimal DOCUMENT_NOT_FOUND = new BigDecimal(50);

    FilingPackageServiceImpl sut;

    @Mock
    EfilingReviewService efilingReviewServiceMock;

    @Mock
    AccountService accountServiceMock;

    @BeforeAll
    public void beforeAll() {

        MockitoAnnotations.openMocks(this);

        Mockito.when(accountServiceMock.getCsoAccountDetails(ArgumentMatchers.eq(TestHelpers.CASE_1_STRING))).thenReturn(TestHelpers.createAccount(BigDecimal.ONE));

        Mockito.when(accountServiceMock.getCsoAccountDetails(ArgumentMatchers.eq(TestHelpers.CASE_2_STRING))).thenReturn(TestHelpers.createAccount(null));

        sut = new FilingPackageServiceImpl(efilingReviewServiceMock, accountServiceMock, new FilingPackageMapperImpl(), null);
    }


    @Test
    @DisplayName("Ok: a rush document was returned")
    public void withValidRequestReturnDocument() {

        Mockito.when(efilingReviewServiceMock.findStatusByPackage(ArgumentMatchers.any())).thenReturn(Optional.of(TestHelpers.createFilingPackage(true)));

        Mockito.when(efilingReviewServiceMock.getRushDocument(Mockito.eq(TestHelpers.DOCUMENT_ID_TWO_BD))).thenReturn(Optional.of(DOC_DATA));

        Optional<SubmittedDocument> result = sut.getRushDocument(TestHelpers.CASE_1_STRING, BigDecimal.ONE, TestHelpers.DOCUMENT_ID_TWO_BD.toPlainString());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(TestHelpers.NAME, result.get().getName());
        Assertions.assertEquals(new ByteArrayResource(DOC_DATA), result.get().getData());

    }

    @Test
    @DisplayName("Not found: missing account")
    public void withValidRequestButMissingAccountReturnEmpty() {

        Optional<SubmittedDocument> result = sut.getRushDocument(TestHelpers.CASE_2_STRING, BigDecimal.ONE, TestHelpers.DOCUMENT_ID_TWO_BD.toPlainString());
        Assertions.assertFalse(result.isPresent());

    }


    @Test
    @DisplayName("Not found: no filing package")
    public void withValidRequestButMissingPackageReturnEmpty() {

        Mockito.when(efilingReviewServiceMock.findStatusByPackage(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Optional<SubmittedDocument> result = sut.getRushDocument(TestHelpers.CASE_1_STRING, BigDecimal.TEN, TestHelpers.DOCUMENT_ID_TWO_BD.toPlainString());

        Assertions.assertFalse(result.isPresent());

    }

    @Test
    @DisplayName("Not found: document not in filing package")
    public void withValidRequestButMissingDocumentInPackageReturnEmpty() {

        Mockito.when(efilingReviewServiceMock.findStatusByPackage(ArgumentMatchers.any())).thenReturn(Optional.of(TestHelpers.createFilingPackage(true)));

        Optional<SubmittedDocument> result = sut.getRushDocument(TestHelpers.CASE_1_STRING, BigDecimal.ONE, DOCUMENT_NOT_FOUND.toPlainString());

        Assertions.assertFalse(result.isPresent());

    }

    @Test
    @DisplayName("Not found: no document")
    public void withValidRequestButMissingDocumentReturnEmpty() {

        Mockito.when(efilingReviewServiceMock.findStatusByPackage(ArgumentMatchers.any())).thenReturn(Optional.of(TestHelpers.createFilingPackage(true)));

        Mockito.when(efilingReviewServiceMock.getRushDocument(Mockito.eq(TestHelpers.DOCUMENT_ID_TWO_BD))).thenReturn(Optional.empty());

        Optional<SubmittedDocument> result = sut.getRushDocument(TestHelpers.CASE_1_STRING, BigDecimal.TEN, TestHelpers.DOCUMENT_ID_TWO_BD.toPlainString());

        Assertions.assertFalse(result.isPresent());

    }

}
