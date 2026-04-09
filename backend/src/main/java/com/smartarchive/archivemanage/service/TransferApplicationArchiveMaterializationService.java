package com.smartarchive.archivemanage.service;

/**
 * 移交申请审批通过后，将申请明细与扩展字段写入档案主表与扩展表，供档案查询使用。
 */
public interface TransferApplicationArchiveMaterializationService {

    void materializeAfterApproval(Long applicationId);

    void markRejected(Long applicationId);
}
