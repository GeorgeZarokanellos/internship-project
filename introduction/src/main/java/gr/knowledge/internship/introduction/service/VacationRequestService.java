package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.VacationRequestDTO;
import gr.knowledge.internship.introduction.entity.Employee;
import gr.knowledge.internship.introduction.enums.VacationStatusEnum;
import gr.knowledge.internship.introduction.entity.VacationRequest;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import gr.knowledge.internship.introduction.repository.VacationRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
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
    private final Map<VacationStatusEnum, Function<VacationRequestDTO,VacationRequestDTO>> statusMap;

    @Autowired
    public VacationRequestService(VacationRequestRepository vacationRequestRepository,
                                  EmployeeRepository employeeRepository,
                                  ModelMapper modelMapper){
        this.vacationRequestRepository = vacationRequestRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.statusMap = new HashMap<>();
        //initialize the map with the functions that correspond to each status
        insertAcceptRejectVacationRequest();
    }

    public List<VacationRequestDTO> getVacationRequests(){
        List<VacationRequest> vacationRequestList = vacationRequestRepository.findAll();
        return modelMapper.map(vacationRequestList, new TypeToken<List<VacationRequestDTO>>(){}.getType());
    }

    public VacationRequestDTO getVacationRequestById(int id) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacation request with id: " + id + " not found"));
        return modelMapper.map(vacationRequest, new TypeToken<VacationRequestDTO>(){}.getType());
    }
    public VacationRequestDTO createVacationRequest(VacationRequestDTO vacationRequestDTO){
        log.info("Creating vacation request for employee with id: " + vacationRequestDTO.getEmployee().getId());

        //get the employee entity to retrieve the remaining holidays
        Employee employee = employeeRepository.findById(vacationRequestDTO.getEmployee().getId())
                .orElseThrow(() -> {
                        log.error("Employee with id: " + vacationRequestDTO.getEmployee().getId() + " not found from createVacationRequest!");
                        return new EntityNotFoundException("Employee with id: " + vacationRequestDTO.getEmployee().getId() + " not found from createVacationRequest!");
                });

        //calculates the number of days between the dates the employee wants to take a leave
        int daysBetween = (int) ChronoUnit.DAYS.between(vacationRequestDTO.getStartDate(), vacationRequestDTO.getEndDate());
        log.info("Days between the dates: " + daysBetween);

        return checkRemainingDaysAndSetStatus(vacationRequestDTO, daysBetween, employee);
    }

    public VacationRequestDTO updateVacationRequest(VacationRequestDTO requestBody, int vacationId){

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

            vacationRequestRepository.save(modelMapper.map(vacationRequestDTO, VacationRequest.class));
            return vacationRequestDTO;
        }
    }
    public boolean deleteVacationRequest(int id){
        vacationRequestRepository.deleteById(id);
        return true;
    }

    //TODO: check again if the days are being subtracted correctly
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

    private void insertAcceptRejectVacationRequest(){
        statusMap.put(VacationStatusEnum.ACCEPTED, this::acceptVR);
        statusMap.put(VacationStatusEnum.REJECTED, this::rejectVR);
    }
    private VacationRequestDTO acceptVR(VacationRequestDTO vacationRequestDTO) {
        //calculates the number of days between the dates the employee wants to take a leave
        int daysToBeSubtracted = (int) ChronoUnit.DAYS.between(vacationRequestDTO.getStartDate(),
                vacationRequestDTO.getEndDate()) - vacationRequestDTO.getDays() + 1;

        //get remaining vacation days of the employee
        int vacationDays = vacationRequestDTO.getEmployee().getVacationDays();

        //subtracts the days from the employee's remaining vacation days and change status
        vacationRequestDTO.getEmployee().setVacationDays(vacationDays - daysToBeSubtracted);
        vacationRequestDTO.setStatus(VacationStatusEnum.ACCEPTED);

        return vacationRequestDTO;
    }

    private VacationRequestDTO rejectVR(VacationRequestDTO vacationRequestDTO) {
        vacationRequestDTO.setStatus(VacationStatusEnum.REJECTED);
        return vacationRequestDTO;
    }

}
