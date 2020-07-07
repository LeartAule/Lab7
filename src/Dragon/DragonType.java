package Dragon;

public enum DragonType {
    WATER, UNDERGROUND, AIR, FIRE, EARTH, ICE, CHAOS, UNKNOWN, VOID;

    public static DragonType byOrdinal(String s){
        try {
            DragonType dragonType = DragonType.valueOf(s);
            return dragonType;

        }catch (IllegalArgumentException e){
            return DragonType.UNKNOWN;
        }
    }


}
/**
 *UNKNOWN используется в случае неправильного ввода всех остлаьных enum-ов
 */