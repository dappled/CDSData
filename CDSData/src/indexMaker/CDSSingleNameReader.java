package indexMaker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import Utils.ParseDate;
import dataWrapper.CDSFields;

public class CDSSingleNameReader implements I_DBReader, Comparable<CDSSingleNameReader> {
	private final int					_nRecs;
	private final String				_id;
	private int							_lastSequeneNumberRead;
	private boolean						_isFinished;

	private int							_gaps;

	private final LinkedList<CDSFields>	_recordsBuffer;

	// Record fields
	protected Long[]					_date;
	protected Float[]					_lastPrice;
	protected Float[]					_askPrice;

	public long getDate(final int index) {
		return _date[ index ];
	}

	public float getLastPrice(final int index) {
		return _lastPrice[ index ];
	}

	public float getAskPrice(final int index) {
		return _askPrice[ index ];
	}

	public int getNRecs() {
		return _nRecs;
	}

	public LinkedList<CDSFields> getRecordsBuffer() {
		return _recordsBuffer;
	}

	public String getId() {
		return _id;
	}

	/**
	 * Constructor - Opens a csv cds data file and reads entire contents into memory.
	 * 
	 * @param filePathName Name of gzipped TAQ quotes file to read
	 * @throws IOException
	 */
	public CDSSingleNameReader(final String id, final String filePathName) throws IOException {
		_id = id;
		_isFinished = false;
		_lastSequeneNumberRead = 0;
		_gaps = 0;

		_recordsBuffer = new LinkedList<CDSFields>();

		final ArrayList<Long> date = new ArrayList<>();
		final ArrayList<Float> last = new ArrayList<>();
		final ArrayList<Float> ask = new ArrayList<>();
		// Open file
		BufferedReader in = null;
		try {
			in = new BufferedReader( new FileReader( filePathName ) );
			String str;
			String[] line;
			String[] day;
			in.readLine(); // ignore header
			while ((str = in.readLine()) != null) {
				line = str.split( "," );
				day = line[ 0 ].split( "-" );
				if (day.length == 1) { // my time version
					date.add( ParseDate.longFromStandard( line[ 0 ] ) );
				} else { // dashen's time version
					date.add( Long.parseLong( day[ 0 ] ) * 10000 + Long.parseLong( day[ 1 ] ) * 100
							+ Long.parseLong( day[ 2 ] ) );
				}
				//
				last.add( Float.parseFloat( line[ 1 ] ) );
				ask.add( Float.parseFloat( line[ 2 ] ) );
			}
		} catch (final FileNotFoundException e) {
			throw e;
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (NullPointerException e) {}
		}

		_date = date.toArray( new Long[ date.size() ] );
		_lastPrice = last.toArray( new Float[ last.size() ] );
		_askPrice = ask.toArray( new Float[ ask.size() ] );
		_nRecs = date.size();
	}

	@Override
	public int readChunk(final long sequenceNum) {
		// Check for finished in a previous read

		if (isFinished()) return 0;

		// Clear list of records to prepare for new records
		_recordsBuffer.clear();

		// Check for sequence number that is ahead of
		// specified sequence number - Nothing to read

		if (_date[ _lastSequeneNumberRead ] > sequenceNum) {
			if (_lastSequeneNumberRead == 0) return 0;
			else if (++_gaps >= 24) return -1; // inactive CDS, no data for half year
			return 0; // // no data befor first date
			/* System.err.println( "No weekly data for " + _id + " on week of " + sequenceNum
			 * + ", will use previous data "
			 * + _date[ _lastSequeneNumberRead - 1 ] ); */
		}

		// Record reading loop - Exit when we encounter a sequence number
		// that is above the one we want
		else {
			try {
				while (_date[ _lastSequeneNumberRead ] <= sequenceNum) {
					_lastSequeneNumberRead++;
				}
				_gaps = 0;
			} catch (IndexOutOfBoundsException e) {
				stop();
			}
		}
		try {
			_recordsBuffer
					.add( new CDSFields( _askPrice[ _lastSequeneNumberRead - 1 ],
							_lastPrice[ _lastSequeneNumberRead - 1 ] ) );
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Return number of records in our records buffer
		return 1;
	}

	@Override
	public long getSequenceNumber() {
		return _lastSequeneNumberRead;
	}

	@Override
	public void stop() {
		_isFinished = true;
	}

	@Override
	public boolean isFinished() {
		return _isFinished;
	}

	@Override
	public long getLastSequenceNumberRead() {
		return _lastSequeneNumberRead - 1;
	}

	@Override
	public int compareTo(final CDSSingleNameReader o) {
		return Long.compare( getSequenceNumber(), o.getSequenceNumber() );
	}

	@Override
	public void reset() {
		_isFinished = false;
		_lastSequeneNumberRead = 0;
		_gaps = 0;
	}

	/**
	 * Example of using this class to read a TAQ quotes file and access
	 * individual records.
	 */
	/* public static void example1() {
	 * String f =
	 * "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\data\\CDS\\histprice\\csv\\AA CDS USD SR 5Y Corp.csv"
	 * ;
	 * try {
	 * // Read entire TAQ quotes file into memory
	 * CDSSingleNameReader taqQuotes = new CDSSingleNameReader( f );
	 * // Iterate over all records, writing the contents of each to the console
	 * System.out.println( taqQuotes.getNRecs() );
	 * int nRecs = taqQuotes.getNRecs();
	 * for (int i = 0; i < nRecs; i++) {
	 * System.out.println(
	 * taqQuotes.getDate( i )
	 * + ","
	 * + taqQuotes.getLastPrice( i )
	 * + ","
	 * + taqQuotes.getAskPrice( i )
	 * );
	 * }
	 * } catch (IOException e1) {
	 * e1.printStackTrace();
	 * }
	 * }
	 * public static void main(String[] args) {
	 * example1();
	 * } */
}
