package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.VacationRequestDTO;
import gr.knowledge.internship.introduction.entity.Employee;
import gr.knowledge.internship.introduction.enums.VacationStatusEnum;
import gr.knowledge.internship.introduction.entity.VacationRequest;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import gr.knowledge.internship.introduction.repository.VacationRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Transactional
@Log4j2
public class VacationRequestService {

    private final VacationRequestRepository vacationRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    /**
     * Constructor for VacationRequestService.
     *
     * @param vacationRequestRepository Repository for vacation requests.
     * @param employeeRepository Repository for employees.
     * @param modelMapper Model mapper for converting between DTOs and entities.
     */
    @Autowired
    public VacationRequestService(VacationRequestRepository vacationRequestRepository,
                                  EmployeeRepository employeeRepository,
                                  ModelMapper modelMapper){
        this.vacationRequestRepository = vacationRequestRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves all vacation requests.
     *
     * @return List of VacationRequestDTOs.
     */
    @Transactional(readOnly = true)
    public List<VacationRequestDTO> getVacationRequests(){
        List<VacationRequest> vacationRequestList = vacationRequestRepository.findAll();
        return modelMapper.map(vacationRequestList, new TypeToken<List<VacationRequestDTO>>(){}.getType());
    }

/**
     * Retrieves a vacation request by its id.
     *
     * @param id Id of the vacation request.
     * @return VacationRequestDTO.
     */
    @Transactional(readOnly = true)
    public VacationRequestDTO getVacationRequestById(int id) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacation request with id: " + id + " not found"));
        return modelMapper.map(vacationRequest, new TypeToken<VacationRequestDTO>(){}.getType());
    }

/**
     * Creates a vacation request.
     *
     * @param vacationRequestDTO VacationRequestDTO to be created.
     * @return VacationRequestDTO.
     */
    public VacationRequestDTO createVacationRequest(VacationRequestDTO vacationRequestDTO){
        log.info("Creating vacation request for employee with id: " + vacationRequestDTO.getEmployee().getId());

        //get the employee entity to retrieve the remaining holidays
        Employee employee = employeeRepository.findById(vacationRequestDTO.getEmployee().getId())
                .orElseThrow(() -> {
                        log.error("Employee with id: " + vacationRequestDTO.getEmployee().getId() + " not found from createVacationRequest!");
                        return new EntityNotFoundException("Employee with id: " + vacationRequestDTO.getEmployee().getId() + " not found from createVacationRequest!");
                });

        //calculates the number of days between the dates the employee wants to take a leave
        int daysBetween =  Period.between(vacationRequestDTO.getStartDate(), vacationRequestDTO.getEndDate()).getDays();
        log.info("Days between the dates: " + daysBetween);

        return checkRemainingDaysAndSetStatus(vacationRequestDTO, daysBetween, employee);
    }

/**
     * Updates a vacation request.
     *
     * @param requestBody VacationRequestDTO to be updated.
     * @param vacationId Id of the vacation request to be updated.
     * @return VacationRequestDTO.
     */
    public VacationRequestDTO updateVacationRequest(VacationRequestDTO requestBody, int vacationId){
        Map<VacationStatusEnum, Function<VacationRequestDTO, VacationRequestDTO>> statusMap = new HashMap<>();
        insertAcceptRejectVacationRequest(statusMap);
        if(!VacationStatusEnum.resolveEnum(requestBody.getStatus().toString()))
            throw new IllegalArgumentException("Invalid status: " + requestBody.getStatus().toString() + " from updateVacationRequest!");

        VacationRequestDTO vacationRequestDTO = modelMapper.map(vacationRequestRepository.findById(vacationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vacation request with id: " + vacationId + " not found from updateStatusAndVacationDays!")
                ), new TypeToken<VacationRequestDTO>(){}.getType());

        if(requestBody.getStatus() == vacationRequestDTO.getStatus()){  //if the status is the same, then do normal update operation
            requestBody.setId(vacationId);
            vacationRequestRepository.save(modelMapper.map(requestBody, VacationRequest.class));
            return requestBody;
        } else {
            //if the status is different, then apply the function that corresponds to the status
            vacationRequestDTO = statusMap.get(requestBody.getStatus()).apply(vacationRequestDTO);
            log.info("Vacation days in update method: " + vacationRequestDTO.getEmployee().getVacationDays());
            vacationRequestRepository.save(modelMapper.map(vacationRequestDTO, VacationRequest.class));
            return vacationRequestDTO;
        }
    }

/**
     * Deletes a vacation request.
     *
     * @param id Id of the vacation request to be deleted.
     * @return boolean.
     */
    public boolean deleteVacationRequest(int id){
        vacationRequestRepository.deleteById(id);
        return true;
    }

/**
     * Checks if the remaining vacation days of the employee are enough and sets the status of the vacation request.
     *
     * @param vacationRequestDTO VacationRequestDTO to be checked.
     * @param daysBetween Number of days between the dates the employee wants to take a leave.
     * @param employee Employee entity.
     * @return VacationRequestDTO.
     */
    private VacationRequestDTO checkRemainingDaysAndSetStatus(VacationRequestDTO vacationRequestDTO, int daysBetween, Employee employee){
        //check if the remaining vacation days of the employee are enough
        if( daysBetween - vacationRequestDTO.getDays() <= employee.getVacationDays()) {
            log.info("Enough vacation days for employee with id: " + vacationRequestDTO.getEmployee().getId());
            vacationRequestDTO.setStatus(VacationStatusEnum.PENDING);
        } else {
            log.info("Not enough vacation days for employee with id: " + vacationRequestDTO.getEmployee().getId());
            vacationRequestDTO.setStatus(VacationStatusEnum.REJECTED);
        }
        vacationRequestRepository.save(modelMapper.map(vacationRequestDTO, VacationRequest.class));
        log.info("Vacation request created successfully!");
        return vacationRequestDTO;
    }

    /**
     * Inserts the accept and reject functions in the status map.
     *
     * @param statusMap Map to insert the functions.
     */
    private void insertAcceptRejectVacationRequest(Map<VacationStatusEnum, Function<VacationRequestDTO, VacationRequestDTO>> statusMap){
        statusMap.put(VacationStatusEnum.ACCEPTED, this::acceptVR);
        statusMap.put(VacationStatusEnum.REJECTED, this::rejectVR);
    }

    /**
     * Accepts a vacation request.
     *
     * @param vacationRequestDTO VacationRequestDTO to be accepted.
     * @return VacationRequestDTO.
     */
    private VacationRequestDTO acceptVR(VacationRequestDTO vacationRequestDTO) {
        //calculates the number of days between the dates the employee wants to take a leave
        int daysToBeSubtracted = Period.between(vacationRequestDTO.getStartDate(), vacationRequestDTO.getEndDate()).getDays() - vacationRequestDTO.getDays();

        //get remaining vacation days of the employee
        int vacationDays = vacationRequestDTO.getEmployee().getVacationDays();

        //subtracts the days from the employee's remaining vacation days and change status
        vacationRequestDTO.getEmployee().setVacationDays(vacationDays - daysToBeSubtracted);
        vacationRequestDTO.setStatus(VacationStatusEnum.ACCEPTED);
        employeeRepository.save(modelMapper.map(vacationRequestDTO.getEmployee(), Employee.class));
        return vacationRequestDTO;
    }

    /**
     * Rejects a vacation request.
     *
     * @param vacationRequestDTO VacationRequestDTO to be rejected.
     * @return VacationRequestDTO.
     */
    private VacationRequestDTO rejectVR(VacationRequestDTO vacationRequestDTO) {
        vacationRequestDTO.setStatus(VacationStatusEnum.REJECTED);
        return vacationRequestDTO;
    }

}
