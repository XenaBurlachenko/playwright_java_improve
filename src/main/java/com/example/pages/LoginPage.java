package com.example.pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;
    
    // Локаторы
    private final String usernameInput = "#username";
    private final String passwordInput = "#password";
    private final String loginButton = "button[type='submit']";
    private final String flashMessage = "#flash";
    private final String secureAreaHeader = "h2";
    private final String logoutButton = "a.button[href='/logout']";
    
    // URL
    private final String baseUrl = "https://the-internet.herokuapp.com";
    private final String loginUrl = baseUrl + "/login";
    private final String secureUrl = baseUrl + "/secure";
    
    public LoginPage(Page page) {
        this.page = page;
    }
    
    public void navigate() {
        page.navigate(loginUrl);
    }
    
    public void login(String username, String password) {
        page.fill(usernameInput, username);
        page.fill(passwordInput, password);
        page.click(loginButton);
    }
    
    public String getFlashMessage() {
        return page.locator(flashMessage).textContent().replace("×", "").trim();
    }
    
    public boolean isFlashMessageContains(String text) {
        return getFlashMessage().contains(text);
    }
    
    public void navigateToSecureArea() {
        page.navigate(secureUrl);
    }
    
    public String getSecureAreaHeader() {
        // Исправлено: добавляем trim() для удаления лишних пробелов
        return page.locator(secureAreaHeader).textContent().trim();
    }
    
    public boolean isLogoutButtonVisible() {
        return page.locator(logoutButton).isVisible();
    }
    
    public void logout() {
        page.locator(logoutButton).click();
    }
}