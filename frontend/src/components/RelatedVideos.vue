<script setup lang="ts">
import type { LearningResource } from '../api/types'
import Icon from './Icon.vue'
withDefaults(defineProps<{ resources: LearningResource[]; compact?: boolean }>(), {
  compact: false,
})
</script>
<template>
  <details v-if="resources.length" class="related-videos" :class="{ compact }">
    <summary>
      <Icon name="play" :size="19" /><span>相关视频</span
      ><span class="video-count">{{ resources.length }}</span
      ><span class="small muted">换个角度理解</span>
    </summary>
    <div class="video-list">
      <article v-for="resource in resources" :key="resource.id" class="video-item">
        <a
          :href="resource.url"
          target="_blank"
          rel="noopener noreferrer"
          class="video-link"
          :aria-label="`${resource.title}，UP主${resource.authorName}，在 B 站新窗口观看`"
          ><span class="video-play"><Icon name="play" :size="22" /></span
          ><span
            ><strong>{{ resource.title }}</strong
            ><small>{{ resource.authorName }} · B站原视频</small></span
          ><Icon name="arrow" :size="17"
        /></a>
      </article>
      <p class="hint">视频在 B 站原页面播放，播放权限与可用性以原站为准。</p>
    </div>
  </details>
</template>
