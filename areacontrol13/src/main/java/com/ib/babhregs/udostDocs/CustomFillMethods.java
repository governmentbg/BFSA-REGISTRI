package com.ib.babhregs.udostDocs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.words.Bookmark;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Font;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.ParagraphFormat;
import com.ib.babhregs.db.dao.RegistraturaDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Registratura;
import com.ib.babhregs.db.dto.ShablonBookmark;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;

public class CustomFillMethods {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomFillMethods.class);

    private final Vpisvane vpisvane;
    private final Doc doc;
    private final UserData userData;
    private final SystemData systemData;
    private final Date date;

    public CustomFillMethods(Vpisvane vpisvane, Doc doc, UserData userData, SystemData systemData) {
        this.vpisvane = vpisvane;
        this.doc = doc;
        this.userData = userData;
        this.systemData = systemData;
        this.date = new Date();
    }
    
    public void buildFooter(Document document, ShablonBookmark bookmark) {
    	Bookmark b = document.getRange().getBookmarks().get(bookmark.getLabel());
    	if(b == null) {
    		LOGGER.error("Документът не съдържа bookmark с името " + bookmark.getLabel());
    		return;
    	}
    	
    	try {
	    	DocumentBuilder builder = new DocumentBuilder(document);
			builder.moveToBookmark(bookmark.getLabel());
			
			RegistraturaDAO regDao = new RegistraturaDAO(userData);
	    	Registratura reg = regDao.findById(this.vpisvane.getRegistraturaId());
			
			// start writing
			Font font = builder.getFont();
			ParagraphFormat format = builder.getParagraphFormat();
			format.setAlignment(ParagraphAlignment.CENTER);
			format.setSpaceBefore(0);
			format.setSpaceAfter(0);
			font.setSize(12);
			font.setName("Segoe UI Symbol");
			builder.write("\u2709");
			font.setName("Calibri");
			builder.writeln(" " + reg.getAddress());
			font.setName("Wingdings");
			builder.write("\u0028");
			font.setName("Calibri");
			builder.write(reg.getContactPhone() + " ");
			font.setName("Segoe UI Symbol");
			builder.write("\uD83D\uDCE0");
			font.setName("Calibri");
			builder.write(" " + reg.getContactFax() + " ");
			font.setName("Segoe UI Symbol");
			font.setBold(true);
			builder.write("\uD83C\uDF10");
			font.setName("Calibri");
			font.setBold(false);
			builder.write(" " + reg.getContactEmail());
			// end writing
    	}
    	catch(Exception e) {
    		LOGGER.error("Документът не съдържа bookmark с името " + bookmark.getLabel(), e);
    	}
    }
}
