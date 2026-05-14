from playwright.sync_api import sync_playwright

def run_cuj(page):
    page.goto("http://localhost:8080/login")
    page.wait_for_timeout(2000)

    # Login as admin
    page.locator("input[name='username']").fill("admin@resto.com")
    page.wait_for_timeout(500)
    page.locator("input[name='password']").fill("admin123")
    page.wait_for_timeout(500)
    page.get_by_role("button", name="Se connecter").click()
    page.wait_for_timeout(2000)

    # Setup alert handler BEFORE clicking the button that triggers it
    page.on("dialog", lambda dialog: dialog.accept())

    # Navigate directly to the report page
    page.goto("http://localhost:8080/admin/z-report")
    page.wait_for_timeout(2000)

    # Take screenshot at the key moment
    page.screenshot(path="/home/jules/verification/screenshots/verification.png")
    page.wait_for_timeout(2000)

if __name__ == "__main__":
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            record_video_dir="/home/jules/verification/videos"
        )
        page = context.new_page()
        try:
            run_cuj(page)
        finally:
            context.close()
            browser.close()
