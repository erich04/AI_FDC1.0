<template>
  <div class="app-page">
    <div class="page-summary-grid">
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">盘点任务</div>
        <div class="summary-card__value">{{ tasks.length }}</div>
        <div class="summary-card__meta">支持全盘、抽盘和专项盘点</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">处理中任务</div>
        <div class="summary-card__value">{{ activeTaskCount }}</div>
        <div class="summary-card__meta">任务执行、结果审批和报告归档</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">异常总数</div>
        <div class="summary-card__value">{{ abnormalCount }}</div>
        <div class="summary-card__meta">盘盈、盘亏、错位与异常状态</div>
      </el-card>
      <el-card class="summary-card" shadow="never">
        <div class="summary-card__label">责任人覆盖</div>
        <div class="summary-card__value">{{ ownerCount }}</div>
        <div class="summary-card__meta">按库房和范围分配执行责任</div>
      </el-card>
    </div>

    <el-card class="page-section-card" shadow="never">
      <template #header>新增盘点任务</template>
      <el-form :model="form" label-width="88px" class="page-form-grid">
        <el-form-item label="库房编码"><el-input v-model="form.warehouseCode" /></el-form-item>
        <el-form-item label="盘点范围"><el-input v-model="form.inventoryScope" /></el-form-item>
        <el-form-item label="负责人"><el-input v-model="form.owner" /></el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div class="form-actions">
        <el-button @click="resetForm">重置</el-button>
        <el-button type="primary" @click="submit">创建任务</el-button>
      </div>
    </el-card>

    <el-card class="page-section-card" shadow="never">
      <template #header>
        <div class="page-toolbar">
          <div class="page-toolbar__left"><span>盘点任务</span></div>
          <div class="page-toolbar__right">
            <el-tooltip content="列设置" placement="top"><el-button circle :icon="Setting" @click="notifyColumnSetting" /></el-tooltip>
            <el-tooltip :content="listFullPage ? '退出全页面展示' : '列表栏信息全页面展示'" placement="top"><el-button circle :icon="FullScreen" @click="listFullPage = !listFullPage" /></el-tooltip>
            <el-tooltip content="刷新数据" placement="top"><el-button circle :icon="RefreshRight" @click="loadData" /></el-tooltip>
          </div>
        </div>
      </template>
      <el-table :data="tasks" stripe border>
        <el-table-column prop="taskCode" label="任务号" min-width="140" />
        <el-table-column prop="warehouseCode" label="库房" width="120" />
        <el-table-column prop="inventoryScope" label="范围" min-width="240" show-overflow-tooltip />
        <el-table-column prop="taskStatus" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.taskStatus === 'COMPLETED' ? 'success' : 'warning'" effect="light">
              {{ row.taskStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="abnormalCount" label="异常数" width="100" />
        <el-table-column prop="owner" label="负责人" width="120" />
        <el-table-column prop="dueDate" label="截止日期" width="140" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { FullScreen, RefreshRight, Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { createInventoryTask, fetchInventoryTasks, type InventoryCreateCommand } from '../../api/modules/lifecycle'
import type { InventoryTask } from '../../types'

const form = reactive<InventoryCreateCommand>({ warehouseCode: 'WH-001', inventoryScope: '', owner: '档案管理员', dueDate: '' })
const tasks = ref<InventoryTask[]>([])
const listFullPage = ref(false)
const activeTaskCount = computed(() => tasks.value.filter(item => item.taskStatus !== 'COMPLETED').length)
const abnormalCount = computed(() => tasks.value.reduce((sum, item) => sum + item.abnormalCount, 0))
const ownerCount = computed(() => new Set(tasks.value.map(item => item.owner)).size)

const loadData = async () => {
  tasks.value = await fetchInventoryTasks()
}

const resetForm = () => {
  form.warehouseCode = 'WH-001'
  form.inventoryScope = ''
  form.owner = '档案管理员'
  form.dueDate = ''
}

const submit = async () => {
  await createInventoryTask(form)
  ElMessage.success('盘点任务已创建')
  resetForm()
  await loadData()
}

const notifyColumnSetting = () => {
  ElMessage.info('列设置入口已保留，当前版本不展示具体列配置面板。')
}

onMounted(loadData)
</script>

<style scoped>
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
