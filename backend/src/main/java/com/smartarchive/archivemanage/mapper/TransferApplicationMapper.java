package com.smartarchive.archivemanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartarchive.archivemanage.domain.TransferApplication;
import com.smartarchive.archivemanage.dto.TransferApplicationRecordQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TransferApplicationMapper extends BaseMapper<TransferApplication> {

    long countTransferApplicationRecords(@Param("tenantid") Long tenantid, @Param("f") TransferApplicationRecordQuery f);

    List<TransferApplication> selectTransferApplicationRecordPage(@Param("tenantid") Long tenantid,
                                                                @Param("f") TransferApplicationRecordQuery f,
                                                                @Param("offset") long offset,
                                                                @Param("limit") int limit);
}
