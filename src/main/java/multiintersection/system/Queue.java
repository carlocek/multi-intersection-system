package multiintersection.system;

import java.util.*;

public class Queue
{
    private String queueId;
    private String semaphoreId;
    private Intersection intersection;
    private boolean hasTram;
    private ArrayList<ArrayList<Double>> stateProbMatrix= new ArrayList<ArrayList<Double>>();
    private double poissonArrivalRate;
    private double serviceRate;
    private int size;
    private ArrayList<String> inputQueuesId = new ArrayList<String>(); 
    private ArrayList<Queue> inQueues = new ArrayList<Queue>();
    private HashMap<String,String> outQueuesId = new HashMap<String,String>();
    private HashMap<Queue,Double> outQueues = new HashMap<Queue,Double>();

    public Queue(String queueId, String semaphoreId, boolean hasTram, int size, double poissonArrivalRate, double serviceRate, ArrayList<String> inputQueuesId, ArrayList<ArrayList<Double>> stateProbMatrix, HashMap<String,String> outQueuesId)
    {
        this.queueId = queueId;
        this.semaphoreId = semaphoreId;
        this.hasTram = hasTram;
        this.stateProbMatrix = stateProbMatrix;
        this.poissonArrivalRate = poissonArrivalRate;
        this.serviceRate = serviceRate;
        this.size = size;
        this.inputQueuesId = inputQueuesId;
        this.outQueuesId = outQueuesId;
    }

    public void inQueues(Queue inQueue)
    {
        this.inQueues.add(inQueue);
    }

    public ArrayList<String> getInputQueuesId()
    {
        return this.inputQueuesId;
    }

    public void outQueues(HashMap<Queue,Double> outQueues)
    {
        this.outQueues = outQueues;
    }

    public HashMap<String,String> getOutQueuesId()
    {
        return this.outQueuesId;
    }

    public void setIntersection(Intersection intersection)
    {
        this.intersection = intersection;
    }

    public Intersection getIntersection()
    {
        return this.intersection;
    }

    public String getQueueId()
    {
        return this.queueId;
    }

    public String getSemaphoreId()
    {
        return this.semaphoreId;
    }
    
    public int getSize()
    {
        return this.size;
    }

    public double getBlockingProb(int t)
    {
        ArrayList<Double> col = this.stateProbMatrix.get(t);
        return col.get(col.size()-1);
    }

    public double getServiceRate()
    {
        return this.serviceRate;
    }

    public ArrayList<ArrayList<Double>> getStateProbMatrix()
    {
        return this.stateProbMatrix;
    }

    public Double getForwardingProbs(Queue d)
    {
        Double weight = this.outQueues.get(d);
        return weight;
    }

    public void updateStateProbMatrix(ArrayList<Double> newColumn)
    {
        this.stateProbMatrix.add(newColumn);
    }

    //reset the stateProbMatrix leaving the first column which represents the initial distribution
    public void resetStateProbmatrix()
    {
        this.stateProbMatrix.subList(1, this.stateProbMatrix.size()).clear();
    }

    //implements the prob of having only one departure from the queue
    public double depFromQueue(int tIndex, double timeStep, ArrayList<String> singleCombination) 
    {
        double sum = 0;
        for(int i = 0; i < this.outQueues.keySet().toArray().length; i++)
        {
            double prod = 1;
            for(int j = 0; j < this.outQueues.keySet().toArray().length; j++)
            {                
                if(j != i)
                {
                    prod *= (1-depFromQueueToDest((Queue)this.outQueues.keySet().toArray()[j], tIndex, timeStep, singleCombination));
                }
            }
            sum += (this.depFromQueueToDest((Queue)this.outQueues.keySet().toArray()[i], tIndex, timeStep, singleCombination)*prod);
        }
        return sum;
    }
    
    //implements the prob of having only one departure from the queue to a destination queue d
    public double depFromQueueToDest(Queue d, int tIndex, double timeStep, ArrayList<String> singleCombination) 
    {
        if(this.hasTram)
        {
            //if the destination is an exit we don't consider the blocking prob
            if(d.getQueueId().equals("uscita"))
            {
                //computes the availability of the intersection at time t combining the traffic light pattern with the tram availabilty
                double totAvailability = this.intersection.getTrafficLightPattern(this, tIndex*timeStep, singleCombination) * this.intersection.getTramAvailability(tIndex);
                return this.getServiceRate() * this.getForwardingProbs(d) * totAvailability;
            }
            else
            {
                double totAvailability = this.intersection.getTrafficLightPattern(this, tIndex*timeStep, singleCombination) * this.intersection.getTramAvailability(tIndex);
                return this.getServiceRate() * this.getForwardingProbs(d) * totAvailability * (1-d.getBlockingProb(tIndex));
            }
        }
        else
        {
            if(d.getQueueId().equals("uscita"))
            {
                //computes the availability of the intersection at time t combining the traffic light pattern with the tram availabilty
                double totAvailability = this.intersection.getTrafficLightPattern(this, tIndex*timeStep, singleCombination);
                return this.getServiceRate() * this.getForwardingProbs(d) * totAvailability;
            }
            else
            {
                double totAvailability = this.intersection.getTrafficLightPattern(this, tIndex*timeStep, singleCombination);
                return this.getServiceRate() * this.getForwardingProbs(d) * totAvailability * (1-d.getBlockingProb(tIndex));
            }
        }
    }

    //implements the prob of having only one arrival to the queue
    public double arrToQueue(int t0, double timeStep, ArrayList<String> singleCombination)
    {
        if(this.inQueues.size() == 0)
        {
            return this.poissonArrivalRate;
        }
        else
        {
            double sum = 0;   
            for(int i = 0; i < this.inQueues.size(); i++)
            {
                double prod = 1;
                for(int j = 0; j < this.inQueues.size(); j++)
                {
                    if(j != i)
                    {
                        prod *= (1-arrToQueueFromOrig(this.inQueues.get(j), t0, timeStep, singleCombination));
                    }
                }
                sum += (arrToQueueFromOrig(this.inQueues.get(i), t0, timeStep, singleCombination)*prod);
            }
            return sum;
        }
    }
    
    //implements the prob of having only one arrival to the queue from an origin o
    public double arrToQueueFromOrig(Queue o, int tIndex, double timeStep, ArrayList<String> singleCombination) 
    {
        //separates the case of the origin being a poisson process or a real queue
        if(this.poissonArrivalRate == 0)
        {
            double sum = 0;
            for(int s = 0; s < o.getStateProbMatrix().get(tIndex).size(); s++)
            {
                sum += (o.depFromQueueToDest(this, tIndex, timeStep, singleCombination) * o.getStateProbMatrix().get(tIndex).get(s));
            }
            return sum;
        }
        else
        {
            return this.poissonArrivalRate;
        }
    }
}
