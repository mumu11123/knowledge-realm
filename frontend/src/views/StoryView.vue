<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api, errorMessage } from '../api/client'
import type { Answer } from '../api/types'
import { useResource } from '../composables/useResource'
import { useLearning } from '../stores/learning'
import AsyncState from '../components/AsyncState.vue'
import SceneArt from '../components/SceneArt.vue'
import Icon from '../components/Icon.vue'
const route = useRoute()
const learning = useLearning()
const id = computed(() => Number(route.params.id))
const {
  data: story,
  loading,
  error,
  reload,
} = useResource(
  () => api.story(id.value),
  () => id.value,
)
const selected = ref<number | null>(null)
const answer = ref<Answer>()
const submitting = ref(false)
const submitError = ref('')
const stage = ref<'question' | 'analysis' | 'summary' | 'complete'>('question')
const feedbackHeading = ref<HTMLElement>()
watch(story, (value) => {
  if (value) learning.visit(value)
  selected.value = null
  answer.value = undefined
  stage.value = 'question'
  submitError.value = ''
})
const progress = computed(
  () => ({ question: 1, analysis: 2, summary: 3, complete: 4 })[stage.value],
)
async function submit() {
  if (!story.value || selected.value === null || submitting.value) return
  const activeId = id.value
  submitting.value = true
  submitError.value = ''
  try {
    const result = await api.answer(activeId, story.value.question.id, selected.value)
    if (id.value !== activeId) return
    answer.value = result
    stage.value = 'analysis'
    await nextTick()
    feedbackHeading.value?.focus()
    feedbackHeading.value?.scrollIntoView({ block: 'start' })
  } catch (e) {
    if (id.value === activeId) submitError.value = errorMessage(e)
  } finally {
    submitting.value = false
  }
}
function finish() {
  if (answer.value) {
    learning.complete(answer.value)
    stage.value = 'complete'
    window.scrollTo({ top: 0 })
  }
}
function restart() {
  selected.value = null
  answer.value = undefined
  stage.value = 'question'
  window.scrollTo({ top: 0 })
}
function showSummary() {
  stage.value = 'summary'
  window.scrollTo({ top: 0 })
}
</script>
<template>
  <div class="story-page">
    <RouterLink to="/map" class="back-link"><Icon name="back" :size="18" /> 返回知识地图</RouterLink
    ><AsyncState :loading="loading" :error="error" @retry="reload" />
    <template v-if="story"
      ><div class="story-meta">
        <span class="badge violet">演示故事 · 待审核</span
        ><span class="muted small"
          >{{ progress }} / 4 ·
          {{ ['', '进入场景', '理解选择', '知识总结', '完成旅程'][progress] }}</span
        >
      </div>
      <div
        class="progress-track"
        role="progressbar"
        :aria-valuenow="progress"
        :aria-valuemin="0"
        :aria-valuemax="4"
        aria-label="故事学习进度"
      >
        <span :style="{ width: `${progress * 25}%` }" />
      </div>
      <h1 class="story-title">{{ story.title }}</h1>
      <p class="scene-label"><Icon name="clock" :size="16" /> {{ story.scene }}</p>
      <template v-if="stage === 'question'"
        ><SceneArt :scene="story.illustration" />
        <div class="narrative">
          <p>{{ story.introduction }}</p>
        </div>
        <form @submit.prevent="submit">
          <fieldset :disabled="submitting">
            <legend>{{ story.question.prompt }}</legend>
            <div class="options">
              <label
                v-for="option in story.question.options"
                :key="option.id"
                class="option"
                :class="{ selected: selected === option.id }"
                ><input v-model="selected" type="radio" name="answer" :value="option.id" /><span
                  class="option-letter"
                  >{{ option.label }}</span
                ><span>{{ option.name }}</span
                ><Icon v-if="selected === option.id" name="check" :size="19"
              /></label>
            </div>
          </fieldset>
          <p v-if="submitError" class="error-text" role="alert">{{ submitError }}</p>
          <button class="button full" :disabled="selected === null || submitting" type="submit">
            {{ submitting ? '正在提交选择…' : '提交选择，继续故事'
            }}<Icon name="arrow" :size="19" />
          </button>
          <p class="hint center">选错也没关系，每一个选择都有值得理解的地方。</p>
        </form>
      </template>
      <template v-if="stage === 'analysis' && answer"
        ><section class="feedback panel" :class="answer.correct ? 'correct' : 'reflect'">
          <span class="eyebrow">故事继续</span>
          <h2 ref="feedbackHeading" tabindex="-1">
            {{ answer.correct ? '你的选择，让故事顺利向前。' : '换个角度，就会看见答案。' }}
          </h2>
          <p>{{ answer.feedback }}</p>
          <span class="badge">{{ answer.correct ? '本次选择正确' : '本次选择不符合题意' }}</span>
        </section>
        <div class="section-heading">
          <h2>每个选项，都讲清楚</h2>
          <span class="small muted">点击展开</span>
        </div>
        <details
          v-for="option in answer.explanations"
          :key="option.id"
          class="explanation panel"
          :open="option.id === answer.selectedOptionId"
        >
          <summary>
            <span class="option-letter">{{ option.label }}</span
            ><span
              >{{ option.name
              }}<small
                >{{ option.id === answer.selectedOptionId ? '你的选择 · ' : ''
                }}{{ option.correct ? '符合题意' : '不符合题意' }}</small
              ></span
            ><span class="expand-symbol">+</span>
          </summary>
          <dl>
            <dt>专业术语</dt>
            <dd>{{ option.term }}</dd>
            <dt>通俗含义</dt>
            <dd>{{ option.plainMeaning }}</dd>
            <dt>{{ option.correct ? '为什么正确' : '为什么不符合' }}</dt>
            <dd>{{ option.reason }}</dd>
            <dt>容易产生的误区</dt>
            <dd>{{ option.misconception }}</dd>
          </dl>
        </details>
        <button class="button full" @click="showSummary">
          带走这一段知识<Icon name="arrow" :size="19" />
        </button>
      </template>
      <template v-if="stage === 'summary' && answer"
        ><p class="intro">故事是理解的入口，定义是知识的根基。</p>
        <article
          v-for="summary in answer.summaries"
          :key="summary.knowledgeId"
          class="panel summary-card"
        >
          <span class="eyebrow">KNOWLEDGE NOTES</span>
          <h2>{{ summary.name }}</h2>
          <dl>
            <dt>专业定义</dt>
            <dd>{{ summary.definition }}</dd>
            <dt>通俗解释</dt>
            <dd>{{ summary.plainExplanation }}</dd>
            <dt>关键结论与适用条件</dt>
            <dd>{{ summary.keyConclusions }}</dd>
            <dt>常见误区</dt>
            <dd>{{ summary.misconceptions }}</dd>
          </dl>
        </article>
        <button class="button full" @click="finish">
          完成学习，解锁知识卡<Icon name="cards" :size="19" /></button
        ><button class="text-button full" @click="stage = 'analysis'">
          再看一次选项解析
        </button></template
      >
      <template v-if="stage === 'complete' && answer"
        ><section class="completion panel">
          <span class="completion-icon"><Icon name="check" :size="36" /></span>
          <p class="eyebrow">A LITTLE DISCOVERY</p>
          <h2>又一小片知识，<br />有了清晰的形状。</h2>
          <p>已记录本次学习，并解锁对应知识卡。<br />完成学习不代表已经熟练掌握，记得回来复习。</p>
          <RouterLink
            v-for="summary in answer.summaries.filter((s) => s.cardId != null)"
            :key="summary.knowledgeId"
            :to="`/cards/${summary.cardId}`"
            class="button full"
            >打开知识卡<Icon name="cards" :size="19" /></RouterLink
          ><RouterLink to="/map" class="button secondary full">回到知识地图</RouterLink
          ><button class="text-button" @click="restart">再体验一次故事</button>
        </section></template
      >
      <details class="metadata story-source">
        <summary>演示内容与参考信息</summary>
        <p>生活情境是类比，不等同于真实计算机实现。</p>
        <p>{{ story.contentSource }}</p>
        <p>{{ story.syllabusVersion }}</p>
      </details>
    </template>
  </div>
</template>
