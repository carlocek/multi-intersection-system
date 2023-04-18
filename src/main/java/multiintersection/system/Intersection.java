package multiintersection.system;

import multiintersection.tramway.TramCrossing;

import java.util.ArrayList;

public class Intersection 
{
    private String intersectionId;
    private double length;
    private double trafficLightPeriod;
    private ArrayList<RoadSection> sections = new ArrayList<RoadSection>();
    private ArrayList<TramCrossing> tramsCrossing = new ArrayList<TramCrossing>();
    private ArrayList<Queue> queues = new ArrayList<Queue>();
    private ArrayList<Queue> queuesSimultaneousGreen = new ArrayList<Queue>();

    public Intersection(String intersectionId, double length, double trafficLightPeriod, ArrayList<RoadSection> sections, ArrayList<Queue> queues, ArrayList<Queue> queuesSimultaneousGreen)
    {
        this.intersectionId = intersectionId;
        this.length = length;
        this.queues = queues;
        this.sections = sections;
        this.trafficLightPeriod = trafficLightPeriod;
    }

    public double getLength()
    {
        return this.length;
    }

    public double getTrafficLightPeriod()
    {
        return this.trafficLightPeriod;
    }

    //determines if at time t the semaphore for the queue q is red (0) or green (1)
    public int getTrafficLightPattern(Queue q, double t, ArrayList<String> singleCombination)
    {
        //if there's no list of patterns, uses the number of queues involved in the intersection
        //and the index of the queue in exam in the array "queues" of the json to determine the temporal interval for the green.
        //if the queue in exam must have the green simultaneously with another queue it checks if the queue in exam is not the
        //first occurrence of the "simultaneous green queues" in the queues array and in case it adjusts the qIndex
        //NOTE: green parallelism is permitted between no more than two queues per intersection,
        //in the json the "simultaneous green queues" must be adjacent in the array "queues"
        if(singleCombination == null)
        {
            double periodSlice = this.trafficLightPeriod/(this.queues.size() - this.queuesSimultaneousGreen.size() + 1);
            int qIndex = this.queues.indexOf(q);
            if(this.queuesSimultaneousGreen.contains(q) && this.queuesSimultaneousGreen.contains(this.queues.get(qIndex-1)))
            {
                qIndex--;
            }
            return periodSlice*qIndex < t%this.trafficLightPeriod && t%this.trafficLightPeriod < periodSlice*(qIndex+1) ? 1 : 0;
        }
        //if there's the list of patterns, uses the intersection id to determine the pattern of interest in the list,
        //confronts the queue's semaphore id and the id at position t in the schedule to determine red or green
        //(it uses the semaphore id of the queue because it keeps track of the queues that must have simultaneous green)
        else
        {
            String schedule = singleCombination.get(Integer.parseInt(this.intersectionId)-1);
            int qId = Integer.parseInt(q.getSemaphoreId());
            char c = schedule.charAt((int)(t%this.trafficLightPeriod));
            int qIdSchedule = Character.getNumericValue(c);  
            return qId == qIdSchedule ? 1 : 0;
        }
    }

    public String getIntersectionId()
    {
        return this.intersectionId;
    }

    public ArrayList<RoadSection> getSections()
    {
        return this.sections;
    }

    public void setTramCrossing(TramCrossing tramCrossing)
    {
        this.tramsCrossing.add(tramCrossing);
    }

    public ArrayList<TramCrossing> getTramCrossing()
    {
        return this.tramsCrossing;
    }

    public double getTramAvailability(int tIndex)
    {
        double p = 1;
        for(TramCrossing t : this.tramsCrossing)
        {
            p *= t.getAvailability(tIndex);
        }
        return p;
    }

    public ArrayList<Queue> getQueues()
    {
        return this.queues;
    }
}
