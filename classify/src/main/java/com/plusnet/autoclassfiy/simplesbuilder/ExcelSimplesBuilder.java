package com.plusnet.autoclassfiy.simplesbuilder;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.plusnet.deduplicate.utils.KeyIndexGenerator;

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
	private final static String TRAIN_FILE_EX = ".train";
	private final static String MAPPING_FILE_EX = ".mapping";
	private final static String FILE_CHARSET = "UTF-8";
	private static Logger loger = LoggerFactory.getLogger(ExcelSimplesBuilder.class);

	public ExcelSimplesBuilder(File inputFile, File outputFile) {
		super();
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}

	public void train() {
		try {
			KeyIndexGenerator kg = new KeyIndexGenerator();
			Workbook wb = WorkbookFactory.create(inputFile);
			Sheet sheet = wb.getSheetAt(0);
			StringBuilder sb = new StringBuilder();
			Iterator<Row> it =sheet.rowIterator();
			it.next();
			while (it.hasNext()) {
				Row row = it.next();
				String typeName = row.getCell(0).getStringCellValue().trim();
				String value = row.getCell(1).getStringCellValue().trim();
				if(!StringUtils.isEmpty(typeName) && !StringUtils.isEmpty(value)){
					Integer typeIndex = kg.getKeyIndex(typeName);
					String line = buildLine(value, typeIndex,1000);
					sb.append(line + "\n");
				}
			}
			loger.info("save train file:"+trainFile);
			trainFile = new File(outputFile.getPath() + TRAIN_FILE_EX);
			FileUtils.write(trainFile, sb.toString(), FILE_CHARSET);
			
			loger.info("save map file:"+trainFile);
			File mappingFile = new File(outputFile.getPath() + MAPPING_FILE_EX);
			FileUtils.write(mappingFile, JSON.toJSONString(kg.getTypeMapping(), true), FILE_CHARSET);
			
			loger.info("train file:"+trainFile);
			Train.main(new String[] { trainFile.getAbsolutePath(), outputFile.getAbsolutePath() });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void test() {

	}

	public static void main(String[] args) {
		String inputFile = "D:/svm-simples/simples-input.xlsx";
		String outputFile = "D:/svm-simples/svm.model";
		ExcelSimplesBuilder esb = new ExcelSimplesBuilder(new File(inputFile), new File(outputFile));
		esb.train();
		esb.test();
	}

}
