package cloud.test.web.Controllers.Validators;

import cloud.test.web.Constants;
import com.google.api.pathtemplate.ValidationException;
import org.springframework.util.StringUtils;

public class DataValidator {
    public static void isNotEmptyValidate(Object value, String propertyName) {
        if (value == null || StringUtils.isEmpty(value)) {
            throw new ValidationException(String.format(Constants.EMPTY_PROPERTY_EXCEPTION_MESSAGE, propertyName));
        }
    }

    public static boolean isNotEmptyCheck(Object value) {
        if (value == null || StringUtils.isEmpty(value)) {
            return false;
        } else {
            return true;
        }
    }
}
