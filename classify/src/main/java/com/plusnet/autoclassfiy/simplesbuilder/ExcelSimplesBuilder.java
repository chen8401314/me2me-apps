package com.plusnet.autoclassfiy.simplesbuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me2me.ml.core.type.TypeMapper;
import com.me2me.ml.core.type.TypeMgr;

import de.bwaldvogel.liblinear.Train;

/**
 * 通过excel创建训练样本。
 * 
 * @author zhangjiwei
 * @date Apr 28, 2017
 */
public class ExcelSimplesBuilder extends AbsSVMSimplesBuilder {
	private File inputFile;
	private File outputFile;
	private File trainFile;
	private final static String TYPE_MAPPING_FILE ="/svm/svm.model.mapping";
	private final static String TRAIN_FILE_EX = ".train";
	private final static String KEYWORD_FILE_EX = ".keywords";
	private final static String FILE_CHARSET = "UTF-8";
	private static Logger loger = LoggerFactory.getLogger(ExcelSimplesBuilder.class);
	class RowData{
		 String typeName;
		 String content;
		 String weightKeys;
	}
	public ExcelSimplesBuilder(File inputFile, File outputFile) {
		super();
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}
	public List<RowData> readExcelFile(){
		try {
			this.keywordService.reloadModel(null);
			Workbook wb = WorkbookFactory.create(inputFile);
			Sheet sheet = wb.getSheetAt(0);
			Iterator<Row> it =sheet.rowIterator();
			it.next();
			List<RowData> result = new ArrayList<>();
			
			while (it.hasNext()) {
				Row row = it.next();
				String typeName = row.getCell(0).getStringCellValue().trim();
				String value = row.getCell(1).getStringCellValue().trim();
				String weightKeys = row.getCell(2).getStringCellValue().trim();
				if(!StringUtils.isEmpty(typeName) && !StringUtils.isEmpty(value)){
					RowData rd = new RowData();
					rd.typeName=typeName;
					rd.content=value;
					rd.weightKeys=weightKeys;
					result.add(rd);
				}
			}
			File keyFile = new File(outputFile.getPath() + KEYWORD_FILE_EX);
			this.keywordService.saveTrainResult(keyFile);
			return result;
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void train() {
		try {
			//read file.
			List<RowData> dataList = this.readExcelFile();
			
			//load types
			InputStream in = ExcelSimplesBuilder.class.getResourceAsStream(TYPE_MAPPING_FILE);
			TypeMgr typeMgr= TypeMgr.loadTypes(in);
			in.close();
			StringBuilder sb = new StringBuilder();
			for(RowData data:dataList) {
				TypeMapper typeMapper = typeMgr.getTypeByName(data.typeName);
				int index= typeMapper.getIndex();
				Map<String,Float> weightKeywords =typeMapper.getWeightKeyMap();
				String line = buildLine(data.content, index,1000,weightKeywords);
				sb.append(line + "\n");
			}
			
			loger.info("save train file");
			trainFile = new File(outputFile.getPath() + TRAIN_FILE_EX);
			FileUtils.write(trainFile, sb.toString(), FILE_CHARSET);
			
			loger.info("train file");
			Train.main(new String[] { trainFile.getAbsolutePath(), outputFile.getAbsolutePath() });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String inputFile = "D:/svm-simples/simples-input.xlsx";
		String outputFile = "D:/svm-simples/svm.model";
		ExcelSimplesBuilder esb = new ExcelSimplesBuilder(new File(inputFile), new File(outputFile));
		esb.train();
	}

}
