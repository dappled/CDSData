package indexMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import Utils.ParseDate;
import dataWrapper.CDSFields;

/**
 * This is an implementation of {@link I_DBProcessor}, it merges several quote files to a new GZfile
 * @author Zhenghong Dong
 */
public class CDSSingleNameProcessor implements I_DBProcessor {
	private boolean				_isFinished;
	private final FileWriter	_out;
	private final String		_name;

	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public CDSSingleNameProcessor(final String name, final String filePathName) throws IOException {
		_isFinished = false;
		_name = name;
		_out = new FileWriter( filePathName );
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
			float ask = 0, last = 0;
			final Iterator<I_DBReader> readerIterator = readers.iterator();
			for (int i = 0; i < numReadersWithNewData; i++) {
				final I_DBReader reader = readerIterator.next();
				for (final CDSFields fields : ((CDSSingleNameReader) reader).getRecordsBuffer()) {
					ask += fields.getAsk();
					last += fields.getLast();
				}
			}
			_out.append( ParseDate.standardFromLong( sequenceNumber ) );
			_out.append( ',' );
			_out.append( String.valueOf( last ) );
			_out.append( ',' );
			_out.append( String.valueOf( ask ) );
			_out.append( '\n' );
			
		} catch (final IOException e) {
			System.err.println( "error in processing " + _name + e.getMessage() );
			stop();
		}
		return true; // Not finished
	}

	/** Called by db manager to give this processor a chance to tie up loose ends */
	@Override
	public void stop() {
		try {
			_out.flush();
			_out.close();
			_isFinished = true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
