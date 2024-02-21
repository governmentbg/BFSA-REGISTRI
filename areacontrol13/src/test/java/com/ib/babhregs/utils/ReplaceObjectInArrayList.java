package com.ib.babhregs.utils;

import java.util.ArrayList;

import com.ib.babhregs.db.dto.VlpVeshtva;

public class ReplaceObjectInArrayList {

	public static void main(String[] args) {
		 ArrayList<VlpVeshtva> list = new ArrayList<>();
		 VlpVeshtva v = new VlpVeshtva();
		 VlpVeshtva tmpv = new VlpVeshtva();
		 v.setId(1);
		 v.setQuantity("testNK");
		 list.add(v);
		 VlpVeshtva v2 = new VlpVeshtva();
		 v2.setId(2);
		 v2.setQuantity("popo--popo");
		 list.add(v2);
		 System.out.println("purvi put spisyk:" + list.toString());
		 tmpv.setId(1);
	        // Get the index of the object to be replaced.
	        int index = list.indexOf(tmpv);

	        // Replace the object at the specified index with a new object.
	        list.set(index, tmpv);

	        // Print the updated list.
	        System.out.println(list);
	}

}
