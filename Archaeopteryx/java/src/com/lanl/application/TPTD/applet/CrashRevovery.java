package com.lanl.application.TPTD.applet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import com.lanl.application.treePruner.custom.data.WorkingSet;

public class CrashRevovery {
	ArrayList<String> crashRecoveryAccessions = new ArrayList<String>();
	WorkingSet ws = new WorkingSet();

	public void crashRecoveryInit() {
		String accessions = null;
		// String savedAcc = "file:////Users/kmohan/Desktop/2.txt";

		if (AppletParams.savedAccFlag.equals("true")) {
			// wait till all the nodes have been successfully painted on the
			// applet. After 2 secs hoping that all tree branches would have
			// successfully painted by then
			// we will call the crash recovery and repaint the tree(see below).
			// START
			long wait, time;
			wait = System.currentTimeMillis();
			while (true) {
				time = System.currentTimeMillis() - wait;
				if (time >= 2000) // 2 seconds wait
					break;
			}
			// wait till all the nodes have been successfully painted on the
			// applet. After 2 secs hoping that all tree branches would have
			// successfully painted by then
			// we will call the crash recovery and repaint the tree (see below).
			// END
			URL savedAccURL;
			try {
				savedAccURL = new URL(AppletParams.codeBase,
						AppletParams.savedAcc);
				InputStreamReader fin;
				fin = new InputStreamReader(savedAccURL.openStream());
				BufferedReader d = new BufferedReader(fin);
				accessions = d.readLine();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				System.err
						.println("Unable to read from file of saved accession");
			}
		String[] recoveredAccessions = null;
		recoveredAccessions = accessions.split("\\+");
		for (int i = 0; i < recoveredAccessions.length; i++) {
			crashRecoveryAccessions.add(recoveredAccessions[i]);
		}
		ws.crashRecovery(crashRecoveryAccessions);
		ControlPanelAdditions.lastAction = "save";
		ws.copyAccToRememberAcc();
		ws.clear("removeActive");
		// Repaint the tree. START
		SubTreePanel.mainAppletFrame.repaintPanel();
		// atvappletframe.getATVpanel().getATVtreePanel().repaint();
		// Repaint the tree. END

		} // end of if(saved_session_before.equals("true")){
	} // end of crashRecoveryInit
}