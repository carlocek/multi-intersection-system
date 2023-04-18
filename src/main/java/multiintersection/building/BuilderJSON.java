package multiintersection.building;

import multiintersection.system.*;
import multiintersection.tramway.TramCrossing;
import multiintersection.tramway.TramLine;
import multiintersection.tramway.pn.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.math.BigInteger;

public class BuilderJSON
{
    //costruisce tutto il sistema, gli oggetti che lo compongono e le reciproche dipendenze.
    //Mentre crea il sistema aggiunge ciò che viene creato a gli array di SystemManager
  
    //metodo che costruisce le code
    public void buildQueue(JSONObject jsonObj, SystemManager systemManager)
    {
        JSONObject i = jsonObj;

        //variabili temporanee per eseguire il parsing
        String tempQueueId;
        String tempPoissonArrivalRate;
        String tempServiceRate;
        String tempSize;
        String tempSemaphoreId;
        String tempHasTram;

        ArrayList<String> tempInputQueuesId = new ArrayList<String>();
        ArrayList<Double> tempInitialDistribution = new ArrayList<Double>();
        ArrayList<ArrayList<Double>> tempStateProbMatrix = new ArrayList<ArrayList<Double>>();
        HashMap<String,String> tempOutQueuesId = new HashMap<String,String>();
        
        tempQueueId = (String)i.get("queueId");
        tempPoissonArrivalRate = (String)i.get("poissonArrivalRate");
        tempServiceRate = (String)i.get("serviceRate");
        tempSize = (String)i.get("size");
        tempSemaphoreId = (String)i.get("semaphoreId");   
        tempHasTram = (String)i.get("hasTram");
        
        JSONArray links = (JSONArray)i.get("inputQueuesId");
        for(Object c:links)
        {
            String newId = (String) c;
            tempInputQueuesId.add(newId);
        }
        
        JSONArray stateDistribution = (JSONArray)i.get("initialDistribution");
        for(Object d:stateDistribution)
        {
            String probability = (String) d;
            tempInitialDistribution.add(Double.parseDouble(probability));
        }
        tempStateProbMatrix.add(tempInitialDistribution);

        JSONArray destQueues = (JSONArray) i.get("outputQueuesId");
        @SuppressWarnings("unchecked")Iterator<JSONObject> itr2 = destQueues.iterator();
        while(itr2.hasNext())
        {
            JSONObject m = (JSONObject) itr2.next();
            String tempD = (String) m.get("D");
            String tempW = (String) m.get("W");
            tempOutQueuesId.put(tempD, tempW);
        }

        Queue tempQueue = new Queue(tempQueueId, tempSemaphoreId, Boolean.parseBoolean(tempHasTram), Integer.parseInt(tempSize), Double.parseDouble(tempPoissonArrivalRate), Double.parseDouble(tempServiceRate), tempInputQueuesId, tempStateProbMatrix, tempOutQueuesId);
        if(!tempQueueId.equals("uscita"))
        {
            systemManager.addQueue(tempQueue);
        }
        systemManager.addQueueExit(tempQueue);
    }

    //metodo che costruisce le roadSections
    public void buildRoadSection(JSONObject jsonObj, SystemManager systemManager)
    {
        JSONObject i = jsonObj;
        String tempId = (String) i.get("roadId");
        String tempLenght = (String) i.get("lenght");
        String tempSpeedLimit = (String) i.get("speedLimit");

        RoadSection tempRoadSection = new RoadSection(tempId, Double.parseDouble(tempLenght), Integer.parseInt(tempSpeedLimit));
        systemManager.addRoad(tempRoadSection);
    }
  
    //metodo che costruisce le tramLines
    public void buildTramLine(JSONObject jsonObj, SystemManager systemManager)
    {
        JSONObject i = jsonObj;
        String tempTramLineId = (String) i.get("tramLineId");
        JSONArray jaTramTracks = (JSONArray) i.get("tramTracks");
        @SuppressWarnings("unchecked")Iterator<JSONObject> itr2 = jaTramTracks.iterator();
        TramLine tramLine = new TramLine(tempTramLineId);
        while(itr2.hasNext())
        {
            JSONObject h = (JSONObject) itr2.next();
            String tempT_name = (String) h.get("t_name");
            String tempT_periodTime = (String) h.get("t_periodTime");
            String tempT_phaseTime = (String) h.get("t_phaseTime");
            String tempT_delayEFTime = (String) h.get("t_delayEFTime");
            String tempT_delayLFTime = (String) h.get("t_delayLFTime");
            String tempT_crosslightAntTime = (String) h.get("t_crosslightAntTime");
            String tempT_leavingEFTime = (String) h.get("t_leavingEFTime");
            String tempT_leavingLFTime = (String) h.get("t_leavingLFTime");

            PetriNetTramTrack bin = PetriNetTramTrackBuilder.getInstance(tempT_name, new BigInteger(tempT_periodTime), new BigInteger(tempT_phaseTime), new BigInteger(tempT_delayEFTime), new BigInteger(tempT_delayLFTime), new BigInteger(tempT_crosslightAntTime), new BigInteger(tempT_leavingEFTime), new BigInteger(tempT_leavingLFTime));

            tramLine.addTramTrack(bin);
        }
        TramCrossing tramCrossing = new TramCrossing(tramLine, tempTramLineId);
        systemManager.addTram(tramCrossing);
    }
    
    //metodo che costruisce le intersections
    public  void buildIntersection(JSONObject jsonObj, SystemManager systemManager)
    {
        JSONObject i = jsonObj;
        
        String tempIntersectionId;
        String tempLenght;
        String tempTrafficLightPeriod;

        ArrayList<Queue> queuesArray = systemManager.getQueue();
        ArrayList<RoadSection> roadArray = systemManager.getRoad();
        ArrayList<TramCrossing> tramCrossingArray = systemManager.getTram();
        ArrayList<String> tempQueuesId = new ArrayList<String>();
        ArrayList<String> tempQueuesSimultaneousGreenId = new ArrayList<String>();
        ArrayList<String> tempSectionsId = new ArrayList<String>();
        ArrayList<Queue> tempQueues = new ArrayList<Queue>();
        ArrayList<Queue> tempQueuesSimultaneousGreen = new ArrayList<Queue>();
        ArrayList<RoadSection> tempSections = new ArrayList<RoadSection>();
        ArrayList<String> tempTramLinesId = new ArrayList<String>();
        ArrayList<TramCrossing> tempTramLines = new ArrayList<TramCrossing>();

        tempIntersectionId = (String)i.get("intersectionId");
        tempLenght = (String)i.get("lenght");
        tempTrafficLightPeriod = (String)i.get("lightPeriod");

        //costruisce la lista delle code componenti l'incrocio
        JSONArray queuesId = (JSONArray) i.get("queues");
        for(Object b : queuesId)
        {
            tempQueuesId.add((String) b);
        }

        for(String j : tempQueuesId)
        {
            for(Queue h : queuesArray)
            {
                if(j.equals(h.getQueueId()))
                {
                    tempQueues.add(h);
                }
            }
        }

        //costruisce la lista delle code che devono avere verde in contemporanea
        JSONArray queuesSimultaneousGreenId = (JSONArray) i.get("queuesSimultaneousGreen");
        for(Object b : queuesSimultaneousGreenId)
        {
            tempQueuesSimultaneousGreenId.add((String) b);
        }

        for(String j : tempQueuesSimultaneousGreenId)
        {
            for(Queue h : queuesArray)
            {
                if(j.equals(h.getQueueId()))
                {
                    tempQueuesSimultaneousGreen.add(h);
                }
            }
        }

        //costruisce la lista di roadSection componenti l'incrocio
        JSONArray sectionsId = (JSONArray) i.get("sections");
        for(Object m : sectionsId)
        {
            tempSectionsId.add((String) m);
        }

        for(String j : tempSectionsId)
        {
            for(RoadSection h : roadArray)
            {
                if(j.equals(h.getId()))
                {
                    tempSections.add(h);
                }
            }
        }

        JSONArray tramLinesId = (JSONArray) i.get("tramLines");
        for(Object b : tramLinesId)
        {
            tempTramLinesId.add((String) b);
        }
        for(String j : tempTramLinesId)
        {
            for(TramCrossing h : tramCrossingArray)
            {
                if(j.equals(h.getLineId()))
                {
                    tempTramLines.add(h);
                }
            }
        }

        //costruisce le intersezioni
        Intersection tempIntersection = new Intersection(tempIntersectionId, Double.parseDouble(tempLenght), Double.parseDouble(tempTrafficLightPeriod), tempSections, /*h*/ tempQueues, tempQueuesSimultaneousGreen);
        systemManager.addIntersection(tempIntersection);

        //setta riferimenti a intersezione nelle code
        for(Queue d:tempQueues)
        {
            d.setIntersection(tempIntersection);
        }

        //setta le tramline
        for (TramCrossing h: tempTramLines)
        {
            tempIntersection.setTramCrossing(h);
        }
    }

    //metodo che costruisce i collegamenti tra le code, invocato nel buildQueues
    public void buildQueueLinks(ArrayList<Queue> queueListExit, SystemManager systemManager)
    {    
        //inputQueues
        ArrayList<Queue> queueList = systemManager.getQueue();
        for(Queue i : queueList)
        {
            ArrayList<String> inputQueuesId = new ArrayList<String>();
            inputQueuesId = i.getInputQueuesId();
            for(String j : inputQueuesId)
            {
                for(Queue h: queueList)
                {
                    if(j.equals(h.getQueueId()))
                    {
                        i.inQueues(h);
                    }
                }
            }
        }

        //outputQueues
        for(Queue i : queueList)
        {
            HashMap<String,String> outQueuesId = new HashMap<String,String>();
            HashMap<Queue,Double> outQueues = new HashMap<Queue,Double>();
            outQueuesId = i.getOutQueuesId();
            for (Map.Entry<String, String> set : outQueuesId.entrySet())
            {
                Object k = set.getKey();
                Object v = set.getValue();
                String destId = (String) k;
                String destWeight = (String) v;
                for(Queue h: queueListExit)
                {
                    if(destId.equals(h.getQueueId()))
                    {
                        outQueues.put(h, Double.parseDouble(destWeight));
                    }
                }
            }
            i.outQueues(outQueues);
        }
    }
}
