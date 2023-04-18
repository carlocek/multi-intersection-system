package multiintersection.system;

import java.util.*;

import multiintersection.tramway.TramCrossing;

public class SystemManager 
{
    private static ArrayList<Queue> queueList = new ArrayList<Queue>();
    private static ArrayList<Queue> queueListExit = new ArrayList<Queue>();
	private static ArrayList<RoadSection> roadList = new ArrayList<RoadSection>();
    private static ArrayList<TramCrossing> tramCrossingList = new ArrayList<TramCrossing>();
    private static ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();

    public void addQueue(Queue q)
    {
        queueList.add(q);
    }

    public void addRoad(RoadSection r)
    {
        roadList.add(r);
    }
    
    public void addIntersection(Intersection i)
    {
        intersectionList.add(i);
    }

    public void addTram(TramCrossing t)
    {
        tramCrossingList.add(t);
    }

    public ArrayList<Queue> getQueue()
    {
        return queueList;
    }

    public ArrayList<Intersection> getIntersection()
    {
        return intersectionList;
    }

    public ArrayList<RoadSection> getRoad()
    {
        return roadList;
    }

    public ArrayList<TramCrossing> getTram()
    {
        return tramCrossingList;
    }

    public void addQueueExit(Queue queueExit)
    {
        queueListExit.add(queueExit);
    }

    public ArrayList<Queue> getQueueExit()
    {
        return queueListExit;
    }
}
