package multiintersection.building;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.parser.ParseException;

import multiintersection.system.*;

public interface Director<T>
{
    public SystemManager construct(T obj) throws ParseException, FileNotFoundException, IOException;
    public void constructQueues(T obj, SystemManager systemManager);
    public void constructRoadSections(T obj, SystemManager systemManager);
    public void constructTrams(T obj, SystemManager systemManager);
    public void constructIntersections(T obj, SystemManager systemManager);
}
