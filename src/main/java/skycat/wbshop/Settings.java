package skycat.wbshop;

public class Settings {
    public double POINTS_PER_BLOCK;
    public static Settings DEFAULT_SETTINGS = new Settings(3.0);


    public Settings(double POINTS_PER_BLOCK) {
        this.POINTS_PER_BLOCK = POINTS_PER_BLOCK;
    }
}
