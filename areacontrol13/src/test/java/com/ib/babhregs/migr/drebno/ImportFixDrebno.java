package com.ib.babhregs.migr.drebno;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.Query;

import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.ib.babhregs.migr.invitro.InvitroMigrate;
import com.ib.system.db.JPA;
import com.ib.system.utils.FileUtils;
import com.ib.system.utils.SearchUtils;

public class ImportFixDrebno {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		try {
			
			
			String active = "Y";
			
			
			
			
			byte[] byteLic = FileUtils.getBytesFromFile(new File("D:\\Bullshit.iv"));
			ByteArrayInputStream bisL = new ByteArrayInputStream(byteLic);
				
			com.aspose.words.License license = new com.aspose.words.License();
			license.setLicense(bisL);
			
			byte[] bytes = FileUtils.getBytesFromFile(new File("D:\\_VLP\\Регистър на издадените лицензи за търговия на дребно с ВМП.doc"));
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			Document doc = new Document(bis);
			
			NodeCollection<Node> tables = doc.getChildNodes(NodeType.TABLE, true);
			if (tables.getCount() != 2) {
				System.out.println("много таблицииииииииииииииии !!!!!!!!!!!!!!!!!!!" + tables.getCount() );
				return;
			}
			
			Node table = tables.get(0);
			if (active.equals("N")) {
				table = tables.get(1);
			}
			
			
			JPA.getUtil().begin();
			
			NodeCollection<Node> rows = ((CompositeNode<Node>) table).getChildNodes(NodeType.ROW, true);
			//System.out.println("Number of rows : " + rows.getCount());			
			for (int r = 1; r < rows.getCount(); r++) {
				Node row = rows.get(r);
				NodeCollection<Node> cells = ((CompositeNode<Node>) row).getChildNodes(NodeType.CELL, true);
				if (cells.getCount() > 0) {
					
					String nom = ""+r;					
					String razreshenie = InvitroMigrate.clearString(cells.get(1).getText());
					String firm = InvitroMigrate.clearString(cells.get(2).getText());
					String addr = InvitroMigrate.clearString(cells.get(3).getText());
					String upr = InvitroMigrate.clearString(cells.get(4).getText());
					String dopInfo = InvitroMigrate.clearString(cells.get(5).getText());
					String dopInfo2 = InvitroMigrate.clearString(cells.get(6).getText());
					
					
					System.out.println("nom="  + nom  + " razr=" + razreshenie);
					
					if (! SearchUtils.isEmpty(dopInfo2)) {
						//System.out.println("nom="  + nom  + " d=" + dopInfo2);
						
						Query query = JPA.getUtil().getEntityManager().createNativeQuery("update temp_drebno set notes = :notes where active = 'Y' and nom = :nom");
						query.setParameter("nom", nom);
						query.setParameter("notes", dopInfo2);
						query.executeUpdate();
						//System.out.println(query.getResultList().size());
					}
//					
////					
//					Query q = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO temp_drebno (nom, razreshenie, firm, addr, upr, dop_info, active) VALUES (?, ?, ?, ?, ?, ?, ?);");
//					q.setParameter(1, nom);
//					q.setParameter(2, razreshenie);
//					q.setParameter(3, firm);
//					q.setParameter(4, addr);
//					q.setParameter(5, upr);
//					q.setParameter(6, dopInfo);
//					q.setParameter(7, active);
//
//					q.executeUpdate();
				}
				
			}
				
			JPA.getUtil().commit();
			
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JPA.getUtil().rollback();
		}finally {
			JPA.getUtil().closeConnection();
		}

	}
	
	
	
	
	
	

}
