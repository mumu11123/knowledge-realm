import { test, expect } from '@playwright/test'
import AxeBuilder from '@axe-core/playwright'

test('related videos show original author links without unlocking unfinished lessons', async ({
  page,
}) => {
  await page.goto('/map')
  await expect(page.getByRole('heading', { name: '换个角度，用动画理解' })).toBeVisible()
  const profile = page.getByRole('link', { name: /UP主：蓝不过海呀/ })
  await expect(profile).toHaveAttribute('href', 'https://space.bilibili.com/401399175')
  const linear = page
    .locator('.knowledge-point')
    .filter({ has: page.getByRole('heading', { name: '顺序表与单链表', exact: true }) })
  await linear.locator('.related-videos > summary').click()
  await expect(linear.locator('.video-link')).toHaveCount(4)
  await expect(linear.locator('.video-link').first()).toHaveAttribute(
    'href',
    'https://www.bilibili.com/video/BV1CMQEBnE6o/',
  )
  await expect(linear.locator('.video-link').first()).toHaveAttribute('target', '_blank')
  await expect(linear.locator('.video-link').first()).toHaveAttribute('rel', 'noopener noreferrer')
  const incomplete = page
    .locator('.knowledge-point')
    .filter({ has: page.getByRole('heading', { name: '双链表与循环链表', exact: true }) })
  await expect(incomplete.getByText('站内故事待制作')).toBeVisible()
  await expect(incomplete.locator('.story-entry')).toHaveCount(0)
  expect(
    (await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa', 'wcag21aa']).analyze())
      .violations,
  ).toEqual([])
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
  if (process.env.CAPTURE_RESOURCES)
    await page.screenshot({
      path: `../.local/resources-${test.info().project.name}.png`,
      fullPage: true,
      animations: 'disabled',
    })
  await page.getByRole('link', { name: '我的学习', exact: true }).click()
  await expect(page.locator('.stats-grid strong').first()).toHaveText('0')
})
