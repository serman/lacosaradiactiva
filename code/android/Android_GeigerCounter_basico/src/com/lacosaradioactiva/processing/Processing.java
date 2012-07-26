package com.lacosaradioactiva.processing;

import processing.core.PApplet;
import processing.core.PFont;
import android.os.Bundle;
import android.os.Looper;

public class Processing extends PApplet {

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

		pushMatrix();
		translate(width / 2, height / 2);
		ellipse(0, 0, 100, 100);
		arcText("ArcText", 0, 0, mouseX, mouseY, 1, 1);
		popMatrix();

	}

	// the good stuff
	void arcText(String text, float x, float y, float radius, float startAngle,
			float arcScale, int direction) {
		if (g.textFont == null)
			return;
		if (text == null)
			return;
		if (text.length() <= 0)
			return;

		textFont(g.textFont);

		float totalWidth = textWidth(text);
		float circumference = PI * radius * 2;

		pushMatrix();
		translate(x, y);

		float widthAccumulate = 0;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			float percent = widthAccumulate / circumference;
			float angle = radians(startAngle + percent * 360 * arcScale)
					* (direction >= 0 ? 1 : -1);
			widthAccumulate += textWidth(c);

			float px = cos(angle) * radius;
			float py = sin(angle) * radius;

			pushMatrix();
			translate(px, py);

			// this rotates from the -center- of the letter
			translate(textWidth(c) / 2, 0);
			rotate(angle + radians(90));
			translate(-textWidth(c) / 2, 0);
			text(c, 0, 0);

			popMatrix();
		}

		popMatrix();
	} 

	/*
	 * public int sketchWidth() { return 500; }
	 * 
	 * public int sketchHeight() { return 500; }
	 */

}
