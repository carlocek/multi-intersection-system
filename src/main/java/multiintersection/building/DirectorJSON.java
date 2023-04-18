package multiintersection.building;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Iterator;
import java.util.ArrayList;

import multiintersection.system.*;

public class DirectorJSON implements Director<JSONObject>
{
    private BuilderJSON builder = new BuilderJSON();

    //it constructs the whole system by passing json objects to the single constructors
    public SystemManager construct(JSONObject jsonObj) throws ParseException, FileNotFoundException, IOException
    {
        SystemManager systemManager = new SystemManager();
        constructQueues(jsonObj, systemManager);
        constructRoadSections(jsonObj, systemManager);
        constructTrams(jsonObj, systemManager);
        constructIntersections(jsonObj, systemManager);
        return systemManager;
    }
    
    //it constructs all the queues by passing slices of json to the builder method
    public void constructQueues(JSONObject jsonObj, SystemManager systemManager)
    {
        JSONArray ja = (JSONArray) jsonObj.get("queues");
        @SuppressWarnings("unchecked")Iterator<JSONObject> itr1 = ja.iterator();          
        while(itr1.hasNext())
        {
            JSONObject i = (JSONObject) itr1.next();
            this.builder.buildQueue(i, systemManager);
        }
        ArrayList<Queue> queueListExit = systemManager.getQueueExit();
        this.builder.buildQueueLinks(queueListExit, systemManager);
    }

    //it constructs all the road sections by passing slices of json to the builder method
    public void constructRoadSections(JSONObject jsonObj, SystemManager systemManager)
    {
        JSONArray jaRoad = (JSONArray) jsonObj.get("RoadSections");
        @SuppressWarnings("unchecked")Iterator<JSONObject> itr1 = jaRoad.iterator();
        while(itr1.hasNext())
        {
            JSONObject i = (JSONObject) itr1.next();
            builder.buildRoadSection(i, systemManager);
        }
    }

    //it constructs all the trams by passing slices of json to the builder method
    public void constructTrams(JSONObject jsonObj, SystemManager systemManager)
    {
        JSONArray jaTramLines = (JSONArray) jsonObj.get("TramLines");
        @SuppressWarnings("unchecked")Iterator<JSONObject> itr1 = jaTramLines.iterator();
        while(itr1.hasNext())
        {
            JSONObject i = (JSONObject) itr1.next();
            builder.buildTramLine(i, systemManager);
        }
    }
    
    //it constructs all the intersections by passing slices of json to the builder method
    public void constructIntersections(JSONObject jsonObj, SystemManager systemManager)
    {
        JSONArray jaIntersection = (JSONArray) jsonObj.get("Intersections");
        @SuppressWarnings("unchecked") Iterator<JSONObject> itr1 = jaIntersection.iterator();
        while(itr1.hasNext())
        {
            JSONObject i = (JSONObject) itr1.next();
            builder.buildIntersection(i, systemManager);
        }
    }
}
