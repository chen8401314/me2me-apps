package com.plusnet.autoclassfiy.simplesbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.plusnet.deduplicate.utils.KeyIndexGenerator;

public class FileSimplesBuilder extends AbsSVMSimplesBuilder {
	ExecutorService pool = Executors.newFixedThreadPool(4);

	public FileSimplesBuilder() {
		super();
	}

	public void start(File inputDir, File outPutFile) {
		try {
			KeyIndexGenerator kg = new KeyIndexGenerator();
			final FileWriter fw = new FileWriter(outPutFile);
			File[] subDirs = inputDir.listFiles();
			if (subDirs != null) {
				for (final File sub : subDirs) {
					if (sub.isDirectory()) {
						File[] files = sub.listFiles();
						if (files != null) {
							final String typeStr = sub.getName().replaceAll("\\d", "");
							final int type =kg.getKeyIndex(typeStr);
							
							final CountDownLatch cd = new CountDownLatch(files.length);
							System.out.println("create type:" + typeStr + ",simples:" + cd.getCount());
							for (final File file : files) {
								pool.execute(new Runnable() {
									@Override
									public void run() {
										try {
											String txt = FileUtils.readFileToString(file, "utf-8");
											String line = buildLine(txt, type);
											if (line != null) {
												fw.write(line+"\n");
												fw.flush();
											}
										} catch (IOException e) {
											e.printStackTrace();
										} finally {
											cd.countDown();
										}
									}

								});
							}
							cd.await();
						}
					}
				}
			}

			pool.shutdown();
			pool.awaitTermination(999999, TimeUnit.DAYS);
			fw.close();
			System.out.println("finished.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
