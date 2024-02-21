package com.ib.babhregs.rest.common;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Този интерфейс се използва за класифициране на РЕСТ услуги. Т.е. кой е общодостъпен и кой иска някакви креденшъли
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD})
public @interface Secured { }
