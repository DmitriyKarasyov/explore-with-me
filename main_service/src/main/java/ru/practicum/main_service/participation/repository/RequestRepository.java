package ru.practicum.main_service.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_service.participation.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findAllByEvent_Id(Integer eventId);

    List<ParticipationRequest> findAllByIdIn(List<Integer> ids);

    List<ParticipationRequest> findAllByRequester_Id(Integer requesterId);

    Boolean existsByEvent_IdAndRequester_Id(Integer eventId, Integer requesterId);
}
