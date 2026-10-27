import { describe, expect, it } from 'vitest'
import {
  completeStory,
  createProgressStorage,
  dueCards,
  emptyProgress,
  REVIEW_INTERVAL,
  reviewCard,
  STORAGE_KEY,
  visitStory,
} from '../src/storage/progress'

const story = { id: 1, title: '来自 API 的标题', knowledgeIds: [201] }
const cards = [{ id: 1, knowledgeId: 201, name: '知识卡' }]
describe('guest learning lifecycle', () => {
  it('visiting is not completion and does not unlock any card', () => {
    const p = visitStory(emptyProgress(), story, 100)
    expect(p.stories[0].completed).toBe(false)
    expect(p.stories[0].encounteredIds).toEqual([201])
    expect(p.cards).toHaveLength(0)
  })
  it('completion unlocks once; replay preserves favorites and review schedule', () => {
    let p = completeStory(visitStory(emptyProgress(), story, 100), 1, cards, 200)
    p.cards[0].favorite = true
    p = reviewCard(p, 1, 400)
    p = completeStory(visitStory(p, story, 500), 1, cards, 600)
    expect(p.stories).toHaveLength(1)
    expect(p.stories[0].completed).toBe(true)
    expect(p.cards).toHaveLength(1)
    expect(p.cards[0].favorite).toBe(true)
    expect(p.cards[0].nextReviewAt).toBe(400 + REVIEW_INTERVAL)
    expect(p.cards[0].reviewCount).toBe(1)
  })
  it('does not create progress for a story that was never entered', () => {
    expect(completeStory(emptyProgress(), 9, cards, 100)).toEqual(emptyProgress())
  })
  it('due status changes at the boundary and is reset by a review', () => {
    const p = completeStory(visitStory(emptyProgress(), story, 0), 1, cards, 100)
    expect(dueCards(p, 99 + REVIEW_INTERVAL)).toHaveLength(0)
    expect(dueCards(p, 100 + REVIEW_INTERVAL)).toHaveLength(1)
    expect(dueCards(reviewCard(p, 1, 100 + REVIEW_INTERVAL), 100 + REVIEW_INTERVAL)).toHaveLength(0)
  })
})
describe('storage adapter', () => {
  it('round-trips completion, favorites, and review state across a new adapter', () => {
    const values = new Map<string, string>()
    const port = {
      getItem: (key: string) => values.get(key) ?? null,
      setItem: (key: string, value: string) => {
        values.set(key, value)
      },
    }
    const first = createProgressStorage(port)
    const p = completeStory(visitStory(emptyProgress(), story, 100), 1, cards, 200)
    p.cards[0].favorite = true
    expect(first.write(reviewCard(p, 1, 300))).toBe(true)
    const loaded = createProgressStorage(port).read()
    expect(loaded.warning).toBe('')
    expect(loaded.progress.cards[0].favorite).toBe(true)
    expect(loaded.progress.stories[0].completed).toBe(true)
    expect(loaded.progress.cards[0].reviewCount).toBe(1)
  })
  it.each([
    '{broken',
    '{"version":99}',
    JSON.stringify({ ...emptyProgress(), stories: [{ id: -1 }] }),
    JSON.stringify({ ...emptyProgress(), cards: [{ id: 1 }] }),
  ])('preserves invalid stored data: %s', (raw) => {
    const port = {
      getItem: () => raw,
      setItem: () => {
        throw new Error('must not write')
      },
    }
    expect(createProgressStorage(port).read().warning).not.toBe('')
    expect(port.getItem()).toBe(raw)
  })
  it('handles blocked reads and full storage without pretending persistence succeeded', () => {
    const blocked = createProgressStorage({
      getItem: () => {
        throw new Error('blocked')
      },
      setItem: () => {
        throw new Error('full')
      },
    })
    expect(blocked.read().progress).toEqual(emptyProgress())
    expect(blocked.read().warning).not.toBe('')
    expect(blocked.write(emptyProgress())).toBe(false)
  })
  it('uses a versioned isolated key', () => {
    expect(STORAGE_KEY).toBe('knowledge-realm:progress:v1')
  })
})
