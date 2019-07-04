package ua.stamanker.api.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/")
@Slf4j
public class ReController {

    @GetMapping("/something/{act}")
    public Object getCredAmmount(@PathVariable("act") String act) {
        log.info("act = {}", act);
        Map<String, String> result = new HashMap<>();
        result.put("user", SecurityContextHolder.getContext().getAuthentication().toString());
        return result;
    }

}
