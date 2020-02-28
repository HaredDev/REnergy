package net.hared.energy.util;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to identify RE module
 * @author HARED
 *
 */
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface RE {

	 /**
	  * @return The type of module
	  */
	 REType type();
	
	 /**
	  * Used to identify consumer method of module(Only on type Consumer and Storage)
	  * @author HARED
	  *
	  */
	 @Retention(RUNTIME)
	 @Target(ElementType.METHOD)
	 @interface Consumer{
		 
		 /**
		  * @return Max Transfer of consumer(default Float.MAX_VALUE)
		  */
		 float maxTransfer() default Float.MAX_VALUE;
		 
	 }
	 
	 /**
	  * Used to identify producer method of module(Only on type Producer and Storage)
	  * @author HARED
	  *
	  */
	 @Retention(RUNTIME)
	 @Target(ElementType.METHOD)
	 @interface Producer{
		 
		 /**
		  * @return Max Transfer of producer(default Float.MAX_VALUE)
		  */
		 float maxTransfer() default Float.MAX_VALUE;;
		 
	 }
	 
	 /**
	  * Used to store data. E.x. energy stored...
	  * Must be public!
	  * @author HARED
	  *
	  */
	 @Retention(RUNTIME)
	 @Target({ElementType.FIELD, ElementType.METHOD})
	 @interface Data{}
	
}
