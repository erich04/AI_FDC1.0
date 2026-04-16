import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLayoutStore = defineStore('layout', () => {
  const collapsed = ref(false)
  const documentTypeCode = ref(localStorage.getItem('fdc.documentTypeCode') || '')
  const toggle = () => {
    collapsed.value = !collapsed.value
  }
  const setDocumentTypeCode = (next: string) => {
    documentTypeCode.value = next || ''
    if (documentTypeCode.value) localStorage.setItem('fdc.documentTypeCode', documentTypeCode.value)
    else localStorage.removeItem('fdc.documentTypeCode')
  }

  return {
    collapsed,
    documentTypeCode,
    toggle,
    setDocumentTypeCode
  }
})
