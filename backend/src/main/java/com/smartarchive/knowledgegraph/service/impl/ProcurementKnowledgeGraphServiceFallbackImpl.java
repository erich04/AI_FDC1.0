package com.smartarchive.knowledgegraph.service.impl;

import com.smartarchive.knowledgegraph.dto.ContractComplianceResponse;
import com.smartarchive.knowledgegraph.dto.CreateConversationRequest;
import com.smartarchive.knowledgegraph.dto.MatchingRuleVersionResponse;
import com.smartarchive.knowledgegraph.dto.ProcurementAskRequest;
import com.smartarchive.knowledgegraph.dto.ProcurementAskResponse;
import com.smartarchive.knowledgegraph.dto.ProcurementChainResponse;
import com.smartarchive.knowledgegraph.dto.ProcurementConversationMessageResponse;
import com.smartarchive.knowledgegraph.dto.ProcurementConversationResponse;
import com.smartarchive.knowledgegraph.dto.RebuildTaskRequest;
import com.smartarchive.knowledgegraph.dto.RebuildTaskResponse;
import com.smartarchive.knowledgegraph.dto.SendConversationMessageRequest;
import com.smartarchive.knowledgegraph.dto.SupplierAccountTimelineResponse;
import com.smartarchive.knowledgegraph.dto.UpdateMatchingRuleCommand;
import com.smartarchive.knowledgegraph.service.ProcurementKnowledgeGraphService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "knowledge-graph.neo4j", name = "enabled", havingValue = "false", matchIfMissing = true)
public class ProcurementKnowledgeGraphServiceFallbackImpl implements ProcurementKnowledgeGraphService {
    private static final String FALLBACK_HINT = "知识图谱功能未启用，当前返回降级数据。";
    private static final List<String> FALLBACK_FOLLOW_UPS = List.of(
        "请联系管理员启用 knowledge-graph.neo4j.enabled",
        "可先使用档案检索与普通问答功能",
        "如需图谱分析，请补充 Neo4j 连接配置"
    );

    @Override
    public List<MatchingRuleVersionResponse> listRules() {
        return Collections.emptyList();
    }

    @Override
    public MatchingRuleVersionResponse updateRule(String scenarioCode, UpdateMatchingRuleCommand command) {
        return MatchingRuleVersionResponse.builder()
            .versionId(0L)
            .scenarioCode(scenarioCode)
            .ruleName("fallback-rule")
            .primaryKeys(Collections.emptyList())
            .auxiliaryKeys(Collections.emptyList())
            .matchMode("EXACT")
            .conflictStrategy("MARK_BROKEN")
            .ruleVersion(0)
            .currentFlag("Y")
            .enabledFlag(command.getEnabledFlag())
            .lastUpdateDate(LocalDateTime.now())
            .build();
    }

    @Override
    public RebuildTaskResponse rebuild(RebuildTaskRequest request) {
        return RebuildTaskResponse.builder()
            .taskCode("FALLBACK-TASK")
            .targetScope(request.getTargetScope())
            .targetValue(request.getTargetValue())
            .taskStatus("SKIPPED")
            .summary(FALLBACK_HINT)
            .lastUpdateDate(LocalDateTime.now())
            .build();
    }

    @Override
    public ProcurementChainResponse getChainByArchiveId(Long archiveId) {
        return emptyChain("ARCHIVE:" + archiveId);
    }

    @Override
    public ProcurementChainResponse getContractChain(String contractNo) {
        return emptyChain("CONTRACT:" + contractNo);
    }

    @Override
    public SupplierAccountTimelineResponse getSupplierAccounts(String supplierName) {
        return SupplierAccountTimelineResponse.builder()
            .supplierName(supplierName)
            .changedInLastThreeYears(false)
            .accountNumbers(Collections.emptyList())
            .timeline(Collections.emptyList())
            .evidenceItems(Collections.emptyList())
            .build();
    }

    @Override
    public ContractComplianceResponse getContractCompliance(String contractNo) {
        return ContractComplianceResponse.builder()
            .contractNo(contractNo)
            .overallStatus("UNKNOWN")
            .summary(FALLBACK_HINT)
            .checks(Collections.emptyList())
            .anomalies(List.of(FALLBACK_HINT))
            .references(Collections.emptyList())
            .build();
    }

    @Override
    public ProcurementAskResponse ask(ProcurementAskRequest request) {
        return ProcurementAskResponse.builder()
            .conversationId(0L)
            .answer("知识图谱未启用，暂无法提供图谱增强答案。")
            .contextSummary(FALLBACK_HINT)
            .chain(emptyChain("ASK"))
            .suggestedFollowUps(FALLBACK_FOLLOW_UPS)
            .build();
    }

    @Override
    public ProcurementConversationResponse createConversation(CreateConversationRequest request) {
        return ProcurementConversationResponse.builder()
            .conversationId(0L)
            .title(request.getTitle())
            .anchorType(request.getAnchorType())
            .anchorKey(request.getAnchorKey())
            .scopeMode(request.getScopeMode())
            .contextSummary(FALLBACK_HINT)
            .lastQuestion("")
            .lastAnswer(FALLBACK_HINT)
            .lastUpdateDate(LocalDateTime.now())
            .build();
    }

    @Override
    public List<ProcurementConversationResponse> listConversations() {
        return Collections.emptyList();
    }

    @Override
    public List<ProcurementConversationMessageResponse> listConversationMessages(Long conversationId) {
        return Collections.emptyList();
    }

    @Override
    public ProcurementConversationMessageResponse sendConversationMessage(Long conversationId, SendConversationMessageRequest request) {
        return ProcurementConversationMessageResponse.builder()
            .messageId(0L)
            .conversationId(conversationId)
            .question(request.getQuestion())
            .answer(FALLBACK_HINT)
            .contextSummary(FALLBACK_HINT)
            .chain(emptyChain("CONVERSATION"))
            .suggestedFollowUps(FALLBACK_FOLLOW_UPS)
            .creationDate(LocalDateTime.now())
            .build();
    }

    private ProcurementChainResponse emptyChain(String anchorNodeKey) {
        return ProcurementChainResponse.builder()
            .anchorNodeKey(anchorNodeKey)
            .contextSummary(FALLBACK_HINT)
            .nodes(Collections.emptyList())
            .edges(Collections.emptyList())
            .evidenceItems(Collections.emptyList())
            .timeline(Collections.emptyList())
            .anomalies(List.of(FALLBACK_HINT))
            .complianceChecks(Collections.emptyList())
            .references(Collections.emptyList())
            .build();
    }
}

