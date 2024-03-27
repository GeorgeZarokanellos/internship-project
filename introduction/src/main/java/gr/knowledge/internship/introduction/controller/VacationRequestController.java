package gr.knowledge.internship.introduction.controller;

import gr.knowledge.internship.introduction.dto.VacationRequestDTO;
import gr.knowledge.internship.introduction.service.VacationRequestService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/vacation-requests")
public class VacationRequestController {
    private final VacationRequestService vacationRequestService;

    @Autowired
    public VacationRequestController(VacationRequestService vacationRequestService){ this.vacationRequestService = vacationRequestService; }

    @GetMapping
    public List<VacationRequestDTO> getVacationRequests(){
        return vacationRequestService.getVacationRequests();
    }

    @GetMapping("/{id}")
    public VacationRequestDTO getVacationRequestById(@PathVariable int id) {
        return vacationRequestService.getVacationRequestById(id);
    }

    @PostMapping
    public VacationRequestDTO createVacationRequest(@RequestBody VacationRequestDTO requestBody){
        return vacationRequestService.createVacationRequest(requestBody);
    }

    @PutMapping("/{id}")
    public VacationRequestDTO updateVacationRequest(@RequestBody VacationRequestDTO vacationRequestDTO, @PathVariable int id){
        return vacationRequestService.updateVacationRequest(vacationRequestDTO, id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteVacationRequest(@PathVariable int id){
        return vacationRequestService.deleteVacationRequest(id);
    }
}
