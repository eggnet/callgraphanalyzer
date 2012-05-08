package callgraphanalyzer;
import java.util.HashMap;
import java.util.LinkedList;

import models.Mapping;

public class Mappings {
	public LinkedList<HashMap<String, Mapping>> maps;
	public HashMap<String, Mapping> currentMap;
	public Mappings() {
		maps = new LinkedList<HashMap<String, Mapping>>();
	}
	public void push(HashMap<String, Mapping> map)
	{
		maps.addFirst(map);
		currentMap = maps.getFirst();
	}
	
	public Mapping lookup(String key)
	{
		return currentMap.get(key);
	}
	
	public void addMapping(String key, Mapping mapping)
	{
		currentMap.put(key, mapping);
	}
	
	public void newMap()
	{
		maps.addFirst(new HashMap<String, Mapping>());
		currentMap = maps.getFirst();
	}
	
	public void removeMap()
	{
		maps.removeFirst();
		if (maps.size() > 0)
			currentMap = maps.getFirst();
	}
}