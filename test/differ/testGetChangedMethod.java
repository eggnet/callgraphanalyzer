package differ;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import differ.filediffer.methodResult;

public class testGetChangedMethod {

	@Test
	public void testGetChangedMethods() {
		filediffer mydiffer = new filediffer("emptyText", "emptyText");
		
		ArrayList<methodResult> results = mydiffer.getChangedMethods("private void myfunction(int haha, custom hoho)");
		
		assertEquals(results.size(), 1);
	}

}
