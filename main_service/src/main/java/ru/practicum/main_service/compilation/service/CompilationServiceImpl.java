package ru.practicum.main_service.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.common.DBRequest;
import ru.practicum.main_service.common.PageableParser;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main_service.compilation.mapper.CompilationMapper;
import ru.practicum.main_service.compilation.model.Compilation;
import ru.practicum.main_service.compilation.repository.CompilationRepository;
import ru.practicum.main_service.event.model.event.Event;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final DBRequest<Compilation> compilationDBRequest;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository,
                                  EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        compilationDBRequest = new DBRequest<>(compilationRepository);
    }

    @Override
    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageableParser.makePageable(from, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findBypinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }
        return CompilationMapper.makeCompilationDto(compilations);
    }

    @Override
    @Transactional
    public CompilationDto getCompilation(Integer compId) {
        compilationDBRequest.checkExistence(Compilation.class, compId);
        Compilation compilation = compilationRepository.getReferenceById(compId);
        return CompilationMapper.makeCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto postCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = makeCompilation(newCompilationDto);
        return CompilationMapper.makeCompilationDto(
                compilationDBRequest.tryRequest(compilationRepository::save, compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Integer compId) {
        compilationDBRequest.checkExistence(Compilation.class, compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest updateCompilationRequest) {
        compilationDBRequest.checkExistence(Compilation.class, compId);
        Compilation compilation = compilationRepository.getReferenceById(compId);
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            Set<Event> newEvents = new HashSet<>(eventRepository.findAllByIdIn(updateCompilationRequest.getEvents()));
            compilation.setEvents(newEvents);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        Compilation newCompilation = compilationDBRequest.tryRequest(compilationRepository::save, compilation);
        return CompilationMapper.makeCompilationDto(newCompilation);
    }

    public Compilation makeCompilation(NewCompilationDto newCompilationDto) {
        if (!eventRepository.existsAllByIdIn(newCompilationDto.getEvents())) {
            throw new NotFoundException("Event was not found.");
        }
        return Compilation.builder()
                .events(new HashSet<>(eventRepository.findAllByIdIn(newCompilationDto.getEvents())))
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }
}
