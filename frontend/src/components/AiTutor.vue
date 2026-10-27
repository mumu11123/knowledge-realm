<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { aiApi, type TutorReply } from '../api/ai'
import { errorMessage } from '../api/client'
import Icon from './Icon.vue'
const props = defineProps<{ knowledgeId: number; name: string }>()
const open = ref(false),
  question = ref(''),
  busy = ref(false),
  error = ref(''),
  reply = ref<TutorReply>()
watch(
  () => props.knowledgeId,
  () => {
    open.value = false
    question.value = ''
    reply.value = undefined
    error.value = ''
  },
)
async function ask() {
  if (!question.value.trim() || busy.value) return
  const active = props.knowledgeId
  busy.value = true
  error.value = ''
  reply.value = undefined
  try {
    await aiApi.session()
    const result = await aiApi.ask(active, question.value)
    if (active === props.knowledgeId) reply.value = result
  } catch (e) {
    if (active === props.knowledgeId) error.value = errorMessage(e)
  } finally {
    busy.value = false
  }
}
const labels: Record<string, string> = {
  getKnowledgePoint: '查询当前知识',
  getRelatedKnowledgePoints: '查询关联知识',
  getLearningProgress: '读取我的答题统计',
  getWrongKnowledgePoints: '读取我的错题',
  generateReviewPlan: '生成复习建议',
}
const pretty = (value: unknown) => JSON.stringify(value, null, 2)
const toolProgress = computed(
  () =>
    reply.value?.toolResults.getLearningProgress as
      { attempts: number; correctAttempts: number; storiesTouched: number } | undefined,
)
const wrongPoints = computed(
  () =>
    reply.value?.toolResults.getWrongKnowledgePoints as
      { knowledgeId: number; name: string; wrongAttempts: number }[] | undefined,
)
const reviewPlan = computed(
  () =>
    reply.value?.toolResults.generateReviewPlan as
      { knowledgeId: number; name: string; recommendation: string }[] | undefined,
)
</script>
<template>
  <section class="ai-tutor panel">
    <button
      class="ai-entry"
      :aria-expanded="open"
      aria-controls="ai-tutor-content"
      @click="open = !open"
    >
      <span class="subject-icon"><Icon name="spark" /></span
      ><span
        ><strong>问问 AI</strong><small>围绕「{{ name }}」，一起想明白</small></span
      ><span class="expand-symbol">{{ open ? '−' : '+' }}</span>
    </button>
    <div v-if="open" id="ai-tutor-content" class="ai-content">
      <p class="hint">
        AI 只引用已审核专业片段；可能出错，请核对来源。提问及工具查询到的当前会话学习信息会发送至
        DeepSeek。历史本地进度不会自动上传。
      </p>
      <form @submit.prevent="ask">
        <label for="ai-question">你想理解什么？</label
        ><textarea
          id="ai-question"
          v-model="question"
          maxlength="1000"
          rows="3"
          :disabled="busy"
          placeholder="例如：为什么单链表不能像顺序表一样随机访问？"
          required
        />
        <p class="hint">{{ question.length }} / 1000 · 也可以问“结合我的错题给出复习计划”</p>
        <button type="submit" class="button full" :disabled="busy || !question.trim()">
          {{ busy ? '正在检索资料、思考并按需调用工具…' : '带着问题，问问 AI'
          }}<Icon name="arrow" :size="18" />
        </button>
      </form>
      <p v-if="busy" class="hint" role="status">
        最长约 45 秒。调用过程受限，页面不会替模型编造回答。
      </p>
      <p v-if="error" class="error-text" role="alert">{{ error }}</p>
      <article v-if="reply" class="ai-response" aria-live="polite">
        <p class="ai-status">{{ reply.message }}</p>
        <div v-for="(claim, index) in reply.claims" :key="index" class="ai-claim">
          <p>
            {{ claim.text }}
            <a :href="`#ai-source-${claim.sourceId}`" class="citation">[{{ claim.sourceId }}]</a>
          </p>
          <details>
            <summary>核对这段解释的原文依据</summary>
            <blockquote>{{ claim.quote }}</blockquote>
          </details>
        </div>
        <div v-if="reply.tools.length" class="tool-trace">
          <h3>本次真实工具调用</h3>
          <span v-for="(tool, index) in reply.tools" :key="index" class="badge violet"
            >{{ labels[tool.name] || tool.name }} ·
            {{ tool.status === 'EXECUTED' ? '已执行' : '未完成' }}</span
          >
          <p v-if="toolProgress">
            当前会话已提交 {{ toolProgress.attempts }} 次答案，其中
            {{ toolProgress.correctAttempts }} 次正确，涉及
            {{ toolProgress.storiesTouched }} 个故事。
          </p>
          <div v-if="wrongPoints">
            <h3>需要再看看的知识</h3>
            <p v-if="!wrongPoints.length">当前会话还没有错题记录。</p>
            <p v-for="point in wrongPoints" :key="point.knowledgeId">
              {{ point.name }} · {{ point.wrongAttempts }} 次错误选择
            </p>
          </div>
          <div v-if="reviewPlan">
            <h3>你的复习建议</h3>
            <p v-if="!reviewPlan.length">暂时没有可据以制定计划的错题，先完成一段故事吧。</p>
            <div v-for="item in reviewPlan" :key="item.knowledgeId">
              <strong>{{ item.name }}</strong>
              <p>{{ item.recommendation }}</p>
            </div>
          </div>
          <details>
            <summary>查看工具返回的原始数据（调试）</summary>
            <pre>{{ pretty(reply.toolResults) }}</pre>
          </details>
        </div>
        <section v-if="reply.sources.length" class="ai-sources">
          <h3>本次检索到的已审核依据</h3>
          <details v-for="source in reply.sources" :id="`ai-source-${source.id}`" :key="source.id">
            <summary>[{{ source.id }}] {{ source.title }}</summary>
            <p>{{ source.excerpt }}</p>
            <a :href="source.url" target="_blank" rel="noopener noreferrer">查看原始来源</a>
            <p class="hint">{{ source.version }}</p>
          </details>
        </section>
        <p class="hint">
          {{ (reply.elapsedMs / 1000).toFixed(1) }} 秒 ·
          {{ reply.promptTokens + reply.completionTokens }} Token（提供商返回时）<br />请求编号：{{
            reply.requestId
          }}
        </p>
      </article>
    </div>
  </section>
</template>
