import { chromium } from 'playwright'
import { mkdir } from 'node:fs/promises'
import { fileURLToPath } from 'node:url'

const browser = await chromium.launch({ channel: process.env.PLAYWRIGHT_CHANNEL || 'msedge' })
const output = new URL('../../.local/ui/', import.meta.url)
await mkdir(output, { recursive: true })
try {
  for (const width of [320, 390, 1440]) {
    const page = await browser.newPage({
      viewport: { width, height: width > 600 ? 1000 : 844 },
      reducedMotion: 'reduce',
    })
    const shot = async (name) => {
      await page.evaluate(() => window.scrollTo(0, 0))
      await page.waitForFunction(() => {
        const content = document.querySelector('main')?.firstElementChild
        return content && getComputedStyle(content).opacity === '1'
      })
      await page.screenshot({
        path: fileURLToPath(new URL(`${width}-${name}.png`, output)),
        fullPage: true,
        animations: 'disabled',
      })
    }
    await page.goto('http://127.0.0.1:5173/')
    await page.getByRole('heading', { name: '数据结构', exact: true }).waitFor()
    await shot('home')
    await page.getByRole('link', { name: '开始探索' }).click()
    await page.getByRole('link', { name: '星光书屋的寻书委托' }).waitFor()
    await shot('map')
    await page.getByRole('link', { name: '星光书屋的寻书委托' }).click()
    await page.getByRole('radio', { name: /A 顺序表/ }).waitFor()
    await shot('story')
    await page.getByRole('radio', { name: /A 顺序表/ }).check()
    await page.getByRole('button', { name: '提交选择，继续故事' }).click()
    await page.getByRole('heading', { name: '每个选项，都讲清楚' }).waitFor()
    await shot('analysis')
    await page.getByRole('button', { name: '带走这一段知识' }).click()
    await page.getByRole('button', { name: '完成学习，解锁知识卡' }).click()
    await page.getByRole('link', { name: '打开知识卡' }).click()
    await page.getByRole('button', { name: '翻转查看通俗解释' }).waitFor()
    await shot('card')
    await page.getByRole('link', { name: '我的学习', exact: true }).click()
    await page.getByRole('heading', { name: '知境旅人' }).waitFor()
    await shot('study')
    await page.close()
  }
} finally {
  await browser.close()
}
console.log('UI screenshots saved to .local/ui')
