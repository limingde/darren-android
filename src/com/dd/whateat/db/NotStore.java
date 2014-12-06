package com.dd.whateat.db;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * declare a field no need to store in the database
 * @author Administrator
 *
 */
//声明注解用在成员变量上
@Target(ElementType.FIELD)
// 声明注解运行级别在JVM
@Retention(RetentionPolicy.RUNTIME)
//将此注解包含在 javadoc中
@Documented 
//允许子类继承父类中的注解
@Inherited
public @interface NotStore {
}
