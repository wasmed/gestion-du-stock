import time
from playwright.sync_api import sync_playwright, expect

def verify_stock_view():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context()
        page = context.new_page()

        try:
            print("Navigating to http://localhost:8080/stock/list")
            page.goto("http://localhost:8080/stock/list", timeout=30000)

            # 1. Handle Login
            if "login" in page.url:
                print("Redirected to login, logging in...")
                # We need valid credentials. Based on traces and previous context, "admin@resto.com" or "chef@resto.com" might work.
                # Let's try to find a known user from DB initialization if possible, or try common defaults.
                # The trace showed "serveur@resto.com" being used in tests. Let's try that or admin.
                # "admin@resto.com" seems standard for admin tasks.

                # Check if elements exist before filling
                if page.locator("input[name='username']").count() > 0:
                    page.fill("input[name='username']", "admin@resto.com")
                    page.fill("input[name='password']", "password") # Default password in many tutorials, might fail.
                    page.click("button[type='submit']")
                else:
                    print("Login form not found on login page?")

                # Wait for navigation
                page.wait_for_load_state("networkidle")

                # If login failed (still on login page or error param)
                if "error" in page.url or "login" in page.url:
                    print("Login failed with admin@resto.com. Trying chef@resto.com...")
                    page.fill("input[name='username']", "chef@resto.com")
                    page.fill("input[name='password']", "password")
                    page.click("button[type='submit']")
                    page.wait_for_load_state("networkidle")

            # 2. Check current URL
            print(f"Current URL: {page.url}")

            # 3. Take Screenshot
            screenshot_path = "/home/jules/verification/stock_list_grouped.png"
            page.screenshot(path=screenshot_path, full_page=True)
            print(f"Screenshot saved to {screenshot_path}")

        except Exception as e:
            print(f"Error: {e}")
            page.screenshot(path="/home/jules/verification/error_screenshot.png")

        finally:
            browser.close()

if __name__ == "__main__":
    verify_stock_view()
