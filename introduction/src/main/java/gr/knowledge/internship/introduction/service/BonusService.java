package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.BonusDTO;
import gr.knowledge.internship.introduction.entity.Bonus;
import gr.knowledge.internship.introduction.enums.BonusRateEnum;
import gr.knowledge.internship.introduction.entity.Employee;
import gr.knowledge.internship.introduction.repository.BonusRepository;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
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
    public List<BonusDTO> getBonuses(){
        List<Bonus> bonusList = bonusRepository.findAll();
        return modelMapper.map(bonusList, new TypeToken<List<BonusDTO>>(){}.getType());
    }

    public BonusDTO getBonusById(int id){
        Bonus bonus = bonusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bonus with id: " + id + " not found"));
        return modelMapper.map(bonus, new TypeToken<BonusDTO>(){}.getType());
    }

    public BonusDTO createBonus(BonusDTO bonusDTO){
        Bonus bonus = new Bonus();
        Employee employee = employeeRepository.getReferenceById(bonusDTO.getEmployeeId());
        double amountAfterBonus = calcBonus(employee.getSalary(), "autumn");
        bonusDTO.setAmount(amountAfterBonus);
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
        double rate = 0;
        for (BonusRateEnum entry: BonusRateEnum.values()) {
            if(entry.getSeason().equals(season))
                rate = entry.getRate();
        }
        return salary * rate;
    }
}
