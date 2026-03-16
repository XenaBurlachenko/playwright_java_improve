package com.example.tests;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.pages.LoginPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class LoginAfterTest {
    
    private Playwright playwright;
    private Browser browser;
    private Page page;
    private LoginPage loginPage;
    
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true));
        page = browser.newPage();
        loginPage = new LoginPage(page);
    }
    
    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
    
    @ParameterizedTest(name = "Логин: {0}, Пароль: {1} -> Ожидаем: {2}")
    @CsvSource({
        "tomsmith, SuperSecretPassword!, You logged into a secure area!",
        "tomsmith, wrongpassword, Your password is invalid!",
        "wronguser, SuperSecretPassword!, Your username is invalid!",
        " '', '', Your username is invalid!"
    })
    void testLoginScenarios(String username, String password, String expectedMessage) {
        // Открываем страницу логина
        loginPage.navigate();
        
        // Выполняем вход
        loginPage.login(username, password);
        
        // Проверяем сообщение
        Assertions.assertTrue(
            loginPage.isFlashMessageContains(expectedMessage),
            "Ожидаемое сообщение: '" + expectedMessage + "', получено: '" + loginPage.getFlashMessage() + "'"
        );
    }
    
    @Test
    void testSuccessfulLoginThenLogout() {
        // Успешный вход
        loginPage.navigate();
        loginPage.login("tomsmith", "SuperSecretPassword!");
        
        // Проверяем, что попали в secure area
        Assertions.assertTrue(loginPage.isFlashMessageContains("You logged into a secure area!"));
        
        // Переходим на secure area и проверяем наличие кнопки logout
        loginPage.navigateToSecureArea();
        Assertions.assertTrue(loginPage.isLogoutButtonVisible());
        Assertions.assertEquals("Secure Area", loginPage.getSecureAreaHeader());
        
        // Выполняем logout
        loginPage.logout();
        
        // Проверяем, что вернулись на страницу логина
        Assertions.assertTrue(loginPage.isFlashMessageContains("You logged out of the secure area!"));
    }
    
    @ParameterizedTest
    @MethodSource("invalidCredentialsProvider")
    void testMultipleInvalidCredentials(String username, String password) {
        loginPage.navigate();
        loginPage.login(username, password);
        
        Assertions.assertTrue(
            loginPage.getFlashMessage().contains("invalid"),
            "Должно быть сообщение об ошибке для логина: " + username
        );
    }
    
    private static Stream<Arguments> invalidCredentialsProvider() {
        return Stream.of(
            Arguments.of("admin", "admin"),
            Arguments.of("tomsmith", "12345"),
            Arguments.of("user", "SuperSecretPassword!"),
            Arguments.of("test", "test"),
            Arguments.of("", "SuperSecretPassword!"),
            Arguments.of("tomsmith", "")
        );
    }
}
