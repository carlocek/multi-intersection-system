package multiintersection.system;

public class RoadSection 
{
    private String roadSectionId;
    private double length;
    private static final int vehicleLength = 3;
    private static final int minimumDistance = 5;
    private double speedLimit;

    public RoadSection(String roadId,double l, double sl)
    {
        this.length = l;
        this.speedLimit = sl;
        this.roadSectionId=roadId;
    }

    public String getId()
    {
        return this.roadSectionId;
    }
 
    public double getLength()
    {
        return this.length;
    }
    public int getVehicleLength()
    {
        return vehicleLength;
    }
    public int getMinimumDistance()
    {
        return minimumDistance;
    }
    public double getSpeedLimit()
    {
        return this.speedLimit;
    }
}