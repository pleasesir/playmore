package org.playmore.api.annotation;

import org.playmore.api.registrar.GameComponentScanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GameComponentScanRegistrar.class)
public @interface GameComponentScan {
}
