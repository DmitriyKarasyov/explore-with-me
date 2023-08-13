package ru.practicum.main_service.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main_service.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("admin/compilations")
public class CompilationAdminController {
    private final CompilationService service;

    @Autowired
    public CompilationAdminController(CompilationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("post compilation: {}", newCompilationDto);
        return service.postCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compId) {
        log.info("delete compilation with id={}", compId);
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Integer compId,
                                            @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("patch compilation with id={}, update compilation request: {}", compId, updateCompilationRequest);
        return service.updateCompilation(compId, updateCompilationRequest);
    }
}
