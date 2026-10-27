<script setup lang="ts">
import { computed } from 'vue'
import { api } from '../api/client'
import { useResource } from '../composables/useResource'
import { useLearning } from '../stores/learning'
import AsyncState from '../components/AsyncState.vue'
import Icon from '../components/Icon.vue'
import SceneArt from '../components/SceneArt.vue'
const { data: subjects, loading, error, reload } = useResource(api.subjects)
const learning = useLearning()
const last = computed(() =>
  learning.progress.stories.find((s) => s.id === learning.progress.lastStoryId),
)
const icons: Record<string, string> = {
  'data-structures': 'layers',
  'computer-organization': 'cpu',
  'operating-systems': 'cards',
  'computer-networks': 'network',
}
</script>
<template>
  <div class="home-page">
    <section class="welcome">
      <p class="eyebrow">每一次好奇，都是旅程的开始</p>
      <h1>在故事里，<br /><span>理解知识。</span></h1>
      <p class="intro">把抽象的 408 知识，<br class="mobile-only" />变成一段你亲自参与的故事。</p>
      <div class="hero-art">
        <SceneArt scene="library" /><span class="art-caption"
          ><Icon name="spark" :size="14" /> 下一站，知识的另一面</span
        >
      </div>
    </section>
    <section v-if="last" class="continue-card">
      <div>
        <span class="eyebrow">{{
          last.completed ? '再走一次，也会有新发现' : '你的故事，等你继续'
        }}</span>
        <h3>{{ last.title }}</h3>
      </div>
      <RouterLink
        :to="`/stories/${last.id}`"
        class="icon-button"
        :aria-label="`继续学习：${last.title}`"
        ><Icon name="arrow"
      /></RouterLink>
    </section>
    <section>
      <div class="section-heading">
        <div>
          <p class="eyebrow">CHOOSE YOUR WORLD</p>
          <h2>选择你的知识世界</h2>
        </div>
        <span class="muted small">408 四门学科</span>
      </div>
      <AsyncState :loading="loading" :error="error" @retry="reload" />
      <div v-if="subjects" class="subject-grid">
        <article
          v-for="subject in subjects"
          :key="subject.id"
          class="subject-card"
          :class="{ open: subject.availability === 'OPEN' }"
        >
          <div class="subject-top">
            <span class="subject-icon"
              ><Icon :name="icons[subject.code] || 'book'" :size="27" /></span
            ><span class="badge" :class="subject.availability === 'OPEN' ? 'violet' : ''">{{
              subject.availability === 'OPEN' ? '已开放' : '即将开放'
            }}</span>
          </div>
          <h3>{{ subject.name }}</h3>
          <p>{{ subject.description }}</p>
          <RouterLink v-if="subject.availability === 'OPEN'" to="/map" class="subject-link"
            >开始探索 <Icon name="arrow" :size="19"
          /></RouterLink>
          <span v-else class="subject-link muted"
            ><Icon name="lock" :size="15" /> 故事正在酝酿中</span
          >
        </article>
      </div>
    </section>
    <section class="learning-strip">
      <div>
        <Icon name="cards" /><span
          ><strong>{{ learning.progress.cards.length }}</strong> 张已解锁知识卡</span
        >
      </div>
      <RouterLink to="/cards?filter=due"
        >今日复习 <span class="number-pill">{{ learning.due().length }}</span
        ><Icon name="arrow" :size="16"
      /></RouterLink>
    </section>
    <p class="demo-note">
      V0.1 · 当前提供数据结构演示故事，内容待审核。<br />慢慢探索，不用急着抵达终点。
    </p>
  </div>
</template>
