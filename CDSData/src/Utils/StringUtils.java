package Utils;

/**
 * @author Zhenghong Dong
 */
public class StringUtils {

	public static int firstOccuranceOfArray(String obj, String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			if (obj.indexOf( strings[i] ) > -1) return obj.indexOf( strings[i] );
		}
		return -1;
	}
	
	public static void main(String[] args) {
		String name = "121 Weatherford International, Ltd. ENRG, HVOL GPE58HAB2 WFT 4.5 15Apr22 94707VAC4 0.008";
		String[] list = {"FIN","INDU","ENRG","CONS","TMT"};
		
		name = name.substring( name.indexOf( " " ) + 1,firstOccuranceOfArray( name, list ) - 1);
		System.out.println(name);
		
		if (name.lastIndexOf( " " ) > -1) {
			name = name.substring( 0, name.lastIndexOf( " " ) );
		}
		System.out.println(name);
		// get rid of last posible ,
		if (name.endsWith( "," )) {
			name = name.substring( 0, name.lastIndexOf( "," ) );
		}
		System.out.println(name);
		name += " CDS SR 5Y CORP";
		System.out.println(name);
		
	}

}
