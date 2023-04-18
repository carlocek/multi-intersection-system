package multiintersection.analysis;

import java.util.*;

public interface SemPatternEvaluationStrategy 
{
    public double evaluatePattern(ArrayList<ArrayList<Double>> totCarsInQueue, ArrayList<Integer> queuesSize);
}
