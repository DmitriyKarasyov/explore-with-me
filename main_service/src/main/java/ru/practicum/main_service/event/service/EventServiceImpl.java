package ru.practicum.main_service.event.service;

import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.common.DBRequest;
import ru.practicum.main_service.common.EWMDateFormatter;
import ru.practicum.main_service.common.PageableParser;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.mapper.*;
import ru.practicum.main_service.event.model.event.Event;
import ru.practicum.main_service.event.model.sort.Sort;
import ru.practicum.main_service.event.model.state.State;
import ru.practicum.main_service.event.model.state_action.AdminStateAction;
import ru.practicum.main_service.event.model.state_action.UserStateAction;
import ru.practicum.main_service.event.model.status.UpdateRequestStatus;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.event.model.event.QEvent;
import ru.practicum.main_service.event.repository.LocationRepository;
import ru.practicum.main_service.exception.ConditionViolationException;
import ru.practicum.main_service.exception.IncorrectRequestException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.participation.dto.ParticipationRequestDto;
import ru.practicum.main_service.participation.mapper.ParticipationRequestMapper;
import ru.practicum.main_service.participation.model.ParticipationRequest;
import ru.practicum.main_service.participation.repository.RequestRepository;
import ru.practicum.main_service.participation.status.Status;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;
import ru.practicum.main_service.validation.EventDateValidator;
import ru.practicum.statistics_service.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final DBRequest<Event> eventDBRequest;
    private final DBRequest<User> userDBRequest;
    private final DBRequest<Category> categoryDBRequest;
    private final DBRequest<ParticipationRequest> requestDBRequest;
    private final EWMStatsClient statsClient;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            RequestRepository requestRepository,
                            LocationRepository locationRepository,
                            EWMStatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.locationRepository = locationRepository;
        eventDBRequest = new DBRequest<>(eventRepository);
        userDBRequest = new DBRequest<>(userRepository);
        categoryDBRequest = new DBRequest<>(categoryRepository);
        requestDBRequest = new DBRequest<>(requestRepository);
        this.statsClient = statsClient;
    }

    @Override
    @Transactional
    public List<EventShortDto> getUserEvents(Integer userId, Integer from, Integer size) {
        userDBRequest.checkExistence(User.class, userId);
        Pageable pageable = PageableParser.makePageable(from, size);
        return EventMapper.makeEventShortDto(eventRepository.findAllByInitiator_Id(userId, pageable));
    }

    @Override
    @Transactional
    public EventFullDto postEvent(Integer userId, NewEventDto newEventDto) {
        userDBRequest.checkExistence(User.class, userId);
        categoryDBRequest.checkExistence(Category.class, newEventDto.getCategory());
        locationRepository.save(newEventDto.getLocation());
        setDefaultFields(newEventDto);
        Event newEvent = eventDBRequest.tryRequest(eventRepository::save, makeEvent(newEventDto, userId));
        return EventMapper.makeEventFullDto(newEvent);
    }

    @Override
    @Transactional
    public EventFullDto getEventByIdPrivate(Integer userId, Integer eventId) {
        userDBRequest.checkExistence(User.class, userId);
        eventDBRequest.checkExistence(Event.class, eventId);
        Event event = eventRepository.getReferenceById(eventId);
        checkInitiator(event, userId);
        return EventMapper.makeEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto patchEventUser(Integer userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest) {
        userDBRequest.checkExistence(User.class, userId);
        eventDBRequest.checkExistence(Event.class, eventId);
        Event event = eventRepository.getReferenceById(eventId);
        checkInitiator(event, userId);
        if (updateEventUserRequest.getStateAction() != null) {
            checkStateUser(event);
            setStateUser(event, updateEventUserRequest.getStateAction());
        }
        updateEvent(event, updateEventUserRequest);
        Event updatedEvent = eventDBRequest.tryRequest(eventRepository::save, event);
        return EventMapper.makeEventFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getEventRequests(Integer userId, Integer eventId) {
        userDBRequest.checkExistence(User.class, userId);
        eventDBRequest.checkExistence(Event.class, eventId);
        Event event = eventRepository.getReferenceById(eventId);
        checkInitiator(event, userId);
        return ParticipationRequestMapper.makeParticipationRequestDto(requestRepository.findAllByEvent_Id(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(Integer userId,
                                                         Integer eventId,
                                                         EventRequestStatusUpdateRequest request) {
        UpdateRequestStatus requestStatus = StatusMapper.makeUpdateRequestStatus(request.getStatus());
        userDBRequest.checkExistence(User.class, userId);
        eventDBRequest.checkExistence(Event.class, eventId);
        Event event = eventRepository.getReferenceById(eventId);
        checkInitiator(event, userId);
        List<ParticipationRequest> requestsToUpdate = requestRepository.findAllByIdIn(request.getRequestIds());
        if (requestsToUpdate.isEmpty()) {
            throw new NotFoundException("Requests with ids: " + request.getRequestIds() + " were not found");
        }
        checkRequestsRightEvent(event, requestsToUpdate);
        checkRequestsStatus(requestsToUpdate);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        if (requestStatus == UpdateRequestStatus.CONFIRMED) {
            result = confirmRequests(requestsToUpdate, event);
        } else if (requestStatus == UpdateRequestStatus.REJECTED) {
            result = rejectRequests(requestsToUpdate);
        }
        return result;
    }

    @Override
    @Transactional
    public List<EventFullDto> getEventsAdmin(Integer[] users, String[] stateStringsArray, Integer[] categories,
                                             String rangeStart, String rangeEnd, Integer from, Integer size) {
        BooleanBuilder whereClause = new BooleanBuilder();
        QEvent event = QEvent.event;

        if ((rangeStart != null && !rangeStart.isBlank()) && (rangeEnd != null && !rangeEnd.isBlank())) {
            checkDates(rangeStart, rangeEnd);
        }
        if (users != null && users.length != 0 && (users.length == 1 && users[0] != 0)) {
            whereClause.and(event.initiator.id.in(users));
        }
        if (stateStringsArray != null && stateStringsArray.length != 0) {
            List<String> stateStringsList = List.of(stateStringsArray);
            List<State> states = StateMapper.makeState(stateStringsList);
            whereClause.and(event.state.in(states));
        }
        if (categories != null && categories.length != 0 && (categories.length == 1 && categories[0] != 0)) {
            whereClause.and(event.category.id.in(categories));
        }
        if (rangeStart != null && !rangeStart.isBlank()) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, EWMDateFormatter.FORMATTER);
            whereClause.and(event.eventDate.after(start));
        }
        if (rangeEnd != null && !rangeEnd.isBlank()) {
            LocalDateTime end = LocalDateTime.parse(rangeEnd, EWMDateFormatter.FORMATTER);
            whereClause.and(event.eventDate.before(end));
        }
        Pageable pageable = PageableParser.makePageable(from, size);
        List<Event> events = eventRepository.findAll(whereClause, pageable).toList();
        return EventMapper.makeEventFullDto(events);
    }

    @Override
    @Transactional
    public EventFullDto patchEventAdmin(Integer eventId, UpdateEventAdminRequest updateRequest) {
        eventDBRequest.checkExistence(Event.class, eventId);
        EventDateValidator.validate(updateRequest.getEventDate());
        Event event = eventRepository.getReferenceById(eventId);
        if (updateRequest.getStateAction() != null && !updateRequest.getStateAction().isBlank()) {
            AdminStateAction stateAction = StateActionMapper.makeAdminStateAction(updateRequest.getStateAction());
            checkStateAdmin(event, stateAction);
            setStateAdmin(event, stateAction);
        }
        event = updateEvent(event, updateRequest);
        Event updatedEvent = eventDBRequest.tryRequest(eventRepository::save, event);
        return EventMapper.makeEventFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public List<EventShortDto> getEventsPublic(String text, Integer[] categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sortString, Integer from, Integer size) {
        BooleanBuilder whereClause = new BooleanBuilder();
        QEvent event = QEvent.event;
        whereClause.and(event.state.eq(State.PUBLISHED));
        if ((rangeStart != null && !rangeStart.isBlank()) && (rangeEnd != null && !rangeEnd.isBlank())) {
            checkDates(rangeStart, rangeEnd);
        }
        if ((rangeStart == null || rangeStart.isBlank()) && (rangeEnd == null || rangeEnd.isBlank())) {
            whereClause.and(event.eventDate.after(LocalDateTime.now()));
        }
        if (text != null && !text.isBlank()) {
            whereClause.and(event.annotation.toLowerCase().contains(text.toLowerCase())).or(
                    event.description.toLowerCase().contains(text.toLowerCase()));
        }
        if (categories != null && categories.length != 0) {
            whereClause.and(event.category.id.in(categories));
        }
        if (paid != null) {
            whereClause.and(event.paid.eq(paid));
        }
        if (rangeStart != null && !rangeStart.isBlank()) {
            whereClause.and(event.eventDate.after(LocalDateTime.parse(rangeStart, EWMDateFormatter.FORMATTER)));
        }
        if (rangeEnd != null && !rangeEnd.isBlank()) {
            whereClause.and(event.eventDate.before(LocalDateTime.parse(rangeEnd, EWMDateFormatter.FORMATTER)));
        }
        if (onlyAvailable != null && onlyAvailable) {
            whereClause.and(event.requestModeration.eq(false)).or(
                    event.participantLimit.gt(event.confirmedRequests.intValue()));
        }

        Pageable pageable = PageableParser.makePageable(from, size);
        List<Event> events = eventRepository.findAll(whereClause, pageable).toList();
        if (sortString != null && !sortString.isBlank()) {
            return sortList(events, sortString);
        } else {
            return EventMapper.makeEventShortDto(events);
        }
    }

    @Override
    @Transactional
    public EventFullDto getEventByIdPublic(Integer id, HttpServletRequest request) {
        eventDBRequest.checkExistence(Event.class, id);
        Event event = eventRepository.getReferenceById(id);
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("Event with id=%d was not found", id));
        }
        event.setViews(event.getViews() + 1);
        statsClient.postHit(makeEndpointHitDto(request));
        return EventMapper.makeEventFullDto(eventDBRequest.tryRequest(eventRepository::save, event));
    }

    public void checkDates(String startString, String endString) {
        if (LocalDateTime.parse(startString, EWMDateFormatter.FORMATTER).isAfter(
                LocalDateTime.parse(endString, EWMDateFormatter.FORMATTER))) {
            throw new IncorrectRequestException("Range start cannot be after range end.");
        }
    }

    public List<EventShortDto> sortList(List<Event> events, String sortString) {
        Sort sort = SortMapper.makeSort(sortString);
        switch (sort) {
            case EVENT_DATE:
                return EventMapper.makeEventShortDto(
                        events.stream().sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList()));
            case VIEWS:
                return EventMapper.makeEventShortDto(
                        events.stream().sorted(Comparator.comparing(Event::getViews).reversed())
                                .collect(Collectors.toList()));
        }
        return new ArrayList<>();
    }

    public EndpointHitDto makeEndpointHitDto(HttpServletRequest request) {
        return EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(EWMDateFormatter.FORMATTER))
                .build();
    }

    public EventRequestStatusUpdateResult confirmRequests(List<ParticipationRequest> requests, Event event) {
        if (checkModeration(event)) {
            changeStatusToConfirmed(event, requests);
            return makeEventRequestStatusUpdateResult(changeStatusToConfirmed(event, requests), new ArrayList<>());
        }
        Integer freePlaces = checkEventLimit(event, requests);
        List<ParticipationRequest> requestsToConfirm = requests.subList(0, freePlaces);
        List<ParticipationRequest> requestsToReject = requests.subList(freePlaces, requests.size());
        return makeEventRequestStatusUpdateResult(changeStatusToConfirmed(event, requestsToConfirm),
                changeStatusToRejected(requestsToReject));
    }

    public EventRequestStatusUpdateResult rejectRequests(List<ParticipationRequest> requests) {
        return makeEventRequestStatusUpdateResult(new ArrayList<>(), changeStatusToRejected(requests));
    }

    public EventRequestStatusUpdateResult makeEventRequestStatusUpdateResult(
            List<ParticipationRequestDto> confirmedRequests,
            List<ParticipationRequestDto> rejectedRequests
    ) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    public List<ParticipationRequestDto> changeStatusToConfirmed(Event event, List<ParticipationRequest> requests) {
        for (ParticipationRequest request : requests) {
            request.setStatus(Status.CONFIRMED);
        }
        Integer confirmedRequests = event.getConfirmedRequests();
        event.setConfirmedRequests(confirmedRequests + requests.size());
        eventDBRequest.tryRequest(eventRepository::save, event);
        requestDBRequest.tryListRequest(requestRepository::saveAll, requests);
        return requests.stream()
                .map(ParticipationRequestMapper::makeParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> changeStatusToRejected(List<ParticipationRequest> requests) {
        for (ParticipationRequest request : requests) {
            request.setStatus(Status.REJECTED);
        }
        requestDBRequest.tryListRequest(requestRepository::saveAll, requests);
        return requests.stream()
                .map(ParticipationRequestMapper::makeParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public void checkRequestsStatus(List<ParticipationRequest> requests) {
        for (ParticipationRequest request : requests) {
            if (request.getStatus() != Status.PENDING) {
                throw new ConditionViolationException(
                        String.format("Only pending requests cam change status. Request with id=%d has %s status.",
                                request.getId(), request.getStatus().toString()));
            }
        }
    }

    public Integer checkEventLimit(Event event, List<ParticipationRequest> requests) {
        if (Objects.equals(event.getParticipantLimit(), event.getConfirmedRequests())) {
            throw new ConditionViolationException("The participant limit has been reached");
        }
        return event.getParticipantLimit() - event.getConfirmedRequests();
    }

    public Boolean checkModeration(Event event) {
        return event.getParticipantLimit() == 0 || !event.getRequestModeration();
    }

    public void checkRequestsRightEvent(Event event, List<ParticipationRequest> requests) {
        Integer eventId = event.getId();
        for (ParticipationRequest request : requests) {
            if (!Objects.equals(request.getEvent().getId(), eventId)) {
                throw new ConditionViolationException(
                        String.format("Wrong event for participation request with id = %d", request.getId())
                );
            }
        }
    }

    public void checkStateUser(Event event) {
        State state = event.getState();
        if (!(state == null || state == State.PENDING || state == State.CANCELED)) {
            throw new IncorrectRequestException("Only pending or canceled events can be changed");
        }
    }

    public void checkStateAdmin(Event event, AdminStateAction stateAction) {
        State state = event.getState();
        switch (stateAction) {
            case PUBLISH_EVENT:
                if (state != State.PENDING) {
                    throw new ConditionViolationException(
                            "Cannot publish the event because it's not in the right state: " + state);
                }
            case REJECT_EVENT:
                if (state == State.PUBLISHED) {
                    throw new ConditionViolationException(
                            "Cannot reject the event because it's not in the right state: " + state
                    );
                }
        }
    }

    public Event updateEvent(Event event, UpdateEventRequest updateEventUserRequest) {
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            categoryDBRequest.checkExistence(Category.class, updateEventUserRequest.getCategory());
            event.setCategory(categoryRepository.getReferenceById(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null && !updateEventUserRequest.getDescription().isBlank()) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null && !updateEventUserRequest.getEventDate().isBlank()) {
            event.setEventDate(LocalDateTime.parse(updateEventUserRequest.getEventDate(), EWMDateFormatter.FORMATTER));
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null && !updateEventUserRequest.getTitle().isBlank()) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        return event;
    }

    public void setStateUser(Event event, String stateActionString) {
        UserStateAction stateAction = StateActionMapper.makeUserStateAction(stateActionString);
        switch (stateAction) {
            case SEND_TO_REVIEW:
                event.setState(State.PENDING);
                break;
            case CANCEL_REVIEW:
                event.setState(null);
                break;
        }
    }

    public void setStateAdmin(Event event, AdminStateAction stateAction) {
        switch (stateAction) {
            case PUBLISH_EVENT:
                event.setState(State.PUBLISHED);
                break;
            case REJECT_EVENT:
                event.setState(State.CANCELED);
                break;
        }
    }

    public void checkInitiator(Event event, Integer userId) {
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", event.getId()));
        }
    }

    public void setDefaultFields(NewEventDto newEventDto) {
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
    }

    public Event makeEvent(NewEventDto newEventDto, Integer userId) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(categoryRepository.getReferenceById(newEventDto.getCategory()))
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), EWMDateFormatter.FORMATTER))
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .initiator(userRepository.getReferenceById(userId))
                .views(0)
                .confirmedRequests(0)
                .build();
    }
}
