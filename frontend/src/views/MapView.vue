<script setup lang="ts">
import { computed, ref } from 'vue'
import { loadCatalog } from '../api/catalog'
import { useResource } from '../composables/useResource'
import { useLearning } from '../stores/learning'
import type { Knowledge } from '../api/types'
import AsyncState from '../components/AsyncState.vue'
import Icon from '../components/Icon.vue'
import RelatedVideos from '../components/RelatedVideos.vue'
import { api } from '../api/client'
const { data: catalog, loading, error, reload } = useResource(loadCatalog)
const {
  data: resources,
  error: resourceError,
  reload: reloadResources,
} = useResource(() => api.subjectResources(1))
const videosFor = (id: number) =>
  resources.value?.filter((resource) => resource.knowledgeIds.includes(id)) ?? []
const learning = useLearning()
const expanded = ref<number[]>([2, 3])
const openStories = computed(
  () => new Set(catalog.value?.points.flatMap((p) => p.stories.map((s) => s.id)) ?? []).size,
)
function toggle(id: number) {
  expanded.value = expanded.value.includes(id)
    ? expanded.value.filter((x) => x !== id)
    : [...expanded.value, id]
}
function state(point: Knowledge) {
  if (!point.stories.length) return videosFor(point.id).length ? '站内故事待制作' : '暂无教学内容'
  if (learning.due().some((c) => c.knowledgeId === point.id)) return '待复习'
  if (learning.progress.cards.some((c) => c.knowledgeId === point.id)) return '已完成学习'
  return '已开放故事'
}
function names(ids: number[]) {
  return (
    ids
      .map((id) => catalog.value?.points.find((k) => k.id === id)?.name || `知识点 ${id}`)
      .join('、') || '无'
  )
}
</script>
<template>
  <div>
    <p class="eyebrow">DATA STRUCTURES</p>
    <h1 class="page-title">数据结构<span class="title-dot">.</span></h1>
    <p class="intro">一张地图，连接每一次恍然大悟。</p>
    <div class="map-overview panel">
      <Icon name="map" :size="32" />
      <div>
        <strong>从小小的故事开始</strong>
        <p>7 个基础章节 · {{ openStories }} 个开放演示故事</p>
      </div>
      <span class="badge violet">演示内容</span>
    </div>
    <p class="hint">目录为基础框架，不代表已覆盖整门考纲。演示内容尚未完成正式审核。</p>
    <section v-if="resources?.length" class="resource-intro panel">
      <span class="subject-icon"><Icon name="play" :size="26" /></span>
      <div>
        <h2>换个角度，用动画理解</h2>
        <p>{{ resources.length }} 个拓展视频已关联到对应知识点，展开下方“相关视频”观看。</p>
        <a :href="resources[0].authorUrl" target="_blank" rel="noopener noreferrer"
          >UP主：{{ resources[0].authorName }} · B站主页<Icon name="arrow" :size="15"
        /></a>
      </div>
    </section>
    <p v-if="resourceError" class="hint" role="status">
      相关视频暂时无法加载。<button class="text-button" @click="reloadResources">
        重试视频列表
      </button>
    </p>
    <AsyncState :loading="loading" :error="error" @retry="reload" />
    <div v-if="catalog" class="chapter-list">
      <section v-for="chapter in catalog.chapters" :key="chapter.id" class="chapter panel">
        <button
          class="chapter-heading"
          :aria-expanded="expanded.includes(chapter.id)"
          :aria-controls="`chapter-${chapter.id}`"
          @click="toggle(chapter.id)"
        >
          <span class="chapter-number">{{ String(chapter.sortOrder).padStart(2, '0') }}</span
          ><span
            ><strong>{{ chapter.name }}</strong
            ><small
              >{{ catalog.points.filter((p) => p.chapterId === chapter.id).length }} 个知识点</small
            ></span
          ><span class="expand-symbol">{{ expanded.includes(chapter.id) ? '−' : '+' }}</span>
        </button>
        <div v-if="expanded.includes(chapter.id)" :id="`chapter-${chapter.id}`" class="point-list">
          <article
            v-for="point in catalog.points.filter((p) => p.chapterId === chapter.id)"
            :key="point.id"
            class="knowledge-point"
          >
            <div class="point-heading">
              <span class="point-marker" :class="{ available: point.stories.length }"
                ><Icon :name="point.stories.length ? 'spark' : 'lock'" :size="15"
              /></span>
              <h3>{{ point.name }}</h3>
            </div>
            <span class="badge" :class="point.stories.length ? 'violet' : ''">{{
              state(point)
            }}</span>
            <p>{{ point.introduction }}</p>
            <RouterLink
              v-for="story in point.stories"
              :key="story.id"
              :to="`/stories/${story.id}`"
              class="story-entry"
              >{{ story.title }}<Icon name="arrow" :size="19"
            /></RouterLink>
            <RelatedVideos :resources="videosFor(point.id)" compact />
            <details class="metadata">
              <summary>知识关系与内容信息</summary>
              <p>前置知识：{{ names(point.prerequisites) }}</p>
              <p>关联知识：{{ names(point.relatedKnowledge) }}</p>
              <p>
                ID {{ point.id }} · {{ point.reviewStatus === 'REVIEWED' ? '已审核' : '待审核' }} ·
                {{ point.resourceStatus === 'EMPTY' ? '暂无教学资源' : '演示资源' }}
              </p>
              <p>{{ point.contentSource }}</p>
              <p>{{ point.syllabusVersion }}</p>
            </details>
          </article>
          <p v-if="!catalog.points.some((p) => p.chapterId === chapter.id)" class="empty-copy">
            这个章节正在准备中。
          </p>
        </div>
      </section>
    </div>
  </div>
</template>
