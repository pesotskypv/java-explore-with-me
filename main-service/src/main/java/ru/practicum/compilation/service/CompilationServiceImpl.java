package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.EntityConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.EntityValidationException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (compilationRepository.existsByTitle(newCompilationDto.getTitle()))
            throw new EntityValidationException("Имя категории должно быть уникальным.");

        Compilation savedCompilation = compilationMapper.toCompilation(newCompilationDto);
        List<Long> eventIds = newCompilationDto.getEvents();
        Boolean pinned = newCompilationDto.getPinned();

        if (eventIds != null && !eventIds.isEmpty()) savedCompilation.setEvents(eventRepository.findAllById(eventIds));
        savedCompilation.setPinned(pinned != null ? pinned : false);

        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationRepository
                .save(savedCompilation));

        if (compilationDto.getEvents() == null) compilationDto.setEvents(Collections.emptyList());

        log.info("Добавлена новая категория: {}", compilationDto);
        return compilationDto;
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена или недоступена."));
        List<Event> events = compilation.getEvents();

        if (events != null && !events.isEmpty())
            throw new EntityConflictException("Существуют события, связанные с категорией.");

        compilationRepository.deleteById(compId);
        log.info("Удалена категория с id: {}", compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена или недоступена."));
        List<Long> events = updateCompilationRequest.getEvents();
        Boolean pinned = updateCompilationRequest.getPinned();
        String title = updateCompilationRequest.getTitle();

        if (title != null) {
            if (compilationRepository.existsByTitle(title) && !compilation.getTitle().equals(title))
                throw new EntityValidationException("Имя категории должно быть уникальным.");
            compilation.setTitle(title);
        }
        if (events != null && !events.isEmpty()) compilation.setEvents(eventRepository.findAllById(events));
        if (pinned != null) compilation.setPinned(pinned);

        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationRepository.save(compilation));

        log.info("Изменена категория: {}", compilationDto);
        return compilationDto;
    }

    @Override
    public List<CompilationDto> findAllCompilation(Boolean pinned, PageRequest of) {
        List<CompilationDto> compilationDtos;

        if (pinned != null) {
            compilationDtos = compilationRepository.findByPinned(pinned, of).stream()
                    .map(compilationMapper::toCompilationDto).collect(Collectors.toList());
        } else {
            compilationDtos = compilationRepository.findAll(of).stream().map(compilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }

        log.info("Возвращена подборка событий: {}", compilationDtos);
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationRepository
                .findById(compId).orElseThrow(() ->
                        new EntityNotFoundException("Категория не найдена или недоступена.")));

        log.info("Возвращена подборка событий: {}", compilationDto);
        return compilationDto;
    }
}
