package com.ib.babhregs.migr.invitro;

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
import com.ib.system.db.JPA;
import com.ib.system.utils.FileUtils;

public class ImportInvitro {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		try {
			
			List<Object[]> allInvitro = JPA.getUtil().getEntityManager().createNativeQuery("select id, naim_s, cert_s, bolesti_s, firmi_s, notes_s from temp_invitro order by id").getResultList();
			
			
			
			
			
			byte[] byteLic = FileUtils.getBytesFromFile(new File("D:\\Bullshit.iv"));
			ByteArrayInputStream bisL = new ByteArrayInputStream(byteLic);
				
			com.aspose.words.License license = new com.aspose.words.License();
			license.setLicense(bisL);
			
			byte[] bytes = FileUtils.getBytesFromFile(new File("D:\\_VLP\\Registar IVMS- raboten - LAST.doc"));
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			Document doc = new Document(bis);
			
			NodeCollection<Node> tables = doc.getChildNodes(NodeType.TABLE, true);
			if (tables.getCount() != 1) {
				System.out.println("много таблицииииииииииииииии !!!!!!!!!!!!!!!!!!!" + tables.getCount() );
				return;
			}
			Node table = tables.get(0);
			
			
			JPA.getUtil().begin();
			
			NodeCollection<Node> rows = ((CompositeNode<Node>) table).getChildNodes(NodeType.ROW, true);
			//System.out.println("Number of rows : " + rows.getCount());			
			for (int r = 2; r < rows.getCount(); r++) {
				Node row = rows.get(r);
				NodeCollection<Node> cells = ((CompositeNode<Node>) row).getChildNodes(NodeType.CELL, true);
				if (cells.getCount() > 0) {
					
					String cert = InvitroMigrate.clearString(cells.get(1).getText());					
					String notes = InvitroMigrate.clearString(cells.get(4).getText());
					
					String pritejatel = null;
					String proizvoditel = null;
					
					String pp[] = notes.split("-");
					if (pp.length == 1) {
						pritejatel = pp[0].trim();
						proizvoditel = pp[0].trim();
					}else {
						pritejatel = pp[1].trim();
						proizvoditel = pp[0].trim();
					}
					
					Query q = JPA.getUtil().getEntityManager().createNativeQuery("update temp_invitro set proizvoditel = ?, pritejatel = ? where id = ?");
					q.setParameter(1, proizvoditel);
					q.setParameter(2, pritejatel);
					q.setParameter(3, allInvitro.get(r-2)[0]);
					q.executeUpdate();
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
	
	@SuppressWarnings("unchecked")
	public static void main2(String[] args) {
		
		try {
			byte[] byteLic = FileUtils.getBytesFromFile(new File("D:\\Bullshit.iv"));
			ByteArrayInputStream bisL = new ByteArrayInputStream(byteLic);
				
			com.aspose.words.License license = new com.aspose.words.License();
			license.setLicense(bisL);
			
			byte[] bytes = FileUtils.getBytesFromFile(new File("D:\\_VLP\\Registar IVMS- raboten (003).docx"));
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			Document doc = new Document(bis);
			
			NodeCollection<Node> tables = doc.getChildNodes(NodeType.TABLE, true);
			if (tables.getCount() != 1) {
				System.out.println("много таблицииииииииииииииии !!!!!!!!!!!!!!!!!!!" + tables.getCount() );
				return;
			}
			Node table = tables.get(0);
			
			NodeCollection<Node> rows = ((CompositeNode<Node>) table).getChildNodes(NodeType.ROW, true);
			//System.out.println("Number of rows : " + rows.getCount());
			JPA.getUtil().begin();
			for (int r = 2; r < rows.getCount(); r++) {
				Node row = rows.get(r);
				NodeCollection<Node> cells = ((CompositeNode<Node>) row).getChildNodes(NodeType.CELL, true);
//				System.out.println("Row " + (r+1) + ": Number of cells: " + cells.getCount());
//				for (int c = 0; c < cells.getCount(); c++) {
//					
//					String textCell = cells.get(c).getText();
//					textCell = textCell.replace("", "");
//					
//					System.out.print("\t" + textCell );
//				}
//				System.out.println();
				
				if (cells.getCount() > 0) {
					String naim = InvitroMigrate.clearString(cells.get(0).getText());
					String cert = InvitroMigrate.clearString(cells.get(1).getText());
					String bolesti = InvitroMigrate.clearString(cells.get(2).getText());
					String firmi = InvitroMigrate.clearString(cells.get(3).getText());
					String notes = InvitroMigrate.clearString(cells.get(4).getText());
					if (notes == null) {
						notes = "";
					}
					
					System.out.println(cert);
					
					Query q = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO temp_invitro (id, naim_s, cert_s, bolesti_s, firmi_s, notes_s) VALUES (:id, :naim, :cert, :bolesti, :firmi, :notes)");
					q.setParameter("id", r);
					q.setParameter("naim", naim);
					q.setParameter("cert", cert);
					q.setParameter("bolesti", bolesti);
					q.setParameter("firmi", firmi);
					q.setParameter("notes", notes);
					
					q.executeUpdate();
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
