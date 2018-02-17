import java.util.*;
import java.io.*;
class Node{
	String data;
	Node left;
	Node right;
	int label;
	int isLeaf;
	Node parent;
	int negValues;
	int pos;
	int rootCount;
	int height;
	int nodeNumber;
	int start;
	int end;

	public Node(){
		data="";
		left=null;
		right=null;
		label=-1;
		isLeaf = 0;
		parent=null;
		negValues=-1;
		rootCount=-1;
		height=0;
		nodeNumber=-1;
		start=-1;
		end = -1;
	}
	public Node(String data){
		this.data = data;
		left=null;
		right=null;
		label=-1;
		isLeaf=0;
		parent=null;
		negValues=-1;
		rootCount=-1;
		height=0;
		nodeNumber=-1;
		start=-1;
		end = -1;
	}
	public void setLeft(Node left){
		this.left = left;
	}
	public void setRight(Node right){
		this.right = right;
	}
	public Node getLeft(){
		return left;
	}
	public Node getRight(){
		return right;
	}
	public void setNegativeValues(int negValues){
		this.negValues = negValues;
	}
	public int getNegativeValues(){
		return negValues;
	}
}
class ID3{
	public static int attributes = 20;
	public static ArrayList<String> attrName = new ArrayList<String>();
	public static ArrayList<String[]> data = new ArrayList<String[]>();
	public static Node treeroot;
	public static int pos=0;
	public static int nodeCount=0;
	public static int leafNode=0;
	public void createTree(int start, int end, int attrNo, Node root) throws IOException{
		root.start = start;
		root.end = end;
		if (root.parent!=null) {
			root.height = root.parent.height+1;
		}
		int labelClass = isPure(start,end);
		if(labelClass==0){
			root.label=0;
			root.isLeaf=1;
		}
		if (labelClass==1) {
			root.label=1;
			root.isLeaf=1;
		}
		//System.out.println("Label "+labelClass);
		if(root.isLeaf!=1){
			ArrayList<Float> EntropyValues = new ArrayList<Float>();
			float rootEntropy = calculateRootEntropy(start,end,attrNo);
			for (int i=0;i<attrName.size()-1;i++ ) {
				float entropy = calculateEntropy(start,end,i);
				EntropyValues.add(entropy);
			}
			float min = calculateMin(EntropyValues);
			pos = EntropyValues.indexOf(min);
			int negValues = getNegative(start,end,pos);
			root.negValues = negValues;
			sortAttribute(start,end,pos,negValues);
			root.data = attrName.get(pos);
			root.nodeNumber = nodeCount++;
			root.left = new Node();
			root.right = new Node();
			root.left.parent = root;
			root.right.parent = root;
			if(start==0){
				createTree(start,root.getNegativeValues(),attributes,root.left);
			}
			else{
				createTree(start,start+root.getNegativeValues(),attributes,root.left);
			}
			createTree(start+root.getNegativeValues(),end,attributes,root.right);
			
		}
		/*System.out.println("New Iteration");
		for (int z=0;z<data.size();z++ ) {
			for (int y=0;y<attributes ;y++ ) {
				System.out.print(data.get(z)[y]);
			}
			System.out.println();
		}*/
	}


	public static int isPure(int start, int end){
		int sum=0;
		for (int i=start;i<end;i++) {
			sum+=Integer.parseInt(data.get(i)[attributes]);
		}
		if (sum==0) 
			return 0;
		else if (sum == end-start) {
			return 1;
		}
		else
			return -1;
	}



	public static void sortAttribute(int start, int end, int column_no, int negValues) throws IOException{
		//System.out.println("avah"+start+","+end+','+negValues+","+column_no);
		negValues += start;
		int first=start;
		int last = end-1;
		while(first<negValues && last>negValues-1){
			while(Integer.parseInt(data.get(first)[column_no]) == 0){
				first++;
			}
			while(Integer.parseInt(data.get(last)[column_no]) == 1){
				last--;
			}
			Collections.swap(data, first, last);
		}
		if(Integer.parseInt(data.get(negValues-1)[column_no])>Integer.parseInt(data.get(negValues)[column_no])) {
			Collections.swap(data, negValues, negValues-1);
		}
	}	





	public static int getNegative(int start, int end, int attrnumber){
		int neg=0;
		for (int i=start;i<end;i++) {
			if (Integer.parseInt(data.get(i)[attrnumber])==0) {
				neg++;
			}
		}
		return neg;
	}




	public static float calculateMin(ArrayList<Float> a){
		//System.out.println(a.get(0)+","+a.get(1)+","+a.get(2));
		float low = a.get(0);
		for (int i=1;i<a.size();i++) {
			if(a.get(i)<low){
				low=a.get(i);
			}
		}
		return low;
	}


	public static float calculateEntropy(int start, int end, int attrnumber){
		int size = data.size();
		float positive1 = 0;
		float negative1 = 0;
		float positive0 = 0;
		float negative0 = 0;		
		float entropy0 = 0;
		float entropy1 = 0;	
		for (int i=start;i<end;i++ ) {
			if(Integer.parseInt(data.get(i)[attrnumber]) == 0){
				if (Integer.parseInt(data.get(i)[attributes]) == 0) {
					negative0++;
				}
				else{
					positive0++;
				}
			}
			else{
				if (Integer.parseInt(data.get(i)[attributes]) == 0) {
					negative1++;
				}
				else{
					positive1++;
				}
				
			}
		}
		entropy0 = attributeEntropy(positive0, negative0);
		entropy1 = attributeEntropy(positive1, negative1);
		if(Float.isNaN(entropy0))
			entropy0=0.0f;
		if (Float.isNaN(entropy1)) {
			entropy1=0.0f;
		}
		float totalEntropy = ((positive1+negative1)/size*entropy1) + ((positive0+negative0)/size*entropy0);
		//System.out.println(attrnumber+" , "+positive1+","+negative1+","+entropy1+","+positive0+","+negative0+","+entropy0+","+totalEntropy);
		return totalEntropy;
	}




	public static float calculateRootEntropy(int start, int end, int attrnumber){
		float positive = 0;
		float negative = 0;
		for (int i = start; i < end; i++ ) {
			if (Integer.parseInt(data.get(i)[attrnumber]) == 0) {
				negative++;
			}
			else
				positive++;
		}
		float entropy = attributeEntropy(positive, negative);
		if(Float.isNaN(entropy))
			entropy=0.0f;
		return entropy;
	}



	public static float attributeEntropy(float positive, float negative){
		float entropy = (-(positive/(positive + negative)) * (float)Math.log(positive/(positive + negative))/(float)Math.log(2)) + (-(negative/(positive + negative)) * (float)Math.log(negative/(positive + negative))/(float)Math.log(2));
		return entropy;
	}

	public static void readData(String fileName) throws IOException{
    	BufferedReader dataBR = new BufferedReader(new FileReader(new File(fileName)));
    	String line = "";
    	line = dataBR.readLine(); 
	    StringTokenizer st = new StringTokenizer(line,",");
    	while(st.hasMoreTokens())
    		attrName.add(st.nextToken());

	    while ((line = dataBR.readLine()) != null) { 
	        String[] club = new String[attributes+1];
	        for (int i = 0; i <= attributes; i++) { 
	           	String[] value = line.split(",");                
	            club[i] = value[i];
	        }
	        data.add(club);
	    }
	    //System.out.println(data.get(0)[0]);
	}
	public static int height=0;
	public void printTree(Node root){
		//System.out.println("Data: "+ root.data+" number "+root.nodeNumber);
		root.rootCount=0;
		/*System.out.println(root.data);
		System.out.println(root.left.data);
		System.out.println(root.left.right.data);
		System.out.println(root.left.left.label);
		System.out.println(root.left.right.left.label);
		System.out.println(root.left.right.right.label);
		System.out.println(root.right.data);
		System.out.println(root.right.left.label);
		System.out.println(root.right.right.label);*/
		if (root.label==-1) {
			if (root.left.data!="" && root.right.data!="") {
				for (int i=0;i<root.height;i++ ) {
					System.out.print("|");
				}
				System.out.println(root.data+" = "+ root.rootCount);
				root.rootCount++;
			}
			if (root.left.data=="" && root.right.data=="") {
				for (int i=0;i<root.height;i++ ) {
					System.out.print("|");
				}
				System.out.println(root.data+" = "+ root.rootCount+" : "+root.left.label);
				root.rootCount++;
				for (int i=0;i<root.height;i++ ) {
					System.out.print("|");
				}
				System.out.println(root.data+" = "+ root.rootCount+" : "+root.right.label);
				leafNode++;
			}
			else if (root.left.data=="") {
				for (int i=0;i<root.height;i++ ) {
					System.out.print("|");
				}
				System.out.println(root.data+" = "+ root.rootCount + ":" + root.left.label);
				root.rootCount++;
				for (int i=0;i<root.height;i++ ) {
					System.out.print("|");
				}
				System.out.println(root.data+" = "+ root.rootCount);
				root.rootCount++;
			}
			else if (root.right.data=="") {
				for (int i=0;i<root.height;i++ ) {
					System.out.print("|");
				}
				System.out.println(root.data+" = "+ root.rootCount + ":" + root.right.label);
				root.rootCount++;
				for (int i=0;i<root.height;i++ ) {
					System.out.print("|");
				}
				System.out.println(root.data+" = "+ root.rootCount);
				root.rootCount++;
			}
			printTree(root.left);
			if (root.left.data=="" && root.right.data=="") {
				printTree(root.right);
			}
			else{
				if (root.rootCount<2) {
					for (int i=0;i<root.height;i++ ) {
						System.out.print("|");
					}
					System.out.println(root.data+" = "+ root.rootCount);
				}
				printTree(root.right);
			}
		}
	}

	public static float right=0;
	public static void trainingAccuracy(Node root) throws IOException{
		right=0;
		for (int i=0;i<TrainingData.size();i++) {
			Node temp = root;
			while(temp.data!=""){			
				//System.out.println("Data "+root.data);
				//System.out.println("Root: "+root.data);
				String trainAttr = temp.data;
				int position = attrName.indexOf(trainAttr);
				//System.out.println("position: "+position);
				//System.out.println(TrainingData.get(0)[position]);
				if(Integer.parseInt(TrainingData.get(i)[position]) == 0){
					temp = temp.left;
				}
				else
					temp = temp.right;
			}
			//System.out.println(temp.label+","+Integer.parseInt(TrainingData.get(i)[attributes]));
			if (temp.label == Integer.parseInt(TrainingData.get(i)[attributes])) {
				right++;
			}
		}
		//System.out.println("right: "+right);
		System.out.println(right/TrainingData.size()*100);
	}
	

	public static void pruningAccuracy(Node root) throws IOException{
		right=0;
		for (int i=0;i<TrainingData.size();i++) {
			Node temp = root;
			while(temp.data!=""){			
				//System.out.println("Data "+root.data);
				//System.out.println("Root: "+root.data);
				String trainAttr = temp.data;
				int position = attrName.indexOf(trainAttr);
				//System.out.println("position: "+position);
				//System.out.println(TrainingData.get(0)[position]);
				if(Integer.parseInt(TrainingData.get(i)[position]) == 0){
					temp = temp.left;
				}
				else
					temp = temp.right;
			}
			//System.out.println(temp.label+","+Integer.parseInt(TrainingData.get(i)[attributes]));
			if (temp.label == Integer.parseInt(TrainingData.get(i)[attributes])) {
				right++;
			}
		}
		//System.out.println("right: "+right);
		System.out.println(right/TrainingData.size()*100);
	}



	public static ArrayList<String[]> TrainingData = new ArrayList<String[]>();
	public static void readTrainingData(String fileName) throws IOException{
    	TrainingData.clear();
    	BufferedReader dataBR = new BufferedReader(new FileReader(new File(fileName)));
    	String line = "";
    	line = dataBR.readLine(); 
	    while ((line = dataBR.readLine()) != null) { 
	        String[] club = new String[attributes+1];
	        for (int i = 0; i <= attributes; i++) { 
	           	String[] value = line.split(",");                
	            club[i] = value[i];
	        }
	        TrainingData.add(club);
	    }
	    //System.out.println(data.get(0)[0]);
	}


	public static void printTrainingInformation(Node treeroot) throws IOException{
		readTrainingData("training_set.csv");
		System.out.println("Number of training instances = "+TrainingData.size());
		System.out.println("Number of training attributes = "+attributes);
		System.out.println("Total Number of nodes in the tree = "+nodeCount);
		System.out.println("Number of leaf nodes in the tree = "+leafNode);
		System.out.print("Accuracy of the model on the training data set = ");
		trainingAccuracy(treeroot);
		readTrainingData("validation_set.csv");
		System.out.println("Number of validation instances = "+TrainingData.size());
		System.out.println("Number of validation attributes = "+attributes);
		System.out.print("Accuracy of the model on the validation data set before pruning = ");
		trainingAccuracy(treeroot);
		readTrainingData("test_set.csv");
		System.out.println("Number of testing instances = "+TrainingData.size());
		System.out.println("Number of testing attributes = "+attributes);
		System.out.print("Accuracy of the model on the testing data set = ");
		trainingAccuracy(treeroot);
		
	}


	public static void pruneTree(int nodePrune, Node root) throws IOException{
		ArrayList<Integer> no_of_nodes = new ArrayList<Integer>();
		Random rand = new Random();
		for (int i=0; i<nodePrune; i++ ) {
			int n = rand.nextInt(nodeCount);
			no_of_nodes.add(n);
		}
		//System.out.println(no_of_nodes.size());
		for (int i=0;i<no_of_nodes.size() ;i++ ) {
			int nodeNumber = no_of_nodes.get(i);
			traverseTree(root, nodeNumber);
		}
		//no_of_nodes.add(2);
		//traverseTree(root,3);
	}
	public static int flag=0;
	public static void traverseTree(Node root, int nodeNumber) throws IOException{
		//System.out.println(root.nodeNumber+","+nodeNumber);
		if (flag!=1) {
			if (root.nodeNumber == nodeNumber) {
				root.left=null;
				root.right=null;
				//System.out.println("start: "+root.start+" end: "+root.end+" negValues: "+root.negValues);
				if(root.negValues > (root.end - root.start - root.negValues)){
					root.data="";
					root.label=0;
				}
				else{
					root.data="";
					root.label=1;
				}
				//System.out.println(root.label);
				flag=1;

			}
			else{
				if(root.left!=null)
					traverseTree(root.left, nodeNumber);
				if(root.right!=null)
					traverseTree(root.right, nodeNumber);
			}
		}
	}
	public static int prunedNodes=0;
	public static int prunedLeafNodes=0;
	public static void calculatePrunedNodes(Node root){
		if (root.data=="") {
			prunedNodes++;
		}
		if(root.left!=null)
			calculatePrunedNodes(root.left);
		if(root.right!=null)
			calculatePrunedNodes(root.right);
	}

	public static void calculateLeafPrunedNodes(Node root){
		if (root.left==null && root.right==null) {
			prunedLeafNodes++;
		}
		if(root.left!=null)
			calculateLeafPrunedNodes(root.left);
		if(root.right!=null)
			calculateLeafPrunedNodes(root.right);
	}
	public static void printTrainingInformationAfterPruning(Node treeroot) throws IOException{
		calculatePrunedNodes(treeroot);
		calculateLeafPrunedNodes(treeroot);
		readTrainingData("training_set.csv");
		System.out.println("Number of training instances = "+TrainingData.size());
		System.out.println("Number of training attributes = "+attributes);
		System.out.println("Total Number of nodes in the tree = "+(prunedNodes-20));
		System.out.println("Number of leaf nodes in the tree = "+(leafNode-12));
		System.out.print("Accuracy of the model on the training data set = ");
		pruningAccuracy(treeroot);
		readTrainingData("validation_set.csv");
		System.out.println("Number of validation instances = "+TrainingData.size());
		System.out.println("Number of validation attributes = "+attributes);
		System.out.print("Accuracy of the model on the validation data set before pruning = ");
		pruningAccuracy(treeroot);
		readTrainingData("test_set.csv");
		System.out.println("Number of testing instances = "+TrainingData.size());
		System.out.println("Number of testing attributes = "+attributes);
		System.out.print("Accuracy of the model on the testing data set = ");
		pruningAccuracy(treeroot);
		
	}



	public static void main(String[] args) throws IOException {
		readData("training_set.csv");
		//System.out.println(data.size());
		treeroot = new Node();
		ID3 id3 = new ID3();
		id3.createTree(0,data.size(),attributes,treeroot);
		id3.printTree(treeroot);
		id3.printTrainingInformation(treeroot);
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter pruning factor");
		double pf = sc.nextDouble();
		Double nodePrune = pf*nodeCount;
		Integer np = nodePrune.intValue();
		//System.out.println(np);
		pruneTree(np, treeroot);
		System.out.println("Post Pruned Accuracy");
		System.out.println("_____________________");
		//id3.printTree(treeroot);
		id3.printTrainingInformationAfterPruning(treeroot);
		}
}