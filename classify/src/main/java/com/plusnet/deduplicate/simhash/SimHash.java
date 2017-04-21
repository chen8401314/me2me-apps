package com.plusnet.deduplicate.simhash;

import java.io.IOException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.plusnet.autoclassfiy.idf.TFIDFKeyword;


/**
 * simHash实现,从网上直接拷贝的代码有隐藏BUG，此实现已经修复。
 * @author jiwei.zhang
 * @date 2016年5月20日
 */
public class SimHash {

	private int hashbits = 64;
	
	private List<TFIDFKeyword> TFIDFKeywordList;

	private String strSimHash;
	
	private long simHashValue;


	public List<TFIDFKeyword> getTFIDFKeywordList() {
		return TFIDFKeywordList;
	}
	public long getSimHashValue() {
		return simHashValue;
	}
	public SimHash(List<TFIDFKeyword> keyList, int hashbits) {
		this.TFIDFKeywordList = keyList;
		this.hashbits = hashbits;
		calcSimHash();
	}
	public SimHash(List<TFIDFKeyword> keyList){
		this(keyList,64);
	}
	/**
	 * 将simhash 转换为4个分段。
	 * @param len
	 * @return
	 */
	public int[] toFourPart(){
		int[] parts= new int[4];
		int mask = 0xFFFF;
		for(byte i=0;i<4;i++){
			int pos = 16*(3-i);
			parts[i]=  (int) ((this.simHashValue>>pos ) & mask);
		}
		return parts;
	}
	/**
	 * 计算simHash值。
	 */
	private void calcSimHash() {
		int[] bitArray = new int[this.hashbits];
		for(TFIDFKeyword TFIDFKeyword:this.TFIDFKeywordList){
			BigInteger wordHash = this.hash(TFIDFKeyword.getName());
			int weight = TFIDFKeyword.getAppearCount();
			weight=weight==0?1:weight;
			long s=1;
			for (byte i = 0; i < this.hashbits; i++) {
				BigInteger bitmask = BigInteger.valueOf(s<<i);
				if (wordHash.and(bitmask).signum() != 0) {
					bitArray[i] += weight;
				} else {
					bitArray[i] -= weight;
				}
			}
		}
		StringBuilder buffered= new StringBuilder();
		long finalHash = 0;
		for (byte i = 0; i < this.hashbits; i++) {	// 大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名. 
			if (bitArray[i] > 0) {  
				finalHash=finalHash<<1|1;	// 末位填1
				buffered.append("1");
			} else {
				finalHash=finalHash<<1|0;	// 末位填0
				buffered.append("0");
			}
		}
		this.strSimHash=buffered.toString();
		this.simHashValue=finalHash;
	}
	
	
    /**
	 * 将字符串生成hash值
	 * @param source
	 * @return
	 */
	private BigInteger hash(String source) {
	    if (source == null || source.length() == 0) {
	        return new BigInteger("0");
	    } else {
	        char[] sourceArray = source.toCharArray();
	        long tmp = (long) sourceArray[0];
	        BigInteger x = BigInteger.valueOf(tmp<<7);
	        BigInteger m = new BigInteger("1000003");
	        BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
	        for (char item : sourceArray) {
	            BigInteger temp = BigInteger.valueOf((long) item);
	            x = x.multiply(m).xor(temp).and(mask);
	        }
	        x = x.xor(new BigInteger(String.valueOf(source.length())));
	        if (x.equals(new BigInteger("-1"))) {
	            x = new BigInteger("-2");
	        }
	        return x;
	    }
	}
	public List<TFIDFKeyword> getTokens() {
		return TFIDFKeywordList;
	}
	public String getStrSimHash() {
		return strSimHash;
	}
	public int getHashbits() {
		return hashbits;
	}
	/**
	 * 计算汉明距离
	 * @param other
	 * @return
	 */
	public int hammingDistance(SimHash other) {
		return getHammingDistance(this.simHashValue, other.simHashValue);
	}
	/**
	 * 按位比较两个simhash的汉明距离
	 * @param va
	 * @param vb
	 * @return
	 */
	static public int getHammingDistance(long va,long vb) {
		int tot = 0;
		// 从右向左，按位比较
		for(byte i=0;i<64;i++){
			byte a = (byte) (va>>i&1);		
			byte b=  (byte) (vb>>i&1);
			if(a!=b) tot++;
		}
		return tot;
	}
	/**
	 * 按位比较两个simhash的汉明距离
	 * @param va 字符型的二进制码。如1110000000000110000000000110000001000100011100100000110010000000
	 * @param vb 字符型的二进制码
	 * @return
	 */
	static public int getHammingDistance(String va,String vb) {
		int tot = 0;
		// 从右向左，按位比较
		for(int i=0;i<va.length();i++){
			if(i<vb.length() && vb.charAt(i)!=va.charAt(i)){
				tot++;
			}
		}
		return tot;
	}
	@Override
	public String toString() {
		return strSimHash;
	}
	/**
	 * 将数字转换为2进制字符。
	 * @param n
	 * @return
	 */
	static public String toBinary(int n){
		StringBuilder sb = new StringBuilder();
		for(byte i=0;i<16;i++){
			int t= n>>(15-i)&1;
			sb.append(t);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		TFIDFKeyword a=new TFIDFKeyword("中国人");
		TFIDFKeyword b=new TFIDFKeyword("很好");
		List<TFIDFKeyword> keylist = new ArrayList<>();
		keylist.add(a);
		keylist.add(b);
		SimHash sh2 = new SimHash(keylist);
		System.out.println(sh2.strSimHash);
		
		int[] part= sh2.toFourPart();
		for(int t:part){
			System.out.print(toBinary(t));
		}
		
		System.out.println();
		System.out.println(sh2.simHashValue);
		// 9
		System.out.println(getHammingDistance(-2304153745888244608L, -2272612055822233728L));
		
	}
}