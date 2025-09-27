package uz.tengebank.notificationcontracts.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface NotImplementedYet {
  String message() default "This value is not implemented yet";
}
