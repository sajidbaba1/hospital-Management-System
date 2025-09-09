package com.hms.analytics;

import com.hms.appointment.AppointmentRepository;
import com.hms.patient.PatientRepository;
import com.hms.user.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final UserRepository users;

    public AnalyticsController(PatientRepository patients, AppointmentRepository appointments, UserRepository users) {
        this.patients = patients;
        this.appointments = appointments;
        this.users = users;
    }

    @GetMapping
    public String dashboard(Model model) {
        // Basic metrics
        model.addAttribute("totalPatients", patients.count());
        model.addAttribute("totalAppointments", appointments.count());
        model.addAttribute("totalUsers", users.count());
        
        // Today's appointments
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        long todayAppointments = appointments.findByScheduledAtBetweenOrderByScheduledAtAsc(startOfDay, endOfDay).size();
        model.addAttribute("todayAppointments", todayAppointments);
        
        return "analytics/dashboard";
    }
}
