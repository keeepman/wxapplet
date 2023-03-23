package com.ymt.mapper;

import com.ymt.domain.Audit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuditMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Audit record);

    int insertSelective(Audit record);

    Audit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Audit record);

    int updateByPrimaryKey(Audit record);

    List<Audit> findAllByStatusAndUserId(@Param("status") Integer status, @Param("userId") Long userId);

    List<Audit> selectAllByUserIdAndSubTime(@Param("userId") Long userId, @Param("subTime") String subTime);

    List<Audit> selectAllByUserIdAndSubTimeAndStatus(@Param("userId") Long userId, @Param("subTime") String subTime, @Param("status") Integer status);


}