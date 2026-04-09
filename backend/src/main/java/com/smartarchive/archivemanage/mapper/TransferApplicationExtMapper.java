package com.smartarchive.archivemanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartarchive.archivemanage.domain.TransferApplicationExt;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface TransferApplicationExtMapper extends BaseMapper<TransferApplicationExt> {
    int insertExtRow(@Param("masterId") Long masterId,
                     @Param("objectId") Long objectId,
                     @Param("tenantid") Long tenantid,
                     @Param("operatorId") Long operatorId,
                     @Param("attrValues") Map<String, Object> attrValues);

    List<Map<String, Object>> selectByMasterId(@Param("masterId") Long masterId, @Param("tenantid") Long tenantid);
}
