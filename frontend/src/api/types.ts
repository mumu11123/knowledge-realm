export interface Subject {
  id: number
  code: string
  name: string
  description: string
  availability: 'OPEN' | 'COMING_SOON'
}
export interface Chapter {
  id: number
  subjectId: number
  parentId: number | null
  name: string
  sortOrder: number
}
export interface StoryLink {
  id: number
  title: string
}
export interface LearningResource {
  id: number
  title: string
  platform: string
  authorName: string
  authorUrl: string
  url: string
  description: string
  contentReviewStatus: string
  linkVerifiedAt: string
  knowledgeIds: number[]
}
export interface Knowledge {
  id: number
  chapterId: number
  name: string
  introduction: string
  resourceStatus: string
  reviewStatus: string
  contentSource: string
  syllabusVersion: string
  prerequisites: number[]
  relatedKnowledge: number[]
  stories: StoryLink[]
}
export interface Option {
  id: number
  label: string
  name: string
}
export interface Story {
  id: number
  title: string
  scene: string
  introduction: string
  illustration: string
  resourceStatus: string
  reviewStatus: string
  contentSource: string
  syllabusVersion: string
  knowledgeIds: number[]
  question: { id: number; prompt: string; options: Option[] }
  totalQuestions: number
}
export interface Explanation extends Option {
  term: string
  plainMeaning: string
  correct: boolean
  reason: string
  misconception: string
}
export interface Summary {
  knowledgeId: number
  name: string
  definition: string
  plainExplanation: string
  keyConclusions: string
  misconceptions: string
  cardId: number | null
}
export interface Answer {
  storyId: number
  questionId: number
  selectedOptionId: number
  correct: boolean
  feedback: string
  explanations: Explanation[]
  summaries: Summary[]
}
export interface Card {
  id: number
  knowledgeId: number
  name: string
  chapterName: string
  definition: string
  plainExplanation: string
  keyConclusions: string
  misconceptions: string
  diagramType: string
  resourceStatus: string
  reviewStatus: string
  contentSource: string
  syllabusVersion: string
  stories: StoryLink[]
}
