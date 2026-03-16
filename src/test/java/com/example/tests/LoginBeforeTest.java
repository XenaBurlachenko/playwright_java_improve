package com.example.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class LoginBeforeTest {
    
    private Playwright playwright;
    private Browser browser;
    private Page page;
    
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true));
        page = browser.newPage();
    }
    
    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
    
    @Test
    void testValidLogin() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("#username", "tomsmith");
        page.fill("#password", "SuperSecretPassword!");
        page.click("button[type='submit']");
        
        String flashMessage = page.locator("#flash").textContent();
        Assertions.assertTrue(flashMessage.contains("You logged into a secure area!"));
    }
    
    @Test
    void testInvalidPassword() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("#username", "tomsmith");
        page.fill("#password", "wrongpassword");
        page.click("button[type='submit']");
        
        String flashMessage = page.locator("#flash").textContent();
        Assertions.assertTrue(flashMessage.contains("Your password is invalid!"));
    }
    
    @Test
    void testInvalidUsername() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("#username", "wronguser");
        page.fill("#password", "SuperSecretPassword!");
        page.click("button[type='submit']");
        
        String flashMessage = page.locator("#flash").textContent();
        Assertions.assertTrue(flashMessage.contains("Your username is invalid!"));
    }
    
    @Test
    void testEmptyFields() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("#username", "");
        page.fill("#password", "");
        page.click("button[type='submit']");
        
        String flashMessage = page.locator("#flash").textContent();
        Assertions.assertTrue(flashMessage.contains("Your username is invalid!"));
    }
}