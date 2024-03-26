package gr.knowledge.internship.introduction.enums;


public enum VacationStatusEnum {
    PENDING,
    REJECTED,
    ACCEPTED;
    public String toDbValue() {
        return this.name().toLowerCase();
    }

    public static VacationStatusEnum from(String status) {
        // Note: error if null, error if not "ACTIVE" nor "INACTIVE"
        return VacationStatusEnum.valueOf(status.toUpperCase());
    }

    public static boolean resolveEnum(String status){
        for(VacationStatusEnum vacationStatusEnum : VacationStatusEnum.values()){
            if(vacationStatusEnum.name().equals(status)){
                return true;
            }
        }
        return false;
    }
}
