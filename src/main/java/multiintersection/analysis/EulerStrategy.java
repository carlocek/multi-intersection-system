package multiintersection.analysis;

import multiintersection.system.Queue;

import java.util.*;

//solves the diff equations system using euler method
public class EulerStrategy implements DiffEqResolutionStrategy
{
    public void solve(int tIndex, ArrayList<Double> y, double timeStep, double timeLimit, Queue q, ArrayList<String> singleCombination)
    {
        ArrayList<Double> newColumn = new ArrayList<Double>();
        for(int i = 0; i < y.size(); i++)
        {
            if(i == 0)
            {
                newColumn.add((y.get(i) + timeStep * func1(i, y, tIndex, q, timeStep, singleCombination)));
            }
            else if(i == y.size()-1)
            {
                newColumn.add(y.get(i) + timeStep * func3(i, y, tIndex, q, timeStep, singleCombination));
            }
            else
            {
                newColumn.add(y.get(i) + timeStep * func2(i, y, tIndex, q, timeStep, singleCombination));
            }
        }
        //at the end of every iteration over the possible states of the queue appends the computed column to the stateProbMatrix
        q.updateStateProbMatrix(newColumn);
    }

    public double func1(int i, ArrayList<Double> y, int tIndex, Queue q, double timeStep, ArrayList<String> singleCombination)
    {
        return (y.get(i+1) * q.depFromQueue(tIndex, timeStep, singleCombination)) - (y.get(i) * q.arrToQueue(tIndex, timeStep, singleCombination));
    }

    public double func2(int i, ArrayList<Double> y, int tIndex, Queue q, double timeStep, ArrayList<String> singleCombination)
    {
        return (-y.get(i) * q.depFromQueue(tIndex, timeStep, singleCombination)) - (y.get(i) * q.arrToQueue(tIndex, timeStep, singleCombination)) + (y.get(i+1) * q.depFromQueue(tIndex, timeStep, singleCombination)) + (y.get(i-1) * q.arrToQueue(tIndex, timeStep, singleCombination));
    }

    public double func3(int i, ArrayList<Double> y, int tIndex, Queue q, double timeStep, ArrayList<String> singleCombination)
    {
        return (y.get(i-1) * q.arrToQueue(tIndex, timeStep, singleCombination)) - (y.get(i) * q.depFromQueue(tIndex, timeStep, singleCombination));
    }
}
