<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../api/client'
import { useResource } from '../composables/useResource'
import { useLearning } from '../stores/learning'
import AsyncState from '../components/AsyncState.vue'
import Icon from '../components/Icon.vue'
const route = useRoute()
const learning = useLearning()
const id = computed(() => Number(route.params.id))
const unlocked = computed(() => learning.progress.cards.find((c) => c.id === id.value))
const flipped = ref(false)
const reviewed = ref(false)
const {
  data: card,
  loading,
  error,
  reload,
} = useResource(
  () => api.card(id.value),
  () => id.value,
)
watch(id, () => {
  flipped.value = false
  reviewed.value = false
})
function review() {
  learning.review(id.value)
  reviewed.value = true
}
</script>
<template>
  <div>
    <RouterLink to="/cards" class="back-link"
      ><Icon name="back" :size="18" /> 返回知识卡片</RouterLink
    ><AsyncState :loading="loading" :error="error" @retry="reload" />
    <template v-if="card"
      ><template v-if="unlocked"
        ><div class="section-heading">
          <span class="badge violet">演示知识卡 · 待审核</span
          ><button
            class="favorite-button"
            :aria-pressed="unlocked.favorite"
            @click="learning.favorite(id)"
          >
            <Icon name="star" :size="20" />{{ unlocked.favorite ? '已收藏' : '收藏' }}
          </button>
        </div>
        <h1 class="page-title">{{ card.name }}</h1>
        <p class="intro">{{ card.chapterName }} · 一次发现，一点积累</p>
        <button
          class="flip-card"
          :class="{ flipped }"
          :aria-pressed="flipped"
          :aria-label="flipped ? '显示专业定义' : '翻转查看通俗解释'"
          @click="flipped = !flipped"
        >
          <span class="flip-inner"
            ><span class="flip-front" :aria-hidden="flipped"
              ><span class="eyebrow">THE DEFINITION</span><strong>{{ card.name }}</strong
              ><span>{{ card.definition }}</span
              ><span class="mini-diagram" aria-hidden="true"
                ><template v-if="card.diagramType === 'stack'"
                  >入栈 ↓<br /><span>C</span><br /><span>B</span><br /><span>A</span><br />↑
                  出栈</template
                ><template v-else-if="card.diagramType === 'queue'"
                  >出队 ← <span>A</span> <span>B</span> <span>C</span> ← 入队</template
                ><template v-else
                  ><span>0</span><span>1</span><span>2</span><br /><span>结点</span> →
                  <span>结点</span> → ∅</template
                ></span
              ><span class="flip-hint"
                ><Icon name="flip" :size="18" /> 点击卡片，换个角度理解</span
              ></span
            ><span class="flip-back" :aria-hidden="!flipped"
              ><span class="eyebrow">IN EVERYDAY WORDS</span><strong>换成生活里的语言</strong
              ><span>{{ card.plainExplanation }}</span
              ><span class="flip-hint"><Icon name="flip" :size="18" /> 点击返回专业定义</span></span
            ></span
          >
        </button>
        <section class="panel summary-card">
          <h2>记住这两个重点</h2>
          <dl>
            <dt>关键结论与适用条件</dt>
            <dd>{{ card.keyConclusions }}</dd>
            <dt>常见误区</dt>
            <dd>{{ card.misconceptions }}</dd>
          </dl>
        </section>
        <button class="button full" :disabled="reviewed" @click="review">
          {{ reviewed ? '已记录复习，24 小时后再见' : '我已复习，记录这一次'
          }}<Icon name="check" :size="19" />
        </button>
        <p class="hint center" aria-live="polite">
          {{
            reviewed
              ? '也可以随时回来，按自己的节奏学习。'
              : `已记录 ${unlocked.reviewCount} 次复习 · 点击后开始下一轮 24 小时提醒`
          }}
        </p>
        <div class="section-heading"><h2>回到故事里</h2></div>
        <RouterLink
          v-for="story in card.stories"
          :key="story.id"
          :to="`/stories/${story.id}`"
          class="story-entry panel"
          >{{ story.title }}<Icon name="arrow" :size="19"
        /></RouterLink>
        <details class="metadata story-source">
          <summary>内容来源与版本</summary>
          <p>{{ card.contentSource }}</p>
          <p>{{ card.syllabusVersion }}</p>
        </details>
      </template>
      <section v-else class="empty-state panel">
        <Icon name="lock" :size="38" />
        <h1>这张知识卡还未解锁</h1>
        <p>先体验对应故事，再读完知识总结，就能把它收入卡片集。</p>
        <RouterLink
          v-for="story in card.stories"
          :key="story.id"
          :to="`/stories/${story.id}`"
          class="button"
          >进入 {{ story.title }}<Icon name="arrow" :size="18"
        /></RouterLink></section
    ></template>
  </div>
</template>
