package Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Zhenghong Dong
 */
public class CollectionUtils {

	public static List<Double> addDoubleList(List<Double> l1, List<Double> l2) throws Exception {
		List<Double> ret = new ArrayList<Double>(l1);
		
		if (l1.size() != l2.size() ){
				throw new Exception("Size of two list must be same");
		}
		ListIterator<Double> l2Ite = l2.listIterator();
		for (ListIterator<Double> l1Ite = ret.listIterator(); l1Ite.hasNext();) {
			l1Ite.set( l1Ite.next().doubleValue() + l2Ite.next().doubleValue() );
		}
		return ret;
	}
	
	public static List<Double> divideDoubleList(List<Double> l1, int num) {
		List<Double> ret = new ArrayList<Double>(l1);
		
		for (ListIterator<Double> iterator = ret.listIterator(); iterator.hasNext();) {
			iterator.set( iterator.next().doubleValue()/num );
		}
		return ret;
	}
	
	public static void main(String[] args) throws Exception {
		LinkedList<Double> l1 = new LinkedList<>();
		l1.add( 1.0);l1.add(2.0);l1.add( 3.0 );
		ArrayList<Double> l2 = new ArrayList<>();
		l2.add( 1.0);l2.add(2.0);l2.add( 3.0 );
		List<Double> l3 = addDoubleList(l1,l2);
		System.out.println(l3.get( 1 ));
		
		l3 = divideDoubleList( l3, 2 );
		System.out.println(l3.get( 1 ));
		
		System.out.println("End");
	}

}
