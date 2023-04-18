package multiintersection.analysis;

import multiintersection.semaphore.*;
import multiintersection.system.Queue;

import java.io.IOException;
import java.util.*;
import com.github.sh0nk.matplotlib4j.*;

public class Analyser 
{
    private double timeStep;
    private double timeLimit;
    private DiffEqResolutionStrategy resolutionStrategy;
    private SemPatternMergeStrategy mergeStrategy;
    private SemPatternEvaluationStrategy evaluationStrategy;
    private ArrayList<Queue> allQueues;
    private List<Set<SemaphorePattern>> patternsAllIntersections;

    public Analyser(double timeStep, double timeLimit, DiffEqResolutionStrategy resolutionStrategy, SemPatternMergeStrategy mergeStrategy, SemPatternEvaluationStrategy evaluationStrategy, ArrayList<Queue> allQueues, List<Set<SemaphorePattern>> patternsAllIntersections)
    {
        this.timeStep = timeStep;
        this.timeLimit = timeLimit;
        this.resolutionStrategy = resolutionStrategy;
        this.mergeStrategy = mergeStrategy;
        this.evaluationStrategy = evaluationStrategy;
        this.allQueues = allQueues;
        this.patternsAllIntersections = patternsAllIntersections;
    }

    //computes the stateProbMatrixes for every queue and plots the mean number of cars in each queue for every istant until timeLimit
    public void analyseCarsInQueue(double t0, int tIndexLimit, ArrayList<String> singleCombination) throws IOException, PythonExecutionException
    {
        computeStateProbMatrixes(t0, singleCombination);
        List<Double> x = NumpyUtils.linspace(0, this.timeLimit, tIndexLimit);
        Plot plt = Plot.create();
        for(Queue q : this.allQueues)
        {
            ArrayList<Double> carsInQueue = carsInQueue(q, tIndexLimit);
            plt.plot().add(x, carsInQueue).linestyle("-").linewidth(1.2).label("Q"+q.getQueueId());
        }
        plt.xlabel("time (s)");
        plt.ylabel("cars in queue");
        plt.legend().loc("upper right");
        plt.show();

        //to save the plot
        /* plt.savefig("C:/Users/carlo/Desktop/plotDalmaziaTramBestPattern.png").dpi(300);
        plt.executeSilently(); */
    }    

    //computes all the combinations of patterns of every intersection,
    //for every combination executes the analysis of the mean number of cars enqueued
    //and evaluates the score of the single combination, finding the best one
    public void analysePatternEvaluation(double t0, int tIndexLimit, int nIntersections) throws IOException, PythonExecutionException
    {
        ArrayList<ArrayList<String>> patternCombinations = this.mergeStrategy.mergePatternsOfAllIntersections(this.patternsAllIntersections, nIntersections);
        System.out.println("The total number of combinations of patterns for all the intersections is: " + patternCombinations.size());
        double currentPercentage;
        double bestPercentage = 100;
        ArrayList<String> bestPattern = new ArrayList<String>();
        ArrayList<Integer> queuesSize = new ArrayList<Integer>();
        for(Queue q : this.allQueues)
        {
            queuesSize.add(q.getSize());
        }
        for(ArrayList<String> currentPattern : patternCombinations)
        {
            computeStateProbMatrixes(t0, currentPattern);
            ArrayList<ArrayList<Double>> totCarsInQueue = new ArrayList<ArrayList<Double>>();
            for(Queue q : this.allQueues)
            {
                ArrayList<Double> carsInQueue = carsInQueue(q, tIndexLimit);
                totCarsInQueue.add(carsInQueue);
                //at the end of every iteration resets the stateProbMatrixes of every queue
                q.resetStateProbmatrix();
            }
            currentPercentage = evaluationStrategy.evaluatePattern(totCarsInQueue, queuesSize);
            //System.out.println(currentPercentage);
            if(currentPercentage < bestPercentage)
            {
                bestPercentage = currentPercentage;
                bestPattern = currentPattern;
            }  
        }
        System.out.println("The best combination of patterns is: " + bestPattern);
        this.analyseCarsInQueue(t0, tIndexLimit, bestPattern);
    }

    //resolves the diff equations for every queue and updates every stateProbMarix
    public void computeStateProbMatrixes(double t0, ArrayList<String> singleCombination)
    {
        int tIndex = 0;
        while(t0 < this.timeLimit)
        {
            for(Queue q: allQueues)
            {
                this.resolutionStrategy.solve(tIndex, q.getStateProbMatrix().get(tIndex), this.timeStep, this.timeLimit, q, singleCombination);
            }
            tIndex++;
            t0 += this.timeStep;
        }  
    }

    //computes the mean number of cars in every queue for every istant until timeLimit
    public ArrayList<Double> carsInQueue(Queue q, int tIndexLimit)
    {
        ArrayList<Double> carsInQueue = new ArrayList<Double>();
        int tIndex = 0;
        while(tIndex < tIndexLimit)
        {
            double sum1 = 0;
            for(int k = 0; k < q.getStateProbMatrix().get(tIndex).size(); k++)
            {
                double sum2 = 0; 
                for(int j = 0; j < q.getStateProbMatrix().get(tIndex).size(); j++)
                {
                    sum2 += (j*q.getStateProbMatrix().get(tIndex).get(j));
                }
                sum1 += (q.getStateProbMatrix().get(0).get(k)*sum2);
            }
            carsInQueue.add(sum1);
            tIndex++;
        }
        return carsInQueue;
    }
}
