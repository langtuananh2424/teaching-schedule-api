package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.CreateMakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;

import java.util.List;

public interface MakeupSessionService {

    /**
     * Retrieves all makeup sessions based on the user's role.
     * - ADMIN can see all requests.
     * - MANAGER can only see requests from their department.
     * @return A list of makeup sessions.
     */
    List<MakeupSessionDTO> getAllMakeupSessions();

    /**
     * Retrieves a single makeup session by its ID.
     * @param id The ID of the makeup session.
     * @return The found makeup session DTO.
     */
    MakeupSessionDTO getMakeupSessionById(Integer id);

    /**
     * Creates a new makeup session. The initial status for both manager and academic affairs approval
     * will be set to PENDING.
     * @param createDto The DTO containing information for the new session.
     * @return The created makeup session DTO.
     */
    MakeupSessionDTO createMakeupSession(CreateMakeupSessionDTO createDto);

    /**
     * Updates the approval status from the Manager level for a makeup session.
     * A manager can only approve requests from their own department.
     * @param id The ID of the makeup session to update.
     * @param statusDto The DTO containing the new status (APPROVED or REJECTED).
     * @return The updated makeup session DTO.
     */
    MakeupSessionDTO updateManagerApproval(Integer id, UpdateApprovalStatusDTO statusDto);

    /**
     * Updates the approval status from the Academic Affairs level for a makeup session.
     * @param id The ID of the makeup session to update.
     * @param statusDto The DTO containing the new status (APPROVED or REJECTED).
     * @return The updated makeup session DTO.
     */
    MakeupSessionDTO updateAcademicAffairsApproval(Integer id, UpdateApprovalStatusDTO statusDto);
}
