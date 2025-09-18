package scripts;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.BookingPage;
import pages.LoginPage;

public class BookingTest extends BaseTest {

    @Test
    public void testBookingRoom() {
        BookingPage bookingPage = new BookingPage(driver);

        bookingPage.clickDiaDiem();
        bookingPage.clickPhong();

        bookingPage.openLoginModal();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("blue299@gmail.com", "blue299");

        bookingPage.clickBooking();

    }
}
