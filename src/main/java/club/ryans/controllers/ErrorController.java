package club.ryans.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @RequestMapping("/error")
    public String handleError(final HttpServletRequest request) {
        HttpStatus status = getStatusCode(request);

        if (status.value() == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        }

        return "error/error";
    }

    private HttpStatus getStatusCode(final HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status == null) {
            return HttpStatus.OK;
        }
        return HttpStatus.valueOf(Integer.valueOf(status.toString()));
    }
}
