package com.msc.dao.daoproject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Force a null un attribut. Utile quand on veux faire un getPassword() ca
 * retournera toujours null.
 *
 * @author micky
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForceNull {

}
