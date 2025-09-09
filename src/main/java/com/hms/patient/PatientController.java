package com.hms.patient;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        return "patients/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patients/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Patient> p = patientRepository.findById(id);
        if (p.isEmpty()) {
            ra.addFlashAttribute("error", "Patient not found");
            return "redirect:/patients";
        }
        model.addAttribute("patient", p.get());
        return "patients/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("patient") Patient patient, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "patients/form";
        }
        patientRepository.save(patient);
        ra.addFlashAttribute("success", "Patient saved successfully");
        return "redirect:/patients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            ra.addFlashAttribute("success", "Patient deleted");
        } else {
            ra.addFlashAttribute("error", "Patient not found");
        }
        return "redirect:/patients";
    }
}
