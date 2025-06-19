package ru.glebova.NauJava.UI;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

class LogoutTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Daria\\.cache\\selenium\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();

        // Предварительный вход в систему
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button.btn-login")).click();
        wait.until(ExpectedConditions.urlContains("/home"));
    }

    @Test
    void logoutUrlTest() {
        driver.get(BASE_URL + "/logout");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.logout-card")));

        Assertions.assertEquals("Выход из системы",
                driver.findElement(By.tagName("h3")).getText());
    }

    @Test
    void logoutSuccessfulTest() {
        driver.get(BASE_URL + "/logout");
        driver.findElement(By.cssSelector("button.btn-logout")).click();

        wait.until(ExpectedConditions.urlContains("/login")); // Ждем смены URL

        Assertions.assertEquals("Вход в школьный дневник",
                driver.findElement(By.cssSelector("h2.text-center")).getText());
    }

    @Test
    void logoutCancelHomePageTest() {
        testCancelLogoutAnyPage("/home");
    }

    private void testCancelLogoutAnyPage(String pageUrl) {
        driver.get(BASE_URL + pageUrl);
        driver.findElement(By.cssSelector("button.btn-logout")).click();

        wait.until(ExpectedConditions.urlContains("/logout"));

        driver.findElement(By.cssSelector("a.btn-cancel")).click();

        Assertions.assertTrue(driver.getCurrentUrl().contains(pageUrl));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}