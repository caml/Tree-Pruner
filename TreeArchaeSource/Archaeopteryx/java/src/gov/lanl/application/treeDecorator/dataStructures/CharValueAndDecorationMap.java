package gov.lanl.application.treeDecorator.dataStructures;

import java.util.HashMap;

public class CharValueAndDecorationMap<K,V> extends HashMap<K,V>{
	@Override
	public V get(Object key){
		V decorateObject = null;
		String keyInLowerCase = key.toString().toLowerCase();
		String keyInUpperCase = key.toString().toUpperCase();
		if(super.containsKey(key)){
			decorateObject = super.get(key);
			return decorateObject;
		}
		for (K keys:super.keySet()){
			if(keys.toString().toLowerCase().equals(keyInLowerCase)){
				decorateObject = super.get(keys);
				return decorateObject;
			}
			else if (keys.toString().toLowerCase().equals(keyInUpperCase)){
				decorateObject = super.get(keys);
				return decorateObject;
			}
		}
		return decorateObject;
	}
}
