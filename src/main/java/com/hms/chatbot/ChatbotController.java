package com.hms.chatbot;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> body, Authentication auth) {
        String message = body.getOrDefault("message", "").toLowerCase();
        String user = auth != null ? auth.getName() : "guest";
        String reply = generateReply(message, user);
        Map<String, String> resp = new HashMap<>();
        resp.put("reply", reply);
        return resp;
    }

    private String generateReply(String message, String user) {
        if (message.contains("appointment")) {
            return "You can manage appointments at /appointments. To create one, click 'Create Appointment' and choose a patient, doctor and time.";
        }
        if (message.contains("patient")) {
            return "To manage patients, go to /patients. You can add, edit, and delete patient records.";
        }
        if (message.contains("login") || message.contains("sign in")) {
            return "Use your credentials on /login. Demo accounts: admin/admin123, dr.jane/doctor123, reception/recep123.";
        }
        if (message.contains("help") || message.isBlank()) {
            return "Hi " + user + ", I can help with quick guidance. Try: 'how to create appointment', 'patient management', or 'dashboard'.";
        }
        return "I didn't catch that. Try asking about 'appointments', 'patients', or 'login'.";
    }
}
