package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.BonusDTO;
import gr.knowledge.internship.introduction.entity.Bonus;
import gr.knowledge.internship.introduction.enums.BonusRateEnum;
import gr.knowledge.internship.introduction.entity.Employee;
import gr.knowledge.internship.introduction.repository.BonusRepository;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Log4j2
public class BonusService {

    private final BonusRepository bonusRepository;
    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public BonusService(BonusRepository bonusRepository, EmployeeRepository employeeRepository , ModelMapper modelMapper){
        this.bonusRepository = bonusRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }
    @Transactional(readOnly = true)
    public List<BonusDTO> getBonuses(){
        List<Bonus> bonusList = bonusRepository.findAll();
        return modelMapper.map(bonusList, new TypeToken<List<BonusDTO>>(){}.getType());
    }

    @Transactional(readOnly = true)
    public BonusDTO getBonusById(int id){
        Bonus bonus = bonusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bonus with id: " + id + " not found"));
        return modelMapper.map(bonus, new TypeToken<BonusDTO>(){}.getType());
    }

    public BonusDTO createBonus(BonusDTO bonusDTO){

        Bonus bonus = new Bonus();
        //get the employee entity to retrieve the salary
        Employee employee = employeeRepository.getReferenceById(bonusDTO.getEmployeeId());
        //calculate the bonus amount
        double amountAfterBonus = calcBonus(employee.getSalary(), "autumn");
        //set the bonus amount to the DTO
        bonusDTO.setAmount(amountAfterBonus);
        //map the DTO to the entity
        modelMapper.map(bonusDTO, bonus);

        bonusRepository.save(bonus);
        return bonusDTO;
    }

    public BonusDTO updateBonus(BonusDTO bonusDTO, int id){
        bonusDTO.setId(id);
        bonusRepository.save(modelMapper.map(bonusDTO, Bonus.class));
        return bonusDTO;
    }

    public boolean deleteBonus(int id){
        bonusRepository.deleteById(id);
        return true;
    }

    public double calcBonus(double salary, String season){
        if(!BonusRateEnum.resolveEnum(season))
            throw new IllegalArgumentException("Invalid season: " + season + " from calcBonus!");

        double rate = 0;
        //get the bonus rate for the specific season
        for (BonusRateEnum entry: BonusRateEnum.values()) {
            //if the season is the same as the one in the enum
            if(entry.getSeason().equals(season))
                rate = entry.getRate();
        }
        return salary * rate;
    }
}
