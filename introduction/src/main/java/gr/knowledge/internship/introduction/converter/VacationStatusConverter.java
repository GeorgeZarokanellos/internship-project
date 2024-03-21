package gr.knowledge.internship.introduction.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import gr.knowledge.internship.introduction.enums.VacationStatusEnum;

@Converter(autoApply = true)
public class VacationStatusConverter implements AttributeConverter<VacationStatusEnum, String> {
    @Override
    public String convertToDatabaseColumn(VacationStatusEnum status) {
        return status.toDbValue();
    }

    @Override
    public VacationStatusEnum convertToEntityAttribute(String dbData) {
        return VacationStatusEnum.from(dbData);
    }
}
