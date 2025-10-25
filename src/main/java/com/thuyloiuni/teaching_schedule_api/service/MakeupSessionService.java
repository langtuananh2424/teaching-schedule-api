package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.CreateMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;

import java.util.List;

public interface MakeupSessionService {

    /**
     * Retrieves all makeup sessions.
     * @return A list of all makeup sessions.
     */
    List<MakeupSessionDTO> getAllMakeupSessions();

    /**
     * Retrieves a single makeup session by its ID.
     * @param id The ID of the makeup session.
     * @return The found makeup session DTO.
     */
    MakeupSessionDTO getMakeupSessionById(Integer id);

    /**
     * Creates a new makeup session. The initial status for both department and CTSV approval
     * will be set to PENDING.
     * @param createDto The DTO containing information for the new session.
     * @return The created makeup session DTO.
     */
    MakeupSessionDTO createMakeupSession(CreateMakeupSessionDTO createDto);

    /**
     * Updates the approval status from the Department level for a makeup session.
     * @param id The ID of the makeup session to update.
     * @param statusDto The DTO containing the new status (APPROVED or REJECTED).
     * @return The updated makeup session DTO.
     */
    MakeupSessionDTO updateDepartmentApproval(Integer id, UpdateApprovalStatusDTO statusDto);

    /**
     * Updates the approval status from the CTSV level for a makeup session.
     * @param id The ID of the makeup session to update.
     * @param statusDto The DTO containing the new status (APPROVED or REJECTED).
     * @return The updated makeup session DTO.
     */
    MakeupSessionDTO updateCtsvApproval(Integer id, UpdateApprovalStatusDTO statusDto);
}
