import { api } from './client'
export async function loadCatalog() {
  const chapters = await api.chapters(1)
  const points = (await Promise.all(chapters.map((chapter) => api.points(chapter.id)))).flat()
  return { chapters, points }
}
