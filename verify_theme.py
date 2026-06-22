import time
from playwright.sync_api import sync_playwright

def run_cuj(page):
    # Wait for the server to be up
    for _ in range(10):
        try:
            page.goto("http://localhost:55000/shared_shop/")
            break
        except Exception:
            time.sleep(2)

    page.wait_for_timeout(1000)

    # Take screenshot at the key moment (Top page)
    page.screenshot(path="/home/jules/verification/screenshots/verification.png")
    page.wait_for_timeout(2000)  # Hold final state for the video

if __name__ == "__main__":
    import os
    os.makedirs("/home/jules/verification/videos", exist_ok=True)
    os.makedirs("/home/jules/verification/screenshots", exist_ok=True)
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            record_video_dir="/home/jules/verification/videos"
        )
        page = context.new_page()
        try:
            run_cuj(page)
        finally:
            context.close()  # MUST close context to save the video
            browser.close()
