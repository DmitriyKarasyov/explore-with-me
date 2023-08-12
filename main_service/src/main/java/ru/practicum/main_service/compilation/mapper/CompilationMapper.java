package ru.practicum.main_service.compilation.mapper;

import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.model.Compilation;
import ru.practicum.main_service.event.mapper.EventMapper;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {

    public static CompilationDto makeCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.makeEventShortDto(new ArrayList<>(compilation.getEvents())))
                .pinned(compilation.getPined())
                .title(compilation.getTitle())
                .build();
    }

    public static List<CompilationDto> makeCompilationDto(List<Compilation> compilationList) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilationList) {
            compilationDtoList.add(makeCompilationDto(compilation));
        }
        return compilationDtoList;
    }
}
