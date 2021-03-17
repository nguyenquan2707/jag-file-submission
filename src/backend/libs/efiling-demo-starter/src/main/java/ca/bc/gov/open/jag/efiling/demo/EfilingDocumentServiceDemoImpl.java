package ca.bc.gov.open.jag.efiling.demo;

import ca.bc.gov.open.jag.efilingcommons.model.DocumentTypeDetails;
import ca.bc.gov.open.jag.efilingcommons.service.EfilingDocumentService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class EfilingDocumentServiceDemoImpl implements EfilingDocumentService {
    @Override
    public DocumentTypeDetails getDocumentTypeDetails(String courtLevel, String courtClass, String documentType) {
        return new DocumentTypeDetails("This is a doc", documentType, BigDecimal.valueOf(77), true, true, false);
    }

    @Override
    public List<DocumentTypeDetails> getDocumentTypes(String courtLevel, String courtClass) {
        return Arrays.asList(new DocumentTypeDetails("Description1", "AFF", BigDecimal.valueOf(77),true, true, false), new DocumentTypeDetails("Description2", "Type2", BigDecimal.valueOf(77),true, true, false));
    }
}
