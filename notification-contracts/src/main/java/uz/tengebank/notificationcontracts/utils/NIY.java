package uz.tengebank.notificationcontracts.utils;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface NIY {
  String message() default "This value is not implemented yet";
}
