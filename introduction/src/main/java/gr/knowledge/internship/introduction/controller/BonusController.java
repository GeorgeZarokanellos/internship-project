package gr.knowledge.internship.introduction.controller;

import gr.knowledge.internship.introduction.dto.BonusDTO;
import gr.knowledge.internship.introduction.service.BonusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bonuses")
@CrossOrigin
public class BonusController {
    private final BonusService bonusService;

    public BonusController(BonusService bonusService){
        this.bonusService = bonusService;
    }

    @GetMapping("/")
    public List<BonusDTO> getBonuses(){ return bonusService.getBonuses(); }

    @GetMapping("/{id}")
    public BonusDTO getBonusById(@PathVariable int id) { return bonusService.getBonusById(id); }

    @PostMapping("/")
    public BonusDTO createBonus(@RequestBody BonusDTO bonusDTO) { return bonusService.createBonus(bonusDTO); }

    @PutMapping("/{id}")
    public BonusDTO updateBonus(@RequestBody BonusDTO bonusDTO, @PathVariable int id) {
        return bonusService.updateBonus(bonusDTO, id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteBonus(@PathVariable int id){
        return bonusService.deleteBonus(id);

    }
}
