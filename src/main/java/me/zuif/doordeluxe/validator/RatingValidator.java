package me.zuif.doordeluxe.validator;

import me.zuif.doordeluxe.model.Rating;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class RatingValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Rating.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Rating rating = (Rating) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rate", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "error.not_empty");
        if (rating.getComment().length() <= 0) {
            errors.rejectValue("name", "ratingForm.error.comment.less_1");
        }

        if (rating.getComment().length() > 256) {
            errors.rejectValue("name", "ratingForm.error.comment.over_256");
        }
        if (rating.getTitle().length() <= 0) {
            errors.rejectValue("title", "ratingForm.error.title.less_1");
        }

        if (rating.getTitle().length() > 32) {
            errors.rejectValue("title", "ratingForm.error.title.over_32");
        }
        if (rating.getRate() <= 0) {
            errors.rejectValue("rate", "ratingForm.error.rate.less_1");
        }
        if (rating.getRate() >= 6) {
            errors.rejectValue("rate", "ratingForm.error.rate.over_5");
        }
    }
}
