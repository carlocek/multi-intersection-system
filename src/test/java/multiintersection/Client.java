package multiintersection;

//import static org.junit.jupiter.api.DynamicTest.stream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import com.github.sh0nk.matplotlib4j.*;

import multiintersection.analysis.*;
import multiintersection.building.*;
import multiintersection.system.SystemManager;
import multiintersection.system.Queue;
import multiintersection.tramway.TramCrossing;
import multiintersection.tramway.analysis.*;
import multiintersection.semaphore.*;

public class Client
{
    public static final BigDecimal timeStepTram = new BigDecimal("0.1");
    public static void main(String[] args) throws ParseException, FileNotFoundException, IOException, PythonExecutionException 
    {
        //reads json
        JSONParser parser = new JSONParser();
		Reader reader = new FileReader("src/main/java/multiintersection/building/configurationDalmazia.json");
        //Reader reader = new FileReader("src/main/java/multiintersection/building/configurationTest.json");
		Object obj = parser.parse(reader);
		JSONObject jsonObj = (JSONObject) obj;

        //builds the system and initialize the system manager
        Director<JSONObject> director = new DirectorJSON();
        SystemManager systemManager = director.construct(jsonObj);
        ArrayList<Queue> allQueues = systemManager.getQueue();
        ArrayList<TramCrossing> allTramCrossing = systemManager.getTram();
        
        //calls the analysis on all the tram crossing in the system
        for(TramCrossing tc : allTramCrossing)
        {
            tc.analyze(new ParallelGreenProbabilityVisitor(), timeStepTram);
        } 
        
        //sets the strategies used in the analyses
        DiffEqResolutionStrategy resolutionStrategy = new EulerStrategy();
        SemPatternMergeStrategy mergeStrategy = new BruteForceStrategy();
        SemPatternEvaluationStrategy evaluationStrategy = new PercentageCarsInQueueStrategy();

        //gets the set of sem patterns for every intersection
        //intersection 1
        List<Integer> greenSlotsFlow1Int1 = new ArrayList<Integer>(List.of(20, 30));
        List<Integer> greenSlotsFlow2Int1 = new ArrayList<Integer>(List.of(15, 20));
        VehicleFlow flow1Int1 = new VehicleFlow("1", greenSlotsFlow1Int1);
        VehicleFlow flow2Int1 = new VehicleFlow("2", greenSlotsFlow2Int1);
        List<VehicleFlow> flowsInt1 = new ArrayList<VehicleFlow>(List.of(flow1Int1, flow2Int1));
        Set<SemaphorePattern> patternsInt1 = SemPatternGenerator.generateAllPatternWithGreenSlotSets(flowsInt1, 220, 5);
        System.out.println("The total number of patterns for the first intersection is: " + patternsInt1.size());
        //intersection 2
        List<Integer> greenSlotsFlow1Int2 = new ArrayList<Integer>(List.of(15, 30));
        List<Integer> greenSlotsFlow2Int2 = new ArrayList<Integer>(List.of(15, 20));
        VehicleFlow flow1Int2 = new VehicleFlow("3", greenSlotsFlow1Int2);
        VehicleFlow flow2Int2 = new VehicleFlow("4", greenSlotsFlow2Int2);
        List<VehicleFlow> flowsInt2 = new ArrayList<VehicleFlow>(List.of(flow1Int2, flow2Int2));
        Set<SemaphorePattern> patternsInt2 = SemPatternGenerator.generateAllPatternWithGreenSlotSets(flowsInt2, 220, 5);
        System.out.println("The total number of patterns for the second intersection is: " + patternsInt2.size());
        //adds the sets of sem patterns generated for every intersection into a list
        List<Set<SemaphorePattern>> patternsAllIntersections = new ArrayList<Set<SemaphorePattern>>(List.of(patternsInt1, patternsInt2));

        //injects the analysis parameters into the Analyser
        Analyser analyser = new Analyser(0.1, 440, resolutionStrategy, mergeStrategy, evaluationStrategy, allQueues, patternsAllIntersections);

        //calls the analysis for the mean number of cars in each queue
        analyser.analyseCarsInQueue(0, 4400, null);
        
        //calls the analysis for the evaluation of the best pattern
        long start = System.currentTimeMillis();
        //analyser.analysePatternEvaluation(0, 4400, 2);
        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + ((end-start)/1000)/60 + " minutes");
    }
}
