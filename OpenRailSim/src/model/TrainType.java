package model;

/**
 * A type of train that is used in service
 * @author 100128483
 */
public class TrainType {
    private enum TractionType {
        Electric, Diesel, Hybrid;        
    }
    
    private final String TRN_CLASS;
    private final boolean MULTI_UNIT;
    private final int CAR_COUNT;
    private final double CAR_LENGTH; // in metres
    private final double ACC; // in m/s/s
    private final double DEC; // in -m/s/s
    private final double MAX_TRAC_EFFORT; // in kN (kilo newtons)
    private final double CONT_TRAC_EFFORT; // in kN (kilo newtons)
    private final double MAX_SPEED; // in km/h
    private final double WEIGHT; // in tonnes (1000 KG)
    private final TractionType TRAC_TYPE;
    
    
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    
    /**
     * Main constructor, requires all parameters
     * @param trnClass
     * @param multiUnit
     * @param carrCount
     * @param length
     * @param acc
     * @param dec
     * @param maxSpeed
     * @param weight
     * @param type
     * @throws AttributeOutOfRangeException 
     */
    public TrainType(String trnClass, boolean multiUnit, int carrCount, double length, double acc, double dec, double maxSpeed, double weight, int type) 
     throws AttributeOutOfRangeException {
        try {
            this.TRN_CLASS = trnClass;
            this.MULTI_UNIT = multiUnit;
            this.CAR_COUNT = carrCount;
            this.CAR_LENGTH = length;
            this.ACC = acc;
            this.DEC = dec;
            this.MAX_SPEED = maxSpeed;
            this.WEIGHT = weight;
            this.TRAC_TYPE = TractionType.values()[type];
            
            this.MAX_TRAC_EFFORT = 0;
            this.CONT_TRAC_EFFORT = 0;
        } catch(Exception e) {
            throw new AttributeOutOfRangeException("Train type invalid");
        }
    }
    
    /**
     * Default constructor
     */
    public TrainType() {
        this.TRN_CLASS = "Default";
        this.MULTI_UNIT = false;
        this.CAR_COUNT = 0;
        this.CAR_LENGTH = 0;
        this.MAX_TRAC_EFFORT = 0;
        this.CONT_TRAC_EFFORT = 0;
        this.ACC = 0;
        this.DEC = 0;
        this.MAX_SPEED = 0;
        this.WEIGHT = 0;
        this.TRAC_TYPE = TractionType.Electric;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Getters">

    /**
     * @return train class
     */
    
    public String getTrnClass() { return this.TRN_CLASS; }
    
    /**
     * @return whether the train is a multiple unit
     */
    public boolean isMU() { return this.MULTI_UNIT; }
    
    /**
     * @return number of cars on the train
     */
    public int getCarCount() { return this.CAR_COUNT; }
    
    /**
     * @return length of the train
     */
    public double getLength() { return this.CAR_LENGTH; }
        
    /**
     * @return maximum tractive effort
     */
    public double getMaxTracEffort() { return this.MAX_TRAC_EFFORT; }
    
    /**
     * @return continuous tractive effort
     */
    public double getContTracEffort() { return this.CONT_TRAC_EFFORT; }
        
    /**
     * @return top speed of the train
     */
    public double getMaxSpeed() { return this.MAX_SPEED; }
    
    /**
     * @return weight of the train
     */
    public double getWeight() { return this.WEIGHT; }
    
    /**
     * @return if the train is electric
     */
    public boolean isElectric() { return this.TRAC_TYPE.equals(TractionType.Electric); }

    /**
     * @return if the train is diesel
     */
    public boolean isDiesel() { return this.TRAC_TYPE.equals(TractionType.Diesel); }

    /**
     * @return if the train is hybrid
     */
    public boolean isHybrid() { return this.TRAC_TYPE.equals(TractionType.Hybrid); }
    

    //Acceleration

    /**
     * @param w
     * @return acceleration, given the weight
     */
    public double getAcc(double w) { //m/s^2
        return (this.MAX_TRAC_EFFORT*1000) / w;
    }

    /**
     * @return acceleration rate
     */
    public double getAcc() {
        if(this.ACC != 0)
            return this.ACC;
        else
            return getAcc(this.WEIGHT);
    }
    
    /**
     * @return deceleration rate
     */
    public double getDec() { return this.DEC; }

    // </editor-fold>

    /**
     * @return test type to use for testing
     */
    public static TrainType test() {
        try {
            return new TrainType("Class 90", false, 12, 18.75, 1, 2, 177, 84.5, 1);
        } catch(AttributeOutOfRangeException e) {
            System.out.println("Something went wrong!");
            return null;
        }
    }
}
