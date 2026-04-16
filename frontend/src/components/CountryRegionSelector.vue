<template>
  <el-popover
    v-model:visible="popupVisible"
    placement="bottom-start"
    :width="460"
    trigger="click"
    :disabled="disabled"
  >
    <template #reference>
      <el-input
        :model-value="displayText"
        readonly
        :disabled="disabled"
        :clearable="clearable"
        placeholder="请选择归档地（国家/省份/城市）"
        @clear="clearSelection"
      />
    </template>

    <div class="panel-wrap">
      <el-cascader-panel
        v-model="draftPath"
        :options="options"
        :props="cascaderProps"
      />
      <div class="panel-actions">
        <el-button size="small" @click="cancelSelection">取消</el-button>
        <el-button type="primary" size="small" @click="confirmSelection">确定</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { fetchCountryRegions } from '../api/modules/countryRegion'
import type { CountryRegionItem } from '../types'

const props = withDefaults(defineProps<{
  modelValue?: string
  disabled?: boolean
  clearable?: boolean
}>(), {
  modelValue: '',
  disabled: false,
  clearable: true
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

interface RegionOption {
  value: string
  label: string
  children?: RegionOption[]
}

const options = ref<RegionOption[]>([])
const selectedPath = ref<string[]>([])
const draftPath = ref<string[]>([])
const codeToPathMap = ref<Record<string, string[]>>({})
const popupVisible = ref(false)

const cascaderProps = {
  checkStrictly: true,
  emitPath: true,
  value: 'value',
  label: 'label',
  children: 'children'
}

const confirmSelection = () => {
  if (!draftPath.value.length) {
    selectedPath.value = []
    emit('update:modelValue', '')
    popupVisible.value = false
    return
  }
  selectedPath.value = [...draftPath.value]
  emit('update:modelValue', selectedPath.value[selectedPath.value.length - 1] || '')
  popupVisible.value = false
}

const cancelSelection = () => {
  draftPath.value = [...selectedPath.value]
  popupVisible.value = false
}

const clearSelection = () => {
  selectedPath.value = []
  draftPath.value = []
  emit('update:modelValue', '')
}

const syncFromModel = async (value: string) => {
  const cityCode = String(value || '').trim()
  if (!cityCode) {
    selectedPath.value = []
    draftPath.value = []
    return
  }
  const path = codeToPathMap.value[cityCode] ?? []
  selectedPath.value = path
  draftPath.value = [...path]
}

const displayText = computed(() => {
  if (!selectedPath.value.length) {
    return ''
  }
  const labels: string[] = []
  let nodes = options.value
  for (const code of selectedPath.value) {
    const current = nodes.find(item => item.value === code)
    if (!current) break
    labels.push(current.label)
    nodes = current.children || []
  }
  return labels.join('/')
})

watch(() => props.modelValue, (value) => {
  void syncFromModel(value || '')
}, { immediate: true })

const loadOptions = async () => {
  const countries = await fetchCountryRegions({ regionLevel: 'COUNTRY' })
  const countryNodes: RegionOption[] = []
  const pathMap: Record<string, string[]> = {}
  for (const country of countries) {
    pathMap[country.regionCode] = [country.regionCode]
    const provinces = await fetchCountryRegions({ regionLevel: 'PROVINCE', parentRegionCode: country.regionCode })
    const provinceNodes: RegionOption[] = []
    for (const province of provinces) {
      pathMap[province.regionCode] = [country.regionCode, province.regionCode]
      const cities = await fetchCountryRegions({ regionLevel: 'CITY', parentRegionCode: province.regionCode })
      const cityNodes = cities.map((city: CountryRegionItem) => {
        pathMap[city.regionCode] = [country.regionCode, province.regionCode, city.regionCode]
        return {
          value: city.regionCode,
          label: city.regionName
        }
      })
      provinceNodes.push({
        value: province.regionCode,
        label: province.regionName,
        children: cityNodes
      })
    }
    countryNodes.push({
      value: country.regionCode,
      label: country.regionName,
      children: provinceNodes
    })
  }
  options.value = countryNodes
  codeToPathMap.value = pathMap
  await syncFromModel(props.modelValue || '')
}

void loadOptions()
</script>

<style scoped>
.panel-wrap {
  display: grid;
  gap: 8px;
}

.panel-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
