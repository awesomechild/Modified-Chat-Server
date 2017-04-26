package files.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueIdentifier {

	private static List<Integer> ids = new ArrayList<Integer>();

	private static final int RANGE = 10000;
	// Range is the amount of possible identifiers we want to generate
	// Total number of Clients or maybe when they leaves and wants to rejoin ->
	// then we will have to increment

	private static int index = 0;

	// because this is static it will run without a method
	// we dont have to call it it will exist
	static {
		for (int i = 0; i < RANGE; i++) {
			ids.add(i);
		}
		Collections.shuffle(ids);
	}

	private UniqueIdentifier() {
	}

	public static int getIdentifier() {
		if (index > ids.size() - 1)
			index = 0; // re-using the random numbers
		return ids.get(index++); // incrementing it after returning the id
		// index doesn't just return any random number it will return 0 1 2 3 4
		// 5 6 and so on . Therefore shuffling them
	}

}
