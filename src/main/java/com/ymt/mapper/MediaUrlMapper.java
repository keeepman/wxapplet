package com.ymt.mapper;

import com.ymt.domain.MediaUrl;
import org.apache.ibatis.annotations.Mapper;import org.apache.ibatis.annotations.Param;import java.util.List;

@Mapper
public interface MediaUrlMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MediaUrl record);

    int insertSelective(MediaUrl record);

    MediaUrl selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MediaUrl record);

    int updateByPrimaryKey(MediaUrl record);

    List<MediaUrl> findAllByMediaBaseInfoId(@Param("mediaBaseInfoId") String mediaBaseInfoId);
}