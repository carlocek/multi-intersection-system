package multiintersection.analysis;

import java.util.*;

public class PercentageCarsInQueueStrategy implements SemPatternEvaluationStrategy
{
    //it iterates over the carsInQueue arrays of all the queues and for each one computes the percentage of cars enqueued
    //then it assigns a score to the entire pattern in exam by taking the average percentage of the percentages computed
    public double evaluatePattern(ArrayList<ArrayList<Double>> totCarsInQueue,  ArrayList<Integer> queuesSize)
    {
        ArrayList<Double> percentageOfQueues = new ArrayList<Double>();
        int counter = 0;
        for(ArrayList<Double> carsInQueue : totCarsInQueue)
        {
            int size = queuesSize.get(counter);
            double sum = carsInQueue.stream().mapToDouble(a -> a).sum(); 
            double percentage = (sum/(size*carsInQueue.size()))*100;
            percentageOfQueues.add(percentage);
            counter += 1;
        }
        double averagePercentage = (percentageOfQueues.stream().mapToDouble(a -> a).sum())/(percentageOfQueues.size());
        return averagePercentage;
    }
}
