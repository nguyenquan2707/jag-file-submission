package ca.bc.gov.open.jag.efilingapi.filingpackage.service;

import ca.bc.gov.open.jag.efilingapi.TestHelpers;
import ca.bc.gov.open.jag.efilingapi.account.service.AccountService;
import ca.bc.gov.open.jag.efilingapi.api.model.DocumentProperties;
import ca.bc.gov.open.jag.efilingapi.api.model.FilingPackage;
import ca.bc.gov.open.jag.efilingapi.filingpackage.mapper.FilingPackageMapperImpl;
import ca.bc.gov.open.jag.efilingcommons.model.AccountDetails;
import ca.bc.gov.open.jag.efilingcommons.model.Court;
import ca.bc.gov.open.jag.efilingcommons.model.Document;
import ca.bc.gov.open.jag.efilingcommons.model.Party;
import ca.bc.gov.open.jag.efilingcommons.submission.EfilingStatusService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("FilePackageServiceImplTest")
public class FilingPackageServiceImplTest {

    public static final String CLASS_DESCRIPTION = "CLASS_DESCRIPTION";
    public static final String COURT_CLASS = "COURT_CLASS";
    public static final String DIVISION = "DIVISION";
    public static final String FILE_NUMBER = "FILE_NUMBER";
    public static final String LEVEL = "LEVEL";
    public static final String LEVEL_DESCRIPTION = "LEVEL_DESCRIPTION";
    public static final String LOCATION = "LOCATION";
    public static final String LOCATION_DESCRIPTION = "LOCATION_DESCRIPTION";
    public static final String PARTICIPATING_CLASS = "PARTICIPATING_CLASS";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String MIDDLE_NAME = "MIDDLENAME";
    public static final String NAME_TYPE = "NAME_TYPE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String MIME_TYPE = "MIME_TYPE";
    public static final String FILE_NAME = "FILE_NAME";
    public static final String NAME = "NAME";
    public static final String SUB_TYPE = "SUB_TYPE";
    public static final String TYPE = "TYPE";
    public static final Object DATA = new Object();
    FilingPackageServiceImpl sut;

    @Mock
    EfilingStatusService efilingStatusServiceMock;

    @Mock
    AccountService accountServiceMock;

    @BeforeAll
    public void beforeAll() {

        MockitoAnnotations.openMocks(this);

        Mockito.when(accountServiceMock.getCsoAccountDetails(ArgumentMatchers.eq(TestHelpers.CASE_1))).thenReturn(createAccount(BigDecimal.ONE));

        Mockito.when(accountServiceMock.getCsoAccountDetails(ArgumentMatchers.eq(TestHelpers.CASE_2))).thenReturn(createAccount(null));

        sut = new FilingPackageServiceImpl(efilingStatusServiceMock, accountServiceMock, new FilingPackageMapperImpl());
    }

    @Test
    @DisplayName("Ok: a filing package was returned")
    public void withValidRequestReturnFilingPackage() {

        Mockito.when(efilingStatusServiceMock.findStatusByPackage(ArgumentMatchers.any())).thenReturn(Optional.of(createFilingPackage()));

        Optional<FilingPackage> result = sut.getCSOFilingPackage(TestHelpers.CASE_1, BigDecimal.ONE);

        Assertions.assertTrue(result.isPresent());
        //Filing Package
        Assertions.assertEquals(BigDecimal.ONE, result.get().getSubmissionFeeAmount());
        //Court
        Assertions.assertEquals(BigDecimal.ONE, result.get().getCourt().getAgencyId());
        Assertions.assertEquals(CLASS_DESCRIPTION, result.get().getCourt().getClassDescription());
        Assertions.assertEquals(COURT_CLASS, result.get().getCourt().getCourtClass());
        Assertions.assertEquals(DIVISION, result.get().getCourt().getDivision());
        Assertions.assertEquals(FILE_NUMBER, result.get().getCourt().getFileNumber());
        Assertions.assertEquals(LEVEL, result.get().getCourt().getLevel());
        Assertions.assertEquals(LEVEL_DESCRIPTION, result.get().getCourt().getLevelDescription());
        Assertions.assertEquals(LOCATION,result.get().getCourt().getLocation());
        Assertions.assertEquals(LOCATION_DESCRIPTION, result.get().getCourt().getLocationDescription());
        Assertions.assertEquals(PARTICIPATING_CLASS, result.get().getCourt().getParticipatingClass());
        //Party
        Assertions.assertEquals(1, result.get().getParties().size());
        Assertions.assertEquals(FIRST_NAME, result.get().getParties().get(0).getFirstName());
        Assertions.assertEquals(LAST_NAME, result.get().getParties().get(0).getLastName());
        Assertions.assertEquals(MIDDLE_NAME, result.get().getParties().get(0).getMiddleName());
        //Document
        Assertions.assertEquals(1, result.get().getDocuments().size());
        Assertions.assertEquals(DATA, result.get().getDocuments().get(0).getData());
        Assertions.assertEquals(DESCRIPTION, result.get().getDocuments().get(0).getDescription());
        Assertions.assertFalse(result.get().getDocuments().get(0).getIsAmendment());
        Assertions.assertTrue(result.get().getDocuments().get(0).getIsSupremeCourtScheduling());
        Assertions.assertEquals(MIME_TYPE, result.get().getDocuments().get(0).getMimeType());
        Assertions.assertEquals(NAME, result.get().getDocuments().get(0).getName());
        Assertions.assertEquals(BigDecimal.ONE, result.get().getDocuments().get(0).getStatutoryFeeAmount());
    }

    @Test
    @DisplayName("Not found: missing account")
    public void withValidRequestButMissingAccountReturnEmpty() {
        Optional<FilingPackage> result = sut.getCSOFilingPackage(TestHelpers.CASE_2, BigDecimal.ONE);

        Assertions.assertFalse(result.isPresent());
    }


    @Test
    @DisplayName("Not found: no filing package")
    public void withValidRequestButMissingPackageReturnEmpty() {

        Mockito.when(efilingStatusServiceMock.findStatusByPackage(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Optional<FilingPackage> result = sut.getCSOFilingPackage(TestHelpers.CASE_1, BigDecimal.TEN);

        Assertions.assertFalse(result.isPresent());
    }

    private AccountDetails createAccount(BigDecimal clientId) {
        return AccountDetails.builder()
                .fileRolePresent(true)
                .accountId(BigDecimal.ONE)
                .clientId(clientId)
                .cardRegistered(true)
                .universalId(UUID.randomUUID())
                .internalClientNumber(null)
                .universalId(TestHelpers.CASE_1).create();
    }

    private ca.bc.gov.open.jag.efilingcommons.submission.models.FilingPackage createFilingPackage() {
        return new ca.bc.gov.open.jag.efilingcommons.submission.models.FilingPackage(
                BigDecimal.ONE,
                createCourt(),
                Collections.singletonList(createDocument()),
                Collections.singletonList(createParty()),
                false,
                "APPLICATION_CODE"
        );
    }

    private Court createCourt() {
        return Court.builder()
                .agencyId(BigDecimal.ONE)
                .classDescription(CLASS_DESCRIPTION)
                .courtClass(COURT_CLASS)
                .division(DIVISION)
                .fileNumber(FILE_NUMBER)
                .level(LEVEL)
                .levelDescription(LEVEL_DESCRIPTION)
                .location(LOCATION)
                .locationDescription(LOCATION_DESCRIPTION)
                .participatingClass(PARTICIPATING_CLASS)
                .create();
    }

    private Party createParty() {
        return Party.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .middleName(MIDDLE_NAME)
                .nameTypeCd(NAME_TYPE)
                .partyTypeCd(ca.bc.gov.open.jag.efilingapi.api.model.Party.PartyTypeEnum.IND.getValue())
                .roleTypeCd(ca.bc.gov.open.jag.efilingapi.api.model.Party.RoleTypeEnum.ABC.getValue())
                .create();
    }

    private Document createDocument() {
        return Document.builder()
                .data(DATA)
                .description(DESCRIPTION)
                .isAmendment(Boolean.FALSE)
                .isSupremeCourtScheduling(Boolean.TRUE)
                .mimeType(MIME_TYPE)
                .name(NAME)
                .serverFileName(FILE_NAME)
                .statutoryFeeAmount(BigDecimal.ONE)
                .subType(SUB_TYPE)
                .type(DocumentProperties.TypeEnum.AAB.getValue())
                .create();
    }
}
