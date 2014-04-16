import java.util.ArrayList;

import edu.smu.tspell.wordnet.*;
public class WordNet implements Properties{



/**
 * Displays word forms and definitions for synsets containing the word form
 * specified on the command line. To use this application, specify the word
 * form that you wish to view synsets for, as in the following example which
 * displays all synsets containing the word form "airplane":
 * <br>
 * java TestJAWS airplane
 */


	/**
	 * Main entry point. The command-line arguments are concatenated together
	 * (separated by spaces) and used as the word form to look up.
	 */
	public ArrayList<String> wordnet(String wordForm)
	{
			
			System.setProperty(WORD_NET_DICT_NAME, WORD_NET_DICT_LOC);
		
			ArrayList<String> result=new ArrayList<String>();
			//  Get the synsets containing the wrod form
			WordNetDatabase database = WordNetDatabase.getFileInstance();
			Synset[] synsets = database.getSynsets(wordForm);
			//  Display the word forms and definitions for synsets retrieved
			if (synsets.length > 0)
			{
				
				String[] wordForms = synsets[0].getWordForms();
				
				if(wordForms.length>2)
						{
						result.add(wordForms[1]);
						result.add(wordForms[2]);
						return(result);
						}
				else if(wordForms.length==2)
					{
					result.add(wordForms[1]);
					return(result);
					}
				else
				{
					
					return(result);
					}
			}
			else
			{
				{
					
					return(result);
					}
			}
		}
		
	
	

}