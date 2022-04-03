package lab9;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;

public class WordEmbeddings {
	
	
	

	public WordEmbeddings() throws NumberFormatException, IOException {
		
		
		
	}
	
	
	public void loadPrecomputedEmbeddings() throws IOException {
		
		//https://deeplearning4j.konduit.ai/language-processing/word2vec
		
		
		Word2Vec w2vModel = WordVectorSerializer.readWord2VecModel("files/lab9/pizza.java.embeddings");


		//Attempts to read python generated vectors
		//Word2Vec w2vModel = WordVectorSerializer.readWordVectors(new File("files_lab9/pizza.embeddings.txt"));
		//Word2Vec w2vModel = WordVectorSerializer.readWord2VecModel(new File("files_lab9/pizza.embeddings.bin"));
		//InputStream word2vecmodelFile = new FileInputStream("files_lab9/pizza.embeddings.bin");
		//Word2Vec w2vModel = WordVectorSerializer.readBinaryModel(word2vecmodelFile, false, true);
		//Word2Vec w2vModel = WordVectorSerializer.readWord2Vec(word2vecmodelFile, false);
				
				
		System.out.println(w2vModel.wordsNearest("pizza", 10));
		System.out.println(w2vModel.wordsNearest("margherita", 10));
		System.out.println(w2vModel.similarity("http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza", "margherita"));
		
		
		//To lookup table and INDArray
		WeightLookupTable weightLookupTable = w2vModel.lookupTable();
		Iterator<INDArray> vectors = weightLookupTable.vectors();
		INDArray wordVectorMatrix = w2vModel.getWordVectorMatrix("pizza");
		double[] wordVector = w2vModel.getWordVector("pizza");
		
	}
	
	
	
	public void createEmbeddings(String file_sentences) throws IOException {
		//Reused from https://deeplearning4j.konduit.ai/language-processing/word2vec#loading-data
		
		
		//Load sentences
		SentenceIterator iter = new LineSentenceIterator(
				new File(file_sentences));
		iter.setPreProcessor(new SentencePreProcessor() {
		    @Override
		    public String preProcess(String sentence) {
		        //return sentence.toLowerCase();
		        return sentence;//.toLowerCase();
		    }
		});
		
		// Split on white spaces in the line to get words
		TokenizerFactory t = new DefaultTokenizerFactory();
		//t.setTokenPreProcessor(new CommonPreprocessor());
		
		
		//Building mode;
		Word2Vec w2vModel = new Word2Vec.Builder()
		        .minWordFrequency(1)
		        .epochs(5)
		        .iterations(5)
		        .layerSize(100)
		        .seed(42)
		        .negativeSample(25)
		        .windowSize(5)
		        .iterate(iter)
		        .tokenizerFactory(t)
		        .build();

		//Fitting model
		w2vModel.fit();
		
		//Evaluate model
		System.out.println(w2vModel.wordsNearest("pizza", 10));
		System.out.println(w2vModel.similarity("pizza", "food"));
		
		
		//Vector for a given word
		w2vModel.getWordVector("pizza");
		
		
		//Save vectors
		WordVectorSerializer.writeWord2VecModel(w2vModel, "files/lab9/pizza.java.embeddings");
		WordVectorSerializer.writeWordVectors(w2vModel, "files/lab9/pizza.java.embeddings.txt");
		
		
	}
	
	
	
	//Gives an error to load the embeddings from python. Instead use the generated document and compute word embeddings.
	//It will need some work. 
	
	
	public static void main(String[] args) {

		try {
			WordEmbeddings we = new WordEmbeddings();
			
			we.createEmbeddings("files/lab9/document_sentences.txt");
			
			//From the step above
			we.loadPrecomputedEmbeddings();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
