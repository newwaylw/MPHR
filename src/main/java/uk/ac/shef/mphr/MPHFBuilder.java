package uk.ac.shef.mphr;

import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.sux4j.io.ChunkedHashStore;
import it.unimi.dsi.sux4j.mph.MinimalPerfectHashFunction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.shef.util.Util;

/**
 * Convenient class to build MPH function and serialisation
 * 
 * @author wei
 * 
 */
public class MPHFBuilder {

	static Logger logger = LogManager.getLogger(MPHFBuilder.class);
	public MPHFBuilder() {

	}

	/**
	 * Build a MPH function mapping and serialised it using all keys (String)
	 * from a file each line contains the ngram and its count (frequency)
	 * separated by tab (\\t)
	 * 
	 * @param file
	 *            - input file, can be a gzipped file or plain txt file, this
	 *            method detects it by checking its extension name notice if the
	 *            number of keys are very large this method requires a lot of
	 *            tmp space. by default java uses JAVA_TMP, one might want to
	 *            change this if needed.
	 */
	public static MinimalPerfectHashFunction<String> buildMPHF(String file) {
		try {
			final Random r = new Random();
			/*
			 * note here,
			 */
			ChunkedHashStore<String> chunkedHashStore = new ChunkedHashStore<String>(
					TransformationStrategies.utf16());
			chunkedHashStore.reset(r.nextLong());

			BufferedReader reader;
			String key;
			if (!file.endsWith(".gz")) {
				logger.debug("reading gzip file:" + file);
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF8"));
			} else {
				logger.debug("reading text file:" + file);
				reader = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(new FileInputStream(file)), "UTF8"));
			}
			long no = 0;
			while ((key = reader.readLine()) != null) {
				// assuming the input file is "key<TAB>value" per line.
				key = key.split("\t")[0];
				chunkedHashStore.add(key);
				no++;
				if (no % 10000000 == 0) {
					long m1 = Runtime.getRuntime().totalMemory()
							- Runtime.getRuntime().freeMemory();
					m1 /= (1024 * 1024);
					logger.info("memory used when " + no + " keys inserted ="
							+ m1 + " MBytes.");
				}

			}

			MinimalPerfectHashFunction<String> mphf = new MinimalPerfectHashFunction<String>(
					null, TransformationStrategies.utf16(), chunkedHashStore);
			return mphf;

		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	public static void main(String args[]) {
		int len = args.length;
		if (len != 2) {
			String info = "usage:" + MPHFBuilder.class.getName()
					+ " 0.99a <InputFile> <output_mphf> \n";
			info += "\tEach line should contain a distinct key\n";
			info += "\t a key is the first string to the first tab if present.";
			// info +="\n\t: key+value bits must not exceed 64";
			System.out.println(info);
			System.exit(0);
		}

		String inputHashFile = args[0];
		String outputMPHFPath = args[1];
		// String tmpDir =System.getProperty("java.io.tmpdir");

		MinimalPerfectHashFunction<String> mphf = MPHFBuilder
				.buildMPHF(inputHashFile);
		Util.serializeObject(mphf, outputMPHFPath);
	}
}
