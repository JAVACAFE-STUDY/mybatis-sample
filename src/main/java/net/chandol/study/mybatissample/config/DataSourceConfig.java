package net.chandol.study.mybatissample.config;

import net.chandol.study.mybatissample.MybatisSampleApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackageClasses = MybatisSampleApplication.class)
public class DataSourceConfig {

}