package com.dd.datastatistics.orm;

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
//澹版槑娉ㄨВ鐢ㄥ湪鎴愬憳鍙橀噺涓�
@Target(ElementType.FIELD)
// 澹版槑娉ㄨВ杩愯绾у埆鍦↗VM
@Retention(RetentionPolicy.RUNTIME)
//灏嗘娉ㄨВ鍖呭惈鍦�javadoc涓�
@Documented 
//鍏佽瀛愮被缁ф壙鐖剁被涓殑娉ㄨВ
@Inherited
public @interface NotStore {
}
