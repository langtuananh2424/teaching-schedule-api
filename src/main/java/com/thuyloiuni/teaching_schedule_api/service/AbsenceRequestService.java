package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;

import java.util.List;

public interface AbsenceRequestService {

    /**
     * Retrieves all absence requests based on the user's role.
     * - ADMIN can see all requests.
     * - MANAGER can only see requests from their department.
     * @return A list of all absence requests.
     */
    List<AbsenceRequestDTO> getAllRequests();

    /**
     * Retrieves a single absence request by its ID.
     * @param requestId The ID of the request.
     * @return The found request DTO.
     */
    AbsenceRequestDTO getRequestById(Integer requestId);

    /**
     * Creates a new absence request. The initial status for both manager and academic affairs approval
     * will be set to PENDING.
     * @param createDto The DTO containing information for the new request.
     * @return The created request DTO.
     */
    AbsenceRequestDTO createRequest(CreateAbsenceRequestDTO createDto);

    /**
     * Updates the approval status from the Manager level.
     * A manager can only approve requests from their own department.
     * @param requestId The ID of the request to update.
     * @param statusDto The DTO containing the new status (APPROVED or REJECTED).
     * @return The updated request DTO.
     */
    AbsenceRequestDTO updateManagerApproval(Integer requestId, UpdateApprovalStatusDTO statusDto);

    /**
     * Updates the approval status from the Academic Affairs level.
     * @param requestId The ID of the request to update.
     * @param statusDto The DTO containing the new status (APPROVED or REJECTED).
     * @return The updated request DTO.
     */
    AbsenceRequestDTO updateAcademicAffairsApproval(Integer requestId, UpdateApprovalStatusDTO statusDto);

}
