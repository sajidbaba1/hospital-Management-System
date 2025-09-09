package com.hms.doctor;

import com.hms.medical.MedicalNote;
import com.hms.medical.MedicalNoteRepository;
import com.hms.patient.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/doctor/notes")
@PreAuthorize("hasRole('DOCTOR')")
public class MedicalNoteController {

    private final MedicalNoteRepository medicalNotes;
    private final PatientRepository patients;

    public MedicalNoteController(MedicalNoteRepository medicalNotes, PatientRepository patients) {
        this.medicalNotes = medicalNotes;
        this.patients = patients;
    }

    @GetMapping
    public String list(Authentication auth, Model model) {
        model.addAttribute("notes", medicalNotes.findByDoctorUsernameOrderByCreatedAtDesc(auth.getName()));
        return "doctor/notes/list";
    }

    @GetMapping("/patient/{patientId}")
    public String patientHistory(@PathVariable Long patientId, Model model, RedirectAttributes ra) {
        if (!patients.existsById(patientId)) {
            ra.addFlashAttribute("error", "Patient not found");
            return "redirect:/doctor/notes";
        }
        model.addAttribute("patient", patients.findById(patientId).get());
        model.addAttribute("notes", medicalNotes.findByPatientIdOrderByCreatedAtDesc(patientId));
        return "doctor/notes/patient-history";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam Long patientId, Model model, RedirectAttributes ra) {
        if (!patients.existsById(patientId)) {
            ra.addFlashAttribute("error", "Patient not found");
            return "redirect:/doctor/notes";
        }
        MedicalNote note = new MedicalNote();
        note.setPatient(patients.findById(patientId).get());
        model.addAttribute("note", note);
        return "doctor/notes/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("note") MedicalNote note, BindingResult result, 
                      Authentication auth, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "doctor/notes/form";
        }
        note.setDoctorUsername(auth.getName());
        medicalNotes.save(note);
        ra.addFlashAttribute("success", "Medical note saved");
        return "redirect:/doctor/notes/patient/" + note.getPatient().getId();
    }
}
