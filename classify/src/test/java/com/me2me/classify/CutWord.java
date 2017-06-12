package com.me2me.classify;

import java.util.List;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

public class CutWord {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Word> words = WordSegmenter.segWithStopWords("楚离陌千方百计为无情找回记忆");
		System.out.println(words);
	}
}
