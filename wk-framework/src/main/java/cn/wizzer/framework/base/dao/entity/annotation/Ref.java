package cn.wizzer.framework.base.dao.entity.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ref {
    Class value() default Object.class;
}

