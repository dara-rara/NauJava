package ru.glebova.NauJava.UI;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Daria\\.cache\\selenium\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    void loginUrlTest() {
        driver.get(BASE_URL + "/login");

        Assertions.assertEquals("Вход в школьный дневник",
                driver.findElement(By.cssSelector("h2.text-center")).getText());
    }

    @Test
    void loginSuccessfullyTest() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button.btn-login")).click();

        wait.until(ExpectedConditions.urlContains("/home")); // Ждем смены URL

        Assertions.assertEquals("Вы успешно вошли в школьный электронный дневник",
                driver.findElement(By.cssSelector("p.mt-3")).getText());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}