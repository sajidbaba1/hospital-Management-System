package com.hms.doctor;

import com.hms.appointment.Appointment;
import com.hms.appointment.AppointmentRepository;
import com.hms.appointment.AppointmentStatus;
import com.hms.notification.Notification;
import com.hms.notification.NotificationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private final AppointmentRepository appointments;
    private final NotificationRepository notifications;

    public DoctorController(AppointmentRepository appointments, NotificationRepository notifications) {
        this.appointments = appointments;
        this.notifications = notifications;
    }

    @GetMapping
    public String dashboard(Authentication auth, Model model) {
        String username = auth.getName();
        List<Appointment> myAppts = appointments.findByDoctorUsernameOrderByScheduledAtAsc(username);
        model.addAttribute("appointments", myAppts);
        return "dashboard/doctor";
    }

    @PostMapping("/appointments/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes ra, Authentication auth) {
        return updateStatus(id, AppointmentStatus.COMPLETED, ra, auth);
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra, Authentication auth) {
        return updateStatus(id, AppointmentStatus.CANCELLED, ra, auth);
    }

    private String updateStatus(Long id, AppointmentStatus status, RedirectAttributes ra, Authentication auth) {
        return appointments.findById(id).map(a -> {
            if (!a.getDoctorUsername().equals(auth.getName())) {
                ra.addFlashAttribute("error", "Not authorized for this appointment");
                return "redirect:/doctor";
            }
            a.setStatus(status);
            appointments.save(a);
            // Notify admin and receptionist accounts if they exist
            notifications.save(new Notification("admin",
                    "Doctor " + auth.getName() + " marked appointment for " + a.getPatient().getFirstName() + " as " + status));
            notifications.save(new Notification("reception",
                    "Doctor " + auth.getName() + " marked appointment for " + a.getPatient().getFirstName() + " as " + status));
            ra.addFlashAttribute("success", "Appointment updated");
            return "redirect:/doctor";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Appointment not found");
            return "redirect:/doctor";
        });
    }
}
