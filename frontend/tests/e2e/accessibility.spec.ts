import AxeBuilder from '@axe-core/playwright'
import { test, expect } from '@playwright/test'

test('main pages support basic accessibility, keyboard navigation and reduced motion', async ({
  page,
}) => {
  await page.emulateMedia({ reducedMotion: 'reduce' })
  for (const path of ['/', '/map', '/stories/1', '/cards', '/study']) {
    await page.goto(path)
    await page.waitForLoadState('networkidle')
    const result = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21aa'])
      .analyze()
    expect(
      result.violations.map((v) => ({
        id: v.id,
        nodes: v.nodes.map((n) => ({ html: n.html, summary: n.failureSummary })),
      })),
    ).toEqual([])
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
  }
  await page.goto('/stories/1')
  await page.getByRole('radio', { name: /A 顺序表/ }).focus()
  await page.keyboard.press('Space')
  await expect(page.getByRole('radio', { name: /A 顺序表/ })).toBeChecked()
  await page.keyboard.press('ArrowDown')
  await expect(page.getByRole('radio', { name: /B 单链表/ })).toBeChecked()
  for (const viewport of [
    { width: 430, height: 932 },
    { width: 844, height: 390 },
  ]) {
    await page.setViewportSize(viewport)
    await page.goto('/')
    await page.getByRole('heading', { name: '数据结构', exact: true }).waitFor()
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
  }
})
