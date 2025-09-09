package com.hms.appointment;

import com.hms.patient.PatientRepository;
import com.hms.user.Role;
import com.hms.user.UserAccount;
import com.hms.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/appointments")
@PreAuthorize("hasRole('RECEPTIONIST')")
public class AppointmentController {

    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final UserRepository users;

    public AppointmentController(AppointmentRepository appointments, PatientRepository patients, UserRepository users) {
        this.appointments = appointments;
        this.patients = patients;
        this.users = users;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("appointments", appointments.findAll());
        return "appointments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("patients", patients.findAll());
        model.addAttribute("doctors", doctorUsers());
        model.addAttribute("statuses", AppointmentStatus.values());
        return "appointments/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Appointment> a = appointments.findById(id);
        if (a.isEmpty()) {
            ra.addFlashAttribute("error", "Appointment not found");
            return "redirect:/appointments";
        }
        model.addAttribute("appointment", a.get());
        model.addAttribute("patients", patients.findAll());
        model.addAttribute("doctors", doctorUsers());
        model.addAttribute("statuses", AppointmentStatus.values());
        return "appointments/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("appointment") Appointment appt, BindingResult result, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("patients", patients.findAll());
            model.addAttribute("doctors", doctorUsers());
            model.addAttribute("statuses", AppointmentStatus.values());
            return "appointments/form";
        }
        appointments.save(appt);
        ra.addFlashAttribute("success", "Appointment saved");
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        if (appointments.existsById(id)) {
            appointments.deleteById(id);
            ra.addFlashAttribute("success", "Appointment deleted");
        } else {
            ra.addFlashAttribute("error", "Appointment not found");
        }
        return "redirect:/appointments";
    }

    private List<UserAccount> doctorUsers() {
        return users.findAll().stream().filter(u -> u.getRole() == Role.DOCTOR).toList();
    }
}
