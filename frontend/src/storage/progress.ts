export const STORAGE_KEY = 'knowledge-realm:progress:v1'
export const REVIEW_INTERVAL = 24 * 60 * 60 * 1000
export interface StoryRecord {
  id: number
  title: string
  encounteredIds: number[]
  completed: boolean
  lastVisitedAt: number
}
export interface CardRecord {
  id: number
  knowledgeId: number
  name: string
  favorite: boolean
  nextReviewAt: number
  reviewCount: number
}
export interface Progress {
  version: 1
  stories: StoryRecord[]
  cards: CardRecord[]
  lastStoryId: number | null
}
export interface StoragePort {
  getItem(key: string): string | null
  setItem(key: string, value: string): void
}
export const emptyProgress = (): Progress => ({
  version: 1,
  stories: [],
  cards: [],
  lastStoryId: null,
})
const positiveId = (v: unknown): v is number =>
  typeof v === 'number' && Number.isSafeInteger(v) && v > 0
const timestamp = (v: unknown): v is number => typeof v === 'number' && Number.isFinite(v) && v >= 0
function valid(v: unknown): v is Progress {
  if (!v || typeof v !== 'object') return false
  const p = v as Progress
  return (
    p.version === 1 &&
    (p.lastStoryId === null || positiveId(p.lastStoryId)) &&
    Array.isArray(p.stories) &&
    Array.isArray(p.cards) &&
    p.stories.every(
      (s) =>
        s &&
        positiveId(s.id) &&
        typeof s.title === 'string' &&
        typeof s.completed === 'boolean' &&
        Array.isArray(s.encounteredIds) &&
        s.encounteredIds.every(positiveId) &&
        timestamp(s.lastVisitedAt),
    ) &&
    p.cards.every(
      (c) =>
        c &&
        positiveId(c.id) &&
        positiveId(c.knowledgeId) &&
        typeof c.name === 'string' &&
        typeof c.favorite === 'boolean' &&
        timestamp(c.nextReviewAt) &&
        Number.isSafeInteger(c.reviewCount) &&
        c.reviewCount >= 0,
    ) &&
    new Set(p.stories.map((s) => s.id)).size === p.stories.length &&
    new Set(p.cards.map((c) => c.id)).size === p.cards.length
  )
}
/** Replace this adapter when accounts arrive; learning UI does not depend on localStorage directly. */
export function createProgressStorage(port: StoragePort) {
  return {
    read(): { progress: Progress; warning: string } {
      try {
        const raw = port.getItem(STORAGE_KEY)
        if (!raw) return { progress: emptyProgress(), warning: '' }
        const parsed: unknown = JSON.parse(raw)
        if (!valid(parsed)) throw new Error('Invalid progress')
        return { progress: parsed, warning: '' }
      } catch {
        return {
          progress: emptyProgress(),
          warning:
            '无法读取本地记录，旧数据未被覆盖。可在我的学习中重置；当前记录仅在本次访问中保留。',
        }
      }
    },
    write(progress: Progress): boolean {
      try {
        port.setItem(STORAGE_KEY, JSON.stringify(progress))
        return true
      } catch {
        return false
      }
    },
  }
}
export function visitStory(
  progress: Progress,
  story: { id: number; title: string; knowledgeIds: number[] },
  now: number,
): Progress {
  const previous = progress.stories.find((s) => s.id === story.id)
  return {
    ...progress,
    lastStoryId: story.id,
    stories: [
      {
        id: story.id,
        title: story.title,
        encounteredIds: [...story.knowledgeIds],
        completed: previous?.completed ?? false,
        lastVisitedAt: now,
      },
      ...progress.stories.filter((s) => s.id !== story.id),
    ],
  }
}
export function completeStory(
  progress: Progress,
  storyId: number,
  cards: { id: number; knowledgeId: number; name: string }[],
  now: number,
): Progress {
  if (!progress.stories.some((s) => s.id === storyId)) return progress
  return {
    ...progress,
    stories: progress.stories.map((s) => (s.id === storyId ? { ...s, completed: true } : s)),
    cards: [
      ...progress.cards,
      ...cards
        .filter((c) => !progress.cards.some((old) => old.id === c.id))
        .map((c) => ({
          ...c,
          favorite: false,
          nextReviewAt: now + REVIEW_INTERVAL,
          reviewCount: 0,
        })),
    ],
  }
}
export const dueCards = (p: Progress, now: number): CardRecord[] =>
  p.cards.filter((c) => c.nextReviewAt <= now)
export function reviewCard(progress: Progress, id: number, now: number): Progress {
  return {
    ...progress,
    cards: progress.cards.map((c) =>
      c.id === id
        ? { ...c, nextReviewAt: now + REVIEW_INTERVAL, reviewCount: c.reviewCount + 1 }
        : c,
    ),
  }
}
