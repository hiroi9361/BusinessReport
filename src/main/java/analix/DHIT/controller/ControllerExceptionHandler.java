package analix.DHIT.controller;
import analix.DHIT.exception.ReportNotFoundException;
import analix.DHIT.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({UserNotFoundException.class})
    public String handleUserNotException(UserNotFoundException ex)
    {
        return "common/not-found";
    }

    @ExceptionHandler({ReportNotFoundException.class})
    public String handleReportNotException(ReportNotFoundException ex)
    {
        return "common/not-found";
    }

}
