package com.yj.eleccharge.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 因为为了方便将所有的xls的字段都写成了String类型存储，
 * 所以这里加一个Annotation在Field上面，为以后识别做准备
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TypeAnnotation {
    /**
     * 所属类型
     * @return
     */
    TypeElement type();

    /**
     * 写在xls文件中的标题名
     * @return
     */
    String name() default "";
}
