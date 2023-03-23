package com.ymt.mapper;

import com.ymt.base.NormalConstant;
import com.ymt.domain.Audit;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class AuditMapperTest {
    private static AuditMapper mapper;

    @BeforeClass
    public static void setUpMybatisDatabase() {
        SqlSessionFactory builder = new SqlSessionFactoryBuilder().build(AuditMapperTest.class.getClassLoader().getResourceAsStream("mybatisTestConfiguration/AuditMapperTestConfiguration.xml"));
        //you can use builder.openSession(false) to not commit to database
        mapper = builder.getConfiguration().getMapper(AuditMapper.class, builder.openSession(true));
    }

    @Test
    public void testSelectAllByUserIdAndSubTimeAndStatus() {
        List<Audit> audits = mapper.selectAllByUserIdAndSubTimeAndStatus(1L, "2022-07-30 21:57:49", NormalConstant.REVIEWED_SUCCESS);
        List<Audit> audits1 = mapper.selectAllByUserIdAndSubTimeAndStatus(1L, "2022-07-30 21:57:49", NormalConstant.NOT_REVIEWED);
        System.out.println();
    }
}
