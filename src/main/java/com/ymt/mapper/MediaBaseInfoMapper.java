package com.ymt.mapper;

import com.ymt.domain.MediaBaseInfo;
import org.apache.ibatis.annotations.Mapper;import org.apache.ibatis.annotations.Param;import java.util.List;

@Mapper
public interface MediaBaseInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(MediaBaseInfo record);

    int insertSelective(MediaBaseInfo record);

    MediaBaseInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MediaBaseInfo record);

    int updateByPrimaryKey(MediaBaseInfo record);

    Integer selectTypeIdById(@Param("id") String id);

    List<MediaBaseInfo> findAllByMediaTypeAndUserId(@Param("mediaType") Integer mediaType, @Param("userId") Long userId, @Param("bindUserId") Long bindUserId);
}