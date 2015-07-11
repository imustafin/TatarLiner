package ru.litsey2.computertatarliner;

import java.awt.Cursor;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;

@SuppressWarnings("serial")
public class WebView extends JEditorPane {

	WebView() {
		setContentType("text/html");
		setEditable(false);
	}

	public void showPage(URL pageUrl) throws IOException {
		// Show hour glass cursor while crawling is under way.
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {

			// Load and display specified page.
			setPage(pageUrl);

		} finally {
			// Return to default cursor.
			setCursor(Cursor.getDefaultCursor());
		}
	}

}
