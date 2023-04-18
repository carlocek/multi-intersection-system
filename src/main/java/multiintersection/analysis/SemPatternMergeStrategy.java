package multiintersection.analysis;

import multiintersection.semaphore.*;

import java.util.*;

public interface SemPatternMergeStrategy 
{
    public ArrayList<ArrayList<String>> mergePatternsOfAllIntersections(List<Set<SemaphorePattern>> patternsAllIntersections, int nIntersections);
    //public ArrayList<ArrayList<String>> mergePatternsOfAllIntersections(List<Set<String>> patternsAllIntersections, int nIntersections);
}
