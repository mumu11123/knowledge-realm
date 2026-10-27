<script setup lang="ts">
import { ref } from 'vue'
import { useLearning } from '../stores/learning'
import Icon from '../components/Icon.vue'
const learning = useLearning()
const confirming = ref(false)
const date = (timestamp: number) =>
  new Intl.DateTimeFormat('zh-CN', {
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(timestamp)
function reset() {
  learning.reset()
  confirming.value = false
}
</script>
<template>
  <div>
    <p class="eyebrow">YOUR OWN PACE</p>
    <h1 class="page-title">每一步，都算数<span class="title-dot">.</span></h1>
    <p class="intro">不赶路，慢慢把知识变成自己的。</p>
    <section class="profile panel">
      <span class="profile-avatar"><Icon name="user" :size="30" /></span>
      <div>
        <h2>知境旅人</h2>
        <p>游客模式 · 记录仅保存在当前浏览器</p>
      </div>
    </section>
    <div class="stats-grid">
      <div class="panel">
        <strong>{{ learning.completed.length }}</strong
        ><span>已完成故事</span>
      </div>
      <div class="panel">
        <strong>{{ learning.encountered }}</strong
        ><span>已接触知识点</span>
      </div>
      <RouterLink to="/cards" class="panel"
        ><strong>{{ learning.progress.cards.length }}</strong
        ><span>已解锁知识卡</span></RouterLink
      ><RouterLink to="/cards?filter=favorites" class="panel"
        ><strong>{{ learning.favorites.length }}</strong
        ><span>收藏卡片</span></RouterLink
      >
    </div>
    <RouterLink to="/cards?filter=due" class="review-banner"
      ><span class="subject-icon"><Icon name="clock" /></span>
      <div>
        <h3>给知识一点回响</h3>
        <p>{{ learning.due().length }} 张卡片待复习 · 简单 24 小时提醒</p>
      </div>
      <Icon name="arrow" :size="19"
    /></RouterLink>
    <div class="section-heading">
      <h2>最近走过的故事</h2>
      <span class="small muted">{{ learning.progress.stories.length }} 段旅程</span>
    </div>
    <div v-if="learning.progress.stories.length" class="history-list">
      <RouterLink
        v-for="story in learning.progress.stories"
        :key="story.id"
        :to="`/stories/${story.id}`"
        class="history-item panel"
        ><span class="history-icon"
          ><Icon :name="story.completed ? 'check' : 'book'" :size="22"
        /></span>
        <div>
          <h3>{{ story.title }}</h3>
          <p>
            {{ date(story.lastVisitedAt) }} ·
            {{ story.completed ? '已完成学习' : '已接触 · 可重新进入' }}
          </p>
        </div>
        <Icon name="arrow" :size="18"
      /></RouterLink>
    </div>
    <div v-else class="empty-state panel">
      <Icon name="book" :size="32" />
      <h2>从第一个故事开始</h2>
      <p>每一次进入故事，都会在这里留下足迹。</p>
      <RouterLink to="/map" class="button secondary"
        >探索数据结构<Icon name="arrow" :size="18"
      /></RouterLink>
    </div>
    <section class="local-settings">
      <h2>关于这份学习记录</h2>
      <p>
        已接触、已完成与待复习是不同状态。完成演示不代表掌握知识；目前没有账户和跨设备同步。清除浏览器数据会删除这些记录。
      </p>
      <button v-if="!confirming" class="text-button danger" @click="confirming = true">
        重置本地学习记录
      </button>
      <div v-else class="confirm-reset panel" role="group" aria-label="确认重置本地记录">
        <p>确定清除当前浏览器的进度、收藏和复习记录？此操作无法撤销。</p>
        <button class="button secondary" @click="confirming = false">保留记录</button
        ><button class="button danger-bg" @click="reset">确认清除</button>
      </div>
    </section>
  </div>
</template>
