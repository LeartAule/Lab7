package Dragon;

/**
 * Класс Color
 */
public enum Color {
    RED, YELLOW, WHITE, BLUE, GREEN, PURPLE, CYAN, UNKNOWN;


    public static Color byOrdinal(String s){
        try {
            Color color = Color.valueOf(s);
            return color;

        }catch (IllegalArgumentException e){
            return Color.UNKNOWN;
        }
    }


}
