package club.ryans.controllers;

import club.ryans.error.ServerError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @GetMapping("/login_error")
    public String loginError() {
        return "error/login";
    }

    @RequestMapping("/error")
    public String handleError(final HttpServletRequest request) throws ServerError {
        HttpStatus status = getStatusCode(request);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String requestUrl = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();

        if (requestUrl.startsWith("/api/")) {
            if (exception instanceof ServerError) {
                throw (ServerError) exception;
            }

            if (exception instanceof Exception) {
                Exception e = (Exception) exception;
                if (e.getCause() instanceof ServerError) {
                    throw (ServerError) e.getCause();
                }
            }

            if (exception != null) {
                throw new ServerError(status, (Throwable) exception, "Unknown error");
            }

            if (status.value() == HttpStatus.NOT_FOUND.value()) {
                throw new ServerError(status, "User not authorized for '%s'.", requestUrl);
            }

            if (status.value() == HttpStatus.FORBIDDEN.value()) {
                throw new ServerError(status, "Method not found '%s'", requestUrl);
            }

            throw new ServerError(status, "Unknown error");
        }

        if (status.value() == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        }

        if (status.value() == HttpStatus.FORBIDDEN.value()) {
            return "error/403";
        }

        return "error/error";
    }

    @ExceptionHandler(ServerError.class)
    private ResponseEntity<Exception> handleServerError(final ServerError serverError) {
        return new ResponseEntity<Exception>(serverError, serverError.getStatus());
    }

    private HttpStatus getStatusCode(final HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status == null) {
            return HttpStatus.OK;
        }
        return HttpStatus.valueOf(Integer.valueOf(status.toString()));
    }
}
