
public interface Properties {

	//Starts Here: Update these values accordingly
	public static final String TAG_FILE="E:\\IRE_MP\\Cluster_Index\\ClusterTags";
	public static final String LUCENE_OP_FOLDER="E:\\IRE_MP\\Cluster_Index";
	public static final String CLUSTERS_FOLDER="E:\\IRE_MP\\Clusters";
	
	//newly added-10/04/14
	public static final int RETRIEVE_DOC_NO = 15;
	public static final String LUCENE_OP_FOLDER1="E:\\IRE_MP\\Document_Index";
	public static final String DOC_FOLDER="E:\\IRE_MP\\DataSet";
	public static final String RECALL_FILE="E:\\IRE_MP\\Group-06\\Queryexpansion_Recall.txt";
	public static final String PRECISION_FILE_NAME_PART="E:\\IRE_MP\\Group-06\\Queryexpansion_Precision_";
	public static final String PRECISION_FILE="E:\\IRE_MP\\Group-06\\Queryexpansion_Precision_"+RETRIEVE_DOC_NO+".txt";
	public static final String EVAL_RESULTS_FILE="E:\\IRE_MP\\Group-06\\Queryexpansion_results.xls";
	//newly added-10/04/14-end
	
	public static final String DOC2MAT_IP_FILE_FOLDER="E:\\IRE_MP\\Doc2MatInput";
	public static final String DATASET_FOLDER="E:\\IRE_MP\\DataSet";
	public static final String WORD_NET_DICT_LOC="C:\\WordNet\\2.1\\dict";
	public static final String WORD_NET_DICT_NAME="wordnet.database.dir";
	
	public static final String TAGGER="taggers/english-caseless-left3words-distsim.tagger";
	
	
	public static final int MAX_PER_CLUSTER = 2;
	public static final int DOC_LIMIT=40;
	public static final int NO_OF_CLUSTERS = 150;
	//Ends Here: Update these values accordingly
	
	//No need to set these values
	public static final String DOC2MATINPUT_FILE=DOC2MAT_IP_FILE_FOLDER+"\\File";
	public static final String CLUST_MAT__REP_FILE = DOC2MATINPUT_FILE+".mat.clustering."+NO_OF_CLUSTERS;
	
	
	
					
	
	


}
