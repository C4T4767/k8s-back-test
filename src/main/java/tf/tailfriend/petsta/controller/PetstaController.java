package tf.tailfriend.petsta.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/petsta")
public class PetstaController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
