from playwright.sync_api import sync_playwright

def run_cuj(page):
    # 1. Start on the login page
    page.goto("http://localhost:8080/login")
    page.wait_for_timeout(500)

    # 2. Login as Client
    page.fill("input[name='username']", "client@resto.com")
    page.wait_for_timeout(500)
    page.fill("input[name='password']", "client123")
    page.wait_for_timeout(500)
    page.click("button[type='submit']")
    page.wait_for_timeout(500)

    # 3. Add an item to the cart
    page.goto("http://localhost:8080/client/menu")
    page.wait_for_timeout(500)

    # Try adding the first available item to cart
    add_buttons = page.locator("form[action='/client/add-to-cart'] button[type='submit']")
    if add_buttons.count() > 0:
        add_buttons.first.click()
        page.wait_for_timeout(500)

    # 4. Go to the cart
    page.goto("http://localhost:8080/client/cart")
    page.wait_for_timeout(500)
    page.screenshot(path="/app/verification/screenshots/client_cart.png")
    page.wait_for_timeout(500)

    # 5. Submit the cart WITHOUT selecting a table (Sur place is checked by default)
    page.click("button:has-text('Envoyer au serveur')")
    page.wait_for_timeout(500)

    # 6. Logout client and login as Serveur
    page.goto("http://localhost:8080/logout")
    page.wait_for_timeout(500)

    page.goto("http://localhost:8080/login")
    page.wait_for_timeout(500)
    page.fill("input[name='username']", "serveur@resto.com")
    page.wait_for_timeout(500)
    page.fill("input[name='password']", "serveur123")
    page.wait_for_timeout(500)
    page.click("button[type='submit']")
    page.wait_for_timeout(500)

    # 7. Check server dashboard for the order
    page.goto("http://localhost:8080/orders")
    page.wait_for_timeout(1000)

    # Validate the form has the select element for table assignment
    page.screenshot(path="/app/verification/screenshots/serveur_dashboard.png")
    page.wait_for_timeout(500)

    # Pick a table from the select
    selects = page.locator("select[name='tableId']")
    if selects.count() > 0:
        # Select the second option (first option is disabled)
        selects.first.select_option(index=1)
        page.wait_for_timeout(500)

        # Click validate
        page.locator("button:has-text('Valider')").first.click()
        page.wait_for_timeout(1000)

if __name__ == "__main__":
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            record_video_dir="/app/verification/videos"
        )
        page = context.new_page()
        try:
            run_cuj(page)
        finally:
            context.close()  # MUST close context to save the video
            browser.close()
