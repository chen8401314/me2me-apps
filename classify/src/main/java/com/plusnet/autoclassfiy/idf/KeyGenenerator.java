package com.plusnet.autoclassfiy.idf;

public class KeyGenenerator {
	private byte[] lock= new byte[0];
	private int pos;
	public KeyGenenerator(int pos){
		this.pos=pos;
	}
	
	public KeyGenenerator(){
		this.pos=0;
		System.out.println("key gen:"+this);
	}
	public int getNextKey(){
		synchronized (lock) {
			pos++;
		}
		return pos;
	}
	
	public int getCurKey(){
		return pos;
	}
}
