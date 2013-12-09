package dataGrabber;

import indexMaker.CDSIndexProcessor;
import indexMaker.CDSSingleNameReader;
import indexMaker.FridayClock;
import indexMaker.FridayManager;
import indexMaker.I_DBClock;
import indexMaker.I_DBProcessor;
import indexMaker.I_DBReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.pdfbox.ExtractText;

import Utils.StringUtils;
import Utils.WriteXls;
import dataWrapper.Index;
import dataWrapper.SingleName;

/**
 * @author Zhenghong Dong
 */
public class DataGrabber {
	private final String[]				_igURL				= {
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.<version>-V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20IG%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/IG%20<version>%20v1.pdf"
															};
	private final String				_igLocal			= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\IG<version>.pdf";

	private final String[]				_consURL			= {
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.CONS.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20IG%20CONS%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20CONS%20<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/DJ%20CDX.NA.IG.CONS.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.CONS.<version>-V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/IG%20CONS%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.CONS.<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20CONS%20<version>.pdf"
															};
	private final String				_consLocal			= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\CONS<version>.pdf";

	private final String[]				_enrgURL			= {
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.ENRG.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20IG%20ENRG%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20ENRG%20<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/DJ%20CDX.NA.IG.ENRG.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.ENRG.<version>-V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/IG%20ENRG%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.ENRG.<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20ENRG%20<version>.pdf"
															};
	private final String				_enrgLocal			= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\ENRG<version>.pdf";

	private final String[]				_finURL				= {
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.FIN.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20IG%20FIN%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20FIN%20<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/DJ%20CDX.NA.IG.FIN.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.FIN.<version>-V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/IG%20FIN%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.FIN.<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20FIN%20<version>.pdf"
															};
	private final String				_finLocal			= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\FIN<version>.pdf";

	private final String[]				_tmtURL				= {
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.TMT.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20IG%20TMT%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20TMT%20<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/DJ%20CDX.NA.IG.TMT.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.TMT.<version>-V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/IG%20TMT%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.TMT.<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20TMT%20<version>.pdf"
															};
	private final String				_tmtLocal			= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\TMT<version>.pdf";

	private final String[]				_induURL			= {
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.INDU.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20IG%20INDU%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20INDU%20<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/DJ%20CDX.NA.IG.INDU.<version>.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.INDU.<version>-V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/IG%20INDU%20<version>%20v1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX.NA.IG.INDU.<version>%20V1.pdf",
			"http://www.markit.com/assets/en/docs/products/data/indices/credit-index-annexes/CDX%20NA%20IG%20INDU%20<version>.pdf"
															};
	private final String				_induLocal			= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\INDU<version>.pdf";

	private final String[][]			_urls				= { _igURL, _finURL, _induURL, _enrgURL, _consURL, _tmtURL };
	private final String[]				_localFilesSample	= { _igLocal, _finLocal, _induLocal, _enrgLocal, _consLocal,
															_tmtLocal };
	private static final String[]		_indexList			= { "IG", "FIN", "INDU", "ENRG", "CONS", "TMT" };
	private static final String[]		_indexListOld		= { "IG", "Financials", "Industrials", "Energy", "Consumer", "TMT" };
	// private static final String _highVol = "HVOL";
	private final String				_suffix				= " USD CDS SR 5Y CORP";

	private List<String>				_localFiles;
	private HashMap<String, Index>		_indices;
	private HashMap<String, SingleName>	_singleNames;
	private HashMap<String, I_DBReader> _allSingleName;
	private List<String>				_inactive;
	private final int					_start, _end;

	private final String				_indexFolder		= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\indices\\";
	private final String				_tickerFinder		= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\CDS\\unique.csv";
	private final String				_singleNameOrgFiles	= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\CDS\\histprice\\csv\\<name>.csv";
	private final String				_singleNameFolder	= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\singleNames\\";
	private final String				_inactiveNames		= _singleNameFolder + "inactive.csv";
	
	

	public DataGrabber(int start, int end) {
		_start = start;
		_end = end;
		_indices = new HashMap<>();
		_singleNames = new HashMap<>();
		_inactive = new ArrayList<>();
	}

	public void getPDF() throws Exception {
		_localFiles = new ArrayList<>();
		for (int i = _start; i <= _end; i++) {
			for (int q = 0; q < _urls.length; q++) {
				for (int j = 0; j < _urls[ q ].length; j++) {
					try (InputStream in = URI.create( _urls[ q ][ j ].replace( "<version>", String.valueOf( i ) ) )
							.toURL().openStream()) {
						Files.copy( in, Paths.get( _localFilesSample[ q ].replace( "<version>", String.valueOf( i ) ) ) );
						_localFiles.add( _localFilesSample[ q ].replace( "<version>", String.valueOf( i ) ) );
						break;
					} catch (final FileNotFoundException e) {
						continue;
					} catch (final FileAlreadyExistsException e) {
						_localFiles.add( _igLocal.replace( "<version>", String.valueOf( i ) ) );
						break;
					} catch (final Exception e) {
						System.err.println( "Fail to get cdx data for " + _indexList[ q ] + " version s" + i );
						e.printStackTrace();
						System.exit( 1 );
					}
				}
			}
		}
	}

	public void PDFToTxt() throws Exception {
		final String[] input = new String[ 1 ];
		for (final ListIterator<String> iterator = _localFiles.listIterator(); iterator.hasNext();) {
			input[ 0 ] = iterator.next();
			try {
				ExtractText.main( input );
				iterator.set( input[ 0 ].replace( "pdf", "txt" ) );
			}
			// file is encrypted...markit is stupid ffs
			catch (NoClassDefFoundError e) {
				System.err.println( "Fail to open encrypted file: " + input[ 0 ] );
				continue;
			} catch (final Exception e) {
				System.err.println( "Fail to convert to txt for file: " + input[ 0 ] );
				e.printStackTrace();
				System.exit( 1 );
			}
		}
	}

	public void getIndexComponents() throws Exception {
		File f = new File( _indexFolder + "IndexComponents.xls" );
		if (f.exists()) f.delete();

		BufferedReader in = null;
		String str = null;
		String fileName = "";
		String indexName = "";
		Index index = null;
		String[] subIndex = Arrays.copyOfRange( DataGrabber._indexList, 1, _indexList.length );
		String[] subIndexOld = Arrays.copyOfRange( DataGrabber._indexListOld, 1, _indexListOld.length );
		// get <company name, ticker> map
		HashMap<String, String> map = getCompanyNameMap();

		for (final ListIterator<String> iterator = _localFiles.listIterator(); iterator.hasNext();) {
			try {
				fileName = iterator.next();
				indexName = fileName.substring( fileName.lastIndexOf( "\\" ) + 1 ).split( ".txt" )[ 0 ];
				// if (_indices.containsKey( indexName )) continue;

				in = new BufferedReader( new FileReader( fileName ) );
				// get index
				str = in.readLine();
				// should be date in first two lines
				if (!str.startsWith( "Effective Date:" )) {
					if (!(str = in.readLine()).startsWith( "Effective Date:" )) {
						System.err.println( "Incorrect format for file " + fileName );
						System.exit( 1 );
					}
				}
				index = new Index( indexName, str.substring( str.indexOf( ":" ) + 2 ) );
				while ((str = in.readLine()) != null) {
					if (Character.isDigit( str.charAt( 0 ) )) {
						int indexStr = StringUtils.firstOccuranceOfArray( str, subIndex );
						if (indexStr == -1) {
							if ((indexStr = StringUtils.firstOccuranceOfArray( str, subIndexOld )) == -1){
								continue;
							}
						}
						// get company full name
						String name = str.substring( str.indexOf( " " ) + 1, indexStr - 1 ).toUpperCase() + _suffix;

						// get ticker
						String ticker = map.get( name );

						// save stock data
						// downloadStock(String ticker, new SingleName(ticker));

						// add stock name to index and global storage
						if (ticker == null || ticker.equals( "N/A" )) {
							System.err.println( "For index " + indexName + " there's no company: " + name
									+ " in unique.csv" );
							index.addMissingCompany( name );
						}
						else {
							index.addCompany( ticker );
							_singleNames.put( ticker, new SingleName( ticker ) );
						}
					}
				}
				// add index to global storage
				_indices.put( indexName, index );

				// write this index components data in to index folder
				List<Index> tmp = Arrays.asList( index );
				WriteXls.appendSingleRecord( _indexFolder + "IndexComponents.xls", indexName, tmp );
			} catch (final FileNotFoundException e) {
				System.err.println( "File doesn't exist: " + fileName );
				continue;
			} catch (final IOException e) {
				System.err.println( "Fail to parse file " + fileName );
				e.printStackTrace();
			} catch (final StringIndexOutOfBoundsException e) {
				System.err.println( "Fail to parse file on line: " + str );
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (final NullPointerException e) {}
			}
		}
	}

	public void filterSingleName(int startDate) throws IOException {
		// read all single name price info into memory, this is stupid I know but easy to implement I DONT HAVE TIME!!!
		// actually this might be fast...
		_allSingleName = new HashMap<>();
		for (Iterator<String> iterator = _singleNames.keySet().iterator(); iterator.hasNext();) {
			String name = iterator.next();
			try {
				_allSingleName
						.put( name, new CDSSingleNameReader( name, _singleNameOrgFiles.replace( "<name>", name ) ) );
			} catch (FileNotFoundException e) {
				System.err.println( name + " in unique.cvs but not in single name csv folder" );
			}
		}

		for (Iterator<String> iterator = _singleNames.keySet().iterator(); iterator.hasNext();) {
			// get single name
			String name = iterator.next();

			// use the single name itself's reader
			final LinkedList<I_DBReader> readers = new LinkedList<>();
			I_DBReader single;
			if ((single = _allSingleName.get( name )) == null) {
				//System.err.println( name + " trying to use " + name + " but doesn't exist" );
				continue;
			} else {
				readers.add( single );
			}

			// out file
			String outFile = _singleNameFolder + name + ".csv";
			File f = new File( outFile );
			if (f.exists()) f.delete();

			// write merged data
			final LinkedList<I_DBProcessor> processors = new LinkedList<>();
			processors.add( new CDSIndexProcessor( name, outFile, null, null ) );

			// Make a db clock

			final I_DBClock clock = new FridayClock( startDate, readers );

			// Make a new db manager and hand off db readers, db processors, and clock

			final FridayManager dbManager = new FridayManager(
					readers, // List of readers
					processors, // List of processors
					clock // Clock
			);

			// Launch db manager to merge the three db readers
			String ret = dbManager.launch();
			if (ret != null) {
				_inactive.add( ret );
			}
		}
		if (_inactive.size() != 0) {
			File f = new File( _inactiveNames );
			if (f.exists()) f.delete();
			FileWriter writer = new FileWriter( f );
			for (String string : _inactive) {
				writer.append( string );
				writer.append( '\n' );
			}
			writer.flush();
			writer.close();
		}
	}

	public void constructIndex() throws IOException {
		// read all single name price info into memory, this is stupid I know but easy to implement I DONT HAVE TIME!!!
		// actually this might be fast...
		_allSingleName = new HashMap<>();
		for (Iterator<String> iterator = _singleNames.keySet().iterator(); iterator.hasNext();) {
			String name = iterator.next();
			if (!_inactive.contains( name )) try {
				_allSingleName
						.put( name, new CDSSingleNameReader( name, _singleNameFolder + name + ".csv" ) );
			} catch (FileNotFoundException e) {
				// System.err.println( name + " in unique.cvs but not in single name csv folder" );
			}
		}
		for (Iterator<String> iterator = _indices.keySet().iterator(); iterator.hasNext();) {
			// get index name
			String index = iterator.next();
			Index i = _indices.get( index );

			// use readers who are in this index
			final LinkedList<I_DBReader> readers = new LinkedList<>();
			I_DBReader single;
			for (String tmp : i.getCompanySet()) {
				if ((single = _allSingleName.get( tmp )) == null) {
					//System.err.println( index + " trying to use " + tmp + " but doesn't exist" );
					i.addMissingCDS( tmp );
				} else {
					readers.add( single );
					single.reset();
				}
			}

			// out file
			String outFile = _indexFolder + index + ".csv";
			File f = new File( outFile );
			if (f.exists()) f.delete();

			// write merged data
			final LinkedList<I_DBProcessor> processors = new LinkedList<>();
			processors.add( new CDSIndexProcessor( index, outFile, i.getMissingCompany(), i.getMissingCDS() ) );

			// Make a db clock

			final I_DBClock clock = new FridayClock( i.getStartDate(), readers );

			// Make a new db manager and hand off db readers, db processors, and clock

			final FridayManager dbManager = new FridayManager(
					readers, // List of readers
					processors, // List of processors
					clock // Clock
			);

			dbManager.launch();
		}
	}

	private HashMap<String, String> getCompanyNameMap() throws IOException {
		BufferedReader in = null;
		String str = null;

		// get single name <company name, ticker> map
		HashMap<String, String> map = new HashMap<>();
		String[] line;
		try {
			in = new BufferedReader( new FileReader( _tickerFinder ) );
			in.readLine();

			while ((str = in.readLine()) != null) {
				line = str.split( "," );
				int j = 1;
				String name = line[ j ];
				while (line[ ++j ].startsWith( " " )) {
					name += "," + line[ j ];
				}
				map.put( name.replaceAll( "\"", "" ).toUpperCase(), line[ j + 1 ] );
			}
		} catch (final FileNotFoundException e) {
			System.err.println( "File doesn't exist: " + _tickerFinder );
		} catch (final IOException e) {
			System.err.println( "Fail to parse file " + _tickerFinder );
			e.printStackTrace();
		} catch (final StringIndexOutOfBoundsException e) {
			System.err.println( "Fail to parse file on line: " + str );
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (final NullPointerException e) {}
		}
		return map;
	}

	public HashMap<String, Index> getIndices() {
		return _indices;
	}

	public static void main(final String[] args) throws Exception {
		final int start = 10;
		final int end = 21;

		final DataGrabber grabber = new DataGrabber( start, end );

		// get pdf from Markit
		//grabber.getPDF();

		/*List<String> pdfsTest = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			pdfsTest.add( grabber._igLocal.replace( "<version>", String.valueOf( i ) ) );
			pdfsTest.add( grabber._consLocal.replace( "<version>", String.valueOf( i ) ) );
			pdfsTest.add( grabber._induLocal.replace( "<version>", String.valueOf( i ) ) );
			pdfsTest.add( grabber._enrgLocal.replace( "<version>", String.valueOf( i ) ) );
			pdfsTest.add( grabber._finLocal.replace( "<version>", String.valueOf( i ) ) );
			pdfsTest.add( grabber._tmtLocal.replace( "<version>", String.valueOf( i ) ) );
		}
		grabber._localFiles = pdfsTest;
		// pdf to txt
		 grabber.PDFToTxt();*/

		// get index components from txt file
		List<String> localFilesTest = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			localFilesTest.add( grabber._igLocal.replace( "<version>", String.valueOf( i ) ).replace( "pdf", "txt" ) );
			localFilesTest.add( grabber._consLocal.replace( "<version>", String.valueOf( i ) ).replace( "pdf", "txt" ) );
			localFilesTest.add( grabber._induLocal.replace( "<version>", String.valueOf( i ) ).replace( "pdf", "txt" ) );
			localFilesTest.add( grabber._enrgLocal.replace( "<version>", String.valueOf( i ) ).replace( "pdf", "txt" ) );
			localFilesTest.add( grabber._finLocal.replace( "<version>", String.valueOf( i ) ).replace( "pdf", "txt" ) );
			localFilesTest.add( grabber._tmtLocal.replace( "<version>", String.valueOf( i ) ).replace( "pdf", "txt" ) );
		}
		grabber._localFiles = localFilesTest;
		grabber.getIndexComponents();

		// filter single names to weekly data
		grabber.filterSingleName( 20080101 );

		// construct index payoff
		grabber.constructIndex();

		System.out.println( "End" );
	}
}
