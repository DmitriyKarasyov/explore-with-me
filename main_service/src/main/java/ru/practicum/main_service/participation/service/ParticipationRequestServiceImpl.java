package ru.practicum.main_service.participation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.common.DBRequest;
import ru.practicum.main_service.event.model.event.Event;
import ru.practicum.main_service.event.model.state.State;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.exception.ConditionViolationException;
import ru.practicum.main_service.exception.EWMConstraintViolationException;
import ru.practicum.main_service.participation.dto.ParticipationRequestDto;
import ru.practicum.main_service.participation.mapper.ParticipationRequestMapper;
import ru.practicum.main_service.participation.model.ParticipationRequest;
import ru.practicum.main_service.participation.repository.RequestRepository;
import ru.practicum.main_service.participation.status.Status;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final DBRequest<ParticipationRequest> requestDBRequest;
    private final DBRequest<User> userDBRequest;
    private final DBRequest<Event> eventDBRequest;

    @Autowired
    public ParticipationRequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository,
                                           EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        requestDBRequest = new DBRequest<>(requestRepository);
        userDBRequest = new DBRequest<>(userRepository);
        eventDBRequest = new DBRequest<>(eventRepository);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getUserRequests(Integer userId) {
        userDBRequest.checkExistence(User.class, userId);
        return ParticipationRequestMapper.makeParticipationRequestDto(requestRepository.findAllByRequester_Id(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto postRequest(Integer userId, Integer eventId) {
        userDBRequest.checkExistence(User.class, userId);
        eventDBRequest.checkExistence(Event.class, eventId);
        Event event = eventRepository.getReferenceById(eventId);
        checkIfRequesterIsInitiator(userId, event);
        checkIfRequestIsDouble(eventId, userId);
        checkEventState(event);
        checkEventLimit(event);
        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(userRepository.getReferenceById(userId))
                .build();

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(Status.PENDING);
        } else {
            request.setStatus(Status.CONFIRMED);
        }

        return ParticipationRequestMapper.makeParticipationRequestDto(
                requestDBRequest.tryRequest(requestRepository::save, request));
    }

    public void checkIfRequestIsDouble(Integer eventId, Integer requesterId) {
        if (requestRepository.existsByEvent_IdAndRequester_Id(eventId, requesterId)) {
            throw new EWMConstraintViolationException("Can not post participation request: request from this user " +
                    "already exists");
        }
    }

    public void checkIfRequesterIsInitiator(Integer userId, Event event) {
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConditionViolationException("Initiator of event cannot request participation.");
        }
    }

    public void checkEventState(Event event) {
        if (event.getState() != State.PUBLISHED) {
            throw new ConditionViolationException("Only published event can receive participation requests");
        }
    }

    public void checkEventLimit(Event event) {
        if (event.getParticipantLimit() != 0 && !(event.getConfirmedRequests() <= event.getParticipantLimit())) {
            throw new ConditionViolationException("Event participation limit is reached");
        }
    }
}
