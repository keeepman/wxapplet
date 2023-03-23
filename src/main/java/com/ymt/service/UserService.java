package com.ymt.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import com.ymt.mapper.UserMapper;
import com.ymt.domain.User;

import java.io.Serializable;

@Service
@CacheConfig(cacheNames = {"HelloWorldCache"})
public class UserService implements Serializable {

    @Resource
    private UserMapper userMapper;

    @CacheEvict(key = "#id")
    public int deleteByPrimaryKey(Long id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @CachePut(key = "#record.id")
    @CacheEvict(key = "#record.id")
    public int insert(User record) {
        return userMapper.insert(record);
    }

    @CachePut(key = "#record.id")
    @CacheEvict(key = "#record.id")
    public int insertSelective(User record) {
        return userMapper.insertSelective(record);
    }

    @Cacheable(key = "#id")
    public User selectByPrimaryKey(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @CachePut(key = "#record.id")
    @CacheEvict(key = "#record.id")
    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    @CachePut(key = "#record.id")
    @CacheEvict(key = "#record.id")
    public int updateByPrimaryKey(User record) {
        return userMapper.updateByPrimaryKey(record);
    }

}
