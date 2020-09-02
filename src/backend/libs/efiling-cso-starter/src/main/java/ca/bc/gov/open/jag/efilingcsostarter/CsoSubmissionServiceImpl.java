package ca.bc.gov.open.jag.efilingcsostarter;

import ca.bc.gov.ag.csows.filing.NestedEjbException_Exception;
import ca.bc.gov.ag.csows.filing.ProcessItemStatus;
import ca.bc.gov.ag.csows.filing.*;
import ca.bc.gov.ag.csows.services.*;
import ca.bc.gov.open.jag.efilingcommons.exceptions.EfilingSubmissionServiceException;
import ca.bc.gov.open.jag.efilingcommons.model.FilingPackage;
import ca.bc.gov.open.jag.efilingcommons.model.*;
import ca.bc.gov.open.jag.efilingcommons.service.EfilingPaymentService;
import ca.bc.gov.open.jag.efilingcommons.service.EfilingSubmissionService;
import ca.bc.gov.open.jag.efilingcommons.utils.DateUtils;
import ca.bc.gov.open.jag.efilingcsostarter.config.CsoProperties;
import ca.bc.gov.open.jag.efilingcsostarter.mappers.*;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsoSubmissionServiceImpl implements EfilingSubmissionService {

    private final FilingFacadeBean filingFacadeBean;
    private final ServiceFacadeBean serviceFacadeBean;
    private final ServiceMapper serviceMapper;
    private final FilingPackageMapper filingPackageMapper;
    private final FinancialTransactionMapper financialTransactionMapper;
    private final CsoProperties csoProperties;
    private final DocumentMapper documentMapper;
    private final CsoPartyMapper csoPartyMapper;
    private final PackageAuthorityMapper packageAuthorityMapper;

    public CsoSubmissionServiceImpl(FilingFacadeBean filingFacadeBean,
                                    ServiceFacadeBean serviceFacadeBean,
                                    ServiceMapper serviceMapper,
                                    FilingPackageMapper filingPackageMapper,
                                    FinancialTransactionMapper financialTransactionMapper,
                                    CsoProperties csoProperties,
                                    DocumentMapper documentMapper, CsoPartyMapper csoPartyMapper, PackageAuthorityMapper packageAuthorityMapper) {
        this.filingFacadeBean = filingFacadeBean;
        this.serviceFacadeBean = serviceFacadeBean;
        this.serviceMapper = serviceMapper;
        this.filingPackageMapper = filingPackageMapper;
        this.financialTransactionMapper = financialTransactionMapper;
        this.csoProperties = csoProperties;
        this.documentMapper = documentMapper;
        this.csoPartyMapper = csoPartyMapper;
        this.packageAuthorityMapper = packageAuthorityMapper;
    }

    @Override
    public SubmitPackageResponse submitFilingPackage(
            AccountDetails accountDetails,
            FilingPackage efilingPackage,
            String applicationTypeCode,
            boolean isRushedProcessing,
            EfilingPaymentService paymentService) {

        if (accountDetails == null) throw new IllegalArgumentException("Account Details are required");
        if (accountDetails.getClientId() == null) throw new IllegalArgumentException("Client id is required.");
        if (efilingPackage == null) throw new IllegalArgumentException("EfilingPackage is required.");
        if (StringUtils.isBlank(applicationTypeCode)) throw new IllegalArgumentException("Application Type code is required.");

        ServiceSession serviceSession = getServiceSession(accountDetails.getClientId().toString());

        Service createdService = createEfilingService(efilingPackage, accountDetails, serviceSession);

        updatePaymentForService(
                createdService,
                true,
                createPayment(paymentService, createdService, efilingPackage.getSubmissionFeeAmount(), accountDetails.getInternalClientNumber()));

        ca.bc.gov.ag.csows.filing.FilingPackage csoFilingPackage = buildFilingPackage(accountDetails, efilingPackage, createdService, applicationTypeCode);

        if (isRushedProcessing) {
            csoFilingPackage.setProcRequest(buildRushedOrderRequest(accountDetails));
        }

        BigDecimal filingResult = filePackage(csoFilingPackage);

        updateServiceComplete(createdService);

        return SubmitPackageResponse
                .builder()
                .packageLink(MessageFormat
                        .format("{0}/cso/accounts/bceidNotification.do?packageNo={1}", csoProperties.getCsoBasePath(), filingResult.toPlainString()))
                .transactionId(filingResult)
                .create();
    }

    private ca.bc.gov.ag.csows.filing.FilingPackage buildFilingPackage(AccountDetails accountDetails, FilingPackage efilingPackage, Service createdService, String applicationTypeCode) {
        XMLGregorianCalendar submittedDate = getComputedSubmittedDate(efilingPackage.getCourt().getLocation());
        return filingPackageMapper.toFilingPackage(
                        efilingPackage,
                        accountDetails,
                        applicationTypeCode,
                        createdService.getServiceId(),
                        submittedDate,
                        buildCivilDocuments(accountDetails, efilingPackage, submittedDate),
                        buildCsoParties(accountDetails, efilingPackage),
                        buildPackageAuthorities(accountDetails));
    }

    private List<PackageAuthority> buildPackageAuthorities(AccountDetails accountDetails) {

        return Arrays.asList(packageAuthorityMapper.toPackageAuthority(accountDetails));

    }

    private List<CsoParty> buildCsoParties(AccountDetails accountDetails, FilingPackage efilingPackage) {

        List<CsoParty> csoParties = new ArrayList<>();

        for (int i = 0; i < efilingPackage.getParties().size(); i++) {
            csoParties.add(csoPartyMapper.toEfilingParties(i + 1, efilingPackage.getParties().get(i), accountDetails));
        }

        return csoParties;
    }


    private List<CivilDocument> buildCivilDocuments(AccountDetails accountDetails, FilingPackage efilingPackage, XMLGregorianCalendar submittedDate) {

        List<CivilDocument> documents = new ArrayList<>();

        for (int i = 0; i < efilingPackage.getDocuments().size(); i++) {

            List<DocumentPayments> payments = Arrays.asList(documentMapper.toEfilingDocumentPayment(efilingPackage.getDocuments().get(i), accountDetails));
            List<Milestones> milestones = Arrays.asList(documentMapper.toActualSubmittedDate(accountDetails),
                    documentMapper.toComputedSubmittedDate(accountDetails, submittedDate));
            List<DocumentStatuses> statuses = Arrays.asList(documentMapper.toEfilingDocumentStatus(efilingPackage.getDocuments().get(i), accountDetails));

            documents.add(documentMapper.toEfilingDocument(
                    i + 1,
                    efilingPackage.getDocuments().get(i),
                    accountDetails,
                    efilingPackage,
                    csoProperties.getFileServerHost(),
                    milestones,
                    payments,
                    statuses
            ));
        }

        return documents;

    }

    private RushOrderRequest buildRushedOrderRequest(AccountDetails accountDetails) {
        RushOrderRequest processRequest = new RushOrderRequest();
        processRequest.setEntDtm(DateUtils.getCurrentXmlDate());
        processRequest.setEntUserId(accountDetails.getClientId().toString());
        processRequest.setRequestDt(DateUtils.getCurrentXmlDate());
        RushOrderRequestItem rushOrderRequestItem = new RushOrderRequestItem();
        rushOrderRequestItem.setEntDtm(DateUtils.getCurrentXmlDate());
        rushOrderRequestItem.setEntUserId(accountDetails.getClientId().toString());
        rushOrderRequestItem.setProcessReasonCd(Keys.RUSH_PROCESS_REASON_CD);
        rushOrderRequestItem.getItemStatuses().add(getProcessItemStatusRequest(accountDetails));
        rushOrderRequestItem.getItemStatuses().add(getProcessItemStatusApproved(accountDetails));
        processRequest.setItem(rushOrderRequestItem);
        return processRequest;
    }

    private ProcessItemStatus getProcessItemStatusRequest(AccountDetails accountDetails) {
        return getProcessItemStatus(accountDetails, Keys.REQUEST_PROCESS_STATUS_CD);
    }

    private ProcessItemStatus getProcessItemStatusApproved(AccountDetails accountDetails) {
        return getProcessItemStatus(accountDetails, Keys.APPROVED_PROCESS_STATUS_CD);
    }

    private ProcessItemStatus getProcessItemStatus(AccountDetails accountDetails, String proccessStatusCd) {
        ProcessItemStatus processItemStatus = new ProcessItemStatus();
        processItemStatus.setAccountId(accountDetails.getAccountId());
        processItemStatus.setClientId(accountDetails.getClientId());
        processItemStatus.setEntDtm(DateUtils.getCurrentXmlDate());
        processItemStatus.setEntUserId(accountDetails.getClientId().toString());
        processItemStatus.setProcessStatusCd(proccessStatusCd);
        return processItemStatus;
    }

    private String generateInvoiceNumber(String data) {

        try {
            return serviceFacadeBean.getNextInvoiceNumber(data);
        } catch (ca.bc.gov.ag.csows.services.NestedEjbException_Exception e) {
            throw new EfilingSubmissionServiceException("Exception while generating next invoice number", e.getCause());
        }

    }

    private ServiceSession getServiceSession(String clientId) {
        try {
            UserSession userSession = serviceFacadeBean.createUserSession(clientId);
            return serviceFacadeBean.createServiceSession(userSession, "request");
        } catch (ca.bc.gov.ag.csows.services.NestedEjbException_Exception e) {
            throw new EfilingSubmissionServiceException("Exception while getting user session", e.getCause());
        }
    }

    private Service createEfilingService(FilingPackage efilingPackage, AccountDetails accountDetails, ServiceSession serviceSession) {

        Service serviceToCreate = serviceMapper.toCreateService(efilingPackage, accountDetails, serviceSession);

        try {
            return serviceFacadeBean.addService(serviceToCreate);
        } catch (ca.bc.gov.ag.csows.services.NestedEjbException_Exception e) {
            throw new EfilingSubmissionServiceException("Exception while creating efiling service", e.getCause());
        }
    }

    private void updatePaymentForService(Service service, Boolean feePaid, FinancialTransaction financialTransaction) {

        service.setFeePaidYn(String.valueOf(feePaid));
        service.getTransactions().add(financialTransaction);

        try {
            serviceFacadeBean.updateService(service);
        } catch (ca.bc.gov.ag.csows.services.NestedEjbException_Exception e) {
            throw new EfilingSubmissionServiceException("Exception while updating payment on service", e.getCause());
        }

    }

    private FinancialTransaction createPayment(EfilingPaymentService paymentService, Service service, BigDecimal submissionFeeAmount, String internalClientNumber) {

        EfilingPayment efilingPayment = new EfilingPayment(service.getServiceId(), submissionFeeAmount, generateInvoiceNumber(Keys.INVOICE_PREFIX), internalClientNumber);
        PaymentTransaction payment = paymentService.makePayment(efilingPayment);
        return financialTransactionMapper.toTransaction(payment, service);

    }

    private BigDecimal filePackage(ca.bc.gov.ag.csows.filing.FilingPackage csoFilingPackage) {

        try {
            return filingFacadeBean.submitFiling(csoFilingPackage);
        } catch (NestedEjbException_Exception e) {
            throw new EfilingSubmissionServiceException("Exception while filing package", e.getCause());
        }

    }

    private void updateServiceComplete(Service service) {

        service.setServiceReceivedDtm(DateUtils.getCurrentXmlDate());
        try {
            serviceFacadeBean.updateService(service);
        } catch (ca.bc.gov.ag.csows.services.NestedEjbException_Exception e) {
            throw new EfilingSubmissionServiceException("Exception while updating payment on service", e.getCause());
        }

    }

    private XMLGregorianCalendar getComputedSubmittedDate(String location) {

        try {

            return filingFacadeBean.calculateSubmittedDate(DateUtils.getCurrentXmlDate(), location);
        } catch (NestedEjbException_Exception e) {
            throw new EfilingSubmissionServiceException("Exception while retrieving submitted date", e.getCause());
        }

    }

}
