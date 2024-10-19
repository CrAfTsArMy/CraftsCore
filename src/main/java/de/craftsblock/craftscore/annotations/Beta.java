package de.craftsblock.craftscore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the annotated element is in a beta stage and subject to potential changes.
 * It is intended for elements that are not yet stable and may have limited or experimental functionality.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0
 * @since 3.6#16-SNAPSHOT
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE
})
public @interface Beta {}

