package tf.tailfriend.petsta.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tf.tailfriend.admin.dto.AdminLoginRequest;
import tf.tailfriend.admin.dto.AdminLoginResponse;

@RestController
@RequestMapping("/petsta")
public class PetstaController {

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }
}
