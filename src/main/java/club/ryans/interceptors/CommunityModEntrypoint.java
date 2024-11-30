package club.ryans.interceptors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommunityModEntrypoint {
    String token();
}
