import { test, expect } from '@playwright/test'
import AxeBuilder from '@axe-core/playwright'

test('card AI entry uses real retrieval and reports unconfigured models or grounded answers', async ({
  page,
  request,
}) => {
  await page.goto('/stories/1')
  await page.getByRole('radio', { name: /A 顺序表/ }).check()
  await page.getByRole('button', { name: '提交选择，继续故事' }).click()
  await page.getByRole('button', { name: '带走这一段知识' }).click()
  await page.getByRole('button', { name: '完成学习，解锁知识卡' }).click()
  await page.getByRole('link', { name: '打开知识卡' }).click()
  await page.getByRole('button', { name: /问问 AI/ }).click()
  await page.getByLabel('你想理解什么？').fill('为什么单链表不能像顺序表一样随机访问？')
  // CI has no provider key and validates real DB retrieval plus explicit degradation.
  // A configured local service is tested separately under the explicit live-ai command.
  const configured = (await (await request.get('/api/ai/status')).json()).data.configured
  if (!configured) {
    const response = page.waitForResponse((r) => r.url().endsWith('/api/ai/tutor'))
    await page.getByRole('button', { name: '带着问题，问问 AI' }).click()
    const reply = (await (await response).json()).data
    expect(reply.status).toBe('MODEL_NOT_CONFIGURED')
    expect(reply.claims).toHaveLength(0)
    expect(reply.sources.length).toBeGreaterThan(0)
    await expect(page.getByText('本次检索到的已审核依据')).toBeVisible()
  }
  expect(
    (await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa', 'wcag21aa']).analyze())
      .violations,
  ).toEqual([])
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true)
})

test('admin content operations require a separate credential', async ({ page }) => {
  await page.goto('/admin/ai')
  await page.getByLabel('管理员令牌（仅保存在本页内存）').fill('invalid-admin-token')
  await page.getByRole('button', { name: '验证权限并加载' }).click()
  await expect(page.getByRole('alert')).toContainText(/管理员|权限/)
  await expect(page.getByRole('button', { name: '我已核对，批准用于检索' })).toHaveCount(0)
})
