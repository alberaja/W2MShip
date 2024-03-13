package com.example.w2mExample.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class GeneralInterceptorAspect {

	// Pattern: paquete.Controlador.getById(..)
	@Pointcut("execution(* com.example.w2mExample.controller.*.*(..)) && args(id)")
	private void controllerNegativeId(Integer id) {
	}

	@Before("controllerNegativeId(id)")
	public void beforeMethod(Integer id) {
		if (id < 0) {
			log.error("Id of Ship is < 0!!, value: {}", id);
		}
	}

}