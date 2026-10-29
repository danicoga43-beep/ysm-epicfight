package net.okitsu.ysmepicfightcompat.fabric.shim;

import java.lang.annotation.*;

/** Penanda saja (tidak ada efek runtime di Fabric). */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface OnlyIn {
    Dist value();
}
