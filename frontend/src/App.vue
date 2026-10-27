<script setup lang="ts">
import { watch, nextTick, ref, onMounted } from 'vue'
import { aiApi } from './api/ai'
import { useRoute } from 'vue-router'
import Icon from './components/Icon.vue'
import { useLearning } from './stores/learning'
const route = useRoute()
const learning = useLearning()
const main = ref<HTMLElement>()
onMounted(() => {
  aiApi.session().catch(() => {
    /* Core guest learning remains available without the AI session. */
  })
})
watch(
  () => route.path,
  async () => {
    await nextTick()
    main.value?.focus({ preventScroll: true })
  },
)
const nav = [
  { to: '/', icon: 'spark', label: '发现' },
  { to: '/map', icon: 'map', label: '知识地图' },
  { to: '/cards', icon: 'cards', label: '知识卡片' },
  { to: '/study', icon: 'user', label: '我的学习' },
]
</script>
<template>
  <a href="#main" class="skip-link">跳到内容</a>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink to="/" class="brand"
        ><span class="brand-icon"><Icon name="book" :size="23" /></span
        ><span>知境<small>KNOWLEDGE REALM</small></span></RouterLink
      ><span class="version-pill">408 学习旅程 <span class="desktop-only">· V0.2</span></span>
    </header>
    <div v-if="learning.warning" class="storage-warning" role="alert">
      {{ learning.warning }} <RouterLink to="/study">管理记录</RouterLink>
    </div>
    <main id="main" ref="main" tabindex="-1">
      <RouterView v-slot="{ Component }"
        ><Transition name="page" mode="out-in"
          ><component :is="Component" :key="route.path" /></Transition
      ></RouterView>
    </main>
    <nav class="bottom-nav" aria-label="主要导航">
      <RouterLink
        v-for="item in nav"
        :key="item.to"
        :to="item.to"
        :class="{ active: item.to === '/' ? route.path === '/' : route.path.startsWith(item.to) }"
        ><Icon :name="item.icon" :size="21" /><span>{{ item.label }}</span></RouterLink
      >
    </nav>
  </div>
</template>
