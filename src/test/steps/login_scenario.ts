import { Given, When, Then } from "@cucumber/cucumber";
import { chromium, Browser, Page, expect } from "@playwright/test";

let browser: Browser;
let page: Page;
let url: string = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

Given('provide valid urls', async function () {
  browser = await chromium.launch({ headless: false });
  page = await browser.newPage();
  await page.goto(url);
});

When('enter username as {string}', async function (name) {
  await page.locator("//input[@placeholder='Username']").fill(name);
});

When('enter password as {string}', async function (password) {
    await page.locator("//input[@placeholder='Password']").fill(password);
  });

Then('click on submit button', async function () {
  await page.locator("//button[@type='submit']").click();
  await page.waitForTimeout(2000);
});

Then('verify login success message as {string}', async function (message) {
    const msg = await page.locator("//span[@class='oxd-text oxd-text--span oxd-main-menu-item--name'][normalize-space()='Dashboard']").textContent()
    if(msg == message) {
      expect(msg).toEqual(message)
      await page.close();
      await browser.close();
    } else {
      const msg = await page.locator("//p[@text()='Invalid credentials']").textContent()
      expect(msg).toEqual(message)
      await page.close();
      await browser.close();
    }
  }
);
