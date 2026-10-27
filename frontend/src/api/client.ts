import axios from 'axios'
import type { Subject, Chapter, Knowledge, Story, Answer, Card, LearningResource } from './types'

const http = axios.create({ baseURL: '/api', timeout: 12000 })
async function get<T>(url: string): Promise<T> {
  return (await http.get<{ data: T }>(url)).data.data
}
export const api = {
  subjectResources: (id: number) => get<LearningResource[]>(`/subjects/${id}/resources`),
  pointResources: (id: number) => get<LearningResource[]>(`/knowledge-points/${id}/resources`),
  subjects: () => get<Subject[]>('/subjects'),
  chapters: (id: number) => get<Chapter[]>(`/subjects/${id}/chapters`),
  points: (id: number) => get<Knowledge[]>(`/chapters/${id}/knowledge-points`),
  knowledge: (id: number) => get<Knowledge>(`/knowledge-points/${id}`),
  story: (id: number) => get<Story>(`/stories/${id}`),
  answer: async (id: number, questionId: number, optionId: number) =>
    (await http.post<{ data: Answer }>(`/stories/${id}/answer`, { questionId, optionId })).data
      .data,
  card: (id: number) => get<Card>(`/knowledge-cards/${id}`),
}
export function errorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    if (error.response?.data?.message) return String(error.response.data.message)
    return error.code === 'ECONNABORTED'
      ? '等待有点久，请再试一次。'
      : '暂时连接不到知境，请确认后端已启动后重试。'
  }
  return '内容加载失败，请稍后重试。'
}
