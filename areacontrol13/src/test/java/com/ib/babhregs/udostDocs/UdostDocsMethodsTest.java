package com.ib.babhregs.udostDocs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.s;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.aspose.words.Bookmark;
import com.aspose.words.Cell;
import com.aspose.words.CompositeNode;
import com.aspose.words.ControlChar;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.License;
import com.aspose.words.ListLevel;
import com.aspose.words.ListTemplate;
import com.aspose.words.Node;
import com.aspose.words.NodeType;
import com.aspose.words.NumberStyle;
import com.aspose.words.Paragraph;
import com.aspose.words.Row;
import com.aspose.words.SaveFormat;
import com.aspose.words.Table;
import com.ib.babhregs.db.dao.ShablonLogicDAO;
import com.ib.babhregs.db.dto.ShablonBookmark;
import com.ib.babhregs.db.dto.ShablonBookmark.BookmarkTypes;
import com.ib.babhregs.db.dto.ShablonLogic;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.SystemClassifDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SysClassifUtils;

public class UdostDocsMethodsTest {

	private SystemData systemData;
	
	@Before
    public void setUp() throws Exception {
        
        //dao = new RegisterOptionsDAO(ActiveUser.DEFAULT);
        //this.systemData = new SystemData();
    }

	@Test
	@Ignore
	public void testAsposeBookmark() throws Exception {
		String filename = "bookmark.docx";
		String result = "result.docx";
		
		License license = new License();
		String nameLicense = "Aspose.Words.lic";
		InputStream inp = getClass().getClassLoader().getResourceAsStream(nameLicense);
		license.setLicense(inp);
		
		
		File file = new File(filename);
		InputStream input = new FileInputStream(file);
		Document asposeDoc = new Document(input);
		
		//asposeDoc.getRange().getBookmarks().get("bgWrap").setText("");
		asposeDoc.getRange().getBookmarks().get("bgNom").setText("941204");
		asposeDoc.getRange().getBookmarks().get("chWrap").setText("");
		asposeDoc.getRange().getBookmarks().get("chNom").setText("234265");

		
		OutputStream output = new FileOutputStream(new File(result));
		asposeDoc.save(output, SaveFormat.DOCX);
		
		input.close();
		output.close();
		
	}
	
	@Test
	@Ignore
	public void testAsposeTable() throws Exception {
		String filename = "table.docx";
		String bm = "table";
		String result = "result.docx";
		
		License license = new License();
		String nameLicense = "Aspose.Words.lic";
		InputStream inp = getClass().getClassLoader().getResourceAsStream(nameLicense);
		license.setLicense(inp);
		
		
		File file = new File(filename);
		InputStream input = new FileInputStream(file);
		Document asposeDoc = new Document(input);
		
		Bookmark bookmark = asposeDoc.getRange().getBookmarks().get(bm);
		
		DocumentBuilder builder = new DocumentBuilder(asposeDoc);
		/*
		builder.moveToBookmark(bm);
		
		builder.endBookmark(bm);
		
		System.out.println(builder.getCurrentNode());
		builder.write(null);
		builder.insertCell();
		builder.endRow();
		*/
		
		builder.moveToCell(0, 1, 0, 0);
		// 
		builder.write("Лиско");
		builder.insertCell();
		/*
		builder.write("1");
		builder.endRow();
		builder.insertCell();
		builder.write("Врабчо");
		builder.insertCell();
		builder.write("2");
		builder.endRow();
		builder.endTable();*/
		//
		
		OutputStream output = new FileOutputStream(new File(result));
		asposeDoc.save(output, SaveFormat.DOCX);
		
		input.close();
		output.close();
		
	}
	
	@Test
	@Ignore
	public void testFillTable() throws Exception {
		String filename = "table.docx";
		String bm = "table";
		String result = "result.docx";
		
		License license = new License();
		String nameLicense = "Aspose.Words.lic";
		InputStream inp = getClass().getClassLoader().getResourceAsStream(nameLicense);
		license.setLicense(inp);
		
		File file = new File(filename);
		InputStream input = new FileInputStream(file);
		Document asposeDoc = new Document(input);
		
		
		
		
		
		


        

        Bookmark b = asposeDoc.getRange().getBookmarks().get("table");
        CompositeNode node = b.getBookmarkStart().getParentNode();
        Table targetTable = null;
        while(node != null) {
        	System.out.print(node);
        	if(node instanceof Table) {
        		System.out.print(" <---- eto ia!");
        		targetTable = (Table) node;
        	}
        	System.out.println();
        	
        	node = node.getParentNode();
        }
        
        if (targetTable != null) {
            int tableRows = targetTable.getRows().getCount();
        	for(int i = 1; i < tableRows; i++) {
        		Row row = targetTable.getRows().get(1);
        		targetTable.getRows().remove(row);
        	}
        	List<List<String>> data = new ArrayList<>();
        	data.add(Arrays.asList("врабчета", "2"));
        	data.add(Arrays.asList("синигери", "2"));
        	data.add(Arrays.asList("кос", "1"));
        	
        	DocumentBuilder builder = new DocumentBuilder(asposeDoc);
        	
        	for(List<String> dataRow : data) {
        		Row row = new Row(asposeDoc);
        		targetTable.appendChild(row);
        		for(String dataCell : dataRow) {
        			Cell cell = new Cell(asposeDoc);
        			row.appendChild(cell);
        			cell.appendChild(new Paragraph(asposeDoc));
        			builder.moveTo(cell.getFirstParagraph());
        			builder.write(dataCell);
        		}
        	}
        	
        	/*
            // Add data to the table
            Row row = targetTable.getRows().get(1);
            Cell cell1 = row.getCells().get(0);
            Cell cell2 = row.getCells().get(1);

            // Set data in the cells
            builder = new DocumentBuilder(asposeDoc);
            builder.moveTo(cell1.getFirstParagraph());
            builder.write("Data for cell 1");

            builder.moveTo(cell2.getFirstParagraph());
            builder.write("Data for cell 2");
            
            Row r = new Row(asposeDoc);
            r.appendChild(new Cell(asposeDoc));
            r.appendChild(new Cell(asposeDoc));
            r.appendChild(new Cell(asposeDoc));
            
           targetTable.appendChild(r);
           
           Row r2 = new Row(asposeDoc);
           r2.appendChild(new Cell(asposeDoc));
           targetTable.appendChild(r2);
           */
            
        }


        // Save the document with the updated data
        OutputStream output = new FileOutputStream(new File(result));
		asposeDoc.save(output, SaveFormat.DOCX);
		
		input.close();
		output.close();
	}

	@Test
	public void test() {
		List<String> list = Arrays.asList("fff", "dd", "vv", "as");
		List<String> result = list
				.stream()
				.filter(s -> s.equals("asdasdad"))
				.collect(Collectors.toList());
		result.get(0);
		int a = 1;
	}
	
	@Test
	@Ignore
	public void testAsposeList() throws Exception {
		String filename = "240.docx";
		String bm = "vetDoktoriList";
		String result = "result.docx";
		
		License license = new License();
		String nameLicense = "Aspose.Words.lic";
		InputStream inp = getClass().getClassLoader().getResourceAsStream(nameLicense);
		license.setLicense(inp);
		
		File file = new File(filename);
		InputStream input = new FileInputStream(file);
		Document asposeDoc = new Document(input);
		
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		
		
		com.aspose.words.List asposeList = asposeDoc.getLists().add(ListTemplate.NUMBER_ARABIC_DOT);
		ListLevel level1 = asposeList.getListLevels().get(0);
		level1.setNumberStyle(NumberStyle.ARABIC);
		level1.setNumberFormat("\u0000.");

		ListLevel level2 = asposeList.getListLevels().get(1);
		level2.setNumberStyle(NumberStyle.ARABIC);
		level2.setNumberFormat("\u0000.\u0001");
		
    	DocumentBuilder builder = new DocumentBuilder(asposeDoc);
    	builder.moveToBookmark(bm);
    	
    	builder.getListFormat().setList(asposeList);
		
    	builder.writeln("Text1_Level1");
    	builder.writeln("Text2_Level1");

    	builder.getListFormat().listIndent();
    	builder.writeln("Text1_Level2");
    	builder.writeln("Text2_Level2");

    	builder.getListFormat().listOutdent();
    	builder.writeln("Text3_Level1");

    	builder.getListFormat().removeNumbers();

    	builder.endBookmark(bm);
    	builder.writeln("End list");
		
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		
		// Save the document with the updated data
        OutputStream output = new FileOutputStream(new File(result));
		asposeDoc.save(output, SaveFormat.DOCX);
		
		input.close();
		output.close();
		
	}
	
	@Test
	@Ignore
	public void testJoin() {
		List<String> animals = new ArrayList<>();
		
		animals.add("vrab4o");
		animals.add("");
		animals.add("siniger4o");
		animals.add("kose");
		animals.add("");
		
		String result;
		boolean addI = false;
		
		if(animals.isEmpty()) {
			result = null;
		}
		else if(animals.size() == 1) {
			result = animals.get(0);
		}
		else {
			String joined = String.join(", ", animals);
			
			
			while(joined.lastIndexOf(", ") == joined.length() - 2) {
				joined = joined.substring(0, joined.lastIndexOf(", "));
			}
			
			result = addI
					? joined.substring(0, joined.lastIndexOf(", ")) + " и " + joined.substring(joined.lastIndexOf(", ") + 2)
					: joined;
		}
		
		System.out.println(result);
	}
	
	
	
	@Test
	@Ignore
	public void testFillTableJivotni() throws Exception {
		String filename = "212.docx";
		String bm = "tableJivotni";
		String result = "result.docx";
		
		License license = new License();
		String nameLicense = "Aspose.Words.lic";
		InputStream inp = getClass().getClassLoader().getResourceAsStream(nameLicense);
		license.setLicense(inp);
		
		File file = new File(filename);
		InputStream input = new FileInputStream(file);
		Document asposeDoc = new Document(input);

        Bookmark b = asposeDoc.getRange().getBookmarks().get(bm);
        CompositeNode node = b.getBookmarkStart().getParentNode();
        Table targetTable = null;
        while(node != null) {
        	System.out.print(node);
        	if(node instanceof Table) {
        		System.out.print(" <---- eto ia!");
        		targetTable = (Table) node;
        		break;
        	}
        	System.out.println();
        	
        	node = node.getParentNode();
        }
        
        if (targetTable != null) {
            int tableRows = targetTable.getRows().getCount();
        	for(int i = 1; i < tableRows; i++) {
        		Row row = targetTable.getRows().get(1);
        		targetTable.getRows().remove(row);
        	}
        	List<List<String>> data = new ArrayList<>();
        	data.add(Arrays.asList("врабчета", "2"));
        	data.add(Arrays.asList("синигери", "2"));
        	data.add(Arrays.asList("кос", "1"));
        	
        	DocumentBuilder builder = new DocumentBuilder(asposeDoc);
        	
        	for(List<String> dataRow : data) {
        		Row row = new Row(asposeDoc);
        		targetTable.appendChild(row);
        		for(String dataCell : dataRow) {
        			Cell cell = new Cell(asposeDoc);
        			row.appendChild(cell);
        			cell.appendChild(new Paragraph(asposeDoc));
        			builder.moveTo(cell.getFirstParagraph());
        			builder.write(dataCell);
        		}
        	}
        }


        // Save the document with the updated data
        OutputStream output = new FileOutputStream(new File(result));
		asposeDoc.save(output, SaveFormat.DOCX);
		
		input.close();
		output.close();
	}
	


	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	@Test
	public void testClassifTree() throws DbErrorException {
		// 73, 185, 161
		System.out.println("---------------------");
		List<SystemClassif> p1 = tree(71);
		System.out.println("---------------------");
		List<SystemClassif> p2 = tree(72);
		System.out.println("---------------------");
		//List<SystemClassif> p3 = tree(105);
		//System.out.println("---------------------");
		List<SystemClassif> p4 = tree(77);
		System.out.println("---------------------");
		
		
		System.out.println("---------------------");
		List<SystemClassif> merged = merge(p1, p2, p4);
		merged.forEach(c -> {
			try {
				System.out.println(String.format("[%d]\t[%d]\t%s",
						c.getCode(),
						c.getCodeParent(), 
						this.systemData.decodeItem(
								BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ,
								c.getCode(),
								1,
								new Date())));
			}
			catch (DbErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		System.out.println("---------------------");
		
		int parentestParent = merged.stream().min((s1, s2) -> s1.getCodeParent() - s2.getCodeParent()).map(SystemClassif::getCodeParent).orElse(0);
		
		List<SystemClassif> resultList = new ArrayList<>();
		List<Integer> levels = new ArrayList<>();
		
		addToResultList(resultList, merged, parentestParent, levels, 0);
		
		
		System.out.println("---------- Final result -------------");
		resultList.stream().forEach(System.out::println);
		for(int i = 0; i < resultList.size(); i++) {
			System.out.println(String.format("[%d]\t[%d]\t[%d]\t%s", levels.get(i), resultList.get(i).getCode(), resultList.get(i).getCodeParent(), this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ,
					resultList.get(i).getCode(),
					1,
					new Date())));
		}
		
		
		System.out.println("---------- Left in collection -------------");
		merged.stream().forEach(System.out::println);
		
		
		System.out.println("---------- END ALGORITHM -------------");
		System.out.println();
		
		for(int i = 0; i < resultList.size(); i++) {
			String spaces = "";
			for(int j = 0; j < levels.get(i); j++) {
				spaces +="....";
			}
			System.out.println(String.format("%s%s (%d)", spaces, this.systemData.decodeItem(BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ, resultList.get(i).getCode(), 1,new Date()), resultList.get(i).getCode()));
		}
		
		System.out.println();
		System.out.println("---------- END Program -------------");
		int i = 0;
	}
	
	private List<SystemClassif> tree(Integer code) throws DbErrorException {
		List<SystemClassif> bigClassif = this.systemData.getSysClassification(BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ, new Date(), 1);
		
		SystemClassifDAO dao = new SystemClassifDAO(ActiveUser.DEFAULT);
		SystemClassif c = dao.findByCode(BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ, code, true);
		
		List<SystemClassif> parents = SysClassifUtils.getParents(bigClassif, c);
		parents.forEach(p -> {
			try {
				System.out.println(String.format("[%d]\t[%d]\t%s",
						p.getCode(),
						p.getCodeParent(), 
						this.systemData.decodeItem(
								BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ,
								p.getCode(),
								1,
								new Date())));
			}
			catch (DbErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return parents;
	}
	
	private List<SystemClassif> merge(Collection<SystemClassif>... collection) {
		List<SystemClassif> merged = new ArrayList<>();
		
		for(Collection<SystemClassif> c : collection) {
			//merged.addAll(c);
			
			for(SystemClassif s : c) {
				boolean contained = false;
				for(SystemClassif s1 : merged) {
					if(s1.equals(s)) {
						contained = true;
						break;
					}
				}
				
				if(!contained) {
					merged.add(s);
				}
				
			}
			
		}
		
		return merged;
	}

	private void addToResultList(List<SystemClassif> resultList, List<SystemClassif> collection, Integer parentCode, List<Integer> levels, int depthLevel) {
		if(collection.isEmpty()) return; // da spre rekursiata
		
		List<SystemClassif> childToAdd = 
				collection
					.stream()
					.filter(s -> s.getCodeParent() == parentCode)
					.collect(Collectors.toList());
		
		for(SystemClassif c : childToAdd) {
			int index = collection.indexOf(c);
			collection.remove(index); // tova raboti
			
			int indexWhereToAdd = resultList.size();
			
			if(resultList.isEmpty()) {
				indexWhereToAdd = 0;
			}
			else {
				for(int i = 0; i < resultList.size(); i++) {
					if(resultList.get(i).getCode() == parentCode) {
						indexWhereToAdd = i + 1;
						// da se proveri sledva6tite elementi dali sa siblings
						int j = indexWhereToAdd;
						while(j < resultList.size() && resultList.get(j) != null && resultList.get(j).getCodeParent() == c.getCodeParent()) {
							j++;
						}
						indexWhereToAdd = j;
						break;
					}
				}
			}
			
			resultList.add(indexWhereToAdd, c);
			levels.add(indexWhereToAdd, depthLevel);
		}
		
		System.out.println("---------- Left in the collection -----------");
		collection.stream().forEach(System.out::println);
		System.out.println("---------- Added to results -----------");
		resultList.stream().forEach(System.out::println);
		System.out.println("---------- End of addToResultList iteration for codeParent " + parentCode +  " -----------");
	
		int i = 0;
		
		for (SystemClassif newlyAddedChild : childToAdd) {
			addToResultList(resultList, collection, newlyAddedChild.getCode(), levels, (depthLevel + 1));
		}
	}
	
	
	@Test
	public void testSplit() throws Exception {
		String result = "result.docx";
		
		License license = new License();
		String nameLicense = "Aspose.Words.lic";
		InputStream inp = getClass().getClassLoader().getResourceAsStream(nameLicense);
		license.setLicense(inp);
		
		Document document = new Document();
		DocumentBuilder builder = new DocumentBuilder(document);
		builder.moveToDocumentStart();
		
		String s = "531<*_*>7<*_*>0<*_*>2<._.>531<*_*>65<*_*>1<*_*>2<._.>531<*_*>73<*_*>2<*_*>1<._.>531<*_*>34<*_*>1<*_*>1<._.>531<*_*>4<*_*>1<*_*>2<._.>531<*_*>86<*_*>2<*_*>2<._.>531<*_*>166<*_*>3<*_*>1<._.>531<*_*>1<*_*>1<*_*>1";
		
		String separator = "<._.>";
		String[] rows = s.split(Pattern.quote(separator));
		String indent = ControlChar.TAB;
		
		for(String row : rows) {
			separator = "<*_*>";
			String[] fields = row.split(Pattern.quote(separator));

			if(Integer.valueOf(fields[3]).equals(BabhConstants.CODE_ZNACHENIE_DA)) {
				builder.getFont().setBold(true);
			}
			
			for(int i = 0; i < Integer.valueOf(fields[2]); i++) builder.write(indent);
			builder.write(this.systemData.decodeItem(Integer.valueOf(fields[0]), Integer.valueOf(fields[1]), 1,new Date()));

			builder.getFont().setBold(false);
			builder.writeln();
		}
		
		
		OutputStream output = new FileOutputStream(new File(result));
		document.save(output, SaveFormat.DOCX);
		
		output.close();
		
	}
}


