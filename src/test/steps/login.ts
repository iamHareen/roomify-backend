import { Given, When, Then } from "@cucumber/cucumber";
import { chromium, Browser, Page } from "@playwright/test";

let browser: Browser;
let page: Page;
let url: string = "https://demo.guru99.com/test/newtours/";

Given('provide valid url', async function () {
  browser = await chromium.launch({ headless: false });
  page = await browser.newPage();
  await page.goto(url);
});

When('provide valid username and password', async function () {
  await page.locator('[name="userName"]').fill("mercury");
  await page.locator('[name="password"]').fill("mercury");
});

Then('click on login button', async function () {
  await page.locator('[name="submit"]').click();
  await page.waitForTimeout(2000);
  await page.close();
  await browser.close();
});

When('provide valid username as {string} and password as {string}', async function (name, password) {
    await page.locator('[name="userName"]').fill(name);
    await page.locator('[name="password"]').fill(password);
  }
);
