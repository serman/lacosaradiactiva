package com.lacosaradioactiva.geiger.processing;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import android.os.Bundle;
import android.os.Looper;

public class ProcessingSketch extends PApplet {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	PImage controlRoom; 
	PFont font;
	private float posX;
	private float posY;
	private int referenceX;
	private int referenceY;
	private float zoomFactor;

	public void setup() {
		frameRate(28);
		smooth();
		font = createFont("Verdana-Bold", 48);
		// background(250, 250, 205);
		background(0); 
		
		controlRoom = loadImage("control_room_2.png");
		image(controlRoom, 0, 0); 
		noStroke();

		noLoop();

	}

	public void draw() { // background(255);

		if (frameCount == 1) {
			Looper.prepare();
		}

		background(0); 
		
		//println("1 -- " + posX + " " + posY);
		if (posX < 0) posX = 0; 
		if (posX > controlRoom.width - width) posX = controlRoom.width - width; 
		//println("2 -- " + posX + " " + posY);
		
		if (posY < 0) posY = 0; 
		if (posY > 0) posY = 0; 
		
		
		pushMatrix(); 
		scale(zoomFactor); 
		image(controlRoom, -posX, -posY); 

		popMatrix(); 
		// println("hola");

		//fill(0);
		//rect(0, 0, width, height);

		// you have to set a font first!
		textFont(font);

		// pushMatrix();
		// translate(width / 2, height / 2);
		for (int i = 0; i < 52; i++) {
			fill(random(125, 255));
			ellipse(random(width), random(height), 2, 2);
		}

		// popMatrix();

	}

	public void setReady() {

		loop();

	} 
	
	@Override
	public void mousePressed() {
		super.mousePressed();
		//println("mousePressed");
		
		referenceX = mouseX; 
		referenceY = mouseY; 
	}
	
	@Override
	public void tapEvent(float x, float y) {
		super.tapEvent(x, y);
	
		
	} 
	
	@Override
	public void zoomEvent(float x, float y, float d0, float d1) {
		super.zoomEvent(x, y, d0, d1);
	
		zoomFactor = map(d1, 0f, 500f, 0f, 2f); 
		println(" " + x + " " + y + " " + d0 + " " + d1);
	} 
	
	
	@Override
	public void mouseDragged() {
		super.mouseDragged();
		//println("mouseDragged"); 
		
		posX += (referenceX - mouseX) * 0.1; 
		posY += (referenceY - mouseY) * 0.1;
		
	
	}

	/*
	 * public int sketchWidth() { return 500; }
	 * 
	 * public int sketchHeight() { return 500; }
	 */

}
