import { createRouter, createWebHistory } from 'vue-router'

export const router = createRouter({
  history: createWebHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    { path: '/', component: () => import('./views/HomeView.vue'), meta: { title: '发现' } },
    { path: '/map', component: () => import('./views/MapView.vue'), meta: { title: '知识地图' } },
    {
      path: '/stories/:id(\\d+)',
      component: () => import('./views/StoryView.vue'),
      meta: { title: '互动故事' },
    },
    {
      path: '/cards',
      component: () => import('./views/CardsView.vue'),
      meta: { title: '知识卡片' },
    },
    {
      path: '/cards/:id(\\d+)',
      component: () => import('./views/CardView.vue'),
      meta: { title: '知识卡片' },
    },
    {
      path: '/study',
      component: () => import('./views/StudyView.vue'),
      meta: { title: '我的学习' },
    },
    {
      path: '/:pathMatch(.*)*',
      component: () => import('./views/NotFoundView.vue'),
      meta: { title: '页面未找到' },
    },
  ],
})
router.afterEach((to) => {
  document.title = `${to.meta.title} · 知境`
})
