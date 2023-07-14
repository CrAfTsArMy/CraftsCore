package de.craftsblock.craftscore.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 *
 * @author CraftsBlock
 * @since  3.6.16-SNAPSHOT
 * @version 1.0
 *
 * @see Event
 * @see Cancelable
 * @see EventPriority
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    EventPriority priority() default EventPriority.NORMAL;

}
