package com.bhavyachahal.aiqa.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class DemoUserController {

    private final Map<Integer, Map<String, Object>> users =
            new LinkedHashMap<>();

    public DemoUserController() {

        Map<String, Object> user =
                new LinkedHashMap<>();

        user.put("id", 1);
        user.put("name", "Existing User");
        user.put("email", "existing@example.com");
        user.put("age", 30);

        users.put(1, user);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUsers() {

        return ResponseEntity.ok(
                List.copyOf(users.values())
        );
    }

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody Map<String, Object> request) {

        Object name =
                request.get("name");

        Object email =
                request.get("email");

        Object age =
                request.get("age");

        if (!(name instanceof String)
                || ((String) name).isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("name is required");
        }

        if (!(email instanceof String)
                || ((String) email).isBlank()
                || !((String) email).contains("@")) {

            return ResponseEntity
                    .badRequest()
                    .body("valid email is required");
        }

        if (age != null
                && !(age instanceof Number)) {

            return ResponseEntity
                    .badRequest()
                    .body("age must be an integer");
        }

        int id =
                users.size() + 1;

        Map<String, Object> user =
                new LinkedHashMap<>();

        user.put("id", id);
        user.put("name", name);
        user.put("email", email);

        if (age != null) {
            user.put("age", age);
        }

        users.put(
                id,
                user
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(
            @PathVariable Integer id) {

        Map<String, Object> user =
                users.get(id);

        if (user == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                user
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Integer id) {

        if (!users.containsKey(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        users.remove(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}