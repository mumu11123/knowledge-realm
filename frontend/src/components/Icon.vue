<script setup lang="ts">
withDefaults(defineProps<{ name: string; size?: number }>(), { size: 24 })
const paths: Record<string, string> = {
  play: 'M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0 M10 8l6 4-6 4z',
  book: 'M4 4h5a5 5 0 0 1 3 1 5 5 0 0 1 3-1h5v15h-5a5 5 0 0 0-3 1 5 5 0 0 0-3-1H4z M12 5v15',
  map: 'm3 5 6-2 6 2 6-2v16l-6 2-6-2-6 2z M9 3v16 M15 5v16',
  cards: 'M7 3h12v16H7z M4 7v15h11 M10 8h6 M10 12h4',
  user: 'M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0 M4 21v-2a8 8 0 0 1 16 0v2',
  arrow: 'M4 12h16 M14 6l6 6-6 6',
  back: 'm14 5-7 7 7 7',
  check: 'm5 12 4 4L19 6',
  star: 'm12 3 3 6 6 1-4.5 4.5 1 6.5-5.5-3-5.5 3 1-6.5L3 10l6-1z',
  clock: 'M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0 M12 7v5l3 2',
  layers: 'm12 3 10 5-10 5L2 8z M2 12l10 5 10-5 M2 16l10 5 10-5',
  cpu: 'M6 6h12v12H6z M9 9h6v6H9z M9 2v4 M15 2v4 M9 18v4 M15 18v4 M2 9h4 M2 15h4 M18 9h4 M18 15h4',
  network: 'M9 3h6v6H9z M2 16h6v6H2z M16 16h6v6h-6z M12 9v4 M5 16v-3h14v3',
  spark: 'm12 2 2.5 7.5L22 12l-7.5 2.5L12 22l-2.5-7.5L2 12l7.5-2.5z',
  lock: 'M6 10h12v11H6z M8 10V6a4 4 0 0 1 8 0v4',
  flip: 'M4 8a8 8 0 0 1 14-3l2 3 M20 3v5h-5 M20 16A8 8 0 0 1 6 19l-2-3 M4 21v-5h5',
}
</script>
<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.65"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <path :d="paths[name] || paths.book" />
  </svg>
</template>
