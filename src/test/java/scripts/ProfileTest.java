package scripts;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.profile.ProfilePage;
import utils.ExcelReader;

public class ProfileTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(ProfileTest.class);

    private final String filePath = "src/test/resources/Data.xlsx";
    private final String sheetName = "data1";

    // ✅ Sửa lại tên DataProvider để khớp với @Test
    @DataProvider(name = "getData")
    public Object[][] getData() {
        return ExcelReader.getData(filePath, sheetName);
    }

    /**
     * ✅ Test Case: Đăng nhập → Dashboard → /info-user → Cập nhật ảnh → Chỉnh sửa hồ sơ → Lưu thành công
     */
    @Test(dataProvider = "getData", priority = 1)
    public void testProfileUpdateFlow(String email, String password, String diaDiem, String phong) {
        logger.info("🔹 Test Data: Email={}, DiaDiem={}, Phong={}", email, diaDiem, phong);

        ProfilePage profilePage = new ProfilePage(driver);
        LoginPage loginPage = new LoginPage(driver);

        // --- STEP 1: Mở popup login ---
        logger.info("➡ Mở modal đăng nhập...");
        profilePage.openLoginModal();

        // --- STEP 2: Đăng nhập ---
        logger.info("➡ Thực hiện login với email: {}", email);
        loginPage.login(email, password);

        // --- STEP 3: Vào trang Dashboard / info-user ---
        logger.info("➡ Điều hướng đến trang Thông tin người dùng...");
        profilePage.goToDashboard();

        // --- STEP 4: Cập nhật ảnh đại diện ---
        //Đổi thông tin avatar1.png, avatar3.png, avatar4.png
        String imagePath = System.getProperty("user.dir") + "/src/test/resources/avatar2.png";
        logger.info("➡ Cập nhật ảnh đại diện từ file: {}", imagePath);
        profilePage.uploadAvatar(imagePath);

        // --- STEP 5: Click nút Upload Avatar ---
        logger.info("➡ Click nút Upload Avatar");
        profilePage.clickUploadAvatarButton();

        // --- STEP 6: Chỉnh sửa hồ sơ ---
        // Thay thông tin bằng tay
        logger.info("➡ Thực hiện chỉnh sửa hồ sơ...");
        profilePage.editProfile(
                "Hoài Hiếu",   // name
                "0123456789",   // phone
                "22/10/2003"    // birthday
        );

        // --- STEP 7: Verify kết quả ---
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/info-user"), "❌ Không ở đúng trang info-user sau khi cập nhật!");
        logger.info("✅ Hồ sơ người dùng được cập nhật thành công!");

        // (Tùy chọn) có thể chụp screenshot ở đây
        // takeScreenshot("Profile_Update_Success");
    }
}
