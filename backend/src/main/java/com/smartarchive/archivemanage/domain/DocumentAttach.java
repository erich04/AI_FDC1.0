package com.smartarchive.archivemanage.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fdc_document_attach_t")
@EntityListeners(AuditingEntityListener.class)
public class DocumentAttach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_attach_id")
    private Long documentAttachId;

    @Column(name = "tenantid", nullable = false)
    private Long tenantid;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "attach_category", length = 30)
    private String attachCategory;

    @Column(name = "att_type", length = 30)
    private String attType;

    // 50个扩展字段
    @Column(name = "attribute1", length = 500)
    private String attribute1;
    @Column(name = "attribute2", length = 500)
    private String attribute2;
    @Column(name = "attribute3", length = 500)
    private String attribute3;
    @Column(name = "attribute4", length = 500)
    private String attribute4;
    @Column(name = "attribute5", length = 500)
    private String attribute5;
    @Column(name = "attribute6", length = 500)
    private String attribute6;
    @Column(name = "attribute7", length = 500)
    private String attribute7;
    @Column(name = "attribute8", length = 500)
    private String attribute8;
    @Column(name = "attribute9", length = 500)
    private String attribute9;
    @Column(name = "attribute10", length = 500)
    private String attribute10;
    @Column(name = "attribute11", length = 500)
    private String attribute11;
    @Column(name = "attribute12", length = 500)
    private String attribute12;
    @Column(name = "attribute13", length = 500)
    private String attribute13;
    @Column(name = "attribute14", length = 500)
    private String attribute14;
    @Column(name = "attribute15", length = 500)
    private String attribute15;
    @Column(name = "attribute16", length = 500)
    private String attribute16;
    @Column(name = "attribute17", length = 500)
    private String attribute17;
    @Column(name = "attribute18", length = 500)
    private String attribute18;
    @Column(name = "attribute19", length = 500)
    private String attribute19;
    @Column(name = "attribute20", length = 500)
    private String attribute20;
    @Column(name = "attribute21", length = 500)
    private String attribute21;
    @Column(name = "attribute22", length = 500)
    private String attribute22;
    @Column(name = "attribute23", length = 500)
    private String attribute23;
    @Column(name = "attribute24", length = 500)
    private String attribute24;
    @Column(name = "attribute25", length = 500)
    private String attribute25;
    @Column(name = "attribute26", length = 500)
    private String attribute26;
    @Column(name = "attribute27", length = 500)
    private String attribute27;
    @Column(name = "attribute28", length = 500)
    private String attribute28;
    @Column(name = "attribute29", length = 500)
    private String attribute29;
    @Column(name = "attribute30", length = 500)
    private String attribute30;
    @Column(name = "attribute31", length = 500)
    private String attribute31;
    @Column(name = "attribute32", length = 500)
    private String attribute32;
    @Column(name = "attribute33", length = 500)
    private String attribute33;
    @Column(name = "attribute34", length = 500)
    private String attribute34;
    @Column(name = "attribute35", length = 500)
    private String attribute35;
    @Column(name = "attribute36", length = 500)
    private String attribute36;
    @Column(name = "attribute37", length = 500)
    private String attribute37;
    @Column(name = "attribute38", length = 500)
    private String attribute38;
    @Column(name = "attribute39", length = 500)
    private String attribute39;
    @Column(name = "attribute40", length = 500)
    private String attribute40;
    @Column(name = "attribute41", length = 500)
    private String attribute41;
    @Column(name = "attribute42", length = 500)
    private String attribute42;
    @Column(name = "attribute43", length = 500)
    private String attribute43;
    @Column(name = "attribute44", length = 500)
    private String attribute44;
    @Column(name = "attribute45", length = 500)
    private String attribute45;
    @Column(name = "attribute46", length = 500)
    private String attribute46;
    @Column(name = "attribute47", length = 500)
    private String attribute47;
    @Column(name = "attribute48", length = 500)
    private String attribute48;
    @Column(name = "attribute49", length = 500)
    private String attribute49;
    @Column(name = "attribute50", length = 500)
    private String attribute50;

    @Column(name = "enable_flag", length = 1, nullable = false)
    private String enableFlag = "Y";

    @Column(name = "delete_flag", length = 1, nullable = false)
    private String deleteFlag = "N";

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @LastModifiedBy
    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;

    @LastModifiedDate
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Version
    @Column(name = "last_update_version", nullable = false)
    private Integer lastUpdateVersion;
}
