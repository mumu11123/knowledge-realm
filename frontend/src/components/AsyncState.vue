<script setup lang="ts">
defineProps<{ loading: boolean; error: string }>()
defineEmits<{ retry: [] }>()
</script>
<template>
  <div v-if="loading" class="async-state" role="status">
    <span class="spinner" />正在打开这段旅程…
  </div>
  <div v-else-if="error" class="async-state panel" role="alert">
    <p>{{ error }}</p>
    <button class="button secondary" @click="$emit('retry')">重新加载</button>
  </div>
</template>
