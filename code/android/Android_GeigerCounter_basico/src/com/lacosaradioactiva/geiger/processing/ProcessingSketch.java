package com.lacosaradioactiva.geiger.processing;

import processing.core.PApplet;
import processing.core.PFont;
import android.os.Bundle;
import android.os.Looper;

public class ProcessingSketch extends PApplet {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
	
	}

	
	PFont font;

	public void setup() {
		frameRate(28);
		smooth();
		font = createFont("Verdana-Bold", 48);
		//background(250, 250, 205);
		
	} 

	public void draw() { // background(255);

		if (frameCount == 1) {
			Looper.prepare();
		}

		background(250, 250, 205, 255);

		fill(255);
		// you have to set a font first!
		textFont(font);

		//pushMatrix();
		//translate(width / 2, height / 2);
		ellipse(mouseX, mouseY, 100, 100);

		//popMatrix();

	}

	


	/*
	 * public int sketchWidth() { return 500; }
	 * 
	 * public int sketchHeight() { return 500; }
	 */

}
