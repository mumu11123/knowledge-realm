import axios from 'axios'
export interface KnowledgeSource {
  id: string
  knowledgeId: number
  title: string
  excerpt: string
  url: string
  version: string
}
export interface TutorReply {
  requestId: string
  status: string
  message: string
  claims: { text: string; sourceId: string; quote: string }[]
  sources: KnowledgeSource[]
  tools: { name: string; status: string }[]
  toolResults: Record<string, unknown>
  elapsedMs: number
  promptTokens: number
  completionTokens: number
}
export interface AdminChunk {
  id: number
  knowledgeId: number
  title: string
  body: string
  sourceUrl: string
  sourceTitle: string
  sourceVersion: string
  reviewStatus: string
  reviewedBy: string | null
}
export interface StoryDraft {
  id: number
  knowledgeId: number
  payload: string
  sourceIds: string
  status: string
  publishedStoryId: number | null
}
const http = axios.create({ baseURL: '/api', timeout: 100000 })
export const aiApi = {
  clearSession: async () => (await http.delete('/ai/session')).data.data,
  session: async () => (await http.post('/ai/session')).data.data,
  status: async (): Promise<{ configured: boolean; model: string }> =>
    (await http.get('/ai/status')).data.data,
  sources: async (id: number): Promise<KnowledgeSource[]> =>
    (await http.get(`/ai/knowledge/${id}/sources`)).data.data,
  ask: async (knowledgeId: number, message: string): Promise<TutorReply> =>
    (await http.post('/ai/tutor', { knowledgeId, message })).data.data,
  chunks: async (token: string): Promise<AdminChunk[]> =>
    (await http.get('/admin/ai/chunks', { headers: { Authorization: `Bearer ${token}` } })).data
      .data,
  review: async (token: string, id: number, reviewer: string, note: string, approve: boolean) =>
    (
      await http.post(
        `/admin/ai/chunks/${id}/review`,
        { reviewer, note, approve },
        { headers: { Authorization: `Bearer ${token}` } },
      )
    ).data.data,
  drafts: async (token: string): Promise<StoryDraft[]> =>
    (await http.get('/admin/ai/drafts', { headers: { Authorization: `Bearer ${token}` } })).data
      .data,
  generate: async (
    token: string,
    knowledgeId: number,
    constraints: string,
    difficulty: string,
    style: string,
  ): Promise<StoryDraft> =>
    (
      await http.post(
        '/admin/ai/drafts',
        { knowledgeId, constraints, difficulty, style },
        { headers: { Authorization: `Bearer ${token}` } },
      )
    ).data.data,
  publish: async (token: string, id: number, reviewer: string, note: string) =>
    (
      await http.post(
        `/admin/ai/drafts/${id}/publish`,
        { reviewer, note, approve: true },
        { headers: { Authorization: `Bearer ${token}` } },
      )
    ).data.data,
}
