import { ref, shallowRef, watch } from 'vue'
import { errorMessage } from '../api/client'

export function useResource<T>(loader: () => Promise<T>, key: () => unknown = () => true) {
  const data = shallowRef<T>()
  const loading = ref(false)
  const error = ref('')
  let version = 0
  async function load() {
    const current = ++version
    loading.value = true
    error.value = ''
    data.value = undefined
    try {
      const result = await loader()
      if (current === version) data.value = result
    } catch (e) {
      if (current === version) error.value = errorMessage(e)
    } finally {
      if (current === version) loading.value = false
    }
  }
  watch(key, load, { immediate: true })
  return { data, loading, error, reload: load }
}
