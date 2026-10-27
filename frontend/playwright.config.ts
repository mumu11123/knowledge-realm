import { defineConfig, devices } from '@playwright/test'
export default defineConfig({
  testDir: './tests/e2e',
  fullyParallel: false,
  retries: 0,
  timeout: 30000,
  use: { baseURL: 'http://127.0.0.1:5173', trace: 'retain-on-failure' },
  projects: [
    {
      name: 'mobile',
      use: {
        ...devices['iPhone 13'],
        defaultBrowserType: 'chromium',
        browserName: 'chromium',
        channel: process.env.PLAYWRIGHT_CHANNEL || undefined,
      },
    },
    {
      name: 'small-mobile',
      use: {
        viewport: { width: 320, height: 760 },
        browserName: 'chromium',
        channel: process.env.PLAYWRIGHT_CHANNEL || undefined,
      },
    },
    {
      name: 'desktop',
      use: {
        viewport: { width: 1440, height: 1000 },
        browserName: 'chromium',
        channel: process.env.PLAYWRIGHT_CHANNEL || undefined,
      },
    },
  ],
  webServer: {
    command: 'npm run dev',
    url: 'http://127.0.0.1:5173',
    reuseExistingServer: !process.env.CI,
  },
})
