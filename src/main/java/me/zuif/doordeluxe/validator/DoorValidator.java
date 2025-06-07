package me.zuif.doordeluxe.validator;

import me.zuif.doordeluxe.model.door.Door;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class DoorValidator implements Validator {

    private static final int MIN_WIDTH = 300;
    private static final int MIN_HEIGHT = 300;
    private static final int MAX_WIDTH = 3840;
    private static final int MAX_HEIGHT = 2160;

    @Override
    public boolean supports(Class<?> aClass) {
        return Door.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Door door = (Door) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "count", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "manufacturer", "error.not_empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "imageUrl", "error.not_empty");

        if (door.getName() != null) {
            if (door.getName().length() < 5) {
                errors.rejectValue("name", "doorForm.error.name.less_2");
            }
            if (door.getName().length() > 50) {
                errors.rejectValue("name", "doorForm.error.name.over_64");
            }
        }

        if (door.getDescription() != null) {
            if (door.getDescription().length() < 50) {
                errors.rejectValue("description", "doorForm.error.description.less_10");
            }
            if (door.getDescription().length() > 2000) {
                errors.rejectValue("description", "doorForm.error.description.over_1000");
            }
        }

        if (door.getImageUrl() != null) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(door.getImageUrl()).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");

                try (InputStream inputStream = connection.getInputStream()) {
                    BufferedImage image = ImageIO.read(inputStream);
                    if (image == null) {
                        errors.rejectValue("imageUrl", "doorForm.error.image.invalid");
                    } else {
                        int width = image.getWidth();
                        int height = image.getHeight();

                        if (width < MIN_WIDTH || height < MIN_HEIGHT) {
                            errors.rejectValue("imageUrl", "doorForm.error.image.too_small");
                        }
                        if (width > MAX_WIDTH || height > MAX_HEIGHT) {
                            errors.rejectValue("imageUrl", "doorForm.error.image.too_large");
                        }
                    }
                } catch (IOException e) {
                    errors.rejectValue("imageUrl", "doorForm.error.image.load_fail");
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (door.getPrice() != null) {
                if (door.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                    errors.rejectValue("price", "doorForm.error.price_less_0");
                } else if (door.getPrice().compareTo(BigDecimal.valueOf(100_000_000)) > 0) {
                    errors.rejectValue("price", "doorForm.error.price_more_100m");
                }
            }

            if (door.getCount() < 0) {
                errors.rejectValue("count", "doorForm.error.count_less_0");
            }

            if (door.getDoorType() == null) {
                errors.rejectValue("doorType", "error.not_null");
            }
        }
    }
}