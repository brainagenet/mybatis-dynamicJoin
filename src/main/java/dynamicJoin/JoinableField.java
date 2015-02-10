package dynamicJoin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
/**
 * Marks this field as a candidate for dynamic join.  Fields without this annotation
 * will be ignored while generating the dynamic join
 * @author ggefaell
 *
 */
@Documented
public @interface JoinableField {
	
	String ownColumn();
    String foreignColumn();
}
