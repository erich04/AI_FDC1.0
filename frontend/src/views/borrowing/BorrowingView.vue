<template>
  <div class="borrow-page">
    <div class="hero">
      <div>
        <span class="eyebrow">Borrowing Workspace</span>
        <h2>借阅、续借与我的借阅文档</h2>
        <p>覆盖借阅申请、借阅审批、借阅需求分析、借出审批、借阅办理、续借申请/审批与查询模块。</p>
        <div class="hero-toolbar">
          <el-select v-model="selectedWorkflowUserId" style="width: 280px" @change="loadLiveData">
            <el-option
              v-for="user in workflowUsers"
              :key="user.id"
              :label="`${user.name}（${user.role}）`"
              :value="user.id"
            />
          </el-select>
          <el-button :loading="liveLoading" @click="loadLiveData">刷新真实数据</el-button>
        </div>
      </div>
      <div class="hero-cards">
        <el-card v-for="item in summaryCards" :key="item.label" shadow="hover">
          <div class="metric-label">{{ item.label }}</div>
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-desc">{{ item.desc }}</div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="borrowNoticeVisible" :close-on-click-modal="false" :close-on-press-escape="false" width="720px" class="borrow-notice-dialog" append-to-body>
      <template #header>
        <div class="borrow-notice-title">{{ borrowNoticeConfig.title }}</div>
      </template>
      <div class="borrow-notice-body">
        <p class="borrow-notice-lead">{{ borrowNoticeConfig.description }}</p>
        <el-alert :title="borrowNoticeConfig.highlight" type="warning" :closable="false" show-icon />
        <div class="notice-section">
          <h3>借阅前请确认</h3>
          <ul>
            <li v-for="item in borrowNoticeConfig.rules" :key="item">{{ item }}</li>
          </ul>
        </div>
        <div class="notice-section">
          <h3>办理说明</h3>
          <ul>
            <li v-for="item in borrowNoticeConfig.tips" :key="item">{{ item }}</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" round @click="confirmBorrowNotice">{{ borrowNoticeConfig.confirmText }}</el-button>
      </template>
    </el-dialog>

    <el-tabs v-model="activeModule">
      <el-tab-pane label="我的待办" name="todo">
        <el-card shadow="never">
          <template #header>我的待办</template>
          <el-table :data="todoRows" border>
            <el-table-column prop="moduleLabel" label="业务类型" min-width="120" />
            <el-table-column prop="businessKey" label="申请单号" min-width="180">
              <template #default="{ row }">
                <el-button link type="primary" @click="openTodo(row)">{{ row.businessKey }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="taskName" label="当前环节" min-width="180" />
            <el-table-column prop="assigneeName" label="当前处理人" min-width="120" />
            <el-table-column prop="createTime" label="待办时间" min-width="160" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button link type="primary" @click="openTodo(row)">进入处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="我的草稿箱" name="drafts">
        <el-card shadow="never">
          <template #header>我的草稿箱</template>
          <el-table :data="draftRows" border empty-text="暂无草稿">
            <el-table-column prop="moduleLabel" label="业务类型" min-width="120" />
            <el-table-column prop="businessKey" label="申请单号" min-width="180">
              <template #default="{ row }">
                <el-button link type="primary" @click="openDraft(row)">{{ row.businessKey }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="taskName" label="当前环节" min-width="180" />
            <el-table-column prop="assigneeName" label="当前处理人" min-width="120" />
            <el-table-column prop="createTime" label="待办时间" min-width="160" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button link type="primary" @click="openDraft(row)">继续填写</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="借阅工作台" name="borrow">
        <div class="stage-stack">
          <div class="flow-banner">
            <div>
              <div class="flow-title">借阅申请</div>
              <div class="flow-code">{{ borrowOrder.orderNo }}</div>
            </div>
            <el-tag type="primary" effect="light">{{ borrowDisplayStatus }}</el-tag>
          </div>
          <el-card shadow="never">
            <template #header>流程</template>
            <el-steps :active="0" align-center finish-status="success">
              <el-step v-for="step in borrowSteps" :key="step" :title="step" />
            </el-steps>
          </el-card>
          <el-card shadow="never">
            <template #header>申请单信息</template>
            <el-form label-position="top" class="form-grid two">
              <el-form-item label="申请单号"><el-input :model-value="borrowOrder.orderNo" readonly /></el-form-item>
              <el-form-item label="单号状态"><el-input :model-value="currentOrderStatusLabel" readonly /></el-form-item>
            </el-form>
          </el-card>
          <el-card shadow="never">
            <template #header>申请人信息</template>
            <el-form label-position="top" class="form-grid four">
              <el-form-item label="使用人">
                <el-select v-model="borrowForm.userName" @change="syncDepartment">
                  <el-option v-for="user in users" :key="user.name" :label="user.name" :value="user.name" />
                </el-select>
              </el-form-item>
              <el-form-item label="使用人部门"><el-input :model-value="borrowForm.department" readonly /></el-form-item>
              <el-form-item label="申请人"><el-input v-model="borrowForm.applicant" readonly /></el-form-item>
              <el-form-item label="申请时间"><el-input v-model="borrowForm.applyTime" readonly /></el-form-item>
            </el-form>
          </el-card>
          <el-card shadow="never">
            <template #header>申请原因</template>
            <el-form label-position="top" class="form-grid two">
              <el-form-item label="申请用途">
                <el-select v-model="borrowForm.purpose">
                  <el-option v-for="item in purposes" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
              <el-form-item label="背景说明和附件">
                <el-upload action="#" :auto-upload="false" multiple>
                  <el-button type="primary" plain>上传附件</el-button>
                </el-upload>
              </el-form-item>
              <el-form-item label="申请原因/背景说明" class="span-all">
                <el-input v-model="borrowForm.reason" type="textarea" :rows="4" />
              </el-form-item>
            </el-form>
          </el-card>
          <el-card shadow="never">
            <template #header>
              <div class="header-row">
                <span>借阅内容</span>
                <div>
                  <el-button size="small" type="primary" @click="addBorrowItem">新增</el-button>
                  <el-button size="small" type="danger" plain :disabled="selectedBorrowItems.length === 0" @click="removeSelectedBorrowItems">删除</el-button>
                  <el-button size="small" @click="openAiDialog">AI 智能借阅推荐</el-button>
                </div>
              </div>
            </template>
            <el-table :data="borrowItems" border @selection-change="onBorrowItemSelectionChange">
              <el-table-column type="selection" width="50" />
              <el-table-column min-width="170">
                <template #header><span class="required-label">公司</span></template>
                <template #default="{ row }">
                  <el-select v-model="row.company" filterable placeholder="请选择公司">
                    <el-option v-for="item in companyOptionsForSelect" :key="item.companyCode" :label="formatCompanyOption(item)" :value="item.companyName" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column min-width="150">
                <template #header><span class="required-label">文档类型</span></template>
                <template #default="{ row }">
                  <el-select v-model="row.documentType" filterable placeholder="请选择文档类型">
                    <el-option v-for="item in documentTypeOptionsForSelect" :key="item.typeCode" :label="formatDocumentTypeOption(item)" :value="item.typeName" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column min-width="240">
                <template #header><span class="required-label">需求文档详细说明</span></template>
                <template #default="{ row }"><el-input v-model="row.description" type="textarea" :rows="2" placeholder="请输入需求文档详细说明" /></template>
              </el-table-column>
              <el-table-column min-width="130">
                <template #header><span class="required-label">需求类型</span></template>
                <template #default="{ row }"><el-select v-model="row.demandType" placeholder="请选择需求类型" @change="onBorrowDemandTypeChange(row)"><el-option v-for="item in demandTypes" :key="item" :label="item" :value="item" /></el-select></template>
              </el-table-column>
              <el-table-column label="是否归还" width="100"><template #default="{ row }"><el-switch v-model="row.needReturn" /></template></el-table-column>
              <el-table-column min-width="150">
                <template #header><span :class="{ 'required-label': hasReturnRequiredBorrowItem }">预计归还时间</span></template>
                <template #default="{ row }"><el-date-picker v-model="row.expectedReturnDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择预计归还时间" style="width:100%" /></template>
              </el-table-column>
              <el-table-column label="操作" width="90"><template #default="{ $index }"><el-button link type="danger" @click="removeBorrowItem($index)">删除</el-button></template></el-table-column>
            </el-table>
          </el-card>
          <el-card shadow="never">
            <template #header>审核</template>
            <el-form label-position="top" class="form-grid three">
              <el-form-item label="需求审批人"><el-select v-model="borrowForm.demandApprover"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
              <el-form-item label="需求审核人"><el-select v-model="borrowForm.demandReviewer"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
              <el-form-item label="抄送"><el-select v-model="borrowForm.ccUsers" multiple collapse-tags><el-option v-for="item in ccUsers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
            </el-form>
          </el-card>
          <el-card shadow="never">
            <template #header>
              <div class="header-row">
                <span>关联申请单</span>
                <el-button link type="primary" @click="relatedOrdersCollapsed = !relatedOrdersCollapsed">{{ relatedOrdersCollapsed ? '展开' : '折叠' }}</el-button>
              </div>
            </template>
            <el-table v-show="!relatedOrdersCollapsed" :data="relatedBorrowOrders" border empty-text="当前申请单暂无关联子单">
              <el-table-column prop="mainOrderNo" label="主单申请单号" min-width="180" />
              <el-table-column label="子单申请单号" min-width="180">
                <template #default="{ row }">
                  <el-button link type="primary" @click="openRelatedBorrowOrder(row.childOrderNo)">{{ row.childOrderNo }}</el-button>
                </template>
              </el-table-column>
              <el-table-column prop="childHandler" label="子单处理人" min-width="140" />
              <el-table-column prop="childStatus" label="子单状态" min-width="140" />
              <el-table-column prop="childRejectReason" label="子单驳回原因" min-width="260" show-overflow-tooltip />
            </el-table>
          </el-card>
          <workflow-log-card :logs="displayBorrowLogs" title="日志" />
          <div class="actions">
            <el-button @click="resetBorrow">重置</el-button>
            <el-button :loading="draftSaving" @click="saveBorrowDraft">保存为草稿</el-button>
            <el-button type="primary" :loading="borrowSubmitting" @click="submitBorrow">提交申请</el-button>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="续借功能" name="renew">
        <el-tabs v-model="activeRenewStage">
          <el-tab-pane label="可续借申请单查询" name="query">
            <div class="renew-query-page">
              <header class="renew-page-title">
                <div>
                  <h1>续借申请</h1>
                  <p>查询当前用户可续借的借阅申请单，勾选后发起续借流程。</p>
                </div>
              </header>
              <section class="renew-panel renew-filter-panel">
                <div class="renew-section-title"><span></span><h2>查询条件</h2></div>
                <el-form label-position="top" class="renew-filter-grid">
                  <el-form-item label="申请单号"><el-input v-model="renewQuery.orderNo" placeholder="请输入申请单号" /></el-form-item>
                  <el-form-item label="申请时间">
                    <div class="range-fields"><el-date-picker v-model="renewQuery.applyStart" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" /><span>至</span><el-date-picker v-model="renewQuery.applyEnd" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" /></div>
                  </el-form-item>
                  <el-form-item label="申请类型"><el-select v-model="renewQuery.applyType" clearable placeholder="全部类型"><el-option label="常规续借" value="常规续借" /><el-option label="紧急续借" value="紧急续借" /></el-select></el-form-item>
                  <el-form-item label="申请人"><el-input v-model="renewQuery.applicant" placeholder="申请人姓名" /></el-form-item>
                  <el-form-item label="当前处理人"><el-input v-model="renewQuery.currentHandler" placeholder="处理人姓名" /></el-form-item>
                  <el-form-item label="公司/主体"><el-select v-model="renewQuery.company" clearable placeholder="全部主体"><el-option v-for="item in companies" :key="item" :label="item" :value="item" /></el-select></el-form-item>
                  <el-form-item label="业务文档编号"><el-input v-model="renewQuery.businessCode" placeholder="DOC-2024-001" /></el-form-item>
                  <el-form-item label="业务模块"><el-input v-model="renewQuery.businessModule" placeholder="所属模块" /></el-form-item>
                  <el-form-item label="档案期间"><el-date-picker v-model="renewQuery.archivePeriod" type="month" value-format="YYYY-MM" placeholder="选择月份" /></el-form-item>
                </el-form>
                <div class="renew-filter-actions"><el-button @click="resetRenewQuery">重置</el-button><el-button type="primary" @click="loadLiveData">查询</el-button></div>
              </section>
              <section class="renew-panel renew-result-panel">
                <div class="renew-result-toolbar">
                  <div><el-button type="primary" round :disabled="selectedRenewOrders.length === 0" @click="goRenewApply">续借申请</el-button><el-tag type="primary" effect="plain">已选 {{ selectedRenewOrders.length }} 条</el-tag></div>
                  <el-button text>导出</el-button>
                </div>
                <el-table :data="filteredRenewOrders" @selection-change="onRenewOrderSelect">
                  <el-table-column type="selection" width="58" />
                  <el-table-column label="申请单号" min-width="170"><template #default="{ row }"><el-button link type="primary" @click="startRenewFromRow(row)">{{ row.orderNo }}</el-button></template></el-table-column>
                  <el-table-column prop="applyTime" label="申请时间" min-width="160" />
                  <el-table-column prop="borrowTime" label="借出时间" min-width="140" />
                  <el-table-column label="过期时间" min-width="140"><template #default="{ row }"><span class="renew-expire">{{ row.expireTime }}</span></template></el-table-column>
                  <el-table-column prop="currentHandler" label="当前处理人" min-width="150" />
                  <el-table-column label="状态" min-width="120"><template #default><el-tag type="warning" effect="light">可续借</el-tag></template></el-table-column>
                </el-table>
                <div class="renew-pagination"><span>共 {{ filteredRenewOrders.length }} 条可续借申请</span><div><el-button disabled text>上一页</el-button><el-button type="primary">1</el-button><el-button text>下一页</el-button></div></div>
              </section>
            </div>
          </el-tab-pane>
          <el-tab-pane label="续借申请" name="apply">
            <div class="renew-apply-layout">
              <main class="renew-apply-main">
                <header class="renew-apply-title">
                  <h1>续借申请</h1>
                  <div class="renew-meta-row">
                    <div><span>申请单号</span><strong>{{ renewForm.renewOrderNo }}</strong></div>
                    <div><span>原借阅单号</span><strong>{{ renewForm.sourceOrderNo }}</strong></div>
                    <div><span>状态</span><el-tag type="warning" effect="light">待提交</el-tag></div>
                  </div>
                </header>
                <section class="renew-top-flow">
                  <el-steps :active="1" align-center finish-status="success">
                    <el-step title="申请" :description="renewForm.applicant || '当前申请人'" />
                    <el-step title="需求审核人" :description="renewForm.reviewer || '待选择'" />
                    <el-step title="续借办理" :description="renewForm.handler || '未开始'" />
                  </el-steps>
                </section>
                <section class="renew-editorial-card">
                  <div class="renew-section-title"><span></span><h2>申请人信息</h2></div>
                  <el-form label-position="top" class="renew-apply-grid three">
                    <el-form-item label="文档使用人"><el-input v-model="renewForm.userName" readonly /></el-form-item>
                    <el-form-item label="申请人"><el-input v-model="renewForm.applicant" readonly /></el-form-item>
                    <el-form-item label="使用人部门"><el-input v-model="renewForm.department" readonly /></el-form-item>
                  </el-form>
                </section>
                <section class="renew-editorial-card">
                  <div class="renew-section-title"><span></span><h2>借阅原因</h2></div>
                  <el-form label-position="top" class="renew-apply-grid">
                    <el-form-item label="申请用途"><el-input v-model="renewForm.purpose" readonly /></el-form-item>
                    <el-form-item label="具体申请原因/背景说明"><el-input v-model="renewForm.reason" type="textarea" :rows="4" readonly /></el-form-item>
                    <el-form-item label="背景说明支撑附件"><div class="renew-upload-placeholder"><span>附件已上传</span><small>{{ renewForm.attachment || '暂无附件' }}</small></div></el-form-item>
                  </el-form>
                </section>
                <section class="renew-editorial-card">
                  <div class="renew-section-title"><span></span><h2>文档借阅信息</h2></div>
                  <el-table :data="renewItems" @selection-change="onRenewItemSelect">
                    <el-table-column type="selection" width="54" />
                    <el-table-column prop="businessCode" label="文档业务编码" min-width="150" />
                    <el-table-column prop="documentName" label="文档名称" min-width="220" />
                    <el-table-column prop="company" label="公司/主体" min-width="150" />
                    <el-table-column prop="businessModule" label="业务模块" min-width="140" />
                    <el-table-column prop="archivePeriod" label="档期" min-width="120" />
                    <el-table-column prop="borrowType" label="借阅类型" min-width="120" />
                    <el-table-column prop="borrowTime" label="借出时间" min-width="120" />
                    <el-table-column prop="currentExpireTime" label="过期时间" min-width="120" />
                  </el-table>
                  <el-form label-position="top" class="renew-extend-form">
                    <el-form-item label="申请归还日期" required>
                      <el-date-picker v-model="renewForm.returnDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择续借后的归还日期" style="width:100%" />
                    </el-form-item>
                    <el-form-item label="续借原因" required>
                      <el-input v-model="renewForm.renewReason" type="textarea" :rows="3" placeholder="请输入本次续借原因" />
                    </el-form-item>
                  </el-form>
                </section>
                <section class="renew-editorial-card">
                  <div class="renew-section-title"><span></span><h2>审批</h2></div>
                  <el-form label-position="top" class="renew-apply-grid three">
                    <el-form-item label="需求审核人"><el-select v-model="renewForm.reviewer"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
                    <el-form-item label="办理人"><el-select v-model="renewForm.handler"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
                    <el-form-item label="抄送"><el-select v-model="renewForm.ccUsers" multiple collapse-tags><el-option v-for="item in ccUsers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
                  </el-form>
                </section>
                <workflow-log-card :logs="renewLogs" title="流程日志" />
              </main>
              <footer class="renew-apply-footer"><el-button @click="activeRenewStage = 'query'">返回查询</el-button><el-button type="primary" round @click="submitRenew">提交续借申请</el-button></footer>
            </div>
          </el-tab-pane>
          <el-tab-pane label="续借审批" name="approval">
            <div class="stage-stack">
              <page-head title="续借审批" :order-no="renewForm.renewOrderNo" status="待需求审核" />
              <el-card shadow="never"><template #header>续借信息</template><el-descriptions :column="2" border><el-descriptions-item label="续借申请单号">{{ renewForm.renewOrderNo }}</el-descriptions-item><el-descriptions-item label="原申请单号">{{ renewForm.sourceOrderNo }}</el-descriptions-item><el-descriptions-item label="使用人">{{ renewForm.userName }}</el-descriptions-item><el-descriptions-item label="使用人部门">{{ renewForm.department }}</el-descriptions-item><el-descriptions-item label="申请人">{{ renewForm.applicant }}</el-descriptions-item><el-descriptions-item label="申请用途">{{ renewForm.purpose }}</el-descriptions-item><el-descriptions-item label="申请原因/背景说明" :span="2">{{ renewForm.reason }}</el-descriptions-item></el-descriptions></el-card>
              <el-card shadow="never"><template #header>续借文档明细</template><el-table :data="selectedRenewItems" border><el-table-column prop="businessCode" label="文档业务编号" min-width="150" /><el-table-column prop="documentName" label="文档名称" min-width="200" /><el-table-column prop="borrowType" label="借阅类型" min-width="110" /><el-table-column prop="currentExpireTime" label="原归还时间" min-width="120" /><el-table-column prop="renewExpireTime" label="续借申请归还时间" min-width="150" /><el-table-column prop="renewReason" label="续借原因" min-width="220" /></el-table></el-card>
              <decision-card v-model="renewApproval.decision" :comment="renewApproval.comment" @update:comment="renewApproval.comment = $event" @submit="approveRenew(renewApproval.decision)" title="审批" positive="同意" negative="拒绝" />
              <el-card shadow="never"><template #header>下一步处理人/抄送</template><el-form label-position="top" class="form-grid two"><el-form-item label="下一步处理人"><el-select v-model="renewApproval.nextHandler"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select></el-form-item><el-form-item label="抄送"><el-select v-model="renewApproval.ccUsers" multiple collapse-tags><el-option v-for="item in ccUsers" :key="item" :label="item" :value="item" /></el-select></el-form-item></el-form></el-card>
              <workflow-log-card :logs="renewLogs" title="流程日志" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>

      <el-tab-pane label="我的申请" name="applications">
        <el-card shadow="never">
          <template #header>我的申请</template>
          <el-table :data="myApplicationRows" border>
            <el-table-column prop="moduleLabel" label="业务类型" min-width="120" />
            <el-table-column label="申请单号" min-width="180">
              <template #default="{ row }">
                <el-button link type="primary" @click="openMyApplication(row)">{{ row.businessKey }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="applyTime" label="申请时间" min-width="160" />
            <el-table-column prop="statusLabel" label="当前状态" min-width="140" />
            <el-table-column prop="currentHandler" label="当前处理人" min-width="140" />
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的借阅文档" name="mine">
        <el-card shadow="never"><template #header>我的借阅文档</template><el-form label-position="top" class="form-grid four"><el-form-item label="公司"><el-select v-model="myBorrowQuery.company" clearable><el-option v-for="item in companies" :key="item" :label="item" :value="item" /></el-select></el-form-item><el-form-item label="文档业务编号"><el-input v-model="myBorrowQuery.businessCode" /></el-form-item><el-form-item label="文档名称"><el-input v-model="myBorrowQuery.documentName" /></el-form-item><el-form-item label="文档类型"><el-select v-model="myBorrowQuery.documentType" clearable><el-option v-for="item in docTypes" :key="item" :label="item" :value="item" /></el-select></el-form-item><el-form-item label="业务模块"><el-input v-model="myBorrowQuery.businessModule" /></el-form-item><el-form-item label="档期"><el-input v-model="myBorrowQuery.archivePeriod" /></el-form-item><el-form-item label="申请单号"><el-input v-model="myBorrowQuery.orderNo" /></el-form-item><el-form-item label="办理状态"><el-select v-model="myBorrowQuery.status" clearable><el-option v-for="item in myStatuses" :key="item" :label="item" :value="item" /></el-select></el-form-item><el-form-item label="借出时间"><el-date-picker v-model="myBorrowQuery.borrowTime" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-form><el-table :data="filteredMine" border><el-table-column prop="company" label="公司" min-width="150" /><el-table-column prop="businessCode" label="文档业务编号" min-width="150" /><el-table-column prop="documentName" label="文档名称" min-width="200" /><el-table-column prop="documentType" label="文档类型" min-width="120" /><el-table-column prop="businessModule" label="业务模块" min-width="120" /><el-table-column prop="archivePeriod" label="档期" min-width="100" /><el-table-column prop="orderNo" label="申请单号" min-width="150" /><el-table-column prop="status" label="办理状态" min-width="110" /><el-table-column prop="borrowTime" label="借出时间" min-width="120" /><el-table-column prop="attachment" label="附件" min-width="150" /></el-table></el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="aiVisible" width="1180px" class="ai-recommend-dialog" :show-close="false">
      <div class="ai-recommend-shell">
        <aside class="ai-assistant-panel">
          <div class="ai-assistant-head">
            <div class="ai-bot-icon">AI</div>
            <div>
              <div class="ai-panel-title">AI 助手</div>
              <div class="ai-panel-subtitle">当前上下文：{{ borrowForm.department || '借阅申请' }}</div>
            </div>
          </div>
          <div class="ai-chat-stream">
            <div v-for="msg in aiMessages" :key="msg.id" :class="['ai-chat-message', msg.role]">
              <div class="ai-chat-bubble">
                <strong>{{ msg.role === 'assistant' ? 'AI 推荐' : '我' }}</strong>
                <p>{{ msg.content }}</p>
              </div>
              <span>{{ msg.role === 'assistant' ? '刚刚' : '当前' }}</span>
            </div>
          </div>
          <div class="ai-chat-input">
            <el-input v-model="aiDraft" type="textarea" :rows="4" placeholder="请输入跟进问题，例如：优先推荐华东区域、合同协议、近两年文档" />
            <el-button type="primary" class="ai-send-button" @click="sendAi">发送</el-button>
          </div>
        </aside>
        <main class="ai-recommend-main">
          <header class="ai-recommend-header">
            <div>
              <h3>文档借阅推荐</h3>
              <p>基于使用人部门、申请用途与借阅历史，为本次借阅申请推荐以下文档。</p>
            </div>
            <div class="ai-header-actions">
              <el-button @click="selectAllAiRows">全选</el-button>
              <el-button type="primary" @click="appendAiItem">加入申请</el-button>
              <el-button @click="aiVisible = false">关闭</el-button>
            </div>
          </header>
          <div class="ai-result-card">
            <el-table ref="aiResultTableRef" :data="aiResults" @selection-change="onAiSelectionChange">
              <el-table-column type="selection" width="54" />
              <el-table-column label="文档名称" min-width="230">
                <template #default="{ row }">
                  <div class="ai-doc-name">
                    <span class="ai-doc-icon">文</span>
                    <div>
                      <strong>{{ row.documentName || row.description }}</strong>
                      <el-tag v-if="row.score" size="small" type="warning" effect="dark">AI {{ row.score }}%</el-tag>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="company" label="公司/主体" min-width="160" />
              <el-table-column prop="businessCode" label="文档业务编号" min-width="150" />
              <el-table-column prop="documentType" label="文档类型" min-width="130" />
              <el-table-column label="业务模块" min-width="130">
                <template #default="{ row }"><el-tag effect="plain">{{ row.businessModule }}</el-tag></template>
              </el-table-column>
            </el-table>
          </div>
          <div class="ai-result-footer">
            <span>已显示 {{ aiResults.length }} 份高相关文档，已选择 {{ selectedAiResults.length }} 份。</span>
            <el-button text type="primary" @click="sendAi">显示更多</el-button>
          </div>
        </main>
      </div>
    </el-dialog>

    <el-dialog v-model="detailQueryVisible" title="查询借阅文档明细" width="1100px">
      <div class="stage-stack">
        <el-card shadow="never">
          <template #header>查询条件</template>
          <el-form label-position="top" class="form-grid five">
            <el-form-item label="公司"><el-select v-model="detailQuery.company" clearable><el-option v-for="item in companies" :key="item" :label="item" :value="item" /></el-select></el-form-item>
            <el-form-item label="文档业务编码"><el-input v-model="detailQuery.businessCode" /></el-form-item>
            <el-form-item label="文档类型"><el-select v-model="detailQuery.documentType" clearable><el-option v-for="item in docTypes" :key="item" :label="item" :value="item" /></el-select></el-form-item>
            <el-form-item label="业务模块编码"><el-input v-model="detailQuery.businessModule" /></el-form-item>
            <el-form-item label="档期"><el-input v-model="detailQuery.archivePeriod" /></el-form-item>
          </el-form>
        </el-card>
        <div class="actions"><el-button type="primary" @click="appendSelectedDetailQueryRows">新增</el-button></div>
        <el-card shadow="never">
          <template #header>查询结果</template>
          <el-table :data="filteredDetailQueryRows" border @selection-change="onDetailQuerySelectionChange">
            <el-table-column type="selection" width="48" />
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="company" label="公司" min-width="150" />
            <el-table-column prop="documentName" label="文档名称" min-width="180" />
            <el-table-column prop="businessCode" label="文档业务编码" min-width="160" />
            <el-table-column prop="documentType" label="文档类型" min-width="120" />
            <el-table-column prop="businessModule" label="业务模块编码" min-width="140" />
            <el-table-column prop="archivePeriod" label="档期" min-width="100" />
          </el-table>
        </el-card>
      </div>
    </el-dialog>

    <el-dialog v-model="todoVisible" :title="todoDialogTitle" width="1180px" destroy-on-close>
      <div v-if="todoCurrentModule === 'borrow'" class="stage-stack">
        <page-head :title="todoDialogTitle" :order-no="borrowOrder.orderNo" :status="borrowDisplayStatus" />
        <el-card shadow="never">
          <template #header>流程</template>
          <el-steps :active="todoBorrowStep" align-center finish-status="success">
            <el-step v-for="step in todoBorrowSteps" :key="step" :title="step" />
          </el-steps>
        </el-card>
        <el-card shadow="never" class="process-section">
          <template #header><div class="section-head"><span class="section-accent"></span><span>申请单信息</span></div></template>
          <el-form label-position="top" class="form-grid two">
            <el-form-item label="申请单号"><el-input :model-value="borrowOrder.orderNo" readonly /></el-form-item>
            <el-form-item label="单号状态"><el-input :model-value="currentOrderStatusLabel" readonly /></el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="process-section">
          <template #header><div class="section-head"><span class="section-accent"></span><span>申请人信息</span></div></template>
          <el-form label-position="top" class="form-grid four">
            <el-form-item label="使用人"><el-input :model-value="borrowForm.userName" readonly /></el-form-item>
            <el-form-item label="申请人"><el-input :model-value="borrowForm.applicant" readonly /></el-form-item>
            <el-form-item label="使用人部门"><el-input :model-value="borrowForm.department" readonly /></el-form-item>
            <el-form-item label="申请时间"><el-input :model-value="borrowForm.applyTime" readonly /></el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="process-section">
          <template #header><div class="section-head"><span class="section-accent"></span><span>借阅原因</span></div></template>
          <el-form label-position="top" class="form-grid two">
            <el-form-item label="申请用途"><el-input :model-value="borrowForm.purpose || '-'" readonly /></el-form-item>
            <el-form-item label="背景说明和附件"><el-input :model-value="borrowForm.attachment || '-'" readonly /></el-form-item>
            <el-form-item label="申请原因/背景说明" class="span-all"><el-input :model-value="borrowForm.reason || '-'" type="textarea" :rows="4" readonly /></el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="process-section">
          <template #header><div class="section-head"><span class="section-accent"></span><span>借阅内容</span></div></template>
          <el-table :data="borrowItems" border>
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="company" label="公司/主体" min-width="170" />
            <el-table-column prop="documentType" label="文档类型" min-width="120" />
            <el-table-column prop="description" label="需求分档详细说明" min-width="260" />
            <el-table-column prop="demandType" label="需求类型" min-width="140" />
            <el-table-column label="是否归还" width="100">
              <template #default="{ row }">{{ row.needReturn ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column prop="expectedReturnDate" label="预计归还时间" min-width="150" />
            <el-table-column v-if="activeBorrowStage === 'handling'" prop="handlerRemark" label="给借阅办理人备注" min-width="180" />
            <el-table-column v-if="activeBorrowStage === 'analysis'" label="借出审批人" min-width="150">
              <template #default="{ row }">
                <el-select v-model="row.lendingApprover">
                  <el-option v-for="item in handlers" :key="item" :label="item" :value="item" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column v-if="activeBorrowStage === 'analysis' || activeBorrowStage === 'lendingApproval'" label="给借出审批人备注" min-width="180">
              <template #default="{ row }">
                <el-input v-if="activeBorrowStage === 'analysis'" v-model="row.lendingRemark" />
                <span v-else>{{ row.lendingRemark || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column v-if="activeBorrowStage === 'analysis'" label="借阅办理人" min-width="150">
              <template #default="{ row }">
                <el-select v-model="row.handler">
                  <el-option v-for="item in handlers" :key="item" :label="item" :value="item" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column v-if="activeBorrowStage === 'analysis'" label="给借阅办理人备注" min-width="180">
              <template #default="{ row }"><el-input v-model="row.handlerRemark" /></template>
            </el-table-column>
          </el-table>
        </el-card>

        <template v-if="activeBorrowStage === 'approval'">
          <el-card shadow="never" class="process-section">
            <template #header><div class="section-head"><span class="section-accent"></span><span>审批</span></div></template>
            <el-form label-position="top" class="form-grid two">
              <el-form-item label="审批操作" class="span-all">
                <div class="inline-approval-actions">
                  <el-button :type="borrowApproval.decision === '同意' ? 'primary' : 'default'" @click="setBorrowApprovalDecision('同意')">同意</el-button>
                  <el-button :type="borrowApproval.decision === '拒绝' ? 'primary' : 'default'" @click="setBorrowApprovalDecision('拒绝')">拒绝</el-button>
                </div>
              </el-form-item>
              <el-form-item label="审批意见" class="span-all">
                <el-input v-model="borrowApproval.comment" type="textarea" :rows="4" placeholder="请输入审批意见，不填写时默认带出审批按钮内容" />
              </el-form-item>
              <el-form-item class="span-all">
                <el-button type="primary" @click="approveBorrow(borrowApproval.decision)">提交</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </template>

        <template v-else-if="currentBorrowTaskDef === 'rejectedReturnTask' || currentBorrowTaskDef === 'applicantReapplyTask'">
          <el-card shadow="never" class="process-section">
            <template #header><div class="section-head"><span class="section-accent"></span><span>退回说明</span></div></template>
            <el-alert title="当前借阅申请已被驳回并退回申请人，请根据审批日志中的意见修改后重新提交。" type="warning" :closable="false" />
          </el-card>
          <el-card shadow="never" class="process-section">
            <template #header><div class="section-head"><span class="section-accent"></span><span>审核</span></div></template>
            <el-form label-position="top" class="form-grid three">
              <el-form-item label="需求审批人"><el-select v-model="borrowForm.demandApprover"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
              <el-form-item label="需求审核人"><el-select v-model="borrowForm.demandReviewer"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
              <el-form-item label="抄送"><el-select v-model="borrowForm.ccUsers" multiple collapse-tags><el-option v-for="item in ccUsers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
              <el-form-item label="审批意见" class="span-all"><el-input v-model="borrowForm.approvalComment" type="textarea" :rows="3" placeholder="请输入重新提交意见" /></el-form-item>
            </el-form>
          </el-card>
          <div class="actions"><el-button type="primary" @click="resubmitBorrow">重新提交</el-button></div>
        </template>

        <template v-else-if="activeBorrowStage === 'analysis'">
          <el-card shadow="never">
            <template #header>审核</template>
            <el-form label-position="top" class="form-grid three">
              <el-form-item label="处理选项"><el-select v-model="analysisAction.action"><el-option label="分单" value="分单" /><el-option label="转他人处理" value="转他人处理" /><el-option label="驳回" value="驳回" /><el-option label="终止" value="终止" /></el-select></el-form-item>
              <el-form-item label="办理人/审批人">
                <el-input v-if="analysisAction.action === '分单'" :model-value="analysisParticipantsDisplay" readonly />
                <el-select v-else v-model="analysisAction.nextHandler"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select>
              </el-form-item>
              <el-form-item label="审批意见" class="span-all"><el-input v-model="analysisAction.comment" type="textarea" :rows="3" placeholder="请输入审批意见" /></el-form-item>
              <el-form-item class="span-all"><el-button type="primary" @click="saveAnalysis">提交</el-button></el-form-item>
            </el-form>
            <el-alert v-if="analysisAction.action === '分单'" title="先按借出审批人分单，再按借阅办理人分单。" type="info" :closable="false" />
          </el-card>
        </template>

        <template v-else-if="activeBorrowStage === 'lendingApproval'">
          <el-card shadow="never" class="process-section">
            <template #header><div class="section-head"><span class="section-accent"></span><span>借出审批</span></div></template>
            <el-form label-position="top" class="form-grid two">
              <el-form-item label="审批操作" class="span-all">
                <div class="inline-approval-actions">
                  <el-button :type="lendingApproval.decision === '同意' ? 'primary' : 'default'" @click="setLendingDecision('同意')">同意</el-button>
                  <el-button :type="lendingApproval.decision === '不同意' ? 'primary' : 'default'" @click="setLendingDecision('不同意')">不同意</el-button>
                </div>
              </el-form-item>
              <el-form-item label="审批意见" class="span-all">
                <el-input v-model="lendingApproval.comment" type="textarea" :rows="4" placeholder="请借出审批人输入审批意见" />
              </el-form-item>
              <el-form-item class="span-all"><el-button type="primary" @click="handleLending(lendingApproval.decision)">提交</el-button></el-form-item>
            </el-form>
          </el-card>
        </template>

        <template v-else-if="activeBorrowStage === 'handling'">
          <el-card shadow="never">
            <template #header><div class="header-row"><span>文档借阅明细信息</span><div><el-button size="small" @click="watermarkFiles">电子件打水印</el-button><el-button size="small" type="primary" @click="openDetailQueryDialog">查询</el-button><el-button size="small" type="danger" plain @click="removeSelectedDetails">删除</el-button></div></div></template>
            <el-table :data="documentDetails" border @selection-change="onDetailSelectionChange">
              <el-table-column type="selection" width="48" />
              <el-table-column label="附件" min-width="160">
                <template #default="{ row }">
                  <div class="file-tag-list">
                    <el-tag v-for="file in row.attachments" :key="file" size="small" effect="plain">{{ file }}</el-tag>
                    <span v-if="!row.attachments.length" class="muted-text">未上传附件</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="借阅类型" min-width="110"><template #default="{ row }"><el-select v-model="row.borrowType"><el-option v-for="item in borrowKinds" :key="item" :label="item" :value="item" /></el-select></template></el-table-column>
              <el-table-column label="借阅份数" width="90"><template #default="{ row }"><el-input-number v-model="row.borrowCount" :min="1" /></template></el-table-column>
              <el-table-column label="公司" min-width="130"><template #default="{ row }"><el-input v-model="row.company" /></template></el-table-column>
              <el-table-column label="业务编码" min-width="130"><template #default="{ row }"><el-input v-model="row.businessCode" /></template></el-table-column>
              <el-table-column label="文档名称" min-width="180"><template #default="{ row }"><el-input v-model="row.documentName" /></template></el-table-column>
              <el-table-column label="文档类型" min-width="120"><template #default="{ row }"><el-input v-model="row.documentType" /></template></el-table-column>
              <el-table-column label="业务模块" min-width="120"><template #default="{ row }"><el-input v-model="row.businessModule" /></template></el-table-column>
              <el-table-column label="档案期" min-width="100"><template #default="{ row }"><el-input v-model="row.archivePeriod" /></template></el-table-column>
              <el-table-column label="库存份数" width="100"><template #default="{ row }"><el-input-number v-model="row.stockCount" :min="0" /></template></el-table-column>
              <el-table-column label="库位" min-width="100"><template #default="{ row }"><el-input v-model="row.location" /></template></el-table-column>
              <el-table-column label="册号" min-width="90"><template #default="{ row }"><el-input v-model="row.volumeNo" /></template></el-table-column>
              <el-table-column label="档案条码" min-width="120"><template #default="{ row }"><el-input v-model="row.barcode" /></template></el-table-column>
              <el-table-column label="文档状态" min-width="100"><template #default="{ row }"><el-input v-model="row.documentStatus" /></template></el-table-column>
              <el-table-column label="水印" width="90"><template #default="{ row }"><el-tag :type="row.watermark ? 'success' : 'info'">{{ row.watermark ? '已打' : '未打' }}</el-tag></template></el-table-column>
              <el-table-column label="邮寄方式" min-width="120"><template #default="{ row }"><el-select v-model="row.postMethod"><el-option label="现场领取" value="现场领取" /><el-option label="快递邮寄" value="快递邮寄" /><el-option label="专人送达" value="专人送达" /></el-select></template></el-table-column>
              <el-table-column label="邮寄单号" min-width="120"><template #default="{ row }"><el-input v-model="row.postNo" /></template></el-table-column>
              <el-table-column label="操作" width="130"><template #default="{ row }"><el-button link type="primary" @click="uploadAttachment(row)">上传附件</el-button></template></el-table-column>
            </el-table>
          </el-card>
          <el-card shadow="never"><template #header>文档附件</template><el-table :data="attachments" border><el-table-column prop="name" label="附件名称" min-width="200" /><el-table-column prop="watermarkStatus" label="水印状态" min-width="120" /><el-table-column prop="protectionStatus" label="文件保护状态" min-width="140" /><el-table-column prop="uploader" label="上传人" min-width="120" /><el-table-column prop="uploadTime" label="上传时间" min-width="160" /><el-table-column label="操作" width="170"><template #default="{ row, $index }"><el-button link type="primary" @click="toggleWatermark(row)">打水印</el-button><el-button link type="danger" @click="removeAttachment($index)">删除</el-button></template></el-table-column></el-table></el-card>
          <el-card shadow="never">
            <template #header>办理借阅</template>
            <el-form label-position="top" class="form-grid three">
              <el-form-item label="选项按钮" class="span-all">
                <el-radio-group v-model="handlingAction.action" class="radio-wrap"><el-radio-button v-for="item in handlingOptions" :key="item" :label="item" :value="item" /></el-radio-group>
              </el-form-item>
              <el-form-item label="领取人/办理人"><el-select v-model="handlingAction.receiver"><el-option v-for="item in handlingReceivers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
              <el-form-item label="抄送人"><el-select v-model="handlingAction.ccUsers" multiple collapse-tags><el-option v-for="item in ccUsers" :key="item" :label="item" :value="item" /></el-select></el-form-item>
              <el-form-item label="审批意见" class="span-all"><el-input v-model="handlingAction.comment" type="textarea" :rows="3" placeholder="请输入审批意见" /></el-form-item>
              <el-form-item class="span-all"><el-button type="primary" @click="finishHandling">提交</el-button></el-form-item>
            </el-form>
          </el-card>
        </template>

        <template v-else-if="activeBorrowStage === 'receipt'">
          <el-card shadow="never"><template #header>待领取/下载内容</template><el-table :data="handlingItems" border><el-table-column prop="company" label="公司" min-width="150" /><el-table-column prop="documentType" label="文档类型" min-width="120" /><el-table-column prop="description" label="需求说明" min-width="220" /><el-table-column prop="demandType" label="需求类型" min-width="120" /><el-table-column prop="expectedReturnDate" label="预计归还时间" min-width="150" /></el-table></el-card>
          <el-card shadow="never"><template #header>可下载附件</template><el-table :data="attachments" border><el-table-column prop="name" label="附件名称" min-width="220" /><el-table-column prop="watermarkStatus" label="水印状态" min-width="120" /><el-table-column prop="protectionStatus" label="文件保护状态" min-width="140" /><el-table-column prop="uploader" label="上传人" min-width="120" /><el-table-column prop="uploadTime" label="上传时间" min-width="160" /><el-table-column label="操作" width="120"><template #default="{ row }"><el-button link type="primary" @click="downloadAttachment(row)">下载</el-button></template></el-table-column></el-table></el-card>
          <el-card shadow="never"><template #header>领取确认</template><el-form label-position="top" class="form-grid two"><el-form-item label="领取人"><el-input :model-value="borrowForm.applicant" readonly /></el-form-item><el-form-item label="说明"><el-input :model-value="showApplicantReturn ? '申请人可下载电子件附件、领取原件/实物，并在使用完成后通过本待办提交归还。' : '申请人可下载电子件附件，或按办理通知领取原件。'" readonly /></el-form-item></el-form></el-card>
          <el-card v-if="showApplicantReturn" shadow="never">
            <template #header>归还办理</template>
            <el-table :data="receiptReturnItems" border>
              <el-table-column prop="company" label="公司" min-width="150" />
              <el-table-column prop="documentType" label="文档类型" min-width="120" />
              <el-table-column prop="description" label="借阅内容" min-width="220" />
              <el-table-column prop="demandType" label="需求类型" min-width="120" />
              <el-table-column prop="expectedReturnDate" label="预计归还时间" min-width="150" />
            </el-table>
            <el-form label-position="top" class="form-grid three top-gap">
              <el-form-item label="归还方式">
                <el-radio-group v-model="receiptAction.returnMethod">
                  <el-radio-button label="现场归还" value="现场归还" />
                  <el-radio-button label="邮寄" value="邮寄" />
                </el-radio-group>
              </el-form-item>
              <el-form-item label="邮寄单号" v-if="receiptAction.returnMethod === '邮寄'">
                <el-input v-model="receiptAction.postNo" placeholder="请输入邮寄单号" />
              </el-form-item>
              <el-form-item label="归还说明" class="span-all">
                <el-input v-model="receiptAction.comment" type="textarea" :rows="3" placeholder="请输入归还说明" />
              </el-form-item>
            </el-form>
          </el-card>
          <div class="actions"><el-button v-if="showApplicantReturn" @click="submitApplicantReturn">归还</el-button><el-button type="primary" @click="confirmReceipt">确认已领取/下载</el-button></div>
        </template>

        <el-card shadow="never" class="process-section">
          <template #header>
            <div class="header-row">
              <div class="section-head"><span class="section-accent"></span><span>关联申请单</span></div>
              <el-button link type="primary" @click="relatedOrdersCollapsed = !relatedOrdersCollapsed">{{ relatedOrdersCollapsed ? '展开' : '折叠' }}</el-button>
            </div>
          </template>
          <el-table v-show="!relatedOrdersCollapsed" :data="relatedBorrowOrders" border empty-text="当前申请单暂无关联子单">
            <el-table-column prop="mainOrderNo" label="主单申请单号" min-width="180" />
            <el-table-column label="子单申请单号" min-width="180">
              <template #default="{ row }">
                <el-button link type="primary" @click="openRelatedBorrowOrder(row.childOrderNo)">{{ row.childOrderNo }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="childHandler" label="子单处理人" min-width="140" />
            <el-table-column prop="childStatus" label="子单状态" min-width="140" />
            <el-table-column prop="childRejectReason" label="子单驳回原因" min-width="260" show-overflow-tooltip />
          </el-table>
        </el-card>
        <workflow-log-card :logs="displayBorrowLogs" title="日志" />
      </div>

      <div v-else-if="todoCurrentModule === 'renew'" class="stage-stack">
        <page-head title="续借审批" :order-no="renewForm.renewOrderNo" status="待需求审核" />
        <el-card shadow="never"><template #header>流程</template><el-steps :active="1" align-center finish-status="success"><el-step title="申请" /><el-step title="需求审核" /><el-step title="办理" /></el-steps></el-card>
        <el-card shadow="never"><template #header>续借信息</template><el-descriptions :column="2" border><el-descriptions-item label="续借申请单号">{{ renewForm.renewOrderNo }}</el-descriptions-item><el-descriptions-item label="原申请单号">{{ renewForm.sourceOrderNo }}</el-descriptions-item><el-descriptions-item label="使用人">{{ renewForm.userName }}</el-descriptions-item><el-descriptions-item label="使用人部门">{{ renewForm.department }}</el-descriptions-item><el-descriptions-item label="申请人">{{ renewForm.applicant }}</el-descriptions-item><el-descriptions-item label="申请用途">{{ renewForm.purpose }}</el-descriptions-item><el-descriptions-item label="申请原因/背景说明" :span="2">{{ renewForm.reason }}</el-descriptions-item></el-descriptions></el-card>
        <el-card shadow="never"><template #header>续借文档明细</template><el-table :data="selectedRenewItems" border><el-table-column prop="businessCode" label="文档业务编号" min-width="150" /><el-table-column prop="documentName" label="文档名称" min-width="200" /><el-table-column prop="borrowType" label="借阅类型" min-width="110" /><el-table-column prop="currentExpireTime" label="原归还时间" min-width="120" /><el-table-column prop="renewExpireTime" label="续借申请归还时间" min-width="150" /><el-table-column prop="renewReason" label="续借原因" min-width="220" /></el-table></el-card>
        <decision-card v-model="renewApproval.decision" :comment="renewApproval.comment" @update:comment="renewApproval.comment = $event" @submit="approveRenew(renewApproval.decision)" title="审批" positive="同意" negative="拒绝" />
        <el-card shadow="never"><template #header>下一步处理人/抄送</template><el-form label-position="top" class="form-grid two"><el-form-item label="下一步处理人"><el-select v-model="renewApproval.nextHandler"><el-option v-for="item in handlers" :key="item" :label="item" :value="item" /></el-select></el-form-item><el-form-item label="抄送"><el-select v-model="renewApproval.ccUsers" multiple collapse-tags><el-option v-for="item in ccUsers" :key="item" :label="item" :value="item" /></el-select></el-form-item></el-form></el-card>
        <workflow-log-card :logs="renewLogs" title="流程日志" />
      </div>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, defineComponent, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchBorrowRecords } from '../../api/modules/lifecycle'
import { createBorrowOrder, createBorrowRenewOrder, fetchBorrowOrder, fetchBorrowOrders, fetchBorrowRenewOrder, fetchBorrowRenewOrders, updateBorrowOrder } from '../../api/modules/borrow'
import { fetchCompanyInfos } from '../../api/modules/companyInfo'
import { fetchDocumentTypeTree } from '../../api/modules/documentType'
import {
  completeTask,
  delegateTask,
  fetchMyProcesses,
  fetchMyTasks,
  fetchParticipatedProcesses,
  getProcessTasks,
  listProcesses,
  rejectTask,
  startProcess
} from '../../api/modules/workflow'
import type { BorrowOrder, BorrowRecord, BorrowRenewOrder, CompanyInfo, DocumentTypeTreeNode } from '../../types'

type BorrowItem = { id: number; company: string; documentType: string; description: string; demandType: string; needReturn: boolean; expectedReturnDate: string; lendingApprover?: string; lendingRemark?: string; handler?: string; handlerRemark?: string }
type AiRecommendation = BorrowItem & { documentName: string; businessCode: string; businessModule: string; score?: number }
type DetailItem = { id: number; attachments: string[]; borrowType: string; borrowCount: number; company: string; businessCode: string; documentName: string; documentType: string; businessModule: string; archivePeriod: string; stockCount: number; location: string; volumeNo: string; barcode: string; documentStatus: string; watermark: boolean; postMethod: string; postNo: string }
type LogRow = { step: string; handler: string; action: string; comment: string; time: string }
type RenewItem = { id: number; businessCode: string; documentName: string; company: string; businessModule: string; archivePeriod: string; borrowType: string; borrowTime: string; currentExpireTime: string; renewExpireTime: string; renewReason: string }
type RenewOrder = { orderNo: string; applyTime: string; borrowTime: string; expireTime: string; currentHandler: string; applicantName?: string }
type MineRow = { company: string; businessCode: string; documentName: string; documentType: string; businessModule: string; archivePeriod: string; orderNo: string; status: string; borrowTime: string; attachment: string }
type Message = { id: number; role: 'assistant' | 'user'; content: string }
type WorkflowUser = { id: string; name: string; role: string }
type WorkflowTaskRow = { taskId: string; processInstanceId: string; taskDefinitionKey: string; taskName: string; assignee?: string; status?: string; businessKey?: string; createTime?: string; completeTime?: string; comment?: string }
type WorkflowProcessRow = { processInstanceId: string; businessKey: string; processDefinitionKey: string; status: string; initiatorId?: string; initiatorName?: string; startTime?: string; endTime?: string; lastUpdateDate?: string }
type TodoRow = WorkflowTaskRow & { module: 'borrow' | 'renew'; moduleLabel: string; assigneeName: string }
type DraftRow = { module: 'borrow'; moduleLabel: string; businessKey: string; taskName: string; assigneeName: string; createTime: string }
type RelatedBorrowOrderRow = { mainOrderNo: string; childOrderNo: string; childHandler: string; childStatus: string; childRejectReason: string }
type MyApplicationRow = { module: 'borrow' | 'renew'; moduleLabel: string; businessKey: string; applyTime: string; statusLabel: string; currentHandler: string }
type DetailQueryRow = { id: number; company: string; documentName: string; businessCode: string; documentType: string; businessModule: string; archivePeriod: string; location: string; volumeNo: string; barcode: string; stockCount: number; documentStatus: string }

const workflowUsers: WorkflowUser[] = [
  { id: '1', name: '李晓岚', role: '借阅申请人' },
  { id: '2', name: '郑审核', role: '需求审核人' },
  { id: '3', name: '郭审批', role: '需求审批人/借阅分析人' },
  { id: '4', name: '陈借出审批', role: '借出审批人' },
  { id: '5', name: '王借出审批', role: '借出审批人' },
  { id: '6', name: '李办理', role: '借阅办理人' },
  { id: '7', name: '周办理', role: '借阅办理人' }
]
const users = [{ name: '张敏', dept: '法务合规部' }, { name: '王岚', dept: '投后管理部' }, { name: '陈越', dept: '风险管理部' }]
const fallbackCompanies = ['华东控股有限公司', '宏远集团有限公司', '宏远国际投资', '上海核心集团']
const fallbackDocTypes = ['合同协议', '审计报告', '资质证书', '投后材料', '实物档案', '扫描件']
const demandTypes = ['借阅原件', '借阅电子件', '借阅实物', '借阅复印件']
const purposes = ['审计查阅', '投后管理', '法务取证', '项目复盘']
const handlers = workflowUsers.filter(user => user.id !== '1').map(user => user.name)
const ccUsers = ['林静', '赵强', '王经理']
const borrowKinds = ['原件', '电子件', '实物', '复印件']
const handlingOptions = ['借出', '归还', '办理完成', '转需求审核/审批人', '转他人处理', '终止']
const borrowSteps = ['借阅人提交申请', '需求审批人审批', '需求审核人审批', '借阅分析人处理', '借出审批人审批', '借阅办理', '申请人领取/下载', '结束']
const myStatuses = ['处理中', '已完成', '已驳回', '待归还']
const borrowProcessKeys = ['borrowRequest', 'borrowLendingApprovalChild', 'borrowHandlingChild']
const renewProcessKeys = ['borrowRenewRequest']

const activeModule = ref('borrow')
const activeBorrowStage = ref('apply')
const activeRenewStage = ref('query')
const todoVisible = ref(false)
const borrowNoticeVisible = ref(false)
const borrowNoticeAccepted = ref(false)
const relatedOrdersCollapsed = ref(false)
const detailQueryVisible = ref(false)
const todoCurrentModule = ref<'borrow' | 'renew'>('borrow')
const currentBorrowTaskDef = ref('')
const currentBorrowProcessKey = ref('borrowRequest')
const currentHandlingOwner = ref('')
const selectedWorkflowUserId = ref('1')
const liveLoading = ref(false)
const borrowSubmitting = ref(false)
const draftSaving = ref(false)
const liveBorrowRecords = ref<BorrowRecord[]>([])
const liveBorrowOrders = ref<BorrowOrder[]>([])
const liveBorrowRenewOrders = ref<BorrowRenewOrder[]>([])
const myWorkflowTasks = ref<WorkflowTaskRow[]>([])
const myWorkflowProcesses = ref<WorkflowProcessRow[]>([])
const participatedBorrowProcesses = ref<WorkflowProcessRow[]>([])
const allBorrowProcesses = ref<WorkflowProcessRow[]>([])
const relatedOrderTaskMap = ref<Record<string, WorkflowTaskRow | undefined>>({})
const borrowOrder = reactive({ orderNo: 'BOR-20260407-0008', status: '草稿' })
const borrowNoticeConfig = reactive({
  title: '借阅申请提示',
  description: '为保障档案安全、借阅合规和后续归还跟踪，请在填写借阅申请前阅读以下说明。',
  highlight: '涉及原件或实物借阅时，请务必填写预计归还时间，并按期归还或及时发起续借。',
  rules: [
    '请确认借阅用途真实、明确，申请原因/背景说明应能支撑本次借阅需求。',
    '借阅原件、实物档案需按规定归还；电子件下载或查看应遵守保密及水印要求。',
    '借阅内容请尽量填写公司、文档类型、需求说明和预计归还时间，便于审批人判断。'
  ],
  tips: [
    '可通过“AI 智能借阅推荐”辅助查找文档，也可以手工新增借阅内容。',
    '提交后流程将依次进入需求审批、需求审核、需求分析、借出审批和借阅办理。',
    '审批日志会记录各环节处理人、处理意见和处理时间。'
  ],
  confirmText: '我已阅读，开始填写'
})
const borrowForm = reactive({ userName: '张敏', department: '法务合规部', applicant: '李晓岚', applyTime: '2026-04-07 10:18', purpose: '审计查阅', reason: '因年度专项审计及投后风险核查，需要借阅历史合同原件、扫描件与实物档案。', attachment: '', demandApprover: '郭审批', demandReviewer: '郑审核', demandAnalyst: '郭审批', ccUsers: ['林静'] as string[], approvalComment: '因专项审计查阅需要，现提交借阅申请。' })
const itemFactory = (overrides: Partial<BorrowItem> = {}): BorrowItem => ({ id: Date.now() + Math.random(), company: '华东控股有限公司', documentType: '合同协议', description: '2024 年投后管理框架协议及关联审批资料', demandType: '借阅原件', needReturn: true, expectedReturnDate: '2026-04-20', lendingApprover: '陈借出审批', lendingRemark: '优先确认库存', handler: '李办理', handlerRemark: '同步准备电子件', ...overrides })
const borrowItems = ref<BorrowItem[]>([itemFactory(), itemFactory({ company: '宏远国际投资', documentType: '资质证书', description: '境外并购资质证明及扫描件', demandType: '借阅电子件', needReturn: false, expectedReturnDate: '2026-05-10', lendingApprover: '王借出审批', handler: '周办理' })])
const selectedBorrowItems = ref<BorrowItem[]>([])
const companyConfigOptions = ref<CompanyInfo[]>([])
const documentTypeConfigOptions = ref<DocumentTypeTreeNode[]>([])
const borrowLogs = ref<LogRow[]>([{ step: '借阅申请', handler: '李晓岚', action: '提交', comment: '发起借阅申请', time: '2026-04-07 10:18' }])
const borrowApproval = reactive({ decision: '同意', comment: '申请用途明确，建议进入需求分析。' })
const analysisAction = reactive({ action: '分单', nextHandler: '陈借出审批', comment: '先按借出审批人分单，再按借阅办理人分单。' })
const lendingApproval = reactive({ decision: '同意', comment: '同意借出本人名下的借阅内容。' })
const documentDetails = ref<DetailItem[]>([{ id: 1, attachments: ['华东投后协议扫描件.pdf'], borrowType: '电子件', borrowCount: 1, company: '华东控股有限公司', businessCode: 'DOC-X2024-0088', documentName: '2024 年投后管理框架协议', documentType: '合同协议', businessModule: '投后管理', archivePeriod: '2024Q4', stockCount: 2, location: 'A-03-12', volumeNo: 'V-08', barcode: 'BC-20260407-0088', documentStatus: '在库', watermark: true, postMethod: '现场领取', postNo: '' }])
const selectedDetailIds = ref<number[]>([])
const detailQuery = reactive({ company: '', businessCode: '', documentType: '', businessModule: '', archivePeriod: '' })
const detailQueryRows = ref<DetailQueryRow[]>([
  { id: 1, company: '华东控股有限公司', documentName: '2024 年投后管理框架协议', businessCode: 'DOC-X2024-0088', documentType: '合同协议', businessModule: 'TOHOU-001', archivePeriod: '2024Q4', location: 'A-03-12', volumeNo: 'V-08', barcode: 'BC-20260407-0088', stockCount: 2, documentStatus: '在库' },
  { id: 2, company: '宏远国际投资', documentName: '境外并购资质证明', businessCode: 'DOC-HY-2024-015', documentType: '资质证书', businessModule: 'MNA-002', archivePeriod: '2024Q3', location: 'B-02-09', volumeNo: 'V-12', barcode: 'BC-20260407-0150', stockCount: 1, documentStatus: '在库' },
  { id: 3, company: '上海核心集团', documentName: '华东区域投后巡检报告', businessCode: 'DOC-SH-2025-021', documentType: '投后材料', businessModule: 'TOHOU-003', archivePeriod: '2025Q1', location: 'C-01-07', volumeNo: 'V-18', barcode: 'BC-20260407-0210', stockCount: 3, documentStatus: '在库' }
])
const selectedDetailQueryRows = ref<DetailQueryRow[]>([])
const attachments = ref([{ name: '投后协议扫描件-加水印.pdf', watermarkStatus: '已打水印', protectionStatus: '已加密', uploader: '李办理', uploadTime: '2026-04-07 14:26' }])
const handlingAction = reactive({ action: '借出', receiver: '李晓岚', comment: '现场借出并同步上传电子件加水印版本。', ccUsers: ['王经理'] as string[] })
const receiptAction = reactive({ returnMethod: '现场归还', postNo: '', comment: '申请人已归还借阅的原件/实物。' })
const renewQuery = reactive({ orderNo: '', applyStart: '', applyEnd: '', applyType: '', applicant: '', currentHandler: '', company: '', businessCode: '', businessModule: '', archivePeriod: '' })
const selectedRenewOrders = ref<RenewOrder[]>([])
const renewForm = reactive({ renewOrderNo: 'REN-20260407-0003', sourceOrderNo: 'BOR-20260328-0012', userName: '张敏', department: '法务合规部', applicant: '李晓岚', purpose: '审计查阅', reason: '原申请单审计任务仍在进行，需要对未归还原件和实物继续续借。', attachment: 'audit-plan-q2.pdf', returnDate: '2026-05-15', renewReason: '因审计范围扩大，需继续查阅实物凭证以核实跨期交易。', reviewer: '郑审核', handler: '李办理', ccUsers: ['林静'] as string[] })
const renewItems = ref<RenewItem[]>([])
const selectedRenewItemIds = ref<number[]>([])
const renewLogs = ref<LogRow[]>([{ step: '申请', handler: '李晓岚', action: '提交', comment: '发起续借申请', time: '2026-04-07 15:10' }])
const renewApproval = reactive({ decision: '同意', comment: '同意续借，请办理人继续跟踪归还时限。', nextHandler: '李办理', ccUsers: ['赵强'] as string[] })
const myBorrowQuery = reactive({ company: '', businessCode: '', documentName: '', documentType: '', businessModule: '', archivePeriod: '', orderNo: '', status: '', borrowTime: '' })
const aiVisible = ref(false)
const aiResultTableRef = ref<any>()
const route = useRoute()
const aiDraft = ref('请推荐华东区域近两年的投后管理原件与扫描件。')
const aiMessages = ref<Message[]>([
  { id: 1, role: 'assistant', content: '基于当前使用人部门、申请用途和历史借阅记录，我已为你筛选出高相关文档。可以继续告诉我公司、文档类型或时间范围。' },
  { id: 2, role: 'user', content: '查找与本次审计查阅相关的华东区域合同、投后材料和扫描件。' }
])
const selectedAiResults = ref<AiRecommendation[]>([])
const aiResults = ref<AiRecommendation[]>([
  { ...itemFactory({ company: '华东控股有限公司', documentType: '审计报告', description: '2023 年度合规审计报告', demandType: '借阅电子件', needReturn: false }), documentName: '2023 年度合规审计报告', businessCode: 'DOC-7729-BM', businessModule: '合规管理', score: 96 },
  { ...itemFactory({ company: '宏远国际投资', documentType: '合同协议', description: '战略合作伙伴协议', demandType: '借阅原件', needReturn: true }), documentName: '战略合作伙伴协议', businessCode: 'DOC-1102-SA', businessModule: '商务拓展', score: 91 },
  { ...itemFactory({ company: '北方区资产', documentType: '投后材料', description: '风险评估框架 v4', demandType: '借阅电子件', needReturn: false }), documentName: '风险评估框架 v4', businessCode: 'DOC-4481-RF', businessModule: '风险管理', score: 88 },
  { ...itemFactory({ company: '上海核心集团', documentType: '投后材料', description: '季度风险回顾 - 2023 年 Q3 摘要', demandType: '借阅电子件', needReturn: false }), documentName: '季度风险回顾 - 2023 年 Q3 摘要', businessCode: 'DOC-9900-QR', businessModule: '核心风控', score: 98 },
  { ...itemFactory({ company: '华东控股有限公司', documentType: '实物档案', description: '资产清算记录', demandType: '借阅实物', needReturn: true }), documentName: '资产清算记录', businessCode: 'DOC-5512-AL', businessModule: '资产清算', score: 84 },
  { ...itemFactory({ company: '集团总部', documentType: '法务材料', description: '法务合规审查指南', demandType: '借阅电子件', needReturn: false }), documentName: '法务合规审查指南', businessCode: 'DOC-2024-LG', businessModule: '法务', score: 82 }
])

const currentWorkflowUser = computed(() => workflowUsers.find(user => user.id === selectedWorkflowUserId.value) ?? workflowUsers[0])
const companyOptionsForSelect = computed<CompanyInfo[]>(() => {
  if (companyConfigOptions.value.length > 0) return companyConfigOptions.value
  return fallbackCompanies.map((companyName, index) => ({
    companyId: index + 1,
    companyCode: companyName,
    companyName,
    tags: [],
    enabledFlag: 'Y'
  }))
})
const documentTypeOptionsForSelect = computed<DocumentTypeTreeNode[]>(() => {
  const enabledTypes = flattenDocumentTypes(documentTypeConfigOptions.value).filter(item => item.enabledFlag === 'Y')
  if (enabledTypes.length > 0) return enabledTypes
  return fallbackDocTypes.map((typeName, index) => ({
    id: index + 1,
    typeCode: typeName,
    typeName,
    enabledFlag: 'Y',
    levelNum: 1,
    sortOrder: index + 1,
    deleteFlag: 'N',
    createdBy: 0,
    creationDate: '',
    lastUpdatedBy: 0,
    lastUpdateDate: '',
    children: []
  }))
})
const companies = computed(() => companyOptionsForSelect.value.map(item => item.companyName))
const docTypes = computed(() => documentTypeOptionsForSelect.value.map(item => item.typeName))
const hasReturnRequiredBorrowItem = computed(() => borrowItems.value.some(item => isReturnRequiredDemandType(item.demandType)))
const visibleBorrowProcesses = computed(() => allBorrowProcesses.value.filter(process => borrowProcessKeys.includes(process.processDefinitionKey)))
const visibleRenewProcesses = computed(() => allBorrowProcesses.value.filter(process => renewProcessKeys.includes(process.processDefinitionKey)))
const borrowProcessMap = computed(() => new Map(visibleBorrowProcesses.value.map(process => [process.businessKey, process])))
const renewProcessMap = computed(() => new Map(visibleRenewProcesses.value.map(process => [process.businessKey, process])))
const renewOrders = computed<RenewOrder[]>(() => {
  const groups = new Map<string, BorrowRecord[]>()
  liveBorrowRecords.value.forEach(record => {
    const normalized = normalizeBorrowKind(record.borrowType)
    if (!['原件', '实物'].includes(normalized)) return
    if (!record.expectedReturnDate || record.expectedReturnDate < today()) return
    const key = record.borrowCode
    const existing = groups.get(key) ?? []
    existing.push(record)
    groups.set(key, existing)
  })

  return Array.from(groups.entries()).map(([orderNo, records]) => {
    const process = borrowProcessMap.value.get(orderNo)
    const currentTask = myWorkflowTasks.value.find(task => task.businessKey === orderNo && task.status === 'ACTIVE')
    const sourceOrder = liveBorrowOrders.value.find(order => order.orderNo === orderNo)
    return {
      orderNo,
      applyTime: formatDateTime(records[0]?.borrowedAt),
      borrowTime: formatDate(records[0]?.borrowedAt),
      expireTime: records.reduce((latest, current) => current.expectedReturnDate > latest ? current.expectedReturnDate : latest, records[0]?.expectedReturnDate ?? ''),
      currentHandler: currentTask ? resolveUserName(currentTask.assignee) : resolveProcessStatus(process?.status),
      applicantName: sourceOrder?.applicantName
    }
  })
})
const summaryCards = computed(() => [{ label: '申请内容', value: String(borrowItems.value.length).padStart(2, '0'), desc: '当前借阅申请明细条数' }, { label: '我的待办', value: String(myWorkflowTasks.value.filter(task => borrowProcessKeys.includes(processKeyByTask(task))).length).padStart(2, '0'), desc: '真实工作流待办任务数' }, { label: '可续借单据', value: String(renewOrders.value.length), desc: '原件/实物且未到期' }, { label: '我的借阅文档', value: String(mineRows.value.length), desc: '来自后端借阅记录' }])
const displayBorrowLogs = computed(() => borrowLogs.value.length ? borrowLogs.value : buildBorrowFallbackLogs())
const draftRows = computed<DraftRow[]>(() => liveBorrowOrders.value
  .filter(order => order.status === '草稿' && order.applicantName === currentWorkflowUser.value.name)
  .map(order => ({
    module: 'borrow',
    moduleLabel: '借阅',
    businessKey: order.orderNo,
    taskName: '草稿',
    assigneeName: order.applicantName || '-',
    createTime: formatDateTime(order.applyTime)
  }))
  .sort((a, b) => new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime()))
const analysisItems = computed(() => borrowItems.value)
const analysisParticipants = computed(() => Array.from(new Set(
  borrowItems.value.flatMap(item => [item.lendingApprover, item.handler]).filter(Boolean) as string[]
)))
const analysisParticipantsDisplay = computed(() => analysisParticipants.value.join('、'))
const currentApproverItems = computed(() => borrowItems.value.filter(item => item.lendingApprover === currentWorkflowUser.value.name))
const handlingReceivers = computed(() => Array.from(new Set([borrowForm.applicant, ...handlers].filter(Boolean))))
const handlingItems = computed(() => {
  if (activeBorrowStage.value === 'receipt') return borrowItems.value
  return borrowItems.value.filter(item => item.handler === currentHandlingOwner.value || !item.handler)
})
const receiptReturnItems = computed(() => handlingItems.value.filter(item => item.needReturn || ['借阅原件', '借阅实物'].includes(item.demandType)))
const showApplicantReturn = computed(() => receiptReturnItems.value.length > 0)
const filteredDetailQueryRows = computed(() => detailQueryRows.value.filter(row =>
  (!detailQuery.company || row.company === detailQuery.company) &&
  (!detailQuery.businessCode || row.businessCode.includes(detailQuery.businessCode)) &&
  (!detailQuery.documentType || row.documentType === detailQuery.documentType) &&
  (!detailQuery.businessModule || row.businessModule.includes(detailQuery.businessModule)) &&
  (!detailQuery.archivePeriod || row.archivePeriod.includes(detailQuery.archivePeriod))
))
const relatedBorrowOrders = computed<RelatedBorrowOrderRow[]>(() => {
  const rootOrderNo = resolveRootOrderNo(borrowOrder.orderNo)
  const rows = liveBorrowOrders.value
    .filter(order => order.orderNo !== rootOrderNo && resolveRootOrderNo(order.orderNo) === rootOrderNo)
    .sort((a, b) => a.orderNo.localeCompare(b.orderNo))
    .map(order => {
      const process = allBorrowProcesses.value.find(item => item.businessKey === order.orderNo)
      const activeTask = relatedOrderTaskMap.value[order.orderNo]
      return {
        mainOrderNo: rootOrderNo,
        childOrderNo: order.orderNo,
        childHandler: activeTask ? resolveUserName(activeTask.assignee) : (order.currentHandler || resolveProcessStatus(process?.status) || '-'),
        childStatus: order.status || (process ? resolveProcessStatus(process.status) : '-'),
        childRejectReason: order.approvalComment || '-'
      }
    })
  if (rows.length > 0) return rows

  const currentOrder = liveBorrowOrders.value.find(order => order.orderNo === borrowOrder.orderNo)
  if (!currentOrder || currentOrder.orderNo === rootOrderNo) return []

  return [{
    mainOrderNo: rootOrderNo,
    childOrderNo: currentOrder.orderNo,
    childHandler: currentOrder.currentHandler || '-',
    childStatus: resolveBorrowDisplayStatus(currentOrder.orderNo, currentOrder.status),
    childRejectReason: currentOrder.approvalComment || '-'
  }]
})
const filteredRenewOrders = computed(() => renewOrders.value.filter(item =>
  (!renewQuery.orderNo || item.orderNo.includes(renewQuery.orderNo)) &&
  (!renewQuery.applyStart || item.applyTime >= renewQuery.applyStart) &&
  (!renewQuery.applyEnd || item.applyTime <= `${renewQuery.applyEnd} 23:59`) &&
  (!renewQuery.currentHandler || (item.currentHandler || '').includes(renewQuery.currentHandler)) &&
  (!renewQuery.applicant || (item.applicantName || '').includes(renewQuery.applicant))
))
const selectedRenewItems = computed(() => renewItems.value.filter(item => selectedRenewItemIds.value.includes(item.id)))
const mineRows = computed<MineRow[]>(() => liveBorrowRecords.value.map(record => mapBorrowRecordToMineRow(record)))
const filteredMine = computed(() => mineRows.value.filter(item => (!myBorrowQuery.company || item.company === myBorrowQuery.company) && (!myBorrowQuery.businessCode || item.businessCode.includes(myBorrowQuery.businessCode)) && (!myBorrowQuery.documentName || item.documentName.includes(myBorrowQuery.documentName)) && (!myBorrowQuery.documentType || item.documentType === myBorrowQuery.documentType) && (!myBorrowQuery.businessModule || item.businessModule.includes(myBorrowQuery.businessModule)) && (!myBorrowQuery.archivePeriod || item.archivePeriod.includes(myBorrowQuery.archivePeriod)) && (!myBorrowQuery.orderNo || item.orderNo.includes(myBorrowQuery.orderNo)) && (!myBorrowQuery.status || item.status === myBorrowQuery.status) && (!myBorrowQuery.borrowTime || item.borrowTime === myBorrowQuery.borrowTime)))
const myApplicationRows = computed<MyApplicationRow[]>(() => myWorkflowProcesses.value
  .map(process => {
    const module = renewProcessKeys.includes(process.processDefinitionKey) ? 'renew' as const : 'borrow' as const
    const borrowOrderItem = module === 'borrow' ? liveBorrowOrders.value.find(order => order.orderNo === process.businessKey) : undefined
    const renewOrderItem = module === 'renew' ? liveBorrowRenewOrders.value.find(order => order.renewOrderNo === process.businessKey) : undefined
    return {
      module,
      moduleLabel: module === 'renew' ? '续借' : '借阅',
      businessKey: process.businessKey,
      applyTime: formatDateTime(process.startTime || borrowOrderItem?.applyTime || renewOrderItem?.applyTime),
      statusLabel: module === 'borrow'
        ? resolveBorrowDisplayStatus(process.businessKey, borrowOrderItem?.status || resolveProcessStatus(process.status))
        : resolveProcessStatus(process.status),
      currentHandler: module === 'renew'
        ? (renewOrderItem?.currentHandler || resolveProcessStatus(process.status))
        : resolveBorrowCurrentHandler(process.businessKey, borrowOrderItem?.currentHandler || resolveProcessStatus(process.status))
    }
  })
  .sort((a, b) => new Date(b.applyTime || 0).getTime() - new Date(a.applyTime || 0).getTime()))
const borrowDisplayStatus = computed(() => resolveBorrowDisplayStatus(borrowOrder.orderNo, borrowOrder.status))
const currentOrderStatusLabel = computed(() => resolveBorrowDisplayStatus(borrowOrder.orderNo, borrowOrder.status))
const todoBorrowSteps = computed(() => {
  switch (currentBorrowProcessKey.value) {
    case 'borrowLendingApprovalChild':
      return ['借出审批', '借阅办理', '申请人领取/下载', '结束']
    case 'borrowHandlingChild':
      return ['借阅办理', '申请人领取/下载', '结束']
    default:
      return borrowSteps
  }
})
const todoRows = computed<TodoRow[]>(() => myWorkflowTasks.value
  .filter(task => task.status === 'ACTIVE')
  .map(task => {
    const processKey = processKeyByTask(task)
    const module = renewProcessKeys.includes(processKey) ? 'renew' : 'borrow'
    const displayBusinessKey = module === 'borrow' && task.taskDefinitionKey === 'receiveBorrowTask'
      ? resolveRootOrderNo(task.businessKey || '')
      : task.businessKey
    return {
      ...task,
      businessKey: displayBusinessKey,
      module,
      moduleLabel: module === 'renew' ? '续借' : '借阅',
      assigneeName: resolveUserName(task.assignee)
    }
  })
  .concat(
    myWorkflowProcesses.value
      .filter(process => process.status === 'REJECTED')
      .map(process => ({
        taskId: `REJECTED-${process.processInstanceId}`,
        processInstanceId: process.processInstanceId,
        taskDefinitionKey: 'rejectedReturnTask',
        taskName: '驳回退回申请人',
        assignee: selectedWorkflowUserId.value,
        status: 'ACTIVE',
        businessKey: process.businessKey,
        createTime: process.lastUpdateDate || process.endTime || process.startTime,
        module: renewProcessKeys.includes(process.processDefinitionKey) ? 'renew' as const : 'borrow' as const,
        moduleLabel: renewProcessKeys.includes(process.processDefinitionKey) ? '续借' : '借阅',
        assigneeName: currentWorkflowUser.value.name
      }))
  )
  .sort((a, b) => new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime()))
const todoBorrowStep = computed(() => {
  if (currentBorrowProcessKey.value === 'borrowLendingApprovalChild') {
    switch (currentBorrowTaskDef.value || activeBorrowStage.value) {
      case 'lendApproveTask':
      case 'lendingApproval':
        return 0
      case 'handleBorrowTask':
      case 'handling':
        return 1
      case 'receiveBorrowTask':
      case 'receipt':
        return 2
      default:
        return 0
    }
  }

  if (currentBorrowProcessKey.value === 'borrowHandlingChild') {
    switch (currentBorrowTaskDef.value || activeBorrowStage.value) {
      case 'handleBorrowTask':
      case 'handling':
        return 0
      case 'receiveBorrowTask':
      case 'receipt':
        return 1
      default:
        return 0
    }
  }

  switch (currentBorrowTaskDef.value || activeBorrowStage.value) {
    case 'approveDemandTask':
      return 1
    case 'reviewDemandTask':
      return 2
    case 'analyzeDemandTask':
    case 'analysis':
      return 3
    case 'lendApproveTask':
    case 'lendingApproval':
      return 4
    case 'handleBorrowTask':
    case 'handling':
      return 5
    case 'receiveBorrowTask':
    case 'receipt':
      return 6
    case 'rejectedReturnTask':
      return 0
    case 'applicantReapplyTask':
      return 0
    default:
      return 0
  }
})
const todoDialogTitle = computed(() => {
  if (todoCurrentModule.value === 'renew') return '续借待办处理'
  switch (currentBorrowTaskDef.value || activeBorrowStage.value) {
    case 'reviewDemandTask':
      return '需求审核人审批'
    case 'approveDemandTask':
      return '需求审批人审批'
    case 'analyzeDemandTask':
    case 'analysis':
      return '借阅需求分析'
    case 'lendApproveTask':
    case 'lendingApproval':
      return '借出审批'
    case 'handleBorrowTask':
    case 'handling':
      return '借阅办理'
    case 'receiveBorrowTask':
    case 'receipt':
      return '申请人领取/下载'
    case 'rejectedReturnTask':
      return '驳回退回处理'
    case 'applicantReapplyTask':
      return '退回申请人修改'
    default:
      return '借阅待办处理'
  }
})

function today() {
  return new Date().toISOString().slice(0, 10)
}

function nowAsLocalDateTime() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const pad = (num: number) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function formatDate(value?: string) {
  return formatDateTime(value).slice(0, 10)
}

function getUserIdByName(name?: string) {
  return workflowUsers.find(user => user.name === name)?.id ?? '1'
}

function resolveUserName(userId?: string) {
  if (!userId) return '-'
  return workflowUsers.find(user => user.id === String(userId))?.name ?? String(userId)
}

function resolveProcessStatus(status?: string) {
  switch (status) {
    case 'RUNNING':
      return '处理中'
    case 'APPROVED':
      return '已完成'
    case 'REJECTED':
      return '已驳回'
    case 'INVALID':
      return '已失效'
    default:
      return '处理中'
  }
}

function resolveBorrowDisplayStatus(orderNo?: string, fallback?: string) {
  if (!orderNo) return fallback || '处理中'
  const rootOrderNo = resolveRootOrderNo(orderNo)
  const process = allBorrowProcesses.value.find(item => item.businessKey === orderNo)

  if (orderNo !== rootOrderNo) {
    return process ? resolveProcessStatus(process.status) : (fallback || '处理中')
  }

  const childStatuses = liveBorrowOrders.value
    .filter(order => order.orderNo !== rootOrderNo && resolveRootOrderNo(order.orderNo) === rootOrderNo)
    .map(order => {
      const childProcess = allBorrowProcesses.value.find(item => item.businessKey === order.orderNo)
      return childProcess ? resolveProcessStatus(childProcess.status) : order.status
    })
    .filter((status): status is string => Boolean(status))

  if (childStatuses.length > 0) {
    return Array.from(new Set(childStatuses)).join('、')
  }

  return process ? resolveProcessStatus(process.status) : (fallback || '处理中')
}

function resolveBorrowCurrentHandler(orderNo?: string, fallback?: string) {
  if (!orderNo) return fallback || '-'
  const rootOrderNo = resolveRootOrderNo(orderNo)
  const process = allBorrowProcesses.value.find(item => item.businessKey === orderNo)
  const currentOrder = liveBorrowOrders.value.find(order => order.orderNo === orderNo)

  if (orderNo !== rootOrderNo) {
    if (process?.status === 'RUNNING') {
      return currentOrder?.currentHandler || fallback || '-'
    }
    return resolveProcessStatus(process?.status) || fallback || '-'
  }

  const childOrders = liveBorrowOrders.value.filter(order => order.orderNo !== rootOrderNo && resolveRootOrderNo(order.orderNo) === rootOrderNo)
  if (childOrders.length > 0) {
    const runningHandlers = Array.from(new Set(
      childOrders
        .filter(order => {
          const childProcess = allBorrowProcesses.value.find(item => item.businessKey === order.orderNo)
          return childProcess?.status === 'RUNNING'
        })
        .map(order => {
          const childTask = relatedOrderTaskMap.value[order.orderNo]
          return childTask?.assignee ? resolveUserName(childTask.assignee) : order.currentHandler
        })
        .filter((handler): handler is string => Boolean(handler))
    ))

    if (runningHandlers.length > 0) {
      return runningHandlers.join('、')
    }

    const childStatuses = Array.from(new Set(
      childOrders
        .map(order => {
          const childProcess = allBorrowProcesses.value.find(item => item.businessKey === order.orderNo)
          return childProcess ? resolveProcessStatus(childProcess.status) : order.status
        })
        .filter((status): status is string => Boolean(status))
    ))

    if (childStatuses.length > 0) {
      return childStatuses.join('、')
    }
  }

  if (process?.status === 'RUNNING') {
    return currentOrder?.currentHandler || fallback || '-'
  }

  return resolveProcessStatus(process?.status) || fallback || '-'
}

function resolveBorrowStepLabel(taskDefinitionKey?: string, fallback?: string) {
  switch (taskDefinitionKey) {
    case 'approveDemandTask':
      return '需求审批'
    case 'reviewDemandTask':
      return '需求审核'
    case 'applicantReapplyTask':
      return '退回申请人修改'
    case 'analyzeDemandTask':
      return '借阅分析'
    case 'lendApproveTask':
      return '借出审批'
    case 'handleBorrowTask':
      return '借阅办理'
    case 'receiveBorrowTask':
      return '申请人领取/下载'
    default:
      return fallback || '流程处理'
  }
}

function extractErrorMessage(error: unknown) {
  const candidate = error as { response?: { data?: { message?: string } }; message?: string }
  return candidate?.response?.data?.message || candidate?.message || '请稍后重试或联系管理员查看日志'
}

function normalizeBorrowKind(value?: string) {
  if (!value) return '电子件'
  if (value.includes('原件')) return '原件'
  if (value.includes('实物')) return '实物'
  if (value.includes('复印')) return '复印件'
  return '电子件'
}

function parseArchiveTitle(title: string) {
  const [company, ...rest] = title.split(' | ')
  return {
    company: rest.length ? company : '未标注公司',
    name: rest.length ? rest.join(' | ') : title
  }
}

function processKeyByTask(task: WorkflowTaskRow) {
  return allBorrowProcesses.value.find(process => process.processInstanceId === task.processInstanceId)?.processDefinitionKey ?? ''
}

function mapBorrowRecordToMineRow(record: BorrowRecord): MineRow {
  const title = parseArchiveTitle(record.archiveTitle)
  const process = borrowProcessMap.value.get(record.borrowCode)
  return {
    company: title.company,
    businessCode: record.archiveCode,
    documentName: title.name,
    documentType: normalizeBorrowKind(record.borrowType),
    businessModule: '借阅管理',
    archivePeriod: record.expectedReturnDate?.slice(0, 7) ?? '-',
    orderNo: record.borrowCode,
    status: resolveProcessStatus(process?.status),
    borrowTime: formatDate(record.borrowedAt),
    attachment: '-'
  }
}

function taskStage(task?: WorkflowTaskRow) {
  switch (task?.taskDefinitionKey) {
    case 'reviewDemandTask':
    case 'approveDemandTask':
      return 'approval'
    case 'analyzeDemandTask':
      return 'analysis'
    case 'lendApproveTask':
      return 'lendingApproval'
    case 'handleBorrowTask':
      return 'handling'
    case 'receiveBorrowTask':
      return 'receipt'
    case 'rejectedReturnTask':
      return 'apply'
    case 'applicantReapplyTask':
      return 'apply'
    default:
      return 'apply'
  }
}

function buildChildOrderNo(parentOrderNo: string, index: number) {
  return `${parentOrderNo}-A${String(index).padStart(2, '0')}`
}

function buildHandlingChildOrderNo(parentOrderNo: string, index: number) {
  return `${parentOrderNo}-H${String(index).padStart(2, '0')}`
}

function resolveRootOrderNo(orderNo: string) {
  let current = orderNo || ''
  while (/(?:-A\d{2}|-H\d{2})$/.test(current)) {
    current = current.replace(/(?:-A\d{2}|-H\d{2})$/, '')
  }
  return current
}

function groupBorrowItemsByApprover(items: BorrowItem[]) {
  const groups = new Map<string, BorrowItem[]>()
  items.forEach(item => {
    const key = item.lendingApprover || ''
    const list = groups.get(key) ?? []
    list.push(item)
    groups.set(key, list)
  })
  return groups
}

function groupBorrowItemsByHandler(items: BorrowItem[]) {
  const groups = new Map<string, BorrowItem[]>()
  items.forEach(item => {
    const key = item.handler || ''
    const list = groups.get(key) ?? []
    list.push(item)
    groups.set(key, list)
  })
  return groups
}

function renewTaskStage(task?: WorkflowTaskRow) {
  switch (task?.taskDefinitionKey) {
    case 'renewReviewTask':
      return 'approval'
    case 'renewHandleTask':
      return 'approval'
    default:
      return 'apply'
  }
}

function hydrateBorrowOrder(order: BorrowOrder) {
  borrowOrder.orderNo = order.orderNo
  borrowOrder.status = order.status
  borrowForm.userName = order.userName
  borrowForm.department = order.userDepartment || ''
  borrowForm.applicant = order.applicantName
  borrowForm.applyTime = formatDateTime(order.applyTime)
  borrowForm.purpose = order.purpose || ''
  borrowForm.reason = order.reason || ''
  borrowForm.attachment = order.reasonAttachment || ''
  borrowForm.approvalComment = order.approvalComment || ''
  borrowForm.demandApprover = order.demandApprover || ''
  borrowForm.demandReviewer = order.demandReviewer || ''
  borrowForm.demandAnalyst = order.demandAnalyst || ''
  borrowForm.ccUsers = order.ccUsers || []
  borrowItems.value = order.details.length
    ? order.details.map((detail, index) => itemFactory({
      id: detail.id ?? Date.now() + index,
      company: detail.company,
      documentType: detail.documentType || '',
      description: detail.description || detail.documentName || '',
      demandType: detail.demandType || '',
      needReturn: detail.needReturn,
      expectedReturnDate: detail.expectedReturnDate || '',
      lendingApprover: detail.lendingApprover,
      lendingRemark: detail.lendingRemark,
      handler: detail.handler,
      handlerRemark: detail.handlerRemark
    }))
    : [itemFactory()]
}

function hydrateRenewOrder(order: BorrowRenewOrder) {
  renewForm.renewOrderNo = order.renewOrderNo
  renewForm.sourceOrderNo = order.sourceOrderNo
  renewForm.userName = order.userName
  renewForm.department = order.userDepartment || ''
  renewForm.applicant = order.applicantName
  renewForm.purpose = order.purpose || ''
  renewForm.reason = order.reason || ''
  renewForm.attachment = order.reasonAttachment || ''
  renewForm.reviewer = order.reviewer || ''
  renewForm.handler = order.handler || ''
  renewForm.ccUsers = order.ccUsers || []
  renewItems.value = order.details.map((detail, index) => ({
    id: detail.id ?? index + 1,
    businessCode: detail.businessCode || '',
    documentName: detail.documentName || '',
    company: detail.company || '',
    businessModule: '借阅管理',
    archivePeriod: detail.currentExpireTime?.slice(0, 7) || '-',
    borrowType: detail.borrowType || '',
    borrowTime: detail.borrowTime || '',
    currentExpireTime: detail.currentExpireTime || '',
    renewExpireTime: detail.renewExpireTime || '',
    renewReason: detail.renewReason || ''
  }))
  renewForm.returnDate = renewItems.value[0]?.renewExpireTime || ''
  renewForm.renewReason = renewItems.value[0]?.renewReason || order.reason || ''
  selectedRenewItemIds.value = renewItems.value.map(item => item.id)
}

async function openBorrowTodo(task: WorkflowTaskRow) {
  const order = liveBorrowOrders.value.find(item => item.orderNo === task.businessKey) || await fetchBorrowOrder(task.businessKey || '')
  hydrateBorrowOrder(order)
  await loadRelatedOrderTaskMap(order.orderNo)
  currentBorrowProcessKey.value = processKeyByTask(task) || 'borrowRequest'
  activeBorrowStage.value = taskStage(task)
  currentBorrowTaskDef.value = task.taskDefinitionKey || ''
  currentHandlingOwner.value = resolveUserName(task.assignee)
  todoCurrentModule.value = 'borrow'
  todoVisible.value = true
  await loadProcessLogs(order.orderNo, 'borrow')
}

async function openDraft(row: DraftRow) {
  try {
    const order = liveBorrowOrders.value.find(item => item.orderNo === row.businessKey) || await fetchBorrowOrder(row.businessKey)
    hydrateBorrowOrder(order)
    activeModule.value = 'borrow'
    activeBorrowStage.value = 'apply'
    currentBorrowTaskDef.value = ''
    currentBorrowProcessKey.value = 'borrowRequest'
    borrowLogs.value = buildBorrowFallbackLogs()
    ElMessage.success('已打开草稿')
  } catch (error) {
    console.error('Failed to open draft:', error)
    ElMessage.error(`草稿打开失败：${extractErrorMessage(error)}`)
  }
}

function stageByOrderStatus(status?: string) {
  if (!status) return 'apply'
  if (status.includes('需求审批') || status.includes('需求审核')) return 'approval'
  if (status.includes('借阅分析')) return 'analysis'
  if (status.includes('借出审批')) return 'lendingApproval'
  if (status.includes('借阅办理')) return 'handling'
  if (status.includes('领取') || status.includes('下载') || status.includes('归还') || status.includes('完成')) return 'receipt'
  return 'apply'
}

async function openRelatedBorrowOrder(orderNo: string) {
  try {
    const order = liveBorrowOrders.value.find(item => item.orderNo === orderNo) || await fetchBorrowOrder(orderNo)
    hydrateBorrowOrder(order)
    await loadRelatedOrderTaskMap(order.orderNo)
    const process = allBorrowProcesses.value.find(item => item.businessKey === order.orderNo)
    const processTasks = process ? await getProcessTasks(process.processInstanceId) as WorkflowTaskRow[] : []
    const activeTask = processTasks.find(task => task.status === 'ACTIVE') || getRepresentativeBorrowTask(order.orderNo)
    currentBorrowProcessKey.value = activeTask ? processKeyByTask(activeTask) || process?.processDefinitionKey || 'borrowRequest' : process?.processDefinitionKey || 'borrowRequest'
    activeBorrowStage.value = activeTask ? taskStage(activeTask) : stageByOrderStatus(order.status)
    currentBorrowTaskDef.value = activeTask?.taskDefinitionKey || ''
    currentHandlingOwner.value = resolveUserName(activeTask?.assignee) || order.currentHandler || ''
    todoCurrentModule.value = 'borrow'
    todoVisible.value = true
    await loadProcessLogs(order.orderNo, 'borrow')
  } catch (error) {
    console.error('Failed to open related borrow order:', error)
    ElMessage.error(`关联申请单打开失败：${extractErrorMessage(error)}`)
  }
}

async function loadRelatedOrderTaskMap(orderNo: string) {
  const rootOrderNo = resolveRootOrderNo(orderNo)
  const targetProcesses = allBorrowProcesses.value.filter(process => resolveRootOrderNo(process.businessKey) === rootOrderNo)
  const entries = await Promise.all(targetProcesses.map(async process => {
    try {
      const tasks = await getProcessTasks(process.processInstanceId) as WorkflowTaskRow[]
      return [process.businessKey, tasks.find(task => task.status === 'ACTIVE')] as const
    } catch {
      return [process.businessKey, undefined] as const
    }
  }))
  relatedOrderTaskMap.value = Object.fromEntries(entries)
}

async function preloadMyApplicationTaskMap() {
  const rootOrderNos = Array.from(new Set(
    myWorkflowProcesses.value
      .filter(process => borrowProcessKeys.includes(process.processDefinitionKey))
      .map(process => resolveRootOrderNo(process.businessKey))
  ))
  if (!rootOrderNos.length) {
    return
  }

  const targetProcesses = allBorrowProcesses.value.filter(process => rootOrderNos.includes(resolveRootOrderNo(process.businessKey)))
  const entries = await Promise.all(targetProcesses.map(async process => {
    try {
      const tasks = await getProcessTasks(process.processInstanceId) as WorkflowTaskRow[]
      return [process.businessKey, tasks.find(task => task.status === 'ACTIVE')] as const
    } catch {
      return [process.businessKey, undefined] as const
    }
  }))
  relatedOrderTaskMap.value = { ...relatedOrderTaskMap.value, ...Object.fromEntries(entries) }
}

async function openRenewApplication(orderNo: string) {
  try {
    const order = liveBorrowRenewOrders.value.find(item => item.renewOrderNo === orderNo) || await fetchBorrowRenewOrder(orderNo)
    hydrateRenewOrder(order)
    activeRenewStage.value = 'approval'
    todoCurrentModule.value = 'renew'
    todoVisible.value = true
    await loadProcessLogs(order.renewOrderNo, 'renew')
  } catch (error) {
    console.error('Failed to open renew application:', error)
    ElMessage.error(`申请单打开失败：${extractErrorMessage(error)}`)
  }
}

async function openMyApplication(row: MyApplicationRow) {
  if (row.module === 'renew') {
    await openRenewApplication(row.businessKey)
    return
  }
  await openRelatedBorrowOrder(row.businessKey)
}

async function openRenewTodo(task: WorkflowTaskRow) {
  const order = liveBorrowRenewOrders.value.find(item => item.renewOrderNo === task.businessKey) || await fetchBorrowRenewOrder(task.businessKey || '')
  hydrateRenewOrder(order)
  activeRenewStage.value = renewTaskStage(task)
  todoCurrentModule.value = 'renew'
  todoVisible.value = true
  await loadProcessLogs(order.renewOrderNo, 'renew')
}

async function openTodo(row: TodoRow) {
  try {
    if (row.module === 'renew') {
      await openRenewTodo(row)
    } else {
      await openBorrowTodo(row)
    }
  } catch (error) {
    console.error('Failed to open todo:', error)
    ElMessage.error(`待办打开失败：${extractErrorMessage(error)}`)
  }
}

function syncDepartment(name: string) {
  const target = users.find(item => item.name === name)
  if (target) borrowForm.department = target.dept
}

function resolveDemandAnalyst() {
  return borrowForm.demandAnalyst || borrowForm.demandReviewer || borrowForm.demandApprover
}

function addBorrowItem() {
  borrowItems.value.push(itemFactory())
}

function isReturnRequiredDemandType(demandType?: string) {
  return demandType === '借阅原件' || demandType === '实物' || demandType === '借阅实物'
}

function onBorrowDemandTypeChange(row: BorrowItem) {
  if (isReturnRequiredDemandType(row.demandType)) {
    row.needReturn = true
  }
}

function onBorrowItemSelectionChange(selection: BorrowItem[]) {
  selectedBorrowItems.value = selection
}

function removeSelectedBorrowItems() {
  if (selectedBorrowItems.value.length === 0) {
    ElMessage.warning('请先选择要删除的借阅内容')
    return
  }
  if (selectedBorrowItems.value.length >= borrowItems.value.length) {
    ElMessage.warning('至少保留一条申请内容')
    return
  }
  const selectedIds = new Set(selectedBorrowItems.value.map(item => item.id))
  borrowItems.value = borrowItems.value.filter(item => !selectedIds.has(item.id))
  selectedBorrowItems.value = []
}

function removeBorrowItem(index: number) {
  if (borrowItems.value.length <= 1) {
    ElMessage.warning('至少保留一条申请内容')
    return
  }
  borrowItems.value.splice(index, 1)
  selectedBorrowItems.value = selectedBorrowItems.value.filter(item => borrowItems.value.some(row => row.id === item.id))
}

function resetBorrow() {
  borrowItems.value = [itemFactory()]
  selectedBorrowItems.value = []
  borrowOrder.orderNo = `BOR-${Date.now()}`
  borrowOrder.status = '草稿'
  borrowForm.applyTime = formatDateTime(new Date().toISOString())
  ElMessage.success('借阅申请已重置')
}

function buildBorrowOrderPayload(orderNo: string, status: string, currentHandler?: string) {
  return {
    orderNo,
    userName: borrowForm.userName,
    userDepartment: borrowForm.department,
    applicantName: borrowForm.applicant,
    applyTime: nowAsLocalDateTime(),
    purpose: borrowForm.purpose,
    reason: borrowForm.reason,
    reasonAttachment: borrowForm.attachment,
    approvalComment: status === '草稿' ? borrowForm.approvalComment : '',
    demandApprover: borrowForm.demandApprover,
    demandReviewer: borrowForm.demandReviewer,
    demandAnalyst: resolveDemandAnalyst(),
    ccUsers: borrowForm.ccUsers,
    status,
    currentHandler,
    details: borrowItems.value.map((item, index) => ({
      businessCode: item.description?.startsWith('BR-') ? item.description : `BR-${Date.now()}-${index + 1}`,
      documentName: item.description,
      company: item.company,
      documentType: item.documentType,
      description: item.description,
      demandType: item.demandType,
      needReturn: item.needReturn,
      expectedReturnDate: item.expectedReturnDate || undefined,
      lendingApprover: item.lendingApprover,
      lendingRemark: item.lendingRemark,
      handler: item.handler,
      handlerRemark: item.handlerRemark
    }))
  }
}

async function upsertBorrowOrder(payload: ReturnType<typeof buildBorrowOrderPayload>) {
  const existingOrder = liveBorrowOrders.value.find(order => order.orderNo === payload.orderNo) || await fetchBorrowOrder(payload.orderNo).catch(() => undefined)
  if (existingOrder) {
    return updateBorrowOrder(payload.orderNo, payload)
  }
  return createBorrowOrder(payload)
}

async function saveBorrowDraft() {
  if (draftSaving.value) return
  if (!borrowItems.value.length) {
    ElMessage.warning('请至少保留一条借阅内容')
    return
  }
  draftSaving.value = true
  try {
    const orderNo = borrowOrder.orderNo?.startsWith('BOR-') ? borrowOrder.orderNo : `BOR-${Date.now()}`
    const saved = await upsertBorrowOrder(buildBorrowOrderPayload(orderNo, '草稿', borrowForm.applicant))
    hydrateBorrowOrder(saved)
    borrowOrder.status = '草稿'
    await loadLiveData()
    activeModule.value = 'drafts'
    ElMessage.success('已保存为草稿')
  } catch (error) {
    console.error('Failed to save borrow draft:', error)
    ElMessage.error(`保存草稿失败：${extractErrorMessage(error)}`)
  } finally {
    draftSaving.value = false
  }
}

function flattenDocumentTypes(nodes: DocumentTypeTreeNode[]): DocumentTypeTreeNode[] {
  return nodes.flatMap(node => [node, ...flattenDocumentTypes(node.children ?? [])])
}

function formatCompanyOption(item: CompanyInfo) {
  return item.companyCode && item.companyCode !== item.companyName ? `${item.companyName}（${item.companyCode}）` : item.companyName
}

function formatDocumentTypeOption(item: DocumentTypeTreeNode) {
  return item.typeCode && item.typeCode !== item.typeName ? `${item.typeName}（${item.typeCode}）` : item.typeName
}

async function loadBorrowReferenceOptions() {
  try {
    const [companyRows, documentTypeRows] = await Promise.all([
      fetchCompanyInfos({ enabledFlag: 'Y' }),
      fetchDocumentTypeTree()
    ])
    companyConfigOptions.value = companyRows.filter(item => item.enabledFlag === 'Y')
    documentTypeConfigOptions.value = documentTypeRows
  } catch (error) {
    console.error('Failed to load borrow reference options:', error)
    ElMessage.warning('公司或文档类型配置加载失败，已使用默认选项')
  }
}

function validateBorrowItems() {
  const invalidIndex = borrowItems.value.findIndex(item =>
    !item.company ||
    !item.documentType ||
    !item.description.trim() ||
    !item.demandType
  )
  if (invalidIndex >= 0) {
    ElMessage.warning(`请完善借阅内容第 ${invalidIndex + 1} 行：公司、文档类型、需求文档详细说明、需求类型为必填`)
    return false
  }
  const missingReturnDateIndex = borrowItems.value.findIndex(item => isReturnRequiredDemandType(item.demandType) && !item.expectedReturnDate)
  if (missingReturnDateIndex >= 0) {
    ElMessage.warning(`请完善借阅内容第 ${missingReturnDateIndex + 1} 行：需求类型为借阅原件或实物时，预计归还时间必填`)
    return false
  }
  return true
}

async function loadProcessLogs(businessKey: string, target: 'borrow' | 'renew') {
  const process = await resolveLogProcess(businessKey, target)
  if (!process) {
    if (target === 'borrow') {
      borrowLogs.value = buildBorrowFallbackLogs()
    }
    return
  }
  let tasks: WorkflowTaskRow[] = []
  try {
    tasks = await getProcessTasks(process.processInstanceId) as WorkflowTaskRow[]
  } catch (error) {
    console.error('Failed to load workflow task logs:', error)
    if (target === 'borrow') {
      borrowLogs.value = buildBorrowFallbackLogs(process)
    } else {
      renewLogs.value = []
    }
    return
  }
  const dedupedTasks = Array.from(
    tasks.reduce((map, task) => {
      const current = map.get(task.taskId)
      if (!current) {
        map.set(task.taskId, task)
      } else if (current.status !== 'COMPLETED' && task.status === 'COMPLETED') {
        map.set(task.taskId, task)
      }
      return map
    }, new Map<string, WorkflowTaskRow>()).values()
  )

  const latestCompletedTaskId = dedupedTasks
    .slice()
    .filter(task => task.status !== 'ACTIVE')
    .sort((a, b) => new Date(b.completeTime || b.createTime || 0).getTime() - new Date(a.completeTime || a.createTime || 0).getTime())[0]?.taskId

  const actualLogs = dedupedTasks
    .slice()
    .sort((a, b) => new Date(a.completeTime || a.createTime || 0).getTime() - new Date(b.completeTime || b.createTime || 0).getTime())
    .map((task, index) => ({
      taskId: task.taskId,
      step: resolveBorrowStepLabel(task.taskDefinitionKey, task.taskName),
      taskDefinitionKey: task.taskDefinitionKey,
      handler: resolveUserName(task.assignee),
      action: task.status === 'ACTIVE'
        ? '待处理'
        : process.status === 'REJECTED' && task.taskId === latestCompletedTaskId
          ? '驳回'
          : '已处理',
      comment: task.status === 'ACTIVE'
        ? '等待当前处理人操作'
        : task.comment || (process.status === 'REJECTED' && task.taskId === latestCompletedTaskId
          ? '流程已驳回并退回申请人处理'
          : '流程节点已完成'),
      time: formatDateTime(task.completeTime || task.createTime)
    }))

  if (target === 'borrow') {
    const existingKeys = new Set(actualLogs.map(log => log.taskDefinitionKey))
    const submitLog = {
      step: '借阅申请',
      handler: borrowForm.applicant || process.initiatorName || '-',
      action: '提交',
      comment: borrowForm.approvalComment || '发起借阅申请',
      time: formatDateTime(process.startTime)
    }
    const fallbackRejectComment = borrowOrder.status === '已驳回' && borrowForm.approvalComment
      ? borrowForm.approvalComment
      : ''
    const logsWithRejectReason = actualLogs.map((log, index) => {
      if (index === 0 && log.action === '驳回' && fallbackRejectComment && (!log.comment || log.comment === '流程已驳回并退回申请人处理')) {
        return { ...log, comment: fallbackRejectComment }
      }
      return log
    })
    if (fallbackRejectComment && !logsWithRejectReason.some(log => log.action === '驳回')) {
      logsWithRejectReason.unshift({
        step: '驳回处理',
        taskDefinitionKey: 'rejectFallback',
        handler: borrowOrder.currentHandler || '-',
        action: '驳回',
        comment: fallbackRejectComment,
        time: formatDateTime(process.lastUpdateDate || process.endTime || process.startTime)
      })
    }
    const plannedLogs = [
      { key: 'approveDemandTask', step: '需求审批', handler: borrowForm.demandApprover },
      { key: 'reviewDemandTask', step: '需求审核', handler: borrowForm.demandReviewer }
    ]
      .filter(item => item.handler && !existingKeys.has(item.key))
      .map(item => ({
        step: item.step,
        handler: item.handler || '-',
        action: '待处理',
        comment: '等待当前处理人操作',
        time: formatDateTime(process.startTime)
      }))

    borrowLogs.value = [submitLog, ...logsWithRejectReason.map(({ taskId, taskDefinitionKey, ...log }) => log), ...plannedLogs]
  } else {
    renewLogs.value = actualLogs.map(({ taskId, taskDefinitionKey, ...log }) => log)
  }
}

async function resolveLogProcess(businessKey: string, target: 'borrow' | 'renew') {
  const cachedProcess = allBorrowProcesses.value.find(item => item.businessKey === businessKey)
  if (cachedProcess) return cachedProcess

  if (target === 'borrow') {
    const cachedOrder = liveBorrowOrders.value.find(item => item.orderNo === businessKey)
    const order = cachedOrder || await fetchBorrowOrder(businessKey)
    if (order?.workflowInstanceId) {
      return {
        processInstanceId: order.workflowInstanceId,
        businessKey: order.orderNo,
        processDefinitionKey: 'borrowRequest',
        status: normalizeWorkflowStatus(order.status),
        initiatorName: order.applicantName,
        startTime: order.applyTime,
        lastUpdateDate: order.applyTime
      } as WorkflowProcessRow
    }
    return undefined
  }

  const cachedRenewOrder = liveBorrowRenewOrders.value.find(item => item.renewOrderNo === businessKey)
  const renewOrder = cachedRenewOrder || await fetchBorrowRenewOrder(businessKey)
  if (renewOrder?.workflowInstanceId) {
    return {
      processInstanceId: renewOrder.workflowInstanceId,
      businessKey: renewOrder.renewOrderNo,
      processDefinitionKey: 'borrowRenewRequest',
      status: normalizeWorkflowStatus(renewOrder.status),
      initiatorName: renewOrder.applicantName,
      startTime: renewOrder.applyTime,
      lastUpdateDate: renewOrder.applyTime
    } as WorkflowProcessRow
  }
  return undefined
}

function normalizeWorkflowStatus(status?: string) {
  if (!status) return 'RUNNING'
  if (status.includes('驳回') || status.includes('退回')) return 'REJECTED'
  if (status.includes('完成') || status.includes('已归还')) return 'APPROVED'
  return 'RUNNING'
}

function buildBorrowFallbackLogs(process?: Partial<WorkflowProcessRow>) {
  const logs: LogRow[] = []
  const applyTime = process?.startTime || borrowForm.applyTime
  if (applyTime) {
    logs.push({
      step: '借阅申请',
      handler: borrowForm.applicant || process?.initiatorName || '-',
      action: borrowOrder.status === '草稿' ? '草稿' : '提交',
      comment: borrowOrder.status === '草稿' ? '申请尚未提交' : (borrowForm.approvalComment || '发起借阅申请'),
      time: formatDateTime(applyTime)
    })
  }
  if (borrowOrder.status === '已驳回' && borrowForm.approvalComment) {
    logs.push({
      step: '驳回处理',
      handler: borrowOrder.currentHandler || '-',
      action: '驳回',
      comment: borrowForm.approvalComment,
      time: formatDateTime(new Date().toISOString())
    })
  }
  return logs
}

async function loadLiveData() {
  liveLoading.value = true
  borrowForm.applicant = currentWorkflowUser.value.name
  try {
    const [records, orders, renewOrderList, tasks, processes, participated, listed] = await Promise.all([
      fetchBorrowRecords(),
      fetchBorrowOrders(),
      fetchBorrowRenewOrders(),
      fetchMyTasks(selectedWorkflowUserId.value),
      fetchMyProcesses(selectedWorkflowUserId.value),
      fetchParticipatedProcesses(selectedWorkflowUserId.value),
      listProcesses()
    ])
    liveBorrowRecords.value = (records as BorrowRecord[]) ?? []
    liveBorrowOrders.value = (orders as BorrowOrder[]) ?? []
    liveBorrowRenewOrders.value = (renewOrderList as BorrowRenewOrder[]) ?? []
    myWorkflowTasks.value = (tasks as WorkflowTaskRow[]) ?? []
    myWorkflowProcesses.value = ((processes as WorkflowProcessRow[]) ?? []).filter(item => borrowProcessKeys.concat(renewProcessKeys).includes(item.processDefinitionKey))
    participatedBorrowProcesses.value = ((participated as WorkflowProcessRow[]) ?? []).filter(item => borrowProcessKeys.concat(renewProcessKeys).includes(item.processDefinitionKey))
    allBorrowProcesses.value = ((listed as WorkflowProcessRow[]) ?? []).filter(item => borrowProcessKeys.concat(renewProcessKeys).includes(item.processDefinitionKey))
    await preloadMyApplicationTaskMap()

    const currentBorrowProcess = allBorrowProcesses.value.find(process => process.businessKey === borrowOrder.orderNo)
    if (currentBorrowProcess) {
      borrowOrder.status = resolveProcessStatus(currentBorrowProcess.status)
      await loadProcessLogs(borrowOrder.orderNo, 'borrow')
    }
    const currentBorrowTask = myWorkflowTasks.value.find(task => task.businessKey === borrowOrder.orderNo && borrowProcessKeys.includes(processKeyByTask(task)))
    if (currentBorrowTask) {
      const currentOrder = liveBorrowOrders.value.find(order => order.orderNo === borrowOrder.orderNo)
      if (currentOrder) hydrateBorrowOrder(currentOrder)
      activeBorrowStage.value = taskStage(currentBorrowTask)
      currentBorrowTaskDef.value = currentBorrowTask.taskDefinitionKey || ''
      currentBorrowProcessKey.value = processKeyByTask(currentBorrowTask) || 'borrowRequest'
    }
    if (renewForm.renewOrderNo && renewProcessMap.value.get(renewForm.renewOrderNo)) {
      const currentRenewOrder = liveBorrowRenewOrders.value.find(order => order.renewOrderNo === renewForm.renewOrderNo)
      if (currentRenewOrder) hydrateRenewOrder(currentRenewOrder)
      await loadProcessLogs(renewForm.renewOrderNo, 'renew')
    }
  } catch (error) {
    console.error('Failed to load borrow live data:', error)
    ElMessage.error('真实借阅数据加载失败，请检查后端服务')
  } finally {
    liveLoading.value = false
  }
}

async function submitBorrow() {
  if (borrowSubmitting.value) return
  if (!validateBorrowItems()) return
  borrowSubmitting.value = true
  const sharedBorrowCode = borrowOrder.orderNo?.startsWith('BOR-') ? borrowOrder.orderNo : `BOR-${Date.now()}`
  const firstItem = borrowItems.value[0]
  try {
    await upsertBorrowOrder(buildBorrowOrderPayload(sharedBorrowCode, '待需求审批', borrowForm.demandApprover))

    await startProcess({
      processDefinitionKey: 'borrowRequest',
      businessKey: sharedBorrowCode,
      businessType: 'BORROW',
      initiatorId: selectedWorkflowUserId.value,
      initiatorName: currentWorkflowUser.value.name,
      variables: {
        applicantName: borrowForm.applicant,
        applicantId: getUserIdByName(borrowForm.applicant),
        borrowerName: borrowForm.userName,
        demandApproverId: getUserIdByName(borrowForm.demandApprover),
        demandReviewerId: getUserIdByName(borrowForm.demandReviewer),
        demandAnalystId: getUserIdByName(resolveDemandAnalyst()),
        lendingApproverId: getUserIdByName(firstItem?.lendingApprover),
        handlerId: getUserIdByName(firstItem?.handler),
        splitCreated: false,
        handlingSplitCreated: false,
        purpose: borrowForm.purpose,
        reason: borrowForm.reason,
        comment: '发起借阅申请',
        itemCount: borrowItems.value.length
      }
    })

    borrowOrder.orderNo = sharedBorrowCode
    borrowOrder.status = '待需求审批'
    activeBorrowStage.value = 'approval'
    try {
      await loadLiveData()
      await loadProcessLogs(sharedBorrowCode, 'borrow')
    } catch (refreshError) {
      console.error('Borrow submitted but refresh failed:', refreshError)
      ElMessage.warning('借阅申请已提交，页面刷新稍后重试')
    }
    ElMessage.success(`借阅申请已提交，已创建 ${borrowItems.value.length} 条真实借阅记录`)
  } catch (error) {
    console.error('Failed to submit borrow request:', error)
    ElMessage.error(`借阅申请提交失败：${extractErrorMessage(error)}`)
  } finally {
    borrowSubmitting.value = false
  }
}

function getCurrentBorrowTask() {
  return myWorkflowTasks.value.find(task => task.businessKey === borrowOrder.orderNo && borrowProcessKeys.includes(processKeyByTask(task)) && task.status === 'ACTIVE')
}

function getRelatedActiveBorrowTasks(orderNo: string) {
  const rootOrderNo = resolveRootOrderNo(orderNo)
  return myWorkflowTasks.value.filter(task =>
    task.status === 'ACTIVE' &&
    borrowProcessKeys.includes(processKeyByTask(task)) &&
    resolveRootOrderNo(task.businessKey || '') === rootOrderNo
  )
}

function getRepresentativeBorrowTask(orderNo: string) {
  const directTask = myWorkflowTasks.value.find(task =>
    task.businessKey === orderNo &&
    borrowProcessKeys.includes(processKeyByTask(task)) &&
    task.status === 'ACTIVE'
  )
  if (directTask) return directTask

  const relatedTasks = getRelatedActiveBorrowTasks(orderNo)
  const preferredTask =
    relatedTasks.find(task => task.taskDefinitionKey === 'receiveBorrowTask') ||
    relatedTasks.find(task => task.taskDefinitionKey === 'handleBorrowTask') ||
    relatedTasks.find(task => task.taskDefinitionKey === 'lendApproveTask') ||
    relatedTasks.find(task => task.taskDefinitionKey === 'analyzeDemandTask') ||
    relatedTasks.find(task => task.taskDefinitionKey === 'reviewDemandTask') ||
    relatedTasks.find(task => task.taskDefinitionKey === 'approveDemandTask') ||
    relatedTasks[0]

  return preferredTask
}

function getReceiptBorrowTasks(orderNo: string) {
  return getRelatedActiveBorrowTasks(orderNo).filter(task => task.taskDefinitionKey === 'receiveBorrowTask')
}

function setBorrowApprovalDecision(action: string) {
  borrowApproval.decision = action
  if (!borrowApproval.comment.trim() || ['同意', '拒绝'].includes(borrowApproval.comment.trim())) {
    borrowApproval.comment = action
  }
}

function setLendingDecision(action: string) {
  lendingApproval.decision = action
  if (!lendingApproval.comment.trim() || ['同意', '不同意'].includes(lendingApproval.comment.trim())) {
    lendingApproval.comment = action
  }
}

async function approveBorrow(action: string) {
  const task = getCurrentBorrowTask()
  if (!task) {
    ElMessage.warning('当前流程用户下没有可处理的借阅审批任务')
    return
  }
  const approvalComment = borrowApproval.comment.trim() || action
  borrowApproval.comment = approvalComment
  try {
    if (action === '同意') {
      await completeTask({
        taskId: task.taskId,
        variables: {
          comment: approvalComment,
          rejected: false
        }
      })
    } else {
      await rejectTask({ taskId: task.taskId, reason: approvalComment })
    }
    await loadLiveData()
    await loadProcessLogs(borrowOrder.orderNo, 'borrow')
    if (action !== '同意') {
      activeBorrowStage.value = 'apply'
    } else if (task.taskDefinitionKey === 'reviewDemandTask') {
      activeBorrowStage.value = 'analysis'
    } else if (task.taskDefinitionKey === 'approveDemandTask') {
      activeBorrowStage.value = 'approval'
    } else {
      activeBorrowStage.value = 'analysis'
    }
    ElMessage.success(`借阅审批已${action}`)
  } catch (error) {
    console.error('Failed to approve borrow task:', error)
    ElMessage.error(`借阅审批处理失败：${extractErrorMessage(error)}`)
  }
}

async function resubmitBorrow() {
  const task = getCurrentBorrowTask()
  if (!task) {
    ElMessage.warning('当前没有可重新提交的退回任务')
    return
  }
  if (!borrowForm.approvalComment.trim()) {
    ElMessage.warning('请输入审批意见')
    return
  }
  try {
    if (task.taskDefinitionKey === 'applicantReapplyTask') {
      await completeTask({
        taskId: task.taskId,
        variables: {
          comment: borrowForm.approvalComment,
          rejected: false
        }
      })
    } else {
      await submitBorrow()
      return
    }
    borrowOrder.status = '待需求审批'
    await loadLiveData()
    await loadProcessLogs(borrowOrder.orderNo, 'borrow')
    ElMessage.success('借阅申请已重新提交')
  } catch (error) {
    console.error('Failed to resubmit borrow request:', error)
    ElMessage.error(`重新提交失败：${extractErrorMessage(error)}`)
  }
}

async function saveAnalysis() {
  const task = getCurrentBorrowTask()
  if (!task) {
    ElMessage.warning('当前流程用户下没有可处理的需求分析任务')
    return
  }
  if (!analysisAction.comment.trim()) {
    ElMessage.warning('请输入审批意见')
    return
  }
  try {
    await updateBorrowOrder(borrowOrder.orderNo, {
      orderNo: borrowOrder.orderNo,
      userName: borrowForm.userName,
      userDepartment: borrowForm.department,
      applicantName: borrowForm.applicant,
      applyTime: nowAsLocalDateTime(),
      purpose: borrowForm.purpose,
      reason: borrowForm.reason,
      reasonAttachment: borrowForm.attachment,
      approvalComment: borrowForm.approvalComment,
      demandApprover: borrowForm.demandApprover,
      demandReviewer: borrowForm.demandReviewer,
      demandAnalyst: resolveDemandAnalyst(),
      ccUsers: borrowForm.ccUsers,
      status: borrowOrder.status,
      currentHandler: borrowOrder.currentHandler || currentWorkflowUser.value.name,
      details: borrowItems.value.map(item => ({
        businessCode: item.description?.startsWith('BR-') ? item.description : undefined,
        documentName: item.description,
        company: item.company,
        documentType: item.documentType,
        description: item.description,
        demandType: item.demandType,
        needReturn: item.needReturn,
        expectedReturnDate: item.expectedReturnDate,
        lendingApprover: item.lendingApprover,
        lendingRemark: item.lendingRemark,
        handler: item.handler,
        handlerRemark: item.handlerRemark
      }))
    })

    if (analysisAction.action === '转他人处理') {
      await delegateTask({ taskId: task.taskId, assignee: getUserIdByName(analysisAction.nextHandler), reason: analysisAction.comment })
    } else if (analysisAction.action === '驳回' || analysisAction.action === '终止') {
      await rejectTask({ taskId: task.taskId, reason: analysisAction.comment || analysisAction.action })
    } else if (analysisAction.action === '分单') {
      const approverGroups = Array.from(groupBorrowItemsByApprover(borrowItems.value).entries()).filter(([approver]) => Boolean(approver))
      if (approverGroups.length <= 1) {
        const firstItem = borrowItems.value[0]
        await completeTask({
          taskId: task.taskId,
          variables: {
            comment: analysisAction.comment || '借出审批人为同一人，转入统一借出审批；通过后再按办理人分单。',
            splitCreated: false,
            lendingApproverId: getUserIdByName(firstItem?.lendingApprover),
            handlerId: getUserIdByName(firstItem?.handler)
          }
        })
        activeBorrowStage.value = 'lendingApproval'
        await loadLiveData()
        await loadProcessLogs(borrowOrder.orderNo, 'borrow')
        ElMessage.success('借出审批人为同一人，已直接流转到借出审批；借出审批通过后会再按办理人分单')
        return
      }

      await completeTask({
        taskId: task.taskId,
        variables: {
          comment: analysisAction.comment,
          splitCreated: true
        }
      })

      for (const [index, [approver, items]] of approverGroups.entries()) {
        const childOrderNo = buildChildOrderNo(borrowOrder.orderNo, index + 1)
        const firstItem = items[0]
        await createBorrowOrder({
          orderNo: childOrderNo,
          userName: borrowForm.userName,
          userDepartment: borrowForm.department,
          applicantName: borrowForm.applicant,
          applyTime: nowAsLocalDateTime(),
          purpose: borrowForm.purpose,
          reason: borrowForm.reason,
          reasonAttachment: borrowForm.attachment,
          demandApprover: borrowForm.demandApprover,
          demandReviewer: borrowForm.demandReviewer,
          demandAnalyst: borrowForm.demandAnalyst,
          ccUsers: borrowForm.ccUsers,
          status: '待借出审批',
          currentHandler: approver,
          details: items.map((item, itemIndex) => ({
            businessCode: `BR-${Date.now()}-${index + 1}-${itemIndex + 1}`,
            documentName: item.description,
            company: item.company,
            documentType: item.documentType,
            description: item.description,
            demandType: item.demandType,
            needReturn: item.needReturn,
            expectedReturnDate: item.expectedReturnDate,
            lendingApprover: item.lendingApprover,
            lendingRemark: item.lendingRemark,
            handler: item.handler,
            handlerRemark: item.handlerRemark
          }))
        })

        await startProcess({
          processDefinitionKey: 'borrowLendingApprovalChild',
          businessKey: childOrderNo,
          businessType: 'BORROW',
          initiatorId: selectedWorkflowUserId.value,
          initiatorName: currentWorkflowUser.value.name,
          variables: {
            applicantName: borrowForm.applicant,
            applicantId: getUserIdByName(borrowForm.applicant),
            borrowerName: borrowForm.userName,
            lendingApproverId: getUserIdByName(approver),
            handlerId: getUserIdByName(firstItem?.handler),
            handlingSplitCreated: false,
            purpose: borrowForm.purpose,
            reason: borrowForm.reason,
            itemCount: items.length,
            parentOrderNo: borrowOrder.orderNo
          }
        })
      }
      borrowOrder.status = '已分单'
      todoVisible.value = false
    } else {
      const firstItem = borrowItems.value[0]
      await completeTask({
        taskId: task.taskId,
        variables: {
          comment: analysisAction.comment,
          splitCreated: false,
          lendingApproverId: getUserIdByName(firstItem?.lendingApprover),
          handlerId: getUserIdByName(firstItem?.handler)
        }
      })
      activeBorrowStage.value = 'lendingApproval'
    }
    await loadLiveData()
    await loadProcessLogs(borrowOrder.orderNo, 'borrow')
    ElMessage.success('需求分析已保存')
  } catch (error) {
    console.error('Failed to save analysis:', error)
    ElMessage.error(`需求分析提交失败：${extractErrorMessage(error)}`)
  }
}

async function handleLending(action: string) {
  const task = getCurrentBorrowTask()
  if (!task) {
    ElMessage.warning('当前流程用户下没有可处理的借出审批任务')
    return
  }
  if (!lendingApproval.comment.trim()) {
    ElMessage.warning('请输入审批意见')
    return
  }
  try {
    const currentOrderNo = borrowOrder.orderNo
    if (action === '同意') {
      const handlerGroups = Array.from(groupBorrowItemsByHandler(borrowItems.value).entries()).filter(([handler]) => Boolean(handler))
      if (handlerGroups.length > 1) {
        await completeTask({ taskId: task.taskId, variables: { comment: lendingApproval.comment, rejected: false, handlingSplitCreated: true } })
        for (const [index, [handler, items]] of handlerGroups.entries()) {
          const childOrderNo = buildHandlingChildOrderNo(borrowOrder.orderNo, index + 1)
          await createBorrowOrder({
            orderNo: childOrderNo,
            userName: borrowForm.userName,
            userDepartment: borrowForm.department,
            applicantName: borrowForm.applicant,
            applyTime: nowAsLocalDateTime(),
            purpose: borrowForm.purpose,
            reason: borrowForm.reason,
            reasonAttachment: borrowForm.attachment,
            demandApprover: borrowForm.demandApprover,
            demandReviewer: borrowForm.demandReviewer,
            demandAnalyst: borrowForm.demandAnalyst,
            ccUsers: borrowForm.ccUsers,
            status: '待借阅办理',
            currentHandler: handler,
            details: items.map((item, itemIndex) => ({
              businessCode: `BRH-${Date.now()}-${index + 1}-${itemIndex + 1}`,
              documentName: item.description,
              company: item.company,
              documentType: item.documentType,
              description: item.description,
              demandType: item.demandType,
              needReturn: item.needReturn,
              expectedReturnDate: item.expectedReturnDate,
              lendingApprover: item.lendingApprover,
              lendingRemark: item.lendingRemark,
              handler: item.handler,
              handlerRemark: item.handlerRemark
            }))
          })
          await startProcess({
            processDefinitionKey: 'borrowHandlingChild',
            businessKey: childOrderNo,
            businessType: 'BORROW',
            initiatorId: selectedWorkflowUserId.value,
            initiatorName: currentWorkflowUser.value.name,
            variables: {
              applicantName: borrowForm.applicant,
              applicantId: getUserIdByName(borrowForm.applicant),
              borrowerName: borrowForm.userName,
              handlerId: getUserIdByName(handler),
              handlingSplitCreated: false,
              purpose: borrowForm.purpose,
              reason: borrowForm.reason,
              itemCount: items.length,
              parentOrderNo: borrowOrder.orderNo
            }
          })
        }
        borrowOrder.status = '已按办理人分单'
        todoVisible.value = false
      } else {
        await completeTask({
          taskId: task.taskId,
          variables: {
            comment: lendingApproval.comment,
            rejected: false,
            handlingSplitCreated: false
          }
        })
        activeBorrowStage.value = 'handling'
      }
    } else {
      await rejectTask({ taskId: task.taskId, reason: lendingApproval.comment || '借出审批不同意' })
    }
    await loadLiveData()
    await loadProcessLogs(currentOrderNo, 'borrow')
    ElMessage.success(`借出审批已${action}`)
  } catch (error) {
    console.error('Failed to handle lending approval:', error)
    ElMessage.error(`借出审批处理失败：${extractErrorMessage(error)}`)
  }
}

function addDetail() {
  documentDetails.value.push({ id: Date.now(), attachments: [], borrowType: '原件', borrowCount: 1, company: '', businessCode: '', documentName: '', documentType: '', businessModule: '', archivePeriod: '', stockCount: 0, location: '', volumeNo: '', barcode: '', documentStatus: '在库', watermark: false, postMethod: '现场领取', postNo: '' })
}

function removeDetail(index: number) {
  documentDetails.value.splice(index, 1)
}

function watermarkFiles() {
  documentDetails.value.forEach(item => {
    if (item.borrowType === '电子件') item.watermark = true
  })
  ElMessage.success('电子件已打水印')
}

function onDetailSelectionChange(rows: DetailItem[]) {
  selectedDetailIds.value = rows.map(item => item.id)
}

function openDetailQueryDialog() {
  selectedDetailQueryRows.value = []
  detailQueryVisible.value = true
}

function onDetailQuerySelectionChange(rows: DetailQueryRow[]) {
  selectedDetailQueryRows.value = rows
}

function appendSelectedDetailQueryRows() {
  if (!selectedDetailQueryRows.value.length) {
    ElMessage.warning('请先勾选需要新增的借阅文档')
    return
  }
  const existingCodes = new Set(documentDetails.value.map(item => item.businessCode))
  const rowsToAppend = selectedDetailQueryRows.value.filter(row => !existingCodes.has(row.businessCode))
  if (!rowsToAppend.length) {
    ElMessage.warning('所选借阅文档已存在于文档借阅明细中')
    return
  }
  rowsToAppend.forEach(row => {
    documentDetails.value.push({
      id: Date.now() + row.id,
      attachments: [],
      borrowType: '原件',
      borrowCount: 1,
      company: row.company,
      businessCode: row.businessCode,
      documentName: row.documentName,
      documentType: row.documentType,
      businessModule: row.businessModule,
      archivePeriod: row.archivePeriod,
      stockCount: row.stockCount,
      location: row.location,
      volumeNo: row.volumeNo,
      barcode: row.barcode,
      documentStatus: row.documentStatus,
      watermark: false,
      postMethod: '现场领取',
      postNo: ''
    })
  })
  detailQueryVisible.value = false
  ElMessage.success(`已新增 ${rowsToAppend.length} 条借阅文档明细`)
}

function removeSelectedDetails() {
  if (!selectedDetailIds.value.length) {
    ElMessage.warning('请先选择需要删除的借阅文档明细')
    return
  }
  documentDetails.value = documentDetails.value.filter(item => !selectedDetailIds.value.includes(item.id))
  selectedDetailIds.value = []
  ElMessage.success('已删除所选借阅文档明细')
}

function uploadAttachment(row: DetailItem) {
  const fileName = `${row.documentName || row.businessCode || '借阅文档'}-附件-${row.attachments.length + 1}.pdf`
  row.attachments.push(fileName)
  attachments.value.unshift({ name: fileName, watermarkStatus: row.watermark ? '已打水印' : '未打水印', protectionStatus: '未加密', uploader: handlingAction.receiver, uploadTime: formatDateTime(new Date().toISOString()) })
  ElMessage.success('附件已上传')
}

function downloadAttachment(row: { name: string }) {
  ElMessage.success(`开始下载：${row.name}`)
}

function toggleWatermark(row: { watermarkStatus: string }) {
  row.watermarkStatus = row.watermarkStatus === '已打水印' ? '未打水印' : '已打水印'
}

function shouldShowBorrowNotice() {
  return activeModule.value === 'borrow' && activeBorrowStage.value === 'apply' && !borrowNoticeAccepted.value && !todoVisible.value
}

function showBorrowNoticeIfNeeded() {
  if (shouldShowBorrowNotice()) {
    borrowNoticeVisible.value = true
  }
}

function confirmBorrowNotice() {
  borrowNoticeAccepted.value = true
  borrowNoticeVisible.value = false
}

function removeAttachment(index: number) {
  attachments.value.splice(index, 1)
}

function saveHandling() {
  ElMessage.success('办理信息已保存')
}

async function finishHandling() {
  const task = getCurrentBorrowTask()
  if (!task) {
    ElMessage.warning('当前流程用户下没有可处理的借阅办理任务')
    return
  }
  if (!handlingAction.comment.trim()) {
    ElMessage.warning('请输入审批意见')
    return
  }
  try {
    if (handlingAction.action === '转他人处理' || handlingAction.action === '转需求审核/审批人') {
      await delegateTask({ taskId: task.taskId, assignee: getUserIdByName(handlingAction.receiver), reason: handlingAction.comment })
    } else if (handlingAction.action === '终止') {
      await rejectTask({ taskId: task.taskId, reason: handlingAction.comment || '借阅办理终止' })
    } else {
      await completeTask({
        taskId: task.taskId,
        variables: {
          comment: handlingAction.comment,
          action: handlingAction.action,
          applicantId: getUserIdByName(borrowForm.applicant)
        }
      })
      borrowOrder.status = handlingAction.action === '借出' ? '待申请人领取/下载' : handlingAction.action === '归还' ? '待归还' : '已完成'
    }
    await loadLiveData()
    await loadProcessLogs(borrowOrder.orderNo, 'borrow')
    ElMessage.success('借阅办理已提交')
  } catch (error) {
    console.error('Failed to finish borrow handling:', error)
    ElMessage.error('借阅办理提交失败')
  }
}

async function confirmReceipt() {
  const tasks = getReceiptBorrowTasks(borrowOrder.orderNo)
  const task = tasks[0] || getCurrentBorrowTask()
  if (!task) {
    ElMessage.warning('当前流程用户下没有可处理的领取任务')
    return
  }
  try {
    await Promise.all((tasks.length ? tasks : [task]).map(currentTask => completeTask({
      taskId: currentTask.taskId,
      variables: {
        comment: '申请人已完成领取/下载',
        action: '领取完成',
        returnRequired: showApplicantReturn.value
      }
    })))
    borrowOrder.status = showApplicantReturn.value ? '待归还' : '已完成'
    await loadLiveData()
    await loadProcessLogs(borrowOrder.orderNo, 'borrow')
    ElMessage.success('申请人领取/下载已完成')
  } catch (error) {
    console.error('Failed to complete applicant receipt:', error)
    ElMessage.error(`领取/下载处理失败：${extractErrorMessage(error)}`)
  }
}

async function submitApplicantReturn() {
  const tasks = getReceiptBorrowTasks(borrowOrder.orderNo)
  const task = tasks[0] || getCurrentBorrowTask()
  if (!task) {
    ElMessage.warning('当前流程用户下没有可处理的归还任务')
    return
  }
  if (!showApplicantReturn.value) {
    ElMessage.warning('当前借阅内容无需归还')
    return
  }
  if (receiptAction.returnMethod === '邮寄' && !receiptAction.postNo.trim()) {
    ElMessage.warning('选择邮寄时请输入邮寄单号')
    return
  }
  try {
    await Promise.all((tasks.length ? tasks : [task]).map(currentTask => completeTask({
      taskId: currentTask.taskId,
      variables: {
        comment: receiptAction.comment,
        action: '归还',
        returnMethod: receiptAction.returnMethod,
        postNo: receiptAction.returnMethod === '邮寄' ? receiptAction.postNo.trim() : ''
      }
    })))
    borrowOrder.status = '已归还'
    await loadLiveData()
    await loadProcessLogs(borrowOrder.orderNo, 'borrow')
    ElMessage.success('申请人归还已提交')
  } catch (error) {
    console.error('Failed to submit applicant return:', error)
    ElMessage.error(`申请人归还失败：${extractErrorMessage(error)}`)
  }
}

function onRenewOrderSelect(rows: RenewOrder[]) {
  selectedRenewOrders.value = rows
}

function onRenewItemSelect(rows: RenewItem[]) {
  selectedRenewItemIds.value = rows.map(item => item.id)
}

function syncRenewDetailFields() {
  renewItems.value.forEach(item => {
    if (selectedRenewItemIds.value.includes(item.id)) {
      item.renewExpireTime = renewForm.returnDate
      item.renewReason = renewForm.renewReason
    }
  })
}

function resetRenewQuery() {
  Object.assign(renewQuery, { orderNo: '', applyStart: '', applyEnd: '', applyType: '', applicant: '', currentHandler: '', company: '', businessCode: '', businessModule: '', archivePeriod: '' })
}

function startRenewFromRow(row: RenewOrder) {
  selectedRenewOrders.value = [row]
  goRenewApply()
}

function buildRenewItems(orderNo: string) {
  return liveBorrowRecords.value
    .filter(record => record.borrowCode === orderNo && ['原件', '实物'].includes(normalizeBorrowKind(record.borrowType)))
    .map((record, index) => {
      const title = parseArchiveTitle(record.archiveTitle)
      return {
        id: index + 1,
        businessCode: record.archiveCode,
        documentName: title.name,
        company: title.company,
        businessModule: '借阅管理',
        archivePeriod: record.expectedReturnDate?.slice(0, 7) || '-',
        borrowType: normalizeBorrowKind(record.borrowType),
        borrowTime: formatDate(record.borrowedAt),
        currentExpireTime: record.expectedReturnDate,
        renewExpireTime: record.expectedReturnDate,
        renewReason: '原申请借阅事项尚未办结，需要继续保留借阅权限。'
      }
    })
}

async function goRenewApply() {
  if (!selectedRenewOrders.value[0]) {
    ElMessage.warning('请先选择可续借的申请单')
    return
  }
  const sourceOrder = selectedRenewOrders.value[0]
  renewForm.sourceOrderNo = sourceOrder.orderNo
  renewForm.renewOrderNo = `REN-${Date.now()}`
  renewForm.userName = borrowForm.userName
  renewForm.department = borrowForm.department
  renewForm.applicant = currentWorkflowUser.value.name
  renewForm.purpose = borrowForm.purpose
  renewForm.reason = `基于原申请单 ${sourceOrder.orderNo} 的借阅事项仍在处理中，发起续借。`
  renewItems.value = buildRenewItems(sourceOrder.orderNo)
  selectedRenewItemIds.value = renewItems.value.map(item => item.id)
  renewForm.returnDate = renewItems.value[0]?.currentExpireTime || sourceOrder.expireTime || ''
  renewForm.renewReason = `基于原申请单 ${sourceOrder.orderNo} 的借阅事项仍在处理中，需要延长借阅周期。`
  syncRenewDetailFields()
  activeRenewStage.value = 'apply'
}

function getCurrentRenewTask() {
  return myWorkflowTasks.value.find(task => task.businessKey === renewForm.renewOrderNo && renewProcessKeys.includes(processKeyByTask(task)) && task.status === 'ACTIVE')
}

async function submitRenew() {
  try {
    if (selectedRenewItems.value.length === 0) {
      ElMessage.warning('请先勾选需要续借的文档借阅信息')
      return
    }
    if (!renewForm.returnDate) {
      ElMessage.warning('请填写申请归还日期')
      return
    }
    if (!renewForm.renewReason.trim()) {
      ElMessage.warning('请填写续借原因')
      return
    }
    syncRenewDetailFields()
    await createBorrowRenewOrder({
      renewOrderNo: renewForm.renewOrderNo,
      sourceOrderNo: renewForm.sourceOrderNo,
      userName: renewForm.userName,
      userDepartment: renewForm.department,
      applicantName: renewForm.applicant,
      applyTime: nowAsLocalDateTime(),
      purpose: renewForm.purpose,
      reason: renewForm.reason,
      reasonAttachment: renewForm.attachment,
      reviewer: renewForm.reviewer,
      handler: renewForm.handler,
      ccUsers: renewForm.ccUsers,
      status: '待需求审核',
      currentHandler: renewForm.reviewer,
      details: selectedRenewItems.value.map(item => ({
        businessCode: item.businessCode,
        documentName: item.documentName,
        company: item.company,
        borrowType: item.borrowType,
        borrowTime: item.borrowTime,
        currentExpireTime: item.currentExpireTime,
        renewExpireTime: item.renewExpireTime || renewForm.returnDate,
        renewReason: item.renewReason || renewForm.renewReason
      }))
    })
    await startProcess({
      processDefinitionKey: 'borrowRenewRequest',
      businessKey: renewForm.renewOrderNo,
      businessType: 'BORROW_RENEW',
      initiatorId: selectedWorkflowUserId.value,
      initiatorName: currentWorkflowUser.value.name,
      variables: {
        sourceOrderNo: renewForm.sourceOrderNo,
        reviewerId: getUserIdByName(renewForm.reviewer),
        handlerId: getUserIdByName(renewForm.handler),
        renewItems: selectedRenewItems.value.map(item => item.businessCode).join(','),
        renewReturnDate: renewForm.returnDate,
        renewReason: renewForm.renewReason
      }
    })
    activeRenewStage.value = 'approval'
    try {
      await loadLiveData()
      await loadProcessLogs(renewForm.renewOrderNo, 'renew')
    } catch (refreshError) {
      console.error('Renew submitted but refresh failed:', refreshError)
      ElMessage.warning('续借申请已提交，页面刷新稍后重试')
    }
    ElMessage.success('续借申请已提交')
  } catch (error) {
    console.error('Failed to submit renew request:', error)
    ElMessage.error(`续借申请提交失败：${extractErrorMessage(error)}`)
  }
}

async function approveRenew(action: string) {
  const task = getCurrentRenewTask()
  if (!task) {
    ElMessage.warning('当前流程用户下没有可处理的续借任务')
    return
  }
  try {
    if (action === '同意') {
      await completeTask({ taskId: task.taskId, variables: { comment: renewApproval.comment, nextHandlerId: getUserIdByName(renewApproval.nextHandler) } })
    } else {
      await rejectTask({ taskId: task.taskId, reason: renewApproval.comment || '续借审批驳回' })
    }
    await loadLiveData()
    await loadProcessLogs(renewForm.renewOrderNo, 'renew')
    ElMessage.success(`续借审批已${action}`)
  } catch (error) {
    console.error('Failed to approve renew request:', error)
    ElMessage.error('续借审批处理失败')
  }
}

function openAiDialog() {
  aiVisible.value = true
}

function sendAi() {
  if (!aiDraft.value.trim()) return
  aiMessages.value.push({ id: Date.now(), role: 'user', content: aiDraft.value })
  aiMessages.value.push({ id: Date.now() + 1, role: 'assistant', content: '已根据你的补充条件重新排序推荐结果，优先展示匹配公司、文档类型和时间范围的文档。' })
  aiDraft.value = ''
}

function onAiSelectionChange(rows: AiRecommendation[]) {
  selectedAiResults.value = rows
}

function selectAllAiRows() {
  aiResults.value.forEach(row => aiResultTableRef.value?.toggleRowSelection(row, true))
}

function appendAiItem() {
  const rows = selectedAiResults.value.length ? selectedAiResults.value : aiResults.value
  borrowItems.value.push(...rows.map(item => itemFactory({
    company: item.company,
    documentType: item.documentType,
    description: item.documentName || item.description,
    demandType: item.demandType,
    needReturn: item.needReturn,
    expectedReturnDate: item.expectedReturnDate,
    lendingApprover: item.lendingApprover,
    lendingRemark: item.lendingRemark,
    handler: item.handler,
    handlerRemark: item.handlerRemark
  })))
  aiVisible.value = false
  selectedAiResults.value = []
  ElMessage.success(`已加入 ${rows.length} 条 AI 推荐借阅内容`)
}

const PageHead = defineComponent({ props: { title: { type: String, required: true }, orderNo: { type: String, required: true }, status: { type: String, required: true } }, template: '<div class="flow-banner"><div><div class="flow-title">{{ title }}</div><div class="flow-code">{{ orderNo }}</div></div><el-tag type="primary" effect="light">{{ status }}</el-tag></div>' })
const DecisionCard = defineComponent({
  props: { modelValue: { type: String, required: true }, comment: { type: String, required: true }, title: { type: String, required: true }, positive: { type: String, required: true }, negative: { type: String, required: true } },
  emits: ['update:modelValue', 'update:comment', 'submit'],
  template: '<el-card shadow="never" class="process-section"><template #header><div class="section-head"><span class="section-accent"></span><span>{{ title }}</span></div></template><el-form label-position="top" class="form-grid two"><el-form-item label="选项结果" class="span-all"><div class="inline-approval-actions"><el-button :type="modelValue === positive ? \'primary\' : \'default\'" @click="$emit(\'update:modelValue\', positive)">{{ positive }}</el-button><el-button :type="modelValue === negative ? \'primary\' : \'default\'" @click="$emit(\'update:modelValue\', negative)">{{ negative }}</el-button></div></el-form-item><el-form-item label="审批意见" class="span-all"><el-input :model-value="comment" type="textarea" :rows="4" placeholder="请输入审批意见" @update:model-value="$emit(\'update:comment\', $event)" /></el-form-item><el-form-item class="span-all"><el-button type="primary" @click="$emit(\'submit\')">提交</el-button></el-form-item></el-form></el-card>'
})
const WorkflowLogCard = defineComponent({
  props: { logs: { type: Array, required: true }, title: { type: String, default: '日志' } },
  setup() {
    const collapsed = ref(false)
    const formatLogOperation = (log: LogRow) => {
      if (!log.step && !log.action) return '-'
      if (!log.step) return log.action
      if (!log.action) return log.step
      return `${log.step}${log.action}`
    }
    const formatLogComment = (comment?: string) => comment || '-'
    return { collapsed, formatLogOperation, formatLogComment }
  },
  template: '<el-card shadow="never" class="process-section log-card"><template #header><div class="header-row"><div class="section-head"><span class="section-accent"></span><span>{{ title }}</span></div><el-button link type="primary" @click="collapsed = !collapsed">{{ collapsed ? \'展开\' : \'折叠\' }}</el-button></div></template><el-table v-show="!collapsed" :data="logs" border empty-text="暂无日志"><el-table-column label="操作类型" min-width="180"><template #default="{ row }">{{ formatLogOperation(row) }}</template></el-table-column><el-table-column prop="handler" label="操作人" min-width="130"><template #default="{ row }">{{ row.handler || \'-\' }}</template></el-table-column><el-table-column prop="time" label="操作时间" min-width="170"><template #default="{ row }">{{ row.time || \'-\' }}</template></el-table-column><el-table-column label="意见" min-width="260" show-overflow-tooltip><template #default="{ row }">{{ formatLogComment(row.comment) }}</template></el-table-column></el-table></el-card>'
})

onMounted(() => {
  borrowForm.applyTime = formatDateTime(new Date().toISOString())
  showBorrowNoticeIfNeeded()
  loadBorrowReferenceOptions()
  loadLiveData()
})

watch(() => handlingAction.action, action => {
  if (action === '借出') {
    handlingAction.receiver = borrowForm.applicant
  } else if (handlingAction.receiver === borrowForm.applicant) {
    handlingAction.receiver = currentHandlingOwner.value || handlers[0] || ''
  }
}, { immediate: true })

watch(() => route.path, path => {
  if (path === '/archive-management/borrow-renew') {
    activeModule.value = 'renew'
    activeRenewStage.value = 'query'
  } else if (path === '/archive-management/borrow' && activeModule.value === 'renew') {
    activeModule.value = 'borrow'
  }
  showBorrowNoticeIfNeeded()
}, { immediate: true })

watch([activeModule, activeBorrowStage, todoVisible], () => {
  showBorrowNoticeIfNeeded()
})

watch(analysisParticipantsDisplay, value => {
  if (analysisAction.action === '分单') {
    analysisAction.nextHandler = value
  }
}, { immediate: true })

watch(() => analysisAction.action, action => {
  if (action === '分单') {
    analysisAction.nextHandler = analysisParticipantsDisplay.value
  } else if (analysisAction.nextHandler === analysisParticipantsDisplay.value) {
    analysisAction.nextHandler = handlers[0] || ''
  }
}, { immediate: true })

watch(() => receiptAction.returnMethod, method => {
  if (method !== '邮寄') {
    receiptAction.postNo = ''
  }
})
</script>

<style scoped>
.borrow-page { display: grid; gap: 20px; }
.hero { display: grid; grid-template-columns: 1.1fr 1fr; gap: 18px; padding: 24px; border-radius: 24px; background: linear-gradient(135deg, #10253d 0%, #1b4372 55%, #eef5ff 55%, #f7fbff 100%); box-shadow: 0 16px 36px rgba(18, 38, 63, 0.14); }
.hero h2 { margin: 12px 0 8px; color: #fff; font-size: 30px; }
.hero p { margin: 0; color: rgba(255,255,255,.84); line-height: 1.7; }
.hero-toolbar { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 18px; }
.eyebrow { display: inline-flex; padding: 6px 12px; border-radius: 999px; background: rgba(255,255,255,.14); color: #fff; font-size: 12px; letter-spacing: .08em; text-transform: uppercase; }
.hero-cards { display: grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap: 12px; }
.metric-label { color: #6d8096; font-size: 13px; }
.metric-value { color: #173654; font-size: 30px; font-weight: 700; }
.metric-desc { color: #6b7c90; line-height: 1.6; }
.borrow-notice-dialog :deep(.el-dialog) { border-radius: 20px; overflow: hidden; }
.borrow-notice-dialog :deep(.el-dialog__header) { padding: 26px 30px 16px; border-bottom: 1px solid #e8edf4; }
.borrow-notice-dialog :deep(.el-dialog__body) { padding: 24px 30px 8px; }
.borrow-notice-dialog :deep(.el-dialog__footer) { padding: 18px 30px 28px; }
.borrow-notice-title { color: #12263f; font-size: 22px; font-weight: 850; }
.borrow-notice-body { display: grid; gap: 18px; color: #334155; }
.borrow-notice-lead { margin: 0; color: #475569; line-height: 1.8; }
.notice-section { padding: 18px 20px; border: 1px solid #e6edf6; border-radius: 16px; background: #f8fbff; }
.notice-section h3 { margin: 0 0 12px; color: #12263f; font-size: 15px; font-weight: 800; }
.notice-section ul { margin: 0; padding-left: 20px; line-height: 1.9; }
.stage-stack, .summary-stack { display: grid; gap: 16px; }
.flow-banner { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; padding: 20px 22px; border-radius: 20px; background: linear-gradient(135deg, #f8fbff 0%, #eef5ff 100%); border: 1px solid #dbe8fb; }
.flow-title { color: #4d6a87; font-size: 13px; letter-spacing: .08em; text-transform: uppercase; }
.flow-code { margin-top: 8px; color: #173654; font-size: 24px; font-weight: 700; }
.process-section { border-radius: 22px; border: 1px solid #dde6f2; box-shadow: 0 10px 26px rgba(20, 45, 76, 0.06); }
.section-head { display: inline-flex; align-items: center; gap: 10px; font-size: 18px; font-weight: 700; color: #193655; }
.section-accent { width: 6px; height: 22px; border-radius: 999px; background: linear-gradient(180deg, #1a73e8 0%, #58a6ff 100%); }
.log-card :deep(.el-card__body) { padding: 20px 24px 24px; }
.timeline-title-icon { display: inline-grid; place-items: center; width: 24px; height: 24px; color: #1a73e8; font-size: 22px; font-weight: 900; line-height: 1; }
.workflow-timeline { position: relative; display: grid; gap: 0; padding: 4px 0 4px 34px; }
.timeline-node { position: relative; display: grid; grid-template-columns: 40px minmax(0, 1fr); column-gap: 22px; min-height: 112px; }
.timeline-node::before { content: ""; position: absolute; left: 19px; top: 34px; bottom: -2px; width: 2px; background: #1a73e8; }
.timeline-node:last-child::before { display: none; }
.timeline-marker { position: relative; z-index: 1; display: grid; place-items: center; width: 34px; height: 34px; margin-top: 4px; border-radius: 50%; color: #fff; background: #1a73e8; box-shadow: 0 0 0 5px #edf5ff; font-size: 15px; font-weight: 900; }
.timeline-node.current .timeline-marker { color: #1a73e8; background: #fff; border: 4px solid #1a73e8; box-shadow: 0 0 0 5px #edf5ff; }
.timeline-node.current .timeline-marker span { width: 9px; height: 9px; border-radius: 50%; background: #1a73e8; }
.timeline-node.pending::before { background: #e7edf5; }
.timeline-node.pending .timeline-marker { color: #b5c1d1; background: #edf2f8; box-shadow: none; }
.timeline-node.pending .timeline-marker span { width: 9px; height: 9px; border-radius: 50%; background: #b5c1d1; }
.timeline-node.rejected::before { background: #ef4444; }
.timeline-node.rejected .timeline-marker { background: #ef4444; box-shadow: 0 0 0 5px #fff1f1; }
.timeline-content { padding: 0 0 24px; }
.timeline-step { color: #0f172a; font-size: 18px; font-weight: 900; line-height: 1.45; }
.timeline-node.current .timeline-step { color: #1a73e8; }
.timeline-node.pending .timeline-step { color: #0f172a; }
.timeline-node.rejected .timeline-step { color: #dc2626; }
.timeline-summary { margin-top: 8px; color: #111827; font-size: 15px; font-weight: 800; line-height: 1.55; }
.timeline-node.current .timeline-summary { color: #1a73e8; }
.timeline-node.rejected .timeline-summary { color: #b91c1c; }
.timeline-time { margin-top: 6px; color: #111827; font-size: 12px; line-height: 1.4; }
.timeline-node.pending .timeline-time { color: #7b8797; }
.form-grid { display: grid; gap: 16px; }
.form-grid :deep(.el-form-item) { margin-bottom: 0; }
.form-grid.four { grid-template-columns: repeat(4, minmax(0,1fr)); }
.form-grid.five { grid-template-columns: repeat(5, minmax(0,1fr)); }
.form-grid.three { grid-template-columns: repeat(3, minmax(0,1fr)); }
.form-grid.two { grid-template-columns: repeat(2, minmax(0,1fr)); }
.span-all { grid-column: 1 / -1; }
.header-row, .actions { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.actions { justify-content: flex-end; }
.required-label::before { content: '*'; color: #f56c6c; margin-right: 4px; }
.file-tag-list { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.muted-text { color: #8a9ab0; font-size: 13px; }
.decision-group :deep(.el-radio-button__inner) { min-width: 120px; height: 44px; font-weight: 700; }
.inline-approval-actions { display: flex; justify-content: flex-start; gap: 14px; width: 100%; }
.inline-approval-actions :deep(.el-button) { min-width: 86px; }
.top-gap { margin-top: 16px; }
.radio-wrap { display: flex; flex-wrap: wrap; gap: 8px; }
.renew-query-page { display: grid; gap: 28px; padding: 8px 2px 28px; }
.renew-page-title { display: flex; justify-content: space-between; align-items: flex-end; gap: 16px; }
.renew-page-title h1 { margin: 0; color: #050505; font-size: 42px; font-weight: 900; letter-spacing: -.04em; }
.renew-page-title p { margin: 8px 0 0; color: #667085; font-size: 16px; }
.renew-panel { border-radius: 18px; background: #fff; box-shadow: 0 4px 22px rgba(25, 28, 29, 0.045); border: 1px solid rgba(193, 198, 214, 0.18); }
.renew-filter-panel { padding: 28px; }
.renew-section-title { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
.renew-section-title span { width: 5px; height: 28px; border-radius: 999px; background: #005bbf; }
.renew-section-title h2 { margin: 0; color: #000; font-size: 22px; font-weight: 850; }
.renew-filter-grid { display: grid; grid-template-columns: repeat(5, minmax(0, 1fr)); column-gap: 28px; row-gap: 20px; }
.renew-filter-grid :deep(.el-form-item) { margin-bottom: 0; }
.renew-filter-grid :deep(.el-form-item__label), .renew-apply-grid :deep(.el-form-item__label) { color: #414754; font-size: 12px; font-weight: 800; letter-spacing: .04em; text-transform: uppercase; }
.range-fields { display: flex; align-items: center; gap: 8px; width: 100%; }
.range-fields :deep(.el-date-editor) { width: 100%; }
.renew-filter-actions { display: flex; justify-content: flex-end; gap: 14px; margin-top: 30px; }
.renew-filter-actions :deep(.el-button), .renew-result-toolbar :deep(.el-button), .renew-apply-footer :deep(.el-button) { min-width: 104px; font-weight: 750; }
.renew-result-panel { overflow: hidden; }
.renew-result-toolbar { display: flex; justify-content: space-between; align-items: center; padding: 18px 28px; border-bottom: 1px solid rgba(225, 227, 228, 0.85); }
.renew-result-toolbar > div { display: flex; align-items: center; gap: 12px; }
.renew-result-panel :deep(.el-table__header th) { background: #f3f4f5; color: #414754; font-size: 12px; font-weight: 850; letter-spacing: .06em; text-transform: uppercase; }
.renew-result-panel :deep(.el-table__row) { height: 64px; }
.renew-expire { color: #ba1a1a; font-weight: 750; }
.renew-pagination { display: flex; justify-content: space-between; align-items: center; padding: 18px 28px; background: rgba(243, 244, 245, 0.62); color: #667085; font-size: 13px; }
.renew-apply-layout { position: relative; display: grid; grid-template-columns: minmax(0, 1fr); gap: 24px; padding-bottom: 86px; }
.renew-apply-main { display: grid; gap: 28px; min-width: 0; }
.renew-apply-title { margin: 8px 0 4px; }
.renew-apply-title h1 { margin: 0 0 20px; color: #000; font-size: 48px; font-weight: 900; letter-spacing: -.045em; }
.renew-meta-row { display: flex; flex-wrap: wrap; gap: 42px; }
.renew-meta-row div { display: flex; flex-direction: column; gap: 6px; }
.renew-meta-row span { color: #667085; font-size: 12px; font-weight: 800; letter-spacing: .06em; text-transform: uppercase; }
.renew-meta-row strong { color: #000; font-size: 17px; }
.renew-top-flow { padding: 28px; border-radius: 18px; background: linear-gradient(135deg, #fff 0%, #f7fbff 100%); border: 1px solid rgba(193, 198, 214, 0.2); box-shadow: 0 4px 22px rgba(25, 28, 29, 0.045); }
.renew-editorial-card { padding: 28px; border-radius: 18px; background: #fff; border: 1px solid rgba(193, 198, 214, 0.18); box-shadow: 0 4px 22px rgba(25, 28, 29, 0.045); }
.renew-apply-grid { display: grid; grid-template-columns: 1fr; gap: 22px; }
.renew-apply-grid.three { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.renew-upload-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100px; border: 2px dashed rgba(193, 198, 214, 0.36); border-radius: 14px; background: #f3f4f5; color: #414754; }
.renew-upload-placeholder span { font-weight: 800; }
.renew-upload-placeholder small { margin-top: 4px; color: #727785; }
.renew-editorial-card :deep(.el-table__header th) { background: #edeeef; color: #414754; font-weight: 850; }
.renew-extend-form { display: grid; gap: 22px; margin-top: 28px; padding-top: 26px; border-top: 1px solid rgba(193, 198, 214, 0.32); }
.renew-process-sidebar { position: sticky; top: 16px; align-self: start; min-height: 420px; padding: 24px; border-radius: 18px; background: #fff; border: 1px solid rgba(193, 198, 214, 0.22); }
.renew-process-sidebar h3 { margin: 0 0 28px; color: #000; font-size: 20px; font-weight: 850; }
.renew-timeline { display: grid; gap: 28px; position: relative; padding-left: 28px; }
.renew-timeline::before { content: ""; position: absolute; left: 9px; top: 6px; bottom: 6px; width: 2px; background: #e7e8e9; }
.renew-timeline div { position: relative; display: grid; gap: 4px; color: #727785; }
.renew-timeline div::before { content: ""; position: absolute; left: -27px; top: 2px; width: 20px; height: 20px; border-radius: 50%; background: #e7e8e9; border: 4px solid #fff; box-shadow: 0 0 0 1px #e7e8e9; }
.renew-timeline div.done::before { background: #005bbf; }
.renew-timeline div.active::before { background: #1a73e8; box-shadow: 0 0 0 5px rgba(26, 115, 232, 0.12); }
.renew-timeline b { color: #111827; font-size: 14px; }
.renew-timeline span { color: #667085; font-size: 12px; }
.renew-apply-footer { position: sticky; bottom: 0; grid-column: 1 / -1; display: flex; justify-content: flex-end; gap: 14px; padding: 18px 24px; border: 1px solid rgba(193, 198, 214, 0.18); border-radius: 18px; background: rgba(255,255,255,.9); backdrop-filter: blur(10px); box-shadow: 0 -8px 26px rgba(15, 23, 42, 0.06); }
.ai-recommend-dialog :deep(.el-dialog) { border-radius: 22px; overflow: hidden; background: #f8f9fa; }
.ai-recommend-dialog :deep(.el-dialog__header) { display: none; }
.ai-recommend-dialog :deep(.el-dialog__body) { padding: 0; }
.ai-recommend-shell { display: grid; grid-template-columns: 320px minmax(0, 1fr); min-height: 680px; background: #f8f9fa; color: #191c1d; }
.ai-assistant-panel { display: flex; flex-direction: column; min-height: 680px; background: #f8fafc; border-right: 1px solid rgba(203, 213, 225, 0.72); }
.ai-assistant-head { display: flex; align-items: center; gap: 12px; padding: 18px; border-bottom: 1px solid rgba(203, 213, 225, 0.66); }
.ai-bot-icon { display: grid; place-items: center; width: 34px; height: 34px; border-radius: 12px; background: #1a73e8; color: #fff; font-size: 13px; font-weight: 800; letter-spacing: .04em; }
.ai-panel-title { color: #0f172a; font-size: 14px; font-weight: 800; letter-spacing: .06em; text-transform: uppercase; }
.ai-panel-subtitle { margin-top: 3px; color: #64748b; font-size: 11px; }
.ai-chat-stream { display: flex; flex: 1; flex-direction: column; gap: 18px; overflow: auto; padding: 18px; }
.ai-chat-message { display: flex; flex-direction: column; gap: 5px; max-width: 92%; }
.ai-chat-message.user { align-self: flex-end; align-items: flex-end; }
.ai-chat-bubble { padding: 13px 14px; border-radius: 16px; background: #fff; border: 1px solid #eef2f7; box-shadow: 0 6px 16px rgba(15, 23, 42, 0.06); }
.ai-chat-message.user .ai-chat-bubble { background: #1a73e8; color: #fff; border-color: #1a73e8; border-top-right-radius: 4px; }
.ai-chat-message.assistant .ai-chat-bubble { border-top-left-radius: 4px; }
.ai-chat-bubble strong { display: block; margin-bottom: 6px; color: #1a73e8; font-size: 12px; }
.ai-chat-message.user .ai-chat-bubble strong { color: #fff; }
.ai-chat-bubble p { margin: 0; font-size: 13px; line-height: 1.7; }
.ai-chat-message span { color: #94a3b8; font-size: 11px; }
.ai-chat-input { padding: 16px; background: #fff; border-top: 1px solid rgba(203, 213, 225, 0.66); }
.ai-send-button { width: 100%; margin-top: 10px; border-radius: 10px; font-weight: 700; }
.ai-recommend-main { overflow: auto; padding: 30px; }
.ai-recommend-header { display: flex; justify-content: space-between; align-items: flex-end; gap: 18px; margin-bottom: 22px; }
.ai-recommend-header h3 { margin: 0 0 10px; color: #111827; font-size: 30px; font-weight: 900; letter-spacing: -.03em; }
.ai-recommend-header p { margin: 0; color: #64748b; font-size: 14px; }
.ai-header-actions { display: flex; gap: 10px; align-items: center; }
.ai-header-actions :deep(.el-button--primary) { border-radius: 999px; padding-inline: 22px; font-weight: 800; box-shadow: 0 10px 24px rgba(26, 115, 232, 0.22); }
.ai-result-card { overflow: hidden; border: 1px solid #e2e8f0; border-radius: 18px; background: #fff; box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06); }
.ai-result-card :deep(.el-table__header th) { background: #f8fafc; color: #94a3b8; font-size: 11px; font-weight: 800; letter-spacing: .06em; text-transform: uppercase; }
.ai-result-card :deep(.el-table__row) { height: 68px; }
.ai-result-card :deep(.el-table__row:hover > td) { background: #f8fbff; }
.ai-doc-name { display: flex; align-items: center; gap: 12px; }
.ai-doc-icon { display: grid; place-items: center; width: 32px; height: 32px; border-radius: 10px; background: #e8f1ff; color: #1a73e8; font-size: 13px; font-weight: 900; transition: all .18s ease; }
.ai-doc-name strong { display: inline-flex; align-items: center; gap: 8px; color: #0f172a; font-size: 14px; }
.ai-result-footer { display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 24px; color: #64748b; font-size: 13px; font-style: italic; }
.ai-box { display: grid; gap: 16px; }
.ai-log { display: grid; gap: 10px; max-height: 220px; overflow: auto; padding: 12px; border: 1px solid #dfeaf7; border-radius: 16px; background: #f7faff; }
.msg { max-width: 88%; padding: 12px 14px; border-radius: 14px; line-height: 1.6; }
.msg.assistant { background: #fff; border: 1px solid #dfe8f3; }
.msg.user { margin-left: auto; background: #1a73e8; color: #fff; }
.msg p { margin: 6px 0 0; }
@media (max-width: 1200px) { .hero, .form-grid.five, .form-grid.four, .form-grid.three, .renew-filter-grid, .renew-apply-grid.three { grid-template-columns: repeat(2, minmax(0,1fr)); } .renew-apply-layout { grid-template-columns: 1fr; } .renew-process-sidebar { position: static; } }
@media (max-width: 768px) { .hero, .hero-cards, .form-grid.five, .form-grid.four, .form-grid.three, .form-grid.two, .ai-recommend-shell, .renew-filter-grid, .renew-apply-grid.three { grid-template-columns: 1fr; } .header-row, .actions, .flow-banner, .ai-recommend-header, .ai-header-actions, .ai-result-footer, .renew-page-title, .renew-result-toolbar, .renew-pagination, .renew-apply-footer { flex-direction: column; align-items: flex-start; } .ai-assistant-panel { min-height: 420px; } .renew-page-title h1, .renew-apply-title h1 { font-size: 34px; } .range-fields { flex-direction: column; align-items: stretch; } }
</style>

