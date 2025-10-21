package scripts;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.auth.LoginPage;
import pages.signout.SignoutPage;
import utils.ExcelReader;

public class SignoutTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(SignoutTest.class);

    String filePath = "src/test/resources/Data.xlsx";
    String sheetName = "data1";

    @DataProvider(name = "Data")
    public Object[][] getData() {
        return ExcelReader.getData(filePath, sheetName);
    }

    @Test(dataProvider = "Data", priority = 1)
    public void testLoginAndSignout(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Test đăng nhập và đăng xuất với email: {}", email);

        try {
            SignoutPage signoutPage = new SignoutPage(driver);

            // --- Mở modal login ---
            signoutPage.openLoginModal();

            // --- Login ---
            LoginPage loginPage = new LoginPage(driver);
            loginPage.login(email, password);
            logger.info("Đăng nhập thành công với user: {}", email);

            // --- Mở menu user và click Sign out ---
            signoutPage.openUserMenu();
            signoutPage.clickSignOut();

            // --- Kiểm tra đăng xuất thành công ---
            boolean loggedOut = signoutPage.verifyLoggedOut();
            Assert.assertTrue(loggedOut, "Người dùng chưa đăng xuất hoàn toàn!");
            logger.info("Đăng xuất thành công.");

        } catch (Exception e) {
            logger.error("Test thất bại: {}", e.getMessage(), e);
            Assert.fail("Lỗi khi test đăng xuất: " + e.getMessage());
        }
    }
}
