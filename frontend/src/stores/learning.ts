import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import {
  completeStory,
  createProgressStorage,
  dueCards,
  emptyProgress,
  reviewCard,
  visitStory,
  type Progress,
  type StoragePort,
} from '../storage/progress'
import type { Story, Answer } from '../api/types'

export const useLearning = defineStore('learning', () => {
  const port: StoragePort = {
    getItem: (key) => window.localStorage.getItem(key),
    setItem: (key, value) => window.localStorage.setItem(key, value),
  }
  const storage = createProgressStorage(port)
  const loaded = storage.read()
  const progress = ref<Progress>(loaded.progress)
  const warning = ref(loaded.warning)
  let readable = !loaded.warning
  function save(next: Progress) {
    progress.value = next
    if (!readable) return
    warning.value = storage.write(next)
      ? ''
      : '浏览器无法保存学习记录。当前记录仅在本次访问中保留，请检查存储权限或空间。'
  }
  function visit(story: Story) {
    save(visitStory(progress.value, story, Date.now()))
  }
  function complete(answer: Answer) {
    save(
      completeStory(
        progress.value,
        answer.storyId,
        answer.summaries.flatMap((s) =>
          s.cardId == null ? [] : [{ id: s.cardId, knowledgeId: s.knowledgeId, name: s.name }],
        ),
        Date.now(),
      ),
    )
  }
  function favorite(id: number) {
    save({
      ...progress.value,
      cards: progress.value.cards.map((c) => (c.id === id ? { ...c, favorite: !c.favorite } : c)),
    })
  }
  function review(id: number) {
    save(reviewCard(progress.value, id, Date.now()))
  }
  function reset() {
    readable = true
    save(emptyProgress())
  }
  const completed = computed(() => progress.value.stories.filter((s) => s.completed))
  const encountered = computed(
    () => new Set(progress.value.stories.flatMap((s) => s.encounteredIds)).size,
  )
  const favorites = computed(() => progress.value.cards.filter((c) => c.favorite))
  // Called with current time by views; never cache time-dependent status indefinitely.
  const due = () => dueCards(progress.value, Date.now())
  return {
    progress,
    warning,
    visit,
    complete,
    favorite,
    review,
    reset,
    completed,
    encountered,
    favorites,
    due,
  }
})
