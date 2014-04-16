
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class AddSynonyms {
	
	@SuppressWarnings("rawtypes")
	public String process(String input)
	{
		StringBuffer sb=new StringBuffer();
		Object[] words=SearchIndex.getStopWords("Stopword").toArray();
		int index; 
		WordNet w=new WordNet();
		ArrayList<String> wordnet=new ArrayList<String>();
	
		HashMap<String,Integer> map=new HashMap<String,Integer>();
		ValueComparator bvc =  new ValueComparator(map);
		TreeMap<String,Integer> mapsorted = new TreeMap<String,Integer>(bvc);
		int value;
		StringTokenizer s = new StringTokenizer(input," ");
        while (s.hasMoreTokens())
        {
            String token = s.nextToken().replaceAll("[\\W\\d]","").toLowerCase();
            index=java.util.Arrays.binarySearch(words, token);
            
            if(!token.equals("") && index<0)
            {	
            //	System.out.println(token);
            	if(map.containsKey(token))
            	{
            		value=map.get(token);
            		value++;
            		map.put(token, value);
            	}
            	else
            		map.put(token, 1);
            }
          
        }
        mapsorted.putAll(map);
        
	//	System.out.println(mapsorted);
		
		//get the top 5 % words
		
		int total=mapsorted.size();
		int count=(total*5)/100+1;
	//	System.out.println("total"+total);
	//	System.out.println("count"+count);
	//
		ArrayList<String> imp=new ArrayList<String>();
		
		Collection entrySet = mapsorted.keySet();
	    Iterator it = entrySet.iterator();
	    int i=1;
	    while(it.hasNext() && i<=count)
	    {   	
	    	imp.add((String) it.next());
	    	i++;
	    }
		
	 //   System.out.println(imp);
	    int number;

	    sb.append(input);
	    
	    for(int j=0;j<imp.size();j++)
	    {
	    	number=map.get(imp.get(j));
	    //	System.out.println("number"+number);
	    	
	   
	    	wordnet=w.wordnet(imp.get(j));
	    //	System.out.println("wordnet"+wordnet);	
	    	for(int k=0;k<wordnet.size();k++)
	    	{
	    	 	
	    		for(int u=1;u<=number;u++)
	    			sb.append(" "+wordnet.get(k));
	    	}
	    //	System.out.println("sb contains"+sb);
	    
	    	wordnet.clear();
	    
	    }
	  
		return sb.toString();
	}
	public static void main(String args[])
	{
		AddSynonyms t=new AddSynonyms();
		String input=" The Telegraph - Calcutta : Bengal   Wednesday, September 01, 2004    CPM supporters blocked GT Road near here this morning and clashed with police protesting against the arrest of two leaders accused of murder....    A Citu leader and a Calcutta police constable have been arrested for allegedly harassing a woman and trying to grab her ...    Suspicion about his wifes fidelity apparently prompted Harekrishna Bera, 40, a small farmer, to butcher his four children a ...    The government today sanctioned 40 inspector-in-charge posts in 40 police stations in militancy-prone districts and rapi ...    Little did police realise that the five Indians they were preparing to receive from their Bangladesh counterparts at Hi ...    Royal Bengal Tiger     ";
		System.out.println(t.process(input));
	}

}

class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

