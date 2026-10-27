import { chromium } from 'playwright'
import { mkdir, writeFile } from 'node:fs/promises'
import { fileURLToPath } from 'node:url'

// Explicitly invoked live check. Never runs in the default test suite or CI; incurs real provider usage.
const output = new URL('../../.local/ai-ui/', import.meta.url)
await mkdir(output, { recursive: true })
const browser = await chromium.launch({ channel: process.env.PLAYWRIGHT_CHANNEL || 'msedge' })
try {
  const context = await browser.newContext({
    viewport: { width: 390, height: 844 },
    reducedMotion: 'reduce',
  })
  const page = await context.newPage()
  await page.goto('http://127.0.0.1:5173/stories/1')
  await page.getByRole('radio', { name: /B 单链表/ }).check()
  await page.getByRole('button', { name: '提交选择，继续故事' }).click()
  await page.getByRole('button', { name: '带走这一段知识' }).click()
  await page.getByRole('button', { name: '完成学习，解锁知识卡' }).click()
  await page.getByRole('link', { name: '打开知识卡' }).click()
  await page.getByRole('button', { name: /问问 AI/ }).click()
  await page
    .getByLabel('你想理解什么？')
    .fill(
      '请用工具查询我的学习进度和错题，并调用 generateReviewPlan，解释为什么应复习单链表随机访问。',
    )
  const response = page.waitForResponse((r) => r.url().endsWith('/api/ai/tutor'), {
    timeout: 100000,
  })
  await page.getByRole('button', { name: '带着问题，问问 AI' }).click()
  const reply = (await (await response).json()).data
  await writeFile(new URL('live-browser-result.json', output), JSON.stringify(reply, null, 2))
  if (reply.status !== 'ANSWERED' || !reply.claims.length || !reply.sources.length)
    throw new Error('Grounded answer unavailable: ' + reply.status)
  for (const tool of ['getLearningProgress', 'getWrongKnowledgePoints', 'generateReviewPlan']) {
    if (!reply.tools.some((event) => event.name === tool && event.status === 'EXECUTED'))
      throw new Error('Missing real tool: ' + tool)
  }
  await page.getByRole('heading', { name: '本次检索到的已审核依据' }).waitFor()
  await page.evaluate(() => window.scrollTo(0, 0))
  await page.screenshot({
    path: fileURLToPath(new URL('390-ai-tutor.png', output)),
    fullPage: true,
    animations: 'disabled',
  })
  await page.locator('.ai-tutor').screenshot({
    path: fileURLToPath(new URL('390-ai-panel.png', output)),
    animations: 'disabled',
  })
  const { default: AxeBuilder } = await import('@axe-core/playwright')
  const accessibility = await new AxeBuilder({ page })
    .withTags(['wcag2a', 'wcag2aa', 'wcag21aa'])
    .analyze()
  if (accessibility.violations.length)
    throw new Error(
      'Accessibility: ' +
        JSON.stringify(
          accessibility.violations.map((v) => ({ id: v.id, nodes: v.nodes.map((n) => n.html) })),
        ),
    )
  if (!(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)))
    throw new Error('Horizontal overflow')
  console.log(
    JSON.stringify({
      status: reply.status,
      sources: reply.sources.map((s) => s.id),
      tools: reply.tools,
      elapsedMs: reply.elapsedMs,
      promptTokens: reply.promptTokens,
      completionTokens: reply.completionTokens,
    }),
  )
} finally {
  await browser.close()
}
