package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.CreateAbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UpdateApprovalStatusDTO;

import java.util.List;

public interface AbsenceRequestService {

    /**
     * Retrieves all absence requests.
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
     * Creates a new absence request. The initial status for both department and CTSV approval
     * will be set to PENDING.
     * @param createDto The DTO containing information for the new request.
     * @return The created request DTO.
     */
    AbsenceRequestDTO createRequest(CreateAbsenceRequestDTO createDto);

    /**
     * Updates the approval status from the Department level.
     * @param requestId The ID of the request to update.
     * @param statusDto The DTO containing the new status (APPROVED or REJECTED).
     * @return The updated request DTO.
     */
    AbsenceRequestDTO updateDepartmentApproval(Integer requestId, UpdateApprovalStatusDTO statusDto);

    /**
     * Updates the approval status from the CTSV (Phòng Công tác Sinh viên) level.
     * @param requestId The ID of the request to update.
     * @param statusDto The DTO containing the new status (APPROVED or REJECTED).
     * @return The updated request DTO.
     */
    AbsenceRequestDTO updateCtsvApproval(Integer requestId, UpdateApprovalStatusDTO statusDto);

}
