package cs6380simulator;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.LinkedList;

/**
 * Parses out an input file for the application into its individual components
 * @author mark
 *
 */
public class InputFile {
	public int NodeCount;
	public LinkedList<LinkedList<Double>> AdjacencyMatrix;

	/**
	 * Instantiates a new instance of InputFile
	 * @param filename The filename of the file to parse
	 * @throws IOException 
	 * @throws InputFileInvalidException 
	 */
	public InputFile(String filename) throws IOException, InputFileInvalidException{
		StreamTokenizer tokenizer = new StreamTokenizer(new FileReader(filename));
		
		// Configure the tokenizer to ignore slash comments and EOLs
		tokenizer.slashSlashComments(true);
		tokenizer.eolIsSignificant(false);
		
		AdjacencyMatrix = new LinkedList<LinkedList<Double>>();
		
		// The first integer in the file represents the number of nodes
		tokenizer.nextToken();
		NodeCount = (int)tokenizer.nval;
		
		for (int i = 0; i < NodeCount; i++){
			LinkedList<Double> row = new LinkedList<Double>();
			
			for (int j = 0; j < NodeCount; j++){
				// Each decimal number that follows represents a weight for the link from node i to node j
				
				tokenizer.nextToken();
				
				if (tokenizer.ttype != StreamTokenizer.TT_NUMBER){
					throw new InputFileInvalidException("An unexpected character was found while reading the input file.");
				}
				row.add(tokenizer.nval);
			}
			
			AdjacencyMatrix.add(row);
		}		
	}

}