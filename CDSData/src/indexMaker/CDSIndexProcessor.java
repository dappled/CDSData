package indexMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Utils.ParseDate;
import dataWrapper.CDSFields;

/**
 * This is an implementation of {@link I_DBProcessor}, it merges several quote files to a new GZfile
 * @author Zhenghong Dong
 */
public class CDSIndexProcessor implements I_DBProcessor {
	private boolean				_isFinished;
	private final FileWriter	_out;
	private final String		_indexName;
	private final List<String>	_missingCompany;
	private final List<String>	_missingCDS;

	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public CDSIndexProcessor(final String indexName, final String filePathName, final List<String> missingCompany,
			final List<String> missingCDS) throws IOException {
		_isFinished = false;
		_indexName = indexName;
		_out = new FileWriter( filePathName );
		_missingCDS = missingCDS;
		_missingCompany = missingCompany;
		_out.write( "Date" );
		_out.write( ',' );
		_out.write( "Last" );
		_out.write( ',' );
		_out.write( "Ask" );
		_out.write( '\n' );
	}

	/***********************************************************************
	 * {@link I_DBProcessor} methods
	 ***********************************************************************/
	/**
	 * This method gets called by db manager when a chunk of records has
	 * been read by all of the db readers. This is where this db processor
	 * writes all records to one merged output file.
	 */
	@Override
	public boolean processReaders(
			final long sequenceNumber,
			final int numReadersWithNewData,
			final LinkedList<I_DBReader> readers
			) {
		if (_isFinished) return false;

		try {
			List<String> missingData = new ArrayList<>();
			float ask = 0, last = 0;
			final Iterator<I_DBReader> readerIterator = readers.iterator();
			int available = 0;
			for (int i = 0; i < numReadersWithNewData; i++) {
				final I_DBReader reader = readerIterator.next();
				for (final CDSFields fields : ((CDSSingleNameReader) reader).getRecordsBuffer()) {
					if (!fields.isNull()) {
						ask += fields.getAsk();
						last += fields.getLast();
						available++;
					} else {
						missingData.add( ((CDSSingleNameReader) reader).getId() );
					}
				}
			}
			if (available != 0) {
				ask /= available;
				last /= available;
			}
			_out.append( ParseDate.standardFromLong( sequenceNumber ) );
			_out.append( ',' );
			_out.append( String.valueOf( last ) );
			_out.append( ',' );
			_out.append( String.valueOf( ask ) );
			if (missingData.size() != 0) {
				_out.append( ',' );
				_out.append( "missing:" );
				for (String s : missingData) {
					_out.append( s + ";" );
				}
			}
			_out.append( '\n' );

		} catch (final IOException e) {
			System.err.println( "error in processing " + _indexName + e.getMessage() );
			stop();
		}
		return true; // Not finished
	}

	/** Called by db manager to give this processor a chance to tie up loose ends */
	@Override
	public void stop() {
		try {
			if (_missingCompany != null && _missingCompany.size() != 0) {
				_out.append( "missingComany:" );
				for (String s : _missingCompany) {
					_out.append( s.replaceAll( ",", "" ) + ";" );
				}
				_out.append( '\n' );
			}
			if (_missingCompany != null && _missingCDS.size() != 0) {
				_out.append( "missingCDS:" );
				for (String s : _missingCDS) {
					_out.append( s.replaceAll( ",", "" ) + ";" );
				}
			}
			_out.append( '\n' );
			_out.flush();
			_out.close();
			_isFinished = true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
