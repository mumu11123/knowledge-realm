<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLearning } from '../stores/learning'
import Icon from '../components/Icon.vue'
const learning = useLearning()
const route = useRoute()
const router = useRouter()
const filter = computed(() =>
  route.query.filter === 'favorites' ? 'favorites' : route.query.filter === 'due' ? 'due' : 'all',
)
const cards = computed(() =>
  filter.value === 'favorites'
    ? learning.favorites
    : filter.value === 'due'
      ? learning.due()
      : learning.progress.cards,
)
const tabs = [
  { value: 'all', name: '全部卡片' },
  { value: 'favorites', name: '我的收藏' },
  { value: 'due', name: '待复习' },
]
</script>
<template>
  <div>
    <p class="eyebrow">COLLECT YOUR DISCOVERIES</p>
    <h1 class="page-title">知识，有迹可循<span class="title-dot">.</span></h1>
    <p class="intro">把故事里的发现，收进自己的口袋。</p>
    <div class="tabs" aria-label="卡片筛选">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        :class="{ active: filter === tab.value }"
        :aria-pressed="filter === tab.value"
        @click="router.replace({ query: tab.value === 'all' ? {} : { filter: tab.value } })"
      >
        {{ tab.name }}
      </button>
    </div>
    <div v-if="cards.length" class="card-grid">
      <RouterLink
        v-for="card in cards"
        :key="card.id"
        :to="`/cards/${card.id}`"
        class="collectible-card"
        ><div class="collectible-top">
          <span class="badge violet">演示知识卡</span
          ><Icon :name="card.favorite ? 'star' : 'cards'" :size="22" />
        </div>
        <span class="card-index">NO. {{ String(card.id).padStart(2, '0') }}</span>
        <h2>{{ card.name }}</h2>
        <span class="collectible-bottom"
          >{{ card.nextReviewAt <= Date.now() ? '到时间，复习一下' : '已解锁 · 随时回顾'
          }}<Icon name="arrow" :size="19" /></span
      ></RouterLink>
    </div>
    <div v-else class="empty-state panel">
      <span class="empty-icon"><Icon name="cards" :size="34" /></span>
      <h2>
        {{
          filter === 'due'
            ? '今天的复习已准备妥当'
            : filter === 'favorites'
              ? '把喜欢的知识，留在手边'
              : '你的第一张知识卡，正在等你'
        }}
      </h2>
      <p>
        {{
          filter === 'due'
            ? '新解锁或已复习的卡片，24 小时后会再次提醒。也可以随时打开全部卡片回顾。'
            : filter === 'favorites'
              ? '打开一张已解锁的卡片，点击收藏，就能在这里找到它。'
              : '完成一段故事并阅读知识总结，解锁属于你的知识卡片。'
        }}
      </p>
      <RouterLink :to="filter === 'all' ? '/map' : '/cards'" class="button secondary"
        >{{ filter === 'all' ? '去探索一个故事' : '查看全部卡片' }}<Icon name="arrow" :size="18"
      /></RouterLink>
    </div>
    <p class="demo-note">学习记录保存在当前浏览器。<br />收藏与复习状态不会自动跨设备同步。</p>
  </div>
</template>
