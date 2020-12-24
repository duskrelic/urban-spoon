package com.pebblepost.business.entity;

import javax.validation.*;
import java.util.Set;

public abstract class SelfValidating<T> {

  private final Validator validator;

  public SelfValidating() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  public void validate() {

    @SuppressWarnings("unchecked")
    Set<ConstraintViolation<T>> violations = validator.validate((T) this);

    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
