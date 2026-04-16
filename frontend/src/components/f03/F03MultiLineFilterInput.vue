<template>
  <div class="f03-mlfi" :title="multiLineTooltip">
    <el-input
      :model-value="mainDisplay"
      class="f03-mlfi__input f02-control"
      :class="{ 'is-multiline-summary': isMultiLineLocked }"
      :readonly="isMultiLineLocked"
      clearable
      :placeholder="placeholder"
      @clear="onClear"
      @paste="onPasteMain"
      @update:model-value="onMainInput"
    >
      <template #suffix>
        <el-tooltip content="多行录入" placement="top">
          <button
            type="button"
            class="f03-mlfi__trigger"
            aria-label="打开多行录入"
            @click.stop="openDrawer"
          >
            <el-icon :size="16"><EditPen /></el-icon>
          </button>
        </el-tooltip>
      </template>
    </el-input>

    <el-drawer
      v-model="drawerVisible"
      :title="drawerTitle"
      direction="rtl"
      size="420px"
      append-to-body
      class="f03-mlfi__drawer"
    >
      <p class="f03-mlfi__hint">{{ drawerHint }}</p>
      <el-input
        v-model="draft"
        type="textarea"
        :rows="14"
        resize="vertical"
        placeholder="每行一条，空行忽略；最多100 行"
        class="f03-mlfi__textarea"
      />
      <template #footer>
        <div class="f03-mlfi__footer">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" @click="applyDraft">确定</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { EditPen } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, ref } from 'vue'
import { MULTI_VALUE_MAX, parseMultiValueLines } from '../../utils/multiValueQuery'

const props = withDefaults(
  defineProps<{
    modelValue: string
    placeholder?: string
    drawerTitle?: string
    /** 抽屉内说明，可选 */
    drawerHint?: string
  }>(),
  {
    placeholder: '',
    drawerTitle: '多行录入',
    drawerHint: '与主输入框共用同一筛选值：仅一行时在主框直接输入即可；多值时请在此每行一条。'
  }
)

const emit = defineEmits<{
  'update:modelValue': [string]
}>()

const drawerVisible = ref(false)
const draft = ref('')

const isMultiLineLocked = computed(() => {
  const v = props.modelValue ?? ''
  const s = String(v)
  if (!s.trim()) return false
  if (/[\r\n]/.test(s)) return true
  return parseMultiValueLines(s).length > 1
})

const mainDisplay = computed(() => {
  const v = props.modelValue ?? ''
  const s = String(v)
  if (!s.trim()) return ''
  if (isMultiLineLocked.value) {
    const n = parseMultiValueLines(s).length
    return `已录入 ${n} 条`
  }
  return s
})

const multiLineTooltip = computed(() => {
  if (!isMultiLineLocked.value) return undefined
  return parseMultiValueLines(props.modelValue ?? '').join('\n')
})

function onMainInput(val: string) {
  if (isMultiLineLocked.value) return
  const s = val ?? ''
  // 少数环境会在主框里短暂保留换行；统一成与抽屉一致的「每行一条」存储，避免后端按多条精确匹配时解析异常
  if (/[\r\n]/.test(s)) {
    const lines = parseMultiValueLines(s)
    emit('update:modelValue', lines.length > 0 ? lines.join('\n') : '')
    return
  }
  emit('update:modelValue', s)
}

/**
 * 主框是单行 input：粘贴多行时浏览器常去掉换行，把多段拼成一串，后端只会当成一条无效精确值 → 查不到。
 * 从剪贴板取原文并按多行规范化写入 modelValue。
 */
function onPasteMain(e: ClipboardEvent) {
  if (isMultiLineLocked.value) return
  const text = e.clipboardData?.getData('text/plain') ?? ''
  if (!text || !/[\r\n]/.test(text)) return
  const lines = parseMultiValueLines(text)
  if (lines.length <= 1) return
  e.preventDefault()
  emit('update:modelValue', lines.join('\n'))
}

function onClear() {
  emit('update:modelValue', '')
}

function openDrawer() {
  draft.value = props.modelValue ?? ''
  drawerVisible.value = true
}

function applyDraft() {
  const lines = parseMultiValueLines(draft.value)
  if (lines.length > MULTI_VALUE_MAX) {
    ElMessage.warning(`最多 ${MULTI_VALUE_MAX} 行，请删减后重试`)
    return
  }
  const normalized = lines.join('\n')
  emit('update:modelValue', normalized)
  drawerVisible.value = false
}
</script>

<style scoped>
.f03-mlfi {
  width: 100%;
  min-width: 0;
}
.f03-mlfi__input :deep(.el-input__wrapper) {
  padding-right: 6px;
}
.f03-mlfi__input.is-multiline-summary :deep(.el-input__inner) {
  color: var(--el-text-color-secondary);
  cursor: default;
}
.f03-mlfi__trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 4px;
  padding: 4px;
  border: none;
  background: transparent;
  color: var(--el-color-primary);
  border-radius: 4px;
  cursor: pointer;
  line-height: 1;
}
.f03-mlfi__trigger:hover {
  background: var(--el-fill-color-light);
  color: var(--el-color-primary-light-3);
}
.f03-mlfi__hint {
  margin: 0 0 12px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}
.f03-mlfi__textarea {
  width: 100%;
}
.f03-mlfi__footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
