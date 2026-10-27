import { test, expect } from '@playwright/test'
import AxeBuilder from '@axe-core/playwright'

test('real API learning journey, wrong feedback, all explanations, cards and persistence', async ({
  page,
}) => {
  const errors: string[] = []
  page.on('pageerror', (e) => errors.push(e.message))
  await page.goto('/')
  await expect(page.getByRole('heading', { name: '数据结构', exact: true })).toBeVisible()
  await expect(page.getByText('即将开放')).toHaveCount(3)
  await page.getByRole('link', { name: '开始探索' }).click()
  await expect(page.getByText('7 个基础章节 · 3 个开放演示故事')).toBeVisible()
  await page.getByRole('link', { name: '星光书屋的寻书委托' }).click()
  await expect(page.getByRole('button', { name: '提交选择，继续故事' })).toBeDisabled()
  await expect(page.getByText('为什么正确')).toHaveCount(0)
  await page.getByRole('radio', { name: /B 单链表/ }).check()
  const submission = page.waitForResponse(
    (r) => r.url().endsWith('/api/stories/1/answer') && r.request().method() === 'POST',
  )
  await page.getByRole('button', { name: '提交选择，继续故事' }).click()
  expect((await (await submission).json()).data.correct).toBe(false)
  await expect(page.getByText('本次选择不符合题意')).toBeVisible()
  expect(
    (await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa', 'wcag21aa']).analyze())
      .violations,
  ).toEqual([])
  for (const label of ['A', 'B', 'C', 'D']) {
    const detail = page
      .locator('details.explanation')
      .filter({ has: page.locator('summary .option-letter', { hasText: label }) })
    if (!((await detail.getAttribute('open')) !== null)) await detail.locator('summary').click()
    await expect(detail.getByText('专业术语', { exact: true })).toBeVisible()
    await expect(detail.getByText('容易产生的误区', { exact: true })).toBeVisible()
  }
  await page.getByRole('button', { name: '带走这一段知识' }).click()
  await page.getByRole('button', { name: '完成学习，解锁知识卡' }).click()
  await page.getByRole('link', { name: '打开知识卡' }).click()
  await page.locator('.related-videos > summary').click()
  await expect(page.locator('.related-videos .video-link')).toHaveCount(4)
  await page.getByRole('button', { name: '翻转查看通俗解释' }).click()
  await expect(page.getByRole('button', { name: '显示专业定义' })).toHaveAttribute(
    'aria-pressed',
    'true',
  )
  await page.getByRole('button', { name: '收藏', exact: true }).click()
  expect(
    (await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa', 'wcag21aa']).analyze())
      .violations,
  ).toEqual([])
  await page.getByRole('button', { name: '我已复习，记录这一次' }).click()
  await page.reload()
  await expect(page.getByRole('button', { name: '已收藏', exact: true })).toBeVisible()
  await expect(page.getByText(/已记录 1 次复习/)).toBeVisible()
  await page.getByRole('link', { name: '我的学习', exact: true }).click()
  await expect(page.getByText(/已完成学习/)).toBeVisible()
  await expect(page.locator('.stats-grid strong').first()).toHaveText('1')
  await expect(page.locator('.stats-grid strong').nth(2)).toHaveText('1')
  expect(errors).toEqual([])
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
})

test('correct stack branch and locked card entry', async ({ page }) => {
  await page.goto('/cards/2')
  await expect(page.getByRole('heading', { name: '这张知识卡还未解锁' })).toBeVisible()
  await page.getByRole('link', { name: '进入 云朵咖啡馆的托盘' }).click()
  await page.getByRole('radio', { name: /B C → B → A/ }).check()
  await page.getByRole('button', { name: '提交选择，继续故事' }).click()
  await expect(page.getByText('本次选择正确', { exact: true })).toBeVisible()
  await expect(
    page.getByText('你轻轻拿起最上面的 C，接着是 B，最后是 A。三个托盘顺利摆上了柜台。'),
  ).toBeVisible()
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
})

test('unavailable backend shows retry and recovers without fake content', async ({ page }) => {
  await page.route('**/api/subjects', (r) => r.abort())
  await page.goto('/')
  await expect(page.getByRole('button', { name: '重新加载' })).toBeVisible()
  await expect(page.getByRole('heading', { name: '数据结构', exact: true })).toHaveCount(0)
  await page.unroute('**/api/subjects')
  await page.getByRole('button', { name: '重新加载' }).click()
  await expect(page.getByRole('heading', { name: '数据结构', exact: true })).toBeVisible()
})
