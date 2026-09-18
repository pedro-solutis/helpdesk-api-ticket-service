package br.com.solutis.helpdesk.api_ticket_service.validation.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence>{

    private List<String> acceptedValues;

    @Override 
    public void initialize(ValueOfEnum annotations){
        acceptedValues = Stream.of(annotations.enumClass().getEnumConstants())
            .map(e -> e.name())
            .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }
        return acceptedValues.contains(value.toString().toUpperCase());
    }

}
