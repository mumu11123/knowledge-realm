<script setup lang="ts">
import { ref } from 'vue'
import { aiApi, type AdminChunk, type StoryDraft } from '../api/ai'
import { errorMessage } from '../api/client'
const token = ref(''),
  reviewer = ref(''),
  note = ref(''),
  error = ref(''),
  busy = ref(false),
  notice = ref('')
const chunks = ref<AdminChunk[]>([]),
  drafts = ref<StoryDraft[]>([])
const knowledge = ref(201),
  constraints = ref('明确区分生活类比与真实存储；复杂度必须给出适用条件。'),
  difficulty = ref('基础'),
  style = ref('温柔生活化')
const confirmed = ref<number | null>(null)
async function action(work: () => Promise<unknown>) {
  busy.value = true
  error.value = ''
  notice.value = ''
  try {
    await work()
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    busy.value = false
  }
}
async function load() {
  const [a, b] = await Promise.all([aiApi.chunks(token.value), aiApi.drafts(token.value)])
  chunks.value = a
  drafts.value = b
}
function review(chunk: AdminChunk, approve: boolean) {
  if (!reviewer.value.trim() || !note.value.trim()) {
    error.value = '请填写真实审核人和审核说明。'
    return
  }
  return action(async () => {
    await aiApi.review(token.value, chunk.id, reviewer.value, note.value, approve)
    await load()
    notice.value = '知识审核状态已记录。'
  })
}
function generate() {
  return action(async () => {
    await aiApi.generate(
      token.value,
      knowledge.value,
      constraints.value,
      difficulty.value,
      style.value,
    )
    await load()
    notice.value = '仅生成并保存草稿，尚未发布。'
  })
}
function publish(draft: StoryDraft) {
  if (!reviewer.value.trim() || !note.value.trim()) {
    error.value = '发布必须填写审核人和说明。'
    return
  }
  return action(async () => {
    const result = await aiApi.publish(token.value, draft.id, reviewer.value, note.value)
    await load()
    confirmed.value = null
    notice.value = `已人工批准发布，故事 ID：${result.storyId}`
  })
}
function pretty(payload: string) {
  try {
    return JSON.stringify(JSON.parse(payload), null, 2)
  } catch {
    return payload
  }
}
</script>
<template>
  <div class="admin-page">
    <p class="eyebrow">CONTENT WORKSPACE</p>
    <h1 class="page-title">知识审核与 AI 草稿</h1>
    <p class="intro">仅供管理员。先核对专业来源，再审核片段；生成内容始终先进入草稿区。</p>
    <form class="panel admin-form" @submit.prevent="action(load)">
      <label for="admin-token">管理员令牌（仅保存在本页内存）</label
      ><input id="admin-token" v-model="token" type="password" autocomplete="off" required /><button
        class="button full"
        :disabled="busy"
      >
        验证权限并加载
      </button>
    </form>
    <p v-if="error" class="error-text" role="alert">{{ error }}</p>
    <p v-if="notice" class="ai-status" role="status">{{ notice }}</p>
    <p v-if="busy" role="status">正在处理，请稍候…</p>
    <template v-if="chunks.length"
      ><section class="panel admin-form">
        <h2>审核信息</h2>
        <label for="reviewer">实际审核人</label
        ><input id="reviewer" v-model="reviewer" maxlength="100" /><label for="review-note"
          >审核说明</label
        ><textarea
          id="review-note"
          v-model="note"
          maxlength="1000"
          rows="3"
          placeholder="请记录核对依据、适用条件与是否发现问题。"
        />
      </section>
      <div class="section-heading"><h2>专业知识片段</h2></div>
      <article v-for="chunk in chunks" :key="chunk.id" class="panel admin-form">
        <span class="badge violet">{{ chunk.reviewStatus }} · K{{ chunk.id }}</span>
        <h3>{{ chunk.title }}</h3>
        <p>{{ chunk.body }}</p>
        <a :href="chunk.sourceUrl" target="_blank" rel="noopener noreferrer">{{
          chunk.sourceTitle
        }}</a>
        <p class="hint">{{ chunk.sourceVersion }} · 知识点 {{ chunk.knowledgeId }}</p>
        <div class="admin-actions">
          <button class="button secondary" :disabled="busy" @click="review(chunk, false)">
            驳回</button
          ><button class="button" :disabled="busy" @click="review(chunk, true)">
            我已核对，批准用于检索
          </button>
        </div>
      </article>
      <form class="panel admin-form" @submit.prevent="generate">
        <h2>生成故事草稿</h2>
        <label for="draft-knowledge">已审核知识点</label
        ><select id="draft-knowledge" v-model="knowledge">
          <option :value="201">201 · 顺序表与单链表</option>
          <option :value="301">301 · 栈</option>
          <option :value="302">302 · 队列</option></select
        ><label for="draft-constraints">专业约束</label
        ><textarea id="draft-constraints" v-model="constraints" maxlength="300" required /><label
          for="draft-difficulty"
          >目标难度</label
        ><input id="draft-difficulty" v-model="difficulty" maxlength="50" required /><label
          for="draft-style"
          >故事风格</label
        ><input id="draft-style" v-model="style" maxlength="100" required /><button
          class="button full"
          :disabled="busy"
        >
          基于已审核专业片段生成草稿
        </button>
        <p class="hint">定义由服务端从已审核片段读取，前端不能替换。不会自动发布。</p>
      </form>
      <div class="section-heading"><h2>草稿与人工发布</h2></div>
      <article v-for="draft in drafts" :key="draft.id" class="panel admin-form">
        <h3>草稿 #{{ draft.id }} · {{ draft.status }}</h3>
        <details>
          <summary>完整内容、全部选项和来源映射</summary>
          <pre>{{ pretty(draft.payload) }}</pre>
        </details>
        <p class="hint">知识点 {{ draft.knowledgeId }} · 依据 {{ draft.sourceIds }}</p>
        <template v-if="draft.status === 'DRAFT'"
          ><button
            v-if="confirmed !== draft.id"
            class="button secondary full"
            @click="confirmed = draft.id"
          >
            进入人工发布确认
          </button>
          <div v-else class="confirm-reset">
            <p>
              请逐项核对题意、正确答案、全部解析与专业约束。填写上方实际审核人和说明后，才可正式发布。
            </p>
            <button class="button secondary" @click="confirmed = null">继续保留草稿</button
            ><button class="button" :disabled="busy" @click="publish(draft)">
              已人工审核，确认发布
            </button>
          </div></template
        ><RouterLink
          v-if="draft.publishedStoryId"
          :to="`/stories/${draft.publishedStoryId}`"
          class="story-entry"
          >查看已发布故事</RouterLink
        >
      </article>
    </template>
  </div>
</template>
