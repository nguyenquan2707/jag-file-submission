package ca.bc.gov.open.jag.efiling.stepDefinitions;

import ca.bc.gov.open.jag.efiling.Keys;
import ca.bc.gov.open.jag.efiling.helpers.FileDownloadHelper;
import ca.bc.gov.open.jag.efiling.page.PackageReviewPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class viewAndDownloadReceiptSD {

    private final PackageReviewPage packageReviewPage;

    private final Logger logger = LoggerFactory.getLogger(viewAndDownloadReceiptSD.class);

    public viewAndDownloadReceiptSD(PackageReviewPage packageReviewPage) {
        this.packageReviewPage = packageReviewPage;
    }

    @And("payment status tab is available")
    public void paymentStatusTabIsPresent() {
        packageReviewPage.clickPaymentStatusTab();
        Assert.assertTrue(packageReviewPage.verifyPaymentStatusIsDisplayed());
    }

    @Then("payment receipt can be downloaded")
    public void downloadPaymentReceipt() throws InterruptedException {
        packageReviewPage.clickToDownloadReceipt();

        FileDownloadHelper fileDownloadHelper = new FileDownloadHelper();
        File downloadedFile = fileDownloadHelper.downloadFile(Keys.BASE_PATH + Keys.DOWNLOADED_FILES_PATH);

        logger.info("Downloaded file name is: {}", downloadedFile.getName());
        Assert.assertEquals("PaymentReceipt.pdf", downloadedFile.getName());

        Assert.assertTrue(downloadedFile.length() > 0);
        logger.info("Files successfully downloaded");

        logger.info("Files deleted after validation: {}", downloadedFile.delete());
        Assert.assertEquals(0, downloadedFile.length());
    }
}
