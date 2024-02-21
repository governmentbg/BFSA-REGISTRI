package bg.egov.eforms.utils;

import java.util.ArrayList;
import java.util.List;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.system.db.dto.Files;
import com.ib.babhregs.db.dto.Referent;

public class EgovContainer {
	
	public Doc doc;
	
	public Vpisvane vpisvane;
	
	public List<Files> files;
	
	public Referent  ref1;
	
	public Referent  ref2;
	
	public Referent  ref3;
	
	public List<String> warnings = new ArrayList<String>();
	
	public List<Mps> allMps = new ArrayList<Mps>();
	

}
