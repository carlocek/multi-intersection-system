package multiintersection.analysis;

import multiintersection.semaphore.*;

import java.util.*;

public class BruteForceStrategy implements SemPatternMergeStrategy
{ 
    //takes the list of sets of patterns generated for every intersection and the number of intersections,
    //uses a recursive method to return a list where every element is a list containing a single combination of patterns (one schedule for every intersection)
    public ArrayList<ArrayList<String>> mergePatternsOfAllIntersections(List<Set<SemaphorePattern>> patternsAllIntersections, int nIntersections)
    {
        ArrayList<ArrayList<String>> patternCombinations = new ArrayList<ArrayList<String>>();
        ArrayList<String> singleCombination = new ArrayList<String>();
        recursiveCombinations(patternsAllIntersections, patternCombinations, singleCombination, 0, nIntersections);
        return patternCombinations;
    }

    public void recursiveCombinations(List<Set<SemaphorePattern>> patternsAllIntersections,
           ArrayList<ArrayList<String>> patternCombinations, ArrayList<String> singleCombination, int i, int nIntersections)
    {
        singleCombination.add("z");
        Set<SemaphorePattern> set = patternsAllIntersections.get(i);
        Iterator<SemaphorePattern> itr = set.iterator();
        while(itr.hasNext())
        {
            singleCombination.set(i, itr.next().getSchedule());
            if(singleCombination.size() == nIntersections)
            {
                ArrayList<String> singleCombinationCopy = new ArrayList<String>(singleCombination);
                patternCombinations.add(singleCombinationCopy);
            }
            else
            {
                recursiveCombinations(patternsAllIntersections, patternCombinations, singleCombination, i+1, nIntersections);
                singleCombination.remove(singleCombination.size()-1);
            }
        }
    }
} 

