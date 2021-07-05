package data;

/**
 *
 * @author chvaleriy
 */
public abstract class HeaterMeter implements DataMeter  {
    
    /** Заводской номер сч тепла */
    private String heaterFactoryNum = "";
    private double gKalor = 0.0;
    private String heatTime = "";
    private String heatDate = "";

    public HeaterMeter() {}

     public String getHeaterFactoryNum() {
        return heaterFactoryNum;
    }

    public void setHeaterFactoryNum(String heaterFactoryNum) {
        this.heaterFactoryNum = heaterFactoryNum;
    }

    public double getgKalor() {
        return gKalor;
    }

    public void setgKalor(double gKalor) {
        this.gKalor = gKalor;
    }

    public String getHeatTime() {
        return heatTime;
    }

    public void setHeatTime(String heatTime) {
        this.heatTime = heatTime;
    }

    public String getHeatDate() {
        return heatDate;
    }

    public void setHeatDate(String heatDate) {
        this.heatDate = heatDate;
    }

    
    
    
    
}
