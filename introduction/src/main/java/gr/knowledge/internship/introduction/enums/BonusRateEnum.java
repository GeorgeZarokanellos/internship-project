package gr.knowledge.internship.introduction.enums;

import lombok.Getter;

@Getter
public enum BonusRateEnum {
    //capitals
    WINTERBONUS("winter", 1.3),
    AUTUMNBONUS("autumn", 0.4),
    SPRINGBONUS("spring", 0.6),
    SUMMERBONUS("summer", 0.7);

    private final String season;
    private final Double rate;

    BonusRateEnum(String season, Double rate){
        this.season = season;
        this.rate = rate;
    }
}
