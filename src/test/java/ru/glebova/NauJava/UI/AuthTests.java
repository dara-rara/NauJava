package ru.glebova.NauJava.UI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthTests {

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

        assertEquals("Вход в школьный дневник",
                driver.findElement(By.cssSelector("h2.text-center")).getText());
    }

    @Test
    void loginSuccessfullyTest() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("user");
        driver.findElement(By.cssSelector("button.btn-login")).click();
        wait.until(ExpectedConditions.urlContains("/home"));

        assertEquals("Вы успешно вошли в электронную систему \"Школьный дневник\"",
                driver.findElement(By.cssSelector("p.mt-3")).getText());
    }

    @Test
    void logoutUrlTest() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("user");
        driver.findElement(By.cssSelector("button.btn-login")).click();
        wait.until(ExpectedConditions.urlContains("/home"));

        driver.get(BASE_URL + "/logout");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.logout-card")));

        assertEquals("Выход из системы", driver.findElement(By.tagName("h3")).getText());
    }

    @Test
    void logoutCancelHomePageTest() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("user");
        driver.findElement(By.cssSelector("button.btn-login")).click();
        wait.until(ExpectedConditions.urlContains("/home"));

        driver.findElement(By.cssSelector("button.btn-logout")).click();
        wait.until(ExpectedConditions.urlContains("/logout"));
        driver.findElement(By.cssSelector("a.btn-cancel")).click();

        assertTrue(driver.getCurrentUrl().contains("/home"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}