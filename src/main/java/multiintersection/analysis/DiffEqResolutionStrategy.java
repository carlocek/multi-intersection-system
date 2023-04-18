package multiintersection.analysis;

import multiintersection.system.Queue;

import java.util.*;

public interface DiffEqResolutionStrategy 
{
    public void solve(int tIndex, ArrayList<Double> y, double timeStep, double timeLimit, Queue q, ArrayList<String> singleCombination);
}