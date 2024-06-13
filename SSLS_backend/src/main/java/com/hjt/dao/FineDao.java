package com.hjt.dao;

import com.hjt.domain.Fine;
import com.hjt.dto.Result;
import com.hjt.pojo.FineInfo;
import com.hjt.pojo.NameValue;

import java.util.List;

public interface FineDao {
    int queryFineCountByUserId(int userId);

    Fine queryFineByBorrowId(int borrowId);

    List<FineInfo> queryFineByUserId(int userId);

    Result payFineByFineId(int fineId);
    
    // 前十二个月，每个月新增的罚款总额
    List<NameValue> queryFineTotalByMonth();
}
