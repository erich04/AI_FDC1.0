package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveCreateOptionsResponse {
    private List<LabelValueOption> companyProjects;
    private List<LabelValueOption> busiModules;
    private List<LabelValueOption> archiveDestinations;
    private List<LabelValueOption> documentOrganizations;
    private List<LabelValueOption> securityLevels;
    private List<LabelValueOption> carrierTypes;
    private List<LabelValueOption> attachmentTypes;
    private List<LabelValueOption> archiveTypes;
    private List<LabelValueOption> aiModels;
    private List<LabelValueOption> geoCountries;
    private List<LabelValueOption> geoRepOffices;
    private List<LabelValueOption> geoRegions;
    private List<LabelValueOption> custodyStatuses;

    // Backward-compatible alias for historical API naming.
    public List<LabelValueOption> getDocumentTypes() {
        return busiModules;
    }

    // Backward-compatible alias for historical API naming.
    public void setDocumentTypes(List<LabelValueOption> documentTypes) {
        this.busiModules = documentTypes;
    }
}
