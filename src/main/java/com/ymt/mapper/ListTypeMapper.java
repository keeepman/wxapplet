package com.ymt.mapper;

import com.ymt.domain.ListType;
import org.apache.ibatis.annotations.Mapper;import java.util.List;

@Mapper
public interface ListTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ListType record);

    int insertSelective(ListType record);

    ListType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ListType record);

    int updateByPrimaryKey(ListType record);

    List<ListType> findAll();
}