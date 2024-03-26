package es.unex.spilab.contigo.validator;

import es.unex.spilab.contigo.model.UserAccess;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserAccessValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return UserAccess.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reason", "NotEmpty");

	}
}
