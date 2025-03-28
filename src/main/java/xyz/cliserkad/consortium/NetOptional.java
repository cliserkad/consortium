package xyz.cliserkad.consortium;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark methods that are optional for networked communication.
 * Method will return null if invocation fails.
 * NetworkedControllers will not halt/fail if these methods are called while disconnected.
 * NetworkedResponders will not respond with exceptions.
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@Target({ java.lang.annotation.ElementType.METHOD })
public @interface NetOptional {

}
