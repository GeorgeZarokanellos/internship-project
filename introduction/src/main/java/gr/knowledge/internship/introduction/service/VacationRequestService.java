package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.VacationRequestDTO;
import gr.knowledge.internship.introduction.entity.Employee;
import gr.knowledge.internship.introduction.enums.VacationStatusEnum;
import gr.knowledge.internship.introduction.entity.VacationRequest;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import gr.knowledge.internship.introduction.repository.VacationRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class VacationRequestService {

    private final VacationRequestRepository vacationRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public VacationRequestService(VacationRequestRepository vacationRequestRepository, EmployeeRepository employeeRepository, ModelMapper modelMapper){
        this.vacationRequestRepository = vacationRequestRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
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

        //get the employee entity to retrieve the remaining holidays
        Employee employee = employeeRepository.findById(vacationRequestDTO.getEmployee().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Employee with id: " + vacationRequestDTO.getEmployee().getId() + " not found from createVacationRequest!")
                );

        //calculates the number of days between the dates the employee wants to take a leave
        int daysBetween = (int) ChronoUnit.DAYS.between(vacationRequestDTO.getStartDate(), vacationRequestDTO.getEndDate());

        //check if the remaining vacation days of the employee are enough
        if( daysBetween - vacationRequestDTO.getDays() <= employee.getVacationDays()) {
            vacationRequestDTO.setStatus(VacationStatusEnum.PENDING);
        } else {
            System.out.println("Remaining vacation days not enough!");
            vacationRequestDTO.setStatus(VacationStatusEnum.REJECTED);
        }
        vacationRequestRepository.save(modelMapper.map(vacationRequestDTO, VacationRequest.class));
        return vacationRequestDTO;
    }

    public VacationRequestDTO updateVacationRequest(VacationRequestDTO requestBody, int vacationId){

        VacationRequestDTO vacationRequestDTO = modelMapper.map(vacationRequestRepository.findById(vacationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vacation request with id: " + vacationId + " not found from updateStatusAndVacationDays!")
                ), new TypeToken<VacationRequestDTO>(){}.getType());

        if(requestBody.getStatus() == vacationRequestDTO.getStatus()){  //if the status is the same, then do normal update operation
            requestBody.setId(vacationId);
            vacationRequestRepository.save(modelMapper.map(requestBody, VacationRequest.class));
            return requestBody;
        } else {
            if(requestBody.getStatus() == VacationStatusEnum.ACCEPTED){

                //calculates the number of days between the dates the employee wants to take a leave
                int daysToBeSubtracted = (int) ChronoUnit.DAYS.between(vacationRequestDTO.getStartDate(),
                        vacationRequestDTO.getEndDate()) - vacationRequestDTO.getDays() + 1;

                //get remaining vacation days of the employee
                int vacationDays = vacationRequestDTO.getEmployee().getVacationDays();

                //subtracts the days from the employee's remaining vacation days and change status
                vacationRequestDTO.getEmployee().setVacationDays(vacationDays - daysToBeSubtracted);
                vacationRequestDTO.setStatus(VacationStatusEnum.ACCEPTED);

            } else if (requestBody.getStatus() == VacationStatusEnum.REJECTED)  vacationRequestDTO.setStatus(VacationStatusEnum.REJECTED);

            vacationRequestRepository.save(modelMapper.map(vacationRequestDTO, VacationRequest.class));
            return vacationRequestDTO;
        }
    }

    public boolean deleteVacationRequest(int id){
        vacationRequestRepository.deleteById(id);
        return true;
    }
}
