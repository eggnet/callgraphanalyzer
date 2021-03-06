package callgraphanalyzer;
import java.util.HashMap;
import java.util.LinkedList;

import models.Clazz;
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
	
	public String lookupType(String key)
	{
		if(!maps.isEmpty()) {
			HashMap<String, Mapping> ptr = maps.getFirst();
			for (int i = 0;i < maps.size();i++) 
			{ 
				if (maps.get(i).containsKey(key))
				{
					ptr = maps.get(i); 
				}
			}
			if (ptr.containsKey(key))
				return ptr.get(key).getType();
			else
				return null;
		}
		return null;
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