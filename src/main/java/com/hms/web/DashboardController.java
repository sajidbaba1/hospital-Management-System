package com.hms.web;

import com.hms.appointment.AppointmentRepository;
import com.hms.patient.PatientRepository;
import com.hms.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final UserRepository users;

    public DashboardController(PatientRepository patients, AppointmentRepository appointments, UserRepository users) {
        this.patients = patients;
        this.appointments = appointments;
        this.users = users;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        boolean isDoctor = hasRole(auth, "ROLE_DOCTOR");
        boolean isReception = hasRole(auth, "ROLE_RECEPTIONIST");
        model.addAttribute("username", auth.getName());
        model.addAttribute("patientCount", patients.count());
        model.addAttribute("appointmentCount", appointments.count());
        model.addAttribute("userCount", users.count());
        if (isAdmin) return "dashboard/admin";
        if (isDoctor) return "dashboard/doctor";
        if (isReception) return "dashboard/receptionist";
        return "access-denied";
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (ga.getAuthority().equals(role)) return true;
        }
        return false;
    }
}
