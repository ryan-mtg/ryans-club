package club.ryans.views;

import club.ryans.utility.Time;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewAdvice {
    private static final Utility UTILITY = new Utility();
    private static final Time TIME = new Time();

    @ModelAttribute
    public void addUtility(final Model model) {
        model.addAttribute("utility", UTILITY);
        model.addAttribute("Time", TIME);
    }
}
