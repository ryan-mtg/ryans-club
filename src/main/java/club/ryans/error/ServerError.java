package club.ryans.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ServerError extends Exception {
    @Getter
    private HttpStatus status;

    public ServerError(final HttpStatus status, final Throwable cause, final String format, final Object... args) {
        super(String.format(format, args), cause);
        this.status = status;
    }

    public ServerError(final HttpStatus status, final String format, final Object... args) {
        super(String.format(format, args));
        this.status = status;
    }
}
